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
			Sound.beep();
			// since starts at (1, 1), only want to travel 2 tile sizes for (1, 3)
			// so need to subtract 1 from all the coordinates
			travelTo((point[0]-1)*TILE_SIZE, (point[1]-1)*TILE_SIZE);
		} 
	}


	//	/**
	//	 * Travels to designated position, while constantly updating its heading.
	//	 * 
	//	 * @param x the destination x, in cm.
	//	 * @param y the destination y, in cm.
	//	 */
	//	public static void travelTo(double x, double y) {
	//		//TODO
	//		double currentX;
	//		double currentY;
	//		double deltaX;
	//		double deltaY;
	//		double minAng;
	//		// get coordinates
	//		currentX = odometer.getXYT()[0];
	//		currentY = odometer.getXYT()[1];
	//
	//		if (isDone(x,y)) {
	//			Sound.beep();
	//		}
	//
	//		while (!isDone(x, y)) { // while not at way point
	//			// get coordinates
	//			currentX = odometer.getXYT()[0];
	//			currentY = odometer.getXYT()[1];
	//			deltaX = x-currentX;
	//			deltaY = y-currentY;
	//			minAng = Math.atan2(deltaX, deltaY); // determine angle need to turn to
	//
	//			turnTo(minAng); // turn to this angle
	//			// set speeds
	//			leftMotor.setSpeed(ROTATE_SPEED);
	//			rightMotor.setSpeed(ROTATE_SPEED);
	//			leftMotor.forward();
	//			rightMotor.forward();
	//
	//			double distRemaining = distRemaining(Math.abs(currentX - x), Math.abs(currentY - y));
	//			int rotation = convertDistance(distRemaining);
	//			leftMotor.rotate(rotation, true);
	//			rightMotor.rotate(rotation, false);
	//
	//			while (!isDone(x, y)) {
	//				currentX = odometer.getXYT()[0];
	//				currentY = odometer.getXYT()[1];
	//
	//				usSensor.fetchSample(usValues, 0); // from wall following lab
	//
	//				int distanceCheck = (int) (usValues[0] * 100); // to decrease error cm --> *100
	//				// check if safe distance from a block
	//				if (distanceCheck < BAND_WIDTH) {
	//					obstacleAvoidance();
	//					break;
	//				}
	//			}
	//		}
	//	}
	/**
	 * Travels to designated position, while constantly updating its heading.
	 * 
	 * @param x the destination x, in cm.
	 * @param y the destination y, in cm.
	 */
	public static void travelTo(double x, double y) {

		double currentX;
		double currentY;
		double deltaX;
		double deltaY;
		double minAng;


		//while (!isDone(x, y)) { // while not at way point
		// get coordinates
		currentX = odometer.getXYT()[0];
		currentY = odometer.getXYT()[1];
		deltaX = x-currentX;
		deltaY = y-currentY;
		minAng = Math.atan2(deltaX, deltaY); // determine angle need to turn to

		// set speeds
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		// turn to
		turnTo(minAng); // turn to this angle

		// distance remaining to point
		double distRemaining = distRemaining(deltaX, deltaY);
		int rotation = convertDistance(distRemaining);

		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);

		leftMotor.rotate(rotation, true);
		rightMotor.rotate(rotation, false);

		leftMotor.forward();
		rightMotor.forward();

		while (!isDone(x, y)) {
			currentX = odometer.getXYT()[0];
			currentY = odometer.getXYT()[1];

			usSensor.fetchSample(usValues, 0); // from wall following lab

			int distanceCheck = (int) (usValues[0] * 100); // to decrease error cm --> *100
			// check if safe distance from a block
			if (distanceCheck < BAND_WIDTH * 100) {
				obstacleAvoidance();
				break;
			}
		}
		//}
	}

	private static void obstacleAvoidance() {
		// TODO Auto-generated method stub
		rightMotor.setSpeed(FORWARD_SPEED);
		leftMotor.setSpeed(FORWARD_SPEED);

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
		double deltaT = angle - odometer.getXYT()[2];

		//		while (abs(deltaT) > DEG_ERR) {

		deltaT = angle-odometer.getXYT()[2];

		if(deltaT < -180) {
			deltaT += 360.0;
		} else if ( deltaT > 180) {
			deltaT -= 360.0;
		}

		rightMotor.rotate(-convertAngle(deltaT), true);
		leftMotor.rotate(convertAngle(deltaT), false);
		
		rightMotor.stop();
		leftMotor.stop();
		
		//leftMotor.forward();
		//rightMotor.forward();
		//}

		//		// since angle is always positive, make currentT always positive, too
		//		double currentT = odometer.getXYT()[2];
		//		if( currentT < 0.0 ) {
		//			currentT += 360.0;
		//		}
		//
		//		double error = angle - currentT;
		//		leftMotor.setSpeed(ROTATE_SPEED);
		//		rightMotor.setSpeed(ROTATE_SPEED);

		//		if (abs(error) > DEG_ERR) {
		//			if (error > 180.0) {
		//				leftMotor.rotate(-convertAngle(360 - error), true);
		//				rightMotor.rotate(convertAngle(360 - error), false);
		//			} else if (error < -180.0) {
		//				leftMotor.rotate(convertAngle(360 + error), true);
		//				rightMotor.rotate(-convertAngle(360 + error), false);
		//			} else if (error > 0.0) {
		//				leftMotor.rotate(convertAngle(error), true);
		//				rightMotor.rotate(-convertAngle(error), false);
		//			} else if (error < 0.0) {
		//				leftMotor.rotate(-convertAngle(error), true);
		//				rightMotor.rotate(convertAngle(error), false);
		//			}
		//		}

		//	    double error = angle - odometer.getXYT()[2];
		//
		//	    while (abs(error) > DEG_ERR) {
		//	      error = angle - odometer.getXYT()[2];
		//
		//	      if (error < -180.0) {
		//	        setSpeeds(ROTATE_SPEED, -ROTATE_SPEED);
		//	      } else if (error < 0.0) {
		//	        setSpeeds(-ROTATE_SPEED, ROTATE_SPEED);
		//	      } else if (error > 180.0) {
		//	        setSpeeds(-ROTATE_SPEED, ROTATE_SPEED);
		//	      } else {
		//	        setSpeeds(ROTATE_SPEED, -ROTATE_SPEED);
		//	      }
		//	      
		//			leftMotor.forward();
		//			rightMotor.forward();
		//		}

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
	 * Returns {@code true} when done.
	 * 
	 * @param x
	 * @param y
	 * @return {@code true} when done.
	 */
	public static boolean isDone(double x, double y) {
		double error = Math.sqrt(Math.pow((odometer.getXYT()[0] - x), 2) + Math.pow((odometer.getXYT()[1] - y), 2));
		//double error = distRemaining(odometer.getXYT()[0] - x, odometer.getXYT()[1] - y);
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

	//	/**
	//	 * Sets the motor speeds jointly.
	//	 */
	//	public static void setSpeeds(float leftSpeed, float rightSpeed) {
	//		leftMotor.setSpeed(leftSpeed);
	//		rightMotor.setSpeed(rightSpeed);
	//		if (leftSpeed < 0) {
	//			leftMotor.backward();
	//		} else {
	//			leftMotor.forward();
	//		}
	//		if (rightSpeed < 0) {
	//			rightMotor.backward();
	//		} else {
	//			rightMotor.forward();
	//		}
	//	}


}
