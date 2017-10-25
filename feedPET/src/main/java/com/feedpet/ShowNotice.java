package com.feedpet;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import java.text.DateFormat;
import java.util.Locale;



public class ShowNotice extends Activity {
	public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);      
        setContentView(R.layout.reading_layout);  
		
		TextView a=  (TextView)  findViewById (R.id.txt);
		TextView g=  (TextView)  findViewById (R.id.txtg);
		WebView b=  (WebView)  findViewById (R.id.txt2);
		
		// set webview properties
		WebSettings ws = b.getSettings();
		ws.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		ws.getPluginState();
		ws.setPluginState(PluginState.ON);
		ws.setJavaScriptEnabled(true);
		ws.setBuiltInZoomControls(true);
				
		Button but= (Button) findViewById(R.id.button1);
	
		
		Bundle extras = getIntent().getExtras();
		String content1 = extras.getString("titulo");
		final String content2 = extras.getString("description");
		final String link = extras.getString("link");

		if(link.contains("facebook")){
			g.setText((extras.getString("grupo")).toUpperCase());
			content1 = content1.replaceAll("\n"," ");
			if(content1.length()>80)
				content1=content1.substring(0,80);
			content1+="...";
		}

		else
			g.setText(link.substring(link.indexOf("//")+2, link.indexOf(".")).toUpperCase());
		
		
	
		
		b.loadDataWithBaseURL("http://www.androidcentral.com/", content2+"<br/>", "text/html", "UTF-8", null);
		
				
		//b.setMovementMethod(new ScrollingMovementMethod());
		
		a.setText(content1);
				
		but.setOnClickListener(  new View.OnClickListener() {  
		   	@Override
		   	public void onClick(View v){
		   		Uri uri = Uri.parse(link);
				
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);	
		   	}
		   });		
	}
	
}
