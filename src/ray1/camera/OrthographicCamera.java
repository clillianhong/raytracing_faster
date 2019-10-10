package ray1.camera;

import ray1.Ray;
import egl.math.Vector3d;
import egl.math.Vector3;

public class OrthographicCamera extends Camera {

    //TODO#Ray Task 1: create necessary new variables/objects here, including an orthonormal basis
    //          formed by three basis vectors and any other helper variables 
    //          if needed.
	
    
    Vector3d u;
    Vector3d v;
    Vector3d w;
    
    /**
     * Initialize the derived view variables to prepare for using the camera.
     */
    public void init() {
        // TODO#Ray Task 1:  Fill in this function.
        // 1) Set the 3 basis vectors in the orthonormal basis, 
        //    based on viewDir and viewUp
        // 2) Set up the helper variables if needed
    	
    	//w is -viewDir
    	//u is w x viewUp
    	//v is w x u
    	
    	w = new Vector3d(this.getViewDir().clone().mul(-1).normalize());
    	Vector3 up = this.getViewUp().clone();
    	u = w.clone().cross(up).normalize();
    	v = w.clone().cross(u).normalize();
    	
    }

    /**
     * Set outRay to be a ray from the camera through a point in the image.
     *
     * @param outRay The output ray (not normalized)
     * @param inU The u coord of the image point (range [0,1])
     * @param inV The v coord of the image point (range [0,1])
     */
    public void getRay(Ray outRay, float inU, float inV) {
        // TODO#Ray Task 1: Fill in this function.
        // 1) Transform inU so that it lies between [-viewWidth / 2, +viewWidth / 2] 
        //    instead of [0, 1]. Similarly, transform inV so that its range is
        //    [-vieHeight / 2, +viewHeight / 2]
        // 2) Set the origin field of outRay for an orthographic camera. 
        //    In an orthographic camera, the origin should depend on your transformed
        //    inU and inV and your basis vectors u and v.
        // 3) Set the direction field of outRay for an orthographic camera.
     	
    	double newU = ((double)inU-0.5) * (double)this.getViewWidth();
    	double newV = ((double)inV-0.5) * (double)this.getViewHeight();
    	
    	Vector3d camOrigin = new Vector3d(this.getViewPoint());
    	camOrigin.sub(u.clone().mul(newU)).sub(v.clone().mul(newV));
    	
    	outRay.set(camOrigin, new Vector3d(this.getViewDir()));
        
        outRay.makeOffsetRay();

    }

}
