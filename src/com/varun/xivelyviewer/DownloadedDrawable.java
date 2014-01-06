package com.varun.xivelyviewer;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.varun.xivelyviewer.DataAdapter.dlImageTask;

public class DownloadedDrawable extends BitmapDrawable
{
	private final WeakReference<dlImageTask> bitmapDownloaderTaskReference;
	private final WeakReference<Bitmap> bmref;
	@SuppressWarnings("deprecation")
	public DownloadedDrawable(dlImageTask bitmapDownloaderTask)
	{
		bitmapDownloaderTaskReference = new WeakReference<dlImageTask>(bitmapDownloaderTask);
		bmref= null;
	}
	
	public DownloadedDrawable(dlImageTask bitmapDownloaderTask, Bitmap bm, Context context)
	{
		super(context.getResources(), bm);
		bitmapDownloaderTaskReference = new WeakReference<dlImageTask>(bitmapDownloaderTask);
		bmref = new WeakReference<Bitmap>(bm);
	}
	

	public dlImageTask getBitmapDownloaderTask()
	{
		return bitmapDownloaderTaskReference.get();
	}
}
