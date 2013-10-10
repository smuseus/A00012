package elements;

import processing.data.JSONObject;

public class Element {
	public String type;

	Element() {
		type = "Element";
	}

	public void reactWith(Element element) {
	}

	public void act() {
	}

	public JSONObject toJSON() {
		return new JSONObject();
	}
}
