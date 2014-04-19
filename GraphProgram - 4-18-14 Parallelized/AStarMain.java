

class AStarMain {
	public AStarMain() {
		
	}
	
	public void parallelBidirectionalAStar() {
		M = new ArrayList<Node>(graph.nodeList); 
		L = Float.MAX_VALUE;
		
		threadLock = new Semaphore(1, true);
		
//		Node startTemp = startNode.copy();
//		Node goalTemp = goalNode.copy();
	
		startToGoal = new AStarThread("1", this, startNode, goalNode);
		goalToStart = new AStarThread("2", this, goalNode, startNode);
		startToGoal.setOtherThread(goalToStart);
		goalToStart.setOtherThread(startToGoal);

		startNode.isStart = true;
		goalNode.isGoal = true;

		startTime = System.nanoTime();
//		System.out.println("This is printed before the threads run...");
		startToGoal.run();
		goalToStart.run();		
		waitForThreads();
		endTime = System.nanoTime();

		shortestPath = startToGoal.sPath;
//		System.out.println("size of thread 1's: "+shortestPath.size());
		shortestPathGTS = goalToStart.sPath;
//		System.out.println("size of thread 2's: "+shortestPathGTS.size());
		shortestPath.addAll(shortestPathGTS);
//		System.out.println("size of both threads': "+shortestPath.size());
		Node middleOne = null;
		int countTest = 0;
		for(Node test : shortestPath) {
			test.isPath = true;
			graph.nodeList.get(test.getID()).isPath = true;
			if(test.getPrevNode(1) == null && test.getPrevNode(2) == null) {
				middleOne = test;
				++countTest;
//				System.out.println("In for-loop: "+middleOne.getID()+" count:"+countTest);
			}
//			System.out.print("["+test.getID()+"],");
		}
		System.out.println();
//		if(middleOne!=null)
//			System.out.println("After for-loop: "+middleOne.getID()+" count:"+countTest+
//				" shortest path size:"+shortestPath.size());
	}

	
	public void waitForThreads() {
		System.out.println("\n\n*********************************************"+
			"\nWaiting for all threads to complete..."+
			"\n*********************************************\n");
		while(!done) {
			int numThreadsDone = 0;
			for(int i = 0; i < NUM_THREADS; i++) {
				if(doneProcessing[i]){
					++numThreadsDone;
				}
			}
			System.out.print("");
			
			if(numThreadsDone==NUM_THREADS) {
				done = true;
			}
		}
		// resets for next time...
		doneProcessing = new boolean[NUM_THREADS];
		firstThreadDone = false;	// may not care to have this...
		done = false;
		
		//***************************************************
		// Since all threads have completed, the end time is marked...
		//***************************************************
		endTime = System.nanoTime();
		System.out.println("\n*********************************************"+
			"\nAll of the threads have finished\ncomputing shortest path"+
			"\n*********************************************\n\n");
	}
}