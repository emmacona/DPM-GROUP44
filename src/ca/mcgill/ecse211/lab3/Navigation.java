package ca.mcgill.ecse211.lab3;

import static ca.mcgill.ecse211.lab3.Resources.*;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan2;

import lejos.hardware.Sound;

public class Navigation implements Runnable {

	int [][] map1 = {{1, 3}, {2 ,2}, {3, 3}, {3, 2}, {2, 1}};
	int [][] map2 = {{2, 2}, {1, 3}, {3, 3}, {3, 2}, {2, 1}};
	int [][] map3 = {{2, 1}, {3, 2}, {3, 3}, {1, 3}, {2, 2}};
	int [][] map4 = {{1, 2}, {2, 3}, {2, 1}, {3, 2}, {3, 3}};

	public static boolean isNavigating;

	public void run() {
		int[][] waypoints = map1;

		for (int[] point : waypoints) {
		  // since starts at (1, 1), only want to travel 2 tile sizes for (1, 3)
		  // so need to subtract 1 from all the coordinates
		  travelTo((point[0]-1)*TILE_SIZE, (point[1]-1)*TILE_SIZE);
		} 
	}

	/**
	 * Travels to designated position, while constantly updating its heading.
	 * 
	 * @param x the destination x, in cm.
	 * @param y the destination y, in cm.
	 */
	public static void travelTo(double x, double y) {
		//TODO
		//		isNavigating = true;

		//		// get current coordinates
		//		double currentX = odometer.getXYT()[0];
		//		double currentY = odometer.getXYT()[1];
		double currentX;
		double currentY;
		double minAng;

		/*
		if (isDone(x,y)) {
			Sound.beep();
		}
		*/

		while (!isDone(x, y)) { // while not at way point
			// get coordinates
			currentX = odometer.getXYT()[0];
			currentY = odometer.getXYT()[1];

			// since turning in place, don't need to worry about obstacles yet
			minAng = getDestAngle(x, y); // determine angle need to turn to
			turnTo(minAng); // turn to this angle

			leftMotor.forward();
			rightMotor.forward();
			
			/*
            usSensor.fetchSample(usValues, 0); // from wall following lab

            int distanceCheck = (int) (usValues[0] * 100); // to decrease error cm --> *100
            // check if safe distance from a block
            if (distanceCheck < BAND_WIDTH) {
                Sound.beep();
                obstacleAvoidance();
                // break;
            }
            */

            
			double distRemaining = distRemaining(Math.abs(currentX - x), Math.abs(currentY - y));
			int rotation = convertDistance(distRemaining);
			leftMotor.rotate(rotation, true);
			rightMotor.rotate(rotation, false);
			
			
			// Sound.beep();

			
			while (!isDone(x, y)) {
				currentX = odometer.getXYT()[0];
				currentY = odometer.getXYT()[1];

				usSensor.fetchSample(usValues, 0); // from wall following lab

				int distanceCheck = (int) (usValues[0] * 100); // to decrease error cm --> *100
				// check if safe distance from a block
				if (distanceCheck < BAND_WIDTH) {
					obstacleAvoidance();
					break;
				}
			}
			// doesn't get here yet
			Sound.beep();
		}
		// doesn't get here yet
		Sound.beep();
	}

	private static void obstacleAvoidance() {
		// TODO Auto-generated method stub
		rightMotor.setSpeed(ROTATE_SPEED);
		leftMotor.setSpeed(ROTATE_SPEED);

		// if wall turn 90 deg
		rightMotor.rotate(-90, true);
		leftMotor.rotate(90, false);

		rightMotor.forward();
		leftMotor.forward();

		// then go straight
		rightMotor.rotate(convertDistance(2*ROBOT_LENGTH));
		leftMotor.rotate(convertDistance(2*ROBOT_LENGTH));

		// when no wall 90 deg back
		rightMotor.rotate(90, true);
		leftMotor.rotate(-90, false);

		rightMotor.forward();
		leftMotor.forward();
	}

	/**
	 * Turns robot towards the indicated angle.
	 * 
	 * @param angle
	 * @param stop controls whether or not to stop the motors when the turn is completed
	 */
	public static void turnTo(double angle) {
		// since angle is always positive, make currentT always positive, too
	    double currentT = odometer.getXYT()[2];
		if( currentT < 0.0 ) {
			currentT += 360.0;
		}
		
		double error = angle - currentT;
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		if (abs(error) > DEG_ERR) {
			if (error > 180.0) {
				leftMotor.rotate(-convertAngle(error), true);
				rightMotor.rotate(convertAngle(error), false);
			} else if (error < -180.0) {
				leftMotor.rotate(convertAngle(error), true);
				rightMotor.rotate(-convertAngle(error), false);
			} else if (error > 0.0) {
				leftMotor.rotate(convertAngle(error), true);
				rightMotor.rotate(-convertAngle(error), false);
			} else if (error < 0.0) {
				leftMotor.rotate(-convertAngle(error), true);
				rightMotor.rotate(convertAngle(error), false);
			}
		}

		// setSpeeds(0, 0);
	}


	/**
	 * Dist remaining to point
	 * 
	 * @param x
	 * @param y
	 * @return dist
	 */
	public static double distRemaining(double x, double y) {
		return(Math.hypot(x, y));
	}

	/**
	 * Returns the destination angle.
	 * 
	 * @param x
	 * @param y
	 * @return the destination angle.
	 */
	public static double getDestAngle(double x, double y) {
		double minAng = (atan2(y - odometer.getXYT()[1], x - odometer.getXYT()[0])) * (180.0 / PI);
		if (minAng < 0) {
			minAng += 360.0;
		}
		return minAng;
	}

	/**
	 * Returns {@code true} when done.
	 * 
	 * @param x
	 * @param y
	 * @return {@code true} when done.
	 */
	public static boolean isDone(double x, double y) {
	  // double error = Math.sqrt(Math.pow((odometer.getXYT()[0] - x), 2) + Math.pow((odometer.getXYT()[1] - y), 2));
	  double error = distRemaining(odometer.getXYT()[0] - x, odometer.getXYT()[1] - y);
	  return error < CM_ERR;
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


}
