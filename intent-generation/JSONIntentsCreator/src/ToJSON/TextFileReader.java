package ToJSON;

/**
 * This class parses a ".txt" file in the format of BasicIntents.txt to find the tags, patterns, and responses
 */

import java.io.*;
import java.util.ArrayList;

public class TextFileReader {

    private String name;
    private BufferedReader in;
    private boolean isClosed;
    private boolean hasNext;

    private String nextTag;
    private ArrayList<String> nextPatterns;
    private ArrayList<String> nextAnswers;

    private int num;

    TextFileReader(File f) throws FileNotFoundException {
        name = f.getName();

        in = new BufferedReader(new FileReader(f));
        //in.useDelimiter(";|\r?\n|\r");

        isClosed = false;
        hasNext = true;
        num = 0;
    }

    void readNext() throws IncorrectFormatException{

        if(hasNext){
            try {

                num++;
                String tag = in.readLine();
                if(tag==null){
                    hasNext = false;
                    return;
                }

                String check = "";
                num++;
                check = in.readLine();
                if (check==null || !check.equals("-P"))
                    throw new IncorrectFormatException("No -P found in line " + num + ", " + check + " found instead");

                ArrayList<String> patterns = new ArrayList<>();
                num++;
                check = in.readLine();
                while (check != null && !check.equals("-R")) {
                    patterns.add(check);
                    num++;
                    check = in.readLine();
                }

                if (!check.equals("-R"))
                    throw new IncorrectFormatException("No -R found in line " + num + ", " + check + " found instead");

                ArrayList<String> answers = new ArrayList<>();
                num++;
                check = in.readLine();
                while (check != null && check.trim().length() > 0) {
                    answers.add(check);
                    num++;
                    check = in.readLine();
                }

                nextTag = tag;
                nextPatterns = patterns;
                nextAnswers = answers;

                if(check==null) hasNext = false;

            }catch(IOException e){
                hasNext = false;
            }
        }
    }

    boolean hasNext(){
        return hasNext;
    }

    void close(){
        if(!isClosed) {
            isClosed = true;
            hasNext = false;
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
