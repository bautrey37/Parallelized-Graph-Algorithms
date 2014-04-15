#include <iostream>
#include <set>
#include <vector>

#include "DijkstraAlgo.h"
#include "InputData.h"

using namespace std;

void printMatrix(int** m, int n)
{
	for (int i = 0; i < n; i++)
	{
		for (int j = 0; j < n; j++)
		{
			cout << m[i][j] << " ";
		}
		cout << endl;
	}
}


int main()
{
	char x; //to pause before exiting

	int n = 10; //nxn matrix of data
	
	//read or generate input file containing graph data
	InputData::InputData input = InputData::InputData();
	int** matrixInput = input.generateData(n);
	printMatrix(matrixInput, n);

	//choose algorithm and which type of priority queue to use

	
	//run algorithm on graph data

	//output timing results and path

	cout << "Code has completed" << endl;
	cin >> x;

	return 0;
}

