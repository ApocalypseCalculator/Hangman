/*
[redacted]
 */
import java.util.*;
//import Java's utility library including data structures
//such as HashMap, Collections, ArrayList, Stack, etc.
import java.awt.*;
//import java's AWT library for colours etc.
import java.awt.image.*;
//import the awt Image library for drawing images and loading images
import java.io.*;
//Input/Output library for quick IO
import javax.imageio.*;
//Image IO to read assets from folder
import java.net.*;
//networking library for remote server connection (optional usage)
//import javax.net.ssl.*;
//enables the use of SSL on web server connections (optional usage)
//unable to import to RTP because of classpath conflicts
import hsa.Console;
//import hsa library specifically Console to give output
//specifying console instead of hsa.* to resolve ambiguous import
import javax.sound.sampled.*;
//sound library (optional usage)
import javax.swing.*;
//Swing GUI library for jFrame, jDialog, jFileChooser etc.

/*
[redacted]
 */

public class Game {

    private Console c;
    private final static String configPath = "./data/config.hangman";
    private HashMap cache = new HashMap();
    private JFrame errors;

    /*Global Variable Dictionary
    ----------------------------------------------------------------------------
    Type    | Name             | Desription
    ----------------------------------------------------------------------------
    Console | c                | Console variable to create output
            |                  | and draw graphics
    ----------------------------------------------------------------------------
    String  | configPath       | The relative path to the configuration file
            |                  | Used to intialize settings, wordbank, etc.
    ----------------------------------------------------------------------------
    HashMap | cache            | All-in-one variable that maps important
            |                  | content to it's respective names
            |                  | includes word bank, settings, session data, etc.
    ----------------------------------------------------------------------------
    JFrame  | errors           | The JFrame element used for error dialogs
            |                  | imported class from javax.swing.*
    --------------------------------------------------------------------------*/
    public Game() { //class constructor
        /*
        Method description
        
        This method constructs an instance of the Game class, where the user
        can run this hangman game
         */
 /*Method Variable Dictionary
    ----------------------------------------------------------------------------
    No variables used
    --------------------------------------------------------------------------*/

        c = new Console("Hangman Game");
    }

    public static void main(String[] args) { //main method
        /*
        Method description
        
        This method is the driving code that allows the game to function, 
        it calls splashscreen , intializes the cache, and then 
        displays an animation and goes to the menu, where execution picks up
         */
 /*Method Variable Dictionary
    ----------------------------------------------------------------------------
    Type    | Name             | Desription
    ----------------------------------------------------------------------------
    Game    | d                | An instance of the Game class that runs the
            |                  | game session
    --------------------------------------------------------------------------*/
        Game d = new Game();
        d.splashScreen();
        d.initialize();
        if (((String) ((HashMap) d.cache.get("config")).get("firstrun")).equals("1")) {
            d.firstRun();
        }
        d.mainMenu();
    }

    private void mainMenu() { // method for the main game menu
        /*
        Method description
        
        This method displays the main menu, and provides choices for the user
        to play the game, view about page, etc etc.
         */
 /*Method Variable Dictionary
    ----------------------------------------------------------------------------
    Type    | Name             | Desription
    ----------------------------------------------------------------------------
    String  | choiceNum        | Represents the input provided by user
    ----------------------------------------------------------------------------
    int     | choice           | Integer value of choice
    ----------------------------------------------------------------------------
    long    | latency          | Long integer representing internet connection
            |                  | latency measured in milliseconds
    ----------------------------------------------------------------------------
    BufferedImage | wifiIcon   | Image of wifi icon representing current
            |                  | state of internet connectivity
    --------------------------------------------------------------------------*/
        String choiceNum = ""; //intialized to prevent compilation warnings
        int choice = 0;
        c.clear();
        c.setColor(Color.black);

        /*GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage temp = (BufferedImage) ((HashMap) cache.get("assets")).get("mainbackground");
        VolatileImage retVal = gc.createCompatibleVolatileImage( temp.getWidth(), temp.getHeight());
        Graphics2D g2d = retVal.createGraphics();
        g2d.drawImage( temp, 0, 0, null );
        g2d.dispose();*/
 /*
        drawImage here is broken for some reason, 
        on the second call for mainmenu, cpu usage spikes, and screen freezes
        for about 10 seconds.
        and the image is not drawn on the graphics canvas
        Using hardware acceleration with VolatileImage (implementation commented
        out above)  does not solve the problem,
        and neither does allocating more memory/vram to the JVM
        therefore it's probably an issue with the hsa library
         */
        if (!((Boolean) cache.get("mainMenuCalled")).booleanValue()) {
            c.drawImage((BufferedImage) ((HashMap) cache.get("assets")).get("mainbackground"), 0, 0, null);
            cache.put("mainMenuCalled", new Boolean(true));
            //prevent the 10 second glitch by not drawing background on subsequent runs
        }
        //write options on screen
        c.setColor(new Color(50, 205, 50));
        c.setFont(new Font("Times New Roman", 1, 35));
        c.drawString("Main Menu", 240, 60);
        c.setFont(new Font("Times New Roman", 1, 23));
        c.drawString("Enter the corresponding number:", 160, 120);
        c.setFont(new Font("Times New Roman", 1, 25));
        c.drawString("1. Play", 280, 180);
        c.drawString("2. High Scores", 250, 210);
        c.drawString("3. Settings", 260, 240);
        c.drawString("4. About", 270, 270);
        c.drawString("5. Quit", 280, 300);
        c.setFont(new Font("Times New Roman", 1, 30));
        c.drawString("Enter Your Choice: ", 205, 378);
        c.setCursor(19, 58);
        //draw wifiIcon
        /*long latency = new Long(String.valueOf(cache.get("latency"))).longValue();
        BufferedImage wifiIcon = (BufferedImage) ((HashMap) cache.get("assets")).get("wifiicon0");

        removed because no more online functionality
        
        if (latency == -1) {
        } else if (latency < 2000) {
            wifiIcon = (BufferedImage) ((HashMap) cache.get("assets")).get("wifiicon3");
        } else if (latency < 4000) {
            wifiIcon = (BufferedImage) ((HashMap) cache.get("assets")).get("wifiicon2");
        } else {
            wifiIcon = (BufferedImage) ((HashMap) cache.get("assets")).get("wifiicon1");
        }
        c.drawImage(wifiIcon, -10, -10, null);*/
        //get input
        while (true) {
            try {
                choiceNum = c.readLine();
                choice = Integer.parseInt(choiceNum);
                break;
            } catch (NumberFormatException e) {
                createError("Please enter a valid number", "Invalid Input");
                //wipe input and set cursor back
                c.setCursor(19, 58);
                c.print(' ', choiceNum.length());
                c.setCursor(19, 58);
            }
        }
        if (choice == 1) {
            gameMenu();
        } else if (choice == 2) {
            highScores();
        } else if (choice == 3) {
            settings();
        } else if (choice == 4) {
            about();
        } else if (choice == 5) {
            goodbye();
        } else {
            createError("Invalid Choice", "Invalid Input");
            mainMenu();
        }
    }

