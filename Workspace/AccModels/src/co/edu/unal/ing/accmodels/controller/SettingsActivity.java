package co.edu.unal.ing.accmodels.controller;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import co.edu.unal.ing.accmodels.R;

public class SettingsActivity extends Activity implements OnCheckedChangeListener{

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		CheckBox checkUseFilteredData = (CheckBox) findViewById(R.id.checkBoxUseFileteredData);
		
		checkUseFilteredData.setChecked(MainActivity.getUseFileteredData());
		
		checkUseFilteredData.setOnCheckedChangeListener(this);
		
	}
	public void onCheckedChanged(CompoundButton arg0, boolean useFileteredData) {
		MainActivity.setUseFileteredData(useFileteredData);
	}
	
}
