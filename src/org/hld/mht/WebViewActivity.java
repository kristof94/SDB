package org.hld.mht;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.example.sdb.R;

public class WebViewActivity extends Activity {
	WebView webView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.web);
		webView = (WebView)findViewById(R.id.WebView);
		webView.getSettings().setBuiltInZoomControls(true);
		//webView.getSettings().setLoadsImagesAutomatically(true);
		

		Intent intent = getIntent();
		if(intent==null) {
			showMessage("test");
		} else {
			String path = intent.getExtras().getString("path");
			if(path==null) {
				showMessage("test2");
			} else if(!new File(path).isFile()) {
				showMessage(path+"else if");
			} else {
				webView.getSettings().setAllowFileAccessFromFileURLs(true);
				webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
				webView.getSettings().setJavaScriptEnabled(true);
				webView.getSettings().setBuiltInZoomControls(true);
				webView.getSettings().setSupportZoom(true);
				webView.getSettings().setUseWideViewPort(true);
				webView.setWebChromeClient(new WebChromeClient() {
					@Override
					public void onProgressChanged(WebView view, int newProgress) {
						WebViewActivity.this.setProgress(newProgress * 100);
					}
					@Override
					public void onReceivedTitle(WebView view, String title) {
						WebViewActivity.this.setTitle(title);
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
			android.os.Process.killProcess(android.os.Process.myPid());
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
	}
	
	private void showMessage(String msg) {
		webView.getSettings().setDefaultTextEncodingName("UTF-8");
		webView.loadData(Uri.encode(msg, "UTF-8"), "text/plain", "UTF-8");
	}
}
