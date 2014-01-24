package com.varun.xivelyviewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import GraphLib.Line;
import GraphLib.LineGraph;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DataAdapter extends ArrayAdapter<Datastream>
{
	int resource;
	String response;
	int count;
	Context context;
	LineGraph gra;
	private static InputStream is = null;
	private static HashMap<String, LineGraph> graphs;
	private static HashMap<String, String> values;

	public DataAdapter(Context context, int resource,
			ArrayList<Datastream> streams)
	{
		super(context, resource, streams);
		values = new HashMap<String, String>();
		this.context = context;
		graphs = new HashMap<String, LineGraph>();
		this.resource = resource;
		count = 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		Datastream d = getItem(position);
		LinearLayout dataView;

		Typeface thin = Utilities.getTypeface(getContext(), "Roboto-Thin");
		Typeface Black = Utilities.getTypeface(getContext(), "Roboto-Black");

		if (convertView == null)
		{
			dataView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi;
			vi = (LayoutInflater) getContext().getSystemService(inflater);
			vi.inflate(resource, dataView, true);
		}
		
		else
		{
			dataView = (LinearLayout) convertView;
		}
		
		HistoricalData thData = new HistoricalData(d);
		Line thLine = new Line(thData);
		
		
		String url = "https://api.xively.com/v2/feeds/" + d.getFeed()
				+ "/datastreams/" + d.getName() + ".json";

		TextView val = (TextView) dataView.findViewById(R.id.Value);
		TextView name = (TextView) dataView.findViewById(R.id.StreamName);

		gra = (LineGraph) dataView.findViewById(R.id.linegraph);
		gra.removeAllLines();
		gra.addLine(thLine);
		
		name.setTypeface(thin);
		val.setTypeface(Black);
		
		if (values.containsKey(url))
			val.setText(values.get(url));
		
		new JSONTask(val).execute(url);


		name.setText(d.getName());

		return dataView;
	}
	
	public void clear()
	{
		super.clear();
		graphs.clear();
		values.clear();
	}

	private class JSONTask extends AsyncTask<String, Void, String>
	{
		private final WeakReference<TextView> textViewReference;
		private String url;

		public JSONTask(TextView t)
		{
			textViewReference = new WeakReference<TextView>(t);

		}

		@Override
		protected String doInBackground(String... params)
		{
			url = params[0];
			return (Utilities.parseJSON(params[0]));
		}

		protected void onPostExecute(String value)
		{
			if (isCancelled())
			{
				value = null;
			}

			if (textViewReference != null)
			{
				TextView val = textViewReference.get();
				if (val != null)
				{
					values.put(url, value);
					val.setText(value);
				}
			}
		}
	}

}
