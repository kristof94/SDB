package org.hld.mht;

import com.example.sdb.MainActivity;
import com.example.sdb.R;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class TaskManage {
	
	public static void showMHT(Activity activity, String mhtPath) {
		Log.d("SDB","showMHT");

		new ShowMHTAsyncTask(activity, mhtPath).execute();
	}

	private static class ShowMHTAsyncTask extends AsyncTask<Void, Void, String> {
		private Activity activity;
		private String mhtPath;
		
		public ShowMHTAsyncTask(Activity activity, String mhtPath) {
			super();
			Log.d("SDB","ShowMHTAsyncTask");

			this.activity = activity;
			this.mhtPath = mhtPath;
		}

		@Override
		protected void onPreExecute() {
			Log.d("SDB","onPreExecute");

			MiscUtil.toast(activity, "onPreExecute()");
		}
		
		@Override
		protected String doInBackground(Void... params) {
			Log.d("SDB","doInBackground");

			try {
				String path = null;
				if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
					path = MHTUtil.exportHtml(mhtPath, PreferencesManage.getSdcardCachePath());
				} else {
					path = MHTUtil.exportHtml(mhtPath, PreferencesManage.getLocalCachePath(activity));
				}
				MiscUtil.log("MHT save to "+path);
				//MiscUtil.toast(activity, "MHT save to ");

				return path;
			} catch(Exception e) {
				MiscUtil.err("save mht error", e);
				Log.d("SDB", "error "+e.getMessage());
				//MiscUtil.toast(activity, "save mht error");
				
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			Log.d("SDB", "onPostExecut " +  result);

			if(result!=null && activity instanceof MainActivity) {
				if(((MainActivity)activity).isShow()) {
					MiscUtil.showHtml(activity, result);
				} else {
					NotificationManager notificationManager = (NotificationManager)activity.getSystemService(Context.NOTIFICATION_SERVICE);
					PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, MiscUtil.createShowHtmlIntent(activity, result), PendingIntent.FLAG_CANCEL_CURRENT);
					Notification notification = new Notification(R.drawable.icon_html, "On post test", System.currentTimeMillis());
					notification.flags |= Notification.FLAG_AUTO_CANCEL;
					notification.setLatestEventInfo(activity, "MHTè½¬æ�¢å®Œæ¯•", mhtPath.subSequence(mhtPath.lastIndexOf('/')+1, mhtPath.length()), pendingIntent);
					notificationManager.cancel(R.string.app_name);
					notificationManager.notify(R.string.app_name, notification);
				}
			}
		}
	}
}
