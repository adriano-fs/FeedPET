package com.feedpet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Comparator;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndContent;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.XmlReader;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Noticias extends Activity implements OnItemClickListener {

	private ListView listView;
	private Button b, c;
	String fileName, lnk;
	String lnk_g[];
	private ArrayList<SyndEntry> list;
	private MyAdapter adapter;
	TextView grupo;
	EditText palavra;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_layout);


		list = new ArrayList<SyndEntry>();

		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(this);
		grupo = (TextView) findViewById(R.id.tv);

		fileName = getIntent().getExtras().getString("grupoPET");
		grupo.setText(fileName);
		grupo.setBackgroundColor(Color.GRAY);
		grupo.setTextColor(Color.WHITE);

		if(fileName.equals("UERN")){
			lnk_g =  getIntent().getExtras().getStringArray("link");
		}else{
			lnk = getIntent().getExtras().getString("link");
		}


		File feedFile = getBaseContext().getFileStreamPath(fileName);
		ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		if (conMgr.getActiveNetworkInfo() == null) {

			// No connectivity. Check if feed File exists
			if (!feedFile.exists()) {
				// No connectivity & Feed file doesn't exist: Show alert to exit & check for connectivity
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(
						"Não foi possivel encontrar o servidor, \nPor favor verifique sua conectividade")
						.setTitle("FeedPET")
						.setCancelable(false)
						.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								finish();
							}
						});

				AlertDialog alert = builder.create();
				alert.show();
			} else {

				// No connectivty and file exists: Read feed from the File
				Toast toast = Toast.makeText(this,
						"Sem conectividade! Lendo última atualização...",
						Toast.LENGTH_LONG);
				toast.show();
				list = ReadFeed(fileName);
			}
		} else {
			// Connected - Start parsing	

			if(fileName.equals("UERN")) {
				try {
					ParserFeedGeral(lnk_g);

				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FeedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				try {
					ParserFeed(lnk);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FeedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}


			WriteFeed(list);
		}


		if (list != null) {
			adapter = new MyAdapter(this, list);

			listView.setAdapter(adapter);
			//adapter.notifyDataSetChanged();	
		} else {
			Toast.makeText(this, "Um erro ocorreu durante o download do feed.",
					Toast.LENGTH_LONG).show();
		}
		palavra = (EditText) findViewById(R.id.editText1);
		b = (Button) findViewById(R.id.button1);
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String p1 = palavra.getText().toString().toLowerCase();
				if (list != null) {
					adapter = new MyAdapter(getApplicationContext(), BuscaLista.buscaP(p1, list));
					listView.setAdapter(adapter);
				}
			}
		});


		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	private void ParserFeed(final String urlFeed) throws IllegalArgumentException, FeedException, IOException {

		URL url;
		url = new URL(urlFeed);
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed = input.build(new XmlReader(url));
		List entradas = feed.getEntries();
		Iterator it = entradas.iterator();

		while (it.hasNext()) {
			SyndEntry aux = (SyndEntry) it.next();
			list.add(aux);
		}
	}
	private void ParserFeedGeral(final String[] urlFeed) throws IllegalArgumentException, FeedException, IOException{

		SyndFeedInput input = new SyndFeedInput();
		URL url;
		int i;
		Date dt,atual;

		atual = new Date(System.currentTimeMillis());
		//dt = new Date (atual.getYear(),atual.getMonth()-1,1);

		dt = new Date (atual.getYear()-5,1,1);

		for(i=0;i<urlFeed.length;i++){
			url = new URL(urlFeed[i]);

			SyndFeed feed = input.build(new XmlReader(url));
			List entradas = feed.getEntries();
			Iterator it = entradas.iterator();

			while(it.hasNext()){
				SyndEntry aux = (SyndEntry) it.next();
				if(aux.getPublishedDate().after(dt))
					list.add(aux);
				else
					break;
			}
		}
		Collections.sort(list, new Comparator<SyndEntry>() {
			@Override
			public int compare(SyndEntry o1, SyndEntry o2) {
				if (o1.getPublishedDate() == null) {
					return 1;
				}
				if (o2.getPublishedDate() == null) {
					return -1;
				}
				return o2.getPublishedDate().compareTo(o1.getPublishedDate());
			}
		});

	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		SyndEntry item = (SyndEntry) parent.getAdapter().getItem(position);

		Intent i = new Intent(getApplicationContext(), ShowNotice.class);

		i.putExtra("titulo", item.getTitle());
		i.putExtra("link", item.getLink());

		if (item.getLink().indexOf("blogspot") == -1)
			i.putExtra("description", item.getDescription().getValue());
		else
			i.putExtra("description", getContentFeed(item));

		i.putExtra("grupo", grupo.getText());
		startActivity(i);
	}



	public static String getContentFeed(SyndEntry se) {
		StringBuilder sb = new StringBuilder();

		if (se.getContents() != null) {
			List contents = se.getContents();

			Iterator it = contents.iterator();

			while (it.hasNext()) {
				SyndContent aux = (SyndContent) it.next();
				sb.append(aux.getValue());
			}
		}

		return (sb.toString());
	}


	// Method to write the feed to the File
	private void WriteFeed(List<SyndEntry> data) {

		FileOutputStream fOut = null;
		ObjectOutputStream osw = null;

		try {
			fOut = openFileOutput(fileName, MODE_PRIVATE);
			osw = new ObjectOutputStream(fOut);
			osw.writeObject(data);
			osw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Method to read the feed from the File
	private ArrayList<SyndEntry> ReadFeed(String fName) {

		FileInputStream fIn = null;
		ObjectInputStream isr = null;

		ArrayList<SyndEntry> _feed = null;
		File feedFile = getBaseContext().getFileStreamPath(fileName);
		if (!feedFile.exists())
			return null;

		try {
			fIn = openFileInput(fName);
			isr = new ObjectInputStream(fIn);

			_feed = (ArrayList<SyndEntry>) isr.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fIn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return _feed;
	}

	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Noticias Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://com.feedpet/http/host/path")
		);
		AppIndex.AppIndexApi.start(client, viewAction);
	}

	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Noticias Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://com.feedpet/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);
		client.disconnect();
	}
}