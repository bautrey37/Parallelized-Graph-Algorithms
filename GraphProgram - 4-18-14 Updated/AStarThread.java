import java.lang.Thread;
import java.util.ArrayList;
import java.util.PriorityQueue;

class AStarThread extends Thread {
	
	public int threadNum = 0;
	public int otherThreadNum = 0;
	private AStarThread otherThread;
	public boolean finished;
	public Node startN, endN;
	public boolean dijkstrasHeuristic;
	
	volatile GraphGUI graphProg;
	
	PriorityQueue<Node> oNodes;
	// the parallelized thread version does NOT use
	// the typical 'CLOSED' ArrayList, but rather a
	// 'SHARED' list of available nodes (M) that is used
	// by both threads and stored in another main
	// process (GraphGUI, in this case)
	//ArrayList<Node> cNodes;
	ArrayList<Node> sPath;

	// particular for each thread
	public float FLowest;

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
		sPath= new ArrayList<Node>();
		
		this.graphProg = graphProg;
		this.startN = startN;
		this.endN = endN;
		threadNum = Integer.parseInt(str);
		if(threadNum == 1)
			otherThreadNum = 2;
		else
			otherThreadNum = 1;

		// this is the lowest f value on the frontier
		// of this particular search process/thread
		this.FLowest = 0.0f;

		// when one thread discovers a Node that has been
		// visited by both threads, it finishes, which sends the
		// message to 'main' that it should finish, and tell the
		// other thread to finish, because a path has been found!
		finished = false;
		

		startN.setG(threadNum, 0);
		dijkstrasHeuristic = graphProg.dijkstrasHeuristic;
		if(dijkstrasHeuristic)
			startN.setH(threadNum, 0);
		else
			startN.setH(threadNum, getDistBetween(startN, endN));
		startN.setF(threadNum, startN.G(threadNum) + startN.H(threadNum));
		
		oNodes.clear();
		oNodes.add(startN);
	}

	// IMPORTANT... individual AStar
	public void run() {
		// computes individual AStar...
		// and stores nodes in sPath
		AStar();
		graphProg.shortestPath = sPath;

		graphProg.doneProcessing[threadNum-1] = true;
	}
	
	public void AStar() {		
		while(!finished) {
			Node current = oNodes.poll();

			if(elementOfM(current)) {
//				System.out.println("# of adj nodes: "+current.returnAdjNodes().size());
				if((current.F(threadNum) < graphProg.L) &&
						((current.G(threadNum) + otherThread.FLowest -
						current.H(otherThreadNum)) < graphProg.L)) {
					for (Node neighbor : current.returnAdjNodes()) {
						if(elementOfM(neighbor) &&
								(neighbor.G(threadNum) >
								(current.G(threadNum) + getDistBetween(current, neighbor)))) {
							graphProg.getGraph().nodeList.get(neighbor.getID()).setDiscovered(true);

							// SET H, G, and F for the neighbor...
							// setting H...
							if(dijkstrasHeuristic)
								neighbor.setH(threadNum, 0);
							else
								neighbor.setH(threadNum, getDistBetween(neighbor, endN));
							// setting G...
							neighbor.setG(threadNum,
								(current.G(threadNum) + getDistBetween(current, neighbor)));
							// setting F...
							neighbor.setF(threadNum,
								(neighbor.G(threadNum) + neighbor.H(threadNum)));

							neighbor.setPrevNode(threadNum, current);
							
							// This portion removes the neighbor first
							// in case it's in the open Nodes list, so
							// that there are no duplicates
							if(oNodes.contains(neighbor)) {
								oNodes.remove(neighbor);
							}
							// this helps ensure the priority
							// queue is sorted properly
							graphProg.threadLock.acquireUninterruptibly();
							neighbor.currentThread.set(threadNum);
							neighbor.setPrevNode(threadNum, current);
							oNodes.add(neighbor);
							graphProg.threadLock.release();
							
							if((neighbor.G(threadNum) + neighbor.G(otherThreadNum)) <
									graphProg.L) {
								graphProg.threadLock.acquireUninterruptibly();
								if((neighbor.G(threadNum) + neighbor.G(otherThreadNum)) <
										graphProg.L) {
									graphProg.L =
										neighbor.G(threadNum) + neighbor.G(otherThreadNum);
								}
								graphProg.threadLock.release();
							}
						}
					}
				}
				graphProg.M.remove(current);
			}
			if(oNodes.size() > 0) {
				Node next = oNodes.peek();
				FLowest = next.F(threadNum);
			}
			else {
				finished = true;
				sPath = reconstructPath(current);
			}
		}
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
	
	public boolean elementOfM(Node n) {
		return graphProg.M.contains(n);
	}
	
	public void setOtherThread(AStarThread other) {
		this.otherThread = other;
	}
	
	public AStarThread otherThread() {
		return otherThread;
	}

	// a simple method to return a PhilosopherThread String
	public String toString() {
		return "Thread #: "+getName();	// unused
	}
}