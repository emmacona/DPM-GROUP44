package src.ca.mcgill.ecse211.lab1;

import static src.ca.mcgill.ecse211.lab1.Resources.*;

public class BangBangController extends UltrasonicController {

  private static final int TOO_CLOSE = BAND_CENTER - BAND_WIDTH;

  public BangBangController() {
    LEFT_MOTOR.setSpeed(MOTOR_HIGH); // Start robot moving forward
    RIGHT_MOTOR.setSpeed(MOTOR_HIGH);
    LEFT_MOTOR.forward();
    RIGHT_MOTOR.forward();
  }

  @Override
  public void processUSData(int distance) {
    /**
     * 1. If robot is too far from the wall, right motor faster and left slower to turn to left.
     * 2. a) If robot is too close, make left motor faster and right motor slower to turn right.
     *    b) If robot is really too close, set both motor speed to fast and move backwards.
     * 3. If robot is within range, just keep both motors fast.
     */

    // Check to see if magnitude is within bounds
    filter(distance);

    // Initialize speed variables for both motors
    int leftMotorSpeed = 0;
    int rightMotorSpeed = 0;
    

    // 1. Too far
    if (distance > BAND_CENTER + BAND_WIDTH / 2) {
      leftMotorSpeed = MOTOR_LOW;
      rightMotorSpeed = MOTOR_HIGH;
    }
    // 2. a) If too close, adjust a little
    else if (distance < BAND_CENTER - BAND_WIDTH / 2) {
      // 2. b) If really too close, adjust a lot
      if (distance < TOO_CLOSE) {
        LEFT_MOTOR.setSpeed(MOTOR_HIGH);
        RIGHT_MOTOR.setSpeed(MOTOR_HIGH);
        LEFT_MOTOR.forward();
        RIGHT_MOTOR.backward();
        return;
      }
      leftMotorSpeed = MOTOR_HIGH;
      rightMotorSpeed = MOTOR_LOW;
    }
    // 3. If in acceptable range
    else {
      leftMotorSpeed = MOTOR_HIGH;
      rightMotorSpeed = MOTOR_HIGH;
    }

    // Set speed of motors
    LEFT_MOTOR.setSpeed(leftMotorSpeed);
    RIGHT_MOTOR.setSpeed(rightMotorSpeed);
    // Make robot move forward
    LEFT_MOTOR.forward();
    RIGHT_MOTOR.forward();
    return;
  }


  @Override
  public int readUSDistance() {
    return this.distance;
  }
}
