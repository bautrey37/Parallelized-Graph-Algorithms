import java.util.*;
import java.io.*;

public class Vector2D {
	public double x;
	public double y;
	public static final double tol = 0.0001f;

	public Vector2D() {
		x = 0.0f;
		y = 0.0f;
	}

	public Vector2D(double a, double b) {
		x = a;
		y = b;
	}

	// returns a unit vector at the angle
	// entered -- angle is in radians
	public Vector2D(double angle) {
		x = Math.cos(angle);
		y = Math.sin(angle);
	}

	// copy and assignment constructors
	public Vector2D(Vector2D copy) {
		this.x = copy.x;
		this.y = copy.y;
	}

	// sets x and y to zero
	public void zero() {
		x = 0.0f;
		y = 0.0f;
	}

	//returns true if both x and y are zero
	public boolean isZero() {
		return (x == 0.0f && y == 0.0f);
	}

	//returns the length of the vector
	public double length() {
		return (Math.sqrt(x*x + y*y));
	}

	//returns the squared length of the vector (thereby avoiding the sqrt)
	public double lengthSq() {
		return (x*x + y*y);
	}

	public void normalize() {
		double l = length();
		if(l <= tol) l = 1.0f; x/=l; y/=l;
		if(Math.abs(x) < tol) x = 0.0f;
		if(Math.abs(y) < tol) y = 0.0f;
	}
	public Vector2D returnNormal() {
		double l = length();
		if(l <= tol) l = 1; double tempX = x/l; double tempY = y/l;
		if(Math.abs(tempX) < tol) tempX = 0.0f;
		if(Math.abs(tempY) < tol) tempY = 0.0f;
		return new Vector2D(tempX, tempY);
	}

	//returns the dot product of this and v2
	public double dot(Vector2D v2) {
		return (x*v2.x + y*v2.y);
	}

	// returns positive if v2 is clockwise of this vector,
	// negative if counterclockwise (assuming the Y axis is pointing down,
	// X axis to right like a Window app)
	public int sign(Vector2D v2) {
		double result = x*v2.y - v2.x*y;
		if(result > 0.0f) return -1;
		else if(result < 0) return 1; return 0;
	}

	//returns the vector that is perpendicular to this one
	public Vector2D perp() {
		return new Vector2D(y, -x);
	}

	//adjusts x and y so that the length of the vector does not exceed max
	public void truncate(double max) {
		double l = length();
		if(l > max) { normalize(); x*=max; y*=max; }
	}
	public Vector2D returnTruncated(double max) {
		double l = length();
		if(l > max) {
			Vector2D temp = this.returnNormal();
			temp.x*=max; temp.y*=max;
			return temp;
		}
		else return this;
	}

	//returns the distance between this vector
	// and the one passed as a parameter
	public double distance(Vector2D v2) {
		return (Math.sqrt(((x-v2.x)*(x-v2.x)) + ((y-v2.y)*(y-v2.y))));
	}

	//squared version of above
	public double distanceSq(Vector2D v2) {
		return (((x-v2.x)*(x-v2.x)) + ((y-v2.y)*(y-v2.y)));
	}

	//reverses this vector
	public void reverse() { x=-x; y=-y; }
	//returns the vector that is the reverse of this vector
	public Vector2D getReverse() {
		return new Vector2D(-x,-y);
	}
	
	public Vector2D add(Vector2D v1, Vector2D v2) {
		Vector2D summation = new Vector2D(v1.x+v2.x, v1.y+v2.y);
		return summation;
	}
	
	public static Vector2D subtract(Vector2D v1, Vector2D v2) {
		Vector2D difference = new Vector2D(v1.x-v2.x, v1.y-v2.y);
		return difference;
	}

	// for output purposes, if either x or y is small enough,
	// just print 0.0, because that is more useful data
	public String toString() {
		double tempX = x;
		double tempY = y;
		if(Math.abs(tempX) < tol) tempX = 0.0f;
		if(Math.abs(tempY) < tol) tempY = 0.0f;
		return "Vector2D: ("+tempX+", "+tempY+")";
	}
	
	// get rid of this main function when not TESTING --
	// As in, comment it out when used in larger context
	/*public static void main(String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String exit = "";
		
		while(!exit.equals("x")){	
			double num = Math.PI/2;
			Vector2D test = new Vector2D(num);
			Vector2D v2 = test.returnTruncated(1);
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