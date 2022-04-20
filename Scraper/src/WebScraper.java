import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class WebScraper implements Runnable{
    private static final int MAX_DEPTH = 2;                             //number of websites to recursively visit
    private Thread thread;
    private String website;                                             //first link to be visited
    private ArrayList<String> visitedLinks = new ArrayList<String>();
    private int ID;
    private ArrayList<String> data = new ArrayList<String>();
    private ArrayList<String> info = new ArrayList<String>();

    //User information
    public int year = 2021;
    public String user = "root";           //username to database
    public String pass = "password";    //password to database
    public String browser = "webdriver.chrome.driver";     //preferred web browser
    //path to browser driver (obtained from selenium.dev)
    public String browserdriverpath = "C:\\Users\\dental\\IdeaProjects\\Web Scraper\\Lib\\chromedriver.exe";

    public WebScraper(String link, int id){
        System.out.print("Web Scraper created");
        website = link;
        ID = id;

        thread = new Thread((this));
        thread.start();
    }

    @Override
    public void run() {
        visitedLinks.add("javascript:ExportClick('NoExport')");
        scrape(1, website);
    }

    //Recursively scrapes links/data from webpage
    private void scrape(int depth, String url) {

            if (depth <= MAX_DEPTH) {
                Document doc = request(url);        //check connection

                if (doc != null) {
                    for (Element link : doc.select("a[href]")) {     //look for links on webpage
                        String next_link = link.absUrl("href");   //remove href tag from url
                        if(next_link.equals("javascript:ExportClick('NoExport')")){
                            break;
                        }
                        if (visitedLinks.contains(next_link) == false) {     //checks to see if link has been visited
                            //System.out.println(doc.text());
                            //data.add(doc.text());
                            scrape(depth++, next_link);
                        }
                    }

//                    for (Element sublink : doc.select("a[href]")){     //look for links on webpage
//                        String sublinks = sublink.absUrl("href");   //remove href tag from url
//                        //System.out.println("sublink: " + sublink);
//                        if(visitedLinks.contains(sublinks) == false || sublinks == "https://microsoft.com/edge")      //checks to see if link has been visited
//                            scrape(depth++, sublinks);
//                    }
                }

            }

    }

    //Helper function to check if connection has been established
    public Document request(String url){

        Statement statement = null;
        java.sql.Connection con = null;

        try{
            if(url.equals("javascript:ExportClick('NoExport')")){
                System.out.println("invalid");
                return null;
            }
            //System.out.println("url: " + url);
            Connection connection = Jsoup.connect(url);
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc-chatbot", user, pass);
            statement = con.createStatement();
            Document doc = connection.get();

            //If connection is valid
            if (connection.response().statusCode() == 200){
                System.out.println("\nBot ID:" + ID + " Received Webpage at " + url);

                String title = doc.title();         //retrieve title of webpage

                //update brock table in database
                String query = "INSERT IGNORE INTO brock(Topic, PageLink) " +
                        "VALUES " + "(" + '"'+title+'"' + "," + '"'+url+'"' + ")";
                //System.out.println(query);
                //statement.executeUpdate(query);

                //System.out.println(title);
                //info.add(doc.text());               //adds text of website to arraylist
                //System.out.println(doc.text());

                visitedLinks.add(url);              //add link to visited arraylist
                return doc;
            }
        }
        catch (IOException e){
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //If connection is not valid return null
        return null;
    }

    public Thread getThread(){
        return thread;
    }


}
