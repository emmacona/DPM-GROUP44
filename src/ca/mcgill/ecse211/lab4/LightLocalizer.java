package ca.mcgill.ecse211.lab4;
import static ca.mcgill.ecse211.lab4.Resources.*;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

public class LightLocalizer implements Runnable {

	SampleProvider colSample = lightSensor.getMode("Red");
	float[] colorData = new float[colSample.sampleSize()];
	int lightDifferential = 43;

	/**
	 * Light localizer runs from here.
	 * 
	 * 	1. Go straight until see black line
	 * 	2. turn to right (90°)...
	 * 	3. ...Until see black line again
	 * 	4. move forward a bit
	 * 	5. turn left (90°)
	 *	6. move forward a bit
	 *	7. set odo to 0 -- DONE.
	 */
	public void run() {
	    float oldReading = 0;
	    float newReading = 0;

		// Set motor speeds
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
		// Set acceleration
		leftMotor.setAcceleration(ACCELERATION);
		rightMotor.setAcceleration(ACCELERATION);
		
		while (true) { // go forward until see a first line
			
			// continue to move
			leftMotor.forward();
			rightMotor.forward();

			// update light sensor reading
			colSample.fetchSample(colorData, 0);
			newReading = colorData[0] * 1000;		
			
			// keep going forward until cross black line
			if (oldReading - newReading > lightDifferential) {
			  oldReading = newReading;
			  break;
			}
			oldReading = newReading;
			
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		leftMotor.stop(true);
		rightMotor.stop(false);

		Sound.beep();
		
		// Set to rotate speed / low 
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		// Make robot turn to the right
		leftMotor.rotate(convertAngle(90), true);
		rightMotor.rotate(-convertAngle(90), false);

		
		// Now we will do the same procedure but turn the other way
		// reset variables
		oldReading = 0;
	    newReading = 0;
		// Set motor speeds
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);

		while (true) { // move forward until black line is detected
			// move robot forward
			leftMotor.forward();
			rightMotor.forward();
			
			// update light sensor reading
			colSample.fetchSample(colorData, 0);
			newReading = colorData[0] * 1000;
			
	        // keep going forward until cross black line again
            if (oldReading - newReading > lightDifferential) {
              oldReading = newReading;
              break;
            }
            oldReading = newReading;
            
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		leftMotor.stop(true);
		rightMotor.stop(false);
		
		Sound.beep();
		
		// Now, place robot to center of (1,1)
		
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		// move LIGHT_TO_WHEEL cm forward
		leftMotor.rotate(convertDistance(LIGHT_TO_WHEEL), true);
		rightMotor.rotate(convertDistance(LIGHT_TO_WHEEL), false);

		// turn to left now
		leftMotor.rotate(-convertAngle(90), true);
		rightMotor.rotate(convertAngle(90), false);

		// move LIGHT_TO_WHEEL cm forward
		leftMotor.rotate(convertDistance(LIGHT_TO_WHEEL), true);
		rightMotor.rotate(convertDistance(LIGHT_TO_WHEEL), false);

		leftMotor.stop(true);
		rightMotor.stop(false);
		
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
