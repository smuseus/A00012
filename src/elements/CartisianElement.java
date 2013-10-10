package elements;

import toxi.geom.Vec3D;

public class CartisianElement extends Element {
	public Vec3D pos;

	CartisianElement() {
		type = "Cartisian Element";
		pos = new Vec3D();
	}
}
