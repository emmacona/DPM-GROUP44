package ca.mcgill.ecse211.lab2;

import static ca.mcgill.ecse211.lab2.Resources.*;

import java.util.ArrayList;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class OdometryCorrection extends SquareDriver implements Runnable {
	private static final long CORRECTION_PERIOD = 10;
	private static final int LINE_COUNT = 3;
	private Odometer odometer;

	// set up sensor
	private static Port portTouch = LocalEV3.get().getPort("S1");// 1. Get port 
	private static SensorModes mylight = new EV3ColorSensor(portTouch);// 2. Get sensor instance 
	private static SampleProvider myColour = mylight.getMode("Red");// 3. Get sample provider 
	private static float[] sampleTouch = new float[myColour.sampleSize()];  // 4. Create data buffer

	private static int counter = 0;
	private static ArrayList<Double> samples = new ArrayList<Double>();

	// Constructor
	public OdometryCorrection() {
		this.odometer = Odometer.getOdometer();

	}

	/*
	 * Here is where the odometer correction code should be run.
	 */


	public void run() {
		long correctionStart, correctionEnd;

		
		while (true) {
			correctionStart = System.currentTimeMillis();

			// TODO Trigger correction (When do I have information to correct?)
			for (int i = 0; i < (LINE_COUNT) * 4; i++) {
				if (i<(LINE_COUNT-1)) {
					samples.add(i*TILE_SIZE);
				} else if (i < (LINE_COUNT) * 2) {
					samples.add((i % 3)*TILE_SIZE);
				} else if (i < (LINE_COUNT) * 3) {
					samples.add((LINE_COUNT - 1 - i % 3) * TILE_SIZE);
				} else if (i < (LINE_COUNT) * 4) {
					samples.add((LINE_COUNT - 1 - i % 3) * TILE_SIZE);
				}
			}

			// TODO Calculate new (accurate) robot position

			// TODO Update odometer with new calculated (and more accurate) values, eg:
			//odometer.setXYT(0.3, 19.23, 5.0);

			myColour.fetchSample(sampleTouch, 0);

			if ( sampleTouch[0] <= 0.35) {
				//we are over a black line
				LCD.clear();
				//System.out.println(sampleTouch[0]);

				Sound.beep();
				if (counter < (LINE_COUNT) || (counter >= (LINE_COUNT)*2 && counter < (LINE_COUNT)*3)) {
					odometer.setY(samples.get(counter));
				} else {
					odometer.setX(samples.get(counter));
				}
				counter++;
			}

			// this ensures the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				Main.sleepFor(CORRECTION_PERIOD - (correctionEnd - correctionStart));
			}
		}
	}

}
