package a00012;

import java.util.ArrayList;
import toxi.geom.*;
import processing.core.PApplet;
import elements.*;

public class Cell {

	A00012 p;
	CapacityHandler capacityHandler;
	Octree octree;

	public ArrayList<Element> elements;
	public ArrayList<CartisianElement> cartisianElements;
	public ArrayList<Boundary> boundaries;
	public ArrayList<Element> departingElements;

	Cell(A00012 parent) {
		p = parent;
		capacityHandler = new CapacityHandler();
		elements = new ArrayList<Element>();
		cartisianElements = new ArrayList<CartisianElement>();
		boundaries = new ArrayList<Boundary>();
		departingElements = new ArrayList<Element>();
	}

	Cell(A00012 parent, float boundaryRadius) {
		p = parent;
		capacityHandler = new CapacityHandler();
		elements = new ArrayList<Element>();
		cartisianElements = new ArrayList<CartisianElement>();
		boundaries = new ArrayList<Boundary>();
		departingElements = new ArrayList<Element>();

		addBoundary(new Boundary(boundaryRadius, new Vec3D()));
	}

	void step() {

		capacityHandler.startCapacityMeasurement();
		octree = new Octree(new Vec3D(), PApplet.max(getAABB().getExtent().toArray()));
		octree.addElements(cartisianElements);

		for (int A = 0; A < elements.size(); A++) {
			Element ElementA = (Element) elements.get(A);
			for (int B = A + 1; B < elements.size(); B++) {
				Element ElementB = (Element) elements.get(B);
				ElementA.reactWith(ElementB);
			}
		}

		for (Element e : departingElements) {
			if (elements.contains(e)) {
				elements.remove(e);
			}
		}

		for (Element e : elements) {
			e.act();
		}

		capacityHandler.endCapacityMeasurement();
	}

	public void addElement(Element element) {
		if (element.type.equals("Atom"))
			cartisianElements.add((CartisianElement) element);
		if (element.type.equals("Flux"))
			cartisianElements.add((CartisianElement) element);
		elements.add(element);
	}

	void addBoundary(Boundary boundary) {
		boundary.handler.cell = this;
		boundary.handler.networkIO = p.networkIO;
		elements.add(boundary);
		boundaries.add(boundary);
	}

	public void reportDepature(Element element) {
		departingElements.add(element);
	}

	AABB getAABB() {
		Vec3D min = new Vec3D();
		Vec3D max = new Vec3D();
		for (CartisianElement e : cartisianElements) {
			min.minSelf(e.pos);
			max.maxSelf(e.pos);
		}

		return new AABB(new Vec3D(), min.abs().maxSelf(max));
	}

	public void debugCountElements(ArrayList<Element> elems) {
		int atom = 0;
		int boundary = 0;
		int flux = 0;
		for (Element e : elems) {
			if (e.type.equals("Atom"))
				atom += 1;
			if (e.type.equals("Boundary"))
				boundary += 1;
			if (e.type.equals("Flux"))
				flux += 1;
		}
		PApplet.println("Element list contains " + atom + " atoms, " + boundary
				+ " boundary and " + flux + " flux.");
	}
}
