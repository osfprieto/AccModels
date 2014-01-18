package co.edu.unal.ing.accmodels.gui;

import android.graphics.Color;
import co.edu.unal.ing.accmodels.R;
import co.edu.unal.ing.accmodels.controller.MainActivity;
import co.edu.unal.ing.accmodels.controller.VectorController;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

public class PlotUpdater {
	
	public static final int MAX_HISTORY = 20;
	
	private static int colorRaw = Color.BLUE;
	private static int colorFiltered = Color.RED;
	
						//3x2
	SimpleXYSeries series[][];
	XYPlot plots[];
	
	public PlotUpdater(MainActivity caller){
		
		series = new SimpleXYSeries[3][2];
		plots = new XYPlot[3];
		
		plots[0] = (XYPlot) caller.findViewById(R.id.plotX);
		plots[1] = (XYPlot) caller.findViewById(R.id.plotY);
		plots[2] = (XYPlot) caller.findViewById(R.id.plotZ);
		
		for(int i=0;i<3;i++){
			plots[i].setRangeBoundaries(-VectorController.DISTANCE_FACTOR,
					VectorController.DISTANCE_FACTOR,
					BoundaryMode.FIXED);
			plots[i].setDomainBoundaries(0,
					MAX_HISTORY,
					BoundaryMode.FIXED);
		}
		
		series[0][0] = new SimpleXYSeries(caller.getString(R.string.xRaw));
		series[0][0].useImplicitXVals();
		series[0][1] = new SimpleXYSeries(caller.getString(R.string.xFiltered));
		series[0][1].useImplicitXVals();
		
		series[1][0] = new SimpleXYSeries(caller.getString(R.string.yRaw));
		series[1][0].useImplicitXVals();
		series[1][1] = new SimpleXYSeries(caller.getString(R.string.yFiltered));
		series[1][1].useImplicitXVals();
		
		series[2][0] = new SimpleXYSeries(caller.getString(R.string.zRaw));
		series[2][0].useImplicitXVals();
		series[2][1] = new SimpleXYSeries(caller.getString(R.string.zFiltered));
		series[2][1].useImplicitXVals();
		
		LineAndPointFormatter formatter0 = new LineAndPointFormatter(
				colorRaw, Color.BLACK, null, null);
		LineAndPointFormatter formatter1 = new LineAndPointFormatter(
				colorFiltered, Color.BLACK, null, null);
		
		for(int i=0;i<3;i++){
			plots[i].addSeries(series[i][0], formatter0);
			plots[i].addSeries(series[i][1], formatter1);
		}
		
	}
	
	public void updatePlots(float rawData[], float filteredData[]){
	
		for(int i=0;i<3;i++)
			for(int j=0;j<2;j++)
				if(series[i][j].size()>MAX_HISTORY)
					series[i][j].removeFirst();
		for(int i=0;i<3;i++){
			series[i][0].addLast(null, rawData[i]);
			series[i][1].addLast(null, filteredData[i]);
			
			plots[i].redraw();
		}
		
	}
	
	public static void setColorRaw(int color){
		colorRaw = color;
	}
	
	public static void setColorFiltered(int color){
		colorFiltered = color;
	}
	
}
