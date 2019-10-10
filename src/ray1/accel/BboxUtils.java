package ray1.accel;

import egl.math.Vector3;
import egl.math.Vector3d;
import ray1.surface.*;

// TODO#Ray tracing Part 2: Compute the bounding box and store the result in
// averagePosition, minBound, and maxBound.
public class BboxUtils {
	
	/**
	 * Computing Bounding box for a triangle
	 * */
	public static void triangleBBox(Triangle t) {
		// Ray Part 2 Task 1: Compute Bounding Box for a triangle:
		// Compute t.minBound, t.maxBound, t.averagePosition
		double xMin, xMax, yMin, yMax, zMin, zMax;

		xMin = Math.min(t.getVertex(0).x, Math.min(t.getVertex(1).x, t.getVertex(2).x));
		yMin = Math.min(t.getVertex(0).y, Math.min(t.getVertex(1).y, t.getVertex(2).y)); 
		zMin = Math.min(t.getVertex(0).z, Math.min(t.getVertex(1).z, t.getVertex(2).z)); 
		t.minBound = new Vector3d(xMin, yMin, zMin);

		xMax = Math.max(t.getVertex(0).x, Math.max(t.getVertex(1).x, t.getVertex(2).x));
		yMax = Math.max(t.getVertex(0).y, Math.max(t.getVertex(1).y, t.getVertex(2).y)); 
		zMax = Math.max(t.getVertex(0).z, Math.max(t.getVertex(1).z, t.getVertex(2).z));  
		t.maxBound = new Vector3d(xMax, yMax, zMax);

		t.averagePosition = new Vector3d((xMin + xMax) / 2.0, (yMin + yMax) / 2.0, (zMin + zMax) / 2.0);
	}
	
	/**
	 * Computing Bounding box for a sphere
	 * */
	public static void sphereBBox(Sphere s) {
		// Ray Part 2 Task 1: Compute Bounding Box for a Sphere
		// Compute s.minBound, s.maxBound, s.averagePosition
		s.averagePosition = new Vector3d(s.getCenter());

		s.minBound = s.averagePosition.clone().sub(s.getRadius());
		
		s.maxBound = s.averagePosition.clone().add(s.getRadius());
	}
	
	/**
	 * Computing Bounding box for a cylinder
	 * */
	public static void cylinderBBox(Cylinder c) {
		c.averagePosition = new Vector3d(c.getCenter());

		c.minBound = new Vector3d(Double.POSITIVE_INFINITY,
				Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		c.maxBound = new Vector3d(Double.NEGATIVE_INFINITY,
				Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
		
		
		
		
		Vector3d[] v = new Vector3d[8];
		int count = 0;
		for (int i = -1; i < 2; i += 2) {
			for (int j = -1; j < 2; j += 2) {
				for (int k = -1; k < 2; k += 2) {
					v[count] = new Vector3d(c.getCenter());
					v[count].x += c.getRadius() * i;
					v[count].y += c.getRadius() * j;
					v[count].z += k * 0.5 * c.getHeight();
					count++;
				}
			}
		}

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 3; j++) {
				if (v[i].get(j) < c.minBound.get(j))
					c.minBound.set(j, v[i].get(j));
				if (v[i].get(j) > c.maxBound.get(j))
					c.maxBound.set(j, v[i].get(j));
			}
		}
	}
	
	/**
	 * Computing Bounding box for a box
	 * */
	public static void boxBBox(Box b) {
		Vector3 minPt = b.getMinPt();
		Vector3 maxPt = b.getMaxPt();
		
		b.minBound = new Vector3d(Double.POSITIVE_INFINITY,
				Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		b.maxBound = new Vector3d(Double.NEGATIVE_INFINITY,
				Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

		Vector3d[] v = new Vector3d[8];
		int[] k = new int[3];
		int count = 0;
		for (k[0] = 0; k[0] < 2; k[0]++) {
			for (k[1] = 0; k[1] < 2; k[1]++) {
				for (k[2] = 0; k[2] < 2; k[2]++) {
					v[count] = new Vector3d();
					for (int j = 0; j < 3; j++) {
						if (k[j] == 0)
							v[count].set(j,(double) minPt.get(j));
						else
							v[count].set(j,(double) maxPt.get(j));
					}
					count++;
				}
			}
		}

		b.averagePosition = new Vector3d();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 3; j++) {
				b.averagePosition.set(j, b.averagePosition.get(j) + v[i].get(j));
				if (v[i].get(j) < b.minBound.get(j))
					b.minBound.set(j, v[i].get(j));
				if (v[i].get(j) > b.maxBound.get(j))
					b.maxBound.set(j, v[i].get(j));
			}
		}
		b.averagePosition.mul(1.0 / 8);
	}
}
