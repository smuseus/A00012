package a00012;

import elements.*;
import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import toxi.processing.ToxiclibsSupport;
import toxi.geom.Vec3D;

public class A00012 extends PApplet {

	public ToxiclibsSupport gfx;
	public PeasyCam cam;
	public Cell cell;
	public NetworkIO networkIO;
	public OculusObserver observer;
	public Perception perception;
	
	boolean OCCULUS_ENABLED = false;
	boolean PEASY_ENABLED = true;
	
	public void setup() {
		//size(displayWidth, displayHeight, P3D);
		size(1280,800, P3D);
		background(3, 7, 10);
		frameRate(60);
 
		gfx = new ToxiclibsSupport(this);

		cell = new Cell(this);
		networkIO = new NetworkIO(this);
		
		if(OCCULUS_ENABLED) observer = new OculusObserver(this);
		if(PEASY_ENABLED) cam = new PeasyCam(this, 100);
		
		perception = new Perception(this);
		cell.addBoundary(new Boundary(2048, new Vec3D()));
		
		// Location, Spread, Amount, Color
		greatFluxCluster(new Vec3D(-200,-200,-200), 50, 150, color(255,152,0));
		greatFluxCluster(new Vec3D(200,200,-200), 	50, 150, color(200,0,120));
		greatFluxCluster(new Vec3D(-200,-200,200), 	50, 150, color(0,50,200));
		greatFluxCluster(new Vec3D(-200,200,200), 	50, 150, color(255,152,255));
	}

	public void draw() {
		networkIO.tick();
		cell.step();
		perception.render();
		if(OCCULUS_ENABLED) {
			observer.update();
			observer.see(perception);
		}
	}

	public void keyPressed() {
		if (key == 'a') {
			Element e = new Atom(new Vec3D(mouseX, mouseY, 0), new Vec3D(
					random(-2, 2), random(-2, 2), random(-2, 2)));
			cell.addElement(e);
		}
		if (key == 'f') {
			Flux f = new Flux(new Vec3D(mouseX, mouseY, random(-20, 20)));
			cell.addElement(f);
		}
	}
	
	public void greatFluxCluster(Vec3D location, float speard, int amount, int color) {
		for(int i=0; i < amount; i++) {
			Vec3D position = new Vec3D(location.x+random(-speard,speard), location.y+random(-speard,speard), location.z+random(-speard,speard));
			Element e = new Flux(position, color);	
			cell.addElement(e);
		}
	}
	
	public void correctOculus() {
		background(0);
		stroke(255,0,0);
		strokeWeight(5);
		point(0,0,0);
		pushMatrix();
			observer.oculus.poll();
			rotateY(observer.oculus.getYaw());
			rotateX(-observer.oculus.getPitch()*2);
			rotateZ(-observer.oculus.getRoll());
			translate(0,-25,0);
			box(15,50,15);
			translate(0,-25,10);
			box(15,20,10);
			translate(0,50,-50);
			box(12,5,30);
		popMatrix();
	}
	
	public static void main(String args[]) {
		//PApplet.main(new String[] { "--present", "a00012.A00012" });
		PApplet.main(new String[] { "--location=0,0", "a00012.A00012" });
	}
}
