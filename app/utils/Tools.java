package utils;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class Tools {

	private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
	private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

	public static String implodeArray(JsonArray inputArray, String glueString) {
		/** Output variable */
		String output = "";
		StringBuilder sb = new StringBuilder();
		for (JsonElement jsonElement : inputArray) {
			sb.append(glueString);
			sb.append(jsonElement.getAsString());

		}
		output = sb.toString().substring(1);
		return output;
	}

	public static String implodeArray(String[] inputArray, String glueString) {

		/** Output variable */
		String output = "";

		if (inputArray.length > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(inputArray[0]);

			for (int i = 1; i < inputArray.length; i++) {
				sb.append(glueString);
				sb.append(inputArray[i]);
			}

			output = sb.toString();
		}

		return output;
	}

	public static String implodeList(List<String> inputList, String glueString) {
		/** Output variable */
		String output = "";
		StringBuilder sb = new StringBuilder();

		for (String string : inputList) {
			sb.append(glueString);
			sb.append(string);
		}
		if (sb.length() > 0) {
			output = sb.toString().substring(1);
		} else {
			output = sb.toString();
		}

		return output;
	}

	/**
	 * convenience method for replacing german umlauts with html unicode codes
	 * 
	 * @param text
	 *            the text to compute
	 * @return the altered text
	 */
	public static String replaceUmlauts(String text) {
		return text.replaceAll("ä", "a").replaceAll("Ä", "A").replaceAll("ö", "o").replaceAll("Ö", "O").replaceAll("ü", "u").replaceAll("Ü", "U").replaceAll("ß", "s");
	}

	public static String Slugify(String toSlug) {
		if (toSlug == null) {
			return null;
		}
		String nowhitespace = WHITESPACE.matcher(toSlug).replaceAll("-");
		String normalized = Normalizer.normalize(nowhitespace, Form.NFD);
		String slug = NONLATIN.matcher(normalized).replaceAll("");
		return slug.toLowerCase(Locale.GERMAN);
	}

	public static String long2raidTime(Long raidTime) {
		if (raidTime == null) {
			return null;
		}
		
		String time = raidTime.toString();
		String first = time.substring(0, 2);
		String last = time.substring(2);
		
		return first + ":" + last;
	}
}
