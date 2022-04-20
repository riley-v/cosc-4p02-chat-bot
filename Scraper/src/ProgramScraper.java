import org.jsoup.nodes.Document;
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

public class ProgramScraper extends WebScraper{
    private String website;
    private int ID;
    private Thread thread;

    public ProgramScraper(String link, int id) {
        super(link, id);
        website = link;
        ID = id;
    }

    @Override
    public void run() {
        Document doc = request(website);
        if(doc != null){
            //undergraduate programs
            populatePrograms(doc,0);

            //graduate programs
            doc = request("https://brocku.ca/programs/graduate/");
            populatePrograms(doc,1);
        }
    }

    private void populatePrograms(Document doc,int grad) {
        ArrayList<String> programs = new ArrayList<String>();
        ArrayList<String>links = new ArrayList<String>();
        String departmentname = null;
        String programname = null;
        String description = null;
        String requirements = null;
        String progwebsite = null;

        Statement statement = null;
        Connection connection = null;
        int graduate = grad;

        //Database connection
        try{
            //enter username and password to db
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc-chatbot", user, pass);
            statement = connection.createStatement();

            //Retrieve list of all available programs offered and store in arraylist
            Elements prog = doc.getElementsByClass("readmore");
            for(Element p : prog){
                programs.add(p.text());
                Elements website = p.select("a[href]");
                for(Element w : website){
                    //System.out.println(w.absUrl("href"));
                    links.add(w.absUrl("href"));
                }

            }

            for(int i = 0; i < programs.size(); i++){
                programname = programs.get(i);
                //System.out.println("name: "+programname);

                doc = request(links.get(i));
                Elements ele = doc.getAllElements();
                for(Element e : ele){
                    if(e.className().equals("specs")){
                        departmentname = e.child(0).child(0).text();
                        //System.out.println("dep: "+departmentname);
                    }
                    if(e.className().equals("entry-content")){
                        description = e.text();
                        //System.out.println("desc: "+e.text());
                    }

                    if(e.className().equals("col mid")){
                        requirements = e.text();
                        //System.out.println("req: "+e.text());
                    }
                    if(graduate == 1){
                        if(e.className().equals("col")){
                            requirements = e.text();
                            //System.out.println("req: "+e.text());
                        }
                    }

                    if(e.className().equals("button clear programsite fullwidth")){
                        Elements link = e.select("a[href]");
                        for(Element l : link){
                            progwebsite = l.absUrl("href");
                        }
                        //System.out.println("Website: "+progwebsite);
                    }


                }
                //add information to database
                String query = "INSERT IGNORE INTO programs(ProgramName, Requirements, Description, ProgramPageLink, DepartmentName) " +
                        "VALUES " + "(" + '"'+programname+'"' + "," + '"'+requirements+'"' + "," + '"'+description+'"' + "," + '"'+progwebsite+'"' + "," + '"'+departmentname+'"' + ")";

                System.out.println(query);
                statement.executeUpdate(query);
            }


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
}
