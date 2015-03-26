package org.hld.mht;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.sdb.R;

public class WebViewActivity extends Activity {
	WebView webView ;
	String path,folder;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.web);
		webView = (WebView)findViewById(R.id.WebView);

		//


		Intent intent = getIntent();
		if(intent==null) {
			showMessage("test");
		} else {
			path = intent.getExtras().getString("path");
			folder = intent.getExtras().getString("folder");
			Log.d("SSDB","path "+path + " folder "+folder);

			if(path==null) {
				showMessage("test2");
			} else if(!new File(path).isFile()) {
				showMessage(path+"else if");
			} else {
				webView.getSettings().setAllowFileAccessFromFileURLs(true);
				webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
				webView.getSettings().setJavaScriptEnabled(true);
				webView.getSettings().setLoadsImagesAutomatically(true);
				webView.setDrawingCacheEnabled(true);
				//active le zoom
				webView.getSettings().setBuiltInZoomControls(true);
				webView.getSettings().setDisplayZoomControls(false);
				webView.getSettings().setUseWideViewPort(true);
				webView.setInitialScale(1);
				webView.getSettings().setLoadWithOverviewMode(true);


				webView.setWebViewClient(new WebViewClient(){

					@Override
					public void onPageFinished(WebView view, String url) {
						try {
							take_snap();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						super.onPageFinished(view, url);
					}

				});
				webView.loadUrl("file://"+Uri.encode(path, "/\\"));
				Log.d("SSDB","file://"+Uri.encode(path, "/\\") );
			}
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			/*new Runnable() {
				public void run() {
					try {
						take_snap(loadBitmapFromView(
								webView,
								webView.getWidth(),
								webView.getHeight()));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.run();*/
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public static Bitmap loadBitmapFromView(View v, int width, int height) {
		Bitmap b = Bitmap.createBitmap(width , height, Bitmap.Config.ARGB_8888);                
		Canvas c = new Canvas(b);
		v.layout(0, 0, v.getLayoutParams().width/3, v.getLayoutParams().height/4);
		v.draw(c);
		return b;
	}

	public void take_snap() throws IOException
	{
		String F ="/sdcard/Download/pict.png";//path.replace("index.html", "pict.png");

		Log.d("SSDB","1 "+path );
		Log.d("SSDB","2 "+F );

		Bitmap mBitmap          = null;
		ByteArrayOutputStream mByteArrayOpStream        = null;

		//get the picture from the webview
		Picture picture = webView.capturePicture();

		//Create the new Canvas


		mBitmap = Bitmap.createBitmap(webView.getWidth(), webView.getHeight
				(),Config.ARGB_8888);
		//mCanvas.drawBitmap(mBitmap, 0, 0, null);

		if(mBitmap!= null) {
			mByteArrayOpStream = new ByteArrayOutputStream();
			mBitmap.compress(Bitmap.CompressFormat.PNG, 90, mByteArrayOpStream);
			try {
				FileOutputStream fos = new FileOutputStream(F);
				fos.write(mByteArrayOpStream.toByteArray());
				fos.close();
				save_file(F);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}





		// return bmp;  

		/*Bitmap bmp
		File f = new File("/sdcard/Download/"+"pict.png");
		f.createNewFile();
		FileOutputStream fOut;
		try {
			fOut = new FileOutputStream(folder+"/"+"pict.png");
			bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut);		    
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	public void save_file(String F){
		String str = F;

		try {
			FileOutputStream outputStream = openFileOutput("PATH_PICT", this.MODE_PRIVATE);

			outputStream.write(str.getBytes());

			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d("SSDB","snap "+str );
	}


	private void showMessage(String msg) {
		webView.getSettings().setDefaultTextEncodingName("UTF-8");
		webView.loadData(Uri.encode(msg, "UTF-8"), "text/plain", "UTF-8");
	}
}
