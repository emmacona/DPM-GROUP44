package ca.mcgill.ecse211.lab2;

import static ca.mcgill.ecse211.lab2.Resources.*;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

// extends SquareDriver
public class OdometryCorrection implements Runnable {
	private static final long CORRECTION_PERIOD = 10;
	private static final int TILE_NB = 4;
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

			colorSampleProvider.fetchSample(colorReading, 0);
			newReading = colorReading[0];

			// should only trigger when new reading has a significantly lower intensity than old
			// thus indicating a black line
			if (oldReading - newReading > 0.035) {
				Sound.beep();

				position = odometer.getXYT();
				
				// if pointed in positive y
				if(position[2] < 45 || position[2] > 315) {
					counterY++;
					odometer.setY(counterY*TILE_SIZE);
				}
				// else if pointed in positive x
				else if(position[2] < 135 && position[2] > 45) {
					counterX++;
					odometer.setX(counterX*TILE_SIZE);
				}
				// else if pointed in negative y
				else if(position[2] < 225 && position[2] > 135) {
					odometer.setY(counterY*TILE_SIZE);
					counterY--;
				}
				// else if pointed in negative x
				else if(position[2] < 315 && position[2] > 225) {
					odometer.setX(counterX*TILE_SIZE);
					counterX--;
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

			// TODO: is this part redundant?
			// this ensures the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				Main.sleepFor(CORRECTION_PERIOD - (correctionEnd - correctionStart));
			}
		}
	}

}
