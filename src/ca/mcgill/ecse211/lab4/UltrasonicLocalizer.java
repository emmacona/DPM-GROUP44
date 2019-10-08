package ca.mcgill.ecse211.lab4;

import static ca.mcgill.ecse211.lab4.Resources.*;
import static java.lang.Math.abs;

import lejos.hardware.Sound;

public class UltrasonicLocalizer implements Runnable {


	public enum LocalizationType { FALLING_EDGE, RISING_EDGE }
	private LocalizationType type;
	private static int filterControl = 0;
	public static int distance;

	// Class constants
	private static int d = 30; 
	private static int k = 1;
	private static int d_rising = 30;
	private static int k_rising = 1;

	/**
	 * Constructor
	 * @param type (rising/falling)
	 */
	public UltrasonicLocalizer(LocalizationType type) {
		this.type = type;
	}

	/**
	 * US localizer runs from here.
	 * Type specified from Main.
	 */
	public void run() {

		// Set acceleration speed
		// prevents acceleration from going too fast by default as has happened in previous lab
		leftMotor.setAcceleration(ACCELERATION);
		rightMotor.setAcceleration(ACCELERATION);

		// Check localization type
		if (this.type == LocalizationType.RISING_EDGE) { // facing a wall
			risingEdge();
		} else { // falling edge // facing away from wall
			fallingEdge();
		}
	}

