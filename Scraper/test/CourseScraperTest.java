import org.junit.Test;

import static org.junit.Assert.*;

public class CourseScraperTest {

    @Test
    public void getPageSourceForWinterTermShouldNotReturnNull() {
        var scraper = new CourseScraper("https://brocku.ca/guides-and-timetables/timetables/?session=fw&type=ug&level=all" , 1);
        assertNotNull(scraper.getPageSource("https://brocku.ca/guides-and-timetables/timetables/?session=fw&type=ug&level=all"));
    }

    @Test
    public void getPageSourceForSpringTermShouldNotReturnNull() {
        var scraper = new CourseScraper("https://brocku.ca/guides-and-timetables/timetables/?session=sp&type=ug&level=all" , 1);
        assertNotNull(scraper.getPageSource("https://brocku.ca/guides-and-timetables/timetables/?session=sp&type=ug&level=all"));
    }

    @Test
    public void getPageSourceForSummerTermShouldNotReturnNull() {
        var scraper = new CourseScraper("https://brocku.ca/guides-and-timetables/timetables/?session=su&type=ug&level=all" , 1);
        assertNotNull(scraper.getPageSource("https://brocku.ca/guides-and-timetables/timetables/?session=su&type=ug&level=all"));
    }

    @Test
    public void getPageSourceForWinterExamTermShouldNotReturnNull() {
        var scraper = new CourseScraper("https://brocku.ca/guides-and-timetables/timetables/?session=fw&type=ex&level=all" , 1);
        assertNotNull(scraper.getPageSource("https://brocku.ca/guides-and-timetables/timetables/?session=fw&type=ex&level=all"));
    }

    @Test
    public void getPageSourceForJuneExamTermShouldNotReturnNull() {
        var scraper = new CourseScraper("https://brocku.ca/guides-and-timetables/timetables/?session=spjun&type=ex&level=all" , 1);
        assertNotNull(scraper.getPageSource("https://brocku.ca/guides-and-timetables/timetables/?session=spjun&type=ex&level=all"));
    }

    @Test
    public void getPageSourceForJulyExamTermShouldNotReturnNull() {
        var scraper = new CourseScraper("https://brocku.ca/guides-and-timetables/timetables/?session=spjul&type=ex&level=all" , 1);
        assertNotNull(scraper.getPageSource("https://brocku.ca/guides-and-timetables/timetables/?session=spjul&type=ex&level=all"));
    }

    @Test
    public void getPageSourceForAugustExamTermShouldNotReturnNull() {
        var scraper = new CourseScraper("ttps://brocku.ca/guides-and-timetables/timetables/?session=spaug&type=ex&level=all" , 1);
        assertNotNull(scraper.getPageSource("ttps://brocku.ca/guides-and-timetables/timetables/?session=spaug&type=ex&level=all"));
    }

    @Test
    public void getPageSourceForSummerAugustExamTermShouldNotReturnNull() {
        var scraper = new CourseScraper("https://brocku.ca/guides-and-timetables/timetables/?session=su&type=ex&level=all" , 1);
        assertNotNull(scraper.getPageSource("https://brocku.ca/guides-and-timetables/timetables/?session=su&type=ex&level=all"));
    }
}