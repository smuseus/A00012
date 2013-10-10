package elements;

import java.util.ArrayList;
import java.util.Iterator;

import processing.core.PApplet;
import processing.data.*;

import toxi.geom.Vec3D;

public class Flux extends CartisianElement {

	public Vec3D vel;
	Vec3D acc;

	float mass;
	float relaxRange;
	float affectRange, affectAmount;
	float seperationDistance, seperationForce;
	float bondingDistance, numberOfBonds;

	private ArrayList<Flux> bonds;
	Vec3D avgBondPos;

	boolean isGhost;

	public Flux(Vec3D position) {

		mass = 0.9f;
		relaxRange = 0.05f; // Not in pixel units ( lower = longer range );
		affectRange = 50; // In normal units
		affectAmount = 2; // Degree to which Flux inherits Atoms position
							// relatice to distance between the elements
		seperationDistance = 25;
		seperationForce = 3;
		bondingDistance = 20;
		numberOfBonds = 4;

		type = "Flux";
		pos = position;
		vel = new Vec3D();
		acc = new Vec3D();
		avgBondPos = new Vec3D();
		getBonds(new ArrayList<Flux>());

		isGhost = false;
	}

	public Flux(JSONObject flux) {
		mass = flux.getFloat("mass");
		relaxRange = flux.getFloat("relaxRange");
		affectRange = flux.getFloat("affectRange");
		affectAmount = flux.getFloat("affectAmount");
		seperationDistance = flux.getFloat("seperationDistance");
		seperationForce = flux.getFloat("seperationForce");
		bondingDistance = flux.getFloat("bondingDistance");
		numberOfBonds = flux.getFloat("numberOfBonds");

		type = flux.getString("type");
		pos = new Vec3D();
		vel = new Vec3D(flux.getJSONArray("vel").getFloat(0), flux
				.getJSONArray("vel").getFloat(1), flux.getJSONArray("vel")
				.getFloat(2));
		acc = new Vec3D();
		avgBondPos = new Vec3D();
		getBonds(new ArrayList<Flux>());

	}

	public void reactWith(Element element) {
		if (element.type.equals("Atom"))
			reactWithAtom((Atom) element);
		if (element.type.equals("Boundary"))
			reactWith((Boundary) element);
		if (element.type.equals("Flux"))
			reactWithFlux((Flux) element);
	}

	public void reactWithAtom(Atom atom) {
		float affect = affectFunction(pos.distanceTo(atom.pos));
		if (affect > 0) {
			Vec3D dir = atom.pos.sub(pos).normalize(); // issue?
			vel.addSelf(dir.scale(affect));
		}
	}

	public void reactWithBoundary(Boundary boundary) {
		boundary.reactWithFlux(this);
	}

	public void reactWithFlux(Flux flux) {
		if (flux.pos.distanceTo(pos) < bondingDistance) {
			if (!getBonds().contains(flux)) {
				addBond(flux);
				flux.addBond(this);
			}
		}
	}

	public void act() {
		updateBondRelation();
		updateMovement();
	}

	void depart() {
		breakAllBonds();
	}

	void updateBondRelation() {
		breakAllGhostBonds();
		avgBondPos = new Vec3D();
		if (getBonds().size() > 0) {
			for (Flux bond : getBonds()) {
				avgBondPos.addSelf(bond.pos);
				if (seperationFunction(pos.distanceTo(bond.pos)) > 0.01) {
					Vec3D dir = pos.sub(bond.pos).normalize();
					vel.addSelf(dir.scale(seperationFunction(pos
							.distanceTo(bond.pos))));
				}
			}
			avgBondPos.scaleSelf(1 / (float) getBonds().size());
		}
	}

	void updateMovement() {
		Vec3D dir = avgBondPos.sub(pos).normalize();
		if (avgBondPos.isZeroVector())
			dir.clear();
		acc = dir.scale(relaxFunction(pos.distanceTo(avgBondPos)));
		vel.addSelf(acc);
		vel.scaleSelf(mass);
		vel.limit(2);
		pos.addSelf(vel);
		acc.clear();
	}

	void addBond(Flux flux) {
		if (!getBonds().contains(flux) && !flux.isGhost) {
			getBonds().add(flux);
			if (getBonds().size() > numberOfBonds) {
				Flux furtherst = flux;
				float maxdist = 0;
				for (Flux f : getBonds()) {
					float fdist = pos.distanceTo(f.pos);
					if (fdist > maxdist) {
						maxdist = fdist;
						furtherst = f;
					}
				}
				getBonds().remove(furtherst);
			}
		}
	}

	void removeBond(Flux flux) {
		getBonds().remove(flux);
	}

	void breakAllBonds() {
		for (Flux bond : getBonds()) {
			PApplet.println("Before: " + bond.getBonds().contains(this));
			bond.removeBond(this);
			PApplet.println("After: " + bond.getBonds().contains(this));
		}
		getBonds().clear();
	}

	void breakAllGhostBonds() {
		Iterator<Flux> i = getBonds().iterator();
		while (i.hasNext()) {
			Flux f = i.next();
			if (f.isGhost)
				i.remove();
		}
	}

	float relaxFunction(float x) {
		return PApplet.pow(x * relaxRange, 4) * 10;
	}

	float seperationFunction(float x) {
		float e = (float) Math.E;
		return seperationForce
				* PApplet.pow(
						e,
						-1
								* ((x * x) / (2 * PApplet.pow(
										(seperationDistance * 0.25f), 2))));
	}

	float affectFunction(float x) {
		float e = (float) Math.E;
		return affectAmount
				* PApplet.pow(
						e,
						-1
								* ((x * x) / (2 * PApplet.pow(
										(affectRange * 0.25f), 2))));
	}

	public JSONObject toJSON() {
		JSONObject JSONElement = new JSONObject();
		JSONElement.setString("type", type);
		JSONElement.setFloat("mass", mass);
		JSONElement.setFloat("relaxRange", relaxRange);
		JSONElement.setFloat("affectRange", affectRange);
		JSONElement.setFloat("affectAmount", affectAmount);
		JSONElement.setFloat("seperationDistance", seperationDistance);
		JSONElement.setFloat("seperationForce", seperationForce);
		JSONElement.setFloat("bondingDistance", bondingDistance);
		JSONElement.setFloat("numberOfBonds", numberOfBonds);
		JSONElement.setJSONArray(
				"vel",
				new JSONArray().setFloat(0, (float) vel.x)
						.setFloat(1, (float) vel.y).setFloat(2, (float) vel.z));
		return JSONElement;
	}

	public ArrayList<Flux> getBonds() {
		return bonds;
	}

	public void getBonds(ArrayList<Flux> bonds) {
		this.bonds = bonds;
	}
}