	/**
	 *  Falling edge method.
	 *  
	 * 1. Turn clockwise
	 * 2. Get alpha
	 * 	2.1 while distance > (d + k) do nothing
	 * 	   else get first alpha
	 * 	2.2 while distance > (d - k) do nothing
	 * 	   else get 2nd alpha
	 * 	2.3 Get alpha avg
	 * 3. Turn counter clockwise
	 * 4. Get beta
	 * 	4.1 while distance > (d + k) do nothing
	 * 	   else get first alpha
	 * 	4.2 while distance > (d - k) do nothing
	 * 	   else get 2nd alpha
	 * 	4.3 Get beta avg
	 * 5. Calculate delta angle
	 * 6. Add delta angle to odo reading

	 */
	private static void fallingEdge() {

		// constants used for the angles of the falling edges
		double alpha_initial, alpha_final, alpha_avg;
		double beta_initial, beta_final, beta_avg;
		double currentT;

		// constant for the difference between alpha and beta
		double deltaTheta;

		//		processUSData(distance);

		// set speeds
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		// give the us poller the chance to get a few reading in for filter purposes
		try {
			Thread.sleep(250);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// check if too close at beginning
		// if too close, turn until distance is high again
		while (distance <= d + k) {
			leftMotor.rotate(convertAngle(45), true);
			rightMotor.rotate(-convertAngle(45), false);
		}

		// check for when entering noise margin
		while (distance > d + k) {
			//keep turning
			leftMotor.forward();
			rightMotor.backward();
		}

		// used for computing the angle of the falling edge
		alpha_initial = odometer.getXYT()[2];

		// check for when exiting noise margin; this means it's a falling edge!
		while (distance > d - k) {
			// keep turning
			leftMotor.forward();
			rightMotor.backward();
		}

		// used for computing the angle of the falling edge
		alpha_final = odometer.getXYT()[2];

		// angle of the falling edge
		alpha_avg = (alpha_initial + alpha_final) / 2.0;
		if(alpha_avg == 359 + DEG_ERR || alpha_avg == 359 - DEG_ERR) {
			alpha_avg = 0.0;
		}

		leftMotor.stop(true);
		rightMotor.stop(false);
		Sound.beep();

		// other falling edge is definitely not going to be within 45 degrees
		leftMotor.rotate(-convertAngle(45), true);
		rightMotor.rotate(convertAngle(45), false);

		// turn counterclockwise until next rising edge
		leftMotor.backward();
		rightMotor.forward();

		// check for when entering noise margin
		while (distance > d + k) {
			//keep turning
			leftMotor.backward();
			rightMotor.forward();
		}

		// used for computing the angle of the falling edge
		beta_initial = odometer.getXYT()[2];

		// check for when exiting noise margin; this means it's a falling edge!
		while (distance > d - k) {
			// keep turning
			leftMotor.backward();
			rightMotor.forward();
		}

		// used for computing the angle of the falling edge
		beta_final = odometer.getXYT()[2];

		// angle of the falling edge
		beta_avg = (beta_initial + beta_final) / 2.0;
		if(beta_avg == 359 + DEG_ERR || beta_avg == 359 - DEG_ERR) {
			beta_avg = 0.0;
		}

		leftMotor.stop(true);
		rightMotor.stop(false);
		Sound.beep();

		// found alpha and beta, so turn to set theta to 0, then turn by deltaTheta
		// after the turn, angle should be correctly localized to 0 degrees
		// odometer.setTheta(0.0);

		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		// calculate halfway angle between alpha and beta
		// Values of 53 and 245 were chosen based on testing
		deltaTheta = alpha_avg > beta_avg ? (53.0 - (alpha_avg + beta_avg) / 2.0) : (245.0 - (alpha_avg + beta_avg) / 2.0);

		// turn to delta T
		leftMotor.rotate(convertAngle(deltaTheta), true);
		rightMotor.rotate(-convertAngle(deltaTheta), false);

		leftMotor.stop(true);
		rightMotor.stop(false);

		// done!
		odometer.setTheta(0);
		Sound.beepSequenceUp();

	}

	/**
	 *  Rising edge method.
	 *  
	 */
	private static void risingEdge() {
		// constants used for the angles of the falling edges
		double alpha_initial, alpha_final, alpha_avg;
		double beta_initial, beta_final, beta_avg;

		// constant for the difference between alpha and beta
		double deltaTheta;

		// set speeds
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		// give the us poller the chance to get a few reading in for filter purposes
		try {
			Thread.sleep(250);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// check if too far at beginning
		// if too far, turn until distance is low again
		while (distance >= d_rising - k_rising) {
			leftMotor.rotate(convertAngle(45), true);
			rightMotor.rotate(-convertAngle(45), false);
		}

		// check for when entering noise margin
		while (distance < d_rising - k_rising) {
			//keep turning
			leftMotor.forward();
			rightMotor.backward();
		}

		// used for computing the angle of the falling edge
		alpha_initial = odometer.getXYT()[2];
		

		// check for when exiting noise margin; this means it's a falling edge!
		while (distance < d_rising + k_rising) {
			// keep turning
			leftMotor.forward();
			rightMotor.backward();
		}

		// used for computing the angle of the falling edge
		alpha_final = odometer.getXYT()[2];

		// angle of the falling edge
		alpha_avg = (alpha_initial + alpha_final) / 2.0;
		if(alpha_avg == 359 + DEG_ERR || alpha_avg == 359 - DEG_ERR) {
			alpha_avg = 0.0;
		}

		leftMotor.stop(true);
		rightMotor.stop(false);
		Sound.beep();

		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		// other rising edge is definitely not going be within 45 degrees
		leftMotor.rotate(-convertAngle(45), true);
		rightMotor.rotate(convertAngle(45), false);

		// turn counterclockwise until next rising edge
		leftMotor.backward();
		rightMotor.forward();

		// check for when entering noise margin
		while (distance < d_rising - k_rising) {
			//keep turning
			leftMotor.backward();
			rightMotor.forward();
		}

		// used for computing the angle of the falling edge
		beta_initial = odometer.getXYT()[2];

		// check for when exiting noise margin; this means it's a falling edge!
		while (distance < d_rising + k_rising) {
			// keep turning
			leftMotor.backward();
			rightMotor.forward();
		}

		// used for computing the angle of the falling edge
		beta_final = odometer.getXYT()[2];


		// angle of the falling edge
		beta_avg = (beta_initial + beta_final) / 2.0;
		if(beta_avg == 359 + DEG_ERR || beta_avg == 359 - DEG_ERR) {
			beta_avg = 0.0;
		}

		leftMotor.stop(true);
		rightMotor.stop(false);
		Sound.beep();

		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		// calculate halfway angle between alpha and beta
		// values based on testing
		deltaTheta = alpha_avg < beta_avg ? (55.0 - (alpha_avg + beta_avg) / 2.0) : (235.0 - (alpha_avg + beta_avg) / 2.0);

		// make the turn
		leftMotor.rotate(convertAngle(deltaTheta), true);
		rightMotor.rotate(-convertAngle(deltaTheta), false);
		
		leftMotor.stop(true);
		rightMotor.stop(false);

		// done!
		odometer.setTheta(0);
		Sound.beep();
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
	public static void processUSData(int distance) {
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

	/**
	 * 
	 * @param type RISING_EDGE or FALLING_EDGE
	 */
	public void setType(LocalizationType type) {
		this.type = type;
		return;
	}

}
