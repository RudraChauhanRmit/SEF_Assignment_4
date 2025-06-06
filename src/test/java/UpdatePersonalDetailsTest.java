import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//Unit tests for the updatePersonalDetails function in Person class with JUnit
public class UpdatePersonalDetailsTest {

    //Setup method to create test data in persons.txt before each test
    @BeforeEach
    public void setUp() {
        try {
            //Clear the file and add test persons for update operations
            FileWriter writer = new FileWriter("persons.txt", false);
            writer.write("33s#@x!zAB,Vathavooran,Athiyaman,45|King St|Burwood|Victoria|Australia,12-12-1995\n");
            writer.write("24$%df!kGH,Hiran,Karunathilaka,12|Main St|Melbourne|Victoria|Australia,01-01-1990\n");
            writer.write("25^@de!qJK,RMIT,Uni,89|Bridge Rd|Melbourne|Victoria|Australia,15-05-2010\n"); //under 18
            writer.write("26&*lm!vXY,Rudra,Chauhan,90|Market Rd|Melbourne|Victoria|Australia,10-10-1991\n"); //even first digit
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Test cases for 'updatePersonalDetails' function

    //Test case 1 - Valid update with all new data meeting conditions
    //person details should be successfully updated in TXT file
    @Test
    public void testValidUpdate_shouldSuccess() {
        Person p = new Person("", "", "", "", "");
        boolean result = p.updatePersonalDetails("33s#@x!zAB", "27s#@x!zCD", "Vathavooran", "Athiyaman", "50|Queen St|Burwood|Victoria|Australia", "12-12-1995");
        assertTrue(result);
    }

    //Test case 2 - Invalid update - trying to change address for person under 18 - fails Condition 1
    //person details should not be updated in TXT file
    @Test
    public void testUnder18AddressChange_shouldFail() {
        Person p = new Person("", "", "", "", "");
        boolean result = p.updatePersonalDetails("25^@de!qJK", "27s#@x!zEF", "RMIT", "Uni", "100|New St|Melbourne|Victoria|Australia", "15-05-2010");
        assertFalse(result);
    }

    //Test case 3 - Invalid update - trying to change other details when birthday is being changed - fails Condition 2
    //person details should not be updated in TXT file
    @Test
    public void testBirthdayChangeWithOtherChanges_shouldFail() {
        Person p = new Person("", "", "", "", "");
        boolean result = p.updatePersonalDetails("24$%df!kGH", "28s#@x!zGH", "NewName", "Karunathilaka", "12|Main St|Melbourne|Victoria|Australia", "15-06-1990");
        assertFalse(result);
    }

    //Test case 4 - Invalid update - trying to change ID when first digit is even - fails Condition 3
    //person details should not be updated in TXT file
    @Test
    public void testEvenFirstDigitIDChange_shouldFail() {
        Person p = new Person("", "", "", "", "");
        boolean result = p.updatePersonalDetails("26&*lm!vXY", "29s#@x!zIJ", "Rudra", "Chauhan", "90|Market Rd|Melbourne|Victoria|Australia", "10-10-1991");
        assertFalse(result);
    }

    //Test case 5 - Invalid update - person doesn't exist in TXT file
    //update should fail as person is not found
    @Test
    public void testNonExistentPerson_shouldFail() {
        Person p = new Person("", "", "", "", "");
        boolean result = p.updatePersonalDetails("99s#@x!zKL", "30s#@x!zMN", "Ghost", "Person", "1|Fake St|Melbourne|Victoria|Australia", "01-01-2000");
        assertFalse(result);
    }
}