package engine;


import database.DBConnector;
import enumeration.TypeUser;
import staticClasses.ObjectCreator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Bank {


    private HashMap<String, BankUser> listUser;
    private long nTransaction;

    public Bank() {
        nTransaction = countTransaction();
        this.listUser = new HashMap<String,BankUser>();

        DBConnector dbConnector = new DBConnector();
        try {
            ResultSet rs = dbConnector.askDB("SELECT EMAIL FROM CUSTOMERS");
            while (rs.next()) {
                String email = rs.getString("EMAIL");
                BankUser bu = new BankUser(email, TypeUser.CUSTOMER);
                listUser.put(email, bu);
            }

            dbConnector.closeConnection();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            ResultSet rs = dbConnector.askDB("SELECT EMAIL FROM DOGSITTERS");
            while (rs.next()){
                String email = rs.getString("EMAIL");
                BankUser bu = new BankUser(email, TypeUser.DOGSITTER);
                listUser.put(email, bu);
            }
            dbConnector.closeConnection();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public long getnTransaction() {
        return nTransaction;
    }

    public boolean makeBankTransaction(String emailCustomer, String emailDogsitter, int code, double amount) {
        BankUser customer = listUser.get(emailCustomer);
        BankUser dogsitter = listUser.get(emailDogsitter);
        PaymentMethod pmCustomer = customer.getPaymentMethod();
        PaymentMethod pmDogsitter = dogsitter.getPaymentMethod();
        pmCustomer.setAmount(pmCustomer.getAmount() - amount);
        pmDogsitter.setAmount(pmDogsitter.getAmount() + amount);

        if (pmCustomer.getAmount() < 0) {
            System.out.println("Transazione non riuscita: credito insufficiente");
            return false;
        }
        else {
            System.out.println(emailCustomer + ": €" + pmCustomer.getAmount());
            System.out.println(emailDogsitter + ": €" + pmDogsitter.getAmount());

            DBConnector dbConnector = new DBConnector();

            try {
                boolean updateCustomer = dbConnector.updateDB("UPDATE CREDIT_CARDS SET AMOUNT = " + pmCustomer.getAmount() + "WHERE NUM = '" + pmCustomer.getNumber() + "';");
                boolean updateDogsitter = dbConnector.updateDB("UPDATE CREDIT_CARDS SET AMOUNT = " + pmDogsitter.getAmount() + "WHERE NUM = '" + pmDogsitter.getNumber() + "';");
                dbConnector.closeUpdate();

                if (updateCustomer && updateDogsitter) {
                    System.out.println("Importi trasferiti con successo: conti correnti aggiornati");

                    //Date date = new Date(); // java.util.Date; - This date has both the date and time in it already.
                    //Timestamp sqlDate = new Timestamp(new Date().getTime());
                    SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String strDate = date.format(new Date());

                    dbConnector.updateDB("INSERT INTO TRANSACTIONS VALUES ('" + emailCustomer + "', '" + emailDogsitter + "', '" + strDate + "', " + code + ", " + amount + ")");
                    dbConnector.closeUpdate();
                } else {
                    System.out.println("Errore nel trasferimento");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return true;
    }


    public void printBankUsers() {
        for (String key: listUser.keySet()) {
            System.out.println(listUser.get(key));
        }
    }

    private int countTransaction(){
        DBConnector dbConnector = new DBConnector();
        try {
            ResultSet rs = dbConnector.askDB("SELECT * FROM TRANSACTIONS");
            rs.last();
            int nTransaction = rs.getRow();
            dbConnector.closeConnection();
            return nTransaction;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;  //error
        }
    }

    public boolean isTransactionPossible(String emailCustomer, double amount){
        BankUser customer = listUser.get(emailCustomer);
        PaymentMethod pmCustomer = customer.getPaymentMethod();
        if (pmCustomer.getAmount() - amount < 0){
            System.out.println("Impossibile effettuare la transazione: credito insufficiente");
            return false;
        } else {
            return true;
        }
    }
}

