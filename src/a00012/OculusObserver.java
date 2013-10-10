package a00012;

import de.fruitfly.ovr.*;
import processing.core.PGraphics;
import toxi.geom.Vec3D;

public class OculusObserver {

	A00012 p;
	public OculusRift oculus;
	HMDInfo info;
	PGraphics right, left;
	Vec3D pos, face, up;
	float ar, fov, zNear, zFar, h;

	public OculusObserver(A00012 parent) {
		p = parent;
		// Setup oculus
		oculus = new OculusRift();
		oculus.init();
		oculus.poll();
		info = oculus.getHMDInfo();
		// Setup passes
		left = p.createGraphics((int) (info.HResolution * 0.5), info.VResolution, A00012.P3D);
		right = p.createGraphics((int) (info.HResolution * 0.5), info.VResolution, A00012.P3D);
		// Oculus perspective settings.
		pos = new Vec3D();
		ar = (1280*0.5f)/800;
		fov = 2 * A00012.atan(info.VScreenSize / (2 * info.EyeToScreenDistance));
		zNear = 0.1f;
		zFar = 2000f;
		h = (4 * ((info.HScreenSize * 0.25f) - (info.LensSeperationDistance * 0.5f)))
				/ info.HScreenSize;
	}

	public void update() {
		updateCamera(right, 'r');
		updateCamera(left, 'l');
	}

	public void updateCamera(PGraphics pass, char s) {
		oculus.poll();
		pass.camera(0,0,0, 0,0,1, 0,1,0);
		pass.beginCamera();
		if (s == 'r')
			pass.translate(h, 0, 0);
		if (s == 'l')
			pass.translate(-h, 0, 0);
		// Rotations are correct (Works with one eye closed);
		pass.rotateZ(-oculus.getRoll());
		pass.rotateX(-oculus.getPitch()*2);
		pass.rotateY(-oculus.getYaw());	
		pass.translate(pos.x, pos.y, pos.z);
		pass.endCamera();
	}

	public void see(Perception percp) {
		percp.render(left);
		percp.render(right);
		p.image(left, 0, 0, p.width*0.5f, p.height);
		p.image(right, p.width*0.5f, 0, p.width*0.5f, p.height);
	}

}
