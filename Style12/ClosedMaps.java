import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * This is Closed Maps Style, where every function/object is saved in a HashMap
 * or a dictionary, and referred to via a String Identifier. to acheive this
 * style I have encoded everything into a HashMap
 * 
 * @author Madhur J. Bajaj
 *
 */
public class ClosedMaps {

	/* Function to extract the probable tokens from the given file */
	public static void extract_probable_tokens(HashMap<String, Object> obj, String path) throws Exception {
		Scanner sc = new Scanner(new FileReader(path));
		sc.useDelimiter("\\Z");
		String data = sc.next();
		obj.put("data", new ArrayList<String>((Arrays.asList(data.replaceAll("[^A-Za-z0-9]", " ").split(" "))).stream()
				.map(w -> w.toLowerCase().trim()).filter(w -> (w.length() > 1)).collect(Collectors.toList())));
		sc.close();
	}

	/* Function to load the stop words */
	public static void load_stop_words(HashMap<String, Object> obj, String path) throws Exception {
		Scanner sc = new Scanner(new FileReader(path));
		sc.useDelimiter("\\Z");
		String data = sc.next();
		obj.put("stop_words", new ArrayList<String>(Arrays.asList(data.split(","))));
		sc.close();
	}

	/* Function to count the frequency of every token */
	public static void count_tokens(HashMap<String, Object> obj) {
		HashMap<String, Integer> freq = ((HashMap<String, Integer>) obj.get("freq"));
		ArrayList<String> words = (ArrayList<String>) obj.get("words");
		for (String word : words) {
			if (freq.containsKey(word))
				freq.put(word, freq.get(word) + 1);
			else
				freq.put(word, 1);
		}
	}

	public static void main(String args[]) throws Exception {
		/* The HashMap used to store every obejct */
		HashMap<String, Object> prototype = new HashMap<String, Object>();
		prototype.put("init", (Runnable) (() -> {
			try {
				extract_probable_tokens(prototype, args[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}));

		prototype.put("load_stop_words", (Runnable) (() -> {
			try {
				load_stop_words(prototype, "..\\stop_words.txt");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}));

		prototype.put("increment_count", (Runnable) (() -> {
			count_tokens(prototype);
		}));

		prototype.put("freq", new HashMap<String, Integer>());

		((Runnable) prototype.get("init")).run();
		((Runnable) prototype.get("load_stop_words")).run();
		ArrayList<String> words = (ArrayList<String>) prototype.get("data");
		words.removeAll((ArrayList<String>) prototype.get("stop_words"));
		prototype.put("words", words);
		((Runnable) prototype.get("increment_count")).run();

		((HashMap<String, Integer>) prototype.get("freq")).entrySet().stream()
				.sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).limit(25)
				.forEach(p -> System.out.println(p.getKey() + " - " + p.getValue()));
	}
}
