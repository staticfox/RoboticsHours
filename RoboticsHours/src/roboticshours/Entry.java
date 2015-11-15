package roboticshours;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * A class to store data on robotics members' working hours
 * This class is pretty much the core of this project.
 * Each <code>Entry</code> contains the {@link Account} object of the user it belongs to,
 * an integer number of hours worked,
 * the date that the work occurred on,
 * and the date that the entry was last modified.
 * The two dates are stored using {@link GregorianCalendar} objects.
 *
 * @author Alan
 */
public class Entry {

    private final Account user;
    private int hours;
    private Calendar date;
    private Calendar dateModified;

    /**
     * Creates an <code>Entry</code> object
     * @param u the user associated with the entry
     * @param h the number of hours
     * @param c the date that work was performed
     */
    public Entry(Account u, int h, Calendar c) { //Constructor A: Users
        user = u;
        hours = h;
        date = c;
        dateModified = new GregorianCalendar();
    }

    /**
     * Creates an <code>Entry</code> object and specifies the <code>dateModified</code>
     * @param u     the user associated with the entry
     * @param h     the number of hours
     * @param c1    the date that work was performed
     * @param c2    manually sets the <code>dateModified</code>
     */
    public Entry(Account u, int h, Calendar c1, Calendar c2) { //Constructor B: Program
        user = u;
        hours = h;
        date = c1;
        dateModified = c2;
    }

    /**
     * @return  the associated user's <code>Account</code> object
     */
    public Account getUser() {
        return user;
    }

    /**
     * @return  the number of hours worked
     */
    public int getHours() {
        return hours;
    }

    /**
     * Manually sets the number of hours performed.
     * This method modifies the <code>dateModified</code>.
     * @param h number of hours
     */
    public void setHours(int h){
        hours = h;
        dateModified = new GregorianCalendar();
    }

    /**
     * @return  the date that hours were worked
     */
    public Calendar getDate() {
        return date;
    }

    /**
     * Modifies the <code>date</code> and updates the <code>dateModified</code>.
     * @param d the new date
     */
    public void setDate(Calendar d){
        date = d;
        dateModified = new GregorianCalendar();
    }

    /**
     * @return  the date that the code was last modified
     */
    public Calendar getDateModified() {
        return dateModified;
    }

    @Override
    public String toString() {
        return user.getAccountID() + " " + user.getAccountName() + " " + hours + " " +
                date.get(Calendar.DAY_OF_MONTH) + " " + date.get(Calendar.MONTH) + " " + date.get(Calendar.YEAR) + " " +
                dateModified.get(Calendar.DAY_OF_MONTH) + " " + dateModified.get(Calendar.MONTH) + " " + dateModified.get(Calendar.YEAR);
    }
}
