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
			PrintWriter pw = new PrintWriter(new File(route));
			
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
