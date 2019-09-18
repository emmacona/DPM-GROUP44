// Lab2.java
package ca.mcgill.ecse211.lab2;

import lejos.hardware.Button;

// static import to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.lab2.Resources.*;

/**
 * The main driver class for the odometry lab.
 */
public class Main {

  /**
   * The main entry point.
   * 
   * @param args
   */
  public static void main(String[] args) {

    int buttonChoice;

    // Odometer related objects
    OdometryCorrection odoCorrection = new OdometryCorrection(); // TODO Complete implementation
    Display display = new Display(); // No need to change

    do {
      LCD.clear();

      // ask the user whether the motors should drive in a square or float
      LCD.drawString("< Left | Right >", 0, 0);
      LCD.drawString("       |        ", 0, 1);
      LCD.drawString(" Float | Drive  ", 0, 2);
      LCD.drawString("motors | in a   ", 0, 3);
      LCD.drawString("       | square ", 0, 4);

      buttonChoice = Button.waitForAnyPress(); // left or right press
    } while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);

    if (buttonChoice == Button.ID_LEFT) {
      // Float the motors
      leftMotor.forward();
      leftMotor.flt();
      rightMotor.forward();
      rightMotor.flt();

      // Display changes in position as wheels are (manually) moved
      new Thread(odometer).start();
      new Thread(display).start();

    } else {
      LCD.clear();

      // ask the user whether odometry correction should be run or not
      LCD.drawString("< Left | Right >", 0, 0);
      LCD.drawString("  No   | with   ", 0, 1);
      LCD.drawString(" corr- | corr-  ", 0, 2);
      LCD.drawString(" ection| ection ", 0, 3);
      LCD.drawString("       |        ", 0, 4);

      buttonChoice = Button.waitForAnyPress();

      new Thread(odometer).start();
      new Thread(display).start();

      if (buttonChoice == Button.ID_RIGHT) {
        new Thread(odoCorrection).start();
      }

      // spawn a new Thread to avoid SquareDriver.drive() from blocking
      (new Thread() {
        public void run() {
          SquareDriver.drive();
        }
      }).start();
    }

    while (Button.waitForAnyPress() != Button.ID_ESCAPE)
      ; // do nothing
    
    System.exit(0);
  }
  
  /**
   * Sleeps current thread for the specified duration.
   * 
   * @param duration sleep duration in milliseconds
   */
  public static void sleepFor(long duration) {
    try {
      Thread.sleep(duration);
    } catch (InterruptedException e) {
      // There is nothing to be done here
    }
  }
  
}
