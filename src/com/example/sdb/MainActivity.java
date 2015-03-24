package com.example.sdb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.mail.MessagingException;

import org.hld.mht.WebViewActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hankenindustries.mht.Attachment;
import com.hankenindustries.mht.MHTUnpack;

public class MainActivity extends Activity {

	private static final int CHOOSE_FILE_REQUESTCODE = 1;
	LinearLayout layout;
	List<String> PATH;
	List<File> dossier;
	String state;
	static File Index;
	private boolean isShow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		layout = (LinearLayout) findViewById(R.id.ll);
		PATH = new ArrayList<String>();
		dossier = new ArrayList<File>();
		String state = Environment.getExternalStorageState();
		if(!Environment.MEDIA_MOUNTED.equals(state)) {
			if(Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
				Log.d("SDB", "yes");
			} else {
				Log.d("SDB", "no");
			}

		} else {
			;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case CHOOSE_FILE_REQUESTCODE :
			if(resultCode==RESULT_OK){
				final String FilePath = data.getData().getLastPathSegment();
				final String FILEPath = data.getData().getPath();
				final String name = FilePath;

				if(this.add_path(FilePath))
				{
					Button b = new Button(this);
					b.setText(name.replace(".mht",""));
					b.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							open_mht(FILEPath.replace(FilePath, ""),name);
						}

					});

					layout.addView(b);
				}
				open_mht(FILEPath.replace(FilePath, ""),name);

			}
			break;

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void open_mht(String p,String name) {

		p=p.replace(".mht", "");
		String pp=(p+name+"/").replace(".mht", "");
		Log.d("SDB",pp);
		Log.d("SDB",p);
		Log.d("SDB",name);
		File folder = new File(pp);
		File Index_HTML = new File(pp+"index.html");

		if(folder.exists())
		{
			Log.d("SDB","Folder "+pp+" already created");

			if(Index_HTML.exists())
			{
				Log.d("SDB","Index.HTML "+ Index_HTML.getAbsolutePath()+" already created");
				MainActivity.showHtml(this, Index_HTML.getAbsolutePath());
			}
		}
		else
		{
			Log.d("SDB","Folder "+pp+" not created.");
			folder.mkdir();
			Log.d("SDB","Folder "+pp+" created");
			Extract_MHT o =new Extract_MHT(new File(p+name), pp,this);
			o.execute();
			

		}
	}

	public void OpenFileExplorer(View v)
	{
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("file/*");
		startActivityForResult(intent, CHOOSE_FILE_REQUESTCODE); 
	}

	public boolean isExternalStorageWritable() {
		state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {

			return true;
		}
		return false;
	}

	public boolean add_path(String p)
	{
		if(PATH.isEmpty())
		{
			PATH.add(p);
			return true;
		}
		else
		{
			if(PATH.contains(p))
				return false;
			else{
				PATH.add(p);
				return true;
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		isShow = true;
		Log.d("SDB", "onResume");

	}

	@Override
	protected void onStop() {
		super.onStop();
		isShow = false;
		Log.d("SDB", "onStop");

	}

	public boolean isShow() {
		return isShow;
	}

	@Override
	protected void onDestroy() {
		Log.d("SDB", "onDestroy");
		super.onDestroy();
	}

	public static Intent createShowHtmlIntent(Activity activity, String filePath) {
		Intent intent = new Intent();
		intent.setClass(activity, WebViewActivity.class);
		intent.putExtra("path", filePath);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return intent;
	}

	public static void showHtml(Activity activity, String filePath) {
		activity.startActivity(createShowHtmlIntent(activity, filePath));
	}

	private class Extract_MHT extends AsyncTask<Void, Void, String>{

		Collection<Attachment> at;
		File MHT;
		String path;
		Activity aa;
		int duration;
		ProgressBar b;
		
		public Extract_MHT(File mht,String Path,Activity a) {

			aa=a;
			MHT = mht;
			path = Path;
			this.at = null;
			Log.d("SDB","CONSTRUCTOR "+MHT.getAbsolutePath());
			Log.d("SDB","CONSTRUCTOR "+path);

		}

		@Override
		protected void onPreExecute() {

			setContentView(R.layout.loading);
			b =(ProgressBar) findViewById(R.id.progress_bar);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			MainActivity.showHtml(aa, Index.getAbsolutePath());
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
						System.out.println("Terminé");
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
