package co.edu.unal.ing.accmodels.controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import co.edu.unal.ing.accmodels.R;
import co.edu.unal.ing.accmodels.data_processing.Kalman;

public class SettingsActivity extends Activity{

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		CheckBox checkUseFilteredData = (CheckBox) findViewById(R.id.checkBoxUseFileteredData);
		checkUseFilteredData.setChecked(MainActivity.getUseFileteredData());
		checkUseFilteredData.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				MainActivity.setUseFileteredData(isChecked);
			}
		});
		
		int filterToUse = MainActivity.getFilterToUse();
		
		RadioButton radio1D = (RadioButton) findViewById(R.id.radioModel1D);
		radio1D.setChecked(filterToUse==Kalman.TYPE_1D);
		radio1D.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				MainActivity.setFilterToUse(Kalman.TYPE_1D);
			}
		});

		RadioButton radio3D = (RadioButton) findViewById(R.id.radioModel3D);
		radio3D.setChecked(filterToUse==Kalman.TYPE_3D);
		radio3D.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				MainActivity.setFilterToUse(Kalman.TYPE_3D);
			}
		});
		
	}
	
}
