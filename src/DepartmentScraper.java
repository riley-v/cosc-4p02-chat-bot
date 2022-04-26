import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DepartmentScraper extends WebScraper{
    private String website;
    private int ID;
    private Thread thread;

    public DepartmentScraper(String link, int id) {
        super(link, id);
        website = link;
        ID = id;

//        thread = new Thread((this));
//        thread.start();
    }

    @Override
    public void run() {
        Document doc = request(website);
        populateDepartment(doc);
    }

    public void populateDepartment(Document doc){
        int id = 1;
        String departmentname = null;
        String contactinfo = null;
        String link = null;
        String email = null;
        String phone = null;
        String facebook = null;
        String instagram = null;
        String twitter = null;
        String linkedin = null;

        Statement statement = null;
        java.sql.Connection connection = null;

        //Database connection
        try{
            //enter username and password to db
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc-chatbot", user, pass);

            statement = connection.createStatement();

            //get data

            Elements vitals = doc.getElementsByClass("vitals");
            for(Element vital : vitals){
                departmentname = vital.getElementsByClass("name").text();
                link = vital.getElementsByClass("link").text();
                phone = vital.getElementsByClass("phone").text();
                email = vital.getElementsByClass("email").text();
                contactinfo = "email: " + email + " phone: " + phone;

                //check to see if phone and email are null

                String info = "VALUES " + "(" + '"'+departmentname+'"' + "," + '"'+contactinfo+'"' + "," + '"'+link+'"' + "," + id + ")";
                String query = "INSERT IGNORE INTO department " + info;
                //System.out.println(query);

                statement.executeUpdate(query);
                id++;
//                    System.out.print("id = " + id + "," + " ");
//                    System.out.print("name: " + departmentname + "," + " ");
//                    System.out.print("link: " + link + "," + " ");
//                    System.out.print("info: " + contactinfo + "," + " ");
//                    System.out.println();
//                    id++;
            }

            id = 1;
            Element directories = doc.getElementsByClass("directory-listing").first();
            Elements children = directories.children();
            for(Element child : children){
                facebook = child.getElementsByClass("facebook").select("a[href]").attr("href");
                twitter = child.getElementsByClass("twitter").select("a[href]").attr("href");
                instagram = child.getElementsByClass("instagram").select("a[href]").attr("href");
                linkedin = child.getElementsByClass("linkedin").select("a[href]").attr("href");

//                System.out.print("id = " + id + "," + " ");
//                System.out.print("facebook: " + facebook + "," + " ");
//                System.out.print("twitter: " + twitter + "," + " ");
//                System.out.print("instagram: " + instagram + "," + " ");
//                System.out.print("linkedIn: " + linkedin + "," + " ");
//                System.out.println();

                String socialinfo = ("facebook: " + facebook + "," + " " + "twitter: " + twitter + "," + " "+ "instagram: " + instagram + "," + " "+"linkedIn: " + linkedin + "," + " ");
                String socialquery = "UPDATE department SET " + "ContactInformation=concat(ContactInformation,"+ '"'+ " "+socialinfo+'"'+") where id="+id;
                //System.out.println(socialquery);
                statement.executeUpdate(socialquery);

                id++;
            }
            System.out.println("Department table updated");


            //Retrieving data from table
            //ResultSet resultSet = statement.executeQuery("select * from department");

//            while (resultSet.next()){
//                System.out.println(resultSet.getString("id") + " " + (resultSet.getString("DepartmentName")));
//            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                if(statement != null){
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
