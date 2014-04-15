import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

class Node_dijkstra implements Comparable<Node_dijkstra> {
	public final String name;
	public Edge_dijkstra[] adjacencies;
	public double minDistance = Double.POSITIVE_INFINITY;
	public Node_dijkstra previous;

	public Node_dijkstra(String argName) {
		name = argName;
	}

	public String toString() {
		return name;
	}

	public int compareTo(Node_dijkstra other) {
		return Double.compare(minDistance, other.minDistance);
	}
}

class Edge_dijkstra {
	public final Node_dijkstra target;
	public final double weight;

	public Edge_dijkstra(Node_dijkstra argTarget, double argWeight) {
		target = argTarget;
		weight = argWeight;
	}
}

public class Dijkstra {
	public Dijkstra() {
		
	}
	
	public void computePaths(Node_dijkstra source) {
		source.minDistance = 0.;
		PriorityQueue<Node_dijkstra> Node_dijkstraQueue = new PriorityQueue<Node_dijkstra>();
		Node_dijkstraQueue.add(source);

		while (!Node_dijkstraQueue.isEmpty()) {
			Node_dijkstra u = Node_dijkstraQueue.poll();

			// Visit each Edge_dijkstra exiting u
			for (Edge_dijkstra e : u.adjacencies) {
				Node_dijkstra v = e.target;
				double weight = e.weight;
				double distanceThroughU = u.minDistance + weight;
				if (distanceThroughU < v.minDistance) {
					Node_dijkstraQueue.remove(v);
					v.minDistance = distanceThroughU;
					v.previous = u;
					Node_dijkstraQueue.add(v);
				}
			}
		}
	}

	public List<Node_dijkstra> getShortestPathTo(Node_dijkstra target) {
		List<Node_dijkstra> path = new ArrayList<Node_dijkstra>();
		for (Node_dijkstra Node_dijkstra = target; Node_dijkstra != null; Node_dijkstra = Node_dijkstra.previous)
			path.add(Node_dijkstra);
		Collections.reverse(path);
		return path;
	}
}