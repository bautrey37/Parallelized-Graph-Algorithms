/*
Made by Ryan Harrigan
Serial implementation of the Floyd-Warshal algorithm, also known as the All Pairs Shortest Path algorithm

*/

#include <stdio.h>
#include <iostream>
#include <fstream>
using namespace std;


int main(int argc, char* argv){
	//FILE* directedGraph;
	FILE* distanceGraphOut;

	ifstream directedGraph;

	distanceGraphOut = fopen("distance_graph.txt", "w");	//final outputted distance graph 
	directedGraph.open("directed_graph.txt",ifstream::in);
	
	double** distanceGraph;	//distance graph to do computation on
	
	char cNum[10];
	int numNodes = 0;
	int infinity = 10000;

	if(directedGraph.is_open()){

		directedGraph.getline(cNum, 256,',');	
		numNodes = strtol(cNum, NULL, 10);	//get number of nodes

		distanceGraph = new double*[numNodes];

		for (int i = 0; i < numNodes; i++){
			distanceGraph[i] = new double[numNodes];
		}

		while(!directedGraph.eof()){
			
			for (int i = 0; i < numNodes; i++){
				for (int j = 0; j < numNodes; j++){

					directedGraph.getline(cNum, 256,',');
					//cout << "i is " << i << ", j is " << j << ". strtol is " << strtol(cNum, NULL, 10) << endl;
					distanceGraph[i][j] = strtol(cNum, NULL, 10);	// fill in temporary distance graph with the current directed graph
				}
			}
			directedGraph.getline(cNum, 256,',');
		}
	}
	directedGraph.close();  // finished reading original node/edge matrix

	double minDistance;
	double distance;

	int** shortPath;
	
	shortPath = new int*[numNodes];
	for (int i = 0; i < numNodes; i++){
		shortPath[i] = new int[numNodes];
		for (int j = 0; j < numNodes; j++){
			shortPath[i][j] = 0;
		}
	}

	for (int i = 0; i < numNodes; i++){
		for (int j = 0; j < numNodes; j++){
			if (i==j||(distanceGraph[i][j] >= infinity)){
				shortPath[i][j] = 0;
			}
			else {
				shortPath[i][j] = i;
			}
		}
	}

	// BROKEN???!!!! vv
	for (int k = 0; k < numNodes; k++){
		for (int i = 0; i < numNodes; i++){
			for (int j = 0; j < numNodes; j++){
				if ((i != j) && (i != k) && (j != k)){
					//SearchPath(i,j,k);
					minDistance = distanceGraph[i][j];
					distance = (distanceGraph[i][k] + distanceGraph[k][j]);
					if ((distance < minDistance) && (distance < (infinity - 100))){
						distanceGraph[i][j] = distance;
						shortPath[i][j] = shortPath[k][j];
					}
				}
			}
		}
	}
	// BROKEN???!!!! ^^

	fprintf(distanceGraphOut, "%d,\n", numNodes);

	for (int i = 0; i < numNodes; i++){
		for (int j = 0; j < numNodes; j++){
			fprintf(distanceGraphOut, "%d,", distanceGraph[i][j]);
		}
		fprintf(distanceGraphOut, "\n");
	}

	fclose(distanceGraphOut);

}

/*
//find path from i to j, including a mid-node whose index <= k
int SearchPath(int i, int j, int k){
	typedef struct vertex{
		int index;
		vertex neighbors[numNodes];
	}node;

	int distance = 0; 


	return distance;
}
*/