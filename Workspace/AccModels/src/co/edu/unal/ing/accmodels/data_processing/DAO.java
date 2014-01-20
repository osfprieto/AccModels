package co.edu.unal.ing.accmodels.data_processing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedList;

import android.os.Environment;

public class DAO {

	public static boolean saveData(LinkedList<String> message, String fileName){
    	String route = Environment.getExternalStorageDirectory().getAbsolutePath()+
    			"/AccModels/"+fileName;
    	
    	try {
    		File file = new File(route);
			PrintWriter pw = new PrintWriter(file);
			
			for(String s : message){
				pw.append(s);
				pw.append("\n");
			}
			
			pw.close();
			return true;
		} catch (FileNotFoundException e) {
			return false;
		}
    }
	
}
