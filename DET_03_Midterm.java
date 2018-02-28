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
import java.sql.*;


public class DET_03_Midterm {
    public static void main(String[] args) {
        try {
            //open a scanner
            Scanner input = new Scanner(System.in);

            //connect to the website to scrape
            Document doc = Jsoup.connect("https://aps2.missouriwestern.edu/schedule/?tck=201910").get();

            //create a SQlite class
            Sqlite db = new Sqlite();

            //make the connection with the database
            db.makeConnection("classesData.db");

            //Print possibilities for user to choose from
            System.out.println("\tA: Erase and Build Subjects table");
            System.out.println("\tB: Erase and Build Departments table");
            System.out.println("\tC: Print Subjects table");
            System.out.println("\tD: Print Departments table");
            System.out.println("\tE: Print the report of disciplines by Disciplines");
            System.out.println("\tG: Erase and Build Sections data");
            System.out.println("\tH: Print a simple listing of all sections by department or by discipline (will be prompted)");
            System.out.println("\tI: Print faculty and faculty schedules by department");
            System.out.println("\tJ: Print control-break section report for a department (will be prompted for the department)");
            System.out.println("\tK: Produce the control-break output");
            System.out.println("\tL: A statement about how much Person B hates the web developer that designed the sections layout");
            System.out.println("\tQ: Quit");
            System.out.print("Please enter a character to execute: ");

            //user enters choice
            char userIn = input.next().toUpperCase().charAt(0);
            System.out.println();

            String userInput;
            int count = 0;
            //while loop to allow for multiple tests
            while(userIn != 'Q') {


                switch (userIn) {
                    //Erase and Build "subjects" table
                    case 'A':
                        db.clear("subjects");
                        db.build(db, "subject");
                        System.out.println("Successfully erased and built \"subjects\" table\n");
                        break;

                    //Erase and Build "departments" table
                    case 'B':
                        db.clear("departments");
                        db.build(db, "department");
                        System.out.println("Successfully erased and built \"departments\" table\n");

                        break;

                    //Print "subjects" table
                    case 'C':
                        db.report("subjects");
                        break;

                    //Print "departments" table
                    case 'D':
                        db.report("departments");
                        break;

                    //Print the report of disciplines (subjects) by Departments
                    case 'E':

                        break;

                    //Erase and Build Sections data (will be prompted for the department)
                    case 'G':
                        System.out.print("Please enter desired department: ");
                        userInput = input.next();
                        System.out.println();

                        break;

                    //Print a simple listing of all sections by department or by discipline (will be prompted)
                    case 'H':
                        System.out.print("Please enter desired department or discipline (subject): ");
                        userInput = input.next();
                        System.out.println();

                        break;

                    //Print faculty and faculty schedules by department
                    case 'I':

                        break;

                    //Print control-break section report for a department (will be prompted for the department)
                    case 'J':
                        System.out.print("Please enter desired department: ");
                        userInput = input.next();
                        System.out.println();

                        break;

                    //Produce the control-break output
                    case 'K':

                        break;

                    //A statement about how much Person B hates the web developer that designed the sections layout
                    case 'L':
                        System.out.println("Person B hates web developer who designed sections layout...");

                        break;

                    //close the database connection
                    case 'Q':
                        System.out.println("Shutting down");
                        db.close();
                        break;

                    //user inputs numbers or unspecified characters
                    default:
                        System.out.println("\tCould not understand input...\n");
                        break;


                }
                System.out.print("Please enter a character to execute: ");
                userIn = input.next().toUpperCase().charAt(0);
                System.out.println();
            }
            System.out.println("Shutting down");
            db.close();
        }
        catch (IOException e){
            System.out.println("Trouble connecting to the website");
        }
    }
}

//create the Sqlite class to use with the database
class Sqlite {
    //connect to the website
    Document doc = Jsoup.connect("https://aps2.missouriwestern.edu/schedule/?tck=201910").get();

    Connection conn;

    Sqlite() throws IOException {}

    //makes the initial connection to the database (Professor Noynaert's code)
    public boolean makeConnection(String fileName){
        boolean successfullyOpened = false;

        String connectString = "jdbc:sqlite:" + fileName;
        try {
            conn = DriverManager.getConnection(connectString);
            if(conn!=null) {
                successfullyOpened = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            successfullyOpened = false;
        }
        return successfullyOpened;
    }

    //builds the database
    void build(Sqlite db, String value){
        //formatting a string to use in the select method
        String format0 = String.format("#%s [value]", value);
        String format1 = String.format("#%s option", value);

        //select methods to find the needed information
        Elements table = doc.select(format0);
        Elements tableAbbrev = doc.select(format1);

        //"inserts" the retrieved information into the database
        for(int i = 1; i<table.size(); i++) {
            db.insert(new Class(tableAbbrev.get(i).attr("value"), table.get(i).text()), value);
        }
    }

    //inserts data into the database
    void insert(Class _class, String tableName){
        try {
            //create a statement
            Statement stmt = conn.createStatement();

            //formats the string to be able to execute the query successfully
            String queryString = String.format("INSERT INTO %s(abbrev, fullName) VALUES('%s', '%s');",
                            tableName+"s", _class.abbrev, _class.fullName);

            //execute and close the statement
            stmt.executeUpdate(queryString);
            stmt.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //print out the specified table
    void report(String tableName){
        try{
            //formats the string to be able to execute the query successfully
            String queryString = String.format
                    ("SELECT * FROM %s;", tableName);

            //get info for printing out the table
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(queryString);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNum = rsmd.getColumnCount();

            //prints out the table
            while(rs.next()){
                System.out.printf("%-4s --  %s\n",rs.getString(1), rs.getString(2));
                //System.out.print(rs.getString(1)+ "-- " + rs.getString(2) + "\n");
            }

            //close the statement
            stmt.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //clears the specified table
    void clear(String tableName){
        try{
            //create a statement
            Statement stmt = conn.createStatement();

            //formats the string to be able to execute the query successfully
            String queryString = String.format
                    ("DELETE FROM %s;", tableName);

            //executes the query and then closes the statement
            stmt.executeUpdate(queryString);
            stmt.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //close the connection
    public void close(){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

//create the class Class to put into a database
class Class{
    String abbrev;
    String fullName;

    public Class (String abb, String name){
        this.abbrev = abb;
        this.fullName = name;
    }

    //toString method
    @Override
    public String toString(){
        return String.format("%s\t%s", abbrev, fullName);
    }
}