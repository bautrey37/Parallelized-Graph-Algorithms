import java.io.*;
import java.util.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class GraphGUI {

	public long startTime, endTime;

	public static final int WIDTH = 512;
	public static final int HEIGHT = 512;
	public static final int RADIUS = 200;
	public static final int CENTER = 256;
	public static final int NODE_SIZE = 6;
	public static final int OFFSET = NODE_SIZE/2;	// necessary to put the 'center' of each node in it's center

	public static boolean showNumbers = false;
	public static boolean calculateAStar = true;
	public static boolean showAStarPath = true;
	public static boolean dijkstrasHeuristic = false;
	
	private mouseClickListener MCL = new mouseClickListener();

	private int numNodes = 0;

	private JFrame frame;
	private JLabel menuBar, statusBar;
	private Screen screen;
	private Graph graph;
	private JMenuBar bar;
	private JMenu fileMenu, nodeMenu, pathFindingMenu;
	private JMenuItem newGraph, exit,
		orange, yellow, green, cyan, toggleNodeNums,
		useDijkstras, changeSource, changeGoal, toggleDrawPath;

	public final Color OUTLINE_COLOR = Color.black;
	public Color NODE_COLOR = Color.green;
	public Color EDGE_COLOR = Color.orange;
	public Color START_COLOR = Color.blue;
	public Color GOAL_COLOR = Color.red;
	public Color PATH_COLOR = Color.black;
	
	public boolean nodesHavePosition = false;

	// for A Star algorithm
	PriorityQueue<Node> openNodes;
	ArrayList<Node> closedNodes;
	ArrayList<Node> shortestPath;
	Node startNode;
	int sNodeIndex;
	Node goalNode;
	int gNodeIndex;

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

		frame = new JFrame("Graph Illustrator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		String pathName = "graphs\\";
		String fileName = JOptionPane.showInputDialog(frame, "Please enter the file name of new Graph data: ", "NEW GRAPH", JOptionPane.INFORMATION_MESSAGE);
		try {
			new Scanner(new File(pathName+fileName));
			graph = new Graph(pathName+fileName);
			initialize(graph);
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


		pathFindingMenu = new JMenu("Pathfinding");
		pathFindingMenu.setMnemonic(KeyEvent.VK_P);
		bar.add(pathFindingMenu);
		
		changeSource = new JMenuItem("Change Source Node");
		changeSource.setMnemonic(KeyEvent.VK_S);
		changeSource.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_7, ActionEvent.CTRL_MASK));
		changeSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//showAStarPath = false;
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
				//showAStarPath = false;
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

		useDijkstras = new JMenuItem("Use Dijkstra's Heuristic");
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

		
		if(numNodes == 1)
			menuBar = new JLabel("Your graph has 1 node");
		else
			menuBar = new JLabel("Your graph has " + numNodes + " nodes");
		frame.add(menuBar, BorderLayout.NORTH);


		screen = new Screen();
		screen.setSize(WIDTH, HEIGHT);
		screen.setBackground(Color.lightGray);
		screen.addMouseListener(MCL);
		frame.add(screen, BorderLayout.CENTER);

		
		statusBar = new JLabel("Welcome to the Graph Illustrator!");	// the initial message for the statusBar JLabel
		frame.add(statusBar, BorderLayout.SOUTH);

		frame.setVisible(true);
		frame.setSize(522, 600);	// 1024, 576
		frame.setResizable(false);
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
				//drawEdges(g, x, y);
				//drawNodes(g, x, y);
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
						for(int i = 0; i < numNodes; i++) {
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
						//drawEdges(g, x, y);
						//drawNodes(g, x, y);
					}
				}
			}
			if(calculateAStar) {
				startTime = System.nanoTime();
				shortestPath = calcAStar(x, y);
				endTime = System.nanoTime();
				System.out.println("Execution time: "+
					((float)(endTime - startTime)/1000000000)+" seconds.");
				calculateAStar = false;
			}
			drawEdges(g, x, y);
			drawNodes(g, x, y);
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
		graph.resetNodes();
		
		calculateAStar = true;
		showAStarPath = true;
		
		openNodes = new PriorityQueue<Node>();
		closedNodes = new ArrayList<Node>();
		shortestPath= new ArrayList<Node>();
	}
	
	public ArrayList<Node> calcAStar(double[] x, double[] y) {
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

				if(dijkstrasHeuristic)
					neighbor.setH(0);
				else
					neighbor.setH(getDistBetween(neighbor, goalNode));
				neighbor.setDiscovered(true);

				if(closedNodes.contains(neighbor)) {
					//neighbor.setDiscovered(true);
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
		return null;
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
		Vector2D p1 = n1.getPos();
		Vector2D p2 = n2.getPos();
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
	
	public void drawNodes(Graphics g, double[] x, double[] y) {
		for(int i = 0; i < numNodes; i++) {
			// fill in color of nodes
			if(graph.nodeList.get(i).isDiscovered()) {
				g.setColor(Color.white);
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

				g.setColor(OUTLINE_COLOR);
				g.drawOval((int)x1-OFFSET, (int)y1-OFFSET, NODE_SIZE, NODE_SIZE);
			}
		}
	}

	class mouseClickListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			int xTemp = e.getX();
			int yTemp = e.getY();
			Vector2D temp = new Vector2D(xTemp, yTemp);
			//System.out.println(temp +
			//	" Left Button: " + (MouseEvent.BUTTON1 == e.getButton()) +
			//	" Right Button: " + (MouseEvent.BUTTON3 == e.getButton()) );
			Node closestNode = graph.nodeList.get(0);
			float closestPointDist = (float) (closestNode.getPos()).distance(temp);
			for(int i = 0; i < numNodes; ++i) {
				Node current = graph.nodeList.get(i);
				float currentDist = (float) (current.getPos()).distance(temp);
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
		}
		/**
		 * Not utilized, just included because of the MouseListener.
		 */
		public void mouseEntered(MouseEvent e) {}
		/**
		 * Not utilized, just included because of the MouseListener.
		 */
		public void mouseExited(MouseEvent e) {}
		/**
		 * Not utilized, just included because of the MouseListener.
		 */
		public void mousePressed(MouseEvent e) {}
		/**
		 * Not utilized, just included because of the MouseListener.
		 */
		public void mouseReleased(MouseEvent e) {}
	}
}
