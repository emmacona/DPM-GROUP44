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
	private static final int LINE_COUNT = 3;
	private Odometer odometer;

	// set up sensor
	private static Port sensorPort = LocalEV3.get().getPort("S1"); // 1. Get port 
	//private static SensorModes colorSensor;// 2. Get sensor instance 
	private static SampleProvider colorSampleProvider;// 3. Get sample provider 
	private static float[] sampleTouch;  // 4. Create data buffer

	private static int counter = 0;
	private static ArrayList<Double> samples = new ArrayList<Double>();

	// Constructor
	public OdometryCorrection() {
		odometer = Odometer.getOdometer();
		//sensorPort = LocalEV3.get().getPort("S1");
		//sensorPort = SensorPort.S4;
		//colorSensor = new EV3ColorSensor(LocalEV3.get().getPort("S1"));
		//colorSensor = new EV3ColorSensor(sensorPort);
		colorSampleProvider = colorSensor.getMode("Red");
		sampleTouch = new float[colorSampleProvider.sampleSize()];
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

			colorSampleProvider.fetchSample(sampleTouch, 0);

			// TODO: it beeps constantly, which means it thinks it's constantly hitting a black line
			// if it is very near ground, it doesn't read as black, so probably just need to position sensor really low
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
