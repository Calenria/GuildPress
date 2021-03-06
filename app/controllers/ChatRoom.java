package controllers;

import java.util.List;

import models.Chat;
import play.libs.F.IndexedEvent;
import play.mvc.Before;
import play.mvc.Controller;

import com.google.gson.reflect.TypeToken;

public class ChatRoom extends Controller {
	@Before
	static void addDefaults() {
		Application.addDefaults();
	}

	public static void leave(String chatname) {
		Chat.get().leave(chatname);
		Application.index();
	}

	public static void room(String chatname) {
		Chat.get().join(chatname);
		render(chatname);
	}

	public static void say(String chatname, String message, String link) {
		Chat.get().say(chatname, message, link);
	}

	public static void waitMessages(Long lastReceived) {
		// Here we use continuation to suspend
		// the execution until a new message has been received
		List messages = await(Chat.get().nextMessages(lastReceived));
		renderJSON(messages, new TypeToken<List<IndexedEvent<Chat.Event>>>() {
		}.getType());
	}

}
