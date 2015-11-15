package roboticshours;

import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * A class to handle encryption operations for this project.
 * This class utilizes AES symmetric encryption and the SHA-256 hash algorithm.
 *
 * Personal note: This class is my first foray into the weird, weird world of Java's crypto platform.
 *
 * @author Alan
 */
public class Encryptor{

    private static Cipher cipher;
    private static MessageDigest sha;
    private static SecretKeySpec keySpec;
    private static byte[] hashedCredentials;


    static{
       try{
            cipher = Cipher.getInstance("AES");
            sha = MessageDigest.getInstance("SHA-256");
        }
        catch(NoSuchAlgorithmException | NoSuchPaddingException e){
            System.out.println("Apparently AES or SHA-256 don't exist.");
        }
    } //Ensures instantiation of Cipher and MessageDigest BEFORE method execution.

    /**
     * Creates a {@link SecretKeySpec} in order to initialize the {@link Cipher} object for crypto.
     * This method stores the <code>preKey</code> array in the instance variable <code>hashedCredentials</code>,
     * hashes the <code>preKey</code> array using 1024 rounds of SHA-256,
     * and takes the first 16 bytes of the hashed array to create a {@link SecretKeySpec}.
     * @param preKey a byte array used to create the <code>keySpec</code>
     */
    public static void makeKey(byte[] preKey){
        hashedCredentials = preKey; //Stores a copy of the pre-key byte array
        byte[] key = hash(preKey, "XpmynaaIhf3boZhsr47BRbVOsJNLpOTO2ZfpkjghRoHM1fmwo12bQQyfT4STV3vx", 1024); //Hashes the pre-key with a static random hash 1024 times for good measure
        key = Arrays.copyOf(key, 16); //Takes the first 16 bytes from the key material
        keySpec = new SecretKeySpec(key, "AES"); //Creates AES key spec from credentials' bytes after 2 rounds (hopefully) of 1024x salted hash
    }

    /**
     * @return  a byte array representation of the key material.
     */
    public static byte[] getKey(){
        return hashedCredentials;
    }

    /**
     *  Sets the Cipher to encryption mode.
     */
    public static void preEncrypt(){
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec); //Initializes AES cipher to encrypt using hashed key
        } catch (InvalidKeyException e) {
            System.out.println(e + "Location: preEncrypt");
        }
    }

    /**
     *  Sets the Cipher to decryption mode using <code>keySpec</code>
     */
    public static void preDecrypt(){
        try {
            cipher.init(Cipher.DECRYPT_MODE, keySpec); //Initializes AES cipher to encrypt using hashed key
        } catch (InvalidKeyException e) {
            System.out.println(e + "Location: preDecrypt");
        }
    }

    /**
     * Encrypts a byte array.
     * This method sets the Cipher to encrypt mode before encrypting.
     *
     * @param b The byte array to be encrypted
     * @return  The encrypted byte array
     * @throws IllegalBlockSizeException    Thrown if the provided data does not match the block size of AES (16)
     * @throws BadPaddingException          Thrown if the padding on the provided data is incorrect. (Check the key)
     */
    public static byte[] encrypt(byte[] b) throws IllegalBlockSizeException, BadPaddingException{
        preEncrypt();
        return cipher.doFinal(b);
    }

    /**
     * Encrypts a byte array.
     * This method sets the Cipher to decrypt mode before decrypting.
     *
     * @param b The byte array to be decrypted
     * @return  The decrypted byte array
     * @throws IllegalBlockSizeException    Thrown if the provided data does not match the block size of AES (16)
     * @throws BadPaddingException          Thrown if the padding on the provided data is incorrect. (Check the key)
     */
    public static byte[] decrypt(byte[] b) throws IllegalBlockSizeException, BadPaddingException{
        preDecrypt();
        return cipher.doFinal(b);
    }

    //TODO
    //Note to self: Pick a hash method and stick with it. Don't leave unimplemented methods.

    /**
     * Hashes a byte array with a static random salt for a given number of iterations.
     *
     * @param b             The byte array to be hashed
     * @param iterations    The number of rounds to hash the byte array
     * @return              The hashed byte array
     */
    public static byte[] hash(byte[] b, int iterations){

        byte[] salt = "usOLwyhs8n1A5uenhr9RUZDBQCcOcvPhZv2PTHLOdeOs3V8cIEMZFdW8ihMO8Vfm".getBytes(); //Fixed salt

        for(int i = 0; i < iterations; i++){
            byte[] temp = new byte[b.length + salt.length]; //Makes a new array.
            System.arraycopy(b, 0, temp, 0, b.length);      //Copies b to the temporary array.
            System.arraycopy(salt, 0, temp, b.length, salt.length); //Appends the salt to the temporary array.
            b = sha.digest(temp);   //Sets the byte array to the SHA digest of the array with appended salt.
        }

        return b;
    }

    /**
     * Hashes a byte array with a provided salt for a given number of iterations.
     * This method will most likely not be used.
     * @param b             The byte array to be hashed
     * @param salt          The provided salt. It is recommended that this be random.
     * @param iterations    The number of rounds to hash the byte array
     * @return              The hashed byte array
     */
    public static byte[] hash(byte[] b, String salt, int iterations){

        byte[] saltBytes = salt.getBytes();

        for(int i = 0; i < iterations; i++){
            byte[] temp = new byte[b.length + saltBytes.length];
            System.arraycopy(b, 0, temp, 0, b.length);
            System.arraycopy(saltBytes, 0, temp, b.length, saltBytes.length);
            b = sha.digest(temp);
        }

        return b;
    }

    /*public static byte[] hash(byte[] b, long seed, int iterations){

        //Generate a new salt every time
        char[] salt = null;

        for(int i = 0; i < iterations; i++){
            byte[] temp = new byte[b.length + salt.length];
            System.arraycopy(b, 0, temp, 0, b.length);
            System.arraycopy(salt, 0, temp, b.length, salt.length);
            b = temp;
        }

        return b;
    } //If there is extra time, implement new salts each hash using a seeded PSRNG whose IV is generated by a PSRNG and stored in the user's file.
    */

    /**
     * Converts a char array to a byte array.
     * Literally just goes through each index of the char array and
     * casts each char as a byte before storing it in the byte array.
     * @param c The char array to be converted
     * @return  A converted byte array
     */
    public static byte[] charToByte(char[] c){
        byte[] b = new byte[c.length];
        for(int i = 0; i < c.length; i++){
            b[i] = (byte)c[i];
            c[i] = (char)0;
        }
        return b;
    }
    /**
     * Converts a byte array to a char array.
     * Literally just goes through each index of the byte array and
     * casts each byte as a char before storing it in the char array.
     * @param b The byte array to be converted
     * @return  A converted char array
     */
    public static char[] byteToChar (byte[] b){
        char[] c = new char[b.length];  //Create a new char array that's the same size as the byte array
        for(int i = 0; i < b.length; i++){
            c[i] = (char)b[i];  //Set each index of the char array to the char representation of the byte array.
            b[i] = (byte)0;     //Clear the byte array.
        }
        return c;
    }
}
