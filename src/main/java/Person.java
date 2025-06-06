import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

public class Person {
    private String personID;
    private String firstName;
    private String lastName;
    private String address;
    private String birthdate;
    private boolean isSuspended = false;

    public Person(String personID, String firstName, String lastName, String address, String birthdate) {
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthdate = birthdate;
    }

    //FUNCTION 1: addPerson
    public boolean addPerson() {
        //Check for duplicate personID inside persons.txt
        if (isDuplicatePersonID(personID)) {
            System.out.println("Person ID already exists. Record didn't add");
            return false;
        }


        // 1.Validate personID
        if (!isValidPersonID(personID)) {
            return false;
        }


        // 2.Validate address
        if (!isValidAddress(address)) {
            return false;
        }


        // 3.Validate birthdate
        if (!isValidBirthdate(birthdate)) {
            return false;
        }

        //if all validations pass, write to TXT
        try (FileWriter writer = new FileWriter("persons.txt", true)) {
            writer.write(personID + "," + firstName + "," + lastName + "," + address + "," + birthdate + "\n");
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    //FUNCTION 2: updatePersonalDetails
    public boolean updatePersonalDetails(String oldPersonID, String newPersonID, String newFirstName, String newLastName, String newAddress, String newBirthdate) {
        // First check if the person exists in the file
        if (!personExistsInFile(oldPersonID)) {
            System.out.println("Person with ID " + oldPersonID + " not found.");
            return false;
        }

        // Get current person data for validation
        String[] currentPersonData = getCurrentPersonData(oldPersonID);
        if (currentPersonData == null) {
            return false;
        }

        String currentBirthdate = currentPersonData[4];

        // Validate new data using existing validation methods
        if (!isValidPersonID(newPersonID)) {
            return false;
        }

        if (!isValidAddress(newAddress)) {
            return false;
        }

        if (!isValidBirthdate(newBirthdate)) {
            return false;
        }

        // Check if new personID already exists (and it's different from old one)
        if (!oldPersonID.equals(newPersonID) && isDuplicatePersonID(newPersonID)) {
            System.out.println("New Person ID already exists. Cannot update.");
            return false;
        }

        // Condition 1: If person is under 18, address cannot be changed
        if (isUnder18(currentBirthdate) && !currentPersonData[3].equals(newAddress)) {
            System.out.println("Cannot change address for person under 18.");
            return false;
        }

        // Condition 2: If birthday is being changed, no other details can be changed
        if (!currentBirthdate.equals(newBirthdate)) {
            if (!currentPersonData[0].equals(newPersonID) ||
                    !currentPersonData[1].equals(newFirstName) ||
                    !currentPersonData[2].equals(newLastName) ||
                    !currentPersonData[3].equals(newAddress)) {
                System.out.println("When changing birthday, no other personal details can be changed.");
                return false;
            }
        }

        // Condition 3: If first digit of personID is even, ID cannot be changed
        if (!oldPersonID.equals(newPersonID)) {
            char firstChar = oldPersonID.charAt(0);
            if (Character.isDigit(firstChar)) {
                int firstDigit = Character.getNumericValue(firstChar);
                if (firstDigit % 2 == 0) {
                    System.out.println("Cannot change ID when first digit is even.");
                    return false;
                }
            }
        }

        // If all validations pass, update the file
        return updatePersonInFile(oldPersonID, newPersonID, newFirstName, newLastName, newAddress, newBirthdate);
    }

    // Helper method to check if person exists in file
    private boolean personExistsInFile(String personID) {
        try (Scanner scanner = new Scanner(new File("persons.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].equals(personID)) {
                    return true;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    // Helper method to get current person data
    private String[] getCurrentPersonData(String personID) {
        try (Scanner scanner = new Scanner(new File("persons.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[0].equals(personID)) {
                    return parts;
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    // Helper method to check if person is under 18
    private boolean isUnder18(String birthdate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Calendar birthCal = Calendar.getInstance();
            birthCal.setTime(sdf.parse(birthdate));

            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);

            if (today.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            return age < 18;
        } catch (ParseException e) {
            return false;
        }
    }

    // Helper method to update person in file
    private boolean updatePersonInFile(String oldPersonID, String newPersonID, String newFirstName, String newLastName, String newAddress, String newBirthdate) {
        List<String> lines = new ArrayList<>();
        boolean personFound = false;

        // Read all lines and update the matching person
        try (Scanner scanner = new Scanner(new File("persons.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");

                if (parts.length >= 5 && parts[0].equals(oldPersonID)) {
                    // Replace with updated information
                    lines.add(newPersonID + "," + newFirstName + "," + newLastName + "," + newAddress + "," + newBirthdate);
                    personFound = true;
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            return false;
        }

        if (!personFound) {
            return false;
        }

        // Write all lines back to file
        try (FileWriter writer = new FileWriter("persons.txt", false)) {
            for (String line : lines) {
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    //helper methods for the validations
    private boolean isValidPersonID(String id) {
        if (id.length() != 10) return false;

        //first two characters must be digits between 2–9
        if (!Character.isDigit(id.charAt(0)) || !Character.isDigit(id.charAt(1))) return false;
        int d1 = Character.getNumericValue(id.charAt(0));
        int d2 = Character.getNumericValue(id.charAt(1));
        if (d1 < 2 || d1 > 9 || d2 < 2 || d2 > 9) return false;

        //2 special characters between positions 3–8
        int specialCount = 0;
        for (int i = 2; i < 8; i++) {
            char c = id.charAt(i);
            if (!Character.isLetterOrDigit(c)) specialCount++;
        }
        if (specialCount < 2) return false;

        //last two characters must be uppercase
        if (!Character.isUpperCase(id.charAt(8)) || !Character.isUpperCase(id.charAt(9))) return false;

        return true;
    }

    //Address validation with format - 32|Highland Street|Melbourne|Victoria|Australia
    private boolean isValidAddress(String addr) {
        String[] parts = addr.split("\\|");
        if (parts.length != 5) return false;
        String state = parts[3].trim();
        return state.equals("Victoria");
    }

    //Birthdate validation with format - DD-MM-YYYY
    private boolean isValidBirthdate(String dob) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        sdf.setLenient(false);
        try {
            sdf.parse(dob);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    //ADDITIONAL METHOD to validate if the personID already exists in the .TXT file, and if so shows an error message
    private boolean isDuplicatePersonID(String id) {
        try (Scanner scanner = new Scanner(new File("persons.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].equals(id)) {
                    return true;
                }
            }
        } catch (IOException e) {
            //if the file doesn't exist, no duplicates
            return false;
        }
        return false;
    }

    //FUNCTION 3: addDemeritPoints
    public String addDemeritPoints(String personID, String offenseDate, int demeritPoints) {
        //Condition 1: Validate offense date format (DD-MM-YYYY)
        if (!isValidBirthdate(offenseDate)) {
            System.out.println("Invalid offense date format. Use DD-MM-YYYY.");
            return "Failed";
        }

        //Condition 2: Demerit points must be between 1 and 6
        if (demeritPoints < 1 || demeritPoints > 6) {
            System.out.println("Demerit points must be between 1 and 6.");
            return "Failed";
        }

        //Check if person exists
        if (!personExistsInFile(personID)) {
            System.out.println("Person with ID " + personID + " not found.");
            return "Failed";
        }

        //Retrieve person birthdate from persons.txt
        String birthdate = null;
        try (Scanner scanner = new Scanner(new File("persons.txt"))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(",");
                if (parts[0].equals(personID) && parts.length >= 5) {
                    birthdate = parts[4];
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading persons.txt.");
            return "Failed";
        }

        if (birthdate == null) {
            return "Failed";
        }

        //Calculate demerit point total within 2 years
        int previousPoints = getTotalDemeritPointsWithinTwoYears(personID, offenseDate);
        int totalPoints = previousPoints + demeritPoints;

        //Condition 3: Suspension logic
        boolean suspend = false;
        if (isUnder21(birthdate)) {
            if (totalPoints > 6) suspend = true;
        } else {
            if (totalPoints > 12) suspend = true;
        }

        //Write to demerit_points.txt
        try (FileWriter writer = new FileWriter("demerit_points.txt", true)) {
            writer.write(personID + "," + offenseDate + "," + demeritPoints + "," + suspend + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to demerit_points.txt.");
            return "Failed";
        }

        //Update suspension flag in persons.txt
        if (suspend) {
            updateSuspensionStatus(personID, true);
            this.isSuspended = true; // update in-memory field
        }

        return "Success";
    }


    //Getter method for isSuspended
    public boolean isSuspended(String personID) {
        try (Scanner scanner = new Scanner(new File("persons.txt"))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(",");
                if (parts.length >= 6 && parts[0].equals(personID)) {
                    return Boolean.parseBoolean(parts[5].trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading persons.txt: " + e.getMessage());
        }
        return false;
    }

    //Helper: Check if a person is under 21 based on birthdate
    private boolean isUnder21(String birthdate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Calendar birthCal = Calendar.getInstance();
            birthCal.setTime(sdf.parse(birthdate));

            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            return age < 21;
        } catch (ParseException e) {
            System.out.println("Invalid birthdate format for age check.");
            return false;
        }
    }

    //Helper: Total demerit points within 2 years of given offense date
    private int getTotalDemeritPointsWithinTwoYears(String personID, String currentOffenseDate) {
        int totalPoints = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Calendar current = Calendar.getInstance();
            current.setTime(sdf.parse(currentOffenseDate));

            Calendar twoYearsAgo = (Calendar) current.clone();
            twoYearsAgo.add(Calendar.YEAR, -2);

            try (Scanner scanner = new Scanner(new File("demerit_points.txt"))) {
                while (scanner.hasNextLine()) {
                    String[] parts = scanner.nextLine().split(",");
                    if (parts.length >= 3 && parts[0].equals(personID)) {
                        Calendar offense = Calendar.getInstance();
                        offense.setTime(sdf.parse(parts[1]));

                        if (offense.after(twoYearsAgo) && offense.before(current)) {
                            totalPoints += Integer.parseInt(parts[2].trim());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error calculating total demerit points: " + e.getMessage());
            return 0;
        }
        return totalPoints;
    }

    //Helper: Update isSuspended field for person in persons.txt
    private boolean updateSuspensionStatus(String personID, boolean suspendFlag) {
        List<String> updatedLines = new ArrayList<>();
        boolean found = false;

        try (Scanner scanner = new Scanner(new File("persons.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");

                if (parts.length >= 5 && parts[0].equals(personID)) {
                    found = true;
                    if (parts.length == 5) {
                        updatedLines.add(line + "," + suspendFlag);
                    } else if (parts.length == 6) {
                        updatedLines.add(String.join(",", parts[0], parts[1], parts[2], parts[3], parts[4], String.valueOf(suspendFlag)));
                    } else {
                        updatedLines.add(line); // unexpected structure
                    }
                } else {
                    updatedLines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading persons.txt: " + e.getMessage());
            return false;
        }

        if (!found) return false;

        try (FileWriter writer = new FileWriter("persons.txt", false)) {
            for (String line : updatedLines) {
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error writing persons.txt: " + e.getMessage());
            return false;
        }

        return true;
    }






    //testing the code with Sample inputs
    public static void main(String[] args) {
        //Test addPerson
        Person p = new Person("36s#@x!1DE", "Rohan", "Chauhan", "8|Invernes Street|Melbourne|Victoria|Australia", "12-10-2000");
        boolean result = p.addPerson();
        System.out.println("Add person result: " + result);

        //Test updatePersonalDetails
        boolean updateResult = p.updatePersonalDetails("36s#@x!1DE", "27s#@x!1DE", "Hiran", "Kosala", "9|Invernes Street|Melbourne|Victoria|Australia", "12-10-2000");
        System.out.println("Update person result: " + updateResult);

        //Test addDemeritPoints
        String demeritResult = p.addDemeritPoints("27s#@x!1DE", "15-11-2023", 5);
        System.out.println("Add demerit points result: " + demeritResult);
    }
}