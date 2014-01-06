package com.varun.xivelyviewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity
{
	private ArrayList<Datastream> streams;
	private String feed = "7546654";
	private DataAdapter adapter;
	private int lines;
	private EditText edittext;
	private ListView listview;
	private static InputStream is = null;
	private static InputStream isJ = null;
	private final String NOTFOUND = "I'm sorry we are unable to find the feed you are looking for.";
	private final String PRIVATE = "You do not have the necessary permissions to access this resource";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		listview = (ListView) findViewById(R.id.listView1);
		streams = new ArrayList<Datastream>();
		adapter = new DataAdapter(this, R.layout.listitem, streams);

		LayoutInflater inflater = getLayoutInflater();
		ViewGroup mTop = (ViewGroup) inflater.inflate(R.layout.listheader,listview, false);

		listview.addHeaderView(mTop, null, false);
		listview.setAdapter(adapter);
		edittext = (EditText) mTop.findViewById(R.id.editText1);
		
		int sdkVersion = android.os.Build.VERSION.SDK_INT;
		if (sdkVersion >= 14)
		{
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		
		edittext.setOnKeyListener(new OnKeyListener()
		{
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER))
				{
					TextView title = (TextView) findViewById(R.id.textView1);
					TextView updated = (TextView) findViewById(R.id.textView2);
					
					title.setText("Loading...");
					updated.setText("Please wait.");
					streams.clear();
					adapter.clear();
					adapter.notifyDataSetChanged();
					feed = edittext.getText().toString();
					new getLinesTask(adapter).execute();
				}
				return false;
			}
		});

	}

	
	private void gen()
	{

		new getMetaTask(listview).execute("https://api.xively.com/v2/feeds/"+ feed + ".json");
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			public void onItemClick(
					@SuppressWarnings("rawtypes") AdapterView parent, View v,
					int position, long id)
			{
				adapter.notifyDataSetChanged();
				new getMetaTask(listview)
						.execute("https://api.xively.com/v2/feeds/" + feed
								+ ".json");
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

	private class getMetaTask extends AsyncTask<String, Void, String[]>
	{
		private final WeakReference<ListView> listviewref;

		public getMetaTask(ListView l)
		{
			listviewref = new WeakReference<ListView>(l);
		}

		@Override
		protected String[] doInBackground(String... arg0)
		{
			return Utilities.getMeta(arg0[0]);
		}

		protected void onPostExecute(String[] meta)
		{
			TextView title = (TextView) findViewById(R.id.textView1);
			TextView updated = (TextView) findViewById(R.id.textView2);

			Typeface bc = Typeface.createFromAsset(getAssets(),
					"fonts/Roboto-BoldCondensed.ttf");
			title.setTypeface(bc);
			title.setText(meta[0]);
			updated.setText(meta[1]);

		}

	}

	private class getLinesTask extends AsyncTask<Void, Void, ArrayList<String>>
	{
		private DataAdapter adptr;
		public getLinesTask(DataAdapter adapter)
		{
			adptr = adapter;
		}
		@Override
		protected ArrayList<String> doInBackground(Void...params)
		{
			return Utilities.getLines(feed);
		}
		protected void onPostExecute(ArrayList<String> stuff)
		{
			if(stuff!=null)
			{
				if(!stuff.get(0).equals(NOTFOUND) && !stuff.get(0).equals(PRIVATE))
				{
					String metaurl = "https://api.xively.com/v2/feeds/"+ feed +".json";
					new getMetaTask(listview).execute(metaurl);
					for (int i = 0; i < stuff.size(); i++)
					{
						Datastream d = new Datastream(feed, i, stuff.get(i));
						streams.add(d);
					}
					adptr.notifyDataSetChanged();
					gen();

				}
				else
				{
					TextView title = (TextView) findViewById(R.id.textView1);
					TextView updated = (TextView) findViewById(R.id.textView2);
					Typeface bc = Typeface.createFromAsset(getAssets(), "fonts/Roboto-BoldCondensed.ttf");
					title.setTypeface(bc);
					title.setText("Error");
					updated.setText("This feed does not exist or is private.");
				}
			}
		}
		

		
		
	}
}
