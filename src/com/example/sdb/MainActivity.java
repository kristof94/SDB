package com.example.sdb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.hld.mht.WebViewActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

	private static final int CHOOSE_FILE_REQUESTCODE = 1;
	LinearLayout layout;
	List<String> PATH,NAME;
	String state;
	static File Index;
	private boolean isShow;
	Intent i ;
	static String FOLDER;

	File sauvegarde;
	FileInputStream fin ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		layout = (LinearLayout) findViewById(R.id.ll);
		i = new Intent(this,Loading.class);
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)) {
			Log.d("SDB", "yes");
		} else {
			Log.d("SDB", "no");
		}
		PATH = new ArrayList<String>();
		NAME = new ArrayList<String>();
		FOLDER = this.getFilesDir().getAbsolutePath();
		String line = null,l2[];


		try {
			fin = openFileInput("PATH");
			BufferedReader br = new BufferedReader(new InputStreamReader(fin));
			fin = openFileInput("PATH");
			while ((line = br.readLine()) != null) {
				l2 = line.split("-");
				PATH.add(l2[0]);
				NAME.add(l2[1]);
				System.out.println(l2[0]);
				System.out.println(l2[1]);
				this.add_button(l2[0], l2[1]);
			}
			fin.close();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	private void add_button(String PAT,final String name)
	{
		final String P= PAT;
		Button b = new Button(this);

		if(!PATH.contains(PAT))
			PATH.add(PAT);
		if(!NAME.contains(name))			
			NAME.add(name);

		b.setText(name.replace(".mht",""));
		b.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				open_mht(P,name);
			}

		});

		layout.addView(b);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case CHOOSE_FILE_REQUESTCODE :
			if(resultCode==RESULT_OK){
				final String name = data.getData().getLastPathSegment();
				final String FILEPath = data.getData().getPath().replace(name, "");

				open_mht(FILEPath,name);

			}
			break;

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void open_mht(String p,String name) {
		Log.d("SDB",p);
		Log.d("SDB",name);
		File folder = new File(FOLDER+"/"+name.replace(".mht", ""));
		File Index_HTML = new File(folder.getAbsolutePath()+"/"+"index.html");

		if(folder.exists())
		{
			Log.d("SDB","Folder "+folder.getAbsolutePath()+" already created");

			if(Index_HTML.exists())
			{
				Log.d("SDB","Index.HTML "+ Index_HTML.getAbsolutePath()+" already unziped");
				MainActivity.showHtml(this, Index_HTML.getAbsolutePath());
			}
			else
			{
				//Extract_MHT o =new Extract_MHT(new File(p+name),folder.getAbsolutePath()+"/",this);
				this.add_button(folder.getAbsolutePath()+"/",name);
				i.putExtra("file",p+name );
				i.putExtra("folder",folder.getAbsolutePath()+"/");
				startActivity(i);

				//o.execute();
			}
		}
		else
		{
			Log.d("SDB","Folder "+p+" not created.");
			folder.mkdir();
			Log.d("SDB","Folder "+p+" created");
			//Extract_MHT o =new Extract_MHT(new File(p+name),folder.getAbsolutePath()+"/",this);

			this.add_button(folder.getAbsolutePath()+"/",name);
			i.putExtra("file",p+name );
			i.putExtra("folder",folder.getAbsolutePath()+"/");
			//this.add_button(folder.getAbsolutePath()+"/",name);
			startActivity(i);
			//			o.execute();
		}

	}

	public void OpenFileExplorer(View v)
	{
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("file/mht");
		startActivityForResult(intent, CHOOSE_FILE_REQUESTCODE); 
	}

	public boolean isExternalStorageWritable() {
		state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {

			return true;
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		isShow = true;

		Log.d("SDB", "onResume");

	}

	@Override
	protected void onStop() {

		String chemin;

		try {
			FileOutputStream outputStream = openFileOutput("PATH", this.MODE_PRIVATE);
			for(int i =0;i<PATH.size();i++)
			{
				chemin = PATH.get(i)+"-"+NAME.get(i)+System.getProperty("line.separator");
				outputStream.write(chemin.getBytes());
			}
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


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
		intent.putExtra("folder",FOLDER );

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return intent;
	}

	public static void showHtml(Activity activity, String filePath) {
		activity.startActivity(createShowHtmlIntent(activity, filePath));
	}


}
