package org.hld.mht;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MiscUtil {
    
    public static void log(String msg) {
    	Log.i("MHT View", msg==null?"null":msg);
    }
    
    public static void err(String msg, Throwable t) {
    	Log.e("MHT View", msg==null?"null":msg, t);
    }
    
    public static void toast(Context context, String msg) {
    	Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
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
    
    public static void deleteFile(String filePath) {
    	File file = new File(filePath);
    	if(file.isDirectory()) {
    		for(String path:file.list()) {
    			deleteFile(filePath+File.separator+path);
    		}
    		file.delete();
    	} else if(file.isFile()) {
    		file.delete();
    	}
    }
}
