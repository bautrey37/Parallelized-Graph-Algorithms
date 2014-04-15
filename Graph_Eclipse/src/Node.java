import java.util.*;
import java.text.*;
import java.util.concurrent.atomic.AtomicInteger;


public class Node implements Comparable<Node> {

	private static int number = 0;
	private int identifier;
	private ArrayList<Node> adjNodes;
	private ArrayList<Float> adjNodesDistance;

	private float fTotalDistance;
	private float gDistanceFromStart;
	private float hDistanceFromGoal;
	private boolean discovered;
	volatile private Node prevNode;
	public boolean isStart = false;
	public boolean isPath = false;
	public boolean isGoal = false;

	public AtomicInteger startPathFound;
	public AtomicInteger goalPathFound;

	private Vector2D position;

	public Node() {
		this.identifier = Node.number++;
		adjNodes = new ArrayList<Node>();
		adjNodesDistance = new ArrayList<Float>();
		
		reset();

		position = new Vector2D(-1.0f,-1.0f);
	}

	public Node(int number) {
		this.identifier = number;
		adjNodes = new ArrayList<Node>();
		adjNodesDistance = new ArrayList<Float>();
		
		reset();
		
		position = new Vector2D(-1.0f,-1.0f);
	}

	public Node(int number, Vector2D pos) {
		this.identifier = number;
		adjNodes = new ArrayList<Node>();
		adjNodesDistance = new ArrayList<Float>();
		
		reset();

		position = pos;
	}
	
	public void reset() {
		gDistanceFromStart = Float.MAX_VALUE;
		hDistanceFromGoal = Float.MAX_VALUE;
		fTotalDistance = 0;
		discovered = false;
		prevNode = null;
		isStart = false;
		isPath = false;
		isGoal = false;
		
		// default constructor of AtomicInteger sets value to 0 (zero)
		startPathFound = new AtomicInteger(0);
		goalPathFound = new AtomicInteger(0);
	}

	public int getID() {
		return identifier;
	}

	
	// methods for adjacent Nodes
	public void addAdjNode(Node n) {
		adjNodes.add(n);
		Vector2D dist = Vector2D.subtract(n.position, this.position);
		adjNodesDistance.add(new Float(dist.length()));
	}
	public ArrayList<Node> returnAdjNodes() {
		return adjNodes;
	}

	public boolean isAdjacent(Node n) {
		return adjNodes.contains(n);
	}
	public boolean hasNoConnections() {
		return adjNodes.isEmpty();
	}


	// for F = G + H, A* heuristic distance
	public void setF(float num) {
		fTotalDistance = num;
	}
	public float F() {
		return fTotalDistance;
	}

	public void setG(float num) {
		gDistanceFromStart = num;
	}
	public float G() {
		return gDistanceFromStart;
	}

	public void setH(float num) {
		hDistanceFromGoal = num;
	}
	public float H() {
		return hDistanceFromGoal;
	}


	public void setDistance(float distance) {
		fTotalDistance = distance;
	}
	public float getDistance() {
		return fTotalDistance;
	}


	// discovered methods
	public void setDiscovered(boolean disc) {
		discovered = disc;
	}
	public boolean isDiscovered() {
		return discovered;
	}

	
	// previous Node methods (for A* star path)
	public void setPrevNode(Node n) {
		prevNode = n;
	}
	public Node getPrevNode() {
		return prevNode;
	}


	// position method
	public Vector2D getPos() {
		return position;
	}

	@Override
	public int compareTo(Node other) {
		if(this.F() < other.F())
		//if(this.H() < other.H())
		//if((this.F() < other.F()) && this.G() < other.G())
			return -1;
		else
			return 1;
	}

	public void printNodes(Node n) {
		DecimalFormat form = new DecimalFormat("0.00");
		System.out.print("Node #" + this.getID() + ": ");
		for(int i = 0; i < n.adjNodes.size(); i++) {
			System.out.print("["+n.adjNodes.get(i).getID()+":"+form.format((float)n.adjNodesDistance.get(i))+"]");
		}
		//System.out.println("\nPosition: "+position);
	}
}
