import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class AddDemeritPointsTest {

    //Reset files before each test
    @BeforeEach
    public void clearFiles() throws IOException {
        new FileWriter("persons.txt", false).close();           // clear person data
        new FileWriter("demerit_points.txt", false).close();    // clear demerits
    }

    //Test Case 1: Add valid demerit points for a person
    @Test
    public void testValidDemeritAddition_shouldSucceed() {
        Person p = new Person("23$#ab!cDE", "Lewis", "Hamilton",
                "55|Main Rd|Melbourne|Victoria|Australia", "10-10-2000");
        p.addPerson();
        String result = p.addDemeritPoints("23$#ab!cDE", "12-06-2024", 3);
        assertEquals("Success", result);
    }

    //Test Case 2: Add invalid demerit points (out of range 1–6)
    @Test
    public void testInvalidDemeritPointsOutOfRange_shouldFail() {
        Person p = new Person("45@!xy*zFG", "Daniel", "Ricciardo",
                "21|Highland St|Melbourne|Victoria|Australia", "15-05-1995");
        p.addPerson();
        String result = p.addDemeritPoints("45@!xy*zFG", "12-06-2024", 8);
        assertEquals("Failed", result);
    }

    //Test Case 3: Add points with invalid date format
    @Test
    public void testInvalidDateFormat_shouldFail() {
        Person p = new Person("67$&mn!oHI", "Oscar", "Piastri",
                "42|Queen St|Melbourne|Victoria|Australia", "20-04-2002");
        p.addPerson();
        String result = p.addDemeritPoints("67$&mn!oHI", "2024/06/12", 3);
        assertEquals("Failed", result);
    }

    //Test Case 4: Trigger suspension for under 21 if total points within 2 years exceed 6
    @Test
    public void testUnder21Suspension_shouldTrigger() {
        Person p = new Person("89!$pq@rJK", "Lando", "Norris",
                "12|King St|Melbourne|Victoria|Australia", "01-01-2008");
        p.addPerson();
        p.addDemeritPoints("89!$pq@rJK", "01-01-2023", 3);
        p.addDemeritPoints("89!$pq@rJK", "01-06-2023", 4); // total 7 > 6
        assertTrue(p.isSuspended("89!$pq@rJK"));
    }

    //Test Case 5: Trigger suspension for over 21 if total points within 2 years exceed 12
    @Test
    public void testOver21Suspension_shouldTrigger() {
        Person p = new Person("34@!st%uLM", "Carlos", "Sainz",
                "98|Station Rd|Melbourne|Victoria|Australia", "01-01-1990");
        p.addPerson();
        p.addDemeritPoints("34@!st%uLM", "01-01-2022", 6);
        p.addDemeritPoints("34@!st%uLM", "15-03-2022", 4);
        p.addDemeritPoints("34@!st%uLM", "20-12-2023", 4); // total 14 > 12
        assertTrue(p.isSuspended("34@!st%uLM"));
    }
}