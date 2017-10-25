package com.feedpet;



import android.app.Activity;
import android.os.Bundle;
import android.widget.*;


public class HomeSobre extends Activity {
	TextView textViewHomeSobre1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_sobre);
		textViewHomeSobre1 = (TextView) findViewById(R.id.textViewHomeSobre1);
	}
}
