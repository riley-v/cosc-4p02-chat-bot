public class Course implements TableRep{

    private String courseName;
    private String courseCode;
    private String programName;
    private String term;
    private boolean hasExam;
    private String offerings;
    private String courseDescription;
    private String instructor;
    private String prerequisites;
    private String restrictions;
    private boolean hasLab;
    private String labOptions;
    private String lectureTimes;
    private String labHours;
    private boolean hasTutorial;
    private String tutorialHours;
    private String lectureHours;
    private boolean hasSeminar;
    private String seminarHours;

    Course(String cName, String cCode, String pName, String courseTerm, boolean examExists, String courseOfferings, String cDescription, String courseInstructor,
           String coursePrerequisites, String courseRestrictions, boolean labExists, String cLabOptions, String cLectureTimes, String cLabHours, boolean tutorialExists,
           String cTutorialHours, String cLectureHours, boolean seminarExists, String cSeminarHours){
        courseName = pName + " " + cName;
        courseCode = pName + " " + cCode;
        programName = pName;
        term = courseTerm;
        hasExam = examExists;
        offerings = courseOfferings;
        courseDescription = cDescription;
        instructor = courseInstructor;
        prerequisites = coursePrerequisites;
        restrictions = courseRestrictions;
        hasLab = labExists;
        labOptions = cLabOptions;
        lectureTimes = cLectureTimes;
        labHours = cLabHours; 
        hasTutorial = tutorialExists;
        tutorialHours = cTutorialHours;
        lectureHours = cLectureHours;
        hasSeminar = seminarExists;
        seminarHours = cSeminarHours;
    }

    @Override
    public Object getValue(int attrNum){
        switch(attrNum){
            case 0:
                return getCourseName();
            case 1:
                return getCourseCode();
            case 2:
                return getProgramName();
            case 3:
                return getTerm();
            case 4:
                return getHasExam();
            case 5:
                return getOfferings();
            case 6:
                return getCourseDescription();
            case 7:
                return getInstructor();
            case 8:
                return getPrerequisites();
            case 9:
                return getRestrictions();
            case 10:
                return getHasLab();
            case 11:
                return getLabOptions();
            case 12:
                return getLectureTimes();
            case 13:
                return getLabHours();
            case 14:
                return getHasTutorial();
            case 15:
                return getTutorialHours();
            case 16:
                return getLectureHours();
            case 17:
                return getHasSeminar();
            case 18:
                return getSeminarHours();
            default:
                return "No such value";
        }

    }

    String getCourseName(){
        return courseName;
    }

    String getCourseCode(){
        return courseCode;
    }

    String getProgramName(){
        return programName;
    }

    String getTerm(){
        return term;
    }

    boolean getHasExam(){
        return hasExam;
    }

    String getOfferings(){
        return offerings;
    }

    String getCourseDescription(){
        return courseDescription;
    }

    String getInstructor(){
        return instructor;
    }

    String getPrerequisites(){
        return prerequisites;
    }

    String getRestrictions(){
        return restrictions;
    }

    boolean getHasLab(){
        return hasLab;
    }

    String getLabOptions(){
        return labOptions;
    }
    
    String getLectureTimes(){
        return lectureTimes;
    }

    String getLabHours(){
        return labHours;
    }

    boolean getHasTutorial(){
        return hasTutorial;
    }

    String getTutorialHours(){
        return tutorialHours;
    }

    String getLectureHours(){
        return lectureHours;
    }

    boolean getHasSeminar(){
        return hasSeminar;
    }

    String getSeminarHours(){
        return seminarHours;
    }

}
