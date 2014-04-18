import java.util.*;
import java.io.*;

public class Graph {

	private int numNodes = 0;
	private int numEdges = 0;
	private boolean directed = false;
	private boolean nodesHavePosition = false;
	private boolean nodesHaveHeight = false;	// with Ryan's data...
	
	public int startNode;
	public int goalNode;

	public ArrayList<Node> nodeList;

	public Graph(String fileName) throws FileNotFoundException {

		int i, j;
		nodeList = new ArrayList<Node>();

		try {
			Scanner input = new Scanner(new File(fileName));
			numNodes = input.nextInt();
			int nodesHavePos = input.nextInt();

			startNode = input.nextInt();
			goalNode = input.nextInt();

			if(nodesHavePos == 1)
				nodesHavePosition = true;

			if(nodesHavePosition) {
				if(nodesHaveHeight) {
					for(i = 0; i < numNodes; i++) {
						float x = input.nextFloat();
						float y = input.nextFloat();
						float z = input.nextFloat();
						Vector3D pos = new Vector3D(x,y,z);
						nodeList.add(i, new Node(i, pos));
					}
				}
				else {
					for(i = 0; i < numNodes; i++) {
						float x = input.nextFloat();
						float y = input.nextFloat();
						Vector3D pos = new Vector3D(x,y,0.0);
						nodeList.add(i, new Node(i, pos));
					}
				}
			}
			else {
				for(i = 0; i < numNodes; i++) {
					nodeList.add(i, new Node(i));
				}
			}

			while(input.hasNext()) {
				i = input.nextInt();
				j = input.nextInt();
				//System.out.printf("i:%d, j:%d\n", i, j);

				if(!(nodeList.get(i).isAdjacent(nodeList.get(j)))) {
					nodeList.get(i).addAdjNode(nodeList.get(j));
				}
				if(!(nodeList.get(j).isAdjacent(nodeList.get(i)))) {
					nodeList.get(j).addAdjNode(nodeList.get(i));
				}
				
				numEdges++;
			}

			//printGraph(this);

			input.close();
		}
		catch (Exception e) {
			System.out.println("Error: " + e);
		}
	}

	public int getNumNodes() {
		return numNodes;
	}

	public int getNumEdges() {
		return numEdges;
	}

	public void printGraph(Graph g) {
		System.out.println();
		for(int i = 0; i < g.nodeList.size(); i++) {
			g.nodeList.get(i).printNodes(g.nodeList.get(i));
			System.out.println();
		}
		System.out.println();
	}

	public void insertNode() {
		this.nodeList.add(new Node(this.getNumNodes()));
		numNodes++;
	}

	public boolean isDirected() {
		return directed;
	}
	public void setDirected(boolean isDirected) {
		directed = isDirected;
	}
	
	public boolean nodesHavePos() {
		return nodesHavePosition;
	}
	
	public void resetNodes() {
		for(Node n : nodeList) {
			n.reset();
		}
	}
}