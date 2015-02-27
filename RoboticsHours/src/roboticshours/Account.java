package roboticshours;

import java.io.File;
import java.util.ArrayList;

/**
 * A class to organize hour entries.
 * This class contains an ID, name, and <code>entryList</code>,
 * an {@link ArrayList} used to store entries.
 * 
 * @author Alan
 */
public class Account {

    private final int ID; //Primary key?
    private String name; //TODO FIRST AND LAST NAME. Possibly concatenate users' names into one string without spaces.
    private final ArrayList<Entry> entryList;

    /**
     * Creates a new account
     * @param n the user's name
     */
    public Account(String n) { //Constructor A: Users should use this constructor.
        ID = new File("data").listFiles().length;
        name = n;
        entryList = new ArrayList<>();
    }

    /**
     * Creates a new account and specifies the ID number to be used
     * @param i custom ID number
     * @param n the user's name
     */
    public Account(int i, String n) { //Constructor B: This constructor should only be used by the program.
        ID = i;
        name = n;
        entryList = new ArrayList<>();
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
     * Adds an <code>Entry</code> object to the internal <code>ArrayList</code> within the <code>Account</code>
     * @param e the <code>Entry</code> to be added
     */
    public void addEntry(Entry e) {
        entryList.add(e);
    }

    /**
     * @param e the <code>Entry</code> to be removed from the <code>entryList</code>
     */
    public void deleteEntry(Entry e) {
        entryList.remove(e);
    }

    /**
     * @return  the <code>ArrayList</code> of entries
     */
    public ArrayList<Entry> getEntries() {
        return entryList;
    }

    /**
     *  Clears all the entries in <code>entryList</code>.
     */
    public void clearEntries() {
        entryList.clear();
    }
}
