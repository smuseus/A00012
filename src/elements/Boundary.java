package elements;

import a00012.*;
import processing.core.PApplet;
import netP5.NetAddress;
import toxi.geom.*;

public class Boundary extends Element {

	public BoundaryHandler handler;
	float radius;
	Vec3D pos;

	public Boundary(float radius, Vec3D pos) {
		type = "Boundary";
		handler = new BoundaryHandler(this);
		this.radius = radius;
		this.pos = pos;
	}

	public void reactWith(Element element) {
		if (element.type.equals("Atom"))
			reactWithAtom((Atom) element);
		if (element.type.equals("Flux"))
			reactWithFlux((Flux) element);
	}

	void reactWithAtom(Atom atom) {
		if (atom.pos.distanceTo(pos) >= radius) {
			handler.send(atom, getUV(atom.pos));
		}
	}

	void reactWithFlux(Flux flux) {
		if (flux.pos.distanceTo(pos) >= radius) {
			flux.isGhost = true;
			handler.send(flux, getUV(flux.pos));
		}
	}

	public float getTetha(Vec2D elementPos) {
		Vec2D v = elementPos.sub(pos.to2DXY());
		return PApplet.atan2(v.x, v.y);
	}

	public Vec2D getUV(Vec3D elementsPos) {
		return new Vec2D();
	}

	public Vec3D getPosition(Vec2D UV) {
		return new Vec3D();
	}

	public Vec2D getPosition(float theta) {
		Vec2D v = new Vec2D();
		v.y = -v.y;
		v = v.getInverted();
		return v.addSelf(pos.to2DXY());
	}

	Vec2D getNormalAtTheta(float theta) {
		Vec2D v = new Vec2D();
		v.y = -v.y;
		return v.getInverted();
	}
}
