package a00012;

import processing.core.PApplet;

public class CapacityHandler {

	long observer;
	long startTime;
	long cycleDuration;
	double currentCapacityUsage;
	boolean conductingMeasurement;

	public CapacityHandler() {
		observer = 16000000;
		currentCapacityUsage = 0;
		conductingMeasurement = false;
	}

	public void startCapacityMeasurement() {
		conductingMeasurement = true;
		startTime = System.nanoTime();
	}

	public void endCapacityMeasurement() {
		if (conductingMeasurement = true) {
			cycleDuration = System.nanoTime() - startTime;
			currentCapacityUsage = (double) cycleDuration / (double) observer;
			conductingMeasurement = false;
		} else {
			PApplet.println("endCapacityMeasurement() was call without a measurement being started");
		}
	}

	void displayGraph() {
	}

	public String toString() {
		return "Observer: " + observer + "ns   Current Capacity Usage: "
				+ PApplet.nf((float) currentCapacityUsage, 1, 3)
				+ "%   Cycle duration: " + cycleDuration + "ns";
	}
}
