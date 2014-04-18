import java.io.*;
import java.util.*;

import java.lang.Thread;
import java.util.concurrent.Semaphore;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class GraphGUI {

	static int calcCounter = 1;
	public long startTime, endTime;

	public static final int WIDTH = 512;
	public static final int HEIGHT = 512;
	public static final int RADIUS = 200;
	public static final int CENTER = 256;
	public static final int NODE_SIZE = 6;
	public static final int OFFSET = NODE_SIZE/2;	// necessary to put the 'center' of each node in it's center

	public static boolean showNumbers = false;
	public static boolean showNodes = true;
	public static boolean showEdges = false;
	public static boolean showFailedPaths = true;
	public static boolean calculateAStar = true;
	public static boolean showAStarPath = true;
	public static boolean dijkstrasHeuristic = false;
	public static boolean parallelExecution = false;

	private MouseListener ML = new MouseListener();

	private int numNodes = 0;

	private JFrame frame;
	private JLabel menuBar, statusBar;
	private Screen screen;
	private volatile Graph graph;
	private JMenuBar bar;
	private JMenu fileMenu, nodeMenu, pathFindingMenu, graphMenu;
	private JMenuItem newGraph, exit,
		orange, yellow, green, cyan,
		showNodesOption, showEdgesOption,
		failedPathsOption, toggleNodeNums, removeObstacles, removeLastObstacle,
		useDijkstras, changeSource, changeGoal, toggleDrawPath, toggleParallel,
		enterDimensions;

	public final Color OUTLINE_COLOR = Color.black;
	public Color NODE_COLOR = Color.green;
	public Color EDGE_COLOR = Color.orange;
	public Color START_COLOR = Color.blue;
	public Color GOAL_COLOR = Color.red;
	public Color DEADPATH_COLOR = Color.red;
	public Color PATH_COLOR = Color.black;
	public Color OBSTACLE_COLOR = Color.darkGray;
	
	public boolean nodesHavePosition = false;

	// for obstacles
	public ArrayList<Integer> obstacleIndices;
	public Vector3D obstacleLineStart;
	public Vector3D obstacleLineFinish;

	// for A Star algorithm
	PriorityQueue<Node> openNodes;
	ArrayList<Node> closedNodes;
	ArrayList<Node> shortestPath;
	Node startNode;
	int sNodeIndex;
	Node goalNode;
	int gNodeIndex;
	
	// for bidirectional A Star
	PriorityQueue<Node> openNodesGTS;	// GTS = goal-to-start
	ArrayList<Node> closedNodesGTS;
	ArrayList<Node> shortestPathGTS;
	
	// PARALLEL PORTION:,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
	volatile AStarThread startToGoal;
	volatile AStarThread goalToStart;
	volatile ArrayList<Node> M;
	volatile float L;
	public static final int NUM_THREADS = 2;
	
	Semaphore threadLock;
	
	public boolean[] doneProcessing = new boolean[NUM_THREADS];
	public boolean firstThreadDone = false;
	public boolean done = false;
	// ^^^^^PARALLEL PORTION^^^^^^^^^^^^^^^^


	/**
	 * CONSTRUCTOR that builds the GUI.
	 */
	public GraphGUI() throws FileNotFoundException {
		newGraph();
		updateGraph();
	}

	public static void main(String[] args) throws FileNotFoundException {
		new GraphGUI();
	}

	
	private void newGraph() throws FileNotFoundException {
	
		obstacleIndices = new ArrayList<Integer>();

		frame = new JFrame("Graph Illustrator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		String pathName = "graphs\\";
		String fileName = JOptionPane.showInputDialog(frame, "Please enter the file name of new Graph data: ", "NEW GRAPH", JOptionPane.INFORMATION_MESSAGE);
		try {
			new Scanner(new File(pathName+fileName));
			graph = new Graph(pathName+fileName);
			initialize(graph);
			System.out.println("Program gets here?");
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(frame, "I'm sorry you have entered an incorrect file name.  Goodbye!", "ERROR!", JOptionPane.INFORMATION_MESSAGE);
			System.exit(0);
		}

		// Add the menu bar to the frame
		bar = new JMenuBar();
		frame.setJMenuBar(bar);
		
		fileMenu = new JMenu("File");		// Create file menu
		fileMenu.setMnemonic(KeyEvent.VK_F);
		bar.add(fileMenu);
		
		newGraph = new JMenuItem("New Graph");
		newGraph.setMnemonic(KeyEvent.VK_G);
		newGraph.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_1, ActionEvent.CTRL_MASK));		// adding a keyboard-shortcut, ctrl+1
		newGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String pathName = "graphs\\";
				String fileName = JOptionPane.showInputDialog(frame, "Please enter the file name of new Graph data: ", "NEW GRAPH", JOptionPane.INFORMATION_MESSAGE);
				try {
					new Scanner(new File(pathName+fileName));
					graph = new Graph(pathName+fileName);
					initialize(graph);
		
					if(numNodes == 1)
						menuBar.setText("Your graph has 1 node");
					else
						menuBar.setText("Your graph has " + numNodes + " nodes");
					updateGraph();
				}
				catch(Exception ex) {
					JOptionPane.showMessageDialog(frame, "I'm sorry you have entered an incorrect file name.  Goodbye!", "ERROR!", JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
				}
			}
		});
		fileMenu.add(newGraph);
		fileMenu.addSeparator();
		exit = new JMenuItem("Exit Program");
		exit.setMnemonic(KeyEvent.VK_X);
		exit.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_2, ActionEvent.CTRL_MASK));		// adding a keyboard-shortcut, ctrl+2
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, "Thanks for using Graph Illustrator!", "THANK YOU!", JOptionPane.INFORMATION_MESSAGE);
			    System.exit(0);
			}
		});
		fileMenu.add(exit);

		nodeMenu = new JMenu("Nodes");
		nodeMenu.setMnemonic(KeyEvent.VK_N);
		bar.add(nodeMenu);


		orange = new JMenuItem("Orange");
		orange.setMnemonic(KeyEvent.VK_O);
		orange.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_3, ActionEvent.CTRL_MASK));
		orange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NODE_COLOR = Color.orange;
				updateGraph();
			}
		});
		nodeMenu.add(orange);
		yellow = new JMenuItem("Yellow");
		yellow.setMnemonic(KeyEvent.VK_Y);
		yellow.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_4, ActionEvent.CTRL_MASK));
		yellow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NODE_COLOR = Color.yellow;
				updateGraph();
			}
		});
		nodeMenu.add(yellow);
		green = new JMenuItem("Green");
		green.setMnemonic(KeyEvent.VK_G);
		green.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_5, ActionEvent.CTRL_MASK));
		green.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NODE_COLOR = Color.green;
				updateGraph();
			}
		});
		nodeMenu.add(green);
		cyan = new JMenuItem("Cyan");
		cyan.setMnemonic(KeyEvent.VK_C);
		cyan.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_6, ActionEvent.CTRL_MASK));
		cyan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NODE_COLOR = Color.cyan;
				updateGraph();
			}
		});
		nodeMenu.add(cyan);
		nodeMenu.addSeparator();

		showNodesOption = new JMenuItem("Show Nodes");
		showNodesOption.setMnemonic(KeyEvent.VK_N);
		showNodesOption.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		showNodesOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showNodes = !showNodes;
				updateGraph();
			}
		});
		nodeMenu.add(showNodesOption);
		showEdgesOption = new JMenuItem("Show Edges");
		showEdgesOption.setMnemonic(KeyEvent.VK_E);
		showEdgesOption.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		showEdgesOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showEdges = !showEdges;
				updateGraph();
			}
		});
		nodeMenu.add(showEdgesOption);
		failedPathsOption = new JMenuItem("Show Failed Paths");
		failedPathsOption.setMnemonic(KeyEvent.VK_F);
		failedPathsOption.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		failedPathsOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showFailedPaths= !showFailedPaths;
				updateGraph();
			}
		});
		nodeMenu.add(failedPathsOption);
		toggleNodeNums = new JMenuItem("Toggle Node Numbers");
		toggleNodeNums.setMnemonic(KeyEvent.VK_T);
		toggleNodeNums.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_0, ActionEvent.CTRL_MASK));
		toggleNodeNums.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showNumbers = !showNumbers;
				updateGraph();
			}
		});
		nodeMenu.add(toggleNodeNums);
		removeObstacles = new JMenuItem("Remove All Obstacles");
		removeObstacles.setMnemonic(KeyEvent.VK_R);
		removeObstacles.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		removeObstacles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				obstacleIndices.clear();
				assignStartAndGoal(sNodeIndex, gNodeIndex);
				updateGraph();
			}
		});
		nodeMenu.add(removeObstacles);
		removeLastObstacle = new JMenuItem("Remove Last Obstacle");
		removeLastObstacle.setMnemonic(KeyEvent.VK_BACK_SPACE);
		removeLastObstacle.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_BACK_SPACE, ActionEvent.CTRL_MASK));
		removeLastObstacle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!obstacleIndices.isEmpty()) {
					int lastIndex = obstacleIndices.size() - 1;
					obstacleIndices.remove(lastIndex);
					assignStartAndGoal(sNodeIndex, gNodeIndex);
					updateGraph();
				}
			}
		});
		nodeMenu.add(removeLastObstacle);


		pathFindingMenu = new JMenu("Pathfinding");
		pathFindingMenu.setMnemonic(KeyEvent.VK_P);
		bar.add(pathFindingMenu);
		
		changeSource = new JMenuItem("Change Source Node");
		changeSource.setMnemonic(KeyEvent.VK_S);
		changeSource.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_7, ActionEvent.CTRL_MASK));
		changeSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String sourceNum = JOptionPane.showInputDialog(frame, "Please enter the new source node: ", "NEW SOURCE NODE", JOptionPane.INFORMATION_MESSAGE);
				try {
					int temp = Integer.parseInt(sourceNum);
					if(!(temp < 0 || temp >= numNodes)) {
						assignStartAndGoal(temp, gNodeIndex);
						updateGraph();
					}
					else
						JOptionPane.showMessageDialog(frame, "Your node index is out of range.",
							"ERROR!", JOptionPane.INFORMATION_MESSAGE);
				}
				catch(NumberFormatException nfe) {
					JOptionPane.showMessageDialog(frame, "You didn't enter an integer value.",
						"ERROR!", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		pathFindingMenu .add(changeSource);
		
		changeGoal = new JMenuItem("Change Goal Node");
		changeGoal.setMnemonic(KeyEvent.VK_G);
		changeGoal.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_8, ActionEvent.CTRL_MASK));
		changeGoal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String goalNum = JOptionPane.showInputDialog(frame, "Please enter the new goal node: ",
					"NEW GOAL NODE", JOptionPane.INFORMATION_MESSAGE);
				try {
					int temp = Integer.parseInt(goalNum);
					if(!(temp < 0 || temp >= numNodes)) {
						assignStartAndGoal(sNodeIndex, temp);
						updateGraph();
					}
					else
						JOptionPane.showMessageDialog(frame, "Your node index is out of range.",
							"ERROR!", JOptionPane.INFORMATION_MESSAGE);
				}
				catch(NumberFormatException nfe) {
					JOptionPane.showMessageDialog(frame, "You didn't enter an integer value.",
						"ERROR!", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		pathFindingMenu .add(changeGoal);
		pathFindingMenu.addSeparator();

		useDijkstras = new JMenuItem("Toggle Dijkstra's");
		useDijkstras.setMnemonic(KeyEvent.VK_D);
		useDijkstras.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_D, ActionEvent.CTRL_MASK));
		useDijkstras.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dijkstrasHeuristic = !dijkstrasHeuristic;
				assignStartAndGoal(sNodeIndex, gNodeIndex);
				updateGraph();
			}
		});
		pathFindingMenu.add(useDijkstras);

		toggleDrawPath = new JMenuItem("Toggle Path Drawing");
		toggleDrawPath.setMnemonic(KeyEvent.VK_T);
		toggleDrawPath.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_9, ActionEvent.CTRL_MASK));
		toggleDrawPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showAStarPath = !showAStarPath;
				updateGraph();
			}
		});
		pathFindingMenu.add(toggleDrawPath);
		
		toggleParallel = new JMenuItem("Toggle Parallel Execution");
		toggleParallel.setMnemonic(KeyEvent.VK_P);
		toggleParallel.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		toggleParallel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parallelExecution = !parallelExecution;
				assignStartAndGoal(sNodeIndex, gNodeIndex);
				updateGraph();
			}
		});
		pathFindingMenu.add(toggleParallel);
		
		


		graphMenu = new JMenu("Create Custom Grid");
		graphMenu.setMnemonic(KeyEvent.VK_G);
		bar.add(graphMenu);
		
		enterDimensions = new JMenuItem("Enter Grid Dimensions");
		enterDimensions.setMnemonic(KeyEvent.VK_G);
		enterDimensions.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_G, ActionEvent.CTRL_MASK));
		enterDimensions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int rowNum, colNum;
				String dimensions = JOptionPane.showInputDialog(frame, "Please enter the new # of rows: ", "NEW # OF ROWS", JOptionPane.INFORMATION_MESSAGE);
				try {
					rowNum = Integer.parseInt(dimensions);
					if(rowNum < 0 || rowNum > 100) {
						JOptionPane.showMessageDialog(frame, "You need to enter an integer between 1-100, for # of rows.",
							"ERROR!", JOptionPane.INFORMATION_MESSAGE);
						rowNum = 10;
					}
				}
				catch(NumberFormatException nfe) {
					JOptionPane.showMessageDialog(frame, "You didn't enter an integer value.",
						"ERROR!", JOptionPane.INFORMATION_MESSAGE);
					rowNum = 10;
				}
				dimensions = JOptionPane.showInputDialog(frame, "Please enter the new # of columns: ", "NEW # OF COLUMNS", JOptionPane.INFORMATION_MESSAGE);
				try {
					colNum = Integer.parseInt(dimensions);
					if(colNum < 0 || colNum > 100) {
						JOptionPane.showMessageDialog(frame, "You need to enter an integer between 1-100, for # of rows.",
							"ERROR!", JOptionPane.INFORMATION_MESSAGE);
						colNum = 10;
					}
				}
				catch(NumberFormatException nfe) {
					JOptionPane.showMessageDialog(frame, "You didn't enter an integer value.",
						"ERROR!", JOptionPane.INFORMATION_MESSAGE);
					colNum = 10;
				}
				new CreateGridGraph(rowNum, colNum);
				try {
					graph = new Graph("graphs\\grid.txt");
					initialize(graph);
					
					if(numNodes == 1)
						menuBar.setText("Your graph has 1 node");
					else
						menuBar.setText("Your graph has " + numNodes + " nodes");
					
					updateGraph();
				} catch(FileNotFoundException fnfe) {
					JOptionPane.showMessageDialog(frame, "This file doesn't exist!",
						"ERROR!", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		graphMenu.add(enterDimensions);

		
		if(numNodes == 1)
			menuBar = new JLabel("Your graph has 1 node");
		else
			menuBar = new JLabel("Your graph has " + numNodes + " nodes");
		frame.add(menuBar, BorderLayout.NORTH);


		screen = new Screen();
		screen.setSize(WIDTH, HEIGHT);
		screen.setBackground(Color.lightGray);
		screen.addMouseListener(ML);
		screen.addMouseMotionListener(ML);
		frame.add(screen, BorderLayout.CENTER);

		
		statusBar = new JLabel("Welcome to the Graph Illustrator!");	// the initial message for the statusBar JLabel
		frame.add(statusBar, BorderLayout.SOUTH);

		frame.setVisible(true);
		frame.setSize(522, 600);	// 1024, 576
		frame.setResizable(false);
		frame.setLocationRelativeTo( null );
//		frame.pack();
	}
	
	private void initialize(Graph g) {
		nodesHavePosition = graph.nodesHavePos();
		numNodes = graph.getNumNodes();
		assignStartAndGoal(graph.startNode, graph.goalNode);
	}

	private void updateGraph() {
		screen.repaint();
	}

	private class Screen extends JPanel {
		// included to avoid a '@Warning'
		static final long serialVersionUID = 0;
		// This is an overridden method common to all components.
		// It is implicitly called when a component is repainted.
		// Its purpose is to control the visual display of the component.
		public void paintComponent(Graphics g) {
			// According to the 'Connect4' example, the superclass
			// is somewhat necessary to call to avoid errors
			super.paintComponents(g);
			
			double[] x = new double[numNodes];
			double[] y = new double[numNodes];

			if(nodesHavePosition) {
				g.setColor(getBackground());
				g.fillRect(0, 0, getWidth(), getHeight());

				g.setColor(OUTLINE_COLOR);	// for the center
				g.drawOval(CENTER-(OFFSET>>2), CENTER-(OFFSET>>2), NODE_SIZE>>2, NODE_SIZE>>2);

				for(int i = 0; i < numNodes; ++i) {
					double xCoord = graph.nodeList.get(i).getPos().x;
					x[i] = xCoord;
					double yCoord = graph.nodeList.get(i).getPos().y;
					y[i] = yCoord;

					
					g.setColor(PATH_COLOR);
					if(showNumbers) {
						if(i < 10) {
							char[] number = {(char)('0' + i)};
							g.drawChars(number, 0, 1, (int)x[i], (int)y[i]-OFFSET);
						}
						else if(i < 100) {
							char[] number = {(char)('0' + (i/10)), (char)('0' + (i%10))};
							g.drawChars(number, 0, 2, (int)x[i], (int)y[i]-OFFSET);
						}
						else {
							char[] number = {(char)('0' + (i/100)), (char)('0' + ((i%100)/10)), (char)('0' + ((i%10)%10))};
							g.drawChars(number, 0, 3, (int)x[i], (int)y[i]-OFFSET);
						}
					}
				}
			}
			else {
				if(numNodes == 0) {
					g.setColor(getBackground());
					g.fillRect(0, 0, getWidth(), getHeight());

					g.setColor(OUTLINE_COLOR);	// for the center
					g.drawOval(CENTER-(OFFSET>>2), CENTER-(OFFSET>>2), NODE_SIZE>>2, NODE_SIZE>>2);
				}
				else {
					g.setColor(getBackground());
					g.fillRect(0, 0, getWidth(), getHeight());

					g.setColor(OUTLINE_COLOR);	// for the center
					g.drawOval(CENTER-(OFFSET>>2), CENTER-(OFFSET>>2), NODE_SIZE>>2, NODE_SIZE>>2);

					double angleBetween = (2*Math.PI) / numNodes;
					double[] angle = new double[numNodes];

					if(numNodes == 1) {
						g.setColor(NODE_COLOR);
						g.fillOval(CENTER-OFFSET, CENTER-OFFSET, NODE_SIZE, NODE_SIZE);

						g.setColor(OUTLINE_COLOR);
						g.drawOval(CENTER-OFFSET, CENTER-OFFSET, NODE_SIZE, NODE_SIZE);

						char[] number = {'0'};
						g.drawChars(number, 0, 1, CENTER, CENTER + 28);
					}
					else {
						for(int i = 0; i < numNodes; ++i) {
							angle[i] = (i * angleBetween) - (Math.PI / 2);
							double xCoord = 256 + calcCenterX(angle[i]);
							x[i] = xCoord;
							double yCoord = 256 + calcCenterY(angle[i]);
							y[i] = yCoord;


							if(showNumbers) {
								if(i < 10) {
									char[] number = {(char)('0' + i)};
									g.drawChars(number, 0, 1, 252+(int)(calcCenterX(angle[i])*1.12), 260+(int)(calcCenterY(angle[i])*1.12));
								}
								else if(i < 100) {
									char[] number = {(char)('0' + (i/10)), (char)('0' + (i%10))};
									g.drawChars(number, 0, 2, 252+(int)(calcCenterX(angle[i])*1.12), 260+(int)(calcCenterY(angle[i])*1.12));
								}
								else {
									char[] number = {(char)('0' + (i/100)), (char)('0' + ((i%100)/10)), (char)('0' + ((i%10)%10))};
									g.drawChars(number, 0, 3, 252+(int)(calcCenterX(angle[i])*1.12), 260+(int)(calcCenterY(angle[i])*1.12));
								}
							}
						}
					}
				}
			}
			System.out.println("Calculation #"+(calcCounter++));
			if(calculateAStar) {
				//shortestPath = calcBidirectionalAStar();	// need to add serial bidirectional
				if(parallelExecution) {
					parallelBidirectionalAStar();
				}
				else {
					startTime = System.nanoTime();
					shortestPath = calcAStar();
					endTime = System.nanoTime();
				}
				String exTime = "Execution time: "+
					((float)(endTime - startTime)/1000000000)+" seconds.";
				statusBar.setText(exTime);
				calculateAStar = false;
			}
			if(showEdges) {
				drawEdges(g, x, y);
			}
			if(showNodes) {
				drawNodes(g, x, y);
			}
			if(showFailedPaths) {
				drawFailedPathEdges(g, x, y);
			}
			if(showAStarPath && shortestPath != null) {
				displayAStarPath(g, x, y);
			}
		}
	}


	public void assignStartAndGoal(int s, int g) {
		sNodeIndex = s;
		gNodeIndex = g;
		startNode = graph.nodeList.get(s);
		goalNode = graph.nodeList.get(g);
		graph.resetNodes(obstacleIndices);
		
		calculateAStar = true;
		showAStarPath = true;
		
		openNodes = new PriorityQueue<Node>();
		closedNodes = new ArrayList<Node>();
		shortestPath= new ArrayList<Node>();

		// only for bidirectional A Star
		openNodesGTS = new PriorityQueue<Node>();
		closedNodesGTS = new ArrayList<Node>();
		shortestPathGTS= new ArrayList<Node>();
	}


	// currently this is a unidirectional A* search
	public ArrayList<Node> calcAStar() {
		startNode.isStart = true;
		startNode.setG(0);
		if(dijkstrasHeuristic)
			startNode.setH(0);
		else
			startNode.setH(getDistBetween(startNode, goalNode));
		startNode.setF(startNode.G() + startNode.H());
		
		goalNode.isGoal = true;
		
		openNodes.clear();
		closedNodes.clear();
		openNodes.add(startNode);
		
		while(!openNodes.isEmpty()) {
			Node current = openNodes.peek();

			if(current.isGoal)
				return reconstructPath (current);

			current = openNodes.poll();
			closedNodes.add(current);

			for (Node neighbor : current.returnAdjNodes()) {

				if(!neighbor.isObstacle) {
					if(dijkstrasHeuristic)
						neighbor.setH(0);
					else
						neighbor.setH(getDistBetween(neighbor, goalNode));
					neighbor.setDiscovered(true);

					if(closedNodes.contains(neighbor)) {
						continue;
					}

					float neighborDistFromStart = current.G() + getDistBetween(current, neighbor);

					if((!openNodes.contains(neighbor)) || 
							(neighborDistFromStart < neighbor.G())) {
						neighbor.setPrevNode(current);
						neighbor.setG(neighborDistFromStart);
						neighbor.setF(neighbor.G() + neighbor.H());
						if(!openNodes.contains(neighbor)) {
							openNodes.add(neighbor);
						}
					}
				}
			}
		}
		return null;
	}


	// currently this is bidirectional A* search
/*	public ArrayList<Node> calcBidirectionalAStar() {
		startNode.isStart = true;
		startNode.setG(0);
		if(dijkstrasHeuristic)
			startNode.setH(0);
		else
			startNode.setH(getDistBetween(startNode, goalNode));
		startNode.setF(startNode.G() + startNode.H());
		
		goalNode.isGoal = true;

		// for the thread from start to goal node
		openNodes.clear();
		closedNodes.clear();
		openNodes.add(startNode);

		// for the thread from goal to start node
		openNodesGTS.clear();
		closedNodesGTS.clear();
		openNodesGTS.add(goalNode);

		while(!openNodes.isEmpty() || !openNodesGTS.isEmpty()) {
			Node currentSTG = openNodes.peek();
			Node currentGTS = openNodesGTS.peek();

			if(currentSTG.isGoal)
				return reconstructPath (currentSTG);
			else if(currentGTS.isGoal)
				return reconstructPath (currentGTS);

			currentSTG = openNodes.poll();
			closedNodes.add(currentSTG);
			
			currentGTS = openNodesGTS.poll();
			closedNodesGTS.add(currentGTS);

			int STGlength = currentSTG.returnAdjNodes().size();
			int GTSlength = currentGTS.returnAdjNodes().size();
			int longerAdjList = (STGlength > GTSlength) : (STGlength) ? (GTSlength);

			for (int counter = 0; counter < longerAdjList; ++counter) {
				if(STGlength <= longerAdjList) {
					Node STGneighbor = currentSTG.returnAdjNodes()[counter];
				
					if(dijkstrasHeuristic)
						STGneighbor.setH(0);
					else
						STGneighbor.setH(getDistBetween(STGneighbor, goalNode));
					STGneighbor.setDiscovered(true);

					if(closedNodes.contains(STGneighbor)) {
						continue;
					}

					float neighborDistFromStart = currentSTG.G() +
						getDistBetween(currentSTG, neighbor);

					if((!openNodes.contains(neighbor)) || 
							(neighborDistFromStart < neighbor.G())) {
						neighbor.setPrevNode(currentSTG);
						neighbor.setG(neighborDistFromStart);
						neighbor.setF(neighbor.G() + neighbor.H());
						if(!openNodes.contains(neighbor)) {
							openNodes.add(neighbor);
						}
					}
				}
				if(GTSlength <= longerAdjList) {
					Node STGneighbor = currentSTG.returnAdjNodes()[counter];
				
					if(dijkstrasHeuristic)
						neighbor.setH(0);
					else
						neighbor.setH(getDistBetween(neighbor, goalNode));
					neighbor.setDiscovered(true);

					if(closedNodes.contains(neighbor)) {
						continue;
					}

					float neighborDistFromStart = currentSTG.G() +
						getDistBetween(currentSTG, neighbor);

					if((!openNodes.contains(neighbor)) || 
							(neighborDistFromStart < neighbor.G())) {
						neighbor.setPrevNode(currentSTG);
						neighbor.setG(neighborDistFromStart);
						neighbor.setF(neighbor.G() + neighbor.H());
						if(!openNodes.contains(neighbor)) {
							openNodes.add(neighbor);
						}
					}
				}
			}
		}
		return null;
	}	*/

	

// CONTINUE WORKING ON THIS...
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
	
	
	
	public ArrayList<Node> reconstructPath(Node node) {
		ArrayList<Node> path = new ArrayList<Node>();
		node.isPath = true;
		while(node.getPrevNode() != null) {
			path.add(0, node);
			node = node.getPrevNode();
			node.isPath = true;
		}
		path.add(0, node);
		return path;
	}
	
	public float getDistBetween(Node n1, Node n2) {
		Vector3D p1 = n1.getPos();
		Vector3D p2 = n2.getPos();
		return (float) p1.distance(p2);
	}


	private double calcCenterX(double angle) {
		return RADIUS * Math.cos(angle);
	}

	private double calcCenterY(double angle) {
		return RADIUS * Math.sin(angle);
	}
	
	public void drawEdges(Graphics g, double[] x, double[] y) {
		for(int i = 0; i < numNodes; i++) {
			if(graph.nodeList.get(i).hasNoConnections());	// if a node has no connections, no lines are drawn
			else {
				for(int j = 0; j < graph.nodeList.get(i).returnAdjNodes().size(); j++) {
					if(graph.nodeList.get(i).getID() == graph.nodeList.get(i).returnAdjNodes().get(j).getID());
					else {
						g.setColor(EDGE_COLOR);
						int x1, y1, x2, y2;
						x1 = (int)x[graph.nodeList.get(i).getID()];
						y1 = (int)y[graph.nodeList.get(i).getID()];
						x2 = (int)x[graph.nodeList.get(i).returnAdjNodes().get(j).getID()];
						y2 = (int)y[graph.nodeList.get(i).returnAdjNodes().get(j).getID()];
						g.drawLine(x1, y1, x2, y2);
					}
				}
			}
		}
	}
	
	public void drawFailedPathEdges(Graphics g, double[] x, double[] y) {
		for(int i = 0; i < numNodes; i++) {
			if(graph.nodeList.get(i).hasNoConnections());	// if a node has no connections, no lines are drawn
			else {
				for(int j = 0; j < graph.nodeList.get(i).returnAdjNodes().size(); j++) {
					if(graph.nodeList.get(i).getID() == graph.nodeList.get(i).returnAdjNodes().get(j).getID());
					else {
						if(graph.nodeList.get(i).getPrevNode() != null &&
								graph.nodeList.get(i).getPrevNode().getID() ==
								graph.nodeList.get(i).returnAdjNodes().get(j).getID()) {
							g.setColor(DEADPATH_COLOR);
							int x1, y1, x2, y2;
							x1 = (int)x[graph.nodeList.get(i).getID()];
							y1 = (int)y[graph.nodeList.get(i).getID()];
							x2 = (int)x[graph.nodeList.get(i).returnAdjNodes().get(j).getID()];
							y2 = (int)y[graph.nodeList.get(i).returnAdjNodes().get(j).getID()];
							g.drawLine(x1, y1, x2, y2);
						}
					}
				}
			}
		}
	}
	
	public void drawNodes(Graphics g, double[] x, double[] y) {
		for(int i = 0; i < numNodes; i++) {
			// fill in color of nodes
			if(graph.nodeList.get(i).isDiscovered()) {
				g.setColor(Color.white);
			}
			else if(graph.nodeList.get(i).isObstacle) {
				g.setColor(OBSTACLE_COLOR);
			}
			else
				g.setColor(NODE_COLOR);
			g.fillOval((int)x[i]-OFFSET, (int)y[i]-OFFSET, NODE_SIZE, NODE_SIZE);

			// outline of nodes
			g.setColor(OUTLINE_COLOR);
			g.drawOval((int)x[i]-OFFSET, (int)y[i]-OFFSET, NODE_SIZE, NODE_SIZE);
		}
	}

	public void displayAStarPath(Graphics g, double[] x, double[] y) {
		for(int i = (shortestPath.size()-1); i >= 0; --i) {
			if(shortestPath.get(i).hasNoConnections());	// if a node has no connections, no lines are drawn
			else {
				g.setColor(PATH_COLOR);
				int x1, y1, x2, y2;
				x1 = (int)x[shortestPath.get(i).getID()];
				y1 = (int)y[shortestPath.get(i).getID()];
				if(shortestPath.get(i).getPrevNode() != null) {
					x2 = (int)x[shortestPath.get(i).getPrevNode().getID()];
					y2 = (int)y[shortestPath.get(i).getPrevNode().getID()];
					g.drawLine(x1, y1, x2, y2);
				}
				if(shortestPath.get(i).isStart)
					g.setColor(START_COLOR);
				else if(shortestPath.get(i).isGoal)
					g.setColor(GOAL_COLOR);
				g.fillOval((int)x1-OFFSET, (int)y1-OFFSET, NODE_SIZE, NODE_SIZE);

				//g.setColor(OUTLINE_COLOR);
				g.drawOval((int)x1-OFFSET, (int)y1-OFFSET, NODE_SIZE, NODE_SIZE);
			}
		}
	}
	
	public Graph getGraph() {
		return graph;
	}


	class MouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			int xTemp = e.getX();
			int yTemp = e.getY();
			Vector3D temp = new Vector3D(xTemp, yTemp, 0.0);
			//System.out.println(temp +
			//	" Left Button: " + (MouseEvent.BUTTON1 == e.getButton()) +
			//	" Right Button: " + (MouseEvent.BUTTON3 == e.getButton()) );
			Node closestNode = graph.nodeList.get(0);
			float closestPointDist = (float) (closestNode.getPos()).xyDistance(temp);
			for(int i = 0; i < numNodes; ++i) {
				Node current = graph.nodeList.get(i);
				float currentDist = (float) (current.getPos()).xyDistance(temp);
				if(currentDist < closestPointDist) {
					closestPointDist = currentDist;
					closestNode = current;
				}
			}
			int index = closestNode.getID();
			if(MouseEvent.BUTTON1 == e.getButton()) {
				assignStartAndGoal(index, gNodeIndex);
				updateGraph();
			}
			else if((MouseEvent.BUTTON3 == e.getButton())) {
				assignStartAndGoal(sNodeIndex, index);
				updateGraph();
			}
			// middle mouse button
			/*else if((MouseEvent.BUTTON2 == e.getButton())) {
				assignStartAndGoal(sNodeIndex, gNodeIndex);
				if(!obstacleIndices.contains(index)) {
					obstacleIndices.add(index);
				}
				graph.nodeList.get(index).isObstacle = true;
				updateGraph();
			}	*/
		}

		public void mouseMoved(MouseEvent e) {
			/*// middle mouse button
			if((MouseEvent.BUTTON2 == e.getButton())) {
				int xTemp = e.getX();
				int yTemp = e.getY();
				Vector3D temp = new Vector3D(xTemp, yTemp, 0.0);
				//System.out.println(temp +
				//	" Left Button: " + (MouseEvent.BUTTON1 == e.getButton()) +
				//	" Right Button: " + (MouseEvent.BUTTON3 == e.getButton()) );
				Node closestNode = graph.nodeList.get(0);
				float closestPointDist = (float) (closestNode.getPos()).xyDistance(temp);
				for(int i = 0; i < numNodes; ++i) {
					Node current = graph.nodeList.get(i);
					float currentDist = (float) (current.getPos()).xyDistance(temp);
					if(currentDist < closestPointDist) {
						closestPointDist = currentDist;
						closestNode = current;
					}
				}
				int index = closestNode.getID();

				if(!obstacleIndices.contains(index)) {
					obstacleIndices.add(index);
				}
				graph.nodeList.get(index).isObstacle = true;
				assignStartAndGoal(sNodeIndex, gNodeIndex);
				updateGraph();
			}	*/
		}
		
		/**
		 * Not utilized, just included because of the MouseListener.
		 */
		public void mousePressed(MouseEvent e) {
/*			int xTemp = e.getX();
			int yTemp = e.getY();
			Vector3D temp = new Vector3D(xTemp, yTemp, 0.0);
			//System.out.println(temp +
			//	" Left Button: " + (MouseEvent.BUTTON1 == e.getButton()) +
			//	" Right Button: " + (MouseEvent.BUTTON3 == e.getButton()) );
			Node closestNode = graph.nodeList.get(0);
			float closestPointDist = (float) (closestNode.getPos()).xyDistance(temp);
			for(int i = 0; i < numNodes; ++i) {
				Node current = graph.nodeList.get(i);
				float currentDist = (float) (current.getPos()).xyDistance(temp);
				if(currentDist < closestPointDist) {
					closestPointDist = currentDist;
					closestNode = current;
				}
			}
			int index = closestNode.getID();
			// middle mouse button
			if((MouseEvent.BUTTON2 == e.getButton())) {
				assignStartAndGoal(sNodeIndex, gNodeIndex);
				if(!obstacleIndices.contains(index)) {
					obstacleIndices.add(index);
				}
				graph.nodeList.get(index).isObstacle = true;
				updateGraph();
			}	*/
		}
		
		/**
		 * Not utilized, just included because of the MouseListener.
		 */
		public void mouseDragged(MouseEvent e) {
			int xTemp = e.getX();
			int yTemp = e.getY();
			Vector3D temp = new Vector3D(xTemp, yTemp, 0.0);
			//System.out.println(temp +
			//	" Left Button: " + (MouseEvent.BUTTON1 == e.getButton()) +
			//	" Right Button: " + (MouseEvent.BUTTON3 == e.getButton()) );
			Node closestNode = graph.nodeList.get(0);
			float closestPointDist = (float) (closestNode.getPos()).xyDistance(temp);
			for(int i = 0; i < numNodes; ++i) {
				Node current = graph.nodeList.get(i);
				float currentDist = (float) (current.getPos()).xyDistance(temp);
				if(currentDist < closestPointDist) {
					closestPointDist = currentDist;
					closestNode = current;
				}
			}
			int index = closestNode.getID();
			// middle mouse button
			if((MouseEvent.BUTTON2 == e.getButton())) {
//				assignStartAndGoal(sNodeIndex, gNodeIndex);
				if(!obstacleIndices.contains(index)) {
					obstacleIndices.add(index);
				}
				graph.nodeList.get(index).isObstacle = true;
				updateGraph();
			}
		}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
	}
}
