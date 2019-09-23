package ca.mcgill.ecse211.lab2;

import static ca.mcgill.ecse211.lab2.Resources.*;

import java.util.ArrayList;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

// extends SquareDriver
public class OdometryCorrection implements Runnable {
	private static final long CORRECTION_PERIOD = 10;
	private static final int LINE_NB = 3;
	private Odometer odometer;

	// set up sensor
	private static Port sensorPort = LocalEV3.get().getPort("S1"); // Set sensor port to S1
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


		while (true) {
			correctionStart = System.currentTimeMillis();

			// TODO Trigger correction (When do I have information to correct?)
			// TODO Calculate new (accurate) robot position
			// TODO Update odometer with new calculated (and more accurate) values, eg:
			//odometer.setXYT(0.3, 19.23, 5.0);

			int counter = 0; // Count number of lines
			double[] position = new double[3]; // Store position
			boolean up = true; // Moving along north southaxis
			boolean position_sign = true; // If position in positive or negative

			colorSampleProvider.fetchSample(colorReading, 0);

			if (colorReading[0] <= 0.055) { //we are over a black line
				Sound.beep(); // make sound
				counter ++; //increase the nb of lines by 1
				position = odometer.getXYT(); //

				if(up) {
					if(position_sign) {
						if(counter == 1) {
							odometer.setXYT(position[0], TILE_SIZE, position[2]);
						} else if( counter == 2) {
							odometer.setXYT(position[0], 2 * TILE_SIZE, position[2]);
						} else if( counter == 3) {
							odometer.setXYT(position[0], 3 * TILE_SIZE, position[2]);
							counter = 0; // reset counter to 0
							up = false; // now will move from W-E so N-S == FALSE
						}
					}
					else {
						if(counter == 1) {
							odometer.setXYT(position[0], 3 * TILE_SIZE, position[2]);
						} else if( counter == 2) {
							odometer.setXYT(position[0], 2 * TILE_SIZE, position[2]);
						} else if( counter == 3) {
							odometer.setXYT(position[0], TILE_SIZE, position[2]);
							counter = 0; // reset counter to 0
							up = false; // now will move from W-E so N-S == FALSE
						}	
					}
				}
				else {
					if(position_sign) {
						if(counter == 1) {
							odometer.setXYT(TILE_SIZE, position[1], position[2]);
						} else if( counter == 2) {
							odometer.setXYT(2 * TILE_SIZE, position[1], position[2]);
						} else if( counter == 3) {
							odometer.setXYT(3 * TILE_SIZE, position[1], position[2]);
							counter = 0; // reset counter to 0
							up = true;
							position_sign = false;
						}
					}
					else {
						if(counter == 1) {
							odometer.setXYT(3 * TILE_SIZE, position[1], position[2]);
						} else if( counter == 2) {
							odometer.setXYT(2 * TILE_SIZE, position[1], position[2]);
						} else if( counter == 3) {
							odometer.setXYT(TILE_SIZE, position[1], position[2]);
							counter = 0; // reset counter to 0
							up = true;
						}	
					}
				}
			}

			// this ensures the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				Main.sleepFor(CORRECTION_PERIOD - (correctionEnd - correctionStart));
			}
		}
	}

}
