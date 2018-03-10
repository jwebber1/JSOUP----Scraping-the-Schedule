/*                                                                              Jonathan Webber
    Write the code to scrape both the subject and the departments.  Yes, I know it
would probably be quicker to just type the values into the database, but I want you
to insert them using your Java Program  You may create the database and the tables
by hand.
*/

//note "select distinct(subject), department from sections" will produce two field  with all subjects in a repeated department
//ALH   NUR
//HIF   NUR
//NUR   NUR
//PTA   NUR

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
            System.out.println("\tQ: Quit");
            System.out.print("Please enter a character to execute: ");

            //user enters choice
            char userIn = input.next().toUpperCase().charAt(0);
            System.out.println();

            String userInput;
            do {
                switch (userIn) {
                    //working
                    //Erase and Build "subjects" table
                    case 'A':
                        db.clear("subjects");
                        db.build(db, "subject");
                        System.out.println("Successfully erased and built \"disciplines/subjects\" table\n");
                        break;

                    //working
                    //Erase and Build "departments" table
                    case 'B':
                        db.clear("departments");
                        db.build(db, "department");
                        System.out.println("Successfully erased and built \"departments\" table\n");
                        break;

                    //working
                    //Print "subjects" table
                    case 'C':
                        db.report("subjects");
                        break;

                    //working
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

                    //working
                    //Print control-break section report for a department (will be prompted for the department)
                    case 'J':
                        System.out.print("Please enter desired department abbreviation: ");
                        userInput = input.next().toUpperCase();
                        System.out.println();
                        db.secCBreak(userInput);
                        break;

                    //working
                    //Produce the control-break output
                    case 'K':
                        db.contBreakAll();
                        break;

                    //working
                    //close the database connection
                    case 'Q':
                        System.out.println("Shutting down");
                        db.close();
                        break;

                    //working
                    //if the user inputs numbers or unspecified characters
                    default:
                        System.out.println("\tCould not understand input...\n");
                        break;

                }//end of switch
                if(!(userIn == 'Q')) {
                    System.out.print("Please enter a character to execute: ");
                    userIn = input.next().toUpperCase().charAt(0);
                    System.out.println();
                }
            }while(!(userIn == 'Q'));
            //in case the program isn't shut down correctly


        } catch (IOException e) {
            System.out.println("Trouble connecting to the website");
        }
    }

    //create the Sqlite class to use with the database
    static class Sqlite {
        //connect to the website
        Document doc = Jsoup.connect("https://aps2.missouriwestern.edu/schedule/?tck=201910").get();
        Connection conn;
        Sqlite() throws IOException {
        }

        //makes the initial connection to the database (Professor Noynaert's code)
        public boolean makeConnection(String fileName) {
            boolean successfullyOpened = false;

            String connectString = "jdbc:sqlite:" + fileName;
            try {
                conn = DriverManager.getConnection(connectString);
                if (conn != null) {
                    successfullyOpened = true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                successfullyOpened = false;
            }
            return successfullyOpened;
        }

        //builds the database
        void build(Sqlite db, String value) {
            //formatting a string to use in the select method
            String format0 = String.format("#%s [value]", value);
            String format1 = String.format("#%s option", value);

            //select methods to find the needed information
            Elements table = doc.select(format0);
            Elements tableAbbrev = doc.select(format1);

            //"inserts" the retrieved information into the database
            for (int i = 1; i < table.size(); i++) {
                db.insert(new Class(tableAbbrev.get(i).attr("value"), table.get(i).text()), value);
            }
        }

        //inserts data into the database
        void insert(Class _class, String tableName) {
            try {
                //create a statement
                Statement stmt = conn.createStatement();

                //formats the string to be able to execute the query successfully for non-noynaert database
                /*
                String queryString = String.format("INSERT INTO %s(abbrev, fullName) VALUES('%s', '%s');",
                                tableName + "s", _class.abbrev, _class.fullName);
                */

                //use with noynaert's db
                String queryString = String.format("INSERT INTO %s(disc, name) VALUES('%s', '%s');",
                        tableName + "s", _class.abbrev, _class.fullName);

                //execute and close the statement
                stmt.executeUpdate(queryString);
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //print out the specified table
        void report(String tableName) {
            try {
                //formats the string to be able to execute the query successfully
                String queryString = String.format
                        ("SELECT * FROM %s;", tableName);

                //get info for printing out the table
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(queryString);

                //prints out the table
                while (rs.next()) {
                    System.out.printf("%-4s --  %s\n", rs.getString(1), rs.getString(2));
                }

                //close the statement
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //produce a section break on the sections table
        void secCBreak(String department) {
            try {
                //query string for department information
                String queryString0 = String.format
                        ("SELECT name FROM departments WHERE disc like \"%s\";", department); //name to fullName, disc to abbrev in the final build
                Statement stmt0 = conn.createStatement();
                ResultSet rs0 = stmt0.executeQuery(queryString0);

                //print out department abbreviation and name
                System.out.printf("%-4s  --  %s\n", department, rs0.getString(1));


                //query string for disciplines in a department
                String queryString1 = String.format
                        ("SELECT distinct(discipline) FROM sections WHERE department LIKE \"%s\";", department); //discipline to subjects in the final build
                Statement stmt1 = conn.createStatement();
                ResultSet rs1 = stmt1.executeQuery(queryString1);


                //prints out the table
                while(rs1.next()){
                    //query string for matching the discipline abbreviation with the full name
                    String queryString2 = String.format
                            ("select name from subjects where disc like \"%s\";", rs1.getString(1)); //discipline to subjects in the final build
                    Statement stmt2 = conn.createStatement();
                    ResultSet rs2 = stmt2.executeQuery(queryString2);

                    //print out the table of abbreviation and name
                    System.out.printf("\t%-4s --  %s\n",rs1.getString(1), rs2.getString(1));
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //produce a control-break output for all courses
        void contBreakAll() {
            try {
                //create a query statement
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT title, section, seatsAvailable, maximumEnrollment FROM sections;");

                //create strings to check if there is a control-break
                String prevClass = "";
                String currClass = rs.getString(1);

                //counters for seats and a general counter for the first iteration
                int seatsTaken = 0;
                int seatsAvai = 0;
                int genCount = 0;
                do {
                    //very first time going through the loop
                    if(genCount == 0){
                        System.out.printf("%s\n", rs.getString(1));

                    }
                    //after the first time, print the seats taken/available for the previous class if there's a change in class
                    if(genCount>0 && !currClass.equals(prevClass)){
                        System.out.printf("\tTotal seats taken\t%d\n\tTotal seats available\t%d\n\n", seatsTaken, seatsAvai);
                        seatsAvai = 0;
                        seatsTaken = 0;
                        System.out.printf("%-30s\n", rs.getString(1));
                    }
                    //print the section number
                    System.out.printf("\tSection %-3s\n", rs.getString(2));

                    //add the seats available and taken
                    seatsAvai += (int)Double.parseDouble(rs.getString(3));
                    seatsTaken += (int)(Double.parseDouble(rs.getString(4))- Double.parseDouble(rs.getString(3)));

                    //benCount is no longer used
                    if(genCount == 0){genCount=1;}

                    //shift previous and current cluss forward
                    prevClass = currClass;
                    currClass = rs.getString(1);
                }while(rs.next());

                //tag on the last seats available/take
                System.out.printf("\tTotal seats taken\t%d\n\tseats available\t%d\n", seatsTaken, seatsAvai);
                System.out.println();


            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //clears the specified table
        void clear(String tableName) {
            try {
                //create a statement
                Statement stmt = conn.createStatement();

                //formats the string to be able to execute the query successfully
                String queryString = String.format
                        ("DELETE FROM %s;", tableName);

                //executes the query and then closes the statement
                stmt.executeUpdate(queryString);
                stmt.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //close the connection
        public void close() {
            try {
                conn.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //create the class Class to put into a database
    static class Class {
        String abbrev;
        String fullName;

        public Class(String abb, String name) {
            this.abbrev = abb;
            this.fullName = name;
        }

        //toString method
        @Override
        public String toString() {
            return String.format("%s\t%s", abbrev, fullName);
        }
    }
}