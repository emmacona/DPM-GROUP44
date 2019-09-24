package ca.mcgill.ecse211.lab2;

import static ca.mcgill.ecse211.lab2.Resources.*;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

// extends SquareDriver
public class OdometryCorrection implements Runnable {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;

	// set yAxis sensor
	private static SampleProvider colorSampleProvider;// Set sample provider for color
	private static float[] colorReading;  // Color reading stored

	// Constructor
	public OdometryCorrection() {
		odometer = Odometer.getOdometer(); //get the odometer
		colorSampleProvider = colorSensor.getMode("Red"); // set light to red
		colorReading = new float[colorSampleProvider.sampleSize()]; // store current color reading
	}

	/*
	 * Here is where the odometer correction code should be run.
	 * Keeps track of position (x, y, theta)
	 * If black line is detected, adjust position in relation to tile size
	 */
	public void run() {
		long correctionStart, correctionEnd;

		// fetch the initial sample from color sensor
		colorSampleProvider.fetchSample(colorReading, 0);
		
		// since we will compare differences, we need an old reading to compare to
		// hence the use of oldReading for this initial color sensor reading
		// newReading will be used in the while loop below
		double oldReading = colorReading[0];
		double newReading;	
		
		// when travelling in POSITIVE Y-DIRECTION, increment counterY when cross black line
		// when travelling in NEGATIVE Y-DIRECTION, decrement counterY when cross black line
		// when travelling in POSITIVE X-DIRECTION, increment counterX when cross black line
		// when travelling in NEGATIVE X-DIRECTION, decrement counterX when cross black line
		// used to store how many multiples of TILE_SIZE by which to correct odometer
		int counterY = 0; // Count number of lines
		int counterX = 0; // Count number of lines

		while (true) {
			correctionStart = System.currentTimeMillis();

			double[] position = new double[3];

			colorSampleProvider.fetchSample(colorReading, 0); // fetch color reading
			newReading = colorReading[0]; // store fetch as new reading

			// should only trigger when new reading has a significantly lower intensity than old
			// thus indicating a black line
			if (oldReading - newReading > 0.035) {
				Sound.beep(); // make sound to indicate black line was detected

				position = odometer.getXYT(); // get position of odo
				
				// if pointed in positive y
				if(position[2] < 45 || position[2] > 315) { // moving up
					counterY++; //increament y counter
					odometer.setY(counterY*TILE_SIZE); // increment y position in relation to tile size
				}
				// else if pointed in positive x
				else if(position[2] < 135 && position[2] > 45) { // moving to the right
					counterX++; //increment x counter
					odometer.setX(counterX*TILE_SIZE); // increment x position in relation to tile size
				}
				// else if pointed in negative y
				else if(position[2] < 225 && position[2] > 135) { // moving down
					odometer.setY(counterY*TILE_SIZE); // decrement y position in relation to tile size
					counterY--; // this counter handles decrease in y
				}
				// else if pointed in negative x
				else if(position[2] < 315 && position[2] > 225) { // moving right to left
					odometer.setX(counterX*TILE_SIZE); // decrement x position in relation to tile size
					counterX--; // this counter handles decrease in x
				}
				
				// if detect a black line, prevent from double counting line
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			// after doing all the logic of the loop, update oldReading for the next iteration to use
			oldReading = newReading;
			
			// this ensures the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				Main.sleepFor(CORRECTION_PERIOD - (correctionEnd - correctionStart));
			}
		}
	}
}
