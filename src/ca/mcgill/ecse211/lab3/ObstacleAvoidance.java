package ca.mcgill.ecse211.lab3;

<<<<<<< HEAD
import static ca.mcgill.ecse211.lab3.Resources.odometer;
=======
//static imports to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.lab3.Resources.*;
import static ca.mcgill.ecse211.lab3.ObstacleAvoidance.State.*;

/**
 * Avoids obstacles. Implemented using a state machine.
 */
public class ObstacleAvoidance implements Runnable {

  /**
   * The possible states that the robot could be in.
   */
  enum State {
    /** The initial state. */ INIT,
    /** The turning state. */ TURNING, 
    /** The traveling state. */ TRAVELING,
    /** The emergency state. */ EMERGENCY
  };

  /**
   * The current state of the robot.
   */
  static State state;

  /**
   * {@code true} when robot is traveling.
   */
  public static boolean isNavigating = false; // boolean false by default
  
  /**
   * {@code true} when obstacle is avoided.
   */
  public static boolean safe;

  /**
   * The destination x.
   */
  public static double destx;
  
  /**
   * The destination y.
   */
  public static double desty;

  /**
   * The sleep time.
   */
  public static final int SLEEP_TIME = 50;
  
  /**
   * The distance when is in emergency state. in cm
   */
  private static final int TOO_CLOSE = 10;
  
  
  /**
   * Updates the heading.
   */
  public static void updateTravel() {
    Navigation.turnTo(Navigation.getDestAngle(destx, desty), false);
    Navigation.setSpeeds(FAST, FAST);
  }

  public void run() {
	  
	  //TODO rethink this part
	  
    state = INIT;
    while (true) {
      if (state == INIT) {
        if (isNavigating()) {
          state = TURNING;
        }
      } else if (state == TURNING) {
        double destAngle = Navigation.getDestAngle(destx, desty);
        Navigation.turnTo(destAngle, true);
        if (Navigation.facingDest(destAngle)) {
          Navigation.setSpeeds(0, 0);
          state = TRAVELING;
        }
      } else if (state == TRAVELING) {
        checkEmergency();
        if (state == EMERGENCY) { // order matters!
          new Thread(new Runnable() {
            @Override public void run() {
              Log.log(Log.Sender.avoidance,"avoiding obstacle!");
              Navigation.setSpeeds(0, 0);
              Navigation.turnTo(0,true);
              Navigation.goForward(5, false);
              Log.log(Log.Sender.avoidance,"obstacle avoided!");
              safe = true;
            }
          }).start();
        } else if (!Navigation.isDone(destx, desty)) {
          updateTravel();
        } else { // Arrived!
          Navigation.setSpeeds(0, 0);
          isNavigating = false;
          state = INIT;
        }
      } else if (state == EMERGENCY) {
        if (safe) {
          state = TURNING;
        }
      }
      Log.log(Log.Sender.Navigator, "state: " + state);
      Main.sleepFor(SLEEP_TIME);
    }
  }

  /**
   * Sets emergency state when robot is too close to a wall.
   */
  public static void checkEmergency() {
    if (usPoller.getDistance() < TOO_CLOSE) {
      state = EMERGENCY;
    }
  }
  
