import java.io.*;

public class CreateGridGraph {

	public static int numColumns;
	public static int numRows;

	// with integers...
	//public static final int NODE_SIZE = 8;
	//public static final int BORDER = NODE_SIZE*2;
	//public static final int HUSABLE_SPACE = 512 - BORDER;
	//public static final int VUSABLE_SPACE = 590 - BORDER - 78;
	
	// with floats...
	public static final float NODE_SIZE = 6;
	public static final float BORDER = NODE_SIZE*2;
	public static final float HUSABLE_SPACE = 512 - BORDER;
	public static final float VUSABLE_SPACE = 590 - BORDER - 78;
	private Writer gridWriter;
	private Writer circleWriter;

	public CreateGridGraph(int columns, int rows) {
		numColumns = columns;
		numRows = rows;
		
		// with integers...
		//int rowOffset = VUSABLE_SPACE / numRows;
		//int colOffset = HUSABLE_SPACE / numColumns;
		//int HBORDER = colOffset/2+NODE_SIZE;// + (int)((float)BORDER*1.4);
		//int VBORDER = rowOffset/2+NODE_SIZE;//30;// + NODE_SIZE/2;
		
		// with floats...
		float rowOffset = VUSABLE_SPACE / numRows;
		float colOffset = HUSABLE_SPACE / numColumns;
		float HBORDER = colOffset/2+NODE_SIZE;// + (int)((float)BORDER*1.4);
		float VBORDER = rowOffset/2+NODE_SIZE;//30;// + NODE_SIZE/2;

		gridWriter = null;
		circleWriter = null;

		try {
			gridWriter = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream("graphs\\grid.txt"), "utf-8"));
			circleWriter = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream("graphs\\circle.txt"), "utf-8"));
			
			int totalNodes = numColumns*numRows;
			
			// this sets up the source and goal nodes for A* search
			gridWriter.write(totalNodes+"\n1\n0 "+(totalNodes-1)+"\n\n");
			circleWriter.write(totalNodes+"\n0\n0 "+(totalNodes-1)+"\n\n");
			
			for(int i = 0; i < numRows; ++i) {
				for(int j = 0; j < numColumns; ++j) {
					gridWriter.write((j*colOffset+HBORDER)+" "+(i*rowOffset+VBORDER)+"\n");
				}
			}
			gridWriter.write("\n\n");
			circleWriter.write("\n\n");
			for(int i = 0; i < numRows; ++i) {
				for(int j = 0; j < numColumns; ++j) {
					addEdges(i,j);
				}
			}
		} catch (IOException ex) {
		// report
		} finally {
			try {gridWriter.close(); circleWriter.close();} catch (Exception ex) {}
		}		
	}
	
	public void addEdges(int i, int j) {
		try {
			// first add edges between diagonal neighbors
			// adds edges to the Southeast neighbor
			if(i!=(numRows-1) && j!=(numColumns-1)) {
				gridWriter.write((i*numColumns+j)+" "+((i+1)*numColumns+(j+1))+"\n");
				circleWriter.write((i*numColumns+j)+" "+((i+1)*numColumns+(j+1))+"\n");
			}
			// adds edges to the Northwest neighbor
			if(i!=0 && j!=0) {
				gridWriter.write((i*numColumns+j)+" "+((i-1)*numColumns+(j-1))+"\n");
				circleWriter.write((i*numColumns+j)+" "+((i-1)*numColumns+(j-1))+"\n");
			}
			// adds edges to the Southwest neighbor
			if(i!=(numRows-1) && j!=0) {
				gridWriter.write((i*numColumns+j)+" "+((i+1)*numColumns+(j-1))+"\n");
				circleWriter.write((i*numColumns+j)+" "+((i+1)*numColumns+(j-1))+"\n");
			}
			// adds edges to the Northeast neighbor
			if(i!=0 && j!=(numColumns-1)) {
				gridWriter.write((i*numColumns+j)+" "+((i-1)*numColumns+(j+1))+"\n");
				circleWriter.write((i*numColumns+j)+" "+((i-1)*numColumns+(j+1))+"\n");
			}

			// now add edges between straight neighbors
			// adds edges to the East neighbor
			if(j!=(numColumns-1)) {
				gridWriter.write((i*numColumns+j)+" "+(i*numColumns+(j+1))+"\n");
				circleWriter.write((i*numColumns+j)+" "+(i*numColumns+(j+1))+"\n");
			}
			// adds edges to the West neighbor
			if(j!=0) {
				gridWriter.write((i*numColumns+j)+" "+(i*numColumns+(j-1))+"\n");
				circleWriter.write((i*numColumns+j)+" "+(i*numColumns+(j-1))+"\n");
			}
			// adds edges to the South neighbor
			if(i!=(numRows-1)) {
				gridWriter.write((i*numColumns+j)+" "+((i+1)*numColumns+j)+"\n");
				circleWriter.write((i*numColumns+j)+" "+((i+1)*numColumns+j)+"\n");
			}
			// adds edges to the North neighbor
			if(i!=0) {
				gridWriter.write((i*numColumns+j)+" "+((i-1)*numColumns+j)+"\n");
				circleWriter.write((i*numColumns+j)+" "+((i-1)*numColumns+j)+"\n");
			}
		} catch(IOException ex) {
			//...
		} finally {
			//...
		}
	}

//	public static void main(String[] args) {
//		new CreateGridGraph(50, 50);
//	}
}