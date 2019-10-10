package ray1.surface;

import ray1.IntersectionRecord;
import ray1.Ray;
import egl.math.Matrix3d;
import egl.math.Vector2d;
import egl.math.Vector3;
import egl.math.Vector3d;
import ray1.shader.Shader;
import ray1.OBJFace;
import ray1.accel.BboxUtils;

/**
 * Represents a single triangle, part of a triangle mesh
 *
 * @author ags
 */
public class Triangle extends Surface {
  /** The normal vector of this triangle, if vertex normals are not specified */
  Vector3 norm;
  
  /** The mesh that contains this triangle */
  public Mesh owner;
  
  /** The face that contains this triangle */
  public OBJFace face = null;
  
  double a, b, c, d, e, f;
  public Triangle(Mesh owner, OBJFace face, Shader shader) {
    this.owner = owner;
    this.face = face;

    Vector3 v0 = owner.getMesh().getPosition(face,0);
    Vector3 v1 = owner.getMesh().getPosition(face,1);
    Vector3 v2 = owner.getMesh().getPosition(face,2);
    
    if (!face.hasNormals()) {
      Vector3 e0 = new Vector3(), e1 = new Vector3();
      e0.set(v1).sub(v0);
      e1.set(v2).sub(v0);
      norm = new Vector3();
      norm.set(e0).cross(e1).normalize();
    }

    a = v0.x-v1.x;
    b = v0.y-v1.y;
    c = v0.z-v1.z;
    
    d = v0.x-v2.x;
    e = v0.y-v2.y;
    f = v0.z-v2.z;
    
    this.setShader(shader);
  }

  /**
   * Tests this surface for intersection with ray. If an intersection is found
   * record is filled out with the information about the intersection and the
   * method returns true. It returns false otherwise and the information in
   * outRecord is not modified.
   *
   * @param outRecord the output IntersectionRecord
   * @param rayIn the ray to intersect
   * @return true if the surface intersects the ray
   */
  public boolean intersect(IntersectionRecord outRecord, Ray rayIn) {
    // TODO#Ray Task 2: fill in this function.
    
    
    Vector3d dir = rayIn.direction.clone();

    Vector3d xe = rayIn.origin.clone();

    Vector3d xa = new Vector3d(owner.getMesh().getPosition(face,0).clone());

    Vector3d xae = xa.clone().sub(xe);
    
    double g = dir.x;
    double h = dir.y;
    double i = dir.z;
    
    double j = xae.x;
    double k = xae.y;
    double l = xae.z;
    
    double M = a*(e*i - h*f) + b*(g*f-d*i) + c*(d*h - e*g);
    
    double t = -(f*(a*k-j*b)+e*(j*c-a*l)+d*(b*l-k*c))/M;
    
    if(t < rayIn.start || t > rayIn.end) {
        return false;
    }	

    double gamma = (i*(a*k-j*b) + h*(j*c-a*l) + g*(b*l-k*c))/M;
    if(gamma < 0 || gamma > 1) {
        return false;
    }

    double beta = (j*(e*i - h*f) + k*(g*f-d*i) + l*(d*h-e*g))/M;
    if(beta < 0 || beta > 1 - gamma) {
        return false;
    }

    double alpha = 1 - beta - gamma;
   
    if(outRecord!=null) {
        outRecord.t = t;
        outRecord.surface = this;
//            outRecord.location.set(xe.clone().add(dir.clone().mul(t)));
        rayIn.evaluate(outRecord.location, t);
        
//            outRecord.location.set(rayIn.evaluate(, t));

        if(this.face.hasNormals()) {
            Vector3 n0 =  owner.getMesh().getNormal(face, 0).clone().mul((float)alpha);
            Vector3 n1 =  owner.getMesh().getNormal(face, 1).clone().mul((float)beta);
            Vector3 n2 =  owner.getMesh().getNormal(face, 2).clone().mul((float)gamma);
            outRecord.normal.set(n0.clone().add(n1).clone().add(n2).normalize());
            this.norm =  n0.clone().add(n1).clone().add(n2).normalize();
        }
        
        if(this.face.hasUVs()) {
            egl.math.Vector2 v0 = owner.getMesh().getUV(face, 0).clone().mul((float)alpha);
            egl.math.Vector2 v1 = owner.getMesh().getUV(face, 1).clone().mul((float)beta);
            egl.math.Vector2 v2 = owner.getMesh().getUV(face, 2).clone().mul((float)gamma);
            outRecord.texCoords.set(v0.add(v1).add(v2).normalize());
        }

//            outRecord.normal.set(this.norm);
    }
    
    return true;

}
  
  public void computeBoundingBox(){
	  BboxUtils.triangleBBox(this);
  }

  /**
   * @see Object#toString()
   */
  public String toString() {
    return "Triangle ";
  }
}