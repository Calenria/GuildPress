import org.junit.*;
import java.util.*;

import play.db.jpa.GenericModel;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {
	@Before
	public void setup() {
		Fixtures.deleteDatabase();
	}

	@Test
	public void createAndRetrieveUser() {
		// Create a new user and save it
		new User("bob@gmail.com", "secret", "Bob").save();

		// Retrieve the user with e-mail address bob@gmail.com
		User bob = GenericModel.find("byEmail", "bob@gmail.com").first();

		// Test
		assertNotNull(bob);
		assertEquals("Bob", bob.fullname);
	}

	@Test
	public void tryConnectAsUser() {
		// Create a new user and save it
		new User("bob@gmail.com", "secret", "Bob").save();

		// Test
		assertNotNull(User.connect("bob@gmail.com", "secret"));
		assertNull(User.connect("bob@gmail.com", "badpassword"));
		assertNull(User.connect("tom@gmail.com", "secret"));
	}
	@Test
	public void createPost() {
	    // Create a new user and save it
	    User bob = new User("bob@gmail.com", "secret", "Bob").save();
	    
	    // Create a new post
	    new News(bob, "My first post", "Hello world").save();
	    
	    // Test that the post has been created
	    assertEquals(1, GenericModel.count());
	    
	    // Retrieve all posts created by Bob
	    List<News> bobPosts = GenericModel.find("byAuthor", bob).fetch();
	    
	    // Tests
	    assertEquals(1, bobPosts.size());
	    News firstPost = bobPosts.get(0);
	    assertNotNull(firstPost);
	    assertEquals(bob, firstPost.author);
	    assertEquals("My first post", firstPost.title);
	    assertEquals("Hello world", firstPost.content);
	    assertNotNull(firstPost.postedAt);
	}
	
	@Test
	public void postComments() {
	    // Create a new user and save it
	    User bob = new User("bob@gmail.com", "secret", "Bob").save();
	 
	    // Create a new post
	    News bobPost = new News(bob, "My first post", "Hello world").save();
	 
	    // Post a first comment
	    new Comment(bobPost, "Jeff", "Nice post").save();
	    new Comment(bobPost, "Tom", "I knew that !").save();
	 
	    // Retrieve all comments
	    List<Comment> bobPostComments = GenericModel.find("byPost", bobPost).fetch();
	 
	    // Tests
	    assertEquals(2, bobPostComments.size());
	 
	    Comment firstComment = bobPostComments.get(0);
	    assertNotNull(firstComment);
	    assertEquals("Jeff", firstComment.author);
	    assertEquals("Nice post", firstComment.content);
	    assertNotNull(firstComment.postedAt);
	 
	    Comment secondComment = bobPostComments.get(1);
	    assertNotNull(secondComment);
	    assertEquals("Tom", secondComment.author);
	    assertEquals("I knew that !", secondComment.content);
	    assertNotNull(secondComment.postedAt);
	}
	
	@Test
	public void useTheCommentsRelation() {
	    // Create a new user and save it
	    User bob = new User("bob@gmail.com", "secret", "Bob").save();
	 
	    // Create a new post
	    News bobPost = new News(bob, "My first post", "Hello world").save();
	 
	    // Post a first comment
	    bobPost.addComment("Jeff", "Nice post");
	    bobPost.addComment("Tom", "I knew that !");
	 
	    // Count things
	    assertEquals(1, GenericModel.count());
	    assertEquals(1, GenericModel.count());
	    assertEquals(2, GenericModel.count());
	 
	    // Retrieve Bob's post
	    bobPost = GenericModel.find("byAuthor", bob).first();
	    assertNotNull(bobPost);
	 
	    // Navigate to comments
	    assertEquals(2, bobPost.comments.size());
	    assertEquals("Jeff", bobPost.comments.get(0).author);
	    
	    // Delete the post
	    bobPost.delete();
	    
	    // Check that all comments have been deleted
	    assertEquals(1, GenericModel.count());
	    assertEquals(0, GenericModel.count());
	    assertEquals(0, GenericModel.count());
	}
	
	@Test
	public void fullTest() {
	    Fixtures.loadModels("data.yml");
	 
	    // Count things
	    assertEquals(2, GenericModel.count());
	    assertEquals(3, GenericModel.count());
	    assertEquals(3, GenericModel.count());
	 
	    // Try to connect as users
	    assertNotNull(User.connect("bob@gmail.com", "secret"));
	    assertNotNull(User.connect("jeff@gmail.com", "secret"));
	    assertNull(User.connect("jeff@gmail.com", "badpassword"));
	    assertNull(User.connect("tom@gmail.com", "secret"));
	 
	    // Find all of Bob's posts
	    List<News> bobPosts = GenericModel.find("author.email", "bob@gmail.com").fetch();
	    assertEquals(2, bobPosts.size());
	 
	    // Find all comments related to Bob's posts
	    List<Comment> bobComments = GenericModel.find("post.author.email", "bob@gmail.com").fetch();
	    assertEquals(3, bobComments.size());
	 
	    // Find the most recent post
	    News frontPost = GenericModel.find("order by postedAt desc").first();
	    assertNotNull(frontPost);
	    assertEquals("About the model layer", frontPost.title);
	 
	    // Check that this post has two comments
	    assertEquals(2, frontPost.comments.size());
	 
	    // Post a new comment
	    frontPost.addComment("Jim", "Hello guys");
	    assertEquals(3, frontPost.comments.size());
	    assertEquals(4, GenericModel.count());
	}
	@Test
	public void testTags() {
	    // Create a new user and save it
	    User bob = new User("bob@gmail.com", "secret", "Bob").save();
	 
	    // Create a new post
	    News bobPost = new News(bob, "My first post", "Hello world").save();
	    News anotherBobPost = new News(bob, "Hop", "Hello world").save();
	 
	    // Well
	    assertEquals(0, News.findTaggedWith("Red").size());
	 
	    // Tag it now
	    bobPost.tagItWith("Red").tagItWith("Blue").save();
	    anotherBobPost.tagItWith("Red").tagItWith("Green").save();
	 
	    // Check
	    assertEquals(2, News.findTaggedWith("Red").size());
	    assertEquals(1, News.findTaggedWith("Blue").size());
	    assertEquals(1, News.findTaggedWith("Green").size());
	    
	    assertEquals(1, News.findTaggedWith("Red", "Blue").size());
	    assertEquals(1, News.findTaggedWith("Red", "Green").size());
	    assertEquals(0, News.findTaggedWith("Red", "Green", "Blue").size());
	    assertEquals(0, News.findTaggedWith("Green", "Blue").size());
	    
	    List<Map> cloud = Tag.getCloud();
	    assertEquals(
	        "[{tag=Blue, pound=1}, {tag=Green, pound=1}, {tag=Red, pound=2}]",
	        cloud.toString()
	    );
	}
}
