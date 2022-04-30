package ToJSON;

/**
 * This class prints the given input out to a file.
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class IntentsFile {

    private FileWriter writer;
    private boolean isClosed;

    IntentsFile(String name){
        File newFile = new File("Intents/" + name + ".json");
        try {
            writer = new FileWriter(newFile);
            isClosed = false;
        } catch (IOException e) {
            System.out.println("The file could not be created...");
            isClosed = true;
        }
    }

    void writeToFile(String message){
        if(!isClosed){
            try {
                writer.write(message + "\r\n");
            } catch (IOException e) {
                System.out.println("The message: " + message + "could not be written...");
                close();
            }
        }
    }

    void writeWithCommaToFile(String message){
        if(!isClosed){
            try {
                writer.write(message + ",\r\n");
            } catch (IOException e) {
                System.out.println("The message: " + message + "could not be written...");
                close();
            }
        }
    }

    void close(){
        if(!isClosed) {
            try {
                writer.close();
            } catch (IOException e) {
                System.out.println("The printer closed with an error...");
            } finally {
                isClosed = true;
            }
        }
    }

}
