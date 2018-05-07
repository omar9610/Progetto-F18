package engine;

import database.DBConnector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import enumeration.DogSize;

import static staticClasses.ObjectCreator.createDogFromDB;
import static staticClasses.ObjectCreator.getCustomerListAssignmentFromDB;

public class Customer extends User {
    private HashSet<Dog> dogList;        //Sostituire tipo String con tipo engine.Dog quando sarà disponibile la classe
    private HashMap<Integer, Assignment> assignmentList;
    private HashMap<String, Review> reviewList;

    public Customer(String email, String name, String surname, String password, String phoneNumber, Date dateOfBirth, Address address, PaymentMethod paymentMethod){
        super(email, name, surname, password, phoneNumber, dateOfBirth, address, paymentMethod);
        dogList = new HashSet<Dog>(3);    //Sostituire tipo String con tipo engine.Dog quando sarà disponibile la classe
        assignmentList = new HashMap<Integer, Assignment>();
        reviewList = new HashMap<String, Review>();
        assignmentList = getCustomerListAssignmentFromDB(email);
        dogList = getDogListFromDB(email);
    }

    public Assignment addAssignment(DogSitter ds, Date dateStartAssignment, Date dateEndAssignment, HashSet<Dog>selectedDogs, Address meetingPoint){
        String emailDogSitter = ds.email;

        //chiamata alla classe banca per effettuare la transazione
        //implementare metodo definitivo

        //chiamata alla classe banca per effettuare la transazione (blocco provvisorio)
        boolean testTransaction = true;
        DBConnector dbConnector = new DBConnector();
        int code = -1;
        try {
            ResultSet rs = dbConnector.askDB("SELECT * FROM ASSIGNMENT");
            rs.last();
            code = rs.getRow() + 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Bank bank = new Bank();

        //implementare funzione per il calcolo del prezzo della prestazione
        double amount = 1;

        if (bank.isTransactionPossible(email, amount)) {

            //crea un oggetto di tipo Assignment e lo aggiunge all'HashMap assignmentList
            //Assignment assignment = new Assignment(code, selectedDogs, dateStartAssignment, dateEndAssignment, meetingPoint);
            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date.setLenient(false);
            Date startAssignment = new Date();
            Date endAssignment = new Date();

            String dateStringStartAssigment = date.format(dateStartAssignment);
            String dateStringEndAssigment = date.format(dateEndAssignment);
            try {
                startAssignment = date.parse(dateStringStartAssigment);
                endAssignment = date.parse(dateStringEndAssigment);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //String code = dateStringStartAssigment  + "_" + ds.email + "_" + this.email;
            Assignment assignment = new Assignment(code, selectedDogs, dateStartAssignment, dateEndAssignment, meetingPoint);
            assignmentList.put(code, assignment);

            //salva la prenotazione nel database

            Timestamp sqlStart = new Timestamp(startAssignment.getTime());
            Timestamp sqlEnd = new Timestamp(endAssignment.getTime());

            //System.out.println("INSERT INTO ASSIGNMENT VALUES (" + code + ", '" + email  + "', '" + ds.getEmail() + "', TRUE, '" + dateStringStartAssigment + "', '" + dateStringEndAssigment + "')");

            try {
                dbConnector.updateDB("INSERT INTO ASSIGNMENT VALUES (" + code + ", '" + email  + "', '" + ds.getEmail() + "', TRUE, '" + dateStringStartAssigment + "', '" + dateStringEndAssigment + "')");
                dbConnector.updateDB("INSERT INTO MEETING_POINT VALUES (" + code + ", '" + meetingPoint.getCountry() + "', '" + meetingPoint.getCity() + "', '" + meetingPoint.getStreet() + "', '" + meetingPoint.getCap() + "', '" + meetingPoint.getCap() + "')");
                for (Dog d : dogList) {
                    dbConnector.updateDB("INSERT INTO DOG_ASSIGNMENT VALUES (" + code + ", " + d.getID() + ")");
                }
                //bank.makeBankTransaction(email, emailDogSitter, code, amount);
                dbConnector.closeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            bank.makeBankTransaction(email, emailDogSitter, code, amount);

            System.out.println("Assignment completed successfully!");
            System.out.println(assignment.toString());
            return assignment;
        } else {
            System.out.println("Error during assignment with " + ds.email);
            return null;
        }
    }

    public boolean removeAssignment(String key){
        Assignment a = null;
        a = assignmentList.get(key);
        if (a != null){
            assignmentList.remove(key);
            System.out.println("Selected assignment removed!");

            //aggiungere codice per rimuovere la prenotazione dal database

            return true;
        } else {
            System.out.println("Error in removing the selected assignment!");
            return false;
        }
    }

    public Review addReview(DogSitter ds, Date dateReview, int rating, String title, String comment){
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        date.setLenient(false);
        String dateStringReview = date.format(dateReview);
        Review review = new Review(this, ds, dateReview, rating, title.toUpperCase(), comment);

        //salva la recensione nel database
        //sottometodo da implementare

        reviewList.put(dateStringReview  + "_" + ds.email + "_" + this.email, review);
        System.out.println(review.toString());
        return review;
    }

    public boolean removeReview(String key){
        Review r = null;
        r = reviewList.get(key);
        if (r != null){
            reviewList.remove(key);
            System.out.println("Selected review removed!");

            //aggiungere codice per rimuovere la recensione dal database

            return true;
        } else {
            System.out.println("Error in removing the selected review!");
            return false;
        }
    }

    public HashMap<Integer, Assignment> listAssignment(){
        Assignment a = null;
        for(Integer key : assignmentList.keySet()){
            a = assignmentList.get(key);
            System.out.println(a.toString());
        }
        if (a == null){
            System.out.println("There are no assignment available!");
        }
        return assignmentList;
    }

    public HashMap<String, Review> listReview(){
        Review r = null;
        for(String key : reviewList.keySet()){
            r = reviewList.get(key);
            System.out.println(r.toString());
        }
        if (r == null){
            System.out.println("There are no reviews available!");
        }
        return reviewList;
    }

    public HashSet<Dog> addDog(String name, String breed, DogSize size, int age, double weight, int ID){
        Dog dog = new Dog(name, breed, size, age, weight, ID);
        dogList.add(dog);
        return dogList;
    }

    public HashSet<Dog> removeDog(Dog dog){
        if (dogList.contains(dog)){
            dogList.remove(dog);
            return dogList;
        } else {
            return null;
        }
    }

    public HashMap<Integer, Assignment> getAssignmentList() {
        return assignmentList;
    }

    public Address getAddress(){
        return address;
    }

    public HashSet<Dog> getDogList() {
        return dogList;
    }

    private HashSet<Dog> getDogListFromDB(String email){
        DBConnector dbConnector = new DBConnector();
        try {
            ResultSet rs = dbConnector.askDB("SELECT ID FROM DOGS WHERE OWNER_EMAIL = '" + email + "'");
            while (rs.next()){
                int dogID = rs.getInt("ID");
                Dog dog = createDogFromDB(dogID);
                dogList.add(dog);
            }
            dbConnector.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dogList;
    }
}
