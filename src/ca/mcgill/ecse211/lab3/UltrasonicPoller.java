package ca.mcgill.ecse211.lab3;

import static ca.mcgill.ecse211.lab3.Resources.*;

/**
 * Samples the US sensor and invokes the selected controller on each cycle.
 * 
 * Control of the wall follower is applied periodically by the UltrasonicPoller thread. The while
 * loop at the bottom executes in a loop. Assuming that the us.fetchSample, and cont.processUSData
 * methods operate in about 20ms, and that the thread sleeps for 50 ms at the end of each loop, then
 * one cycle through the loop is approximately 70 ms. This corresponds to a sampling rate of 1/70ms
 * or about 14 Hz.
 */
public class UltrasonicPoller implements Runnable {
  private float[] usData = new float[usSensor.sampleSize()];
  int distance;

  // Sensors now return floats using a uniform protocol.
  // Need to convert US result to an integer [0,255]

  public void run() {
    while (true) {
      usSensor.fetchSample(usData, 0); // acquire data
      distance = (int) (usData[0] * 100.0); // extract from buffer, convert to cm, cast to int

      Integer.toString(distance);

      Main.sleepFor(50);
    }
  }

  public int getDistance() {
    return distance;
  }

}
