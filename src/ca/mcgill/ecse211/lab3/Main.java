// Lab2.java
package ca.mcgill.ecse211.lab3;

import lejos.hardware.Button;

// static import to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.lab3.Resources.*;

/**
 * The main driver class for the odometry lab.
 */
public class Main {

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

}
