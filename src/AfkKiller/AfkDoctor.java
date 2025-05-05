package AfkKiller;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.InputEvent;
import java.awt.MouseInfo;
import java.awt.Point;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.time.Duration;
//package AfkKiller;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.InputEvent;
import java.awt.MouseInfo;
import java.awt.Point;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.time.Duration;

//https://docs.oracle.com/javase/8/docs/api/java/awt/Robot.html 

public class AfkDoctor {
	public static final String SAVE_LOCATION = "AfkDoctor_mousePos.txt";
	private Robot robot;
	private static int mx = 1705;
	private static int my = 926;
	private static int mxt = 1735;
	private static int myt = 1000;
	private static int mxc = 1391;
	private static int myc = 653;
	
      private boolean keepGoing;
      private String nextScan;
	private static TextArea output;

	public AfkDoctor(TextArea output) {
		this.output = output;
              nextScan = "";
              keepGoing = true;
		getMPos();
		try {
			robot = new Robot();
		} catch (AWTException awtexception) {
			awtexception.printStackTrace();
		}
	}
	
	public void println(String message) {
		print("\n" + message);
	}
	public void print(String message) {
		output.append(message);
	}
	
	public static void sleep(int sec) {
		try { Thread.sleep(sec * 1000); } catch (InterruptedException e) { e.printStackTrace(); }
	}
	
	public static int getKeyCode(char key) {
		switch(key) {
		case 'w':
			return 87;
		case 's':
			return 83;
		case 'a':
			return 65;
		case 'd':
			return 68;
		default:
			return 39;
		}
	}
      public void setNextScan(String message) {
          nextScan = message;
      }
      public void endRoutine() {
          keepGoing = false;
      }
	public String input(String message) {
		println(message);
              while (nextScan.equals("")) {
                  //print("DEBUG: nextScan is " + nextScan);
                  sleep(0);
              }
              String tempScan = nextScan;
              nextScan = "";
              //println("DEBUG: input() has recieved " + nextScan);
		return tempScan;
	}
	// Python Port Begin
	public void setMPos(int hori,int veri,int horit,int verit,int horic,int veric) throws FileNotFoundException {
		PrintWriter outputFile = new PrintWriter(new File(SAVE_LOCATION));
		outputFile.write(hori + "■" + veri + "■" + horit + "■" + verit + "■" + horic + "■" + veric + "\n");
		println("Set mouse position record to (" + hori + ", " + veri + "), (" + horit + ", " + verit + 
				"), and (" + horic + ", " + veric + ").");
		mx = hori;
		my = veri;
		mxt = horit;
		myt = verit;
		mxc = horic;
		myc = veric;
		outputFile.close();
	}
	
