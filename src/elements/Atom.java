package elements;

import processing.data.*;
import toxi.geom.Vec3D;

public class Atom extends CartisianElement {
	
	public Vec3D vel;
	
	Atom() {
		type = "Atom";
	}

	public Atom(Vec3D position, Vec3D velocity) {
		type = "Atom";
		pos = position;
		vel = velocity;
	}

	public Atom(JSONObject atom) {
		type = atom.getString("type");
		pos = new Vec3D();
		vel = new Vec3D(atom.getJSONArray("vel").getFloat(0), atom
				.getJSONArray("vel").getFloat(1), atom.getJSONArray("vel")
				.getFloat(2));
	}

	public void reactWith(Element element) {
		if (element.type.equals("Boundary"))
			reactWithBoundary((Boundary) element);
	}

	public void reactWith(Boundary element) {
		if (element.type.equals("Boundary"))
			reactWithBoundary((Boundary) element);
	}

	public void reactWithBoundary(Boundary boundary) {
		boundary.reactWithAtom(this);
	}

	public void act() {
		pos.addSelf(vel);
	}

	public JSONObject toJSON() {
		JSONObject JSONElement = new JSONObject();
		JSONElement.setString("type", type);
		JSONElement.setJSONArray("vel", new JSONArray().setFloat(0, vel.x)
				.setFloat(1, vel.y).setFloat(2, vel.z));
		return JSONElement;
	}
}
