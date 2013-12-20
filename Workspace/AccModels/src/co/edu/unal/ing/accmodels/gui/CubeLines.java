package co.edu.unal.ing.accmodels.gui;

public class CubeLines {

	private static final float LINES[][] = {
			{-0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f},
    		{-0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f},
			{-0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f},
			{0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f},
			{0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f},
			{0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f},
			{0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f},
			{0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f},
			{0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f},
			{-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f},
			{0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f},
			{-0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f}
	};
    
	private Line linesToDraw[];
	
	public CubeLines(){
		linesToDraw = new Line[LINES.length];
		for(int i=0;i<LINES.length;i++){
			linesToDraw[i] = new Line();
			linesToDraw[i].setVerts(LINES[i]);
		}
	}
	
	public void draw(float[] mvpMatrix){
		for(Line line : linesToDraw){
			line.draw(mvpMatrix);
		}
	}
	
}
