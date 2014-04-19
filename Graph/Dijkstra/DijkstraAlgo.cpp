#include <omp.h>
#include<iostream>
#include <time.h>

#define INFINITY 999
using namespace std;

class Dijkstra {
private:
	int adjMatrix[15][15];
	int predecessor[15], distance[15];
	bool mark[15]; //keep track of visited node
	int source;
	int numOfVertices;
public:
	/*
	* Function read() reads No of vertices, Adjacency Matrix and source
	* Matrix from the user. The number of vertices must be greather than
	* zero, all members of Adjacency Matrix must be postive as distances
	* are always positive. The source vertex must also be positive from 0
	* to noOfVertices - 1
	*/
	void read();
	/*
	* Function initialize initializes all the data members at the begining of
	* the execution. The distance between source to source is zero and all other
	* distances between source and vertices are infinity. The mark is initialized
	* to false and predecessor is initialized to -1
	*/
	void initialize();
	/*
	* Function getClosestUnmarkedNode returns the node which is nearest from the
	* Predecessor marked node. If the node is already marked as visited, then it search
	* for another node.
	*/
	int getClosestUnmarkedNode();
	/*
	* Function calculateDistance calculates the minimum distances from the source node to
	* Other node.
	*/
	void calculateDistance();
	/*
	* Function output prints the results
	*/
	void output();
	void printPath(int);
};

void Dijkstra::read(){
	cout << "Enter the number of vertices of the graph(should be > 0)\n";
	cin >> numOfVertices;
	while (numOfVertices <= 0) {
		cout << "Enter the number of vertices of the graph(should be > 0)\n";
		cin >> numOfVertices;
	}
	cout << "Enter the adjacency matrix for the graph\n";
	cout << "To enter infinity enter " << INFINITY << endl;
	for (int i = 0; i < numOfVertices; i++) {
		cout << "Enter the (+ve)weights for the row " << i << endl;
		for (int j = 0; j < numOfVertices; j++) {
			cin >> adjMatrix[i][j];
			while (adjMatrix[i][j] < 0) {
				cout << "Weights should be +ve. Enter the weight again\n";
				cin >> adjMatrix[i][j];
			}
		}
	}
	cout << "Enter the source vertex\n";
	cin >> source;
	while ((source<0) && (source>numOfVertices - 1)) {
		cout << "Source vertex should be between 0 and" << numOfVertices - 1 << endl;
		cout << "Enter the source vertex again\n";
		cin >> source;
	}
}

void Dijkstra::initialize(){
	for (int i = 0; i < numOfVertices; i++) {
		mark[i] = false;
		predecessor[i] = -1;
		distance[i] = INFINITY;
	}
	distance[source] = 0;
}

int Dijkstra::getClosestUnmarkedNode(){
	int minDistance = INFINITY;
	int closestUnmarkedNode;
	for (int i = 0; i < numOfVertices; i++) {
		if ((!mark[i]) && (minDistance >= distance[i])) {
			minDistance = distance[i];
			closestUnmarkedNode = i;
		}
	}
	return closestUnmarkedNode;
}

void Dijkstra::calculateDistance(){
	initialize();
	int minDistance = INFINITY;
	int closestUnmarkedNode;
	int count = 0;
	while (count < numOfVertices) {
		closestUnmarkedNode = getClosestUnmarkedNode();
		mark[closestUnmarkedNode] = true;
		for (int i = 0; i<numOfVertices; i++) {
			if ((!mark[i]) && (adjMatrix[closestUnmarkedNode][i]>0)) {
				if (distance[i] > distance[closestUnmarkedNode] + adjMatrix[closestUnmarkedNode][i]) {
					distance[i] = distance[closestUnmarkedNode] + adjMatrix[closestUnmarkedNode][i];
					predecessor[i] = closestUnmarkedNode;
				}
			}
		}
		count++;
	}
}

void Dijkstra::printPath(int node){
	if (node == source)
		cout << (char)(node + 97) << "..";
	else if (predecessor[node] == -1)
		cout << "No path from “<<source<<”to " << (char)(node + 97) << endl;
	else {
		printPath(predecessor[node]);
		cout << (char)(node + 97) << "..";
	}
}

void Dijkstra::output(){
	for (int i = 0; i < numOfVertices; i++) {
		if (i == source)
			cout << (char)(source + 97) << ".." << source;
		else
			printPath(i);
		cout << "->" << distance[i] << endl;
	}
}

int main(){
	char c;
	clock_t start, end;

	Dijkstra G;
	G.read();
	start = clock();
	G.calculateDistance();
	end = clock();
	G.output();

	cout << "Execution Time: " << (double)(end - start)/CLOCKS_PER_SEC << " seconds" << endl;
	cout << "Enter a character + ENTER to exit: ";
	cin >> c;
	return 0;
}


/*
void DijkstraAlgo::dijkstra(int graph[V][V], int src)
{

std::set<unlong>                              searchedList;
std::priority_queue<std::pair<ulong, ulong>>  frontierList;

// frontier list contains pairs of (cost, city)
// std::pair is sorted by first then second so the frontierList
// is automatically sorted by the cost of getting to a city.
// Add the start city as the only city in the frontier list.
frontierList.push(std::make_pair(0, start));

while (!frontierList.empty())
{
std::pair<ulong, ulong> next = frontierList.top();
frontierList.pop();

if (next.second == end)
{
std::cout << "Min Cost: " << next.first << "\n";
return;
}
if (searchedList.find(next.second) != searchedList.end())
{
continue; // We already found this node. move on.
// Note: continue starts the next loop iteration.
}
// We have found the lowest cost to this city.
// So mark that information by placing it in the searched list.
searchedList.insert(next.second);

// Add all city's that can be reached from here to the frontier list.
for (std::map<ulong, unlong>::const_iterator loop = graph[next.second].begin(); loop != graph[next.second].end(); ++loop)
{
// Add item for each city (cost = cost to get here + cost of road)
frontierList.push(std::make_pair(next.first + loop->second, loop->first));
}
}
}*/
