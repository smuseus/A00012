package a00012;

import elements.*;
import processing.core.*;
import processing.data.*;
import toxi.geom.*;
import netP5.*;

public class Message {

	public Element element;
	NetAddress destination;
	NetAddress source;
	public Vec2D transitPosition;

	public Message() {
	}

	Message(String OscMessage) {
		JSONObject JSONMessage = JSONObject.parse(OscMessage);
		destination = new NetAddress(JSONMessage.getString("destination"),
				JSONMessage.getInt("destinationPort"));
		source = new NetAddress(JSONMessage.getString("source"),
				JSONMessage.getInt("sourcePort"));
		setDestination(destination);
		setSource(source);
		setTransitPosition(new Vec2D(JSONMessage
				.getJSONArray("transitPosition").getFloat(0), JSONMessage
				.getJSONArray("transitPosition").getFloat(1))); // This might be
																// buggy
		
		if (JSONMessage.getJSONObject("element").getString("type")
				.equals("Atom")) {
			Atom atom = new Atom(JSONMessage.getJSONObject("element"));
			setElement((Element) atom);
		} else if (JSONMessage.getJSONObject("element").getString("type")
				.equals("Flux")) {
			Flux flux = new Flux(JSONMessage.getJSONObject("element"));
			setElement((Element) flux);
		} else {
			PApplet.println("The OscMessage is corrupt or the message class does not how to handle it.");
		}
	}

	public void setElement(Element element) {
		this.element = element;
	}

	public void setDestination(NetAddress dest) {
		this.destination = dest;
	}

	public void setSource(NetAddress source) {
		this.source = source;
	}

	public void setTransitPosition(Vec2D transPos) {
		this.transitPosition = transPos;
	}

	public JSONObject toJSON() {
		JSONObject jsonMessage = new JSONObject();
		jsonMessage.setJSONObject("element", element.toJSON());
		jsonMessage.setString("destination", destination.address());
		jsonMessage.setInt("destinationPort", destination.port());
		jsonMessage.setString("source", source.address());
		jsonMessage.setInt("sourcePort", source.port());
		jsonMessage.setJSONArray(
				"transitPosition",
				new JSONArray().setFloat(0, transitPosition.x).setFloat(1,
						transitPosition.y));
		return jsonMessage;
	}

	public String serialize() {
		return toJSON().toString();
	}

}