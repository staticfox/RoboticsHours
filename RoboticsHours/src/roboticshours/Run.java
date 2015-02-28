package roboticshours;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import javax.swing.JFrame;

/**
 * A simple main method to populate a list of accounts and test functionality.
 * @author Alan
 */
public class Run {

    private static final ArrayList<roboticshours.Account> accountList = new ArrayList<>();
    private static JFrame loginScreen;
    /**
     *
     * @return
     */
    public static ArrayList<Account> getAccountList() { //returns list of accounts for Import/Export
        return accountList;
    }

    /**
     *  Exports accounts, clears accountList, and re-imports the files.
     *  This is in order to TEST the encryption, export, and import functions.
     */
    public static void save() {
        roboticshours.Encryptor.makeKey(Encryptor.hash("Alan+HuangCheese*97".getBytes(), 1024)); //Makes a key from credentials; will eventially be accomplished using user input.

        Account test = new Account(1, "Alan", "Huang"); //Creates new Account: ID 1, name Saito
        accountList.add(test); //adds Account test to accountList
        test.addEntry(new Entry(test, 6, new GregorianCalendar())); //Adds new entry with current date/time to Account test

        test.addEntry(new Entry(test, 3, new GregorianCalendar()));

        ImportExport.exportSingleFile(accountList.get(0));

        Encryptor.makeKey(Encryptor.hash("Hikari+SaitoRockman.EXE".getBytes(), 1024));

        Account test2 = new Account(2, "Hikari", "Saito");
        accountList.add(test2);

        test2.addEntry(new Entry(test2, 60, new GregorianCalendar()));

        test2.addEntry(new Entry(test2, 30, new GregorianCalendar()));

        ImportExport.exportSingleFile(accountList.get(1));//Export the data to /data/$ID.dat

        for (Account a : accountList) {
            a.clearEntries(); //Clears entries for-each Account in Account List
        }

        accountList.clear(); //Clears the account list itself
    }
    
    public static void hideLoginScreen(){
        loginScreen.setVisible(false);
    }

    public static void main(String[] args) {//TODO: Fix import method trying to call Filename - 1 by finally differentiating between luser and admin methods.
        save();

        loginScreen = new LoginScreen();
        loginScreen.setVisible(true); //Timesheet routine, set! Execute!
    }
}
