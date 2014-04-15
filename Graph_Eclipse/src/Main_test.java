import java.util.List;


public class Main_test {
	public static void main(String[] args) {
		Dijkstra dijkstra = new Dijkstra();
		
		Node_dijkstra v0 = new Node_dijkstra("Redvile");
		Node_dijkstra v1 = new Node_dijkstra("Blueville");
		Node_dijkstra v2 = new Node_dijkstra("Greenville");
		Node_dijkstra v3 = new Node_dijkstra("Orangeville");
		Node_dijkstra v4 = new Node_dijkstra("Purpleville");

		v0.adjacencies = new Edge_dijkstra[] { new Edge_dijkstra(v1, 5), new Edge_dijkstra(v2, 10), new Edge_dijkstra(v3, 8) };
		v1.adjacencies = new Edge_dijkstra[] { new Edge_dijkstra(v0, 5), new Edge_dijkstra(v2, 3), new Edge_dijkstra(v4, 7) };
		v2.adjacencies = new Edge_dijkstra[] { new Edge_dijkstra(v0, 10), new Edge_dijkstra(v1, 3) };
		v3.adjacencies = new Edge_dijkstra[] { new Edge_dijkstra(v0, 8), new Edge_dijkstra(v4, 2) };
		v4.adjacencies = new Edge_dijkstra[] { new Edge_dijkstra(v1, 7), new Edge_dijkstra(v3, 2) };
		Node_dijkstra[] vertices = { v0, v1, v2, v3, v4 };
		dijkstra.computePaths(v0);
		for (Node_dijkstra v : vertices) {
			System.out.println("Distance to " + v + ": " + v.minDistance);
			List<Node_dijkstra> path = dijkstra.getShortestPathTo(v);
			System.out.println("Path: " + path);
		}
	}
	
	public int[][] generateData(int n, int m) {
		int[][] matrix = new int[n][m];
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < m; j++) {
				matrix[i][j] = 1;
			}
		}
		
		return matrix;
	}
}
