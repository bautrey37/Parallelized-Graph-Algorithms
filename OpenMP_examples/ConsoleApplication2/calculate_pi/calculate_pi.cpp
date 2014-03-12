// ConsoleApplication2.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"

// cpp_compiler_options_openmp.cpp
#include <omp.h>
#include <stdio.h>
#include <stdlib.h>
#include <windows.h>
#include <iostream>
#include <vector>

using namespace std;

volatile DWORD dwStart;
volatile int global = 0;

double test2(int num_steps) {
	int i;
	global++;
	double x, pi, sum = 0.0, step;

	step = 1.0 / (double)num_steps;

	/* private(x) - prevents threads from sharing a variable x
	* reduction(+:sum) - does a reduction of the variable sum into a summation from the threads
	- left side of colon specifies the reduction operation, right side specifies the variables to do the operation on
	*/
#pragma omp parallel for reduction(+:sum) private(x) 
	for (i = 1; i <= num_steps; i++) {
		x = (i - 0.5) * step;
		sum = sum + 4.0 / (1.0 + x*x);
	}

	pi = step * sum;
	return pi;
}

int main(int argc, char* argv[]) {
	double   d;
	int n = 100000000;

	if (argc > 1)
		n = atoi(argv[1]);	

	// Get the number of processors in this system
	int iCPU = omp_get_num_procs();
	printf("Number of Processor in system: %d\n", iCPU);

	int threads[] = {4,3,2,1};
	for(int i = 8; i > 0; i--) { 
		omp_set_num_threads(i); //set thread count
		dwStart = GetTickCount();
		d = test2(n);
		printf_s("Number of threads used: %d\n", i);
		printf_s("For %d steps, pi = %.15f, %d milliseconds\n", n, d, GetTickCount() - dwStart);
	}

	system("pause");
}

