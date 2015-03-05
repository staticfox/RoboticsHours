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
            try(PrintWriter out = new PrintWriter(f)){
                
                out.println(Arrays.toString(a.getHashedCredentials()));
                
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
        
        byte[] input = new byte[32]; //Because this method deals with ALL the keys from ALL the files, storing the input in an immutable String is a BAD IDEA, even if it would make programming easier.
        int[] bytes = new int[4];
        int store = 0;
        
        int inputCounter = 0;
        int bytePositionCounter = 0;
        File[] fileList = new File("data").listFiles();
        Arrays.sort(fileList);
        for (File f : fileList) { //For each File in /data/
            try(BufferedReader br = new BufferedReader(new FileReader(f))){
                while(true){
                    int i = br.read();
                    System.out.println(i);
                    if(i == 44 || i == 93){ //comma value separator or end bracket ]
                        switch(bytePositionCounter){
                            case 4:
                                store = ((100 + 10 * bytes[2] + bytes[3]) * -1);
                                break;
                            case 3:
                                if(bytes[0] == -3){
                                    store = ((10 * bytes[1] + bytes[2]) * -1);
                                }
                                else{
                                    store = (100 + 10 * bytes[1] + bytes[2]);
                                }
                                break;
                            case 2:
                                if(bytes[0] == -3){
                                    store = (bytes[1] * -1);
                                }
                                else{
                                    store = (10 * bytes[0] + bytes[1]);
                                }
                                break;
                            case 1:
                                store = bytes[0];
                                break;
                        }
                        System.out.println("Char complete: " + store);
                        input[inputCounter] = (byte) store;
                        inputCounter++;
                        if(i == 44) br.read(); //dispose of space
                        bytePositionCounter = 0;
                        Arrays.fill(bytes, 0);
                    }
                    else if(i == 13){ //carriage return
                        br.read();
                        break;
                    }
                    else if(i == 91); // [ or ]; discard
                    else{
                        bytes[bytePositionCounter] = i - 48;
                        bytePositionCounter++;
                    }
                }
                
                inputCounter = 0;
                
                Encryptor.makeKey(input);
                
                Arrays.fill(input, (byte)0);
                
                String arrayRepresentation = br.readLine();
                String[] byteStrings = arrayRepresentation.substring(1, arrayRepresentation.length() - 1).split(", ");
                byte[] nameBytesToDecrypt = new byte[byteStrings.length];
                for(int i = 0; i < nameBytesToDecrypt.length; i++){
                    nameBytesToDecrypt[i] = (byte)Integer.parseInt(byteStrings[i]);
                }
                
                try{
                    Run.addAccount(new Account(
                        Integer.parseInt(f.getName().replaceAll("[^0-9]", "")), //ID
                        new String(Encryptor.decrypt(nameBytesToDecrypt)).split("[+]")[0], 
                        new String(Encryptor.decrypt(nameBytesToDecrypt)).split("[+]")[1],
                        input) //Name
                    );
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
    public static void importSingleFile(File f){ //TODO Test the single method
        
        byte[] input = new byte[32]; //Because this method deals with ALL the keys from ALL the files, storing the input in an immutable String is a BAD IDEA, even if it would make programming easier.
        int[] bytes = new int[4];
        int store = 0;
        
        int inputCounter = 0;
        int bytePositionCounter = 0;
        
        try(BufferedReader br = new BufferedReader(new FileReader(f))){
            while(true){
                int i = br.read();
                System.out.println(i);
                if(i == 44 || i == 93){ //comma value separator or end bracket ]
                    switch(bytePositionCounter){
                        case 4:
                            store = ((100 + 10 * bytes[2] + bytes[3]) * -1);
                            break;
                        case 3:
                            if(bytes[0] == -3){
                                store = ((10 * bytes[1] + bytes[2]) * -1);
                            }
                            else{
                                store = (100 + 10 * bytes[1] + bytes[2]);
                            }
                            break;
                        case 2:
                            if(bytes[0] == -3){
                                store = (bytes[1] * -1);
                            }
                            else{
                                store = (10 * bytes[0] + bytes[1]);
                            }
                            break;
                        case 1:
                            store = bytes[0];
                            break;
                    }
                    System.out.println("Char complete: " + store);
                    input[inputCounter] = (byte) store;
                    inputCounter++;
                    if(i == 44) br.read(); //dispose of space
                    bytePositionCounter = 0;
                    Arrays.fill(bytes, 0);
                }
                else if(i == 13){ //carriage return
                    br.read();
                    break;
                }
                else if(i == 91); // [ or ]; discard
                else{
                    bytes[bytePositionCounter] = i - 48;
                    bytePositionCounter++;
                }
            }

            inputCounter = 0;

            Encryptor.makeKey(input);
            
            Arrays.fill(input, (byte)0);
            
            String arrayRepresentation = br.readLine();
            String[] byteStrings = arrayRepresentation.substring(1, arrayRepresentation.length() - 1).split(", ");
            byte[] nameBytesToDecrypt = new byte[byteStrings.length];
            for(int i = 0; i < nameBytesToDecrypt.length; i++){
                nameBytesToDecrypt[i] = (byte)Integer.parseInt(byteStrings[i]);
            }
            
            try{
                Run.addAccount(new Account(
                        Integer.parseInt(f.getName().replaceAll("[^0-9]", "")), //ID
                        new String(Encryptor.decrypt(nameBytesToDecrypt)).split("[+]")[0], 
                        new String(Encryptor.decrypt(nameBytesToDecrypt)).split("[+]")[1],
                        input) //Name
                );
            }
            catch(NumberFormatException | IllegalBlockSizeException | BadPaddingException e){}
        }
            
        catch (IOException e) {
            System.out.println("ReadLine for Account Creation in importData failed: " + e);
        }
        readDataSingle(f); //for each file, read the data and store in the account created above's entryList 
    }
    
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
            int id = Integer.parseInt(s[0]); //The ID of the account will be the first data value(ID) - 1 due to 0 base
            Calendar date = new GregorianCalendar(Integer.parseInt(s[5]), //Create a new GregorianCalendar with year for field date in Entry
                    Integer.parseInt(s[4]), //month -> int(!)
                    Integer.parseInt(s[3]));//date
            date.setLenient(false); //probably unnecessary; but disallows Jan 32 -> Feb 1 conversion

            Calendar dateCreated = new GregorianCalendar(Integer.parseInt(s[8]), //same as above, but for the field dateCreated in Entry
                    Integer.parseInt(s[7]),
                    Integer.parseInt(s[6]));
            dateCreated.setLenient(false);

            Run.getAccount(id) //Get account at given ID for each string[] representation of Entry in toEntry[][]
                    .addEntry(new Entry( //Add a new entry to the Account
                    Run.getAccount(id), //with the account from the given ID
                    Integer.parseInt(s[2]), //Hours -> parseInt
                    date, //GregorianCalendar date
                    dateCreated //GregorianCalendar dateCreated
                    )); //Constructor B for Entry
        }
        stringsIn.clear();
    }
    
    /**
     *
     * @param f
     */
    public static void readDataSingle(File f){ //Takes a file f, reads the entries, puts them into the entryLists of Accounts.
        
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
            int id = Integer.parseInt(s[0]); //The ID of the account will be the first data value(ID) - 1 due to 0 base
            Calendar date = new GregorianCalendar(Integer.parseInt(s[5]), //Create a new GregorianCalendar with year for field date in Entry
                    Integer.parseInt(s[4]), //month -> int(!)
                    Integer.parseInt(s[3]));//date
            date.setLenient(false); //probably unnecessary; but disallows Jan 32 -> Feb 1 conversion

            Calendar dateCreated = new GregorianCalendar(Integer.parseInt(s[8]), //same as above, but for the field dateCreated in Entry
                    Integer.parseInt(s[7]),
                    Integer.parseInt(s[6]));
            dateCreated.setLenient(false);

            Run.getAccount(id) //Get account at given ID for each string[] representation of Entry in toEntry[][]
                    .addEntry(new Entry( //Add a new entry to the Account
                    Run.getAccount(id), //with the account from the given ID
                    Integer.parseInt(s[2]), //Hours -> parseInt
                    date, //GregorianCalendar date
                    dateCreated //GregorianCalendar dateCreated
                    )); //Constructor B for Entry
        }
        stringsIn.clear();
    }
    
    public static int getNextID(){
        int ID = 1;
        while(true){
            if(ID < 10){
                 if(new File("data/0" + ID + ".dat").exists())  ID++;
                 else return ID;
            }
            else if(ID > 9){
                if(new File("data/" + ID + ".dat").exists()) ID++;
                else return ID;
            }
        }
    }
}