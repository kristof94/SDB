package com.example.sdb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.hld.mht.MiscUtil;
import org.hld.mht.TaskManage;

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
	List<String> PATH;
	List<File> dossier;
	String state;
	
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
    			MiscUtil.toast(this, "yes");
    		} else {
    			MiscUtil.toast(this, "no");
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
				final String name = FilePath.replace(".mht", "");

				if(this.add_path(FilePath))
				{
					//isExternalStorageWritable();
					Log.d("SDB",PATH.get(PATH.size()-1));
					Button b = new Button(this);
					b.setText(PATH.get(PATH.size()-1));
					b.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							open_mht(FILEPath,name);

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
		
		
		
		String pp=p+name;
		final File folder = new File(pp);
		//folder.mkdir();
		final File file = new File(p+PATH.get(PATH.size()-1));
		String name2 = file.getName();
		
		
		Log.d("SSDB", name2);
		Log.d("SSDB", file.getAbsolutePath());
		Log.d("SSDB", p);
		Log.d("SSDB", folder.getAbsolutePath());

		if(name2.endsWith(".html")) {
			MiscUtil.showHtml(this, file.getAbsolutePath());
			Log.d("SDB",file.getAbsolutePath());
			Log.d("SDB",".html");
		} else {
			Log.d("SDB",file.getAbsolutePath());
			Log.d("SDB",".mht ou autre");
			//
			TaskManage.showMHT(this, file.getAbsolutePath());
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

}
