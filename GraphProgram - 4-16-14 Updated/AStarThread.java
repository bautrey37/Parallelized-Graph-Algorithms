import java.lang.Thread;
import java.util.ArrayList;
import java.util.PriorityQueue;

class AStarThread extends Thread {
	
	public int threadNum = 0;
	public boolean finished;
	public Node startN, endN;
	
	volatile GraphGUI graphProg;
	
	PriorityQueue<Node> oNodes;
	ArrayList<Node> cNodes;
	ArrayList<Node> sPath;

	// The constructor for this class
	public AStarThread(String str, GraphGUI graphProg,
			Node startN, Node endN) {
		super(str);	// number of thread... either 1 or 2...
		reset(str, graphProg, startN, endN);
	}
	
	// pretty sure this is 'unnecessary' and could
	// have been included in the constructor
	public void reset(String str, GraphGUI graphProg,
			Node startN, Node endN) {
		oNodes = new PriorityQueue<Node>();
		cNodes = new ArrayList<Node>();
		sPath= new ArrayList<Node>();
		
		this.graphProg = graphProg;
		this.startN = startN;
		this.endN = endN;
		threadNum = Integer.parseInt(str);
//		this.dijkstrasHeuristic = false;

		// when one thread discovers a Node that has been
		// visited by both threads, it finishes, which sends the
		// message to 'main' that it should finish, and tell the
		// other thread to finish, because a path has been found!
		finished = false;
	}

	// IMPORTANT... individual AStar
	public void run() {
		// computes individual AStar...
		sPath = AStar();
		graphProg.shortestPath = sPath;

		graphProg.doneProcessing[threadNum-1] = true;
	}
	
	public ArrayList<Node> AStar() {
		startN.setG(0);
		boolean dijkstrasHeuristic = graphProg.dijkstrasHeuristic;
		if(dijkstrasHeuristic)
			startN.setH(0);
		else
			startN.setH(getDistBetween(startN, endN));
		startN.setF(startN.G() + startN.H());
		
		oNodes.clear();
		cNodes.clear();
		oNodes.add(startN);
		
		while(!oNodes.isEmpty() ) {//&& !finished) {
			Node current = oNodes.peek();

			// THESE LINES ARE NECESSARY FOR STOPPING THE SEARCH MIDWAY
			// THROUGH IN CASE EITHER THREAD FINDS A NODE THAT HAS BEEN
			// SEARCHED BY BOTH THREADS... 'reconstruct path' needs to be
			// altered to 'concatenate' both paths together...
			if((threadNum == 1) && ((current.isGoal)))// || current.goalPathFound.compareAndSet(1, 2)) )
				return reconstructPath (current);
			else if((threadNum == 2) && ((current.isStart)))// || current.startPathFound.compareAndSet(1, 2)) )
				return reconstructPath (current);

			current = oNodes.poll();
			cNodes.add(current);

			for (Node neighbor : current.returnAdjNodes()) {

				if(dijkstrasHeuristic)
					neighbor.setH(0);
				else
					neighbor.setH(getDistBetween(neighbor, endN));
				neighbor.setDiscovered(true);

				if(cNodes.contains(neighbor)) {
					continue;
				}

				float neighborDistFromStart = current.G() + getDistBetween(current, neighbor);

				if((!oNodes.contains(neighbor)) || 
						(neighborDistFromStart < neighbor.G())) {
					neighbor.setPrevNode(current);
					neighbor.setG(neighborDistFromStart);
					neighbor.setF(neighbor.G() + neighbor.H());
					if(!oNodes.contains(neighbor)) {
						oNodes.add(neighbor);
						
						// THE BELOW PORTION IS A KEY PART OF THE PARALLELIZATION PROCESS...
						// JUST NEED TO FIGURE OUT HOW TO GET IT TO WORK PROPERLY...
					
						/*// if current Thread is Thread #1 (the start to goal thread),
						// then this neighbor Node's startPathFound Atomic Integer
						// is set to 1, meaning it has been discovered by this thread...
						if(threadNum == 1) {
						//	neighbor.startPathFound.compareAndSet(0, 1);
						//	System.out.println("neighbor.startPathFound: "+neighbor.startPathFound);
						}
						else {
						//	neighbor.goalPathFound.compareAndSet(0, 1);
						//	System.out.println("neighbor.goalPathFound: "+neighbor.goalPathFound);
						}	*/
					}
				}
			}
		}
		return null;
	}
	
	public ArrayList<Node> reconstructPath(Node node) {
		ArrayList<Node> path = new ArrayList<Node>();
		node.isPath = true;
		while(node.getPrevNode() != null) {
			path.add(0, node);
			node = node.getPrevNode();
			node.isPath = true;
		}
		path.add(0, node);
		return path;
	}
	
	public float getDistBetween(Node n1, Node n2) {
		Vector3D p1 = n1.getPos();
		Vector3D p2 = n2.getPos();
		return (float) p1.distance(p2);
	}

	// a simple method to return a PhilosopherThread String
	public String toString() {
		return "Thread #: "+getName();	// unused
	}
}