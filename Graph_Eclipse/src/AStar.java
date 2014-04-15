import java.util.ArrayList;
import java.util.PriorityQueue;

class AStarThread extends Thread {
	
	public int threadNum = 0;
	public boolean finished;
	public int startN, endN;
	
	PriorityQueue<Node> oNodes;
	ArrayList<Node> cNodes;
	ArrayList<Node> sPath;

	// The constructor for this class
	public AStarThread(String str, Node startN, Node endN) {
		super(str);	// number of thread... either 1 or 2...
		this.startN = startN;
		this.endN = endN;
		threadNum = Integer.parseInt(str);

		// when one thread discovers a Node that has been
		// visited by both threads, it finishes, which sends the
		// message to 'main' that it should finish, and tell the
		// other thread to finish, because a path has been found!
		finished = false;
	}

	// IMPORTANT... individual AStar
	public void run() {
		// computes individual AStar...
		while(!finished) {		// ************probably don't need this
				case(threadNum)
				{
				
				}
				startN.setG(0);
				if(dijkstrasHeuristic)
					startN.setH(0);
				else
					startN.setH(getDistBetween(startN, endN));
				startN.setF(startN.G() + startN.H());
				
				endN.isGoal = true;
				
				oNodes.clear();
				cNodes.clear();
				oNodes.add(startN);
				
				while(!oNodes.isEmpty()) {
					Node current = oNodes.peek();

					if(current.isGoal)
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
							}
						}
					}
				}
				return null;
			}
		}

		//doneProcessing[Integer.parseInt(getName())-1] = true;
	}

	// a simple method to return a PhilosopherThread String
	public String toString() {
		return "Thread #: "+getName();	// unused
	}
}

/*// This thread is only in charge of exiting the program...
// it is waiting for the user to enter 'n' into standard input...
class ExitThread extends Thread {
	public void run() {
		while(exitChar!='n') {
			try {
				exitChar = (char) br.read();
			} catch (IOException ioe) {
				out.println("IO error trying to exit");
				System.out.println("IO error trying to exit");
				System.exit(1);
			}
		}
	}
}	*/