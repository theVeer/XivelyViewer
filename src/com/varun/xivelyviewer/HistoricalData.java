package com.varun.xivelyviewer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import GraphLib.LinePoint;
import android.os.AsyncTask;
import android.widget.TextView;

public class HistoricalData
{

	/**
	 * @param args
	 */
	
	private Datastream mStream;
	private ArrayList<LinePoint> mData;
	
	public HistoricalData(Datastream d)
	{
		mStream = d;
		mData = new ArrayList<LinePoint>();
		update();
	}
	
	public void update()
	{
		mData = Utilities.parseHistData(mStream);
	}
	
}


