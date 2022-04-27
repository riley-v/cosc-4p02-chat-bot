package ToJSON;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class FileCreator {

    private IntentsFile patterns;
    private IntentsFile responses;

    public FileCreator(String[] files){
        patterns = new IntentsFile("patterns");
        responses = new IntentsFile("responses");

        JSONConverter.getJSONStart().forEach(s -> patterns.writeToFile(s));
        JSONConverter.getJSONStart().forEach(s -> responses.writeToFile(s));

        for (int i = 0; i < files.length; i++) {

            TextFileReader file;
            try {
                file = new TextFileReader(new File(files[i]));
            } catch (FileNotFoundException e) {
                file = null;
                System.out.println(files[i] + " was not found...");
            }

            boolean stop = (file == null);
            String pLast = "";
            String rLast = "";
            while (!stop) {
                try {
                    file.readNext();
                } catch (IncorrectFormatException e) {
                    System.out.println(files[i] + " is formatted incorrectly: " + e.getMessage());
                    stop = true;
                }

                if (file.hasNext()) {
                    if(!pLast.equals("")) patterns.writeWithCommaToFile(pLast);
                    if(!rLast.equals("")) responses.writeWithCommaToFile(rLast);

                    ArrayList<String> json = JSONConverter.getJSONPatterns(file.getNextTag(), file.getNextPatterns());
                    for (int j = 0; j < json.size() - 1; j++) {
                        patterns.writeToFile(json.get(j));
                    }
                    pLast = json.get(json.size() - 1);

                    json = JSONConverter.getJSONResponses(file.getNextTag(), file.getNextAnswers());
                    for (int j = 0; j < json.size() - 1; j++) {
                        responses.writeToFile(json.get(j));
                    }
                    rLast = json.get(json.size() - 1);

                }else{
                    if(i >= files.length - 1){
                        patterns.writeToFile(pLast);
                        responses.writeToFile(rLast);
                    }else{
                        patterns.writeWithCommaToFile(pLast);
                        responses.writeWithCommaToFile(rLast);
                    }
                    stop = true;
                }
            }
            file.close();

        }

        JSONConverter.getJSONEnd().forEach(s -> patterns.writeToFile(s));
        JSONConverter.getJSONEnd().forEach(s -> responses.writeToFile(s));

        patterns.close();
        responses.close();
    }

    public static void main(String[] args) {
        new FileCreator(args);
    }
}
