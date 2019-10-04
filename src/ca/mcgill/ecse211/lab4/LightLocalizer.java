package ca.mcgill.ecse211.lab4;
import static ca.mcgill.ecse211.lab4.Resources.*;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

public class LightLocalizer implements Runnable {

	SampleProvider colSample = lightSensor.getMode("White"); // TODO test other colors
	float[] colorData = new float[colSample.sampleSize()];	

	public void run() {
		// TODO Auto-generated method stub

		// assumptions:
		// light sensor is in front of wheels by distance f
		// starting oriented at 0deg somewhere in bottom left square
		// i.e. after US localization has happened

		// set speed and acceleration
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.setAcceleration(ACCELERATION);
		rightMotor.setAcceleration(ACCELERATION);


		// move forward until a line is detected
		leftMotor.forward();
		rightMotor.forward();

		// get light value
		colSample.fetchSample(colorData, 0);
		float reading = colorData[0] * 1000;		// scale up for more accuracy
		Display.showText("Color: " + reading);

		while (reading >= 550) { // black line
			// update light value
			colSample.fetchSample(colorData, 0);
			reading = colorData[0] * 1000;		// scale up for more accuracy
			Display.showText("Color: " + reading);
		}


		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);

		Sound.beep();

		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);

		// turn 90deg clockwise
		leftMotor.rotate(convertAngle(90), true);
		rightMotor.rotate(-convertAngle(90), false);

		leftMotor.forward();
		rightMotor.forward();

		// move forward until a line is detected, stop
		// get light value
		colSample.fetchSample(colorData, 0);
		reading = colorData[0] * 1000;		// scale up for more accuracy
		Display.showText("Color: " + reading);

		while (reading >= 550) {
			// update light value
			colSample.fetchSample(colorData, 0);
			reading = colorData[0] * 1000;		// scale up for more accuracy
			Display.showText("Color: " + reading);
		}

		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
		Sound.beep();
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);

		// move LIGHT_TO_WHEEL cm forward
		leftMotor.rotate(convertDistance(LIGHT_TO_WHEEL), true);
		rightMotor.rotate(convertDistance(LIGHT_TO_WHEEL), false);

		// turn 90deg counterclockwise
		leftMotor.rotate(-convertAngle(90), true);
		rightMotor.rotate(convertAngle(90), false);

		// move f cm forward
		leftMotor.rotate(convertDistance(LIGHT_TO_WHEEL), true);
		rightMotor.rotate(convertDistance(LIGHT_TO_WHEEL), false);

		// done => zero odometer values
		odometer.setX(0);
		odometer.setY(0);
		odometer.setTheta(0);
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

}
