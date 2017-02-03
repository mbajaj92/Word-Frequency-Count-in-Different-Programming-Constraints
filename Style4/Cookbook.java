import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

/**
 * In this programming style, all the procedures can now interact 
 * with the global variables, and can edit them as per their own 
 * implementation. The functions in this case aren't idempotent.
 */

/**
 * 
 * @author Madhur J. Bajaj
 *
 */
public class Cookbook {
	public static String data[];
	public static ArrayList<String> words;
	public static ArrayList<Integer> freq;

	/**
	 * This function reads the complete file and stores it in data[0]
	 * 
	 * @param path
	 * @throws Exception
	 */
	public static void read_data(String path) throws Exception {
		Scanner sc = new Scanner(new FileReader(path));
		sc.useDelimiter("\\Z");
		data = new String[1];
		data[0] = sc.next();
		sc.close();
	}

	/**
	 * This function will clean the data by replacing all the non wanted
	 * characters with space
	 */
	public static void cleanData() {
		for (int i = 0; i < data[0].length(); i++) {
			char ch = data[0].charAt(i);
			if (!Character.isLetterOrDigit(ch) && ch != ' ') {
				data[0] = data[0].replace(ch, ' ');
			} else if (Character.isUpperCase(ch)) {
				data[0] = data[0].replace(ch, Character.toLowerCase(ch));
			}
		}
	}

	/**
	 * This function will split the String in data[0] into various strings by
	 * space
	 */
	public static void split() {
		data = data[0].split(" ");
	}

	/**
	 * This function will read the stop words and make the entries for all the
	 * to be ignored words blank
	 * 
	 * @param path
	 * @throws Exception
	 */
	public static void ignoreWords(String path) throws Exception {
		Scanner sc = new Scanner(new FileReader(path));
		sc.useDelimiter("\\Z");
		String words = "," + sc.next() + "1,2,3,4,5,6,7,8,9,0,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,";
		sc.close();

		for (int i = 0; i < data.length; i++) {
			data[i] = data[i].trim();
			if (words.contains("," + data[i] + ",")) {
				data[i] = "";
			}
		}
	}

	/**
	 * This function will calculate the frequency(stores in freq) all the words
	 * that are left in data, and moves the tokens to words. The frequency of a
	 * given token is stored in the same index as the token is in words.
	 */
	public static void calcFreq() {
		words = new ArrayList<String>();
		freq = new ArrayList<Integer>();

		for (int i = 0; i < data.length; i++) {
			if (data[i].length() < 2)
				continue;

			if (words.contains(data[i])) {
				int index = words.indexOf(data[i]);
				int val = freq.remove(index);
				freq.add(index, (val + 1));
			} else {
				words.add(data[i]);
				freq.add(1);
			}
		}
	}

	/**
	 * This will sort the words in descending order and print the top 25
	 */
	public static void sortAndPrintTop25() {
		ArrayList<String> answer = new ArrayList<String>(words);
		Collections.sort(answer, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				int index1 = words.indexOf(o1);
				int index2 = words.indexOf(o2);
				return freq.get(index2) - freq.get(index1);
			}
		});

		for (int i = 0; i < 25; i++) {
			String token = answer.get(i);
			int index = words.indexOf(token);
			int frequency = freq.get(index);
			System.out.println(token + " - " + frequency);
		}
	}

	public static void main(String args[]) throws Exception {
		read_data(args[0]);
		cleanData();
		split();
		ignoreWords("..\\stop_words.txt");
		calcFreq();
		sortAndPrintTop25();
	}
}