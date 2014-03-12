// ConsoleApplication3.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"

#include <iostream> //not thread safe
using namespace std;

#include <omp.h>

int main(int argc, char *argv[])
{
	int th_id, nthreads;
	#pragma omp parallel private(th_id) shared(nthreads)
	{
		th_id = omp_get_thread_num(); //
		#pragma omp critical //only one thread at a time can access this section
		{
			cout << "Hello World from thread " << th_id << '\n';
		}
		#pragma omp barrier //all threads synchronize here before moving on

		#pragma omp master //only the master thread executes this section
		{
			nthreads = omp_get_num_threads();
			cout << "There are " << nthreads << " threads" << '\n';
		}
	}
	
	system("pause");
	return 0;
}

