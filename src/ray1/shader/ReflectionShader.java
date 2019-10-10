package ray1.shader;

import egl.math.Color;
import egl.math.Colorf;
import egl.math.Vector2;
import egl.math.Vector3;
import egl.math.Vector3d;
import ray1.IntersectionRecord;
import ray1.Light;
import ray1.Ray;
import ray1.RayTracer;
import ray1.Scene;

public abstract class ReflectionShader extends Shader {

	/** BEDF used by this shader. */
	protected BRDF brdf = null;

	/** Coefficient for mirror reflection. */
	protected final Colorf mirrorCoefficient = new Colorf();
	public void setMirrorCoefficient(Colorf mirrorCoefficient) { this.mirrorCoefficient.set(mirrorCoefficient); }
	public Colorf getMirrorCoefficient() {return new Colorf(mirrorCoefficient);}

	public ReflectionShader() {
		super();
	}

	/**
	 * Evaluate the intensity for a given intersection using the Microfacet shading model.
	 *
	 * @param outIntensity The color returned towards the source of the incoming ray.
	 * @param scene The scene in which the surface exists.
	 * @param ray The ray which intersected the surface.
	 * @param record The intersection record of where the ray intersected the surface.
	 * @param depth The recursion depth.
	 */
	@Override
	public void shade(Colorf outIntensity, Scene scene, Ray ray, IntersectionRecord record, int depth) {
		Vector3d incoming = new Vector3d();
		Vector3d outgoing = new Vector3d();
				
		outgoing.set(ray.origin).sub(record.location).normalize();
		Vector3d surfaceNormal = record.normal;
		Vector2 texCoords = new Vector2(record.texCoords);
//		System.out.println(texCoords.toString());
		
		Colorf BRDFVal = new Colorf();
		
		// direct reflection from light sources
		outIntensity.setZero();
		
		// TODO#Ray Task 5: Fill in this function.
				// 1) Loop through each light in the scene.
				// 2) If the intersection point is shadowed, skip the calculation for the light.
				//	  See Shader.java for a useful shadowing function.
				// 3) Compute the incoming direction by subtracting
				//    the intersection point from the light's position.
				// 4) Compute the color of the point using the shading model. 
				//	  EvalBRDF method of brdf object should be called to evaluate BRDF value at the shaded surface point.
				// 5) Add the computed color value to the output.
				// 6) If mirrorCoefficient is not zero vector, add recursive mirror reflection
				//		6a) Compute the mirror reflection ray direction by reflecting the direction vector of "ray" about surface normal
				//		6b) Construct mirror reflection ray starting from the intersection point (record.location) and pointing along 
				//			direction computed in 6a) (Hint: remember to call makeOffsetRay to avoid self-intersecting)
				//      6c) Compute the Fresnel's refectance coefficient with Schlick's approximation 
				// 		6d) call RayTracer.shadeRay() with the mirror reflection ray and (depth+1)
				// 		6e) add returned color value in 6d) to output
		
		for(Light light : scene.getLights()) {
		    if(this.isShadowed(scene, light, record)) continue;
		    incoming = new Vector3d(light.position).sub(record.location);
		    Colorf color = new Colorf();
		    brdf.EvalBRDF(incoming, outgoing, surfaceNormal, texCoords, color);
		    color.mul(new Vector3(
		            new Vector3d(light.intensity).mul(Math.max(surfaceNormal.clone().dot(incoming.clone().normalize()), 0) / incoming.lenSq())));
		    BRDFVal.add(color);
		}
		
		// recursive reflection
		
		if(!mirrorCoefficient.isZero()) {
			surfaceNormal.normalize();
	        Vector3d reflectionDirection = ray.direction.clone().sub(surfaceNormal.clone().normalize().mul(ray.direction.clone().dot(surfaceNormal.clone().normalize()) * 2.0));
	        Ray mirrorRay = new Ray(record.location, reflectionDirection);
	        mirrorRay.makeOffsetRay();
	        
	        Colorf reflectedColor = new Colorf();

	        double cosTheta = surfaceNormal.clone().normalize().dot(outgoing);

            Vector3 reflectance = mirrorCoefficient.clone().add(mirrorCoefficient.clone().mul(-1).add(1).mul((float)Math.pow(1 - cosTheta, 5)));
	        
            RayTracer.shadeRay(reflectedColor, scene, mirrorRay, depth + 1);

            Colorf color = new Colorf((Colorf) reflectedColor.mul(reflectance));
            BRDFVal.add(color);
	    }
		
		
		outIntensity.set(BRDFVal);
		

		
	}

}