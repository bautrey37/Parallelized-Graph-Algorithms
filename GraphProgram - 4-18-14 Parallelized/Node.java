import java.util.*;
import java.text.*;
import java.util.concurrent.atomic.AtomicInteger;


public class Node implements Comparable<Node> {

	private static int number = 0;
	private int identifier;
	private ArrayList<Node> adjNodes;
	private ArrayList<Float> adjNodesDistance;


	// For Process 1/Default Process
	private float fTotalDistanceSTG;
	private float gDistanceFromStart;
	private float hDistanceToGoal;
	
	// For Process 2
	private float fTotalDistanceGTS;
	private float gDistanceFromGoal;
	private float hDistanceToStart;

	private boolean discovered;
	private Node prevNodeSTG;
	private Node prevNodeGTS;

	public boolean isStart = false;
	public boolean isPath = false;
	public boolean isGoal = false;
	public boolean isObstacle = false;

	volatile public AtomicInteger currentThread;

	private Vector3D position;

	public Node() {
		this.identifier = Node.number++;
		adjNodes = new ArrayList<Node>();
		adjNodesDistance = new ArrayList<Float>();
		
		reset();

		position = new Vector3D(-1.0f,-1.0f,0.0f);
	}

	public Node(int number) {
		this();
		this.identifier = number;
	}

	public Node(int number, Vector3D pos) {
		this(number);
		position = pos;
	}

	public void reset() {
		gDistanceFromStart = Float.MAX_VALUE;
		gDistanceFromGoal = Float.MAX_VALUE;
		hDistanceToGoal = Float.MAX_VALUE;
		hDistanceToStart = Float.MAX_VALUE;

		fTotalDistanceSTG = 0;
		fTotalDistanceGTS = 0;

		discovered = false;
		prevNodeSTG = null;
		prevNodeGTS = null;
		isStart = false;
		isPath = false;
		isGoal = false;
		isObstacle = false;

		currentThread = new AtomicInteger(1);
	}

	public int getID() {
		return identifier;
	}
	
	public double getHeight() {
		return position.z;
	}
	
	// methods for adjacent Nodes
	public void addAdjNode(Node n) {
		adjNodes.add(n);
		Vector3D dist = Vector3D.subtract(n.position, this.position);
		double adjDistance = dist.length();

		adjNodesDistance.add(new Float(adjDistance));
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
	// THESE ARE THE F() FUNCTION METHODS
	public void setF(float num) {
		setF(currentThread.get(), num);
	}
	public void setF(int process, float num) {
		switch(process)
		{
			case 2:
				fTotalDistanceGTS = num;
				break;
			case 1:
			default:
				fTotalDistanceSTG = num;
		}
	}
	public float F() {
		return F(currentThread.get());
	}
	public float F(int process) {
		switch(process)
		{
			case 2:
				return fTotalDistanceGTS;
			case 1:
			default:
				return fTotalDistanceSTG;
		}
	}

	// THESE ARE THE G() FUNCTION METHODS
	public void setG(float num) {
		setG(currentThread.get(), num);
	}
	public void setG(int process, float num) {
		switch(process)
		{
			case 2:
				gDistanceFromGoal = num;
				break;
			case 1:
			default:
				gDistanceFromStart = num;
		}
	}
	public float G() {
		return G(currentThread.get());
	}
	public float G(int process) {
		switch(process)
		{
			case 2:
				return gDistanceFromGoal;
			case 1:
			default:
				return gDistanceFromStart;
		}
	}

	// THESE ARE THE H() FUNCTION METHODS
	public void setH(float num) {
		setH(currentThread.get(), num);
	}
	public void setH(int process, float num) {
		switch(process)
		{
			case 2:
				hDistanceToStart = num;
				break;
			case 1:
			default:
				hDistanceToGoal = num;
		}
	}
	public float H() {
		return H(currentThread.get());
	}
	public float H(int process) {
		switch(process)
		{
			case 2:
				return hDistanceToStart;
			case 1:
			default:
				return hDistanceToGoal;
		}
	}






	public void setDistance(float distance) {
		setDistance(currentThread.get(), distance);
	}
	public void setDistance(int process, float distance) {
	
		switch(process)
		{
			case 2:
				fTotalDistanceGTS = distance;
				break;
			case 1:
			default:
				fTotalDistanceSTG = distance;
		}
	}
	public float getDistance() {
		return getDistance(currentThread.get());
	}
	public float getDistance(int process) {
		switch(process)
		{
			case 2:
				return fTotalDistanceGTS;
			case 1:
			default:
				return fTotalDistanceSTG;
		}
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
		setPrevNode(currentThread.get(), n);
	}
	public void setPrevNode(int process, Node n) {
		switch(process)
		{
			case 2:
				prevNodeGTS = n;
			case 1:
			default:
				prevNodeSTG = n;
		}
	}
	public Node getPrevNode() {
		return getPrevNode(currentThread.get());
	}
	public Node getPrevNode(int process) {
		switch(process)
		{
			case 2:
				return prevNodeGTS;
			case 1:
			default:
				return prevNodeSTG;
		}
	}


	// position method
	public Vector3D getPos() {
		return position;
	}

	@Override
	public int compareTo(Node other) {
		if(this.F() < other.F())
			return -1;
		else
			return 1;
	}
	
	public Node copy() {
		Node temp = new Node(identifier, position);
		temp.adjNodes = new ArrayList<Node>(this.adjNodes);
		temp.adjNodesDistance = new ArrayList<Float>(this.adjNodesDistance);

		temp.isStart = this.isStart;
		temp.isPath = this.isPath;
		temp.isGoal = this.isGoal;
		
		return temp;
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
