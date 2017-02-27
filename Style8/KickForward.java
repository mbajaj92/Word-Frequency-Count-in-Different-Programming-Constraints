import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

/**
 * 
 * This program is an emulation of the Kick Forwards Style. In this we need to pass a
 * reference, or an indication of a the next function that needs to be executed
 * next as a parameter.
 * 
 * We shall be using the Method Reflection feature of java to achieve this
 * functionality. The following code snippet, does the required job.
 * 
 * KickForward.class.getDeclaredMethod("funcName", params).invoke(this,Parameters for the function....);
 * 
 * in the above line, the getDeclaredMethod will try to find a function by the name "funcName", 
 * having the parameters as mentioned in params; 
 * params is of type Class[], which shall hold the class identifiers for parameters of the
 * "funcName" function in the order that they are present.
 * 
 * Eg - void abc(int a, String b, boolean c) { // do something }
 * 
 * funcName = "abc";
 * params[0] = Integer.TYPE;
 * params[1] = String.class;
 * params[2] = boolean.class
 * 
 * and the line would be 
 * KickForward.class.getDeclaredMethod(funcName,params).invoke(this,1,"hello",true);
 * 
 * @author Madhur J. Bajaj
 *
 */
public class KickForward {

	/**
	 * This function will read the complete file.
	 */
	private void read_data(String path, String nextFunc, Class[] params) throws Exception {
		String data = "";
		Scanner sc = new Scanner(new FileReader(path));
		sc.useDelimiter("\\Z");
		data = sc.next();
		sc.close();

		/* Parameter List for removeUnwantedWords */
		Class[] p = new Class[4];
		p[0] = ArrayList.class;
		p[1] = String.class;
		p[2] = String.class;
		p[3] = Class[].class;
		KickForward.class.getDeclaredMethod(nextFunc, params).invoke(this, data, "removeUnwantedWords", p);
	}

	/**
	 * This function will take a String and replace all Alpha-Numeric Characters
	 * with space; change to lower-case; and convert to a List of the words
	 */
	private void cleanDataAndSplit(String data, String nextFunc, Class[] params) throws Exception {
		ArrayList<String> value = new ArrayList<String>(
				Arrays.asList(data.replaceAll("[^A-Za-z0-9]", " ").toLowerCase().split(" ")));

		/* Parameter list for calcFreq */
		Class[] p = new Class[3];
		p[0] = value.getClass();
		p[1] = String.class;
		p[2] = Class[].class;
		KickForward.class.getDeclaredMethod(nextFunc, params).invoke(this, value, "..\\stop_words.txt",
				"calcFreq", p);
	}

	/**
	 * This will remove all the stop words, trim them.
	 */
	private void removeUnwantedWords(ArrayList<String> words, String ignore, String nextFunc, Class[] params)
			throws Exception {
		Scanner sc = new Scanner(new FileReader(ignore));
		sc.useDelimiter("\\Z");
		String stopwords = "," + sc.next();
		sc.close();
		for (int i = words.size() - 1; i >= 0; i--) {
			if (stopwords.contains("," + words.get(i) + ","))
				words.set(i, " ");
		}
		words.forEach((word) -> word.trim());

		/* Parameter list for sortAndPrint */
		Class[] p = new Class[1];
		p[0] = ArrayList.class;
		KickForward.class.getDeclaredMethod(nextFunc, params).invoke(this, words, "sortAndPrint", p);
	}

	/**
	 * This will calculate the frequency of the given list, and ignore all the
	 * words with length < 2
	 */
	private void calcFreq(ArrayList<String> words, String nextFunc, Class[] params) throws Exception {
		HashMap<String, Integer> freq = new HashMap<String, Integer>();
		for (String word : words) {
			if (word.length() < 2)
				continue;

			if (freq.containsKey(word))
				freq.put(word, (freq.get(word) + 1));
			else
				freq.put(word, 1);
		}

		KickForward.class.getDeclaredMethod(nextFunc, params).invoke(this,
				new ArrayList<Map.Entry<String, Integer>>(freq.entrySet()));
	}

	/**
	 * This will sort the given ArrayList, and print the top 25 values
	 */
	private void sortAndPrint(ArrayList<Map.Entry<String, Integer>> values) {
		Collections.sort(values, new Comparator<Map.Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o2.getValue() - o1.getValue();
			}
		});

		for (int i = 0; i < 25; i++)
			System.out.println(values.get(i).getKey() + " - " + values.get(i).getValue());
	}

	public static void main(String args[]) throws Exception {
		Class c = Class.forName("KickForward");
		Object o = c.newInstance(); 

		/* Parameter list for cleanDataAndSplit */
		Class[] params = new Class[3];
		params[0] = String.class;
		params[1] = String.class;
		params[2] = Class[].class;
		((KickForward)o).read_data(args[0], "cleanDataAndSplit", params);
	}
}