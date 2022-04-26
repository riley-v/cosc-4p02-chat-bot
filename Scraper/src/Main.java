import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args){


        //template for instantiating database
//        Statement statement = null;
//        Connection connection = null;
//
//        //Database connection
//        try{
//            //enter username and password to db
//            connection = DriverManager.getConnection("jdbc:mysql://database-1.ct3xgv3usvqg.us-east-1.rds.amazonaws.com:3306/ChatbotDatabase", "admin", "kwftyZpDS1Lr70y6VyGp");
//
//            statement = connection.createStatement();

            //Inserting data into table
//            String query = "INSERT INTO test " +
//                    "VALUES (12345,'TestName')";
//
//            statement.executeUpdate(query);

            //Retrieving data from table
            //ResultSet resultSet = statement.executeQuery("select * from test");

//            while (resultSet.next()){
//                System.out.println(resultSet.getString("id") + " " + (resultSet.getString("name")));
//            }
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//        finally {
//            try {
//                if(statement != null){
//                    System.out.println("success");
//                    connection.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//            try {
//                if(connection != null){
//                    connection.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }


        //Scraper

        ArrayList<WebScraper> bots = new ArrayList<>();

        //Initialize threaded objects
        bots.add(new WebScraper("https://brocku.ca/" , 1));
        //bots.add(new WebScraper("https://niagara2022games.ca/" , 2));
        //bots.add(new WebScraper("https://brocku.ca/webcal/2021/undergrad/" , 2));

        //link to populate the department table in database
        bots.add(new WebScraper("https://brocku.ca/directory/a-z/" , 1));

        //link to fill course table
        bots.add(new WebScraper("https://brocku.ca/webcal/courses.php?prefix=A" , 1));
        bots.add(new CourseScraper("https://brocku.ca/webcal/courses.php?prefix=A" , 1));

        //link to fill department tables
        bots.add(new DepartmentScraper("https://brocku.ca/directory/a-z/" , 2));

        //link to fill exam table
        bots.add(new ExamScraper("https://brocku.ca/guides-and-timetables/timetables/?session=fw&type=ex&level=all" , 1));

        //link to fill program tables
        bots.add(new ProgramScraper("https://brocku.ca/programs/" , 4));

        //Used for multi-threaded execution
        for(WebScraper w: bots){
            try{
                w.getThread().join();
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}