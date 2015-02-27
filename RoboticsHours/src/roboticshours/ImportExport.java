package roboticshours;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

/**
 * This method hooks into the <code>Encryptor</code> method in order to provide data storage and retrieval methods.
 * 
 * 
 * 
 * @author Alan
 */
public class ImportExport { //File Structure:
    //ID hours name day month year day month year

    /**
     *  This method encrypts and exports all of the accounts in the Account list into /data/.
     */
    
    public static void exportAll(){
        new File("data").mkdir(); //Attempt to make the directory /data/ in RoboticsHours
        
        for (Account a : Run.getAccountList()) { //For each account in the accountlist maintained by Run.java
            exportSingleFile(a);
        }
    }
    
    /**
     *
     * @param a
     */
    public static void exportSingleFile(Account a){
        String id = a.getAccountID() < 10 ? "0" + a.getAccountID() : "" + a.getAccountID();//If account ID = 1-9, make 01-09, else simply print id
            try{
                Files.deleteIfExists(FileSystems.getDefault().getPath("data", id + ".dat"));//Deletes existing file if it exists
            }
            catch(IOException e){
                System.out.println("IO Error: " + e);
            }
            
            writeDataToFile(a, new File("data/" + id + ".dat")); //writeData from account A to a new File /data/$ID.dat
    }
    
    /**
     *
     * @param a
     * @param f
     */
    public static void writeDataToFile(Account a, File f){ //Writes the Entry data from an Account to a File. File will most likely have filename Username/ID.dat
        
        try {
            Files.createFile(FileSystems.getDefault().getPath("data", f.getName()));
            //Encryptor.makeKey(Encryptor.getKey());
            try(PrintWriter out = new PrintWriter(f)){
                
                out.println(Arrays.toString(Encryptor.getKey()));
                
                out.println(Arrays.toString(Encryptor.encrypt(a.getAccountName().getBytes())));
                
                System.out.println("These are the entries' toStrings being encrypted and written to the file.");
                for(Entry e : a.getEntries()){
                    System.out.println(e.toString());
                    out.println(Arrays.toString(Encryptor.encrypt(e.toString().getBytes())));
                }
                System.out.println("~~~~");
            } catch (IllegalBlockSizeException | BadPaddingException e) {}
        }
        catch (IOException e) {
            System.out.println("IO Error: " + e);
        }
    }
    
    /**
     *  Imports all relevant encrypted data from the files in /data/.
     */
    public static void importAll(){ //TODO: Change array positions in code due to addition of name field to Entry.toString();
        
        for (File f : new File("data").listFiles()) { //For each File in /data/
            try(BufferedReader br = new BufferedReader(new FileReader(f))){
                br.readLine();  //Discarding the hashed key TODO Make this actually make a keyspec orz
                
                String arrayRepresentation = br.readLine();
                String[] byteStrings = arrayRepresentation.substring(1, arrayRepresentation.length() - 1).split(", ");
                byte[] nameBytesToDecrypt = new byte[byteStrings.length];
                for(int i = 0; i < nameBytesToDecrypt.length; i++){
                    nameBytesToDecrypt[i] = (byte)Integer.parseInt(byteStrings[i]);
                }
                
                try{
                    Run.getAccountList().add(
                            new Account(
                            Integer.parseInt(f.getName().replaceAll("[^0-9]", "")), //ID
                            new String(Encryptor.decrypt(nameBytesToDecrypt))) //Name
                    );
                    Account.incrementNumberOfAccounts();
                }
                catch(NumberFormatException | IllegalBlockSizeException | BadPaddingException e){}
            }
            
            catch (IOException e) {
                System.out.println("ReadLine for Account Creation in importData failed: " + e);
            }
            readData(f); //for each file, read the data and store in the account created above's entryList
        } 
    }
    
