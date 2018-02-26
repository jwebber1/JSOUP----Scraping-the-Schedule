/*                                                                              Jonathan Webber
    Write the code to scrape both the subject and the departments.  Yes, I know it
would probably be quicker to just type the values into the database, but I want you
to insert them using your Java Program  You may create the database and the tables
by hand.
*/

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.*;

public class DET_03_Midterm {
    public static void main(String[] args) {
        try {
            //connect to the website
            Document doc = Jsoup.connect("https://aps2.missouriwestern.edu/schedule/?tck=201910").get();

            //create two arraylists to hold the subjects and abbreviations
            // and departments and abbreviations
            ArrayList<String> subjectText = new ArrayList<>();
            ArrayList<String> departmentText = new ArrayList<>();

            //select methods to find the needed information
            Elements subjects = doc.select("#subject [value]");
            Elements departments = doc.select("#department [value]");
            Elements subAbbrev = doc.select("#subject option");
            Elements depAbbrev = doc.select("#department option");

            //for loops to put the information into arraylists
            for(int i = 1; i<subjects.size(); i++){
                subjectText.add(subAbbrev.get(i).attr("value"));
                subjectText.add(subjects.get(i).text());
            }
            for(int i = 1; i<departments.size(); i++){
                departmentText.add(depAbbrev.get(i).attr("value"));
                departmentText.add(departments.get(i).text());
            }

            //for loop to print out the information (double check)
            for(int i = 0; i<subjectText.size(); i+=2){
                System.out.printf("Subject abbrev & name: %s %s\n", subjectText.get(i), subjectText.get(i+1));
            }
            System.out.println();
            for(int i = 0; i<departmentText.size(); i+=2){
                System.out.printf("Subject abbrev & name: %s\t%s\n", departmentText.get(i), departmentText.get(i+1));
            }
        }
        catch (IOException e){
            System.out.println("Trouble connecting to the website");
        }
    }
}
