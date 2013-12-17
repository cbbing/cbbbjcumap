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
		//�õ��԰�������SharedPreferences
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		mCheckRotate = (CheckBoxPreference)findPreference("set_rotate");
		mCheckZoom = (CheckBoxPreference)findPreference("set_zoom");
		mPreferenceClearCache = (Preference)findPreference("set_clearcache");
		 
		mCheckRotate.setOnPreferenceChangeListener(this);
		mCheckZoom.setOnPreferenceChangeListener(this);
		mPreferenceClearCache.setOnPreferenceClickListener(this);
		
	}

	 /** 
    * ���WebView���� 
    */  
   public void clearWebViewCache(){  
         
       //����Webview�������ݿ�  
       try {  
           deleteDatabase("webview.db");   
           deleteDatabase("webviewCache.db");  
       } catch (Exception e) {  
           e.printStackTrace();  
       }  
         
       //WebView �����ļ�  
       File appCacheDir = new File(getFilesDir().getAbsolutePath()+APP_CACHE_DIRNAME);  
       Log.e(TAG, "appCacheDir path="+appCacheDir.getAbsolutePath());  
         
       File webviewCacheDir = new File(getCacheDir().getAbsolutePath()+"/webviewCache");  
       Log.e(TAG, "webviewCacheDir path="+webviewCacheDir.getAbsolutePath());  
         
       //ɾ��webview ����Ŀ¼  
       if(webviewCacheDir.exists()){  
           deleteFile(webviewCacheDir);  
       }  
       //ɾ��webview ���� ����Ŀ¼  
       if(appCacheDir.exists()){  
           deleteFile(appCacheDir);  
       }  
   }  
   
   /** 
    * �ݹ�ɾ�� �ļ�/�ļ��� 
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