    private void highScores() { //high scores page
        /*
        Method description
        
        This method displays the high scores from the local file, as well as 
        high score from the remote server
         */
 /*Method Variable Dictionary
    ----------------------------------------------------------------------------
    Type    | Name             | Desription
    ----------------------------------------------------------------------------
    long    | curtime          | Current time in milliseconds
    ----------------------------------------------------------------------------
    URL     | request          | URL to remote server for leaderboard
    ----------------------------------------------------------------------------
    HttpUrlConnection | connection | Connection to the leaderboard URL
            |                  | program can run fine without this (optional)
    ----------------------------------------------------------------------------
    BufferedReader | in        | BufferedReader for response
    ----------------------------------------------------------------------------
    String  | inputLine        | inputted response from server
    ----------------------------------------------------------------------------
    String  | scoreTime        | score and respective time achieved
    ----------------------------------------------------------------------------
    ArrayList | lb             | ArrayList of offline leaderboard stats
    --------------------------------------------------------------------------*/
        long curtime = System.currentTimeMillis();
        c.clear();
        for (int i = 0; i < 200; i++) {
            c.setColor(new Color(68, 17, 10));
            c.drawLine(0, i, i, 0);
        }
        for (int i = 200; i < 400; i++) {
            c.setColor(new Color(70, 34, 6));
            c.drawLine(0, i, i, 0);
        }
        for (int i = 400; i < 600; i++) {
            c.setColor(new Color(69, 58, 2));
            c.drawLine(0, i, i, 0);
        }
        for (int i = 600; i < 800; i++) {
            c.setColor(new Color(7, 40, 24));
            c.drawLine(0, i, i, 0);
        }
        for (int i = 800; i < 1000; i++) {
            c.setColor(new Color(16, 30, 51));
            c.drawLine(0, i, i, 0);
        }
        for (int i = 1000; i < 1200; i++) {
            c.setColor(new Color(26, 14, 45));
            c.drawLine(0, i, i, 0);
        }
        c.setColor(Color.white);
        c.setFont(new Font("Times New Roman", Font.BOLD, 40));
        c.drawString("High Scores", 220, 60);
        c.setFont(new Font("Times New Roman", Font.BOLD, 30));
        /*
        removed because no more online functionality

        c.drawString("Online Leaderboard", 40, 130);
        try { //fetch online leaderboard
            URL request = new URL((String) ((HashMap) cache.get("config")).get("onlineLeaderBoard"));
            HttpURLConnection connection = (HttpURLConnection) request.openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == 200) {
                curtime = System.currentTimeMillis() - curtime;
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                for (int i = 0; i < 10; i++) {
                    String inputLine = in.readLine();
                    if (inputLine != null) {
                        String[] scoreTime = inputLine.split("-");
                        //process fetched leaderboard
                        //index 0 of scoreTime is the integer score
                        //index 1 is a string representing the date it was achieved
                        //might wanna use trim() on both the strings
                        c.setFont(new Font("Times New Roman", Font.BOLD, 14));
                        String rank = String.valueOf(i + 1);
                        c.drawString(rank + ".", 20, 170 + 30 * i);
                        c.drawString(scoreTime[0].trim(), 40, 170 + 30 * i);
                        c.drawString(scoreTime[1].trim(), 100, 170 + 30 * i);
                    } else {
                        break;
                    }
                }
            } else {
                curtime = -1;
                if (String.valueOf(connection.getResponseCode()).charAt(0) == '5') {
                    createError("Remote leaderboard server internal error", "Server Error");
                } //response codes with 5 means server error
                //show leaderboard unavailable
            }
        } catch (MalformedURLException e) {
            createError("Corrupted Cache", "Internal Cache Error");
        } catch (Exception e) { //handles all other exceptions, including reading errors etc.
            c.setFont(new Font("Times New Roman", Font.BOLD, 16));
            c.drawString("Could not fetch leaderboard", 20, 170);
        }
        cache.put("latency", new Long(curtime));*/
        //high scores from local leaderboard
        ArrayList lb = (ArrayList) cache.get("lb");
        c.setFont(new Font("Times New Roman", Font.BOLD, 30));
        c.drawString("Offline Leaderboard", 350, 130);
        for (int i = 0; i < ((lb.size() >= 10) ? 10 : lb.size()); i++) {
            c.setFont(new Font("Times New Roman", Font.BOLD, 14));
            String rank = String.valueOf(i + 1);
            c.drawString(rank + ".", 370, 170 + 30 * i);
            c.drawString(String.valueOf(((Integer) lb.get(i)).intValue()), 390, 170 + 30 * i);
        }
        if (lb.size() == 0) {
            c.setFont(new Font("Times New Roman", Font.BOLD, 25));
            c.drawString("No scores recorded", 370, 170);
        }
        c.setFont(new Font("Times New Roman", Font.BOLD, 20));
        c.drawString("Press any key to return to Main Menu...", 160, 460);
        c.getChar();
        mainMenu();
    }

    private long checkForInternet() { //returns internet connectivity
        //by latency in ms. Returns -1 if no internet or server is dead
        /*
        Method description
        
        This method returns internet latency in ms
         */
 /*Method Variable Dictionary
    ----------------------------------------------------------------------------
    Type    | Name             | Desription
    ----------------------------------------------------------------------------
    long    | curtime          | Current time in milliseconds
    ----------------------------------------------------------------------------
    URL     | request          | URL to remote server
    ----------------------------------------------------------------------------
    HttpUrlConnection | connection | Connection to the request URL
            |                  | program can run fine without this (optional)
    --------------------------------------------------------------------------*/
        /*try {
            long curtime = System.currentTimeMillis();
            URL request = new URL((String) ((HashMap) cache.get("config")).get("onlineWordBank"));
            HttpURLConnection connection = (HttpURLConnection) request.openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == 200) {
                return System.currentTimeMillis() - curtime;
            } else {
                return -1;
            }
        } catch (MalformedURLException e) {
            return -1;
        } catch (IOException e) {
            return -1;
        }
        
        removed because no more online :(
        */
        return 0; //in case i accidentally forgot to remove a call to this function lol
    }

    private String getWord(int difficulty) { //get a single word
        /*
        Method description
        
        This method attempts to get a word from online if that mode is chosen
        otherwise it calls offlineWord
         */
 /*Method Variable Dictionary
    ----------------------------------------------------------------------------
    Type    | Name             | Desription
    ----------------------------------------------------------------------------
    URL     | request          | URL to remote server
    ----------------------------------------------------------------------------
    HttpUrlConnection | connection | Connection to the request URL
            |                  | program can run fine without this (optional)
    ----------------------------------------------------------------------------
    BufferedReader | in        | BufferedReader input for response
    ----------------------------------------------------------------------------
    String  | inputLine        | inputted response from server
    --------------------------------------------------------------------------*/
        //10 is easy, 35 is medium, 55 is hard, 70 is extreme
        /*if (((Boolean) cache.get("useOnline")).booleanValue()) {
            try {
                URL request = new URL(((String) ((HashMap) cache.get("config")).get("onlineWordBank")) + "?rarity=" + String.valueOf(difficulty) + "&limit=1");
                //unfortunately the HTTP protocol must be used
                //as JDK 1.4 requires manual SSL verification
                //however this is not an issue as no sensitive data is being sent
                HttpURLConnection connection = (HttpURLConnection) request.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine = in.readLine();
                if (inputLine != null && connection.getResponseCode() == 200) {
                    return inputLine;
                } else {
                    createError("Could not get word from server, using offline word bank instead", "Internet Error");
                }
            } catch (MalformedURLException e) {
                createError("Cache corrupted", "Error occurred");
            } catch (IOException e) {
                createError("Internal Error Occurred", "Error occurred");
            }
            return getOfflineWord(difficulty);
        } else {*/
            return getOfflineWord(difficulty);
        //} RIP ONLINE WORD BANK
    }

    private String getOfflineWord(int difficulty) { //get word from cache
        /*
        Method description
        
        This method returns a word from the offline word bank in the cache
         */
 /*Method Variable Dictionary
    ----------------------------------------------------------------------------
    Type    | Name             | Desription
    ----------------------------------------------------------------------------
    HashMap | wordbank         | HashMap storing the offline word bank
    ----------------------------------------------------------------------------
    String  | str              | String representing difficulty
    ----------------------------------------------------------------------------
    Stack   | words            | Stack of cached words
    ----------------------------------------------------------------------------
    String  | word             | Word that was popped off the stack
    --------------------------------------------------------------------------*/
        //load word from cache
        //pop word from stack
        HashMap wordbank = (HashMap) cache.get("offlineWordBank");
        //10 is easy, 35 is medium, 55 is hard, 70 is extreme
        String str = "";
        if (difficulty == 10) {
            str = "easy";
        } else if (difficulty == 35) {
            str = "medium";
        } else if (difficulty == 55) {
            str = "hard";
        } else {
            str = "extreme";
        }
        Stack words = (Stack) wordbank.get(str);
        if (words.empty()) {
            createError("Offline Word Bank used up, restart to reset the cache", "Internal Error");
            //exit the program once this occurs
            mainMenu();
            return "";
        } else {
            String word = (String) words.pop();
            wordbank.put(str, words);
            cache.put("offlineWordBank", wordbank);
            return word;
        }
    }

