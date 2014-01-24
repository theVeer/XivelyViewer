package com.varun.xivelyviewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import GraphLib.LinePoint;
import android.content.Context;
import android.graphics.Typeface;

public class Utilities
{
	private static final String URLSTUB = "api.xively.com/v2/feeds/";
	
	public Utilities()
	{
		
	}
	
	public static String[] getMeta(String url)
	{
		String[] metadata = new String[2];
		String JSONString = null;
		JSONObject JSONObject = null;
		try
		{

			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			httpGet.addHeader("Authorization",
					"Basic dmFydW5tOmdyZWVuODM2Ng==");
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream isJ = httpEntity.getContent();

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(isJ, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
			}
			JSONString = sb.toString();

			JSONObject = new JSONObject(JSONString);

		} catch (IOException ex)
		{
			ex.printStackTrace();

		} catch (JSONException x)
		{
			x.printStackTrace();

		}

		try
		{
			metadata[0] = (JSONObject.getString("title"));
			metadata[1] = (JSONObject.getString("updated"));

		} catch (JSONException e)
		{
			e.printStackTrace();
		}

		return metadata;
	}
	
	public static ArrayList<String> getLines(String feed)
	{
		ArrayList<String> names = new ArrayList<String>();
		InputStream is = null;
		try
		{
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet("https://api.xively.com/v2/feeds/"
					+ feed + ".csv");
			httpGet.addHeader("Authorization", "Basic dmFydW5tOmdyZWVuODM2Ng==");
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		} catch (ClientProtocolException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		try
		{
			String line;
			while ((line = reader.readLine()) != null)
			{
				String[] RowData = line.split(",");
				names.add(RowData[0]);
			}
		} catch (IOException ex)
		{
			// handle exception
		} finally
		{
			try
			{
				is.close();
			} catch (IOException e)
			{
				// handle exception
			}
		}
		return names;
	}
	public static ArrayList<LinePoint> parseHistData(Datastream d)
	{
		ArrayList<LinePoint> points = new ArrayList<LinePoint>();
		String url = URLSTUB+d.getFeed()+".csv?duration=6hours&interval=0";
		
		InputStream is = null;
		try
		{
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			httpGet.addHeader("Authorization", "Basic dmFydW5tOmdyZWVuODM2Ng==");
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		} catch (ClientProtocolException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		try
		{
			String line;
			int count = 0;
			while ((line = reader.readLine()) != null)
			{
				String[] RowData = line.split(",");
				if(RowData[0].equals(d.getName()))
				{
					String val = RowData[2];
					LinePoint p = new LinePoint();
					p.setX(count);
					p.setY(Double.parseDouble(val));
					
					points.add(p);
					count++;
				}
			}
		} catch (IOException ex)
		{
			// handle exception
		} finally
		{
			try
			{
				is.close();
			} catch (IOException e)
			{
				// handle exception
			}
		}
		
		return points;
	}
	public static String parseJSON(String url)
	{
		String result = "";
		String unit = "";
		String JSONString = null;
		JSONObject JSONObject = null;
		try
		{

			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			httpGet.addHeader("Authorization", "Basic dmFydW5tOmdyZWVuODM2Ng==");
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
			}
			JSONString = sb.toString();

			JSONObject = new JSONObject(JSONString);

		} catch (IOException ex)
		{
			ex.printStackTrace();

		} catch (JSONException x)
		{
			x.printStackTrace();

		}

		try
		{

			result = (JSONObject.getString("current_value"));
			unit = (JSONObject.getJSONObject("unit").getString("symbol"));
		} catch (JSONException e)
		{
			e.printStackTrace();
		}

		return result + " " + unit;

	}
	
	public static Typeface getTypeface(Context context, String name)
	{
		return Typeface.createFromAsset(context.getAssets(), "fonts/"+name+".ttf");
	}
}
