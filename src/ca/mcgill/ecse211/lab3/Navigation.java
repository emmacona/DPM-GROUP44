package ca.mcgill.ecse211.lab3;
/*
 * File: Navigation.java
 * Written by: Sean Lawlor
 * ECSE 211 - Design Principles and Methods, Head TA
 * Fall 2011
 * Ported to EV3 by: Francois Ouellet Delorme
 * Fall 2015
 * Helper methods - Jonah Caplan
 * 2015
 * Refactored codebase (see GitHub for future changes) - Younes Boubekeur
 * Winter 2019
 * 
 * 
 * Movement control class (turnTo, travelTo, flt, localize)
 */

import static ca.mcgill.ecse211.lab3.Resources.*;
//static imports to avoid duplicating variables and make the code easier to read
import static java.lang.Math.*;

<<<<<<< HEAD
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
	 * Calculates the minimal angle
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

=======
import com.sun.xml.internal.bind.v2.TODO;

/**
 * Class that offers static methods used for navigation.
 */
public class Navigation {
  
  static {
    leftMotor.setAcceleration(ACCELERATION);
    rightMotor.setAcceleration(ACCELERATION);
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
   * Floats the two motors jointly.
   */
  public static void setFloat() {
    leftMotor.stop();
    rightMotor.stop();
    leftMotor.flt(true);
    rightMotor.flt(true);
  }

  /**
   * Travels to designated position, while constantly updating its heading.
   * 
   * @param x the destination x, in cm.
   * @param y the destination y, in cm.
   */
  public static void travelTo(double x, double y) {
    double minAng;
    while (!isDone(x, y)) {
      minAng = getDestAngle(x, y);
      turnTo(minAng, false);
      setSpeeds(FAST, FAST);
    }
    setSpeeds(0, 0);
  }
  
  /**
   * Travels to designated position, while constantly updating the heading.
   * @param x
   * @param y
   * @param avoid
   */
  public static void travelTo(double x, double y, boolean avoid) {
    if (avoid) {
      ObstacleAvoidance.destx = x;
      ObstacleAvoidance.desty = y;
      
      // This will trigger the state machine running in the obstacleAvoidance thread
      ObstacleAvoidance.isNavigating = true; 
    } else {
      Navigation.travelTo(x, y);
    }
  }

  /**
   * Returns {@code true} when done.
   * 
   * @param x
   * @param y
   * @return {@code true} when done.
   */
  public static boolean isDone(double x, double y) {
    return abs(x - odometer.getX()) < CM_ERR
        && abs(y - odometer.getY()) < CM_ERR;
  }

  /**
   * Returns {@code true} when facing destination.
   * 
   * @param angle
   * @return {@code true} when facing destination.
   */
  public static boolean facingDest(double angle) {
    return abs(angle - odometer.getTheta()) < DEG_ERR;
  }

  /**
   * Returns the destination angle.
   * 
   * @param x
   * @param y
   * @return the destination angle.
   */
  public static double getDestAngle(double x, double y) {
	  // atan2(sin(x-y), cos(x-y))
	  // x is the target angle. y is the source or starting angle
	  
	  //TODO figure out min angle
    
	  double minAng = (atan2(y - odometer.getY(), x - odometer.getX())) * (180.0 / PI);
    if (minAng < 0) {
      minAng += 360.0;
    }
    return minAng;
  }

  /**
   * Turns robot towards the indicated angle.
   * 
   * @param angle
   * @param stop controls whether or not to stop the motors when the turn is completed
   */
  public static void turnTo(double angle, boolean stop) {
    double error = angle - odometer.getTheta();

    while (abs(error) > DEG_ERR) {
      error = angle - odometer.getTheta();

      if (error < -180.0) {
        setSpeeds(-SLOW, SLOW);
      } else if (error < 0.0) {
        setSpeeds(SLOW, -SLOW);
      } else if (error > 180.0) {
        setSpeeds(SLOW, -SLOW);
      } else {
        setSpeeds(-SLOW, SLOW);
      }
    }

    if (stop) {
      setSpeeds(0, 0);
    }
  }
  
  /**
   * Moves robot forward a set distance in cm.
   * 
   * @param distance
   * @param avoid
   */
  public static void goForward(double distance, boolean avoid) {
    double x = odometer.getX() + cos(toRadians(odometer.getTheta())) * distance;
    double y = odometer.getY() + sin(toRadians(odometer.getTheta())) * distance;

    travelTo(x, y, avoid);
  }
>>>>>>> 97197d7002b63d7a8a3a71b458b74254bb56a373
}