    private String encrypt(String txt) {
        //a very naive encryption using integer char values and hex
        //but it will do for this [redacted] as JDK1.4 does not support many advanced encryption algos
        //using this to prevent cheating through modifying data files
        /*
        Method description
        
        This method encrypts a string by converting it into integers character
        by character, concatenates them with dashes, then converts the entire
        string to hex, to make it unreadable to the human eye
         */
 /*Method Variable Dictionary
    ----------------------------------------------------------------------------
    Type    | Name             | Desription
    ----------------------------------------------------------------------------
    String  | str              | String with numeric representations of chars
    ----------------------------------------------------------------------------
    StringBuffer | sb          | A writable sequence of chars for hex conversion
    ----------------------------------------------------------------------------
    char[]  | ch               | character array of numerized characters
    ----------------------------------------------------------------------------
    String  | hexString        | final hex/encrypted value of string
    --------------------------------------------------------------------------*/
        String str = "";
        for (int i = 0; i < txt.length(); i++) {
            str += ((int) txt.charAt(i)) + "";
            if (i != txt.length() - 1) {
                str += "-";
            }
        }
        StringBuffer sb = new StringBuffer();
        char ch[] = str.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            String hexString = Integer.toHexString(ch[i]);
            sb.append(hexString);
        }
        return sb.toString();
    }

    private String decrypt(String enc) {
        /*
        Method description
        
        This method returns decrypted string
         */
 /*Method Variable Dictionary
    ----------------------------------------------------------------------------
    Type    | Name             | Desription
    ----------------------------------------------------------------------------
    String  | txt              | hex string
    ----------------------------------------------------------------------------
    String[]| chars            | characters split by dashes
    ----------------------------------------------------------------------------
    char[]  | charArray        | character array of numerized characters
    ----------------------------------------------------------------------------
    String  | str              | final decrypted string
    --------------------------------------------------------------------------*/
        String txt = "";
        char[] charArray = enc.toCharArray();
        for (int i = 0; i < charArray.length; i = i + 2) {
            String st = "" + charArray[i] + "" + charArray[i + 1];
            txt += ((char) Integer.parseInt(st, 16));
        }
        String str = "";
        String[] chars = txt.split("-");
        for (int i = 0; i < chars.length; i++) {
            int encchar = Integer.parseInt(chars[i]);
            str += ((char) encchar) + "";
        }
        return str;
    }

    private void firstRun() {
        /*
        Method description
        
        First run animation + prompt
         */
 /*Method Variable Dictionary
    ----------------------------------------------------------------------------
    Type    | Name             | Desription
    ----------------------------------------------------------------------------
    HashMap | config           | Map representing the config settings
    --------------------------------------------------------------------------*/
        c.clear();
        c.setColor(Color.black);
        c.fillRect(0, 0, 800, 500);
        c.setTextColor(Color.white);
        c.setTextBackgroundColor(Color.black);
        c.println();
        c.println("               __      __       .__                               ");
        c.println("              /  \\    /  \\ ____ |  |   ____  ____   _____   ____  ");
        c.println("              \\   \\/\\/   // __ \\|  | _/ ___\\/  _ \\ /     \\_/ __ \\ ");
        c.println("               \\        /\\  ___/|  |_\\  \\__(  <_> )  Y Y  \\  ___/ ");
        c.println("                \\__/\\  /  \\___  >____/\\___  >____/|__|_|  /\\___  >");
        c.println("                     \\/       \\/          \\/            \\/     \\/ \n");
        c.print(' ', 13);
        c.println("Hello! Since this is your first time running this program,\n");
        c.print(' ', 13);
        c.println("would you like to watch a short animation?\n\n");
        c.print(' ', 15);
        c.println("PRESS X TO SKIP OR ANY OTHER KEY TO WATCH ANIMATION\n\n\n\n\n");
        c.println("   If you skip it this dialog will pop up the next time you run this program");
        c.setCursor(10, 10);
        if (c.getChar() == 'x') {
            return;
        }
        // displays animation
        c.clear();
        c.fillRect(0, 0, 700, 500);
        Color hangar = new Color(169, 169, 169);  // creates a Color variable for the hangar component
        Color background = new Color(69, 69, 69);  // creates a Color variable for the background
        Color rope = new Color(140, 101, 41); // creates a Color variable for the rope component
        Color board = new Color(52, 107, 49); // creates a Color variable for the sign component
        Color wood = new Color(120, 81, 21); // creates a Color variable for the s3ign and stool component
        // fills the background
        c.setColor(background);
        c.fillRect(0, 0, 700, 500);
        // draws the title
        c.setColor(Color.white);
        c.setFont(new Font("Times New Roman", Font.ITALIC, 35));
        c.drawString("Guess A Letter!", 200, 50);
        // draws the rope
        c.setColor(rope);
        c.fillRoundRect(220, 90, 15, 100, 30, 30);
        // draws the broken stool
        c.setColor(wood);
        c.fillRect(120, 470, 70, 10);
        c.fillRect(210, 470, 70, 10);
        c.fillRect(160, 460, 70, 10);
        c.fillRect(240, 460, 70, 10);
        c.fillRect(290, 470, 70, 10);
        // draws the hangar
        c.setColor(hangar);
        c.fillRect(10, 480, 620, 20);
        c.fillRect(30, 100, 20, 450);
        c.fillRect(10, 80, 300, 20);
        // draws the sign
        c.setColor(wood);
        c.fillRect(470, 250, 25, 230);
        c.setColor(board);
        c.fillRect(330, 135, 300, 140);
        c.setColor(wood);
        c.fillRect(330, 135, 300, 10);
        c.fillRect(330, 265, 300, 10);
        c.fillRect(330, 135, 10, 140);
        c.fillRect(620, 135, 10, 140);
        // draws the words on the sign
        c.setFont(new Font("Times New Roman", 1, 23));
        c.setColor(Color.white);
        c.drawString("_ _ _ _ _ _ _ _ _ _ _ _ _", 370, 190);
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
        }
        //delay
        //
        c.fillOval(350, 300, 250, 120);
        for (int i = 400; i < 500; i++) {
            c.drawLine(400, 450, i, 400);
        }
        c.setFont(new Font("Times New Roman", 1, 15));
        c.setColor(Color.black);
        c.drawString("Alright folks!", 425, 330);
        c.drawString("Welcome to the weekly hangman!", 360, 350);
        c.drawString("Guess a letter at a time, if you", 360, 370);
        c.drawString("guess the word, the lad", 400, 390);
        c.drawString("can live!", 450, 410);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.setColor(Color.white);
        c.fillOval(350, 300, 250, 120);
        for (int i = 400; i < 500; i++) {
            c.drawLine(400, 450, i, 400);
        }
        c.setColor(Color.black);
        c.drawString("OK me first!", 425, 330);
        c.drawString("Let's see here, hmmm, it's a", 380, 350);
        c.drawString("thirteen letter word. Looks", 380, 370);
        c.drawString("harder than last week", 400, 390);
        c.drawString("eh, hmmm.", 440, 410);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.setColor(Color.white);
        c.fillOval(350, 300, 250, 120);
        for (int i = 400; i < 500; i++) {
            c.drawLine(400, 450, i, 400);
        }
        c.setColor(Color.black);
        c.drawString("Stop dawdling young man!", 390, 350);
        c.drawString("Other people are waiting!", 390, 370);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.setColor(Color.white);
        c.fillOval(350, 300, 250, 120);
        for (int i = 400; i < 500; i++) {
            c.drawLine(400, 450, i, 400);
        }
        c.setColor(Color.black);
        c.drawString("Jeez, okay, okay. Calm", 390, 350);
        c.drawString("yourself. I'm guessing 'd'.", 390, 370);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.setColor(Color.white);
        c.fillOval(350, 300, 250, 120);
        for (int i = 400; i < 500; i++) {
            c.drawLine(400, 450, i, 400);
        }
        c.setColor(Color.black);
        c.drawString("Oops! Wrong!", 425, 330);
        c.drawString("Let's bring in the young man", 380, 350);
        c.drawString("now, shall we? Everyone, welcome", 360, 370);
        c.drawString("Harry Hangman!", 420, 390);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.setFont(new Font("Times New Roman", 1, 23));
        c.setColor(Color.white);
        c.drawString("d", 430, 230);
        c.drawImage((BufferedImage) ((HashMap) cache.get("assets")).get("closed-eyes"), 186, 180, null);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.drawImage((BufferedImage) ((HashMap) cache.get("assets")).get("thinking"), 186, 180, null);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.setColor(background);
        c.fillOval(186, 180, 90, 120);
        c.drawImage((BufferedImage) ((HashMap) cache.get("assets")).get("realization"), 186, 180, null);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.setColor(Color.white);
        c.fillOval(350, 300, 250, 120);
        for (int i = 400; i < 500; i++) {
            c.drawLine(400, 450, i, 400);
        }
        c.setColor(Color.black);
        c.setFont(new Font("Times New Roman", 1, 15));
        c.drawString("Young lad!", 435, 330);
        c.drawString("Seems you've realized the situation", 360, 350);
        c.drawString("you're in right now. Welp, pray you", 360, 370);
        c.drawString("live another day!", 420, 390);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.setColor(background);
        c.fillOval(186, 180, 90, 120);
        c.drawImage((BufferedImage) ((HashMap) cache.get("assets")).get("screaming"), 184, 178, null);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.setColor(Color.white);
        c.fillOval(30, 50, 200, 100);
        for (int i = 200; i > 100; i--) {
            c.drawLine(180, 170, i, 100);
        }
        c.setColor(Color.black);
        c.setFont(new Font("Times New Roman", 1, 20));
        c.drawString("OH NONONO!", 60, 80);
        c.drawString("THIRTEEN", 70, 105);
        c.drawString("LETTERS?", 75, 130);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.setColor(Color.white);
        c.fillOval(350, 300, 250, 120);
        for (int i = 400; i < 500; i++) {
            c.drawLine(400, 450, i, 400);
        }
        c.setColor(Color.black);
        c.setFont(new Font("Times New Roman", 1, 15));
        c.drawString("Of Course!", 435, 330);
        c.drawString("You can bet the rest of your body its", 360, 350);
        c.drawString("thirteen letters! Literally! Well, the", 360, 370);
        c.drawString("people decide your fate now.", 380, 390);
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.setColor(Color.white);
        c.fillOval(350, 300, 250, 120);
        for (int i = 400; i < 500; i++) {
            c.drawLine(400, 450, i, 400);
        }
        c.setColor(Color.black);
        c.setFont(new Font("Times New Roman", 1, 15));
        c.drawString("OK me next!", 435, 330);
        c.drawString("Let's see here, I think I'm gonna", 360, 350);
        c.drawString("play it safe here...I'm gonna go for", 360, 370);
        c.drawString("an 'e' for my guess for now.", 380, 390);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.setColor(Color.white);
        c.fillOval(350, 300, 250, 120);
        for (int i = 400; i < 500; i++) {
            c.drawLine(400, 450, i, 400);
        }
        c.setColor(Color.black);
        c.setFont(new Font("Times New Roman", 1, 15));
        c.drawString("Congratulations!", 425, 330);
        c.drawString("This random sir here has just made", 360, 350);
        c.drawString("a correct guess! Now, let's see the", 360, 370);
        c.drawString("e's in this word.", 420, 390);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.setFont(new Font("Times New Roman", 1, 23));
        c.setColor(Color.white);
        c.drawString("e _ _ e _ _ _ _ _ _ e _ _", 370, 190);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.setColor(Color.white);
        c.fillOval(30, 50, 200, 100);
        for (int i = 200; i > 100; i--) {
            c.drawLine(180, 170, i, 100);
        }
        c.setColor(Color.black);
        c.setFont(new Font("Times New Roman", 1, 20));
        c.drawString("HOLY ****!", 70, 80);
        c.drawString("YOU, GOOD SIR,", 55, 105);
        c.drawString("THANK YOU!", 70, 130);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.setColor(Color.white);
        c.fillOval(350, 300, 250, 120);
        for (int i = 400; i < 500; i++) {
            c.drawLine(400, 450, i, 400);
        }
        c.setColor(Color.black);
        c.setFont(new Font("Times New Roman", 1, 30));
        c.drawString("Alright, next!", 380, 370);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.setColor(Color.white);
        c.fillOval(350, 300, 250, 120);
        for (int i = 400; i < 500; i++) {
            c.drawLine(400, 450, i, 400);
        }
        c.setColor(Color.black);
        c.setFont(new Font("Times New Roman", 1, 15));
        c.drawString("My turn now!", 435, 330);
        c.drawString("I know exactly what the word is this", 360, 350);
        c.drawString("time. I have confidence in my guess.", 360, 370);
        c.drawString("I'm going to guess 'f'.", 400, 390);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.setColor(Color.white);
        c.fillOval(350, 300, 250, 120);
        for (int i = 400; i < 500; i++) {
            c.drawLine(400, 450, i, 400);
        }
        c.setColor(Color.black);
        c.setFont(new Font("Times New Roman", 1, 15));
        c.drawString("INCORRECT!", 425, 330);
        c.drawString("You should have thought carefully", 370, 350);
        c.drawString("about your answer, as now the lad", 360, 370);
        c.drawString("will get his torso!", 420, 390);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.setFont(new Font("Times New Roman", 1, 23));
        c.setColor(Color.white);
        c.drawString("d, f", 430, 230);
        c.setColor(Color.white);
        c.fillOval(30, 50, 200, 100);
        for (int i = 200; i > 100; i--) {
            c.drawLine(180, 170, i, 100);
        }
        c.setColor(Color.black);
        c.setFont(new Font("Times New Roman", 1, 20));
        c.drawString("OH NONONO!", 65, 80);
        c.drawString("THIS, THIS IS,", 65, 105);
        c.drawString("NOT GOOD!", 70, 130);
        for (int i = 220; i < 360; i++) {
            c.setColor(Color.black);
            c.fillOval(220, i, 15, 15);
            c.setColor(rope);
            c.fillOval(186, 190, 80, 80);
            c.drawImage((BufferedImage) ((HashMap) cache.get("assets")).get("screaming"), 184, 178, null);
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
            }
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        c.setColor(Color.black);
        c.fillRect(0, 0, 700, 500);
        c.setColor(Color.white);
        c.setFont(new Font("Times New Roman", Font.ITALIC, 35));
        c.drawString("And So, The Guessing Continued...", 80, 250);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        // fills the background
        c.setColor(background);
        c.fillRect(0, 0, 700, 500);
        // draws the title
        c.setColor(Color.white);
        c.setFont(new Font("Times New Roman", Font.ITALIC, 35));
        c.drawString("Guess A Letter!", 200, 50);
        // draws the rope
        c.setColor(rope);
        c.fillRoundRect(220, 90, 15, 100, 30, 30);
        // draws the broken stool
        c.setColor(wood);
        c.fillRect(120, 470, 70, 10);
        c.fillRect(210, 470, 70, 10);
        c.fillRect(160, 460, 70, 10);
        c.fillRect(240, 460, 70, 10);
        c.fillRect(290, 470, 70, 10);
        // draws the hangar
        c.setColor(hangar);
        c.fillRect(10, 480, 620, 20);
        c.fillRect(30, 100, 20, 450);
        c.fillRect(10, 80, 300, 20);
        // draws the sign
        c.setColor(wood);
        c.fillRect(470, 250, 25, 230);
        c.setColor(board);
        c.fillRect(330, 135, 300, 140);
        c.setColor(wood);
        c.fillRect(330, 135, 300, 10);
        c.fillRect(330, 265, 300, 10);
        c.fillRect(330, 135, 10, 140);
        c.fillRect(620, 135, 10, 140);
        // draws the words on the sign
        c.setFont(new Font("Times New Roman", 1, 23));
        c.setColor(Color.white);
        c.drawString("e _ _ e _ _ a i _ _ e _ _", 370, 190);
        c.drawString("d, f, l, c, x", 430, 230);
        // draws the stickman
        c.setColor(Color.black);
        c.fillRoundRect(220, 220, 15, 140, 20, 20);
        c.setColor(rope);
        c.fillOval(186, 190, 80, 80);
        c.drawImage((BufferedImage) ((HashMap) cache.get("assets")).get("despair"), 184, 178, null);
        c.setColor(Color.black);
        int left = 220;
        for (int i = 270; i < 320; i++) {
            c.fillOval(i - 50, i, 15, 15);
            c.fillOval(left, i, 15, 15);
            left--;
        }
        for (int i = 350; i < 420; i++) {
            c.fillOval(i - 130, i, 15, 15);
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
            }
        }
        //DELAY
        //HERE
        c.setColor(Color.white);
        c.fillOval(350, 300, 250, 120);
        for (int i = 400; i < 500; i++) {
            c.drawLine(400, 450, i, 400);
        }
        c.setColor(Color.black);
        c.setFont(new Font("Times New Roman", 1, 15));
        c.drawString("No 'x' here!", 425, 330);
        c.drawString("The lad's getting awfully close to", 370, 350);
        c.drawString("a full body isn't he? Unfortunate,", 360, 370);
        c.drawString("but you may die.", 420, 390);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.setColor(Color.white);
        c.fillOval(30, 50, 200, 100);
        for (int i = 200; i > 100; i--) {
            c.drawLine(180, 170, i, 100);
        }
        c.setColor(Color.black);
        c.setFont(new Font("Times New Roman", 1, 15));
        c.drawString("I've given up hope.", 70, 80);
        c.drawString("Unfortunate, but all I", 55, 105);
        c.drawString("can curse is my luck.", 67, 130);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.setColor(Color.white);
        c.fillOval(30, 50, 200, 100);
        for (int i = 200; i > 100; i--) {
            c.drawLine(180, 170, i, 100);
        }
        c.setColor(Color.black);
        c.setFont(new Font("Times New Roman", 1, 15));
        c.drawString("I don't think I can", 70, 80);
        c.drawString("pull through. Just, tell", 55, 105);
        c.drawString("my wife I love her.", 70, 130);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.setColor(Color.white);
        c.fillOval(350, 300, 250, 120);
        for (int i = 400; i < 500; i++) {
            c.drawLine(400, 450, i, 400);
        }
        c.setColor(Color.black);
        c.setFont(new Font("Times New Roman", 1, 15));
        c.drawString("How so very sad.", 425, 330);
        c.drawString("These may be the lad's last words", 370, 350);
        c.drawString("men. He may die, but we honor him", 360, 370);
        c.drawString("as a fighter.", 430, 390);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.setColor(Color.white);
        c.fillOval(350, 300, 250, 120);
        for (int i = 400; i < 500; i++) {
            c.drawLine(400, 450, i, 400);
        }
        c.setColor(Color.black);
        c.setFont(new Font("Times New Roman", 1, 15));
        c.drawString("Wait, wait a sec!", 425, 330);
        c.drawString("I think I may know what word it is!", 365, 350);
        c.drawString("Give me a chance! I really know it!", 360, 370);
        c.drawString("The word is...", 420, 390);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.setColor(Color.black);
        c.fillRect(0, 0, 700, 500);
        c.setColor(Color.white);
        c.setFont(new Font("Times New Roman", Font.ITALIC, 35));
        c.drawString("Made By ApocalypseCalculator and CantCod", 60, 250);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.setColor(Color.black);
        c.fillRect(0, 0, 700, 500);
        c.setColor(Color.white);
        c.setFont(new Font("Times New Roman", Font.ITALIC, 35));
        c.drawString("Made With Ready To Program 1.7.1", 80, 250);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.setColor(Color.black);
        c.fillRect(0, 0, 700, 500);
        c.setColor(Color.white);
        c.setFont(new Font("Times New Roman", Font.ITALIC, 35));
        c.drawString("Using Java 1.4.2", 190, 250);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        //DELAY
        //HERE
        c.setColor(Color.black);
        c.fillRect(0, 0, 700, 500);
        c.setColor(Color.white);
        c.setFont(new Font("Times New Roman", Font.ITALIC, 35));
        c.drawString("Harry Hangman", 200, 250);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        c.setColor(Color.black);
        //update config to delete first run
        HashMap config = ((HashMap) cache.get("config"));
        config.put("firstrun", "0");
        cache.put("config", config);
        updateConfig();
    }

    private void playAudio(String path) {
        //after some investigating audio format is extremely complicated
        //in JDK1.4 so I will just have to abandon this for now
        if (((String) ((HashMap) cache.get("config")).get("mute")).equals("0")) {
            try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(path));
                //Clip clip = AudioSystem.getLine();
                //clip.open(audioInputStream);
                //clip.start();
            } catch (Exception ex) {
            }
        }
    }

    private void initialize() {
        /*
        Method description
        
        Initialize the cache
         */
 /*Method Variable Dictionary
    ----------------------------------------------------------------------------
    Type    | Name             | Desription
    ----------------------------------------------------------------------------
    BufferedReader | in        | multiple bufferedreader for reading from
                   |           | multiple files
    ----------------------------------------------------------------------------
    HashMap        | config    | multiple hashmaps to hold cached data
    ----------------------------------------------------------------------------
    File[]         | assetsFolder | Array holding all assets
    ----------------------------------------------------------------------------
    ArrayList      | lb        | ArrayList of leaderboard data
    --------------------------------------------------------------------------*/
        try {
            HashMap config = new HashMap();
            BufferedReader configFile = new BufferedReader(new FileReader(configPath));
            while (true) {
                String inp = configFile.readLine();
                if (inp != null) {
                    String[] params = decrypt(inp.trim()).split("=");
                    config.put(params[0].trim(), params[1].trim());
                } else {
                    break;
                }
            }
            configFile.close();
            File[] assetsFolder = new File((String) config.get("localAssets")).listFiles();
            HashMap assets = new HashMap();
            for (int i = 0; i < assetsFolder.length; i++) {
                if (assetsFolder[i].exists()) {
                    String ext = assetsFolder[i].getName().split("\\.")[1].trim().toLowerCase();
                    if (ext.equals("png") || ext.equals("jpeg") || ext.equals("gif")) {
                        assets.put(assetsFolder[i].getName().split("\\.")[0].trim(), ImageIO.read(new File(config.get("localAssets") + assetsFolder[i].getName().trim())));
                    }
                }
            }
            cache.put("assets", assets);
            cache.put("config", config);
            BufferedReader wordBankFile = new BufferedReader(new FileReader((String) ((HashMap) cache.get("config")).get("localWordBank")));
            HashMap offlineWordBank = new HashMap();
            while (true) {
                String inp = wordBankFile.readLine();
                if (inp != null) {
                    String[] params = decrypt(inp.trim()).split("=");
                    Stack words = new Stack();
                    String[] wordsFromFile = params[1].trim().split(",");
                    for (int i = 0; i < wordsFromFile.length; i++) {
                        String word = wordsFromFile[i];
                        words.push(word.trim());
                    }
                    Collections.shuffle(words);
                    offlineWordBank.put(params[0].trim(), words);
                } else {
                    break;
                }
            }
            wordBankFile.close();
            cache.put("offlineWordBank", offlineWordBank);
            ArrayList lb = new ArrayList();
            BufferedReader lbFile = new BufferedReader(new FileReader((String) ((HashMap) cache.get("config")).get("localLeaderBoard")));
            while (true) {
                String lbLine = lbFile.readLine();
                if (lbLine != null) {
                    lb.add(new Integer(decrypt(lbLine)));
                } else {
                    break;
                }
            }
            Collections.sort(lb, Collections.reverseOrder());
            cache.put("lb", lb);
            //cache.put("useOnline", new Boolean(false));
            //cache.put("latency", new Long(checkForInternet()));
            cache.put("endlessScore", new Integer(0));
            cache.put("mainMenuCalled", new Boolean(false));
        } catch (Exception e) { //catches all exceptions, regardless of type	
            createError("Cache failed to load. \nThis is usually caused by missing data/assets. \nProgram will shut down", "Initialization Error");
            System.exit(0);
        }
    }

    private void updateConfig() {
        /*
        Method description
        
        Write config to file
         */
 /*Method Variable Dictionary
    ----------------------------------------------------------------------------
    Type    | Name             | Desription
    ----------------------------------------------------------------------------
    HashMap        | config    | multiple hashmaps to hold cached data
    ----------------------------------------------------------------------------
    PrintWriter    | out         | Array holding all assets
    ----------------------------------------------------------------------------
    Iterator       | configIterate | Iterator for HashMap
    ----------------------------------------------------------------------------
    Map.Entry      | element     | an entry of the hashmap
    --------------------------------------------------------------------------*/
        HashMap config = ((HashMap) cache.get("config"));
        Iterator configIterate = config.entrySet().iterator();
        try {
            PrintWriter out = new PrintWriter(new FileWriter(configPath));
            while (configIterate.hasNext()) {
                Map.Entry element = (Map.Entry) configIterate.next();
                out.println(encrypt(element.getKey() + "=" + element.getValue()));
            }
            out.close();
        } catch (IOException e) {
            createError("Failed to write cached config to file", "File Error");
        }
    }

    private void createError(String msg, String title) {
        /*
        Method description
        
        Show an error dialog
         */
 /*Method Variable Dictionary
    ----------------------------------------------------------------------------
    No local variables used
    --------------------------------------------------------------------------*/
        JOptionPane.showMessageDialog(errors, msg, title, JOptionPane.ERROR_MESSAGE);
    }

    private void splashScreen() {
        /*
        Method description
        
        Display the splashscreen
         */
 /*Method Variable Dictionary
    ----------------------------------------------------------------------------
    Type    | Name             | Desription
    ----------------------------------------------------------------------------
    int     | left             | integer representing left spacing
    --------------------------------------------------------------------------*/
        Color hangar = new Color(169, 169, 169);  // creates a Color variable for the hangar component
        Color background = new Color(69, 69, 69);  // creates a Color variable for the background
        Color rope = new Color(140, 101, 41); // creates a Color variable for the rope component
        Color board = new Color(52, 107, 49); // creates a Color variable for the sign component
        Color wood = new Color(120, 81, 21); // creates a Color variable for the sign and stool component
        // fills the background
        c.setColor(background);
        c.fillRect(0, 0, 700, 500);
        // draws the title
        c.setColor(Color.white);
        c.setFont(new Font("Times New Roman", Font.ITALIC, 35));
        c.drawString("Harry Hangman", 200, 50);
        // draws the rope
        c.setColor(rope);
        c.fillRoundRect(220, 90, 15, 100, 30, 30);
        // draws the broken stool
        c.setColor(wood);
        c.fillRect(120, 470, 70, 10);
        c.fillRect(210, 470, 70, 10);
        c.fillRect(160, 460, 70, 10);
        c.fillRect(240, 460, 70, 10);
        c.fillRect(290, 470, 70, 10);
        // draws the hangar
        c.setColor(hangar);
        c.fillRect(10, 480, 620, 20);
        c.fillRect(30, 100, 20, 450);
        c.fillRect(10, 80, 300, 20);
        // draws the stickman
        c.setColor(Color.black);
        c.fillRoundRect(220, 220, 15, 140, 20, 20);
        c.setColor(rope);
        c.fillOval(186, 190, 80, 80);
        c.setColor(Color.black);
        c.fillOval(186, 180, 80, 80);
        int left = 220;
        for (int i = 350; i < 420; i++) {
            c.fillOval(i - 130, i, 15, 15);
            c.fillOval(left, i, 15, 15);
            left--;
        }
        left = 220;
        for (int i = 270; i < 320; i++) {
            c.fillOval(i - 50, i, 15, 15);
            c.fillOval(left, i, 15, 15);
            left--;
        }
        // draws the sign
        c.setColor(wood);
        c.fillRect(470, 250, 25, 230);
        c.setColor(board);
        c.fillRect(330, 135, 300, 140);
        c.setColor(wood);
        c.fillRect(330, 135, 300, 10);
        c.fillRect(330, 265, 300, 10);
        c.fillRect(330, 135, 10, 140);
        c.fillRect(620, 135, 10, 140);
        // draws the words on the sign
        c.setFont(new Font("Times New Roman", 1, 23));
        c.setColor(Color.white);
        c.drawString("e _ _ e _ _ a i _ _ e _ _", 370, 190);
        c.drawString("d, f, l, c, x", 430, 230);
        c.setColor(new Color(43, 55, 82));
        c.fillRoundRect(100, 400, 440, 50, 20, 20);
        Color lightblue = new Color(98, 143, 219);
        for (int i = 0; i < 100; i++) {
            //draw loading bar rectangle by rectangle
            c.setColor(lightblue);
            c.fillRoundRect(110 + i * 4, 410, 20, 30, 10, 10);
            //put loading so that rectangles dont overlap it
            c.setColor(Color.white);
            c.drawString("Loading...", 290, 430);
            try {
                Thread.sleep(50); //pause execution to appear animated (50ms)
            } catch (InterruptedException e) {
            } //catch any errors thrown from Thread.sleep()
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }

    private void about() {
        /*
        Method description
        
        About page
         */
 /*Method Variable Dictionary
    ----------------------------------------------------------------------------
    No local variables
    --------------------------------------------------------------------------*/
        c.clear();
        // draws the about page graphic
        c.setColor(Color.black);
        c.fillRect(0, 0, 700, 500);
        c.setColor(new Color(50, 205, 50));
        c.setFont(new Font("Times New Roman", 1, 40));
        c.drawString("About", 270, 50);
        c.setFont(new Font("Times New Roman", 1, 25));
        c.drawString("How To Play:", 255, 100);
        c.setFont(new Font("Times New Roman", 1, 20));
        c.drawString("Hangman is a game in which you have to guess a word with less", 50, 130);
        c.drawString("than six incorrect guesses. Each level will consist of one word with", 50, 155);
        c.drawString("different lengths that depend on the difficulty chosen. Each time ", 50, 180);
        c.drawString("you guess a letter, its blank in the word is filled, or is added to the", 50, 205);
        c.drawString("wrong guesses pool if the letter is not in the word. At six wrong", 50, 230);
        c.drawString("guesses, you lose the game. Points are given based on how many", 50, 255);
        c.drawString("wrong guesses you have at the end, the formula being difficulty", 50, 280);
        c.drawString("times (6 - wrong guesses), with 10 being easy, 35 medium, 55 hard,", 50, 305);
        c.drawString("and 70 extreme. Each word bank has its individual leaderboard.", 50, 330);
        c.drawString("Credits:", 285, 380);
        c.drawString("Made By: ApocalypseCalculator and CantCod", 160, 405);
        c.drawString("Made in Ready to Program Version 1.7.1 using Java 1.4.2", 90, 430);
        c.drawString("Press Any Key To Return To Main Menu...", 140, 480);
        c.getChar();
        mainMenu();
    }

    private void settings() {
        /*
        Method description
        
        Settings page
         */
 /*Method Variable Dictionary
    ----------------------------------------------------------------------------
    Type    | Name             | Desription
    ----------------------------------------------------------------------------
    String         | inp       | console input
    ----------------------------------------------------------------------------
    JFileChooser   | wordFile  | File chooser to select a new file
    ----------------------------------------------------------------------------
    PrintWriter    | out       | Output
    ----------------------------------------------------------------------------
    HashMap        | config    | HashMap of configuration data
    --------------------------------------------------------------------------*/
        c.clear();
        c.setColor(Color.black);
        c.fillRect(0, 0, 700, 500);
        c.setColor(new Color(50, 205, 50));
        c.setFont(new Font("Times New Roman", 1, 40));
        c.drawString("Settings", 260, 60);
        c.setFont(new Font("Times New Roman", 1, 30));
        c.drawString("1. New Word Bank File", 100, 120);
        c.drawImage((BufferedImage) ((HashMap) cache.get("assets")).get("newwwordfile"), 400, 85, null);
        c.drawString("2. Mute Audio", 100, 170);
        c.drawImage((BufferedImage) ((HashMap) cache.get("assets")).get("mutedmic"), 330, 135, null);
        c.drawString("3. Unmute Audio", 100, 220);
        c.drawImage((BufferedImage) ((HashMap) cache.get("assets")).get("umutedmic"), 360, 185, null);
        c.drawString("4. Toggle Animation", 100, 270);
        c.drawString("5. Reset Local Leaderboard", 100, 320);
        c.drawString("6. Return to Main Menu", 100, 370);
        c.drawString("Enter Your Choice:", 200, 458);
        c.setCursor(23, 58);
        while (true) {
            String inp = c.readLine();
            c.setCursor(23, 58);
            c.print(' ', inp.length());
            c.setCursor(23, 58);
            //set cursor back after input, erase input, set cursor back
            if (inp.equals("1")) { //1 is change wordbank file
                try {
                    JFileChooser wordFile = new JFileChooser();
                    wordFile.setDialogTitle("Choose a new wordbank file.");
                    wordFile.showSaveDialog(null);
                    String location = wordFile.getSelectedFile().toString();
                    if (location.endsWith("words.hangman")) {
                        HashMap config = (HashMap) cache.get("config");
                        config.put("localWordBank", location);
                        cache.put("config", config);
                        updateConfig();
                        JOptionPane.showMessageDialog(errors, "Saved settings", "Success!", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        createError("Must use a words.hangman file", "Invalid File");
                    }
                } catch (Exception e) {
                }
            } else if (inp.equals("2")) { //mute
                HashMap config = (HashMap) cache.get("config");
                config.put("mute", "1");
                cache.put("config", config);
                updateConfig();
                JOptionPane.showMessageDialog(errors, "Muted", "Success!", JOptionPane.INFORMATION_MESSAGE);
            } else if (inp.equals("3")) { //unmute
                HashMap config = (HashMap) cache.get("config");
                config.put("mute", "0");
                cache.put("config", config);
                updateConfig();
                JOptionPane.showMessageDialog(errors, "Unmuted", "Success!", JOptionPane.INFORMATION_MESSAGE);
            } else if (inp.equals("4")) { //display animation on next run
                HashMap config = (HashMap) cache.get("config");
                config.put("firstrun", "1");
                cache.put("config", config);
                updateConfig();
                JOptionPane.showMessageDialog(errors, "Game animation will display when you run it again", "Success!", JOptionPane.INFORMATION_MESSAGE);
            } else if (inp.equals("5")) { //reset all stats
                try {
                    PrintWriter out = new PrintWriter(new FileWriter((String) ((HashMap) cache.get("config")).get("localLeaderBoard"), false));
                    out.print("");
                    out.close();
                    JOptionPane.showMessageDialog(errors, "Erased all local stats", "Success!", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    createError("Error occurred while erasing data", "ERROR");
                }
            } else if (inp.equals("6")) { //exit page
                break;
            } else {
                createError("Invalid Input", "ERROR");
            }
        }
        mainMenu();
    }

    private void goodbye() {
        /*
        Method description
        
        Displays goodbye page and graphics, then exits
         */
 /*Method Variable Dictionary
    ----------------------------------------------------------------------------
    No Local Variables
    --------------------------------------------------------------------------*/
        Color hangar = new Color(169, 169, 169);  // creates a Color variable for the hangar component
        Color background = new Color(69, 69, 69);  // creates a Color variable for the background
        Color rope = new Color(140, 101, 41); // creates a Color variable for the rope component
        Color board = new Color(52, 107, 49); // creates a Color variable for the sign component
        Color wood = new Color(120, 81, 21); // creates a Color variable for the sign and stool component
        c.clear();
        // fills the background
        c.setColor(background);
        c.fillRect(0, 0, 700, 500);
        // draws the title
        c.setColor(Color.white);
        c.setFont(new Font("Times New Roman", Font.ITALIC, 35));
        c.drawString("Thank You For Playing Harry Hangman!", 30, 50);
        // draws the rope
        c.setColor(rope);
        c.fillRoundRect(220, 90, 15, 100, 30, 30);
        // draws the broken stool
        c.setColor(wood);
        c.fillRect(120, 470, 70, 10);
        c.fillRect(210, 470, 70, 10);
        c.fillRect(160, 460, 70, 10);
        c.fillRect(240, 460, 70, 10);
        c.fillRect(290, 470, 70, 10);
        // draws the hangar
        c.setColor(hangar);
        c.fillRect(10, 480, 620, 20);
        c.fillRect(30, 100, 20, 450);
        c.fillRect(10, 80, 300, 20);
        // draws the stickman
        c.setColor(Color.black);
        c.fillRoundRect(220, 220, 15, 140, 20, 20);
        c.setColor(rope);
        c.fillOval(186, 190, 80, 80);
        c.setColor(Color.black);
        c.fillOval(186, 180, 80, 80);
        c.drawImage((BufferedImage) ((HashMap) cache.get("assets")).get("awesomeface"), 186, 180, null);
        int left = 220;
        for (int i = 350; i < 420; i++) {
            c.fillOval(i - 130, i, 15, 15);
            c.fillOval(left, i, 15, 15);
            left--;
        }
        left = 220;
        for (int i = 270; i < 320; i++) {
            c.fillOval(i - 50, i, 15, 15);
            c.fillOval(left, i, 15, 15);
            left--;
        }
        // draws the sign
        c.setColor(wood);
        c.fillRect(470, 250, 25, 230);
        c.setColor(board);
        c.fillRect(330, 135, 300, 140);
        c.setColor(wood);
        c.fillRect(330, 135, 300, 10);
        c.fillRect(330, 265, 300, 10);
        c.fillRect(330, 135, 10, 140);
        c.fillRect(620, 135, 10, 140);
        // draws the words on the sign
        c.setFont(new Font("Times New Roman", 1, 23));
        c.setColor(Color.white);
        c.drawString("e n t e r t a i n m e n t", 370, 190);
        c.drawString("d, f, l, c, x", 430, 230);
        // prompts user input to exit
        c.setFont(new Font("Times New Roman", 1, 25));
        c.drawString("Press any key to exit the program...", 140, 470);
        c.getChar();
        System.exit(0); //terminate process with status code 0 (OK)
    }

    private void level(int mode, int difficulty) {
        /*
        Method description
        
        Displays level animations & graphics and prompts user input
        Hardcoded hint & cheat for level 1 & 2 respectively
        For wrong answers there is an if chain that draws body parts
        of hangman and then kills him when he is fully drawn
         */
 /*Method Variable Dictionary
    ----------------------------------------------------------------------------
    Type           | Name      | Desription
    ----------------------------------------------------------------------------
    String         | word      | stores a word taken from either online or 
                   |           | offline word bank
    ----------------------------------------------------------------------------
    int            | spacing   | modified in for loops to center the blanks and 
                   |           | space guessed letters of words correctly on the 
                   |           | sign graphic according to word length
    ----------------------------------------------------------------------------
    int            | mode      | decides whether to run endless or campaign mode
    ----------------------------------------------------------------------------
    int            | difficulty| decides difficulty of word taken from word bank
    ----------------------------------------------------------------------------
    int            | wordLength| stores length of word taken from word bank
    ----------------------------------------------------------------------------
    int            | wrongStart| used in for loops to space incorrect letters
    ----------------------------------------------------------------------------
    int            | correct   | stores number of correctly guessed letters
    ----------------------------------------------------------------------------
    int            | wrong     | stores number of incorrectly guessed letters
    ----------------------------------------------------------------------------
    int            | left      | used in for loop to draw left limbs of stickman
    ----------------------------------------------------------------------------
    String         | guess     | stores user input of guessed letter
    ----------------------------------------------------------------------------
    String         | alphabet  | stores letters of the alphabet and is used to 
                   |           | check if input is valid
    ----------------------------------------------------------------------------
    String         | alphaUsed | stores guessed letters to prevent user from 
                   |           | guessing a letter more than once
    ----------------------------------------------------------------------------*/
        Color hangar = new Color(169, 169, 169);  // creates a Color variable for the hangar component
        Color background = new Color(69, 69, 69);  // creates a Color variable for the background
        Color rope = new Color(140, 101, 41); // creates a Color variable for the rope component
        Color board = new Color(52, 107, 49); // creates a Color variable for the sign component
        Color wood = new Color(120, 81, 21); // creates a Color variable for the sign and stool component
        // fills the background
        c.setColor(background);
        c.fillRect(0, 0, 700, 500);
        // draws the title
        c.setColor(Color.white);
        c.setFont(new Font("Times New Roman", Font.ITALIC, 35));
        if (mode == 1) {
            c.drawString((difficulty == 10) ? "Level One" : "Level Two", 260, 50);
        } else {
            c.drawString("Endless Mode", 240, 50);
        }
        // draws the rope
        c.setColor(rope);
        c.fillRoundRect(220, 90, 15, 100, 30, 30);
        // draws the broken stool
        c.setColor(wood);
        c.fillRect(120, 470, 70, 10);
        c.fillRect(210, 470, 70, 10);
        c.fillRect(160, 460, 70, 10);
        c.fillRect(240, 460, 70, 10);
        c.fillRect(290, 470, 70, 10);
        // draws the hangar
        c.setColor(hangar);
        c.fillRect(10, 480, 620, 20);
        c.fillRect(30, 100, 20, 450);
        c.fillRect(10, 80, 300, 20);
        // draws the sign
        c.setColor(wood);
        c.fillRect(470, 250, 25, 230);
        c.setColor(board);
        c.fillRect(330, 135, 300, 140);
        c.setColor(wood);
        c.fillRect(330, 135, 300, 10);
        c.fillRect(330, 265, 300, 10);
        c.fillRect(330, 135, 10, 140);
        c.fillRect(620, 135, 10, 140);
        // draws the words on the sign
        c.setFont(new Font("Times New Roman", 1, 23));
        c.setColor(Color.white);
        String word = getWord(difficulty).toLowerCase();
        int wordLength = word.length();
        int spacing = 468;
        for (int i = 0; i < wordLength / 2; i++) {
            spacing -= 20;
            c.drawString("_", spacing, 190);
        }
        int start = spacing;
        spacing = 428;
        for (int i = 0; i <= wordLength / 2; i++) {
            spacing += 20;
            c.drawString("_", spacing, 190);
        }
        if (word.length() % 2 == 1) {
            c.drawString("_", spacing + 20, 190);
        }
        c.setFont(new Font("Times New Roman", 1, 25));
        c.drawString("Please enter a letter you would like to guess: ", 30, 455);
        if (mode == 1) {
            c.drawString((difficulty == 10) ? "Enter 'hint' to receive a hint." : "Enter 'cheat' for instant win.", 30, 485);
        }
        c.setCursor(23, 64);
        // checks user guesses
        int wrongStart = 430;
        int correct = 0;
        int wrong = 0;
        spacing = 20;
        //add a var for storing possible 
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        String alphaUsed = "";
        while (true) {
            String guess = c.readLine().toLowerCase();
            //fill in words from original, strip used from valid.
            if (guess.equalsIgnoreCase("hint") && difficulty == 10 && mode == 1) {
                for (int k = 0; k < word.length(); k++) {
                    if (alphaUsed.indexOf(word.charAt(k) + "") == -1) {
                        for (int m = 0; m < word.length(); m++) {
                            if ((word.charAt(k) + "").equals(word.charAt(m) + "")) {
                                c.setFont(new Font("Times New Roman", 1, 23));
                                c.setColor(Color.white);
                                c.drawString(word.charAt(m) + "", start + spacing * m, 190);
                                correct++;
                                if (correct >= word.length()) {
                                    try {
                                        Thread.sleep(3000);
                                    } catch (Exception e) {
                                    }
                                    winScreen();
                                    level(1, 35);
                                }
                            }
                        }
                        c.setCursor(23, 64);
                        c.print(' ', guess.length());
                        c.setCursor(23, 64);
                        alphaUsed += word.charAt(k);
                        break;
                    }
                }
            } else if (guess.equalsIgnoreCase("cheat") && difficulty == 35 && mode == 1) {
                winScreen();
                mainMenu();
            } else if (guess.length() != 1) {
                createError("Only 1 character is accepted as a valid input", "Input Error");
                c.setCursor(23, 64);
                c.print(' ', 17);
                c.setCursor(23, 64);
            } else if (alphabet.indexOf(guess) == -1) {
                createError("Please use valid alphabetical characters", "Invalid Input");
                c.setCursor(23, 64);
                c.print(' ', 1);
                c.setCursor(23, 64);
            } else if (alphaUsed.indexOf(guess) != -1) {
                createError("Letter used already!", "Invalid Input");
                c.setCursor(23, 64);
                c.print(' ', 1);
                c.setCursor(23, 64);
            } else if (word.indexOf(guess) != -1) {
                alphaUsed += guess;
                for (int j = 0; j < word.length(); j++) {
                    if (guess.equalsIgnoreCase(word.charAt(j) + "")) {
                        c.setFont(new Font("Times New Roman", 1, 23));
                        c.setColor(Color.white);
                        c.drawString(word.charAt(j) + "", start + spacing * j, 190);
                        correct++;
                        if (correct >= word.length()) {
                            try {
                                Thread.sleep(2000);
                            } catch (Exception e) {
                            }
                            if (mode == 2) {
                                int curscore = ((Integer) cache.get("endlessScore")).intValue();
                                cache.put("endlessScore", new Integer(curscore + 6 * difficulty - wrong * difficulty));
                            }
                            winScreen();
                            if (mode == 2) {
                                endGameScreen();
                            }
                            if (difficulty == 10 && mode == 1) {
                                level(1, 35);
                            } else if (mode == 2) {
                                //add something to track scores
                                level(2, difficulty);
                            } else {
                                mainMenu();
                            }
                        }
                    }
                }
                c.setCursor(23, 64);
                c.print(' ', guess.length());
                c.setCursor(23, 64);
            } else {
                c.setColor(Color.white);
                c.drawString(guess + ",", wrongStart, 230);
                wrongStart += 25;
                wrong++;
                int left = 220;
                // draws the stickman
                if (wrong == 1) {
                    c.setColor(rope);
                    c.fillOval(186, 190, 80, 80);
                    c.setColor(Color.black);
                    c.fillOval(186, 180, 80, 80);
                    c.setCursor(23, 64);
                    c.print(' ', 1);
                    c.setCursor(23, 64);
                } else if (wrong == 2) {
                    for (int i = 220; i < 360; i++) {
                        c.setColor(Color.black);
                        c.fillOval(220, i, 15, 15);
                        c.setColor(rope);
                        c.fillOval(186, 190, 80, 80);
                        c.setColor(Color.black);
                        c.fillOval(186, 180, 80, 80);
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                        }
                    }
                    c.setCursor(23, 64);
                    c.print(' ', 1);
                    c.setCursor(23, 64);
                } else if (wrong == 3) {
                    for (int i = 350; i < 420; i++) {
                        c.setColor(Color.black);
                        c.fillOval(i - 130, i, 15, 15);
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                        }
                    }
                    c.setCursor(23, 64);
                    c.print(' ', 1);
                    c.setCursor(23, 64);
                } else if (wrong == 4) {
                    for (int i = 350; i < 420; i++) {
                        c.setColor(Color.black);
                        c.fillOval(left, i, 15, 15);
                        left--;
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                        }
                    }
                    c.setCursor(23, 64);
                    c.print(' ', 1);
                    c.setCursor(23, 64);
                } else if (wrong == 5) {
                    for (int i = 270; i < 320; i++) {
                        c.setColor(Color.black);
                        c.fillOval(i - 50, i, 15, 15);
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                        }
                    }
                    c.setCursor(23, 64);
                    c.print(' ', 1);
                    c.setCursor(23, 64);
                } else if (wrong == 6) {
                    left = 220;
                    for (int i = 270; i < 320; i++) {
                        c.setColor(Color.black);
                        c.fillOval(left, i, 15, 15);
                        left--;
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException e) {
                        }
                    }
                    c.setCursor(23, 64);
                    c.print(' ', 1);
                    c.setCursor(23, 64);
                }
                //checks if game is lost
                if (wrong >= 6) {
                    loseScreen();
                    level(mode, difficulty);
                }
                createError("The letter you entered was incorrect!", "WRONG");
            }
        }
    }

    private void gameMenu() {
        /*
        Method description
        
        Prompts user to choose game mode and word bank
         */
 /*Method Variable Dictionary
    ----------------------------------------------------------------------------
    Type           | Name      | Description
    ----------------------------------------------------------------------------
    String         | gameNum   | stores user input for game mode
    ----------------------------------------------------------------------------
    String         | bankNum   | stores user input for word bank
    ----------------------------------------------------------------------------
    int            | game      | used as level method parameter and errortraps
                   |           | user input
    ----------------------------------------------------------------------------
    int            | bank      | used as level method parameter and errortraps
                   |           | user input
    --------------------------------------------------------------------------*/
        c.clear();
        String gameNum;
        int mode;
        c.drawImage((BufferedImage) ((HashMap) cache.get("assets")).get("gamemenu"), 0, 0, null);
        c.setColor(new Color(50, 205, 50));
        c.setFont(new Font("Times New Roman", 1, 40));
        c.drawString("Choose Your Game Mode", 100, 60);
        c.setFont(new Font("Times New Roman", 1, 25));
        c.drawString("Enter the corresponding number:", 140, 120);
        c.setFont(new Font("Times New Roman", 1, 30));
        c.drawString("1. Campaign Mode", 200, 210);
        c.drawString("2. Endless Mode", 220, 260);
        c.drawString("Enter Your Choice: ", 205, 378);
        c.setCursor(19, 58);

        while (true) {
            try {
                gameNum = c.readLine();
                mode = Integer.parseInt(gameNum);
                if (mode == 1 || mode == 2) {
                    break;
                }
                createError("Input must be 1 or 2", "Invalid Input");
            } catch (NumberFormatException e) {
                createError("Input must be an integer", "Invalid Input");
                gameMenu();
            }
        }

        if (mode == 1) {
            String bankNum;
            int bank;
            while (true) {
                c.clear();
                // draws the word bank manu graphic
                c.drawImage((BufferedImage) ((HashMap) cache.get("assets")).get("campaignmenu"), 0, 0, null);
                c.setColor(new Color(50, 205, 50));
                c.setFont(new Font("Times New Roman", 1, 40));
                c.drawString("Choose Your Word Bank", 100, 60);
                c.setFont(new Font("Times New Roman", 1, 25));
                c.drawString("Enter the corresponding number:", 140, 120);
                c.setFont(new Font("Times New Roman", 1, 30));
                c.drawString("1. Online Word Bank", 180, 210);
                c.drawString("2. Offline Word Bank", 178, 260);
                c.drawString("Enter Your Choice: ", 205, 378);
                c.setCursor(19, 58);

                try {
                    bankNum = c.readLine();
                    bank = Integer.parseInt(bankNum);
                    if (bank == 1) {
                        cache.put("useOnline", new Boolean(true));
                        break;
                    } else if (bank == 2) {
                        cache.put("useOnline", new Boolean(false));
                        break;
                    } else {
                        createError("You must choose either 1 or 2 for word bank", "Input Error");
                    }
                } catch (NumberFormatException e) {
                    createError("Your input was not a valid number", "Input Error");
                }
            }
            level(1, 10);
        } else if (mode == 2) {
            endlessMenu();
        }
    }

    private void endlessMenu() {
        /*
        Method description
        
        Prompts user to input difficulty and word bank to use in endless run
         */
 /*Method Variable Dictionary
    ----------------------------------------------------------------------------
    Type           | Name      | Description
    ----------------------------------------------------------------------------
    String         | diffNum   | stores user input for game difficulty
    ----------------------------------------------------------------------------
    String         | bankNum   | stores user input for word bank
    ----------------------------------------------------------------------------
    int            | diff      | used as level method parameter and errortraps
                   |           | user input
    ----------------------------------------------------------------------------
    int            | bank      | used as level method parameter and errortraps
                   |           | user input
    --------------------------------------------------------------------------*/
        String diffNum;
        int diff;
        String bankNum;
        int bank;
        c.clear();
        c.setColor(Color.black);
        //c.fillRect(0, 0, 700, 500);
        c.drawImage((BufferedImage) ((HashMap) cache.get("assets")).get("endlessmenu"), 0, 0, null);
        c.setFont(new Font("Times New Roman", 1, 35));
        c.drawString("Endless Mode", 220, 60);
        c.setFont(new Font("Times New Roman", 1, 23));
        c.drawString("Enter the corresponding numbers:", 140, 120);
        c.setFont(new Font("Times New Roman", 1, 25));
        c.drawString("1. Easy", 280, 180);
        c.drawString("2. Medium", 260, 210);
        c.drawString("3. Hard", 280, 240);
        c.drawString("4. Extreme", 260, 270);
        //c.drawString("1. Online Word Bank", 220, 330);
        //c.drawString("2. Offline Word Bank", 220, 360);
        c.setFont(new Font("Times New Roman", 1, 30));
        c.drawString("Enter Your Difficulty: ", 175, 418);
        //c.drawString("Enter Your Word Bank: ", 175, 458);
        c.setCursor(21, 59);
        while (true) {
            try {
                diffNum = c.readLine();
                diff = Integer.parseInt(diffNum);
                c.setCursor(23, 62);
                bankNum = c.readLine();
                //bank = Integer.parseInt(bankNum);
                if (diff == 1 || diff == 2 || diff == 3 || diff == 4) {
                    /*if (bank == 1) {
                        cache.put("useOnline", new Boolean(true));
                        break;
                    } else if (bank == 2) {
                        cache.put("useOnline", new Boolean(false));
                        break;
                    } else {
                        createError("You must choose either 1 or 2 for word bank", "Input Error");
                    }*/
                    break;
                } else {
                    createError("You need a valid difficulty level!", "Input Error");
                }
            } catch (NumberFormatException e) {
                createError("Invalid input(s)", "Input Error");
            }
        }
        if (diff == 1) {
            diff = 10;
        } else if (diff == 2) {
            diff = 35;
        } else if (diff == 3) {
            diff = 55;
        } else {
            diff = 70;
        }
        level(2, diff);
    }

    private void winScreen() {
        /*
        Method description
        
        Displays graphic if user passes level
         */
 /*Method Variable Dictionary
    ----------------------------------------------------------------------------
    No Local Variables Used
    --------------------------------------------------------------------------*/
        c.drawImage((BufferedImage) ((HashMap) cache.get("assets")).get("winscreen"), 0, 0, null);
        c.setCursor(-10, -10);
        c.setFont(new Font("Times New Roman", 1, 30));
        c.drawString("Press any key to continue...", 170, 480);
        c.getChar();
    }

    private void loseScreen() {
        /*
        Method description
        
        Displays graphic if user loses level
         */
 /*Method Variable Dictionary
    ----------------------------------------------------------------------------
    No Local Variables Used
    --------------------------------------------------------------------------*/
        c.drawImage((BufferedImage) ((HashMap) cache.get("assets")).get("losescreen"), 0, 0, null);
        c.setCursor(-10, -10);
        c.setFont(new Font("Times New Roman", 1, 30));
        c.drawString("Press any key to continue...", 170, 480);
        c.getChar();
    }

    private void endGameScreen() {
        /*
        Method description
        
        Displays graphic showing user score and asks if user wishes to continue
         */
 /*Method Variable Dictionary
    ----------------------------------------------------------------------------
    Type           | Name      | Desription
    ----------------------------------------------------------------------------
    int            | score     | stores user score for endless mode
    ----------------------------------------------------------------------------
    String         | scoreStr  | String value of score
    ----------------------------------------------------------------------------
    URL            | request   | opt-in usage of online leaderboard url
    ----------------------------------------------------------------------------
    HttpUrlConnection | connection | http connection to server
    ----------------------------------------------------------------------------
    BufferdReader  | in        | buffered input stream from connection
    ----------------------------------------------------------------------------
    String         | line      | Line read from bufferedreader
    ----------------------------------------------------------------------------
    PrintWriter    | out       | Output stream for local leaderboard file
    ----------------------------------------------------------------------------
    ArrayList      | lb        | ArrayList of leaderboard
    ----------------------------------------------------------------------------*/
        c.clear();
        c.drawImage((BufferedImage) ((HashMap) cache.get("assets")).get("endscreen"), 0, 0, null);
        //show final score (stored in cache)
        //redirect back to main menu
        int score = ((Integer) cache.get("endlessScore")).intValue();
        String scoreStr = String.valueOf(score);
        c.setCursor(-10, -10);
        c.setColor(Color.white);
        c.setFont(new Font("Times New Roman", Font.BOLD, 35));
        c.drawString(scoreStr, 310, 190);
        c.setFont(new Font("Times New Roman", Font.BOLD, 20));
        c.drawString("Press any key to continue playing...", 160, 440);
        c.drawString("Press x to return to Main Menu and save your score...", 100, 475);
        if (c.getChar() == 'x') {
            //sends score to remote server if mode is online
            /*if (((Boolean) cache.get("useOnline")).booleanValue()) {
                try {
                    URL request = new URL((String) ((HashMap) cache.get("config")).get("onlineLeaderBoard"));
                    HttpURLConnection connection = (HttpURLConnection) request.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setDoOutput(true);
                    connection.getOutputStream().write(("score=" + scoreStr).getBytes());
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    if (connection.getResponseCode() == 200) {
                        while (true) {
                            String line = in.readLine();
                            if (line == null) {
                                break;
                            } else if (line.indexOf("Hacking") != -1) {
                                createError("Score rejected by server anti-cheat", "Error");
                            }
                        }
                    }
                } catch (Exception e) {
                    createError("Exception occurred while sending score data to server", "Error");
                }
            }*/
            //RIP ONLINE LEADERBOARD T_T
            //writes score to local score file
            try {
                PrintWriter out = new PrintWriter(new FileWriter((String) ((HashMap) cache.get("config")).get("localLeaderBoard"), true));
                out.println(encrypt(scoreStr));
                out.close();
            } catch (IOException i) {
                createError("Failed to write new scores to file", "File Error");
            }
            //add current score to cache and sort leaderboard
            ArrayList lb = (ArrayList) cache.get("lb");
            lb.add(new Integer(score));
            Collections.sort(lb, Collections.reverseOrder());
            cache.put("lb", lb);
            //go back to menu
            mainMenu();
        }
    }
}
