package roboticshours;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

/**
 * A JFrame to handle login, authentication, and decryption operations.
 * This will be the first thing the user sees.
 * This screen is a basic login screen visually,
 * with a username field, password field, and a login button.
 * The complexity of this screen comes in the authentication logic.
 * 
 * @author Alan
 */
public class LoginScreen extends JFrame implements KeyListener { //LoginScreen is a JFrame that also has a KeyListener listening for VK_ENTER.
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private static CustomFocusTraversalPolicy policy; //Declares a FocusTraversalPolicy that defines the order in which focus is passed within the JFrame login screen.

    /**
     *  Initializes the components in the LoginScreen.
     */
    public LoginScreen() {
        initComponents();//The constructor creates the JFrame.
    }

    private void initComponents() {

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JLabel usernameLabel = new JLabel();    //Creates a JLabel usernameLabel
        usernameLabel.setText("Username");      //Sets the text on the JLabel to "Username".
        usernameLabel.setFocusable(false);      //Prevents program from focusing on the labels. Probably unnecesary, considering the CustomFocusTraversalPolicy

        JLabel passwordLabel = new JLabel();    //Creates a JLabel passwordLabel
        passwordLabel.setText("Password");
        passwordLabel.setFocusable(false);

        usernameField = new JTextField();

        passwordField = new JPasswordField(){
            @Override
            public void paste(){}
        };
        passwordField.addKeyListener(this); //KeyListener allows VK_ENTER to "press" the login button
        
        loginButton = new JButton();
        loginButton.setText("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });
        loginButton.addKeyListener(this);

        ArrayList<Component> order = new ArrayList<>(3); //Setting the order of components for IRRITATING FocusTraversalPolicy
        order.add(usernameField);
        order.add(passwordField);
        order.add(loginButton);

        policy = new CustomFocusTraversalPolicy(order);//construct the FocusTraversalPolicy
        this.setFocusTraversalPolicy(policy); //Actually set the FocusTraversalPolicy

	/*<NetBeans Generated Layout Code>*/

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(passwordLabel)
                    .addComponent(usernameLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(passwordField, GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                    .addComponent(usernameField))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(loginButton)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(usernameLabel)
                    .addComponent(usernameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordLabel)
                    .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(loginButton)
                .addGap(25, 25, 25))
        );

        /*<End NetBeans Generated Layout Code>*/

        pack();
    }

