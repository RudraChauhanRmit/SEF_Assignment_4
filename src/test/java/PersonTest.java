import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

//Unit tests for the Person class with JUnit
public class PersonTest {

    //Test cases for 'addPerson' function

    //Test case 1 - All data matched the required 3 conditions
    //person should be successfully added to TXT file
    @Test
    public void testValidPerson_shouldAdd() {
        Person p = new Person("23s#@x!zAB", "Vathavooran", "Athiyaman", "45|King St|Burwood|Victoria|Australia", "12-12-1995");
        assertTrue(p.addPerson());
    }

    //Test case 2 - Invalid personID, doesn't meet the constraints - fails Condition 1
    //person should not be added to the TXT file
    @Test
    public void testInvalidID_shouldFail() {
        Person p = new Person("12abcAB", "Luket", "Achumi", "32|Highland St|Springvale|Victoria|Australia", "11-11-1992");
        assertFalse(p.addPerson());
    }

    //Test case 3 - Invalid address - state is not Victoria - fails Condition 2
    //person should not be added to the TXT file
    @Test
    public void testInvalidAddress_shouldFail() {
        Person p = new Person("24$%df!kGH", "Hiran", "Karunathilaka", "12|Main St|Sydney|NSW|Australia", "01-01-1990");
        assertFalse(p.addPerson());
    }

    //Test case 4 - Invalid birthdate - format doesn't match DD-MM-YYYY - fails Condition 3
    //person should not be added to the TXT file
    @Test
    public void testInvalidBirthdate_shouldFail() {
        Person p = new Person("25^@de!qJK", "RMIT", "Uni", "89|Bridge Rd|Melbourne|Victoria|Australia", "2020/01/01");
        assertFalse(p.addPerson());
    }

    //Test case 5 - Duplicate personID - entry valid if it's not in TXT, not valid if already exists in TXT file
    //The second entry below should not be added
    @Test
    public void testDuplicateID_shouldFail() {
        //Entering a valid entry
        Person p1 = new Person("29&*lm!vXY", "Rudra", "Chauhan", "90|Market Rd|Melbourne|Victoria|Australia", "10-10-1991");
        p1.addPerson();  //should be added successfully

        //Re-entering the above entry (duplicate check)
        Person p2 = new Person("29&*lm!vXY", "Rudra", "Chauhan", "90|Market Rd|Melbourne|Victoria|Australia", "10-10-1991");
        assertFalse(p2.addPerson());  //should fail due to duplication
    }
}
