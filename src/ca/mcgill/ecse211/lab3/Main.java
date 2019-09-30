package ca.mcgill.ecse211.lab3;

import static ca.mcgill.ecse211.lab3.Resources.*;
import java.io.FileNotFoundException;
import lejos.hardware.Button;
import lejos.hardware.Sound;

/**
 * The main class.
 */
public class Main {
  
  /**
   * Set this to true to print to a file.
   */
  public static final boolean WRITE_TO_FILE = false;

  /**
   * Main entry point.
   * 
   * @param args
   */
  
	/**
	 * The main entry point.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int buttonChoice;
	    
		Log.setLogging(true, true, false, true);

	    if (WRITE_TO_FILE) {
	      setupLogWriter();
	    }

		buttonChoice = chooseAvoidObstaclesOrFloatMotors();

		if (buttonChoice == Button.ID_LEFT) {
			new Thread(usPoller).start();
			new Thread(odometer).start();
			completeCourse(false);
		} else {
			buttonChoice = chooseAvoidObstaclesOrFloatMotors();
			if (buttonChoice == Button.ID_RIGHT) {
				new Thread(usPoller).start();
				new Thread(odometer).start();
				new Thread(obstacleAvoidance).start();
				completeCourse(true);
			}
		}

		new Thread(new Display()).start();
		
		Sound.beep();
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE) {
		} // do nothing

		System.exit(0);
	}

  
	/**
	 * Asks the user whether the motors should drive in a square or float.
	 * 
	 * @return the user choice
	 */
	private static int chooseAvoidObstaclesOrFloatMotors() {
		int buttonChoice;
		Display.showText("< Left | Right >",
				"       |        ",
				" Navi- | Avoid  ",
				"gation | obst-  ",
				"       | acles  ");

		do {
			buttonChoice = Button.waitForAnyPress(); // left or right press
		} while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
		return buttonChoice;
	}

  public static void setupLogWriter() {
    try {
      Log.setLogWriter(System.currentTimeMillis() + ".log");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Completes a course.
   */
  private static void completeCourse(boolean avoid) {
    int[][] waypoints = {{2, 2}, {3, 1}};
    
    for (int[] point : waypoints) {
      Navigation.travelTo(point[0]*TILE_SIZE, point[1]*TILE_SIZE, avoid);
      
      while (ObstacleAvoidance.traveling) {
        Main.sleepFor(500);
      }
    }
  }
  
  /**
   * Sleeps current thread for the specified duration.
   * 
   * @param duration sleep duration in milliseconds
   */
  public static void sleepFor(long duration) {
    try {
      Thread.sleep(duration);
    } catch (InterruptedException e) {
      // There is nothing to be done here
    }
  }
  
}
