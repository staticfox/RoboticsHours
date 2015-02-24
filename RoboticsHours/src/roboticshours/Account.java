package roboticshours;

import java.util.ArrayList;

/**
 *
 * @author Alan
 */
public class Account {

    private static int numberOfAccounts; //UNNECESSARY: TODO REMOVE
    private final int ID; //Primary key?
    private String name; //TODO FIRST AND LAST NAME
    private final ArrayList<Entry> entryList;

    /**
     * Creates a new account.
     * @param n the user's name
     */
    public Account(String n) { //Constructor A: Users should use this constructor.
        ID = numberOfAccounts;
        name = n;
        entryList = new ArrayList<>();
        numberOfAccounts++;
    }

    /**
     * Creates a new account and specifies the ID number to be used.
     * @param i custom ID number
     * @param n the user's name
     */
    public Account(int i, String n) { //Constructor B: This constructor should only be used by the program.
        ID = i;
        name = n;
        entryList = new ArrayList<>();
        numberOfAccounts++;
    }

    /**
     * @return  the user's name
     */
    public String getAccountName() {
        return name;
    }

    /**
     * @param n the new account name
     */
    public void changeAccountName(String n) {
        name = n;
    }

    /**
     * @return  the account ID
     */
    public int getAccountID() {
        return ID;
    }

    /**
     * Adds an <code>Entry</code> object to the internal <code>ArrayList</code> within the <code>Account</code>.
     * @param e the <code>Entry</code> to be added
     */
    public void addEntry(Entry e) {
        entryList.add(e);
    }

    /**
     *
     * @param e
     */
    public void deleteEntry(Entry e) {
        entryList.remove(e);
    }

    /**
     *
     * @return
     */
    public ArrayList<Entry> getEntries() {
        return entryList;
    }

    /**
     *  Clears all the entries in entryList.
     */
    public void clearEntries() {
        entryList.clear();
    }
    
    /**
     *  Increases the current counter for account IDs.
     */
    public static void incrementNumberOfAccounts(){
        numberOfAccounts++;
    }
}
