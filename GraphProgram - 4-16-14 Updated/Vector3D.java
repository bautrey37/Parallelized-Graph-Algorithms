import java.util.*;
import java.io.*;

public class Vector3D {
	public double x;
	public double y;
	public double z;
	public static final double tol = 0.0001f;

	public Vector3D() {
		x = 0.0;
		y = 0.0;
		z = 0.0;
	}
	
	public Vector3D(double a, double b) {
		this(a, b, 0.0);
	}

	public Vector3D(double a, double b, double c) {
		x = a;
		y = b;
		z = c;
	}

	// returns a unit vector at the angle
	// entered -- angle is in radians
	public Vector3D(double angle) {
		x = Math.cos(angle);
		y = Math.sin(angle);
		z = 0.0;
	}

	// copy and assignment constructors
	public Vector3D(Vector3D copy) {
		this.x = copy.x;
		this.y = copy.y;
		this.z = copy.z;
	}

	// sets x, y, and z to zero
	public void zero() {
		x = 0.0;
		y = 0.0;
		y = 0.0;
	}

	//returns true if both x and y are zero
	public boolean isZero() {
		return (x == 0.0 && y == 0.0 && z == 0.0);
	}

	//returns the length of the vector
	public double length() {
		return (Math.sqrt(x*x + y*y + z*z));
	}

	//returns the squared length of the vector (thereby avoiding the sqrt)
	public double lengthSq() {
		return (x*x + y*y + z*z);
	}

	public void normalize() {
		double l = length();
		if(l <= tol) l = 1.0f; x/=l; y/=l; z/=l;
		if(Math.abs(x) < tol) x = 0.0;
		if(Math.abs(y) < tol) y = 0.0;
		if(Math.abs(z) < tol) z = 0.0;
	}
	public Vector3D returnNormal() {
		double l = length();
		if(l <= tol) l = 1;
		double tempX = x/l;
		double tempY = y/l;
		double tempZ = z/l;
		if(Math.abs(tempX) < tol) tempX = 0.0;
		if(Math.abs(tempY) < tol) tempY = 0.0;
		if(Math.abs(tempZ) < tol) tempZ = 0.0;
		return new Vector3D(tempX, tempY, tempZ);
	}

	//returns the dot product of this and v2
	public double dot(Vector3D v2) {
		return (x*v2.x + y*v2.y + z*v2.z);
	}
	
	// returns the cross product of this and v2
	public Vector3D cross(Vector3D v2) {
		return null;	// complete this later...
	}

/*	// returns positive if v2 is clockwise of this vector,
	// negative if counterclockwise (assuming the Y axis is pointing down,
	// X axis to right like a Window app)
	public int sign(Vector3D v2) {
		double result = x*v2.y - v2.x*y;
		if(result > 0.0f) return -1;
		else if(result < 0) return 1; return 0;
	}	*/

/*	//returns the vector that is perpendicular to this one
	public Vector3D perp() {
		return new Vector3D(y, -x);
	}	*/

	//adjusts x and y so that the length of the vector does not exceed max
	public void truncate(double max) {
		double l = length();
		if(l > max) { normalize(); x*=max; y*=max; z*=max;}
	}
	public Vector3D returnTruncated(double max) {
		double l = length();
		if(l > max) {
			Vector3D temp = this.returnNormal();
			temp.x*=max; temp.y*=max; temp.z*=max;
			return temp;
		}
		else return this;
	}

	//returns the distance between this vector
	// and the one passed as a parameter
	public double distance(Vector3D v2) {
		double xDiff = x-v2.x;
		double yDiff = y-v2.y;
		double zDiff = z-v2.z;
		return (Math.sqrt((xDiff*xDiff) + (yDiff*yDiff) + (zDiff*zDiff)));
	}
	public double xyDistance(Vector3D v2) {
		double xDiff = x-v2.x;
		double yDiff = y-v2.y;
		return (Math.sqrt((xDiff*xDiff) + (yDiff*yDiff)));
	}

	//squared version of above
	public double distanceSq(Vector3D v2) {
		double xDiff = x-v2.x;
		double yDiff = y-v2.y;
		double zDiff = z-v2.z;
		return ((xDiff*xDiff) + (yDiff*yDiff) + (zDiff*zDiff));
	}
	public double xyDistanceSq(Vector3D v2) {
		double xDiff = x-v2.x;
		double yDiff = y-v2.y;
		return ((xDiff*xDiff) + (yDiff*yDiff));
	}

	//reverses this vector
	public void reverse() { x=-x; y=-y; z=-y;}
	//returns the vector that is the reverse of this vector
	public Vector3D getReverse() {
		return new Vector3D(-x,-y, -z);
	}
	
	public Vector3D add(Vector3D v1, Vector3D v2) {
		Vector3D summation = new Vector3D(v1.x+v2.x, v1.y+v2.y, v1.z+v2.z);
		return summation;
	}
	
	public static Vector3D subtract(Vector3D v1, Vector3D v2) {
		Vector3D difference = new Vector3D(v1.x-v2.x, v1.y-v2.y, v1.z-v2.z);
		return difference;
	}

	// for output purposes, if either x or y is small enough,
	// just print 0.0, because that is more useful data
	public String toString() {
		double tempX = x;
		double tempY = y;
		double tempZ = z;
		if(Math.abs(tempX) < tol) tempX = 0.0;
		if(Math.abs(tempY) < tol) tempY = 0.0;
		if(Math.abs(tempZ) < tol) tempZ = 0.0;
		return "Vector3D: ("+tempX+", "+tempY+", "+tempZ+")";
	}
	
	// get rid of this main function when not TESTING --
	// As in, comment it out when used in larger context
	/*public static void main(String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String exit = "";
		
		while(!exit.equals("x")){	
			double num = Math.PI/2;
			Vector3D test = new Vector3D(num);
			Vector3D v2 = test.returnTruncated(1);
			double distance = test.distance(v2);
			System.out.println(test);
			try {
				exit = br.readLine();
			} catch (IOException ioe) {
				System.out.println("IO error trying to read your name!");
				System.exit(1);
			}
		}
	}	*/
}