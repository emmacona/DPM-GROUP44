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
		odometer = Odometer.getOdometer(); //get the odo
		colorSampleProvider = colorSensor.getMode("Red"); // set light to red
		colorReading = new float[colorSampleProvider.sampleSize()]; // store current color reading
	}

	/*
	 * Here is where the odometer correction code should be run.
	 */


	public void run() {
		long correctionStart, correctionEnd;

		colorSampleProvider.fetchSample(colorReading, 0);
		double oldReading = colorReading[0];
		double newReading;

		while (true) {
			correctionStart = System.currentTimeMillis();

			// TODO Trigger correction (When do I have information to correct?)
			// TODO Calculate new (accurate) robot position
			// TODO yAxisdate odometer with new calculated (and more accurate) values, eg:
			//odometer.setXYT(0.3, 19.23, 5.0);

			int counter = 0; // Count number of lines
			double[] position = new double[3]; // Store position
			boolean yAxis = true; // Moving along north south axis
			boolean positive = true; // If position in positive or negative

			colorSampleProvider.fetchSample(colorReading, 0);
			newReading = colorReading[0];

			if (oldReading - newReading > 0.055) { //we are over a black line
				Sound.beep(); // make sound
				counter ++; //increase the nb of lines by 1
				position = odometer.getXYT(); //

				if(yAxis) {
					if(positive) {
						if(counter == 1) {
							odometer.setY(TILE_SIZE);
						} else if(counter == 2) {
							odometer.setY(2 * TILE_SIZE);
						} else if(counter == 3) {
							odometer.setY(3 * TILE_SIZE);
							counter = 0; // reset counter to 0
							yAxis = false; // now will move from W-E so N-S == FALSE
						}
					}
					else if(positive = false){
						if(counter == 1) {
							odometer.setY(3 * TILE_SIZE);
						} else if(counter == 2) {
							odometer.setY(2 * TILE_SIZE);
						} else if(counter == 3) {
							odometer.setY(TILE_SIZE);
							counter = 0; // reset counter to 0
							yAxis = false; // now will move from W-E so N-S == FALSE
							positive = false;
						}	
					}
				}
				else {
					if(positive) {
						if(counter == 1) {
							odometer.setX(TILE_SIZE);
						} else if( counter == 2) {
							odometer.setX(2 * TILE_SIZE);
						} else if( counter == 3) {
							odometer.setX(3 * TILE_SIZE);
							counter = 0; // reset counter to 0
							yAxis = true;
							positive = false;
						}
					}
					else if(positive = false){
						if(counter == 1) {
							odometer.setX(3 * TILE_SIZE);
						} else if( counter == 2) {
							odometer.setX(2 * TILE_SIZE);
						} else if( counter == 3) {
							odometer.setX(TILE_SIZE);
							counter = 0; // reset counter to 0
							yAxis = true;
						}	
					}
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			oldReading = newReading;


			// this ensures the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				Main.sleepFor(CORRECTION_PERIOD - (correctionEnd - correctionStart));
			}
		}
	}

}
