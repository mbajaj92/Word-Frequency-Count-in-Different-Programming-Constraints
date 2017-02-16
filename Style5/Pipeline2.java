import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * In this implementation we will try to emulate the pipeline style of coding;
 * where the out-put of every function is taken in as an input for another
 * function, creating a chain of functions. We shall be using the concurrency
 * properties provided by Java to implement this. The main attraction in this
 * code, are the functions
 * 
 * @trim - This will trim every word and pass it ahead to its next function as
 *       soon as it is processed, concurrently.
 * @removeStopWords - This will return a stream of words that have been
 *                  filtered, to its next function as soon as every word is
 *                  processed, in concurrency.
 * @calcFreq - This will return a stream of (word, freq) pair to its next
 *           function, as soon as it is obtained/calculated.
 * @sortAndPrint - This will accept the stream of (word,freq) pairs
 *               from @calcFreq and sort them, limit to top 25 and print them.
 * @author Madhur J. Bajaj
 *
 */
public class Pipeline2 {

	/**
	 * This function will read the complete file and return the same.
	 * 
	 * @param path
	 * @return A string containing the complete file
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

	public static Stream<String> removeStopWords(Stream<String> words, String stopwords) throws Exception {
		return words.filter(w -> (w.length() > 1 && !stopwords.contains("," + w + ",")));
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

	public static void main(String args[]) throws Exception {
		sortAndPrint(calcFreq(
				removeStopWords(trim(cleanDataAndSplit(read_data(args[0]))), "," + read_data("..\\stop_words.txt"))));
	}
}