package org.hld.mht;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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
				webView.getSettings().setLoadWithOverviewMode(true);


				webView.setWebViewClient(new WebViewClient(){

					@Override
					public void onPageFinished(WebView view, String url){
						super.onPageFinished(view, url);

					}

					@Override
					public void onPageStarted(WebView view, String url,
							Bitmap favicon) {
						webView.setInitialScale(1);
						super.onPageStarted(view, url, favicon);
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
			try {
				take_snap(takeScreenshot());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finish();
			//return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	public Bitmap takeScreenshot() {
		webView.setDrawingCacheEnabled(true);
		webView.buildDrawingCache(true);
		Bitmap bitmap = Bitmap.createBitmap(webView.getDrawingCache(), 0, 0, webView.getWidth()/3,  webView.getWidth()/3);
		webView.setDrawingCacheEnabled(false);
		return bitmap;
	}

	public void take_snap(Bitmap mBitmap) throws IOException
	{
		String F = path.replace("index.html", "pict.jpeg");
		Log.d("SSDB","2 "+F );
		File f = new File(F);
		if (!f.exists())	
		{
			if(mBitmap!= null) 
			{
				try {
					FileOutputStream fos = new FileOutputStream(f);
					mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
					fos.flush();
					fos.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					FileOutputStream outputStream = openFileOutput("PATH_PICT", this.MODE_PRIVATE);
					String chemin = F+System.getProperty("line.separator");
					outputStream.write(chemin.getBytes());
					outputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private void showMessage(String msg) {
		webView.getSettings().setDefaultTextEncodingName("UTF-8");
		webView.loadData(Uri.encode(msg, "UTF-8"), "text/plain", "UTF-8");
	}
}
