package com.example.sdb;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

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
import android.widget.Button;
import android.widget.LinearLayout;

import com.hankenindustries.mht.Attachment;
import com.hankenindustries.mht.MHTUnpack;

public class MainActivity extends Activity {

	private static final int CHOOSE_FILE_REQUESTCODE = 1;
	LinearLayout layout;
	//List<String> PATH,NAME;
	String state;
	static File Index;
	private boolean isShow;
	//File file;
	//	FileOutputStream outputStream;
	//FileInputStream fin ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//PATH = new ArrayList<String>();
		//NAME = new ArrayList<String>();

		/*file = new File(this.getFilesDir(), "PATH");
		try {
			fin = openFileInput("PATH");
			BufferedReader br = new BufferedReader(new InputStreamReader(fin));
			String line = null;
			List<String> spl= new ArrayList<String>();
			int y =0;
			while ((line = br.readLine()) != null) {
				spl.add(line);
				if(y!=0)
					{
						this.add_button(spl.get(y-1), spl.get(y));
						//System.out.println(PATH.get(y-1));
					}
				y++;

			}




			fin.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 */
		setContentView(R.layout.activity_main);
		layout = (LinearLayout) findViewById(R.id.ll);
		String state = Environment.getExternalStorageState();
		//if(!Environment.MEDIA_MOUNTED.equals(state)) {
		if(Environment.MEDIA_MOUNTED.equals(state)) {
			Log.d("SDB", "yes");
		} else {
			Log.d("SDB", "no");
		}

		//} else {
		//;
		//}
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
		File folder = new File(this.getFilesDir().getAbsolutePath()+"/"+name.replace(".mht", ""));
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
				Extract_MHT o =new Extract_MHT(new File(p+name),folder.getAbsolutePath()+"/",this);
				this.add_button(folder.getAbsolutePath()+"/",name);
				o.execute();
			}
		}
		else
		{
			Log.d("SDB","Folder "+p+" not created.");
			folder.mkdir();
			Log.d("SDB","Folder "+p+" created");
			Extract_MHT o =new Extract_MHT(new File(p+name),folder.getAbsolutePath()+"/",this);
			this.add_button(folder.getAbsolutePath()+"/",name);
			o.execute();
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

		/*if(!PATH.isEmpty())
			{
				try {
					outputStream = openFileOutput("PATH", this.MODE_PRIVATE);
					String data;
					for(int i = 0;i<PATH.size();i++)
					{
						data = PATH.get(i)+System.getProperty("line-separator")+NAME.get(i)+System.getProperty("line-separator");
						Log.d("SDB", data);
						outputStream.write(data.getBytes());

					}
					Log.d("SDB", file.getAbsolutePath());
					outputStream.close();

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}*/


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

	public class Extract_MHT extends AsyncTask<Void, Void, String>{

		Collection<Attachment> at;
		File MHT;
		String path;
		Activity aa;

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
			Log.d("SDB","set loading");

		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			setContentView(R.layout.activity_main);
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
