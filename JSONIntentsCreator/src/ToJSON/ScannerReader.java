/**
package ToJSON;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class ScannerReader {

    private String name;
    private BufferedReader in;
    private boolean isClosed;
    private boolean isReady;

    private String nextTag;
    private ArrayList<String> nextPatterns;
    private ArrayList<String> nextAnswers;

    private int num;

    ScannerReader(File f) throws FileNotFoundException {
        name = f.getName();

        in = new BufferedReader(new FileReader(f));
        //in.useDelimiter(";|\r?\n|\r");

        isClosed = false;
        isReady = false;
        num = 0;
    }

    void readNext() throws IncorrectFormatException{

        if(!isClosed && in.hasNext()){
            isReady = false;

            num++;
            String tag = in.readLine();

            String check ="";
            if(in.hasNext()){
                num++;
                check = in.next();
            }
            if(!check.equals("-P")) throw new IncorrectFormatException("No -P found in line " + num + ", " + check + " found instead");

            ArrayList<String> patterns = new ArrayList<>();
            while(in.hasNext() && !check.equals("-R")) {
                num++;
                check = in.next();
                if(!check.equals("-R")) patterns.add(check);
            }

            if(!check.equals("-R")) throw new IncorrectFormatException("No -R found in line " + num + ", " + check + " found instead, " + in.next() + " found next");

            ArrayList<String> answers = new ArrayList<>();
            while(in.hasNext() && check.trim().length() > 0) {
                num++;
                check = in.next();
                if(check.trim().length() > 0) answers.add(check);
            }

            nextTag = tag;
            nextPatterns = patterns;
            nextAnswers = answers;
            isReady = true;
        }
    }

    boolean hasNext(){
        if(!isClosed) {
            return in.hasNext();
        }else{
            return false;
        }
    }

    public boolean isReady() {
        return isReady;
    }

    void close(){
        if(!isClosed) {
            in.close();
            isClosed = true;
            isReady = false;
        }
    }

    String getNextTag(){
        return nextTag;
    }

    ArrayList<String> getNextPatterns(){
        return nextPatterns;
    }

    ArrayList<String> getNextAnswers(){
        return nextAnswers;
    }

}
 **/
