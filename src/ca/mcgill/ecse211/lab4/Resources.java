package ca.mcgill.ecse211.lab4;

import ca.mcgill.ecse211.lab4.UltrasonicPoller;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

/**
 * This class is used to define static resources in one place for easy access and to avoid 
 * cluttering the rest of the codebase. All resources can be imported at once like this:
 * 
 * <p>{@code import static ca.mcgill.ecse211.lab3.Resources.*;}
 */
public class Resources {

	/**
	 * The wheel radius in centimeters.
	 */
	public static final double WHEEL_RAD = 2.130;

	/**
	 * The robot width in centimeters.
	 */
	public static final double TRACK = 11.5;
	
	/**
	 * The distance between light sensor and wheels
	 */
	public static final double LIGHT_TO_WHEEL = 4.5;


	/**
	 * The speed at which the robot moves forward in degrees per second.
	 */
	public static final int FORWARD_SPEED = 100;

	/**
	 * The speed at which the robot rotates in degrees per second.
	 */
	public static final int ROTATE_SPEED = 50;

	/**
	 * The motor acceleration in degrees per second squared.
	 */
	public static final int ACCELERATION = 1000;

	/**
	 * Timeout period in milliseconds.
	 */
	public static final int TIMEOUT_PERIOD = 3000;

	/**
	 * The tile size in centimeters.
	 */
	public static final double TILE_SIZE = 30.48;
	
	/**
	 * The degree error allowed.
	 */
	public static final double DEG_ERR = 3.0;
	
	/**
	 * The filter.
	 */
	public static final int FILTER_OUT = 5;

	/**
	 * The left motor.
	 */
	public static final EV3LargeRegulatedMotor leftMotor =
			new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));

	/**
	 * The right motor.
	 */
	public static final EV3LargeRegulatedMotor rightMotor =
			new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));

	/**
	 * The light sensor.
	 */
	public static final EV3ColorSensor lightSensor = new EV3ColorSensor(SensorPort.S4);


	/**
	 * The ultrasonic sensor.
	 */
	public static final EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(SensorPort.S1);

	/**
	 *The ultrasonic mode.
	 */
	public static SampleProvider usDistance = usSensor.getMode("Distance");

	/**
	 * The ultrasonic reading.
	 */
	public static float[] usValues = new float[usDistance.sampleSize()];

	/**
	 * The ultrasonic poller.
	 */
	public static UltrasonicPoller usPoller = new UltrasonicPoller();
	
	/**
	 * The ultrasonic localizer.
	 */
	public static UltrasonicLocalizer usLocalizer = new UltrasonicLocalizer(null);
	
	/**
	 * The light localizer.
	 */
	public static LightLocalizer lightLocalizer = new LightLocalizer();

	/**
	 * The LCD.
	 */
	public static final TextLCD LCD = LocalEV3.get().getTextLCD();

	/**
	 * The odometer.
	 */
	public static Odometer odometer = Odometer.getOdometer();

}
