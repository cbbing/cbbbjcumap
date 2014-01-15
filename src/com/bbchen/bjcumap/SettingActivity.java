package com.bbchen.bjcumap;

import java.io.File;
import java.lang.reflect.Method;

import com.bbchen.util.ActivityList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;

public class SettingActivity extends PreferenceActivity 
			implements OnPreferenceChangeListener, OnPreferenceClickListener{

	private static final String TAG = MainActivity.class.getSimpleName();  
    private static final String APP_CACHE_DIRNAME = "/webcache"; 
    //private CheckBoxPreference mCheckRotate, mCheckZoom;
    //private Preference mPreferenceClearCache;
    private Preference mPreferenceReset, mPreferenceQuit;
    private SharedPreferences mSharedPrefs;
    
    private static final String ATTR_PACKAGE_STATS="PackageStats";
    
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_setting);
		addPreferencesFromResource(R.xml.setting);
		
		initViews();
		getpkginfo("com.bbchen.bjcumap");
		
		ActivityList.activityListAll.add(this);
	}

	@SuppressWarnings("deprecation")
	private void initViews(){
		//得到以包命名的SharedPreferences
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		//mCheckRotate = (CheckBoxPreference)findPreference("set_rotate");
		//mCheckZoom = (CheckBoxPreference)findPreference("set_zoom");
		//mPreferenceClearCache = (Preference)findPreference("set_clearcache");
		mPreferenceReset = (Preference)findPreference("set_default");
		mPreferenceQuit = (Preference)findPreference("set_quit");
		//mCheckRotate.setOnPreferenceChangeListener(this);
		//mCheckZoom.setOnPreferenceChangeListener(this);
		//mPreferenceClearCache.setOnPreferenceClickListener(this);
		mPreferenceReset.setOnPreferenceClickListener(this);
		mPreferenceQuit.setOnPreferenceClickListener(this);
		
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		if (preference.getKey().equals("set_clearcache")) {
			Log.d("TAG", "clearcache");
			clearWebViewCache();
		}
		else if (preference.getKey().equals("set_default")) {
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    builder.setTitle("恢复默认设置?");
		    //builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Log.d("TAG", "reset all setting");
							Editor editor = mSharedPrefs.edit();
							editor.putString("set_url", "");
							editor.putBoolean("set_rotate", true);
							editor.putBoolean("set_zoom", false);
							editor.putBoolean("set_cacheMode", true);
							editor.putBoolean("set_javascript", true);
							editor.putBoolean("set_handware", false);
							editor.commit();
							finish();
							Intent intent = new Intent(SettingActivity.this, SettingActivity.class);
							startActivity(intent);

						}
					});
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			builder.show();

		}
		else if (preference.getKey().equals("set_quit")) {
			AlertDialog.Builder mDialog = new AlertDialog.Builder(this);  
	        mDialog.setTitle(getString(R.string.tipTitle));  
	        mDialog.setMessage(getString(R.string.tipMain));  
	        mDialog.setPositiveButton(getString(R.string.ok),  
	                new DialogInterface.OnClickListener() {  
	                    public void onClick(DialogInterface dialog, int which) {  
	                    	ActivityList.killall(ActivityList.activityListAll);
	                    	System.exit(0);  
	                       
	                    }  
	                });  
	        mDialog.setNegativeButton(getString(R.string.cancel), null);  
	        mDialog.show(); 
		}
		return false;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		if (preference.getKey().equals("set_rotate")) {
			Log.d("TAG", "rotate");
		} else if (preference.getKey().equals("set_zoom")) {
			Log.d("TAG", "zoom");
		} else if (preference.getKey().equals("set_javascript")) {
			Log.d("TAG", "javascript");
		}
		
		return false;
	} 

	@Override
	//设置回退 
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK ) {
			//Intent intent = new Intent(this, MainActivity.class);
			//startActivity(intent);
			finish();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
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
       Log.i(TAG, "appCacheDir path="+appCacheDir.getAbsolutePath());  
         
       File webviewCacheDir = new File(getCacheDir().getAbsolutePath()+"/webviewCache");  
       Log.i(TAG, "webviewCacheDir path="+webviewCacheDir.getAbsolutePath());  
         
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


	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				String infoString = "";
				PackageStats newPs = msg.getData().getParcelable(
						ATTR_PACKAGE_STATS);
				if (newPs != null) {
					infoString += "应用程序大小: " + formatFileSize(newPs.codeSize);
					infoString += "\n数据大小: " + formatFileSize(newPs.dataSize);
					infoString += "\n缓存大小: " + formatFileSize(newPs.cacheSize);
				}
				//tv.setText(infoString);
				Log.e("cache", infoString);
				//Toast.makeText(this, infoString, Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};

	public void getpkginfo(String pkg) {
		PackageManager pm = getPackageManager();
		try {
			Method getPackageSizeInfo = pm.getClass().getMethod(
					"getPackageSizeInfo", String.class,
					IPackageStatsObserver.class);
			getPackageSizeInfo.invoke(pm, pkg, new PkgSizeObserver());
		} catch (Exception e) {
		}
	}

	class PkgSizeObserver extends IPackageStatsObserver.Stub {
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) {
			Message msg = mHandler.obtainMessage(1);
			Bundle data = new Bundle();
			data.putParcelable(ATTR_PACKAGE_STATS, pStats);
			msg.setData(data);
			mHandler.sendMessage(msg);

		}
	}
 

	/**
	 * 获取文件大小
	 * 
	 * @param length
	 * @return
	 */
	public static String formatFileSize(long length) {
		String result = null;
		int sub_string = 0;
		if (length >= 1073741824) {
			sub_string = String.valueOf((float) length / 1073741824).indexOf(
					".");
			result = ((float) length / 1073741824 + "000").substring(0,
					sub_string + 3) + "GB";
		} else if (length >= 1048576) {
			sub_string = String.valueOf((float) length / 1048576).indexOf(".");
			result = ((float) length / 1048576 + "000").substring(0,
					sub_string + 3) + "MB";
		} else if (length >= 1024) {
			sub_string = String.valueOf((float) length / 1024).indexOf(".");
			result = ((float) length / 1024 + "000").substring(0,
					sub_string + 3) + "KB";
		} else if (length < 1024)
			result = Long.toString(length) + "B";
		return result;
	}
}