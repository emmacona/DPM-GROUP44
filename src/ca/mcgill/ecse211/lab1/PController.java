package ca.mcgill.ecse211.lab1;

import static ca.mcgill.ecse211.lab1.Resources.*;

public class PController extends UltrasonicController {

  private static final int MOTOR_SPEED = 200;

  public PController() {
    LEFT_MOTOR.setSpeed(MOTOR_SPEED); // Initialize motor rolling forward
    RIGHT_MOTOR.setSpeed(MOTOR_SPEED);
    LEFT_MOTOR.forward();
    RIGHT_MOTOR.forward();
  }

  @Override
  public void processUSData(int distance) {
    filter(distance);

    // TODO: process a movement based on the us distance passed in (P style)

    int diff = distance - BAND_CENTER - (BAND_WIDTH / 2);
    int deltaSpeed = 0;

    if (diff > BAND_WIDTH) { // Robot is too far from wall
      if (diff > 28) { // Robot is very far from wall
        deltaSpeed = 100 + (500 / diff);

        // Make wider turn
        LEFT_MOTOR.setSpeed(MOTOR_SPEED - (deltaSpeed / 2));
        RIGHT_MOTOR.setSpeed(MOTOR_SPEED + (deltaSpeed / 2));
        LEFT_MOTOR.forward();
        RIGHT_MOTOR.forward();
        return;
      }

      deltaSpeed = (diff * 3);
    } else if (diff < BAND_WIDTH) { // Robot is too close to wall
      if (diff < -5) { // Robot is very close to wall
        deltaSpeed = -140 + diff;

        // Change direction in place
        LEFT_MOTOR.setSpeed(deltaSpeed);
        RIGHT_MOTOR.setSpeed(Math.abs(deltaSpeed));
        LEFT_MOTOR.forward();
        RIGHT_MOTOR.backward();
        return;
      }

      deltaSpeed = (diff * 8);
    } else { // Within band
      deltaSpeed = 0;
    }

    LEFT_MOTOR.setSpeed(MOTOR_SPEED - deltaSpeed);
    RIGHT_MOTOR.setSpeed(MOTOR_SPEED + deltaSpeed);
    LEFT_MOTOR.forward();
    RIGHT_MOTOR.forward();
  }


  @Override
  public int readUSDistance() {
    return this.distance;
  }

}
