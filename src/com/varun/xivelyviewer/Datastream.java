package com.varun.xivelyviewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class Datastream
{
	private String value;
	private String streamname;
	private String feedId;
	private int pos;
	private String gDur = "6hours";
	
	public Datastream(String id, int p, String name)
	{
		feedId = id;
		pos = p;
		streamname = name;
		
	}

	public String getValue()
	{
		return value;
	}

	public int getPos()
	{
		return pos;
	}
	public String getName()
	{
		return streamname;
	}
	public void setName(String n)
	{
		streamname = n;
	}
	public String getFeed()
	{
		return feedId;
	}
	public String getDur()
	{
		return gDur;
	}
	public void setDur(String newDur)
	{
		gDur = newDur;
	}
	
	
	
}
