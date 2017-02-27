import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * In this Style, we create an object abstraction; and bind all the functions
 * with that object, such that any value that the function returns is saved in
 * the generic value attribute in the object.
 * 
 * @author Madhur J. Bajaj
 *
 */
public class TheOne {
	/* Generic value */
	private Object value = null;

	private TheOne init(String v) {
		value = v;
		return this;
	}

	/* Bind function, to bind the given funcName with the value, this funcName
	 * also accepts an additional parameter: to be provided by the parent */
	private TheOne bind(String funcName, Class[] param, Object parameter) throws Exception {
		value = TheOne.class.getMethod(funcName, param).invoke(null, value, parameter);
		return this;
	}

	/* Bind function, to bind the given funcName with the value */
	private TheOne bind(String funcName, Class[] param) throws Exception {
		value = TheOne.class.getMethod(funcName, param).invoke(null, value);
		return this;
	}

	public static void main(String args[]) throws Exception {
		TheOne theOne = new TheOne();
		Class[] params = new Class[1];
		params[0] = String.class;
		theOne = theOne.init(args[0]).bind("read_data", params).bind("cleanDataAndSplit", params);
		params[0] = List.class;
		theOne = theOne.bind("trim", params);
		params = new Class[2];
		params[0] = Stream.class;
		params[1] = String.class;
		theOne.bind("removeStopWords", params, "..\\stop_words.txt");
		params = new Class[1];
		params[0] = Stream.class;
		theOne.bind("calcFreq", params).bind("sortAndPrint", params);
	}

	/**
	 * Function to read data from a given path of a file.
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static String read_data(String path) throws Exception {
		String data = "";
		Scanner sc = new Scanner(new FileReader(path));
		sc.useDelimiter("\\Z");
		data = sc.next();
		sc.close();
		return data;
	}

	/**
	 * This function will take a String and replace all Alpha-Numeric Characters
	 * with space; change to lower-case; and convert to a List of the words
	 * 
	 * @param data
	 * @return
	 */
	public static List<String> cleanDataAndSplit(String data) {
		return Arrays.asList(data.replaceAll("[^A-Za-z0-9]", " ").toLowerCase().split(" "));
	}

	/**
	 * This will trim all the words.
	 * 
	 * @param words
	 * @return
	 * @throws Exception
	 */
	public static Stream<String> trim(List<String> words) {
		return words.stream().map(w -> w.trim());
	}

	/**
	 * Function to remove the stop words in the current Stream
	 * 
	 * @param words
	 * @param stopwords
	 * @return
	 * @throws Exception
	 */
	public static Stream<String> removeStopWords(Stream<String> words, String stopwords) throws Exception {
		List<String> wordList = words.filter(w -> (w.length() > 1)).collect(Collectors.toList());
		wordList.removeAll(Arrays.asList(read_data(stopwords).split(",")));
		return wordList.stream();
	}

	/**
	 * This will calculate the frequency of the given list, and ignore all the
	 * words with length < 2
	 * 
	 * @param words
	 * @return
	 */
	public static Stream<Map.Entry<String, Long>> calcFreq(Stream<String> words) {
		return words.collect(Collectors.groupingBy(e -> e, Collectors.counting())).entrySet().stream();
	}

	/**
	 * This will sort the given ArrayList, and print the top 25 values
	 * 
	 * @param values
	 */
	public static void sortAndPrint(Stream<Map.Entry<String, Long>> values) {
		values.sorted((x, y) -> y.getValue().compareTo(x.getValue())).limit(25)
				.forEach(p -> System.out.println(p.getKey() + " - " + p.getValue()));
	}
}