  /**
   * This method returns true if another thread has called travelTo() or turnTo() and the method has yet to return; 
   * false otherwise.
   */
  public static boolean isNavigating() {
    return isNavigating;
  }
>>>>>>> 97197d7002b63d7a8a3a71b458b74254bb56a373

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class ObstacleAvoidance extends Thread {
	
	//TODO change this class

	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	final TextLCD t = LocalEV3.get().getTextLCD();
	boolean navigating;
	private static final long ODOMETER_PERIOD = 25; /*odometer update period, in ms*/

	// constants
	private int FORWARD_SPEED = 200;
	private int ROTATE_SPEED = 100;
	private double axleWidth, wheelRadius;	// passed in on system startup
	private double thetaNow, xNow, yNow;	// current heading
	private double gridLength = 30.48;
	private boolean avoidingWall = false;
	private int filterControl = 0;
	private static final int FILTER_OUT = 35;

	// variables
	private int distance;

	public void run() {
		long updateStart, updateEnd;
		while (true) {
			updateStart = System.currentTimeMillis();

			getCoordinates();

			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	public void travelTo(double x, double y) {	
		// getCoordinates();
		x *= gridLength;
		y *= gridLength;

		double deltaX = x - xNow;
		double deltaY = y - yNow;

		// calculate distance between current position and next coordinate
		double distanceToNext = Math.hypot(deltaX, deltaY);

		// calculate angle between current position and next coordinate
		double thetaToNextPoint = Math.atan2(deltaX, deltaY) - thetaNow;

		// ensure the robot rotates the least amount necessary
		if (thetaToNextPoint > Math.PI) {
			thetaToNextPoint -= 2*Math.PI;
		} else if (thetaToNextPoint < -(Math.PI)){
			thetaToNextPoint += 2*Math.PI;
		}

		turnTo(thetaToNextPoint);

		leftMotor.setAcceleration(500);
		rightMotor.setAcceleration(500);
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);

		navigating = true;
		leftMotor.forward();
		rightMotor.forward();

		leftMotor.rotate(convertDistance(wheelRadius,distanceToNext), true);
		rightMotor.rotate(convertDistance(wheelRadius,distanceToNext), false);

		leftMotor.stop();
		rightMotor.stop();
		navigating = false;
	}

	public void turnTo(double theta) {	
		// slow down
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		//convert to degrees
		theta = Math.toDegrees(theta);

		//turn to calculated angle
		int rotation = convertAngle(wheelRadius, axleWidth, theta);

		navigating = true;
		// rotate the appropriate direction (sign on theta accounts for direction
		leftMotor.rotate(rotation, true);
		rightMotor.rotate(-rotation, false);
		navigating = false;

		leftMotor.stop();
		rightMotor.stop();
	}

	public boolean isNavigating() {
		return navigating;
	}

	public void getCoordinates() {
		// synchronize robot's current position
		synchronized (odometer) {
			thetaNow = odometer.getXYT()[2];
			xNow = odometer.getXYT()[0];
			yNow = odometer.getXYT()[1];
		}
	}

	// calculation methods
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

	public void processUSData(int distance) {
		// rudimentary filter - toss out invalid samples corresponding to null signal 
		if (distance >= 255 && filterControl < FILTER_OUT) {
			// bad value: do not set the distance var, do increment the filter value
			this.filterControl++;
		} else if (distance >= 255) {
			// We have repeated large values, so there must actually be nothing
			// there: leave the distance alone
			this.distance = distance;
		} else {
			// distance went below 255: reset filter and leave
			// distance alone.
			this.filterControl = 0;
			this.distance = distance;
		}

		t.drawString("US reading:            ", 0, 5);
		t.drawString("US reading: " + this.distance, 0, 5);

		double originalTheta = 0;	// note down original heading
		if (this.distance < 10 && this.distance > 0 && !avoidingWall) {
			originalTheta = thetaNow;
			avoidingWall = true;
			// turn 90 degrees right
			turnTo(Math.PI/2);

			// move half a block forward
			leftMotor.rotate(convertDistance(wheelRadius, 3*gridLength/4), true);
			rightMotor.rotate(convertDistance(wheelRadius, 3*gridLength/4), false);
		}
		// if we're still avoiding a block AND we haven't gotten back to original heading
		if (avoidingWall && thetaNow >= originalTheta - Math.PI/2) {
			// turn 90 degrees left
			turnTo(-Math.PI/2);

			// if a block is still detected, turn and continue moving alongside it
			if (this.distance <= 10) {
				// turn 90 degrees right
				turnTo(Math.PI/2);

				// move half a block forward
				leftMotor.rotate(convertDistance(wheelRadius, 3*gridLength/4), true);
				rightMotor.rotate(convertDistance(wheelRadius, 3*gridLength/4), false);

				// leftMotor.stop();
				// rightMotor.stop();
			} else {
				// if no block is seen, move half a block forward
				leftMotor.rotate(convertDistance(wheelRadius, 3*gridLength/4), true);
				rightMotor.rotate(convertDistance(wheelRadius, 3*gridLength/4), false);

				// leftMotor.stop();
				// rightMotor.stop();
			}
		}

		// this while loop is buggy and prevents other threads from operating while the program is within it
		// it is also a duplicate of the if statement above
		// ran out of time to fix this!
		while ((thetaNow >= originalTheta - Math.PI/2) && avoidingWall == true) {
			t.drawString("while loop", 0, 6);

			// turn 90 degrees left
			turnTo(-Math.PI/2);

			// check if the block is there
			if (this.distance <= 10) {
				// turn 90 degrees right
				turnTo(Math.PI/2);

				// move half a block forward
				leftMotor.rotate(convertDistance(wheelRadius, gridLength/2), true);
				rightMotor.rotate(convertDistance(wheelRadius, gridLength/2), false);
			} else {
				// move half a block forward
				leftMotor.rotate(convertDistance(wheelRadius, gridLength/2), true);
				rightMotor.rotate(convertDistance(wheelRadius, gridLength/2), false);
			}
		}
		// when the while loop is removed, this should be placed inside the second if statement
		// with another if condition around it
		avoidingWall = false;
	}


	public int readUSDistance() {
		return this.distance;
	}
}