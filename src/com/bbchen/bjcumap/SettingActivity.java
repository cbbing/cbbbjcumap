package com.bbchen.bjcumap;

import java.io.File;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingActivity extends PreferenceActivity 
			implements OnPreferenceChangeListener, OnPreferenceClickListener{

	private static final String TAG = MainActivity.class.getSimpleName();  
    private static final String APP_CACHE_DIRNAME = "/webcache"; 
    private CheckBoxPreference mCheckRotate, mCheckZoom;
    private Preference mPreferenceClearCache;
    private SharedPreferences mSharedPrefs;
    
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_setting);
		addPreferencesFromResource(R.xml.setting);
		
		initViews();
	}

	private void initViews(){
		//得到以包命名的SharedPreferences
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		mCheckRotate = (CheckBoxPreference)findPreference("set_rotate");
		mCheckZoom = (CheckBoxPreference)findPreference("set_zoom");
		mPreferenceClearCache = (Preference)findPreference("set_clearcache");
		 
		mCheckRotate.setOnPreferenceChangeListener(this);
		mCheckZoom.setOnPreferenceChangeListener(this);
		mPreferenceClearCache.setOnPreferenceClickListener(this);
		
	}

	 /** 
    * 清除WebView缓存 
    */  
   public void clearWebViewCache(){  
         
       //清理Webview缓存数据库  
       try {  
           deleteDatabase("webview.db");   
           deleteDatabase("webviewCache.db");  
       } catch (Exception e) {  
           e.printStackTrace();  
       }  
         
       //WebView 缓存文件  
       File appCacheDir = new File(getFilesDir().getAbsolutePath()+APP_CACHE_DIRNAME);  
       Log.e(TAG, "appCacheDir path="+appCacheDir.getAbsolutePath());  
         
       File webviewCacheDir = new File(getCacheDir().getAbsolutePath()+"/webviewCache");  
       Log.e(TAG, "webviewCacheDir path="+webviewCacheDir.getAbsolutePath());  
         
       //删除webview 缓存目录  
       if(webviewCacheDir.exists()){  
           deleteFile(webviewCacheDir);  
       }  
       //删除webview 缓存 缓存目录  
       if(appCacheDir.exists()){  
           deleteFile(appCacheDir);  
       }  
   }  
   
   /** 
    * 递归删除 文件/文件夹 
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


@Override
public boolean onPreferenceClick(Preference preference) {
	// TODO Auto-generated method stub
	if (preference.getKey().equals("set_clearcache")) {
		Log.d("TAG", "clearcache");
		clearWebViewCache();
	}
	return false;
}


@Override
public boolean onPreferenceChange(Preference preference, Object newValue) {
	// TODO Auto-generated method stub
	if (preference.getKey().equals("set_rotate")) {
		Log.d("TAG", "rotate");
	}else if (preference.getKey().equals("set_zoom")) {
		Log.d("TAG", "zoom");
	}
	return false;
}  
}