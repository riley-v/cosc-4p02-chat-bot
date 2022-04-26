import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import java.sql.*;
import java.util.ArrayList;

public class ExamScraper extends WebScraper{
    private String website;
    private int ID;
    private Thread thread;

    public ExamScraper(String link, int id) {
        super(link, id);
        website = link;
        ID = id;

    }

    @Override
    public void run() {
        Document doc = request(website);
        if(doc != null){
            //fall/winter
            populateExams(website);
            //spring june
            populateExams("https://brocku.ca/guides-and-timetables/timetables/?session=spjun&type=ex&level=all");
            //spring july
            populateExams("https://brocku.ca/guides-and-timetables/timetables/?session=spjul&type=ex&level=all");
            //spring august
            populateExams("https://brocku.ca/guides-and-timetables/timetables/?session=spaug&type=ex&level=all");
            //summer august
            populateExams("https://brocku.ca/guides-and-timetables/timetables/?session=su&type=ex&level=all");
        }
    }

    private void  populateExams(String web){
        ArrayList<String> programs = new ArrayList<String>();
        String coursecode = null;
        String duration = null;
        String section = null;
        String date = null;
        String start = null;
        String end = null;
        String time = null;
        String location = null;
        Boolean inPerson = null;
        String cprog = null;
        String ccode = null;

        Statement statement = null;
        Connection connection = null;

        //Database connection
        try{
            //enter username and password to db
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc-chatbot", user, pass);
            statement = connection.createStatement();

            //fall/winter
            //get list of course codes that have exams and add to arraylist
            Document doc = Jsoup.parse(getPageSource(web));
            //System.out.println(doc.text());

            Elements program = doc.getElementsByClass("code");
            for(Element p : program){
                //System.out.println(p.text());
                programs.add(p.text());
            }

            //iterate through each program webpage and retrieve exam information for corresponding courses
            for(int i = 0; i < programs.size(); i++){
                String programcode = programs.get(i);
                doc = Jsoup.parse(getPageSource("https://brocku.ca/guides-and-timetables/timetables/?session=fw&type=ex&level=all&program="+programcode+"&academicyear="+year+"&period=April"));

                //retrieve information (duration, section, date, time, and location)
                Elements data = doc.getElementsByClass("exam-row normal");
                for(Element d : data){
                     for(Element e: d.getAllElements()){
                         if(e.className().equals("course-code")){
                             coursecode= e.text();
                             //System.out.println("coursecode: " + coursecode);
                         }
                         if(e.className().equals("duration")){
                             duration = e.text();
                             //System.out.println("duration: " + duration);
                         }
                         if(e.className().equals("section")){
                             section = e.text();
                             //System.out.println("section: " + section);
                         }
                         if(e.className().equals("day")){
                             date = e.text();
                             //System.out.println("date: " + date);
                         }
                         if(e.className().equals("start")){
                             start = e.text();
                             //System.out.println("start: " + start);
                         }
                         if(e.className().equals("end")){
                             end = e.text();
                             //System.out.println("end: " + end);
                         }
                         if(e.className().equals("location")){
                             location = e.text();
                             if(location.equals("SAKAI") || location.equals("ONLINE")){
                                 inPerson = false;
                             }
                             else{
                                 inPerson = true;
                             }
                             //System.out.println("location: " + location);
                         }

                         time = start + " to " + end + " ";
                         if(date == null || location == null){
                             continue;
                         }

                         //get program and code separately
                         String pos[] = coursecode.split(" ", 2);
                         if(pos.length > 1){
                             cprog = pos[0];
                             ccode = pos[1];
                         }
//                         System.out.println("prog: " + cprog);
//                         System.out.println("code: " + ccode);



                     }
                    //update course table in database
                    String coursequery = "UPDATE courses " +
                            "SET " + "HasExam = " + true+
                            " WHERE CourseCode ="+'"'+ccode+'"'+" AND ProgramName = "+'"'+cprog+'"';
                    //System.out.println(coursequery);
                    statement.executeUpdate(coursequery);

                    //store data into database tables
                    String query = "INSERT IGNORE INTO exams(CourseCode, Date, Time, Location, InPerson, Section, Duration) " +
                            "VALUES " + "(" + '"'+coursecode+'"' + "," + '"'+date+'"' + "," + '"'+time+'"' + "," + '"'+location+'"' +"," + inPerson +"," + '"'+section+'"' +"," + '"'+duration+'"' + ")";

                    System.out.println(query);
                    statement.executeUpdate(query);
                }

            }

            System.out.println("Exam table updated");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                if(statement != null){
                    //System.out.println("success");
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if(connection != null){
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }



    }

    public String getPageSource(String url){
        //Template for instantiating selenium with chrome
        System.setProperty(browser,browserdriverpath);
        ChromeOptions option = new ChromeOptions();
        option.addArguments("headless");    //prevents opening of browser
        WebDriver driver = new ChromeDriver(option);
        driver.get(url);
        //If webpage has a clickable table, it clicks each row in table
        for(WebElement e : driver.findElements(By.id("exam-row normal"))){
            //WebDriverWait wait = new WebDriverWait(driver, 10);
            //e = wait.until(ExpectedConditions.elementToBeClickable(By.className("type")));
            Actions actions = new Actions(driver);
            //actions.pause(java.time.Duration.ofSeconds(1));
            actions.pause(1400);    //wait 1400 miliseconds in between clicks
            actions.moveToElement(e).click().build().perform(); //perform hover mouse over the element and click it

        }

        String pagesource = driver.getPageSource();
        driver.quit();

        return pagesource;
    }
}
