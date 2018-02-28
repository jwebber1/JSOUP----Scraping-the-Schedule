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
            Scanner input = new Scanner(System.in);

            //connect to the website
            Document doc = Jsoup.connect("https://aps2.missouriwestern.edu/schedule/?tck=201910").get();

            Sqlite db = new Sqlite();

            //make the connection with the database
            db.makeConnection("classesData.db");
            //clear out the subjects/departments tables


        System.out.println("    A: Erase and Build Subjects table");
        System.out.println("    B: Erase and Build Departments table");
        System.out.println("    C: Print Subjects table");
        System.out.println("    D: Print Departments table");
        System.out.println("    E: Print the report of disciplines by Disciplines");
        System.out.println("    G: Erase and Build Sections data");
        System.out.println("    H: Print a simple listing of all sections by department or by discipline (will be prompted)");
        System.out.println("    I: Print faculty and faculty schedules by department");
        System.out.println("    J: Print control-break section report for a department (will be prompted for the department)");
        System.out.println("    K: Produce the control-break output");
        System.out.println("    L: A statement about how much Person B hates the web developer that designed the sections layout");
        System.out.println("    Q: Quit");
        System.out.print("Please enter a character to execute: ");
        char userIn = input.next().toUpperCase().charAt(0);

        System.out.println();

        switch (userIn){
            case 'A':
                db.clear("subjects");
                db.build(db, "subject");
                break;
            case 'B':
                db.clear("departments");
                db.build(db, "department");
                break;
            case 'C':
                db.report("subjects");
                break;
            case 'D':
                db.report("departments");
                break;
            case 'E':

                break;
            case 'G':

            case 'H':

            case 'I':

            case 'J':

            case 'K':

            case 'L':

            case 'Q':

        }
        //db.insert(new Class("AAA", "Alakazam"), "subjects");
        db.report("departments");
            db.close();
        }
        catch (IOException e){
            System.out.println("Trouble connecting to the website");
        }
    }
}

class Sqlite {
    //connect to the website
    Document doc = Jsoup.connect("https://aps2.missouriwestern.edu/schedule/?tck=201910").get();

    Connection conn;

    Sqlite() throws IOException {
    }

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

    void build(Sqlite db, String value){


        String format0 = String.format("#%s [value]", value);
        String format1 = String.format("#%s option", value);

        //select methods to find the needed information
        Elements table = doc.select(format0);
        Elements tableAbbrev = doc.select(format1);

        
        for(int i = 1; i<table.size(); i++) {
            db.insert(new Class(tableAbbrev.get(i).attr("value"), table.get(i).text()), value);

        }


    }

    void insert(Class _class, String tableName){
        try {

            Statement stmt = conn.createStatement();
            String queryString = String.format
                    ("INSERT INTO %s(abbrev, fullName) VALUES('%s', '%s');",
                            tableName+"s", _class.abbrev, _class.fullName);


            stmt.executeUpdate(queryString);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void report(String tableName){
        try{

            String queryString = String.format
                    ("SELECT * FROM %s;", tableName);

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(queryString);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNum = rsmd.getColumnCount();

            while(rs.next()){
                System.out.print(rs.getString(1)+ " -- " + rs.getString(2) + "\n");

            }


            //st.executeUpdate(queryString);
            st.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void clear(String tableName){
        try{
            Statement stmt = conn.createStatement();
            String queryString = String.format
                    ("DELETE FROM %s;", tableName);


            stmt.executeUpdate(queryString);
            stmt.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void close(){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class Class{
    String abbrev;
    String fullName;

    public Class (String abb, String name){
        this.abbrev = abb;
        this.fullName = name;
    }

    @Override
    public String toString(){
        return String.format("%s\t%s", abbrev, fullName);
    }
}