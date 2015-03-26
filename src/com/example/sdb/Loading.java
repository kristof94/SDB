package com.example.sdb;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.mail.MessagingException;

import org.hld.mht.WebViewActivity;

import com.hankenindustries.mht.Attachment;
import com.hankenindustries.mht.MHTUnpack;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class Loading extends Activity {




	@Override
	protected void onResume() {
		setContentView(R.layout.loading);
		super.onResume();
	}


public static Intent createShowHtmlIntent(Activity activity, String filePath) {
		Intent intent = new Intent();
		intent.setClass(activity, WebViewActivity.class);
		intent.putExtra("path", filePath);
		intent.putExtra("folder",path );

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return intent;
	}

	public static void showHtml(Activity activity, String filePath) {
		activity.startActivity(createShowHtmlIntent(activity, filePath));
	}

	File Index;	
	static String path;
	File MHT;
	Activity aa;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			path = extras.getString("folder");
			Index = new File(extras.getString("path")+"index.html");
			MHT = new File(extras.getString("file"));
			aa=this;
		}
	}

	@Override
	protected void onStart() {
		Extract_MHT mht = new Extract_MHT();
		mht.execute();
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	private class Extract_MHT extends AsyncTask<Void, Void, String>{

		Collection<Attachment> at;

		@Override
		protected void onPreExecute() {
			setContentView(R.layout.loading);
			Log.d("SDB","set loading");

		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			setContentView(R.layout.activity_main);
			showHtml(aa, Index.getAbsolutePath());
			finish();
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				at = MHTUnpack.unpack(MHT);
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String name="";
			Log.d("SDB",at.size()+" Fichiers extraits.");
			for(Attachment a : at)
			{
				try {
					name = a.getFileName();
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(name != null)
				{
					File file = new File(path+name);
					File parent = file.getParentFile();
					if(!parent.exists() && !parent.mkdirs())
						System.err.println("Couldn't create dir: " + parent);
					try {
						a.saveFile(file);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MessagingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					Index = new File(path+"index.html");
					File parent = Index.getParentFile();
					if(!parent.exists() && !parent.mkdirs())
						System.err.println("Couldn't create dir: " + parent);
					try {
						a.saveFile(Index);
						System.out.println(Index.getAbsolutePath());
						System.out.println("Termin�");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MessagingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return null;
		}	
	}
}