    /**
     *
     * @param f
     */
    public static void importSingleFile(File f){
        
        try(BufferedReader br = new BufferedReader(new FileReader(f))){
                
            br.readLine();
            
            String arrayRepresentation = br.readLine();
            String[] byteStrings = arrayRepresentation.substring(1, arrayRepresentation.length() - 1).split(", ");
            byte[] hashBytesToDecrypt = new byte[byteStrings.length];
            for(int i = 0; i < hashBytesToDecrypt.length; i++){
                hashBytesToDecrypt[i] = (byte)Integer.parseInt(byteStrings[i]);
            }
            
            try{
                Run.getAccountList().add(
                        new Account(
                        Integer.parseInt(f.getName().replaceAll("[^0-9]", "")), //ID
                        new String(Encryptor.decrypt(hashBytesToDecrypt))) //Name
                );
                Account.incrementNumberOfAccounts();
            }
            catch(NumberFormatException | IllegalBlockSizeException | BadPaddingException e){}
        }
            
        catch (IOException e) {
            System.out.println("ReadLine for Account Creation in importData failed: " + e);
        }
        readData(f); //for each file, read the data and store in the account created above's entryList 
    }
    
    /**
     *
     * @param f
     */
    public static void readData(File f){ //Takes a file f, reads the entries, puts them into the entryLists of Accounts.
        
        String[] byteString;
        byte[] toDecrypt;
        final ArrayList<String> stringsIn = new ArrayList<>();
        
        String line;
        try(BufferedReader br = new BufferedReader(new FileReader(f))){
            br.readLine(); //Discarding hashed key
            br.readLine(); //Discarding encrypted name
            while ((line = br.readLine()) != null) {
                byteString = line.substring(1, line.length() - 1).split(", "); //While the read line stored in Line != null, add it to the linesIn<String> ArrayList.
                toDecrypt = new byte[byteString.length];
                for(int i = 0; i < byteString.length; i++){
                    toDecrypt[i] = (byte)Integer.parseInt(byteString[i]);
                    
                }
                stringsIn.add(new String(Encryptor.decrypt(toDecrypt)));
            }
        }
        catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
            System.out.println("readData IO Exception: " + e);
        }

        String[][] toEntry = new String[stringsIn.size()][20]; //2D Array toEntry is a new String Array with linesIn.size() rows and 18 (number of data entries per Entry.toString) columns.

        for (int i = 0; i < stringsIn.size(); i++) { //For each line in LinesIn,
            toEntry[i] = stringsIn.get(i).split("[: ]"); // each row in toEntry = linesIn.get line i and regex split it at NOT number/letter (spaces, colon)
        }
        //ID hours day1 month1 date1 hour1:minute1:second1 timeZone1 year1 day2 month2 date2 hour2:minute2:second2 timeZone2 year2
        for (String[] s : toEntry) {  //For each row array of data Strings in the toEntry table(2D array)
            int id = Integer.parseInt(s[0]) - 1; //The ID of the account will be the first data value(ID) - 1 due to 0 base
            Calendar date = new GregorianCalendar(Integer.parseInt(s[5]), //Create a new GregorianCalendar with year for field date in Entry
                    Integer.parseInt(s[4]), //month -> int(!)
                    Integer.parseInt(s[3]));//date
            date.setLenient(false); //probably unnecessary; but disallows Jan 32 -> Feb 1 conversion

            Calendar dateCreated = new GregorianCalendar(Integer.parseInt(s[8]), //same as above, but for the field dateCreated in Entry
                    Integer.parseInt(s[7]),
                    Integer.parseInt(s[6]));
            dateCreated.setLenient(false);

            Run.getAccountList().get(0) //Get account at given ID for each string[] representation of Entry in toEntry[][]
                    .addEntry(new Entry( //Add a new entry to the Account
                    Run.getAccountList().get(0), //with the account from the given ID
                    Integer.parseInt(s[2]), //Hours -> parseInt
                    date, //GregorianCalendar date
                    dateCreated //GregorianCalendar dateCreated
                    )); //Constructor B for Entry
        }
        
        /*for(Account a : accountList){
         for(Entry e : a.getEntries()){
         System.out.println(e.toString());
         } //Debug, testing printing of entries from each account in accountList
         }*/

        stringsIn.clear();
    } //readData(File f)
}