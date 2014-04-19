#include <omp.h>
#include <iostream>
#include <fstream>
#include <time.h>

#define INFINITY 999999
using namespace std;

class Node {
public:
	Node::Node() {

	}

private:
	float weight;
};

class Dijkstra {
private:
	int rows, cols;
	int** adjMatrix;
	int* predecessor, *distance;
	bool* mark; //keep track of visited node
	int source;
	int vertices = 128;

public:
	/*
	* Function read() reads No of vertices, Adjacency Matrix and source
	* Matrix from the user. The number of vertices must be greather than
	* zero, all members of Adjacency Matrix must be postive as distances
	* are always positive. The source vertex must also be positive from 0
	* to noOfVertices - 1
	*/
	int** read();
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
	void calculateDistance(int** adjMatrix);
	/*
	* Function output prints the results
	*/
	void output();
	void printPath(int);
};

int** Dijkstra::read(){
	cout << "Reading Data" << endl;

	char* inname = "random_terrain_1.txt";
	int weight;
	int vertexKey;
	ifstream infile(inname);
	if (!inname) {
		cout << "Problem opening file." << endl;
	}

	infile >> cols;
	infile >> rows;

	cout << cols << " " << rows << endl;
	//fscanf(fp, "%d,%d" &cols, &rows);

	int** adjMatrix = new int*[cols];
	for (int i = 0; i < cols; i++)
		adjMatrix[i] = new int[rows];

	for (int i = 0; i < cols; i++)
	{
		for (int j = 0; j < rows; j++)
		{
			infile >> weight;
			adjMatrix[i][j] = weight;
		}
	}

	infile.close();
	cout << "Finished reading data" << endl;
	return adjMatrix;
}


void Dijkstra::initialize(){
	distance = new int[128];
	predecessor = new int[128];
	mark = new bool[128];
	source = 1; //starting node

	for (int i = 0; i < vertices; i++) {
		mark[i] = false;
		predecessor[i] = -1;
		distance[i] = INFINITY;
	}
	distance[source] = 0;
}

int Dijkstra::getClosestUnmarkedNode(){
	int minDistance = INFINITY;
	int closestUnmarkedNode;
	for (int i = 0; i < vertices; i++) {
		if ((!mark[i]) && (minDistance >= distance[i])) {
			minDistance = distance[i];
			closestUnmarkedNode = i;
		}
	}
	return closestUnmarkedNode;
}

void Dijkstra::calculateDistance(int ** adjMatrix){
	initialize();

	cout << "Starting calculation" << endl;

	int minDistance = INFINITY;
	int closestUnmarkedNode;
	int count = 0;
	while (count < vertices) {
		closestUnmarkedNode = getClosestUnmarkedNode();
		mark[closestUnmarkedNode] = true;
//omg, increases time
#pragma omp parallel for 
		for (int i = 0; i < vertices; i++) {
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
		cout << "No path from " << source << "to " << node << endl;
	else {
		printPath(predecessor[node]);
		cout << node << "..";
	}
}

void Dijkstra::output(){
	cout << "Printing output" << endl;
	for (int i = 0; i < 128; i++) {
		if (i == source)
			cout << source << ".." << source;
		else
			printPath(i);
		cout << "->" << distance[i] << endl;
	}
}

int main(){
	char c;
	clock_t start, end;

	Dijkstra G;
	int** matrix = G.read();
	start = clock();
	G.calculateDistance(matrix);
	end = clock();
	G.output();

	cout << "\nExecution Time: " << (double)(end - start)/CLOCKS_PER_SEC << " seconds" << endl;
	cout << "Enter a character to exit: ";
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
