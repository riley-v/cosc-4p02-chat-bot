public class Exam implements TableRep {

    private String courseCode;
    private String date;
    private String time;
    private String location;
    private boolean isInPerson;

    Exam(String cc, String d, String t, String l, boolean iip, String sn) {
        courseCode = cc + " Section " + sn;
        date = d;
        time = t;
        location = l;
        isInPerson = iip;
    }

    @Override
    public Object getValue(int attrNum) {
        switch (attrNum) {
            case 0:
                return getCourseCode();
            case 1:
                return getDate();
            case 2:
                return getTime();
            case 3:
                return getLocation();
            case 4:
                return getIsInPerson();
            default:
                return "No such value";
        }

    }

    String getCourseCode(){
        return courseCode;
    }

    String getDate(){
        return date;
    }

    String getTime(){
        return time;
    }

    String getLocation(){
        return location;
    }

    boolean getIsInPerson(){
        return isInPerson;
    }
}
