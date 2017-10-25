package com.feedpet;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent.OnFinished;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndContent;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.XmlReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends Activity implements AdapterView.OnItemClickListener {


	private Spinner sp, sp2;
	ArrayAdapter<CharSequence> adpt1, adpt2, adpt3;
	private ListView listView;
	private ArrayList<SyndEntry> list;
	private MyAdapter myadapter;
	private Button b;
	int pos_sp2, pos_sp1,i;
	EditText palavra;
	String filename;

	private ProgressDialog pd;


	String[][] url = {{
			//"http://feeds.feedburner.com/PetCienciaDaComputacaoUern"
			"http://petcc.uern.br/?feed=rss2", "https://zapier.com/engine/rss/1693013/petpedagogia/",
			"https://zapier.com/engine/rss/2041361/petenfermagem/", "http://petcis-uern.blogspot.com/feeds/posts/default"},

			{"http://petpesca-ufersa.blogspot.com/feeds/posts/default","http://pet-gestaosocial.blogspot.com/feeds/posts/default"},

			{"http://petee.ct.ufrn.br/feed/", "http://petmedufrn.webnode.com/rss/all.xml",
					"http://petgeografiaufrn.blogspot.com/feeds/posts/default", "http://petfisicaufrn.blogspot.com/feeds/posts/default",
					"http://petufrn.blogspot.com/feeds/posts/default", "http://petquimicaufrn.blogspot.com/feeds/posts/default",
					"http://peteq-ufrn.blogspot.com/feeds/posts/default", "http://petcsufrn.blogspot.com.br/feeds/posts/default",
					"http://feeds.feedburner.com/ConexesDeSaberesUfrn"}};
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		list = new ArrayList<SyndEntry>();

		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(this);


		sp = (Spinner) findViewById(R.id.spinner1);
		sp2 = (Spinner) findViewById(R.id.spinner2);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.universidades, R.layout.spinner_item);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		adpt3 = ArrayAdapter.createFromResource(this, R.array.GRUPOSUERN, R.layout.spinner_item);
		adpt3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


		adpt2 = ArrayAdapter.createFromResource(this, R.array.GRUPOSUFERSA, R.layout.spinner_item);
		adpt2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		adpt1 = ArrayAdapter.createFromResource(this, R.array.GRUPOSUFRN, R.layout.spinner_item);
		adpt1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


		sp.setAdapter(adapter);

		sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				if (position == 0)
					sp2.setAdapter(adpt3);
				else if (position == 1)
					sp2.setAdapter(adpt2);
				else if (position == 2)
					sp2.setAdapter(adpt1);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});



		sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				pos_sp1 = sp.getSelectedItemPosition();
				pos_sp2 = sp2.getSelectedItemPosition();
				list.clear();
				filename = sp.getSelectedItem().toString()+sp2.getSelectedItem().toString();

				File feedFile = getBaseContext().getFileStreamPath(filename);
				ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

				if (conMgr.getActiveNetworkInfo() == null) {
					if (!feedFile.exists()) {
						AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
						builder.setMessage(
								"Por favor verifique sua conexão")
								.setCancelable(false)
								.setPositiveButton("Sair",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog,
																int id) {
												finish();
											}
										});

						AlertDialog alert = builder.create();
						alert.show();
					} else {

						Toast toast = Toast.makeText(getApplicationContext(),
								"Sem conectividade! Lendo última atualização...", Toast.LENGTH_LONG);
						toast.show();
						list = ReadFeed(filename);
					}
				} else {

					new AsyncLoadXMLFeed().execute();

				}

				if (list != null) {
					myadapter = new MyAdapter(getApplicationContext(), list);
					listView.setAdapter(myadapter);
					//adapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});


		palavra = (EditText) findViewById(R.id.editText1);
		b = (Button) findViewById(R.id.button1);
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String p1 = palavra.getText().toString().toLowerCase();
				if (list != null) {
					myadapter = new MyAdapter(getApplicationContext(), BuscaLista.buscaP(p1, list));
					listView.setAdapter(myadapter);
				}
			}
		});

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	private class AsyncLoadXMLFeed extends AsyncTask<Void, Void, Void> {


		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			pd = new ProgressDialog(MainActivity.this);
			pd.setMessage("Carregando...");
			pd.show();
		}


		@Override
		protected Void doInBackground(Void... params) {

			if (pos_sp2 != 0) {
				try {
					ParserFeed(url[pos_sp1][pos_sp2 - 1]);
				} catch (FeedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			else {
				for(i=0;i<url[pos_sp1].length;i++){
					if(!pd.isShowing())
						break;

					try {
						ParserFeed(url[pos_sp1][i]);
					} catch (FeedException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
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


			if (list != null)
				WriteFeed(list);

			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pd.dismiss();
			if (list != null) {
				myadapter = new MyAdapter(getApplicationContext(), list);
				listView.setAdapter(myadapter);
				//adapter.notifyDataSetChanged();
			}
		}
	}
	private void ParserFeed(final String urlFeed) throws IllegalArgumentException, FeedException, IOException {

		URL url;
		url = new URL(urlFeed);
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed = input.build(new XmlReader(url));
		List entradas = feed.getEntries();
		Iterator it = entradas.iterator();

		Date dt, atual;
		atual = new Date(System.currentTimeMillis());
		dt = new Date(atual.getYear() , atual.getMonth()-6, 1);

		while (it.hasNext()) {
			SyndEntry aux = (SyndEntry) it.next();
			if (aux.getPublishedDate().after(dt))
				list.add(aux);
			else
				break;
		}
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

		i.putExtra("grupo",(item.getAuthor()).toUpperCase());
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


	private void WriteFeed(List<SyndEntry> data) {

		FileOutputStream fOut = null;
		ObjectOutputStream osw = null;

		try {
			fOut = openFileOutput(filename, MODE_PRIVATE);
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

	private ArrayList<SyndEntry> ReadFeed(String fName) {

		FileInputStream fIn = null;
		ObjectInputStream isr = null;

		ArrayList<SyndEntry> _feed = null;
		File feedFile = getBaseContext().getFileStreamPath(filename);
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
				"Main Page", // TODO: Define a title for the content shown.
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
				"Main Page", // TODO: Define a title for the content shown.
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