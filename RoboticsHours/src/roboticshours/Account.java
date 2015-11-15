package roboticshours;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A class to organize hour entries.
 * This class contains an ID, name, and <code>entryList</code>,
 * an {@link ArrayList} used to store entries.
 *
 * @author Alan
 */
public class Account {

    private final int ID; //Primary key?
    private final String firstName;
    private final String lastName;
    private final ArrayList<Entry> entryList;
    private byte[] hashedCredentials;

    /**
     * Creates a new account
     * @param n  the user's first name
     * @param n2 the user's last name
     */
    public Account(String n, String n2) { //Constructor A: Users should use this constructor.
        ID = ImportExport.getNextID();
        firstName = n;
        lastName = n2;
        entryList = new ArrayList<>();
        hashedCredentials = null;
    }

    /**
     * Creates a new account
     * @param n  the user's first name
     * @param n2 the user's last name
     */
    public Account(String n, String n2, byte[] b) { //Constructor A: Users should use this constructor.
        ID = ImportExport.getNextID();
        firstName = n;
        lastName = n2;
        entryList = new ArrayList<>();
        hashedCredentials = Arrays.copyOf(b, b.length);
    }

    /**
     * Creates a new account and specifies the ID number to be used
     * @param i     custom ID number
     * @param n     the user's name
     * @param n2    the user's last name
     */
    public Account(int i, String n, String n2) { //Constructor B: This constructor should only be used by the program.
        ID = i;
        firstName = n;
        lastName = n2;
        entryList = new ArrayList<>();
        hashedCredentials = null;
    }

    /**
     * Creates a new account and specifies the ID number to be used
     * @param i     custom ID number
     * @param n     the user's name
     * @param n2    the user's last name
     */
    public Account(int i, String n, String n2, byte[] b) { //Constructor B: This constructor should only be used by the program.
        ID = i;
        firstName = n;
        lastName = n2;
        entryList = new ArrayList<>();
        hashedCredentials = Arrays.copyOf(b, b.length);
    }

    /**
     * @return  the user's name
     */
    public String getAccountName() {
        return firstName + "+" + lastName;
    }

    /*/**
     * @param n the new account name
     *
    public void changeAccountName(String n) {
        firstName = n;
    }*/ //Unused.

    /**
     * @return  the account ID
     */
    public int getAccountID() {
        return ID;
    }

    public void setHashedCredentials(byte[] b){
        hashedCredentials = b;
    }

    public byte[] getHashedCredentials(){
        return hashedCredentials;
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

    @Override
    public String toString(){
        return "ID: " + ID + " | Name: " + firstName + lastName;
    }
}
