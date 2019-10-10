package ray1.surface;

import ray1.IntersectionRecord;
import ray1.Ray;
import egl.math.Vector2d;
import egl.math.Vector3;
import egl.math.Vector3d;
import ray1.accel.BboxUtils;

/**
 * Represents a sphere as a center and a radius.
 *
 * @author ags
 */
public class Sphere extends Surface {
  
  /** The center of the sphere. */
  protected final Vector3 center = new Vector3();
  public void setCenter(Vector3 center) { this.center.set(center); }
  public Vector3 getCenter() {return this.center.clone();}
  
  /** The radius of the sphere. */
  protected float radius = 1.0f;
  public void setRadius(float radius) { this.radius = radius; }
  public float getRadius() {return this.radius;}
  
  protected final double M_2PI = 2 * Math.PI;
  
  public Sphere() { }
  
  /**
   * Tests this surface for intersection with ray. If an intersection is found
   * record is filled out with the information about the intersection and the
   * method returns true. It returns false otherwise and the information in
   * outRecord is not modified.
   *
   * @param outRecord the output IntersectionRecord
   * @param ray the ray to intersect
   * @return true if the surface intersects the ray
   */
  public boolean intersect(IntersectionRecord outRecord, Ray rayIn) {
    // TODO#Ray Task 2: fill in this function.

        // t = ( -d*(e - c) +- sqrt( (d*(e - c))^2 - (d*d)((e - c)*(e - c) - R^2) ) ) / (d*d)
        
        Vector3d d = rayIn.direction;
        Vector3d e = rayIn.origin;
        Vector3d c = new Vector3d(center);
        
        // disc = (d*(e - c))^2 - (d*d)((e - c)*(e - c) - R^2)
        double discriminant = Math.pow((d.clone().dot(e.clone().sub(c))), 2) - (d.clone().dot(d)) * ((e.clone().sub(c)).dot(e.clone().sub(c)) - (radius * radius));
        if (discriminant < 0) {
            return false;
        }
        
        double tPos = ( d.clone().mul(-1).dot(e.clone().sub(c)) + Math.sqrt(discriminant) ) / d.clone().dot(d);
        double tNeg = ( d.clone().mul(-1).dot(e.clone().sub(c)) - Math.sqrt(discriminant) ) / d.clone().dot(d);
        
        // If there was an intersection, fill out the intersection record
        if(tNeg <= rayIn.end && tNeg >= rayIn.start) { // Check tNeg first, because it will be closer than tPos
            setIntersectionRecord(outRecord, rayIn, tNeg);
        } else if(tPos <= rayIn.end && tPos >= rayIn.start) {
            setIntersectionRecord(outRecord, rayIn, tPos);
        } else {
            return false;
        }

        return true;
  }
  
  /**
   * Compute Bounding Box for sphere
   * */
  public void computeBoundingBox() {
	  BboxUtils.sphereBBox(this);
  }
  
  /**
   * @see Object#toString()
   */
  public String toString() {
    return "sphere " + center + " " + radius + " " + shader + " end";
  }

  private void setIntersectionRecord(IntersectionRecord outRecord, Ray rayIn, double t) {
      if(outRecord == null) return;
      
      // The point on the ray that intersects the sphere
      Vector3d point = new Vector3d();
      rayIn.evaluate(point, t);
      
      outRecord.location.set(point);
      
      // Normal is the direction from center to point on sphere
      outRecord.normal.set(point.clone().sub(center).normalize());
      
      // Calculate the texture coordinates
      Vector3d intersection = point.clone().sub(center).normalize();
      double theta = Math.asin(intersection.y);
      double phi = Math.asin(intersection.x / Math.cos(theta));
      
      Vector2d texCoords = new Vector2d((theta / Math.PI) + 0.5, phi / 2.0 / Math.PI + 0.5);
      outRecord.texCoords.set(texCoords);

//        System.out.println("intersection: " + intersection + ", texCoords: " + texCoords);
      
      // The intersected object is this sphere
      outRecord.surface = this;

      // save the t value
      outRecord.t = t;
  }

}