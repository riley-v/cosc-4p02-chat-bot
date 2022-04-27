import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class Main {

    private BufferedWriter dbOut;

    private ArrayList<TableRep> departments;
    private ArrayList<TableRep> programs;
    private ArrayList<TableRep> courses;
    private ArrayList<TableRep> exams;
    private ArrayList<TableRep> websites;

    private Main(){

        departments = new ArrayList<>();
        programs = new ArrayList<>();
        courses = new ArrayList<>();
        exams = new ArrayList<>();
        websites = new ArrayList<>();

        try {
            dbOut = new BufferedWriter(new FileWriter("Intents/DatabaseIntents.txt"));
        } catch (IOException e) {
            dbOut = null;
            System.out.println("DatabaseIntents.txt could not be created...");
        }

        Connection conn = null;
        Session session = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            //Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (Exception except) {
            System.out.println("The driver was not obtained");
        }

        try {
            //Amazon connection
            //conn = DriverManager.getConnection("jdbc:mysql://database-1.ct3xgv3usvqg.us-east-1.rds.amazonaws.com:3306/ChatbotDatabase?useUnicode=yes&characterEncoding=UTF-8", "admin", "kwftyZpDS1Lr70y6VyGp");

            //Oracle connection
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");

            JSch jsch = new JSch();
            jsch.addIdentity("Intents/ssh-key-2022-04-16.key");

            session = jsch.getSession("opc", "140.238.157.158");
            session.setConfig(config);
            session.connect();

            int forwardedPort = session.setPortForwardingL(0, "10.0.0.170", 3306);
            conn = DriverManager.getConnection("jdbc:mysql://localhost:" + forwardedPort + "/ChatbotDatabase?useUnicode=yes&characterEncoding=UTF-8", "admin", "Password123_");

            //local connection
            //conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbcchatbot?useUnicode=yes&characterEncoding=UTF-8", "root", "bitb");

            // get department information
            Statement stmt = conn.createStatement();
            String query = "select * from department ;";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()){
                departments.add(new Department(
                        rs.getString("DepartmentName"),
                        rs.getString("ContactInformation"),
                        rs.getString("DepartmentPageLink")));
            }
            stmt.close();

            // get program information
            stmt = conn.createStatement();
            query = "select * from programs ;";
            rs = stmt.executeQuery(query);
            while (rs.next()){
                programs.add(new Program(
                        rs.getString("ProgramName"),
                        rs.getString("Requirements"),
                        rs.getString("Description"),
                        rs.getString("ProgramPageLink"),
                        rs.getString("DepartmentName")));
            }
            stmt.close();

            // get course information
            stmt = conn.createStatement();
            query = "select * from courses ;";
            rs = stmt.executeQuery(query);
            while (rs.next()){
                courses.add(new Course(
                        rs.getString("CourseName"),
                        rs.getString("CourseCode"),
                        rs.getString("ProgramName"),
                        rs.getString("Term"),
                        rs.getBoolean("HasExam"),
                        rs.getString("Offerings"),
                        rs.getString("CourseDescription"),
                        rs.getString("Instructor"),
                        rs.getString("Prerequisites"),
                        rs.getString("Restrictions"),
                        rs.getBoolean("HasLab"),
                        rs.getString("LabOptions"),
                        rs.getString("LectureTimes"),
                        rs.getString("LabHours"),
                        rs.getBoolean("HasTutorial"),
                        rs.getString("TutorialHours"),
                        rs.getString("LectureHours"),
                        rs.getBoolean("HasSeminar"),
                        rs.getString("SeminarHours")));
            }
            stmt.close();

            // get exam information
            stmt = conn.createStatement();
            query = "select * from exams ;";
            rs = stmt.executeQuery(query);
            while (rs.next()){
                exams.add(new Exam(
                        rs.getString("CourseCode"),
                        rs.getString("Date"),
                        rs.getString("Time"),
                        rs.getString("Location"),
                        rs.getBoolean("InPerson"),
                        rs.getString("Section")));
            }
            stmt.close();

            // get website information
            stmt = conn.createStatement();
            query = "select * from brock;";
            rs = stmt.executeQuery(query);
            while (rs.next()){
                websites.add(new BrockEWebsite(
                        rs.getString("Topic"),
                        rs.getString("PageLink")));
            }
            stmt.close();

        } catch (SQLException except) {
            System.out.println("Sql exception occurred: " + except.getMessage());
            except.printStackTrace();
            System.out.println("Current state of Sql is: " + except.getSQLState());
        } catch (JSchException except){
            System.out.println("JSch exception occurred: " + except.getMessage());
            except.printStackTrace();
        } finally {
            try {
                if(conn!=null && !conn.isClosed()) conn.close();
                if(session!=null && session.isConnected()) session.disconnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(dbOut != null) {
            try {
                createDepartmentIntents();
                createProgramIntents();
                createCourseIntents();
                createExamIntents();
                createBrockEWebsiteIntents();
                dbOut.close();
            } catch (IOException e) {
                System.out.println("DatabaseIntents.txt could not be properly written...");
            }

            //create ignore words
            try {
                BufferedWriter ignoreList = new BufferedWriter(new FileWriter("Intents/IgnoreWords.txt"));

                //add unique course codes
                ArrayList<String> progs = new ArrayList<>();
                String prev = "";
                String nextProg;
                String nextCode;
                for (TableRep c:courses) {
                    String[] next = ((Course) c).getCourseCode().split(" ", 2);
                    nextProg = next[0];
                    nextCode = next[1];

                    boolean write = true;
                    for (String p:progs) {
                        if(nextProg.equals(p)) write = false;
                    }
                    if(write){
                        progs.add(nextProg);
                        ignoreList.write(nextProg);
                        ignoreList.newLine();
                    }

                    if(!nextCode.equals(prev)){
                        ignoreList.write(nextCode);
                        ignoreList.newLine();
                        prev = nextCode;
                    }
                }

                ignoreList.close();
            } catch (IOException e) {
                System.out.println("IgnoreWords.txt could not be created and properly written...");
            }
        }
    }

    private void createDepartmentIntents() throws IOException {
        String list;

        write("deplist");
        write("-P");
        write("What departments are there?");
        write("What are the departments at Brock?");
        write("-R");
        list = toList(departments, 0);
        write("The departments at Brock are " + list + ".");
        write("Brock's departments include " + list + ".");
        write("");

        for (int i = 0; i < departments.size(); i++) {
            Department d = (Department) departments.get(i);

            write("contact" + d.getName().toLowerCase().replaceAll("\\s",""));
            write("-P");
            write("What is the contact information for the " + d.getName().toLowerCase() + " department?");
            write("Contact info for " + d.getName().toLowerCase() + "?");
            write("Who can I talk to from " + d.getName().toLowerCase() + "?");
            write("-R");
            write("You can contact the " + d.getName() + " department at " + d.getContactInformation() + ".");
            write("Use this information to talk to someone from " + d.getName() + ": " + d.getContactInformation());
            write("");

            write("visit" + d.getName().toLowerCase().replaceAll("\\s",""));
            write("-P");
            write("What is the link for the " + d.getName().toLowerCase() + " web page?");
            write("Where can I find the " + d.getName().toLowerCase() + " department on the internet?");
            write("-R");
            write("Visit " + d.getPageLink() + " for more information on the " + d.getName() + " department.");
            write("The " + d.getName() + " web page can be found at " + d.getPageLink() + ".");
            write("");

            write(d.getName().toLowerCase().replaceAll("\\s","") + "programs");
            write("-P");
            write("What programs are in " + d.getName().toLowerCase() + "?");
            write("What can I take in " + d.getName().toLowerCase() + "?");
            write("Programs in " + d.getName().toLowerCase() + "?");
            write("-R");
            ArrayList<TableRep> depProgs = new ArrayList<>();
            programs.stream()
                    .map(Program.class::cast)
                    .filter(p -> p.getDepartmentName().equals(d.getName()))
                    .forEach(depProgs::add);
            list = toList(depProgs, 0);
            write("The " + d.getName() + " department has the following programs: " + list);
            write("In " + d.getName() + ", you can take " + list + ".");
            write("");
        }

    }

    private void createProgramIntents() throws IOException {
        String list;

        for (int i = 0; i < programs.size(); i++) {
            Program p = (Program) programs.get(i);

            write(p.getName().toLowerCase().replaceAll("\\s","") + "requirements");
            write("-P");
            write("What are the requirements for " + p.getName().toLowerCase() + "?");
            write("What do I have to do to get into " + p.getName().toLowerCase() + "?");
            write("-R");
            write("The requirements for " + p.getName() + " are: " + p.getRequirements());
            write("Here are the requirements for " + p.getName() + ": " + p.getRequirements());
            write("");

            write(p.getName().toLowerCase().replaceAll("\\s","") + "description");
            write("-P");
            write("What is " + p.getName().toLowerCase() + "?");
            write("What is the " + p.getName().toLowerCase() + " program like?");
            write("-R");
            write("This description for " + p.getName() + " is from Brock's website: " + p.getDescription());
            write(p.getName() + " according to Brock's website: " + p.getDescription());
            write("");

            write("visit" + p.getName().toLowerCase().replaceAll("\\s",""));
            write("-P");
            write("What is the link for the " + p.getName().toLowerCase() + " web page?");
            write("Where can I find the " + p.getName().toLowerCase() + " program on the internet?");
            write("-R");
            write("Visit " + p.getPageLink() + " for more information on the " + p.getName() + " program.");
            write("The " + p.getName() + " web page can be found at " + p.getPageLink() + ".");
            write("");

            write(p.getName().toLowerCase().replaceAll("\\s","") + "department");
            write("-P");
            write("What department does " + p.getName().toLowerCase() + " belong to?");
            write("Where does " + p.getName().toLowerCase() + " belong?");
            write("-R");
            write("The " + p.getName() + " program is part of the " + p.getDepartmentName() + " department.");
            write(p.getName() + " is a " + p.getDepartmentName() + " program.");
            write("");

            write(p.getName().toLowerCase().replaceAll("\\s","") + "courses");
            write("-P");
            write("What courses are in " + p.getName().toLowerCase() + "?");
            write("What classes can I take in " + p.getName().toLowerCase() + "?");
            write(p.getName().toLowerCase() + " classes?");
            write("-R");
            ArrayList<TableRep> progClasses = new ArrayList<>();
            courses.stream()
                    .map(Course.class::cast)
                    .filter(c -> c.getProgramName().equals(p.getName()))
                    .forEach(progClasses::add);
            list = toList(progClasses, 0);
            write("The " + p.getName() + " program has the following courses: " + list);
            write("In " + p.getName() + ", you can take " + list + ".");
            write("");
        }
    }

    private void createCourseIntents() throws IOException {
        //String list;

        /**
        //Course Name
        write("courselist");
        write("-P");
        write("What courses are there?");
        write("What are the courses at Brock?");
        write("What classes can I take?");
        write("-R");
        list = toList(courses, 0);
        write("The courses at Brock are " + list + ".");
        write("Brock's courses include " + list + ".");
        write("");
         **/

        for (int i = 0; i < courses.size(); i++) {
            Course c = (Course) courses.get(i);

            //Course Code
            write(c.getCourseCode().toLowerCase().replaceAll("\\s","") + "code");
            write("-P");
            write("What is the course code for " + c.getCourseName().toLowerCase());
            write("Course code for " + c.getCourseName().toLowerCase() + "?");
            write("What is the code of " + c.getCourseName().toLowerCase() + "?");
            write("-R");
            write("The course code for " + c.getCourseName() + " is " + c.getCourseCode() + ".");
            write(c.getCourseCode() + " is the course code for " + c.getCourseName());
            write("");

            //Term
            write(c.getCourseCode().toLowerCase().replaceAll("\\s","") + "term");
            write("-P");
            write("What term is " + c.getCourseName().toLowerCase() + " in?");
            write("What terms does " + c.getCourseName().toLowerCase() + " take place in?");
            write("What are the terms for " + c.getCourseName().toLowerCase() + "?");
            write("When is " + c.getCourseName().toLowerCase() + "?");
            write("What term is " + c.getCourseCode().toLowerCase() + " in?");
            write("What terms does " + c.getCourseCode().toLowerCase() + " take place in?");
            write("What are the terms for " + c.getCourseCode().toLowerCase() + "?");
            write("When is " + c.getCourseCode().toLowerCase() + "?");
            write("-R");
            write(c.getCourseCode() + " is in the " + c.getTerm() + " term.");
            write(c.getCourseCode() + " runs during the " + c.getTerm() + " term.");
            write("");

            //Exam exists
            write(c.getCourseCode().toLowerCase().replaceAll("\\s","") + "exam");
            write("-P");
            write("Does " + c.getCourseName().toLowerCase() + " have an exam?");
            write("Is there an exam for " + c.getCourseName().toLowerCase() + "?");
            write("Does " + c.getCourseCode().toLowerCase() + " have an exam?");
            write("Is there an exam for " + c.getCourseCode().toLowerCase() + "?");
            write("-R");
            if(c.getHasExam()){ //Has exam
                write(c.getCourseCode() + " has an exam.");
                write("There is an exam for " + c.getCourseCode());
            } else { //No exam
                write(c.getCourseCode() + " does not have an exam.");
                write("There is not an exam for " + c.getCourseCode());
            }
            write("");

            //Offerings
            write(c.getCourseCode().toLowerCase().replaceAll("\\s","") + "offerings");
            write("-P");
            write("What offerings are there for " + c.getCourseName().toLowerCase() + "?");
            write("Does " + c.getCourseName().toLowerCase() + " have offerings?");
            write("What are the offerings for " + c.getCourseName().toLowerCase() + "?");
            write("Can I take " + c.getCourseName().toLowerCase() + "?");
            write("What offerings are there for " + c.getCourseCode().toLowerCase() + "?");
            write("Does " + c.getCourseCode().toLowerCase() + " have offerings?");
            write("What are the offerings for " + c.getCourseCode().toLowerCase() + "?");
            write("Can I take " + c.getCourseCode().toLowerCase() + "?");
            write("-R");
            write(c.getCourseCode() + " has the following offerings: " + c.getOfferings() + ".");
            write(c.getOfferings() + " are " + c.getCourseCode() + "'s offerings.");
            write("");

            //Course Description
            write(c.getCourseCode().toLowerCase().replaceAll("\\s","") + "description");
            write("-P");
            write("What is " + c.getCourseName().toLowerCase() + " about?");
            write("Tell me about " + c.getCourseName().toLowerCase() + "?");
            write("What is the course description for " + c.getCourseName().toLowerCase() + "?");
            write("What is " + c.getCourseCode().toLowerCase() + " about?");
            write("Tell me about " + c.getCourseCode().toLowerCase() + "?");
            write("What is the course description for " + c.getCourseCode().toLowerCase() + "?");
            write("-R");
            write(c.getCourseCode() + " is: " + c.getCourseDescription());
            write(c.getCourseDescription() + " is the description for " + c.getCourseCode());
            write("");

            //Instructor
            write(c.getCourseCode().toLowerCase().replaceAll("\\s","") + "instructor");
            write("-P");
            write("Who teaches " + c.getCourseName().toLowerCase() + "?");
            write("Who is the instructor for " + c.getCourseName().toLowerCase() + "?");
            write("Who is the professor for " + c.getCourseName().toLowerCase() + "?");
            write("Who is " + c.getCourseName().toLowerCase() + "'s professor?");
            write("Who is " + c.getCourseName().toLowerCase() + "'s instructor?");
            write("Who teaches " + c.getCourseCode().toLowerCase() + "?");
            write("Who is the instructor for " + c.getCourseCode().toLowerCase() + "?");
            write("Who is the professor for " + c.getCourseCode().toLowerCase() + "?");
            write("Who is " + c.getCourseCode().toLowerCase() + "'s professor?");
            write("Who is " + c.getCourseCode().toLowerCase() + "'s instructor?");
            write("-R");
            write(c.getInstructor() + " teaches " + c.getCourseCode() + ".");
            write(c.getInstructor() + " is the instructor of " + c.getCourseCode() + ".");
            write("");

            //Prerequisites
            write(c.getCourseCode().toLowerCase().replaceAll("\\s","") + "prerequisites");
            write("-P");
            write("What prerequisites are there for " + c.getCourseName().toLowerCase() + "?");
            write("Does " + c.getCourseName().toLowerCase() + " have prerequisites?");
            write("What are the prerequisites for " + c.getCourseName().toLowerCase() + "?");
            write("What do I need before I can take " + c.getCourseName().toLowerCase() + "?");
            write("What prerequisites are there for " + c.getCourseCode().toLowerCase() + "?");
            write("Does " + c.getCourseCode().toLowerCase() + " have prerequisites?");
            write("What are the prerequisites for " + c.getCourseCode().toLowerCase() + "?");
            write("What do I need before I can take " + c.getCourseCode().toLowerCase() + "?");
            write("-R");
            if(c.getPrerequisites() == null){
                write(c.getCourseCode() + " has no prerequisites.");
                write(c.getCourseCode() + " does not have prerequisites.");
            } else {
                write(c.getCourseCode() + " has the following prerequisites: " + c.getPrerequisites() + ".");
                write("The prerequisites for " + c.getCourseCode() + " are " + c.getPrerequisites() + ".");
            }
            write("");

            //Restrictions
            write(c.getCourseCode().toLowerCase().replaceAll("\\s","") + "restrictions");
            write("-P");
            write("What restrictions are there for " + c.getCourseName().toLowerCase() + "?");
            write("Does " + c.getCourseName().toLowerCase() + " have restrictions?");
            write("What are the restrictions for " + c.getCourseName().toLowerCase() + "?");
            write("Can I take " + c.getCourseName().toLowerCase() + "?");
            write("What restrictions are there for " + c.getCourseCode().toLowerCase() + "?");
            write("Does " + c.getCourseCode().toLowerCase() + " have restrictions?");
            write("What are the restrictions for " + c.getCourseCode().toLowerCase() + "?");
            write("Can I take " + c.getCourseCode().toLowerCase() + "?");
            write("-R");
            if(c.getRestrictions() == null){
                write(c.getCourseCode() + " has no restrictions.");
                write(c.getCourseCode() + " does not have restrictions.");
            } else {
                write(c.getCourseCode() + " has the following restrictions: " + c.getRestrictions() + ".");
                write("The restrictions for " + c.getCourseCode() + " are " + c.getRestrictions() + ".");
            }
            write("");

            //Lab exists
            write(c.getCourseCode().toLowerCase().replaceAll("\\s","") + "haslab");
            write("-P");
            write("Does " + c.getCourseName().toLowerCase() + " have a lab?");
            write("Is there a lab for " + c.getCourseName().toLowerCase() + "?");
            write("Does " + c.getCourseCode().toLowerCase() + " have a lab?");
            write("Is there a lab for " + c.getCourseCode().toLowerCase() + "?");
            write("-R");
            if(c.getHasLab()){ //Has lab
                write(c.getCourseCode() + " has a lab.");
                write("There is a lab for " + c.getCourseCode());
            } else { //No lab
                write(c.getCourseCode() + " does not have a lab.");
                write("There is not a lab for " + c.getCourseCode());
            }
            write("");

            //Lab hours
            write(c.getCourseCode().toLowerCase().replaceAll("\\s","") + "labtime");
            write("-P");
            write("How many lab hours are there for " + c.getCourseName().toLowerCase() + "?");
            write("How many lab hours does " + c.getCourseName().toLowerCase() + " have?");
            write("What are the lab hours for " + c.getCourseName().toLowerCase() + "?");
            write("How long are labs in " + c.getCourseName().toLowerCase() + "?");
            write(c.getCourseName().toLowerCase() + " lab length?");
            write("How many lab hours are there for " + c.getCourseCode().toLowerCase() + "?");
            write("How many lab hours does " + c.getCourseCode().toLowerCase() + " have?");
            write("What are the lab hours for " + c.getCourseCode().toLowerCase() + "?");
            write("How long are labs in " + c.getCourseCode().toLowerCase() + "?");
            write(c.getCourseCode().toLowerCase() + " lab length?");
            write("-R");
            if(c.getHasLab()){ //Has lab
                write(c.getCourseCode() + " has " + c.getLabHours() + " lab hours.");
                write("There are " + c.getLabHours() + " lab hours in " + c.getCourseCode() + ".");
            } else { //No lab
                write(c.getCourseCode() + " does not have a lab.");
                write("There is not a lab for " + c.getCourseCode());
            }
            write("");

            //Tutorial exists
            write(c.getCourseCode().toLowerCase().replaceAll("\\s","") + "hastutorial");
            write("-P");
            write("Does " + c.getCourseName().toLowerCase() + " have a tutorial?");
            write("Is there a tutorial for " + c.getCourseName().toLowerCase() + "?");
            write("Does " + c.getCourseCode().toLowerCase() + " have a tutorial?");
            write("Is there a tutorial for " + c.getCourseCode().toLowerCase() + "?");
            write("-R");
            if(c.getHasTutorial()){ //Has tutorial
                write(c.getCourseCode() + " has a tutorial.");
                write("There is a tutorial for " + c.getCourseCode());
            } else { //No tutorial
                write(c.getCourseCode() + " does not have a tutorial.");
                write("There is not a tutorial for " + c.getCourseCode());
            }
            write("");

            //Tutorial hours
            write(c.getCourseCode().toLowerCase().replaceAll("\\s","") + "tutorialtime");
            write("-P");
            write("How many tutorial hours are there for " + c.getCourseName().toLowerCase() + "?");
            write("How many tutorial hours does " + c.getCourseName().toLowerCase() + " have?");
            write("What are the tutorial hours for " + c.getCourseName().toLowerCase() + "?");
            write("How long are tutorials in " + c.getCourseName().toLowerCase() + "?");
            write(c.getCourseName().toLowerCase() + " tutorial length?");
            write("How many tutorial hours are there for " + c.getCourseCode().toLowerCase() + "?");
            write("How many tutorial hours does " + c.getCourseCode().toLowerCase() + " have?");
            write("What are the tutorial hours for " + c.getCourseCode().toLowerCase() + "?");
            write("How long are tutorials in " + c.getCourseCode().toLowerCase() + "?");
            write(c.getCourseCode().toLowerCase() + " tutorial length?");
            write("-R");
            if(c.getHasTutorial()){ //Has tutorial
                write(c.getCourseCode() + " has " + c.getTutorialHours() + " tutorial hours.");
                write("There are " + c.getTutorialHours() + " tutorial hours in " + c.getCourseCode() + ".");
            } else {//No tutorial
                write(c.getCourseCode() + " does not have a tutorial.");
                write("There is not a tutorial for " + c.getCourseCode());
            }
            write("");

            //Lecture hours
            write(c.getCourseCode().toLowerCase().replaceAll("\\s","") + "lecturetime");
            write("-P");
            write("How many lecture hours are there for " + c.getCourseName().toLowerCase() + "?");
            write("How many lecture hours does " + c.getCourseName().toLowerCase() + " have?");
            write("What are the lecture hours for " + c.getCourseName().toLowerCase() + "?");
            write("How long are lectures in " + c.getCourseName().toLowerCase() + "?");
            write(c.getCourseName().toLowerCase() + " lecture length?");
            write("How many lecture hours are there for " + c.getCourseCode().toLowerCase() + "?");
            write("How many lecture hours does " + c.getCourseCode().toLowerCase() + " have?");
            write("What are the lecture hours for " + c.getCourseCode().toLowerCase() + "?");
            write("How long are lectures in " + c.getCourseCode().toLowerCase() + "?");
            write(c.getCourseCode().toLowerCase() + " lecture length?");
            write("-R");
            write(c.getCourseCode() + " has " + c.getLectureHours() + " lecture hours.");
            write("There are " + c.getLectureHours() + " lecture hours in " + c.getCourseCode() + ".");
            write("");

            //Seminar exists
            write(c.getCourseCode().toLowerCase().replaceAll("\\s","") + "hasseminar");
            write("-P");
            write("Does " + c.getCourseName().toLowerCase() + " have a seminar?");
            write("Is there a seminar for " + c.getCourseName().toLowerCase() + "?");
            write("Does " + c.getCourseCode().toLowerCase() + " have a seminar?");
            write("Is there a seminar for " + c.getCourseCode().toLowerCase() + "?");
            write("-R");
            if(c.getHasSeminar()){ //Has seminar
                write(c.getCourseCode() + " has a seminar.");
                write("There is a seminar for " + c.getCourseCode());
            } else { //No seminar
                write(c.getCourseCode() + " does not have a seminar.");
                write("There is not a seminar for " + c.getCourseCode());
            }
            write("");

            //Seminar hours
            write(c.getCourseCode().toLowerCase().replaceAll("\\s","") + "seminartime");
            write("-P");
            write("How many seminar hours are there for " + c.getCourseName().toLowerCase() + "?");
            write("How many seminar hours does " + c.getCourseName().toLowerCase() + " have?");
            write("What are the seminar hours for " + c.getCourseName().toLowerCase() + "?");
            write("How long are seminars in " + c.getCourseName().toLowerCase() + "?");
            write(c.getCourseName().toLowerCase() + " seminar length?");
            write("How many seminar hours are there for " + c.getCourseCode().toLowerCase() + "?");
            write("How many seminar hours does " + c.getCourseCode().toLowerCase() + " have?");
            write("What are the seminar hours for " + c.getCourseCode().toLowerCase() + "?");
            write("How long are seminars in " + c.getCourseCode().toLowerCase() + "?");
            write(c.getCourseCode().toLowerCase() + " seminar length?");
            write("-R");
            if(c.getHasSeminar()){ //Has seminar
                write(c.getCourseCode() + " has " + c.getSeminarHours() + " seminar hours.");
                write("There are " + c.getSeminarHours() + " seminar hours in " + c.getCourseCode() + ".");
            } else {//No seminar
                write(c.getCourseCode() + " does not have a seminar.");
                write("There is not a seminar for " + c.getCourseCode());
            }
            write("");
        }

    }

    private void createExamIntents() throws IOException{
        for (int i = 0; i < exams.size(); i++) {
            Exam e = (Exam) exams.get(i);

            write(e.getCourseCode().toLowerCase().replaceAll("\\s","") + "examdate");
            write("-P");
            write("What day is the " + e.getCourseCode().toLowerCase() + " exam?");
            write("When is the " + e.getCourseCode().toLowerCase() + " exam?");
            write("What time is the " + e.getCourseCode().toLowerCase() + " exam?");
            write("When should I go to the " + e.getCourseCode().toLowerCase() + " exam?");
            write("What is the date for the " + e.getCourseCode().toLowerCase() + " exam?");
            write("-R");
            write("The " + e.getCourseCode() + " exam is at " + e.getTime() + " on " + e.getDate() + ".");
            write("");

            write(e.getCourseCode().toLowerCase().replaceAll("\\s","") + "examlocation");
            write("-P");
            write("Where is the " + e.getCourseCode().toLowerCase() + " exam?");
            write("Where should I go for the " + e.getCourseCode().toLowerCase() + " exam?");
            write("What room is the " + e.getCourseCode().toLowerCase() + " exam in?");
            write("-R");
            write("Go to " + e.getLocation() + " for the " + e.getCourseCode() + " exam.");
            write("The " + e.getCourseCode() + " exam is in " + e.getLocation() + ".");
            write("");

            write(e.getCourseCode().toLowerCase().replaceAll("\\s","") + "examinperson");
            write("-P");
            write("Is the " + e.getCourseCode().toLowerCase() + " exam at Brock?");
            write("Is the " + e.getCourseCode().toLowerCase() + " exam in person?");
            write("What kind of exam is the " + e.getCourseCode().toLowerCase() + " exam?");
            write("-R");
            if(e.getIsInPerson()){
                write("The " + e.getCourseCode() + " exam is in person.");
            } else {
                write("The " + e.getCourseCode() + " exam is not in person.");
            }
            write("");
        }
    }

    private void createBrockEWebsiteIntents() throws IOException {

        write("websitelist");
        write("-P");
        write("What websites are there?");
        write("What are Brock's websites?");
        write("What links are there?");
        write("What are Brock's links?");
        write("-R");
        String list = toList(websites, 0);
        write("The websites at Brock are " + list + ".");
        write("Brock's websites include " + list + ".");
        write("");

        for (int i = 0; i < websites.size(); i++) {
            BrockEWebsite w = (BrockEWebsite) websites.get(i);

            write(w.getTopic().toLowerCase().replaceAll("\\s","") + "link");
            write("-P");
            write("What is the link for the " + w.getTopic().toLowerCase() + " web page?");
            write("Where can I find the " + w.getTopic().toLowerCase() + " website?");
            write("Tell me about " + w.getTopic().toLowerCase() + ".");
            write("What is " + w.getTopic().toLowerCase() + "?");
            write("-R");
            write("Visit " + w.getPageLink() + " for more information on the " + w.getTopic() + ".");
            write("The " + w.getTopic() + " website can be found at " + w.getPageLink() + ".");
            write("");
        }
    }

    private void write(String l) throws IOException {
        dbOut.write(l);
        dbOut.newLine();
    }

    private String toList(ArrayList<TableRep> list, int attrNum){
        String s = "";
        if(list.size() <= 0) return "um... I guess there are none";
        for (int i = 0; i < list.size(); i++) {
            if(i >= list.size() - 1 && list.size() > 1){
                s = s + "and ";
            }

            s = s + list.get(i).getValue(attrNum).toString();

            if(i < list.size() - 1){
                s = s + ", ";
            }
        }
        return s;
    }

    public static void main(String[] args) {
        new Main();
    }
}
