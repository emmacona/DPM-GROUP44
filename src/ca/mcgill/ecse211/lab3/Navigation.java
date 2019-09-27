package ca.mcgill.ecse211.lab3;

import static ca.mcgill.ecse211.lab3.Resources.*;
//static imports to avoid duplicating variables and make the code easier to read
import static java.lang.Math.*;

public class Navigation extends Thread {

	// Class variables
	private static boolean isNavigating; // false by default

	// Class constants
	public static double currentAngle, currentX, currentY;
	/**
	 * The odometer update period in ms.
	 */
	public static final long ODOMETER_PERIOD = 25;

	public void run() {
		long updateStart, updateEnd;

		// Get robot's current coordinates
		currentAngle = odometer.getXYT()[2];
		currentX = odometer.getXYT()[0];
		currentY = odometer.getXYT()[1];

		// This ensures that the odometer only runs once every period
		updateStart = System.currentTimeMillis();
		updateEnd = System.currentTimeMillis();

		if (updateEnd - updateStart < ODOMETER_PERIOD) {
			try {
				Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
			} catch (InterruptedException e) {
				// there is nothing to be done
			}
		}
	}


	/**
	 * 
	 * @param x
	 * @param y
	 */
	public static void travelTo(double x, double y) {
		// get position in x and y
		x *= TILE_SIZE;
		y *= TILE_SIZE;

		// displacements
		double deltaX = x - currentX;
		double deltaY = y - currentY;

		// distance to next way point
		double dist = getRemainingDistance(deltaX, deltaY);

		// minimal angle
		double minAngle = getMinAngle(deltaX, deltaY, currentAngle);

		// turn towards the next point
		turnTo(minAngle);

		// move robot
		leftMotor.setAcceleration(ACCELERATION);
		rightMotor.setAcceleration(ACCELERATION);
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		isNavigating = true; // set isNavigating to true

		leftMotor.forward();
		rightMotor.forward();

		leftMotor.rotate((int)((180.0 * dist) / (Math.PI * WHEEL_RAD)), true);
		rightMotor.rotate((int)((180.0 * dist) / (Math.PI * WHEEL_RAD)), false);

		leftMotor.stop();
		rightMotor.stop();

		isNavigating = false;
	}

	/**
	 * 
	 * @param theta
	 */
	public static void turnTo(double theta) {

		// Set motors to rotate speed
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		// Get theta
		theta = Math.toDegrees(theta);

		// turn to calculated angle
		int rotation = convertAngle(WHEEL_RAD, TRACK, theta);

		isNavigating = true;

		// rotate the appropriate direction (sign on theta accounts for direction
		leftMotor.rotate(rotation, true);
		rightMotor.rotate(-rotation, false);
		isNavigating = false;

		leftMotor.stop();
		rightMotor.stop();

	}

	/**
	 * 
	 * @return
	 */
	public static boolean isNavigating() {
		return isNavigating;
	}

	/**
	 * 
	 * Calculates the minimal angle
	 * 
	 * @param deltaX
	 * @param deltaY
	 * @param currentAngle
	 * @return minimum angle
	 */
	public static double getMinAngle(double deltaX, double deltaY, double currentAngle) {
		double minAngle;
		minAngle = Math.atan2(deltaX, deltaY) - currentAngle;
		if (minAngle > Math.PI) {
			minAngle -= 2*Math.PI;
		} else if (minAngle < -(Math.PI)){
			minAngle += 2*Math.PI;
		}
		return(minAngle);
	}

	/**
	 * 
	 * Calculates the distance to point
	 * 
	 * @param deltaX
	 * @param deltaY
	 * @param currentAngle
	 * @return minimum angle
	 */
	public static double getRemainingDistance(double deltaX, double deltaY) {
		return(Math.hypot(deltaX, deltaY));
	}

	// calculation methods
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}
