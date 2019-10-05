package ca.mcgill.ecse211.lab4;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.sensor.SensorModes;

// static import to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.lab4.Resources.*;

import ca.mcgill.ecse211.lab4.UltrasonicPoller;

/**
 * The main driver class for the navigation lab.
 */
public class Main {

	/**
	 * The main entry point.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		final TextLCD display = LocalEV3.get().getTextLCD();

		int buttonChoice;
		do {
			
			display.clear();

			display.drawString("< Left   | Right >", 0, 0);
			display.drawString("         |        ", 0, 1);
			display.drawString(" Falling | Rising ", 0, 2);
			display.drawString(" Edge    | Edge   ", 0, 3);
			display.drawString("         |		  ", 0, 4);

			buttonChoice = Button.waitForAnyPress();
			
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		// UltrasonicLocalizer usLocalizer;
		// UltrasonicPoller usPoller = new UltrasonicPoller();
		// not sure if should be threads
		new Thread(usPoller).start();
		new Thread(odometer).start();
		new Thread(new Display()).start();
		//new Thread(usPoller).start();

	    if (buttonChoice == Button.ID_LEFT) {
	    	usLocalizer.setType(UltrasonicLocalizer.LocalizationType.FALLING_EDGE);
	        new Thread(usLocalizer).start();
	    	// new Thread(new UltrasonicLocalizer(UltrasonicLocalizer.LocalizationType.FALLING_EDGE)).start();
	        // usLocalizer = new UltrasonicLocalizer(UltrasonicLocalizer.LocalizationType.FALLING_EDGE);
	    } else {
	    	new Thread(new UltrasonicLocalizer(UltrasonicLocalizer.LocalizationType.RISING_EDGE)).start();
	        // usLocalizer = new UltrasonicLocalizer(UltrasonicLocalizer.LocalizationType.RISING_EDGE);
	    }
	    

		Button.waitForAnyPress();

		// TODO: get light localization working; currently it throws some exceptions
		// LightLocalizer lightLocalizer = new LightLocalizer();
		//not sure if should be threads
		// new Thread(lightLocalizer).start();

		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);	
	}

}
