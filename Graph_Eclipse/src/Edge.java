public class Edge {

	static int numberOfEdges = 0;
	private char identifier;
	private Node from, to;
	private int weight;		// if graph is unweighted, weight will be 0 or negative

	public Edge(Node from, Node to) {				// used for an unweighted graph
		identifier = (char) (97 + Edge.numberOfEdges++);
		this.from = from;
		this.to = to;
		this.weight = 0;							// if it's unweighted I will use 0
	}

	public Edge(Node from, Node to, int weight) {	// used for a weighted graph
		identifier = (char) (97 + Edge.numberOfEdges++);
		this.from = from;
		this.to = to;
		this.weight = weight;
	}

	public char getEdgeID() {
		return identifier;
	}

	public void setFrom(Node from) {
		this.from = from;
	}

	public Node getFrom() {
		return from;
	}

	public void setTo(Node to) {
		this.to = to;
	}

	public Node getTo() {
		return to;
	}

	public void setWeight(int number) {
		this.weight = number;
	}

	public int getWeight() {
		return weight;
	}

	public int getTotalNumberOfEdges() {
		return numberOfEdges;
	}
}