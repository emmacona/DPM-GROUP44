package ca.mcgill.ecse211.lab3;

import static ca.mcgill.ecse211.lab3.Resources.*;
import java.io.FileNotFoundException;
import lejos.hardware.Button;

/**
 * The main class.
 */
public class Main {
<<<<<<< HEAD

	// TODO: create class(es) to handle ultrasonic controller readings
	// TODO: javadoc for everything

	/**
	 * The main entry point.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int buttonChoice;
		new Thread(odometer).start();

		buttonChoice = chooseAvoidObstaclesOrFloatMotors();

		if (buttonChoice == Button.ID_LEFT) {
			floatMotors();
		} else {
			buttonChoice = chooseAvoidObstaclesOrFloatMotors();
			if (buttonChoice == Button.ID_RIGHT) {
				new Thread(usPoller).start();
				new Thread(odometer).start();
				new Thread(obstacleAvoidance).start();
			}
		}

		new Thread(new Display()).start();
		while (Button.waitForAnyPress() != Button.ID_ESCAPE) {
		} // do nothing

		System.exit(0);
	}

	/**
	 * Floats the motors.
	 */
	public static void floatMotors() {
		leftMotor.forward();
		leftMotor.flt();
		rightMotor.forward();
		rightMotor.flt();
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
				" Float | Avoid  ",
				"motors | obst-   ",
				"       | acles ");

		do {
			buttonChoice = Button.waitForAnyPress(); // left or right press
		} while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
		return buttonChoice;
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

	/**
	 * Completes a course.
	 */
	private static void completeCourse() {
		int[][] waypoints = {{1,3}, {2 ,2}, {3, 3}, {3, 2}, {2, 1}};

		for (int[] point : waypoints) {
			Navigation.travelTo(point[0]*TILE_SIZE, point[1]*TILE_SIZE);
			while (Navigation.isNavigating()) {
				Main.sleepFor(500);
			}
		}
	}

=======
  
  /**
   * Set this to true to print to a file.
   */
  public static final boolean WRITE_TO_FILE = false;

  /**
   * Main entry point.
   * 
   * @param args
   */
  public static void main(String[] args) {

    Log.setLogging(true, true, false, true);

    if (WRITE_TO_FILE) {
      setupLogWriter();
    }
    
    new Thread(usPoller).start();
    new Thread(odometer).start();
    new Thread(obstacleAvoidance).start();

    completeCourse();

    while (Button.waitForAnyPress() != Button.ID_ESCAPE)
      ; // do nothing
    
    System.exit(0);
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
  private static void completeCourse() {
    int[][] waypoints = {{60, 30}, {30, 30}, {30, 60}, {60, 0}};

    for (int[] point : waypoints) {
      Navigation.travelTo(point[0], point[1], true);
      while (ObstacleAvoidance.isNavigating) {
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
  
>>>>>>> 97197d7002b63d7a8a3a71b458b74254bb56a373
}
