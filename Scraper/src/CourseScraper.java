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

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;



public class CourseScraper extends WebScraper{
    private String website;
    private int ID;
    private Thread thread;

    public CourseScraper(String link, int id) {
        super(link, id);
        website = link;
        ID = id;

    }

    @Override
    public void run() {
        Document doc = request(website);
        if(doc != null){
            populateCourses(doc);
        }
    }

    private void populateCourses(Document doc) {

        //truncate courses first in database

        ArrayList<String> courses = new ArrayList<String>();
        Statement statement = null;
        java.sql.Connection connection = null;

        String coursecode;
        String coursename;
        String programname;

        //Database connection
        try{
            //enter username and password to db
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc-chatbot", user, pass);

            statement = connection.createStatement();

            //get data

            //Elements courses = doc.getElementsByClass("load-program-courses");
            Elements index = doc.select("td.prefixlist");

            //list of course indexes
            for(Element i : index){
                courses.add(i.text());
                //System.out.println(course.text());
            }

            for(int i = 0; i<=courses.size(); i++){
                //retrieves all course codes and names
                Element table = doc.getElementById("dblist_listing");
                for (Element row : table.select("tr")) {
                    Elements tds = row.select("td");
                    String parts[] = tds.get(0).text().split(" ", 2);
                    //System.out.println(tds.get(0).text());
                    coursecode = parts[1];
                    //System.out.println(coursecode);
                    programname = parts[0];
                    //System.out.println(programname);
                    coursename = tds.get(1).text();
                    //System.out.println(tds.get(1).text());

                    if(coursecode.equals("Code")){
                        continue;
                    }
                    else{
                        //add courses to database
                        String query = "INSERT IGNORE INTO courses(CourseCode, CourseName, ProgramName) " +
                                "VALUES " + "(" + '"'+coursecode+'"' + "," + '"'+coursename+'"' + "," + '"'+programname+'"' + ")";
                        System.out.println(query);
                        statement.executeUpdate(query);
                    }

                }
                //go to next index page
                if(i == courses.size()){
                    break;
                }else{
                    doc = Jsoup.connect("https://brocku.ca/webcal/courses.php?prefix="+courses.get(i)).get();
                }
            }

            //need to add graduate courses (can be found using same link(s))

            //get info for each course
            //fall winter term for undergraduates
            doc = Jsoup.parse(getPageSource("https://brocku.ca/guides-and-timetables/timetables/?session=fw&type=ug&level=all"));

            //gets list of program codes available in fall/winter term
            Elements program = doc.getElementsByClass("code");

            for (Element p : program){
                //for each program go to course listings
                doc = Jsoup.parse(getPageSource("https://brocku.ca/guides-and-timetables/timetables/?session=FW&type=UG&level=All&program="+p.text()));
                //System.out.println(doc.html());
                getCourseInfo(doc,statement,"Fall/Winter");
            }

            //Spring term for undergraduates
            doc = Jsoup.parse(getPageSource("https://brocku.ca/guides-and-timetables/timetables/?session=sp&type=ug&level=all"));

            //gets list of program codes available in spring term
            program = doc.getElementsByClass("code");

            for (Element p : program){
                //for each program go to course listings
                doc = Jsoup.parse(getPageSource("https://brocku.ca/guides-and-timetables/timetables/?session=SP&type=UG&level=All&program="+p.text()));
                //System.out.println(doc.html());
                getCourseInfo(doc,statement,"Spring");
            }

            //Summer term for undergraduates
            doc = Jsoup.parse(getPageSource("https://brocku.ca/guides-and-timetables/timetables/?session=su&type=ug&level=all"));

            //gets list of program codes available in summer term
            program = doc.getElementsByClass("code");

            for (Element p : program){
                //for each program go to course listings
                doc = Jsoup.parse(getPageSource("hhttps://brocku.ca/guides-and-timetables/timetables/?session=SU&type=UG&level=All&program="+p.text()));
                //System.out.println(doc.html());
                getCourseInfo(doc,statement,"Summer");
            }

            //add graduate courses

            System.out.println("Course table updated");


        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                if(statement != null){
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

    public void getCourseInfo(Document doc, Statement statement, String t) throws SQLException {
        Elements tables = doc.getElementsByClass("course-table course-listing");
        for(Element table : tables) {
            Elements tableRows = table.child(1).children().select("tr");
            for(Element tableRow : tableRows){

                Boolean hasLab = false;
                Boolean hasTut = false;
                Boolean hasSeminar = false;
                String days = null;
                String labTime;
                String tutTime;
                String semTime;
                String lecTime;
                String offerings = null;
                String prerequisites = null;
                String restrictions = null;
                String notes;
                String coursedescription = null;
                String duration;
                String term;
                String location;
                String instructor = null;
                String section;
                String ccode;
                String cname;


                //get list of course codes
                String code = tableRow.getElementsByClass("course-code").text();
                //System.out.println("code: " + code);

                String pos[] = code.split(" ", 2);
                if(pos.length > 1){
                    ccode = pos[1];
                    cname = pos[0];
                }
                else{
                    ccode = pos[0];
                    cname = pos[0];
                }


                //get term
                term = t;

                //get type
                String type = tableRow.getElementsByClass("type").text();
                //System.out.println("type: "+type);

                //check if tutorial, lab, or seminar
                if(type.contains("SEM") == true){
                    hasSeminar = true;
                    String semquery = "UPDATE courses SET HasSeminar = " + hasSeminar + " WHERE CourseCode ="+'"'+ccode+'"'+" AND ProgramName = "+'"'+cname+'"';
                    //System.out.println(semquery);
                    statement.executeUpdate(semquery);
                }
                if(type.contains("LAB") == true){
                    hasLab = true;
                    String labquery = "UPDATE courses SET HasLab = " + hasLab + " WHERE CourseCode ="+'"'+ccode+'"'+" AND ProgramName = "+'"'+cname+'"';
                    //System.out.println(labquery);
                    statement.executeUpdate(labquery);
                }
                if(type.contains("TUT") == true){
                    hasTut = true;
                    String tutquery = "UPDATE courses SET HasTutorial = " + hasTut + " WHERE CourseCode ="+'"'+ccode+'"'+" AND ProgramName = "+'"'+cname+'"';
                    //System.out.println(tutquery);
                    statement.executeUpdate(tutquery);
                }


                //get days
                Elements calender = tableRow.getElementsByClass("coursecal");
                for(Element c : calender){
                    days = c.getElementsByClass("active").text();
                }

                if(days == null || days.length() > 13){
                    days = " ";
                }
                //days = tableRow.getElementsByClass("active").text();
                //System.out.println("days: " + days);
                //if days == &nbsp; make null

                //get time
                String time = tableRow.getElementsByClass("time").text();
                if(time == null){
                    time = " ";
                }
                //System.out.println(time);

                if(hasLab){
                    labTime = time + " " + days;
                    String labtimequery = "UPDATE courses SET " + "LabHours=concat(LabHours," + '"'+ " "+labTime+'"'+") WHERE CourseCode ="+'"'+ccode+'"'+" AND ProgramName = "+'"'+cname+'"';
                    System.out.println(labtimequery);
                    statement.executeUpdate(labtimequery);
                }
                else if(hasSeminar){
                    semTime = time + " " + days;
                    String semtimequery = "UPDATE courses SET " + "SeminarHours=concat(SeminarHours," + '"'+ " "+semTime+'"'+") WHERE CourseCode ="+'"'+ccode+'"'+" AND ProgramName = "+'"'+cname+'"';
                    System.out.println(semtimequery);
                    statement.executeUpdate(semtimequery);
                }
                else if(hasTut){
                    tutTime = time + " " + days;
                    String tuttimequery = "UPDATE courses SET " + "TutorialHours=concat(TutorialHours," + '"'+ " "+tutTime+'"'+") WHERE CourseCode ="+'"'+ccode+'"'+" AND ProgramName = "+'"'+cname+'"';
                    System.out.println(tuttimequery);
                    statement.executeUpdate(tuttimequery);
                }
                else{
                    lecTime = time + " " + days;
                    //System.out.println("time: "+ time);
                    //System.out.println("days: "+ days);
                    String lectimequery = "UPDATE courses SET " + "LectureHours=concat(LectureHours," + '"'+ " "+lecTime+'"'+") WHERE CourseCode ="+'"'+ccode+'"'+" AND ProgramName = "+'"'+cname+'"';
                    System.out.println(lectimequery);
                    statement.executeUpdate(lectimequery);
                }


                //get description (Format, Restriction, Prerequisites and notes)
                Elements description = tableRow.getElementsByClass("description").select("p");
                for(Element d:description){
                    if(d.text().contains("Format:")){
                        offerings = d.text();
                        //System.out.println(d.text());
                    }
                    if(d.text().contains("Prerequisites:")){
                        prerequisites = d.text();
                        //System.out.println(d.text());
                    }
                    if(d.text().contains("Restrictions:")){
                        restrictions = d.text();
                        //System.out.println(d.text());
                    }
                    if(d.text().contains("Notes:")){
                        notes = d.text();
                        //System.out.println(d.text());
                    }

                }

                //get information about course
                Elements information = tableRow.getElementsByClass("page-intro").select("p");
                for(Element i: information){
                    coursedescription = i.text();
                    coursedescription = coursedescription.replaceAll("\"","'");
                    //System.out.println("Description: "+i.text());
                }

                //get vitals (duration, time, location, instructor and section)
                Elements vitals = tableRow.getElementsByClass("vitals").select("ul").select("li");
                for(Element v:vitals){
                    if(v.text().contains("Duration:")){
                        duration = v.text();
                        term = term + " " + duration;
                        //System.out.println(term);
                    }
//                        if(v.text().contains("Time:")){
//                            System.out.println(v.text());
//                        }
                    if(v.text().contains("Location:")){
                        location = v.text();
                        //System.out.println(location);
                    }
                    if(v.text().contains("Instructor:")){
                        instructor = v.text();
                        //System.out.println(instructor);
                    }
                    if(v.text().contains("Section:")){
                        section = v.text();
                        //System.out.println(section);
                    }
                }

                //add information to database
                //match course code and name with CourseCode and ProgramName in database
                String coursequery = "UPDATE courses " +
                        "SET " + "Term = " + '"'+term+'"'+  ", Offerings = " + '"'+offerings+'"' + ", CourseDescription = " +
                        '"'+coursedescription+'"' + ", Instructor = " +'"'+ instructor +'"'+ ", Prerequisites = " +'"'+ prerequisites +'"'+
                        ", Restrictions = " +'"'+ restrictions +'"'+
                        " WHERE CourseCode ="+'"'+ccode+'"'+" AND ProgramName = "+'"'+cname+'"';

                //need to concatenate terms

                System.out.println(coursequery);
                statement.executeUpdate(coursequery);

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
        for(WebElement e : driver.findElements(By.className("type"))){
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
