package ca.mcgill.ecse211.lab3;

import static ca.mcgill.ecse211.lab1.Resources.*;

public class PController extends UltrasonicController {

  // Constants
  private static final int MOTOR_SPEED = 200;
  private static final int P_CONSTANT = 3;
  private static final int SMALL_CHANGE = 2;
  private static final int MEDIUM_CHANGE = 2;
  private static final int BIG_CHANGE = 2;


  public PController() {
    LEFT_MOTOR.setSpeed(MOTOR_SPEED);
    RIGHT_MOTOR.setSpeed(MOTOR_SPEED);
    LEFT_MOTOR.forward();
    RIGHT_MOTOR.forward();
  }

  @Override
  public void processUSData(int distance) {

    /**
     * 1. Calculate the error
     * 2. If robot is within range, don't change the speed.
     * 3. If robot is too close from wall
     * 4. If robot is far close from wall
     */

    filter(distance);


    // Initialize speed variables for both motors
    int leftMotorSpeed = 0;
    int rightMotorSpeed = 0;

    // 1. Calculate delta
    int error = Math.abs(distance - BAND_CENTER);
    int deltaSpeed = error * P_CONSTANT;

    if(deltaSpeed > 30) {
      deltaSpeed = 30;
    }

    // 2. If in acceptable range, keep on truckin'
    if (distance >= BAND_CENTER - (BAND_WIDTH / 2) && distance <= BAND_CENTER + (BAND_WIDTH / 2)) {
      leftMotorSpeed = MOTOR_SPEED;
      rightMotorSpeed = MOTOR_SPEED;
    }
    // 3. If too close, adjust a little
    else if (distance < BAND_CENTER - BAND_WIDTH / 2) {
      // 2. b) If really too close, adjust a lot
      if (distance < BAND_CENTER - BAND_WIDTH) {
        LEFT_MOTOR.setSpeed(MOTOR_SPEED + deltaSpeed * SMALL_CHANGE);
        RIGHT_MOTOR.setSpeed(MOTOR_SPEED + deltaSpeed * SMALL_CHANGE);
        LEFT_MOTOR.forward();
        RIGHT_MOTOR.backward();
        return;
      }
      leftMotorSpeed = MOTOR_SPEED + deltaSpeed * MEDIUM_CHANGE;
      rightMotorSpeed = MOTOR_SPEED - deltaSpeed / SMALL_CHANGE;
    }
    // 3. If too far
    else {
      leftMotorSpeed = MOTOR_SPEED - deltaSpeed / SMALL_CHANGE;
      rightMotorSpeed = MOTOR_SPEED + deltaSpeed * BIG_CHANGE;
    }
    
    LEFT_MOTOR.setSpeed(leftMotorSpeed);
    RIGHT_MOTOR.setSpeed(rightMotorSpeed);
    LEFT_MOTOR.forward();
    RIGHT_MOTOR.forward();
    return;
  }


  @Override
  public int readUSDistance() {
    return this.distance;
  }

}
