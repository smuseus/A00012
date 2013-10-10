package a00012;

import toxi.geom.Vec2D;
import elements.*;
import netP5.NetAddress;

public class BoundaryHandler {

	Boundary parent;
	NetAddress bondAddress;
	Cell cell;
	NetworkIO networkIO;

	public BoundaryHandler(Boundary Parent) {
		this.parent = Parent;
		bondAddress = new NetAddress("192.168.55.35", 12000);
	}

	public void send(Element element, Vec2D transitPosition) {
		Message message = new Message();
		message.setDestination(bondAddress);
		message.setTransitPosition(transitPosition);
		message.setElement(element);
		message.setSource(networkIO.myAddress);
		networkIO.toOutQueue(message);
		cell.reportDepature(element);
	}

	public void receive(Message message) {
		if (message.element.type.equals("Atom")) {
			Atom atom = (Atom) message.element;
			atom.pos = parent.getPosition(message.transitPosition);
			atom.pos.addSelf(atom.vel);
		}
		if (message.element.type.equals("Flux")) {
			Flux flux = (Flux) message.element;
			flux.pos = parent.getPosition(message.transitPosition);
			// flux.vel = flux.vel.getInverted();
			flux.pos.addSelf(flux.vel);
		}
		cell.addElement(message.element);
		cell.debugCountElements(cell.elements);
	}
}
