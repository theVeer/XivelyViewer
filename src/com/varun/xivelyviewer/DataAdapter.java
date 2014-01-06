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
	ImageView gra;
	private static InputStream is = null;
	private static HashMap<String, Bitmap> bitmaps;
	private static HashMap<String, String> values;

	public DataAdapter(Context context, int resource,
			ArrayList<Datastream> streams)
	{
		super(context, resource, streams);
		values = new HashMap<String, String>();
		this.context = context;
		bitmaps = new HashMap<String, Bitmap>();
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
		String gurl = "https://api.xively.com/v2/feeds/" + d.getFeed()
				+ "/datastreams/" + d.getName() + ".png?duration=" + d.getDur()
				+ "&g=true&b=true&w=750";

		String url = "https://api.xively.com/v2/feeds/" + d.getFeed()
				+ "/datastreams/" + d.getName() + ".json";

		TextView val = (TextView) dataView.findViewById(R.id.Value);
		TextView name = (TextView) dataView.findViewById(R.id.StreamName);

		gra = (ImageView) dataView.findViewById(R.id.graph);

		name.setTypeface(thin);
		val.setTypeface(Black);
		
		if (values.containsKey(url))
			val.setText(values.get(url));
		
		new JSONTask(val).execute(url);

		download(gurl, gra);

		name.setText(d.getName());

		return dataView;
	}
	
	public void clear()
	{
		super.clear();
		bitmaps.clear();
		values.clear();
	}
	

	private Bitmap parseGraph(String gurl)
	{
		{
			try
			{
				return BitmapFactory.decodeStream((InputStream) new URL(gurl)
						.getContent());
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}

		}
		return null;
	}

	public class dlImageTask extends AsyncTask<String, Void, Bitmap>
	{
		private String url;
		private final WeakReference<ImageView> imageViewReference;

		public dlImageTask(ImageView iv)
		{
			imageViewReference = new WeakReference<ImageView>(iv);
		}

		@Override
		protected Bitmap doInBackground(String... params)
		{
			url = params[0];
			Log.i("IMAGETASK", "Downloading Image");
			return parseGraph(params[0]);
		}

		protected void onPostExecute(Bitmap bitmap)
		{
			if (isCancelled())
			{
				bitmap = null;
			}

			if (imageViewReference != null)
			{
				ImageView imageView = imageViewReference.get();
				Log.i("POSTEXE", "Image Task post execute");
				dlImageTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
				// Change bitmap only if this process is still associated
				// with it

				if (this == bitmapDownloaderTask)
				{
					Log.i("POSTEXE TRUE", "Image Task post execute true");
					imageView.setImageBitmap(bitmap);
					bitmaps.put(url, bitmap);
					Log.i("HASHMAP", "Hashmap modified." + url);
				}

			}
		}
	}

	public void download(String url, ImageView imageView)
	{
		if (cancelPotentialDownload(url, imageView))
		{
			dlImageTask task = new dlImageTask(imageView);
			DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
			if (bitmaps.containsKey(url))
				downloadedDrawable = new DownloadedDrawable(task,
						bitmaps.get(url), context);
			imageView.setImageDrawable(downloadedDrawable);
			task.execute(url);
		}
	}

	private boolean cancelPotentialDownload(String url, ImageView imageView)
	{
		dlImageTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

		if (bitmapDownloaderTask != null)
		{
			String bitmapUrl = bitmapDownloaderTask.url;
			if ((bitmapUrl == null) || (!bitmapUrl.equals(url)))
			{
				bitmapDownloaderTask.cancel(true);
			}
			else
			{
				// The same URL is already being downloaded.
				return false;
			}
		}
		return true;
	}

	private static dlImageTask getBitmapDownloaderTask(ImageView imageView)
	{
		if (imageView != null)
		{
			Log.i("GETTASK", "IMAGEVIEW NOT NULL");
			Drawable drawable = imageView.getDrawable();
			if (drawable instanceof DownloadedDrawable)
			{
				DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
				Log.i("GETTASK", "RETURNED A TASK");
				return downloadedDrawable.getBitmapDownloaderTask();

			}
		}
		Log.i("GETTASK", "RETURNED NULL");
		return null;
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
