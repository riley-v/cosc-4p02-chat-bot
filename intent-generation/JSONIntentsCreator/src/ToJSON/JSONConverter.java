package ToJSON;

/**
 * This class does the actual JSON formatting based on the given input
 */

import java.util.ArrayList;

public class JSONConverter {

    static ArrayList<String> toJSON(String tag, ArrayList<String> patterns, ArrayList<String> answers){
        ArrayList<String> json = new ArrayList<>();

        json.add("\t\t{");
        json.add("\t\t\t\"tag\": \"" + tag + "\",");

        json.add("\t\t\t\"patterns\": [");
        for (int i = 0; i < patterns.size(); i++) {
            if(i < patterns.size() - 1){
                json.add("\t\t\t\t\"" + patterns.get(i) + "\",");
            }else{
                json.add("\t\t\t\t\"" + patterns.get(i) + "\"");
            }
        }
        json.add("\t\t\t],");

        json.add("\t\t\t\"responses\": [");
        for (int i = 0; i < answers.size(); i++) {
            if(i < answers.size() - 1){
                json.add("\t\t\t\t\"" + answers.get(i) + "\",");
            }else{
                json.add("\t\t\t\t\"" + answers.get(i) + "\"");
            }
        }
        json.add("\t\t\t]");
        json.add("\t\t}");

        return json;
    }

    static ArrayList<String> getJSONPatterns(String tag, ArrayList<String> patterns){
        ArrayList<String> json = new ArrayList<>();

        json.add("\t\t{");
        json.add("\t\t\t\"tag\": \"" + tag + "\",");

        json.add("\t\t\t\"patterns\": [");
        for (int i = 0; i < patterns.size(); i++) {
            if(i < patterns.size() - 1){
                json.add("\t\t\t\t\"" + patterns.get(i) + "\",");
            }else{
                json.add("\t\t\t\t\"" + patterns.get(i) + "\"");
            }
        }
        json.add("\t\t\t]");
        json.add("\t\t}");

        return json;
    }

    static ArrayList<String> getJSONResponses(String tag, ArrayList<String> responses){
        ArrayList<String> json = new ArrayList<>();

        json.add("\t\t{");
        json.add("\t\t\t\"tag\": \"" + tag + "\",");

        json.add("\t\t\t\"responses\": [");
        for (int i = 0; i < responses.size(); i++) {
            if(i < responses.size() - 1){
                json.add("\t\t\t\t\"" + responses.get(i) + "\",");
            }else{
                json.add("\t\t\t\t\"" + responses.get(i) + "\"");
            }
        }
        json.add("\t\t\t]");
        json.add("\t\t}");

        return json;
    }

    static ArrayList<String> getJSONStart(){
        ArrayList<String> json = new ArrayList<>();

        json.add("{");
        json.add("\t\"intents\": [");

        return json;
    }

    static ArrayList<String> getJSONEnd(){
        ArrayList<String> json = new ArrayList<>();

        json.add("\t]");
        json.add("}");

        return json;
    }

}
