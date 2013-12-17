package com.bbchen.bjcumap;

import java.io.File;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingActivity extends PreferenceActivity {

	private static final String TAG = MainActivity.class.getSimpleName();  
    private static final String APP_CACHE_DIRNAME = "/webcache"; 
    
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_setting);
		addPreferencesFromResource(R.xml.setting);
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		sp.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
			
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
					String key) {
				// TODO Auto-generated method stub
				if (key.equals("set_rotate")) {
					Log.d("TAG", "rotate");
				}else if (key.equals("set_zoom")) {
					Log.d("TAG", "zoom");
				}else if (key.equals("set_clearcache")) {
					Log.d("TAG", "clearcache");
					clearWebViewCache();
				}
				
			}
		});
		
	}


	 /** 
    * Çå³ýWebView»º´æ 
    */  
   public void clearWebViewCache(){  
         
       //ÇåÀíWebview»º´æÊý¾Ý¿â  
       try {  
           deleteDatabase("webview.db");   
           deleteDatabase("webviewCache.db");  
       } catch (Exception e) {  
           e.printStackTrace();  
       }  
         
       //WebView »º´æÎÄ¼þ  
       File appCacheDir = new File(getFilesDir().getAbsolutePath()+APP_CACHE_DIRNAME);  
       Log.e(TAG, "appCacheDir path="+appCacheDir.getAbsolutePath());  
         
       File webviewCacheDir = new File(getCacheDir().getAbsolutePath()+"/webviewCache");  
       Log.e(TAG, "webviewCacheDir path="+webviewCacheDir.getAbsolutePath());  
         
       //É¾³ýwebview »º´æÄ¿Â¼  
       if(webviewCacheDir.exists()){  
           deleteFile(webviewCacheDir);  
       }  
       //É¾³ýwebview »º´æ »º´æÄ¿Â¼  
       if(appCacheDir.exists()){  
           deleteFile(appCacheDir);  
       }  
   }  
   
   /** 
    * µÝ¹éÉ¾³ý ÎÄ¼þ/ÎÄ¼þ¼Ð 
    *  
    * @param file 
    */  
   public void deleteFile(File file) {  
 
       Log.i(TAG, "delete file path=" + file.getAbsolutePath());  
         
       if (file.exists()) {  
           if (file.isFile()) {  
               file.delete();  
           } else if (file.isDirectory()) {  
               File files[] = file.listFiles();  
               for (int i = 0; i < files.length; i++) {  
                   deleteFile(files[i]);  
               }  
           }  
           file.delete();  
       } else {  
           Log.e(TAG, "delete file no exists " + file.getAbsolutePath());  
       }  
   }  
}