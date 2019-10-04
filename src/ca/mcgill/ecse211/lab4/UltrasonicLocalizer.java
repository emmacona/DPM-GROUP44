package ca.mcgill.ecse211.lab4;

import static ca.mcgill.ecse211.lab4.Resources.*;
import static java.lang.Math.abs;

import lejos.hardware.Sound;

public class UltrasonicLocalizer {


	public enum LocalizationType { FALLING_EDGE, RISING_EDGE }
	private LocalizationType type;
	private static Odometer odometer;
	private static int filterControl = 0;
	public static int distance;

	// Constants
	//TODO Test for optimal d and k values -- see tutorial slides
	private static int d = 40; 
	private static int k = 1;	

	/**
	 * Constructor
	 * @param type (rising/falling)
	 */
	public UltrasonicLocalizer(LocalizationType type) {
		this.type = type;
	}

	/**
	 * TODO
	 */
	public void run() {
		// TODO Auto-generated method stub

		// set speeds of motors to low/rotation speed
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		// set acceleration 
		// (prevents acceleration from going too fast by default as has happened in previous lab)
		leftMotor.setAcceleration(ACCELERATION);
		rightMotor.setAcceleration(ACCELERATION);

		if (this.type == LocalizationType.RISING_EDGE) {
			risingEdge();
		} else { // falling edge
			fallingEdge();
		}

	}

	/**
	 *  Falling edge method.
	 */
	private static void fallingEdge() {
		// TODO CHANGE THIS A LOT

		// avoid finding falling edge too early
		leftMotor.rotate(convertAngle(45), true);
		rightMotor.rotate(-convertAngle(45), false);

		// rotate clockwise
		leftMotor.forward();
		rightMotor.backward();

		// keep turning until wall drops away
		while (distance >= d - k) {
			//keep turning
		}

		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
		Sound.beep();

		// start counting change in angle
		odometer.setTheta(0);

		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		// avoid finding falling edge too early
		leftMotor.rotate(-convertAngle(45), true);
		rightMotor.rotate(convertAngle(45), false);

		// turn counterclockwise until next rising edge
		leftMotor.backward();
		rightMotor.forward();

		while (distance >= d - k) {
			// keep turning
		}

		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
		Sound.beep();

		// record change in angle
		double deltaT = Math.abs(odometer.getXYT()[2]);

		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		// turn to minimal angle
		// TODO test that -- change accordingly, Math.PI could be divided more if falls short
		turnTo(deltaT/2 - Math.PI/4);

		odometer.setTheta(0);
		Sound.beep();

	}

	/**
	 *  Rising edge method.
	 */
	private static void risingEdge() {
		// TODO CHANGE THIS A LOT

		// avoid finding rising edge too early
		leftMotor.rotate(convertAngle(45), true);
		rightMotor.rotate(-convertAngle(45), false);

		// rotate clockwise
		leftMotor.forward();
		rightMotor.backward();

		// keep turning until wall drops away
		while (distance <= d + k) {
			//keep turning
		}

		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
		Sound.beep();

		// start counting change in angle
		odometer.setTheta(0);

		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		// avoid finding rising edge too early
		leftMotor.rotate(-convertAngle(45), true);
		rightMotor.rotate(convertAngle(45), false);

		// turn counterclockwise until next rising edge
		leftMotor.backward();
		rightMotor.forward();

		while (distance <= d + k) {
			// keep turning
		}

		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
		Sound.beep();

		// record change in angle
		double deltaT = Math.abs(odometer.getXYT()[2]);

		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		// turn to minimal angle
		// TODO test  and change
		turnTo(-(5*Math.PI/4 - deltaT/2));

		odometer.setTheta(0);
		Sound.beep();
	}


	/**
	 * Turns robot towards the indicated angle.
	 * 
	 * @param angle
	 * @param x, the goal x location
	 * @param y, the goal y location
	 */
	public static void turnTo(double angle) {
		double theta = odometer.getXYT()[2]; // Get current theta reading
		double error = angle - theta; // Calculate what's left to get to the wanted angle

		while (abs(error) > DEG_ERR) { // while we are not close enough to goal angle
			if (error > 180.0) { // if bigger than 180, turn right
				setSpeeds(-ROTATE_SPEED, ROTATE_SPEED);
			} else if (error < -180.0) { // if smaller than negative 180, turn left
				setSpeeds(ROTATE_SPEED, -ROTATE_SPEED);
			} else if (error > 0.0) { // if bigger than 0, turn left
				setSpeeds(ROTATE_SPEED, -ROTATE_SPEED);
			} else { // if smaller than 0, turn right
				setSpeeds(-ROTATE_SPEED, ROTATE_SPEED);
			}
			theta = odometer.getXYT()[2]; // update current theta reading
			error = angle - theta; // update error
		}
	}

	/**
	 * Converts input distance to the total rotation of each wheel needed to cover that distance.
	 * 
	 * @param distance
	 * @return the wheel rotations necessary to cover the distance
	 */
	public static int convertDistance(double distance) {
		return (int) ((180.0 * distance) / (Math.PI * WHEEL_RAD));
	}

	/**
	 * Converts input angle to the total rotation of each wheel needed to rotate the robot by that
	 * angle.
	 * 
	 * @param angle
	 * @return the wheel rotations necessary to rotate the robot by the angle
	 */
	public static int convertAngle(double angle) {
		return convertDistance(Math.PI * TRACK * angle / 360.0);
	}

	/**
	 * Sets the motor speeds jointly.
	 */
	public static void setSpeeds(float leftSpeed, float rightSpeed) {
		leftMotor.setSpeed(leftSpeed);
		rightMotor.setSpeed(rightSpeed);
		if (leftSpeed < 0) {
			leftMotor.backward();
		} else {
			leftMotor.forward();
		}
		if (rightSpeed < 0) {
			rightMotor.backward();
		} else {
			rightMotor.forward();
		}
	}


	/**
	 * Rudimentary filter - toss out invalid samples corresponding to null signal.
	 * @param distance distance in cm
	 * 
	 * Taken from lab1
	 */
	public void processUSData(int distance) {
		if (distance >= 255 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the filter value
			filterControl++;
		} else if (distance >= 255) {
			// Repeated large values, so there is nothing there: leave the distance alone
			UltrasonicLocalizer.distance = distance;
		} else {
			// distance went below 255: reset filter and leave distance alone.
			filterControl = 0;
			UltrasonicLocalizer.distance = distance;
		}
	}

	/**
	 * 
	 * @return ultrasonic sensor reading
	 * From lab 1
	 */
	public int readUSDistance() {
		return UltrasonicLocalizer.distance;
	}

}
