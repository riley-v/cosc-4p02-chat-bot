import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class WebScraperTest {

    @Test
    public void brockWebsiteConnectionShouldReturnTrue() throws IOException {
        var scraper = new WebScraper("https://brocku.ca/" , 1);
        assertTrue(scraper.checkConnection("https://brocku.ca/") == true);
    }

    @Test
    public void departmentWebsiteConnectionShouldReturnTrue() throws IOException {
        var scraper = new WebScraper("https://brocku.ca/directory/a-z/" , 2);
        assertTrue(scraper.checkConnection("https://brocku.ca/directory/a-z/") == true);
    }

    @Test
    public void courseWebsiteConnectionShouldReturnTrue() throws IOException {
        var scraper = new WebScraper("https://brocku.ca/webcal/courses.php?prefix=A" , 3);
        assertTrue(scraper.checkConnection("https://brocku.ca/webcal/courses.php?prefix=A") == true);
    }

    @Test
    public void examWebsiteConnectionShouldReturnTrue() throws IOException {
        var scraper = new WebScraper("https://brocku.ca/guides-and-timetables/timetables/?session=fw&type=ex&level=all" , 4);
        assertTrue(scraper.checkConnection("https://brocku.ca/guides-and-timetables/timetables/?session=fw&type=ex&level=all") == true);
    }

    @Test
    public void programWebsiteConnectionShouldReturnTrue() throws IOException {
        var scraper = new WebScraper("https://brocku.ca/programs/" , 5);
        assertTrue(scraper.checkConnection("https://brocku.ca/programs/") == true);
    }
}