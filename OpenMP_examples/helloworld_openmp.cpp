// Helloorld_openmp.cpp : Defines the entry point for the console application.s
//

#include "stdafx.h"

#include <omp.h>
#include <stdio.h>
#include <stdlib.h>
#include <windows.h>

int main(int argc, char* argv[])
{

	// Get the number of processors in this system
	int iCPU = omp_get_num_procs();
	printf("Number of processors in system: %d\n", iCPU);

	// Now set the number of threads
	omp_set_num_threads(iCPU);

	// omp_get_thread_num() - returns thread rank in parallel region	

#pragma omp parallel 
	{
		printf("Thread rank : %d\n", omp_get_num_threads());
	}

	system("pause");
	return 0;
}