	public void getMPos() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(SAVE_LOCATION)));
			String[] data = reader.readLine().split("■");
			mx = Integer.parseInt(data[0]);
			my = Integer.parseInt(data[1]);
			mxt = Integer.parseInt(data[2]);
			myt = Integer.parseInt(data[3]);
			mxc = Integer.parseInt(data[4]);
			myc = Integer.parseInt(data[5]);
			reader.close();
		} catch (Exception e) {
			//Throw a File Format Exception
			println("Error in data retrieval, creating fresh file");
			try { setMPos(1705,926,1735,1000,1391,653); } catch (Exception f) {}
		}
	}
	
	public void shockTherapy() {
		//"each shock will take 3.5 second" - r/DeadByDaylight
		robot.keyPress(39); // Right Click
		sleep(4);
		robot.keyRelease(39);
	}
	
	public void staticBlast() {
	    //Static blast cooldown is 30 seconds when no survivors are in its range and 45 seconds when 1+ survivors are in range
	    //"Charging Static Blast for 2 seconds will release a burst of electricity into the ground" - Dbd Wiki
		robot.keyPress(17); // Ctrl
		sleep(3);
		robot.keyRelease(17);
	}
	
	public void botFunction() {
		println("Routine started at " + LocalDateTime.now().toString());
		char direc = 'd';
		println("You have 10 seconds to open Dead by Daylight");
		sleep(10);
		try {
			LocalDateTime staticBlast = LocalDateTime.now();
			while(keepGoing) {
				// Test Key Press
				if (direc == 'w')
					direc = 'a';
				else if (direc == 'a')
					direc = 's';
				else if (direc == 's')
					direc = 'd';
				else if (direc == 'd')
					direc = 'w';
				robot.keyPress(getKeyCode(direc));
				robot.mouseMove(mx, my);
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
				sleep(2);
				robot.mouseMove(mxt, myt);
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
				sleep(2);
				robot.mouseMove(mxc, myc);
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
				sleep(2);
				//Static Blast
				if (Duration.between(staticBlast, LocalDateTime.now()).getSeconds() >= 45) {
					staticBlast();
					staticBlast = LocalDateTime.now();
				} else 
					shockTherapy();
				robot.keyRelease(getKeyCode(direc));
			}
			//println("DEBUG: " + IsKeyPressed.isQPressed());
			println("Routine ended at " + LocalDateTime.now().toString());
			robot.keyRelease(getKeyCode(direc));
		} catch (Exception e) {
			//e.printStackTrace();
			println("Routine ended at " + LocalDateTime.now().toString());
		}
	}
	
	public void automaticInput(int tar) {
		System.out.println("You have 20 seconds to get your mouse into position");
		sleep(20);
		Point mousePosition = MouseInfo.getPointerInfo().getLocation();
		int nx = (int) mousePosition.getX();
		int ny = (int) mousePosition.getY();
		try {
			if (tar == 1)
		        setMPos(nx,ny,mxt,myt,mxc,myc);
			else if (tar == 2)
		        setMPos(mx,my,nx,ny,mxc,myc);
			else if (tar == 3)
		        setMPos(mx,my,mxt,myt,nx,ny);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void manualInput(int tar) {
		int nx = Integer.parseInt(input("x position: "));
		int ny = Integer.parseInt(input("y position: "));
		try {
			if (tar == 1)
		        setMPos(nx,ny,mxt,myt,mxc,myc);
			else if (tar == 2)
		        setMPos(mx,my,nx,ny,mxc,myc);
			else if (tar == 3)
		        setMPos(mx,my,mxt,myt,nx,ny);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void classMain () {
		//Scanner scan = new Scanner(System.in);
		//Opening Print
		println("Welcome to Netra14's Dead by Daylight Doctor Bot!");
		println("     A. Activate Doctor Bot");
		println("     B. Set Autoclick Target");
		println("     C. Check registered mouse position");
		println("     D. Instructions");
		println("     E. Quit");
		String option = "■■■■■";
		while (option.toUpperCase().toCharArray()[0] != 'E') {
			option = input("Please select an option: ") + "■■■■■";
			if (option.toUpperCase().toCharArray()[0] == 'A') {
				botFunction();
                              return;
			} else if (option.toUpperCase().toCharArray()[0] == 'B') {
				String star = "";
				println("Which mouse position would you like to set?");
				while (!(star.equals("1") || star.equals("2") || star.equals("3"))) {
                                  //println("DEBUG: Your input was |" + star + "|");
                                  star = input(" Type '1', '2', or '3': ");
				}
				int tar = Integer.parseInt(star);
				println("     1. Automatic");
				println("     2. Manual");
				boolean xqx = true;
				while (xqx) {
					String choice = input("Please select an option: ");
					if (choice.toCharArray()[0] == '1') {
						xqx = false;
						automaticInput(tar);
					} else if (choice.toCharArray()[0] == '2') {
						xqx = false;
						manualInput(tar);
					} else 
						println("Please type '1' or '2'");
				}
			} else if (option.toUpperCase().toCharArray()[0] == 'C') {
				getMPos();
				println("The in-game mouse positions are registered at: (" + mx + ", " + my + "), (" + mxt + ", " + myt + 
                                      "), and (" + mxc + ", " + myc + ").");
			} else if (option.toUpperCase().toCharArray()[0] == 'D') {
				println("In Dead by Daylight's lobby, there is a small space on the screen at the top of the ready button where an autoclicker will ready up but not cancel the match.");
				println("In the menu of this program, type 'B'. Then, in the submenu that comes up, type '1'.");
				println("Once you enter the command you have 20 seconds to open Dead by Daylight and hover your mouse over the top of the ready button as mentioned above.");
				println("Once 20 seconds are up, the program will record your mouse's position and the bot will use that position to enter games automatically.\n");
				println("Now that the autoclicker is set up you're good to go! Make sure you're using default keyboard/mouse controls. Select 'The Doctor' in Dead by Daylight then go back to this program.");
				println("Enter 'A' into the menu, then you will have 10 seconds to enter Dead by Daylight. After that the bot should be working! Press and hold Q when you're done to exit.\n");
				println("OPTIONAL: I reccomend you run the perks 'Deadlock', 'Hex: No One Escapes Death', 'Hex: Thrill of the Hunt', and 'Distressing'. I do not reccomend bringing any bloodpoint offerings.");
			}
		}
	}
	public static void begin(AfkDoctor me) { //
          //me.println("DEBUG: begin() flag");
          me.classMain();
      }
}
