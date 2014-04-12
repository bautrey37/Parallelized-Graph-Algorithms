#include "InputData.h"
namespace InputData {

	InputData::InputData()
	{
	}


	InputData::~InputData()
	{
	}

	int** InputData::generateData(int n)
	{
		int** input = new int*[n];
		for (int i = 0; i < n; i++)
			input[i] = new int[n];

		for (int i = 0; i < n; i++)
		{
			for (int j = 0; j < n; j++)
			{
				input[i][j] = 1;
			}
		}

		return input;
	}

}
