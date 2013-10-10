package a00012;

import java.util.ArrayList;

import elements.*;
import processing.core.PGraphics;
import processing.core.PImage;
import toxi.geom.AABB;
import toxi.geom.Vec3D;
import toxi.processing.ToxiclibsSupport;

class Perception {

	A00012 p;
	PImage txtr;
	
	Perception(A00012 parent) {
		p = parent;
		txtr = p.loadImage("uv_map_reference.jpg");
	}
	
	void render(PGraphics pass) {
		p.gfx.setGraphics(pass);
		pass.beginDraw();
			pass.background(0);
		
			pass.pushMatrix();
				pass.scale(200);
				TexturedCube(pass, txtr);
			pass.popMatrix();
			
			boolean fluxLines = true;
			
			pass.stroke(255, 0, 0);
			if (true) { // Render Pivot Point
				pass.strokeWeight(10);
				pass.point(0, 0, 0);
			}
			if (true) { // Render Cell Axis-Aligned Bounding Box (AABB)
				pass.strokeWeight(1);
				pass.noFill();
				p.gfx.mesh(p.cell.getAABB().toMesh());
			}
			if (p.cell.octree != null)
				p.cell.octree.draw();
		
			for (Element e : p.cell.elements) {
	
				if (e.type.equals("Atom")) {
					Atom atom = (Atom) e;
					pass.strokeWeight(4);
					pass.stroke(252, 191, 33);
					p.gfx.point(atom.pos);
				}
	
				if (e.type.equals("Boundary")) {
					Boundary b = (Boundary) e;
					pass.noStroke();
					pass.fill(20, 20, 100);
					// gfx.sphere(new Sphere(b.pos, b.radius), 50, true);
				}
	
				if (e.type.equals("Flux")) {
					Flux flux = (Flux) e;
					pass.strokeWeight(1);
					pass.stroke(255);
					if (fluxLines) {
						for (Flux f : ((Flux) e).getBonds()) {
							p.gfx.line(flux.pos, f.pos);
						}
					}
					pass.strokeWeight(2);
					pass.stroke(230);
					p.gfx.point(flux.pos);
				}
			}
		pass.endDraw();
	}

	void TexturedCube(PGraphics pass, PImage tex) {
		pass.textureMode(pass.NORMAL);
		pass.beginShape(pass.QUADS);
		pass.texture(tex);

		// Given one texture and six faces, we can easily set up the uv
		// coordinates
		// such that four of the faces tile "perfectly" along either u or v, but
		// the other
		// two faces cannot be so aligned. This code tiles "along" u, "around"
		// the X/Z faces
		// and fudges the Y faces - the Y faces are arbitrarily aligned such
		// that a
		// rotation along the X axis will put the "top" of either texture at the
		// "top"
		// of the screen, but is not otherwised aligned with the X/Z faces.
		// (This
		// just affects what type of symmetry is required if you need seamless
		// tiling all the way around the cube)

		// +Z "front" face
		pass.vertex(-1, -1, 1, 0, 0);
		pass.vertex(1, -1, 1, 1, 0);
		pass.vertex(1, 1, 1, 1, 1);
		pass.vertex(-1, 1, 1, 0, 1);

		// -Z "back" face
		pass.vertex(1, -1, -1, 0, 0);
		pass.vertex(-1, -1, -1, 1, 0);
		pass.vertex(-1, 1, -1, 1, 1);
		pass.vertex(1, 1, -1, 0, 1);

		// +Y "bottom" face
		pass.vertex(-1, 1, 1, 0, 0);
		pass.vertex(1, 1, 1, 1, 0);
		pass.vertex(1, 1, -1, 1, 1);
		pass.vertex(-1, 1, -1, 0, 1);

		// -Y "top" face
		pass.vertex(-1, -1, -1, 0, 0);
		pass.vertex(1, -1, -1, 1, 0);
		pass.vertex(1, -1, 1, 1, 1);
		pass.vertex(-1, -1, 1, 0, 1);

		// +X "right" face
		pass.vertex(1, -1, 1, 0, 0);
		pass.vertex(1, -1, -1, 1, 0);
		pass.vertex(1, 1, -1, 1, 1);
		pass.vertex(1, 1, 1, 0, 1);

		// -X "left" face
		pass.vertex(-1, -1, -1, 0, 0);
		pass.vertex(-1, -1, 1, 1, 0);
		pass.vertex(-1, 1, 1, 1, 1);
		pass.vertex(-1, 1, -1, 0, 1);

		pass.endShape();
	}
	void render() {
		
		p.println(p.cell.capacityHandler.toString());

		boolean mouseNavigation = false;
		boolean FLUXLINES = false;
		boolean AABB = false;

		p.cam.beginHUD();
		p.noStroke();
		p.fill(3, 7, 10, 100);
		p.rect(0, 0, p.width, p.height);
		p.cam.endHUD();

		p.lights();

		p.stroke(255, 0, 0);
		if (true) { // Render Pivot Point
			p.strokeWeight(10);
			p.point(0, 0, 0);
		}
		if (AABB) { // Render Cell Axis-Aligned Bounding Box (AABB)
			p.strokeWeight(1);
			p.noFill();
			p.gfx.mesh(p.cell.getAABB().toMesh());
		}

		if (p.cell.octree != null)
			p.cell.octree.draw();

		for (Element e : p.cell.elements) {

			if (e.type.equals("Atom")) {
				Atom atom = (Atom) e;
				p.strokeWeight(4);
				p.stroke(252, 191, 33);
				p.gfx.point(atom.pos);
			}

			if (e.type.equals("Boundary")) {
				Boundary b = (Boundary) e;
				p.noStroke();
				p.fill(20, 20, 100);
				// gfx.sphere(new Sphere(b.pos, b.radius), 50, true);
			}

			if (e.type.equals("Flux")) {
				Flux flux = (Flux) e;
				p.strokeWeight(1);
				p.stroke(255);
				if (FLUXLINES) {
					for (Flux f : ((Flux) e).getBonds()) {
						p.gfx.line(flux.pos, f.pos);
					}
				}
				p.strokeWeight(2);
				p.stroke(flux.color);
				p.gfx.point(flux.pos);
			}
		}

		if (mouseNavigation) {
			p.popMatrix();
		}
	}	
}
