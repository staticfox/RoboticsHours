package roboticshours;

import java.io.File;
import java.util.ArrayList;
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

    public static void addAccount(Account a){
        accountList.add(a);
    }

    public static void addNewAccount(Account a){
        accountList.add(a);
        ImportExport.exportSingleFile(a);
    }

    public static Account getAccount(int ID){
        for(Account a : accountList){
            if(a.getAccountID() == ID){
                return a;
            }
        }
        return null;
    }

    public static void hideLoginScreen(){
        loginScreen.setVisible(false);
    }

    public static void main(String[] args) {//TODO: Fix import method trying to call Filename - 1 by finally differentiating between luser and admin methods.
        new File("data").mkdir();
        loginScreen = new LoginScreen();
        java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    loginScreen.setVisible(true); //Timesheet routine, set! Execute!
                }
            });

    }
}
