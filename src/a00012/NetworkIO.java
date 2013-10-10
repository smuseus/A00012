package a00012;

import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import elements.Boundary;
import oscP5.*;
import processing.core.PApplet;
import netP5.*;

public class NetworkIO {

	A00012 p;
	Queue<Message> outQueue;
	Queue<Message> inQueue;
	OscP5 oscP5;
	ArrayList<Boundary> boundaries;

	public NetAddress myAddress;

	NetworkIO(A00012 parent) {
		p = parent;
		oscP5 = new OscP5(this, 12000);
		myAddress = new NetAddress("127.0.0.1", 12000);
		outQueue = new LinkedList<Message>();
		inQueue = new LinkedList<Message>();
	}

	public void toOutQueue(Message message) {
		outQueue.offer(message);
	}

	public void toInQueue(Message message) {
		inQueue.offer(message);
	}

	public void tick() {
		Message outMessage = outQueue.poll();
		if (outMessage != null) {
			send(outMessage);
		}

		Message inMessage = inQueue.poll();
		if (inMessage != null) {
			// TODO Need an id for the boundary to know which of more to
			// receive. This inMessage is now corrupt.
			for (Boundary boundary : boundaries) {
				// TODO Does .equals() work here.
				if (inMessage.destination.equals(boundary.handler.bondAddress)) { 
					boundary.handler.receive(inMessage);
				}
			}
		}
	}

	public void oscEvent(OscMessage theOscMessage) {
		PApplet.println("recieved message");
		if (theOscMessage.checkAddrPattern("/test")) {
			if (theOscMessage.checkTypetag("s")) {
				PApplet.println(theOscMessage.get(0).stringValue());
				toInQueue(new Message(theOscMessage.get(0).stringValue()));
			} else {
				PApplet.println("[ERROR] Received unknown typetag: "
						+ theOscMessage.typetag() + " for OSC message "
						+ theOscMessage.addrPattern());
			}
		} else {
			PApplet.println("[ERROR] Received unknown OSC message with address pattern: "
					+ theOscMessage.addrPattern());
		}
	}

	public void send(Message message) {
		NetAddress destination = message.destination;
		OscMessage oscMessage = new OscMessage("/test");
		oscMessage.add(message.serialize());
		oscP5.send(oscMessage, destination);

		// oscEvent(oscMessage); // Loops into self. For debug only.
	}
}