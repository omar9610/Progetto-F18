package server;

import database.DBConnector;
import enumeration.AssignmentState;
import server.bank.PaymentMethod;
import server.dateTime.WeekDays;
import server.dateTime.WorkingTime;
import server.places.Address;
import server.places.Area;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import static server.tools.dateTime.DateTimeTools.getAge;

/**
 * This class implements Singleton Pattern for the creation of some different types of objects.
 */
public class Singleton {

    /**
     * Create the customer specified.
     * @param customerEmail the customer's email address.
     * @return the object of type Customer related to customerEmail.
     */
    public Customer createCustomerFromDB(String customerEmail){
        DBConnector dbConnector = new DBConnector();
        try {
            ResultSet rs = dbConnector.askDB("SELECT NAME, SURNAME, PASSWORD, PHONE_NUMB, BIRTHDATE, PAYMENT FROM CUSTOMERS WHERE EMAIL = '" + customerEmail + "'");
            rs.next();
            String name = rs.getString("NAME");
            String surname = rs.getString("SURNAME");
            String password = rs.getString("PASSWORD");
            String phone = rs.getString("PHONE_NUMB");
            Date birthdate = rs.getDate("BIRTHDATE");
            String payment = rs.getString("PAYMENT");
            dbConnector.closeConnection();

            PaymentMethod paymentMethod = getPaymentMethodFromDB(payment);

            Address address = getAddressFromDB(customerEmail);

            return new Customer(customerEmail, name, surname, password, phone, birthdate, address, paymentMethod);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Create the dog sitter specified.
     * @param dogSitterEmail the dog sitter's email address.
     * @return the object of type DogSitter related to dogSitterEmail.
     */
    public DogSitter createDogSitterFromDB(String dogSitterEmail){
        DBConnector dbConnector = new DBConnector();
        try {
            ResultSet rs = dbConnector.askDB("SELECT NAME, SURNAME, PASSWORD, PHONE_NUMB, BIRTHDATE, PAYMENT, CASH_FLAG, NDOGS, BIOGRAPHY FROM DOGSITTERS WHERE EMAIL = '" + dogSitterEmail + "'");
            rs.next();
            String name = rs.getString("NAME");
            String surname = rs.getString("SURNAME");
            String password = rs.getString("PASSWORD");
            String phone = rs.getString("PHONE_NUMB");
            Date birthdate = rs.getDate("BIRTHDATE");
            String payment = rs.getString("PAYMENT");
            boolean cashFlag = rs.getBoolean("CASH_FLAG");
            int nDogs = rs.getInt("NDOGS");
            String biography = rs.getString("BIOGRAPHY");
            dbConnector.closeConnection();

            Address address = getAddressFromDB(dogSitterEmail);

            rs = dbConnector.askDB("SELECT CITY FROM DOGSITTER_AREA WHERE DOGSITTER = '" + dogSitterEmail + "'");
            Area listArea = new Area();
            while (rs.next()){
                String cityOp = rs.getString("CITY");
                listArea.addPlace(cityOp);
            }
            dbConnector.closeConnection();
            HashSet<DogSize> listDogSize = createListDogSize(dogSitterEmail);
            PaymentMethod paymentMethod = getPaymentMethodFromDB(payment);
            Availability availability = createAvailability(dogSitterEmail);
            return new DogSitter(dogSitterEmail, name, surname, password, phone, birthdate, address, paymentMethod, listArea, listDogSize, nDogs, biography, availability, cashFlag);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Create dog sitter availability
     * @param dogSitterEmail the dog sitter's email address.
     * @return the dog sitter's availability.
     */
    private Availability createAvailability(String dogSitterEmail){
        ResultSet rs;
        DBConnector dbConnector = new DBConnector();
        try {
            rs = dbConnector.askDB("SELECT  MON_START, MON_END, TUE_START, TUE_END, WED_START, WED_END, THU_START, THU_END, FRI_START, FRI_END, SAT_START, SAT_END, SUN_START, SUN_END FROM AVAILABILITY WHERE DOGSITTER = '" + dogSitterEmail + "'");
            Availability availability = new Availability();
            rs.next();
            Time monStart = rs.getTime("MON_START");
            Time monEnd = rs.getTime("MON_END");
            WorkingTime mon = new WorkingTime(monStart, monEnd);
            availability.setDayAvailability(mon, WeekDays.MON);
            Time tueStart = rs.getTime("TUE_START");
            Time tueEnd = rs.getTime("TUE_END");
            WorkingTime tue = new WorkingTime(tueStart, tueEnd);
            availability.setDayAvailability(tue, WeekDays.TUE);
            Time wedStart = rs.getTime("WED_START");
            Time wedEnd = rs.getTime("WED_END");
            WorkingTime wed = new WorkingTime(wedStart, wedEnd);
            availability.setDayAvailability(wed, WeekDays.WED);
            Time thuStart = rs.getTime("THU_START");
            Time thuEnd = rs.getTime("THU_END");
            WorkingTime thu = new WorkingTime(thuStart, thuEnd);
            availability.setDayAvailability(thu, WeekDays.THU);
            Time friStart = rs.getTime("FRI_START");
            Time friEnd = rs.getTime("FRI_END");
            WorkingTime fri = new WorkingTime(friStart, friEnd);
            availability.setDayAvailability(fri, WeekDays.FRI);
            Time satStart = rs.getTime("SAT_START");
            Time satEnd = rs.getTime("SAT_END");
            WorkingTime sat = new WorkingTime(satStart, satEnd);
            availability.setDayAvailability(sat, WeekDays.SAT);
            Time sunStart = rs.getTime("SUN_START");
            Time sunEnd = rs.getTime("SUN_END");
            WorkingTime sun = new WorkingTime(sunStart, sunEnd);
            availability.setDayAvailability(sun, WeekDays.SUN);
            dbConnector.closeConnection();
            return availability;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }


    /**
     * Create the list of dog size that the dog sitter's accepts.
     * @param dogSitterEmail the dog sitter's email address.
     * @return the list of dog size that the dog sitter's accepts.
     */
    private HashSet<DogSize> createListDogSize(String dogSitterEmail){
        ResultSet rs;
        DBConnector dbConnector = new DBConnector();
        HashSet<DogSize> listDogSize = new HashSet<>();
        try {
            rs = dbConnector.askDB("SELECT SMALL, MEDIUM, BIG, GIANT FROM DOGS_ACCEPTED WHERE DOGSITTER = '" + dogSitterEmail + "'");
            rs.next();
            boolean small = rs.getBoolean("SMALL");
            if (small){
                listDogSize.add(DogSize.SMALL);
            }
            boolean medium = rs.getBoolean("MEDIUM");
            if (medium){
                listDogSize.add(DogSize.MEDIUM);
            }
            boolean big = rs.getBoolean("BIG");
            if (big){
                listDogSize.add(DogSize.BIG);
            }
            boolean giant = rs.getBoolean("GIANT");
            if (giant){
                listDogSize.add(DogSize.GIANT);
            }
            dbConnector.closeConnection();
            return listDogSize;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Create the dog specified.
     * @param dogID the dog's ID.
     * @return the object of type Dog related to dogID.
     */
    public Dog createDogFromDB(int dogID){
        DBConnector dbConnector = new DBConnector();
        Dog dog = null;
        try {
            ResultSet rs = dbConnector.askDB("SELECT NAME, BREED, WEIGHT, AGE, IS_ENABLED, OWNER_EMAIL FROM DOGS WHERE ID = " + dogID + "");
            rs.next();
            String name = rs.getString("NAME");
            String breed = rs.getString("BREED");
            double weight = rs.getDouble("WEIGHT");
            int age = getAge(rs.getDate("AGE"));
            boolean isEnabled =  rs.getBoolean("IS_ENABLED");
            dbConnector.closeConnection();

            rs = dbConnector.askDB("SELECT SIZE FROM BREEDS WHERE NAME = '" + breed + "'");
            rs.next();
            DogSize size = DogSize.valueOf(rs.getString("SIZE"));
            dbConnector.closeConnection();

            dog = new Dog(name, breed, size, age, weight, dogID, isEnabled);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dog;
    }


    /**
     * Get the dog sitter's list of assignments.
     * @param dogSitter the dog sitter's email address.
     * @return the HashMap of Assignment related to dogSitter. The key indicates the assignment's code.
     */
    public HashMap<Integer, Assignment> getDogSitterListAssignmentFromDB(String dogSitter){
        //HashMap<Integer, Assignment> listAssignment = new HashMap<Integer, Assignment>();
        DBConnector dbConnector = new DBConnector();
        try {
            ResultSet rs = dbConnector.askDB("SELECT CODE, CONFIRMATION, DATE_START, DATE_END FROM ASSIGNMENT WHERE DOGSITTER = '" + dogSitter + "'");
            HashMap<Integer, Assignment> assignmentList = getAssignmentList(rs);
            dbConnector.closeConnection();
            return assignmentList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Get the customer's list of assignments.
     * @param customer the customer's email address.
     * @return the HashMap of Assignment related to customer. The key indicates the assignment's code.
     */
    public HashMap<Integer, Assignment> getCustomerListAssignmentFromDB(String customer){
        //HashMap<Integer, Assignment> listAssignment = new HashMap<Integer, Assignment>();
        DBConnector dbConnector = new DBConnector();
        try {
            ResultSet rs = dbConnector.askDB("SELECT CODE, CONFIRMATION, DATE_START, DATE_END FROM ASSIGNMENT WHERE CUSTOMER = '" + customer + "'");
            HashMap<Integer, Assignment> assignmentList = getAssignmentList(rs);
            dbConnector.closeConnection();
            return assignmentList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the meeting point of the assignment specified.
     * @param code the assignment's code.
     * @return the object of type Address related to code.
     */
    public Address getMeetingPointFromDB(int code){
        DBConnector dbConnector = new DBConnector();
        Address address = null;
        try {
            ResultSet rs = dbConnector.askDB("SELECT COUNTRY, CITY, STREET, CNUMBER, CAP FROM MEETING_POINT WHERE CODE = " + code);
            rs.next();
            String country = rs.getString("COUNTRY");
            String city = rs.getString("CITY");
            String street = rs.getString("STREET");
            String number = rs.getString("CNUMBER");
            String cap = rs.getString("CAP");
            address = new Address(country, city, street, number, cap);
            dbConnector.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return address;
    }


    /**
     * Get the list of dog assigned in the specified assignment.
     * @param code the assignment's code.
     * @return the HashSet of Dog related to the Assignment indicated with code.
     */
    public HashSet<Dog> getDogListFromDB(int code){
        HashSet<Dog> dogList= new HashSet<>();
        DBConnector dbConnector = new DBConnector();
        try {
            ResultSet rs = dbConnector.askDB("SELECT DOG_ID FROM DOG_ASSIGNMENT WHERE CODE = '" + code + "'");
            while (rs.next()){
                int dogID = rs.getInt("DOG_ID");
                Dog dog = createDogFromDB(dogID);
                dogList.add(dog);
            }
            dbConnector.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dogList;
    }


    /**
     * Get the user's address.
     * @param email the user's email address.
     * @return the object Address related to email.
     */
    public Address getAddressFromDB(String email){
        DBConnector dbConnector = new DBConnector();
        ResultSet rs = null;
        try {
            rs = dbConnector.askDB("SELECT COUNTRY, CITY, STREET, CNUMBER, CAP FROM ADDRESS WHERE EMAIL = '" + email + "'");
            rs.next();
            String country = rs.getString("COUNTRY");
            String city = rs.getString("CITY");
            String street = rs.getString("STREET");
            String cnumber = rs.getString("CNUMBER");
            String cap = rs.getString("CAP");
            Address address = new Address(country, city, street, cnumber, cap);
            dbConnector.closeConnection();
            return address;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Get the user's payment method.
     * @param payment the card's number.
     * @return the object PaymentMethod related to the card's number 'payment'.
     */
    public PaymentMethod getPaymentMethodFromDB(String payment){
        DBConnector dbConnector = new DBConnector();
        ResultSet rs = null;
        try {
            rs = dbConnector.askDB("SELECT OWNER_NAME, OWNER_SURNAME, EXPIRATION_DATE, CVV, AMOUNT FROM CREDIT_CARDS WHERE NUM = '" + payment + "'");
            rs.next();
            String ownerName = rs.getString("OWNER_NAME");
            String ownerSurname = rs.getString("OWNER_SURNAME");
            Date expirationDate = rs.getDate("EXPIRATION_DATE");
            String cvv = rs.getString("CVV");
            double amount = rs.getDouble("AMOUNT");
            PaymentMethod paymentMethod = new PaymentMethod(payment, ownerName, ownerSurname, expirationDate, cvv, amount);
            dbConnector.closeConnection();
            return paymentMethod;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Get the list of customer's review.
     * @param customer the customer.
     * @return the HashMap of the customer's reviews.
     */
    public HashMap<Integer, Review> getCustomerReviewList(Customer customer){
        DBConnector dbConnector = new DBConnector();
        ResultSet rs = null;
        try {
            rs = dbConnector.askDB("SELECT R.ASSIGNMENT_CODE, R.DATE, R.RATING, R.TITLE, R.DESCRIPTION, R.REPLY FROM REVIEW AS R jOIN ASSIGNMENT AS A ON R.ASSIGNMENT_CODE = A.CODE WHERE A.CUSTOMER = '" + customer.email + "'");
            HashMap<Integer, Review> reviewList = getReviewList(rs);
            dbConnector.closeConnection();
            return reviewList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Get the list of dog sitter's reviews.
     * @param dogSitter the dog sitter.
     * @return the HashMap of the dog sitter's reviews.
     */
    public HashMap<Integer, Review> getDogSitterReviewList(DogSitter dogSitter){
        DBConnector dbConnector = new DBConnector();
        ResultSet rs = null;
        try {
            rs = dbConnector.askDB("SELECT R.ASSIGNMENT_CODE, R.DATE, R.RATING, R.TITLE, R.DESCRIPTION, R.REPLY FROM REVIEW AS R jOIN ASSIGNMENT AS A ON R.ASSIGNMENT_CODE = A.CODE WHERE A.DOGSITTER = '" + dogSitter.email + "'");
            HashMap<Integer, Review> reviewList = getReviewList(rs);
            dbConnector.closeConnection();
            return reviewList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Get the review specified with code.
     * @param code the assignment's code related to the review.
     * @return the object of type Review related to code.
     */
    public Review getReview(int code){
        DBConnector dbConnector = new DBConnector();
        ResultSet rs = null;
        Review review = null;
        try {
            rs = dbConnector.askDB("SELECT DATE, RATING, TITLE, DESCRIPTION, REPLY FROM REVIEW WHERE ASSIGNMENT_CODE = '" + code + "'");
            while (rs.next()){
                Date date = rs.getTimestamp("DATE");
                int rating = rs.getInt("RATING");
                String title = rs.getString("TITLE");
                String description = rs.getString("DESCRIPTION");
                String reply = rs.getString("REPLY");
                Singleton singleton = new Singleton();
                review = new Review(code, date, rating, title, description, reply);
            }
            dbConnector.closeConnection();
            return review;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Read the list of assignments from a ResultSet object.
     * @param rs the result of query.
     * @return the HashMap of assignments.
     * @throws SQLException
     */
    private HashMap<Integer, Assignment> getAssignmentList(ResultSet rs) throws SQLException {
        HashMap<Integer, Assignment> assignmentList = new HashMap<>();
        while (rs.next()){
            int code = rs.getInt("CODE");
            String strState = rs.getString("CONFIRMATION");

            AssignmentState state = AssignmentState.valueOf(strState);

            /*if (strState.equals("TRUE")){
                state = true;
            } else if (strState.equals("FALSE")){
                state = false;
            } else {
                state = null;
            }*/
            Date dateStart = rs.getTimestamp("DATE_START");
            Date dateEnd = rs.getTimestamp("DATE_END");
            Address meetingPoint = getMeetingPointFromDB(code);
            HashSet dogList = getDogListFromDB(code);
            Assignment assignment = new Assignment(code, dogList, dateStart, dateEnd, state, meetingPoint);
            assignmentList.put(code, assignment);
        }
        return assignmentList;
    }


    /**
     * Read the list of reviews from a ResultSet object.
     * @param rs the result of query.
     * @return the HashMap of reviews.
     * @throws SQLException
     */
    private HashMap<Integer, Review> getReviewList(ResultSet rs) throws SQLException {
        HashMap<Integer, Review> reviewList = new HashMap<>();
        while (rs.next()){
            int code = rs.getInt("ASSIGNMENT_CODE");
            Date date = rs.getTimestamp("DATE");
            int rating = rs.getInt("RATING");
            String title = rs.getString("TITLE");
            String description = rs.getString("DESCRIPTION");
            String reply = rs.getString("REPLY");
            Review r = new Review(code, date, rating, title, description, reply);
            reviewList.put(code, r);
        }
        return reviewList;
    }
}
