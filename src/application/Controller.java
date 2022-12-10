package application;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Controller {

	@FXML
	private Label myLabel;
	@FXML
	private Button myButton;
	@FXML
	private TextArea mySpace;
	
	
	public void submit(ActionEvent event) {

		try {
			myLabel.setText("Starting...");
			
			String[] words = new String[20];
			
			words = frequency();
			System.out.println(words[0]);
			
			mySpace.setText(
					"1. " + words[0] + "\n" +
					"2. " + words[1] + "\n"+
					"3. " + words[2] + "\n"+
					"4. " + words[3] + "\n"+
					"5. " + words[4] + "\n"+
					"6. " + words[5] + "\n"+
					"7. " + words[6] + "\n"+
					"8. " + words[7] + "\n"+
					"9. " + words[8] + "\n"+
					"10. " + words[9] + "\n"+
					"11. " + words[10] + "\n"+
					"12. " + words[11] + "\n"+
					"13. " + words[12] + "\n"+
					"14. " + words[13] + "\n"+
					"15. " + words[14] + "\n"+
					"16. " + words[15] + "\n"+
					"17. " + words[16] + "\n"+
					"18. " + words[17] + "\n"+
					"19. " + words[18] + "\n"+
					"20. " + words[19] + "\n"
					);
			
			mySpace.setWrapText(true);
			
		
			
		}
		catch (Exception e) {
			myLabel.setText("error");
		}
	}	

	
	/**
	 * 
	 * @param list words
	 * @throws Exception 
	 */
	
	public static String[] frequency() throws IOException{
		
		try {
			getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		TreeMap<String, Integer> wordTree = new TreeMap<>(Collections.reverseOrder());
		
		wordTree = countWords("text.txt");

		// flip
		// *****************************************************************************

		Set<Entry<String, Integer>> entries = wordTree.entrySet();

		Comparator<Entry<String, Integer>> valueComparator = new Comparator<Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) {
				Integer v1 = e1.getValue();
				Integer v2 = e2.getValue();
				return v2.compareTo(v1);

			}
		};

		// we will need a list
		List<Entry<String, Integer>> wordTree2List = new ArrayList<Entry<String, Integer>>(entries);

		// sort
		Collections.sort(wordTree2List, valueComparator);

		LinkedHashMap<String, Integer> sortedByValue = new LinkedHashMap<String, Integer>(wordTree2List.size());

		// copy
		for (Entry<String, Integer> entry : wordTree2List) {
			sortedByValue.put(entry.getKey(), entry.getValue());
		}
		
		Set<Entry<String, Integer>> wordsSortedByValue = sortedByValue.entrySet();

		printWords(sortedByValue);
		
		String[] words = new String[20];
		int j =0 , k=0;
		for (Entry<String, Integer> mapping : wordsSortedByValue) {
			 
			if (j <= 19) {
				System.out.print(k + ".");
				System.out.println(mapping.getKey() + "\t:" + mapping.getValue());
				
				words[j] = (mapping.getKey());
				System.out.println(words[j]);
				
			} else {
				break;
			}
			k++;
			j++;
		}

		
		return words;
		
	}
	
	/**
	 * @param word, single word with upper case or special values 
	 * @return correctedWord, with no UpperCase or Special Values 
	 */
	public static String correctWord(String word) {
		String correctedWord = "";
		
		correctedWord = word.toLowerCase().
				replaceAll("\\!", " "). 
				replaceAll("\\,", "")
				.replaceAll("\\“", "")
				.replaceAll("\\—", "")
				.replaceAll("\\”", "").replaceAll("\\?", "").replaceAll("\\;", "");
		
		return correctedWord;
	} 
	
	/**
	 * 
	 * @param textDocument, name of text document program is looking for 
	 * @return wordTree, frequency of words in text document 
	 * @throws IOException, ignore input/output exception 
	 */
	public static TreeMap<String, Integer> countWords(String txtFile) throws  IOException{ 
		
		FileInputStream fin = new FileInputStream(txtFile); 
		Scanner fileInput = new Scanner(fin);

		// We need the key to be the string that is how we are identifying it
		TreeMap<String, Integer> wordTree = new TreeMap<>(Collections.reverseOrder());

		while (fileInput.hasNext()) {
			String nextWord = fileInput.next(); 					

			nextWord = correctWord(nextWord);

			if (wordTree.containsKey(nextWord)) {
				wordTree.put(nextWord, wordTree.get(nextWord) + 1); // increase associated key's(string)'s value

				// wordFromFille (KEY IS WORD, SECOND IS FREQUENCY)
				// map.put(key, map.get(key) + 1);
			} else {
				wordTree.put(nextWord, 1); // put value in tree
				// increase associated key's(string)'s value
				try {
					post(nextWord);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		
		//close
		fileInput.close();
		fin.close();
		
		return wordTree;
	}
	
	/**
	 * 
	 * @param sortedByValue, 
	 * 
	 */
	
	public static void printWords(LinkedHashMap<String, Integer> sortedByValue) { 
		
		System.out.println("Top 20 most common words: ");
		System.out.println(" ");
		Set<Entry<String, Integer>> wordsSortedByValue = sortedByValue.entrySet();

		int j = 0;
		int k = 1;
		for (Entry<String, Integer> mapping : wordsSortedByValue) {

			if (j <= 19) {
				System.out.print(k + ".");
				System.out.println(mapping.getKey() + "\t:" + mapping.getValue());
			} else {
				break;
			}
			k++;
			j++;
		}
		
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	
	public static Connection getConnection() throws Exception{
		
		  try{
			   String driver = "com.mysql.cj.jdbc.Driver";
			   String url = "jdbc:mysql://localhost:3306/wordoccurrences";
			   String username = "root";
			   String password = "1234";
			   Class.forName(driver);
			   
			   Connection conn = DriverManager.getConnection(url,username,password);
			   //System.out.println("Connected");
			   return conn;
			  } catch(Exception e){System.out.println(e);}
			  return null;
	}
	
	/**
	 * 
	 * @param nextWord, unique word not seen before to add to database
	 * @throws Exception
	 */
	
	public  static void post (String nextWord)	throws Exception{
		
		try {
			Connection con = getConnection();
			PreparedStatement posted = con.prepareStatement("INSERT INTO words VALUES ('" + nextWord + "')");
			posted.executeUpdate();
		}catch(Exception e) {System.out.println(e);}
		finally {
			//System.out.println("Insert complete");
		}
	}
	
}