   private void loginButtonActionPerformed(ActionEvent evt) { //This describes the actions that are taken when the login button or any of its aliases are pressed, e.g. the process of logging in.
        
        ArrayList<byte[]> authList = new ArrayList<>(); //ArrayList of byte arrays from the files
        File[] fileList = new File("data").listFiles(); //Array of Files
        
        Arrays.sort(fileList); //Ensuring 01, 02 ... order
        
        for(File f : fileList){
            try (Scanner cs = new Scanner(f)){
                String arrayRepresentation = cs.nextLine(); //Get the first line; it's the key bytes.
                String[] byteStrings = arrayRepresentation.substring(1, arrayRepresentation.length() - 1).split(", "); //Extract all but character 0 and last
                byte[] authBytes = new byte[byteStrings.length]; //Make a new array as long as the array of strings
                for(int i = 0; i < authBytes.length; i++){ //For the length of the array,
                    authBytes[i] = (byte)Integer.parseInt(byteStrings[i]); //Parse the byte into authBytes.
                }
                authList.add(authBytes); //Finally, add authBytes to the list.
            }
            catch (FileNotFoundException ex) {
                System.out.println("File not found for iterator loginButtonActionPerformed.");
            }
        } //Now the Credential List is full of credentials. byte[]
        
        byte[] username = usernameField.getText().getBytes();
        byte[] password = Encryptor.charToByte(passwordField.getPassword()); //Get Credential's components from login screen's username/password fields.
        passwordField.setText(null); //Clearing the password field.
        
        byte[] credentials = new byte[username.length + password.length]; //Creating a combined credential array that is the length of username + password
        System.arraycopy(username, 0, credentials, 0, username.length); //Copying from username at 0 to credentials at 0 for all bytes in username.
        System.arraycopy(password, 0, credentials, username.length, password.length); //Copying from password at 0 to credentials at end of username for all bytes in password.
        
        Arrays.fill(username, (byte)0); //Clear username
        
        Arrays.fill(password, (byte)0); //Clear password
        
        byte[] hashedCredentials = Encryptor.hash(credentials, 1024); //Hashes the byte representation of credentials using static salt and 1024 iterations.
        
        Arrays.fill(credentials, (byte)0); //Clear credentials
        
        boolean validCredentials = false; //Prep for authentication
        boolean admin = false; //This is a dangerous value.
        File file = null; //Selected file
        
        if(/*Arrays.equals(credentials, adminCredentials) || */true){ //SO DANGEROUS
            admin = true; //(!!!)
        }
        else{
            for(byte[] b : authList){
                if(Arrays.equals(b, hashedCredentials)){ //If the selected byte[] from authList matches the byte[] hashedcredentials...
                    validCredentials = true; //Yay, valid login
                    file = fileList[authList.indexOf(b)]; //Sets file equal to file at the index of the matching credential from credentialList
                }
            }
        }
        if(admin){
            ImportExport.importAll();
            Run.hideLoginScreen();
            java.awt.EventQueue.invokeLater(() -> {
                new AdminScreen().setVisible(true);
            });
        }
        else if(validCredentials){ //IF valid login
            Encryptor.makeKey(hashedCredentials);
            ImportExport.importSingleFile(file);
            System.out.println("Valid login detected. Entries imported.");
            System.out.println(Run.getAccountList().get(0).getEntries());
            System.out.println("~~~~");
            Run.hideLoginScreen();
            java.awt.EventQueue.invokeLater(() -> {
                new MainScreen(Run.getAccountList().get(0)).setVisible(true);
            });
        }
        else{
            passwordField.setText("");
            JOptionPane.showMessageDialog(rootPane, "Invalid Username or Password!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     *
     * @param ke
     */
    @Override
    public void keyTyped(KeyEvent ke) {} //On key typed, do nothing.

    /**
     *
     * @param ke
     */
    @Override
    public void keyReleased(KeyEvent ke) {} //On ley released, do nothing.
    
    /**
     *
     * @param ke
     */
    @Override
    public void keyPressed(KeyEvent ke) { //On key pressed, if key pressed is enter key, send login button action performed
        if(ke.getKeyCode() == KeyEvent.VK_ENTER){
            loginButtonActionPerformed(null);
        }
    }

    /**
     *  Creates a FocusTraversalPolicy to define the order in which tab inputs shift focus.
     */
    public static class CustomFocusTraversalPolicy extends FocusTraversalPolicy //Making a focus traversal policy to fix the tab order
    {
        ArrayList<Component> order;

        /**
         *
         * @param o
         */
        public CustomFocusTraversalPolicy(ArrayList<Component> o) {
            order = (ArrayList<Component>)o.clone(); //Sets order equal to the arrayList of components supplied
        }
        
        /**
         *
         * @param focusCycleRoot
         * @param aComponent
         * @return
         */
        @Override
        public Component getComponentAfter(Container focusCycleRoot, Component aComponent){
            return order.indexOf(aComponent) == 2 ? order.get(0) : order.get(order.indexOf(aComponent) + 1); //If component index is 2, get at index 0 otherwise get at index + 1
        }

        /**
         *
         * @param focusCycleRoot
         * @param aComponent
         * @return
         */
        @Override
        public Component getComponentBefore(Container focusCycleRoot, Component aComponent){
            return order.indexOf(aComponent) == 0 ? order.get(2) : order.get(order.indexOf(aComponent) - 1); //If component index is 0, get at index 2 otherwise get at index - 1
        }

        /**
         *
         * @param focusCycleRoot
         * @return
         */
        @Override
        public Component getDefaultComponent(Container focusCycleRoot) {
            return order.get(0); //Default component is usernameField
        }

        /**
         *
         * @param focusCycleRoot
         * @return
         */
        @Override
        public Component getLastComponent(Container focusCycleRoot) {
            return order.get(2); //Last component is loginButton
        }

        /**
         *
         * @param focusCycleRoot
         * @return
         */
        @Override
        public Component getFirstComponent(Container focusCycleRoot) {
            return order.get(0); //First component is usernameField
        }
    }
}