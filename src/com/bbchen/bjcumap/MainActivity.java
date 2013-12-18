package com.bbchen.bjcumap;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();  
    private static final String APP_CACHE_DIRNAME = "/webcache"; 
	WebView webView;
	private long firstime = 0;
	String defaultURL = "http://218.246.23.89/bjcumap/index_mobile.jsp";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		webView = (WebView)this.findViewById(R.id.webview);
		
		initWebView();
		
		 //加载需要显示的网页 
		webView.loadUrl(defaultURL);
		//webView.loadUrl("http://www.baidu.com");
		//点击链接继续在当前browser中响应，而不是新开Android的系统browser中响应该链接，必须覆盖 webview的WebViewClient对象
		webView.setWebViewClient(new CbbWebViewClient());
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SharedPreferences prefs =PreferenceManager.getDefaultSharedPreferences(this) ;
     
		//修改主页
        String userUrl = prefs.getString("set_url","");//修改后的url
        String userUrlPre = prefs.getString("set_url_pre",defaultURL);//修改之前的url
        if (!userUrl.contains("http://")) {
        	userUrl = "http://" + userUrl;
		} 
        if (!userUrlPre.contains("http://")) {
        	userUrlPre = "http://" + userUrlPre;
		} 
        if (!userUrl.equals(userUrlPre)) {
            if (checkURL(userUrl))
            {
            	webView.loadUrl(userUrl);
            }
            else {
            	prefs.edit().putString("set_url", defaultURL);
            	webView.loadUrl(defaultURL);
    		}	
            prefs.edit().putString("set_url_pre", userUrl);
            prefs.edit().commit();
		}
        
        
        //设置旋转
     	Boolean bAutoRotate = prefs.getBoolean("set_rotate",false);//false表示没有查到checkbox这个key的返回值
     	changeOrientation(bAutoRotate);
     	
     	//设置缩放
     	Boolean bZoom = prefs.getBoolean("set_zoom", false);
     	webView.getSettings().setBuiltInZoomControls(bZoom);	
     	webView.getSettings().setSupportZoom(true);
     	webView.getSettings().setUseWideViewPort(bZoom);
     	
     	//设置缓存模式
     	Boolean bCacheMode = prefs.getBoolean("set_cacheMode",false);
     	if (bCacheMode) {
     		webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
     	}
     	else {
     		webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
     	}
     	
	}
	private void initWebView() {
		//设置WebView属性，能够执行Javascript脚本 
		WebSettings settings = webView.getSettings();
		settings.setPluginState(PluginState.ON);
		settings.setJavaScriptEnabled(true);
		//settings.setRenderPriority(RenderPriority.HIGH);//提高渲染的优先级,提高加载速度
		//settings.setBlockNetworkImage(true);//把图片加载放在最后来加载渲染
		//设置 缓存模式
		//if (isNetworkConnected(this)) {
		//	settings.setCacheMode(WebSettings.LOAD_DEFAULT);
		//}
		//else {
		//	settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		//}
		
		
		// 开启 DOM storage API 功能  
		settings.setDomStorageEnabled(true);
		//开启 database storage API 功能  
		settings.setDatabaseEnabled(true);
		String cacheDirPath = getFilesDir().getAbsolutePath()+APP_CACHE_DIRNAME;
		Log.i(TAG, "cacheDirPath="+cacheDirPath);
		//设置数据库缓存路径
		settings.setDatabasePath(cacheDirPath);
		//设置 Application Caches 缓存目录
		settings.setAppCachePath(cacheDirPath);
		//开启Application Caches功能
		settings.setAppCacheEnabled(true);
			
		if (android.os.Build.VERSION.SDK_INT >= 8) {
			settings.setPluginState(PluginState.ON);
		}
		settings.setAllowFileAccess(true);
		settings.setDefaultTextEncodingName("UTF-8");
		settings.setAllowFileAccessFromFileURLs(true);
		//if (android.os.Build.VERSION.SDK_INT >= 14) {
			//webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		//}
		
	}
	@Override
	//设置回退 
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
			webView.goBack();
			long secondtime = System.currentTimeMillis();
			if (secondtime - firstime > 2000) {
				Toast.makeText(MainActivity.this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
				firstime = System.currentTimeMillis();
				return true;
			} else {
				finish();
				System.exit(0);
			}
			
		}
		else if (keyCode == KeyEvent.KEYCODE_MENU)
		{
			Intent intent = new Intent(this, SettingActivity.class);
			startActivity(intent);
			//finish();
		}
		
		return super.onKeyDown(keyCode, event);
	}

	 //Web视图 
    private class CbbWebViewClient extends WebViewClient { 
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) { 
        	Log.i(TAG, "intercept url="+url); 
        	view.loadUrl(url); 
            return true; 
        }

		@Override
		public void onLoadResource(WebView view, String url) {
			// TODO Auto-generated method stub
			Log.i(TAG, "onLoadResource url="+url);
			super.onLoadResource(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			Log.i(TAG, "onPageStarted");  
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			String title = view.getTitle();  
            Log.i(TAG, "onPageFinished WebView title=" + title);
			super.onPageFinished(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			// TODO Auto-generated method stub
			super.onReceivedError(view, errorCode, description, failingUrl);
		} 
    } 
	
	
    
    public boolean isNetworkConnected(Context context) {  
        if (context != null) {  
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
                    .getSystemService(Context.CONNECTIVITY_SERVICE);  
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
            if (mNetworkInfo != null) {  
                return mNetworkInfo.isAvailable();  
            }  
        }  
        return false;  
    }
    
	@SuppressLint("NewApi")
	public boolean checkURL(String url) {
		if (!url.contains("http://")) {
			url = "http://" + url;
		}
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build()); 
	
		boolean value = false;
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url)
					.openConnection();
			int code = conn.getResponseCode();
			System.out.println(">>>>>>>>>>>>>>>> " + code
					+ " <<<<<<<<<<<<<<<<<<");
			if (code != 200) {
				value = false;
			} else {
				value = true;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}
	
	/*
	 * 通过API动态改变当前屏幕的显示方向
	 */
	public void changeOrientation(Boolean bAutoRotate) {
		
		if (!bAutoRotate) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
		else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			
			// 取得当前屏幕方向
//			Configuration config = getResources().getConfiguration();   
//		    if (config.orientation == Configuration.ORIENTATION_LANDSCAPE){   
//		            //横屏，比如 480x320
//		    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//		     }else if(config.orientation == Configuration.ORIENTATION_PORTRAIT){   
//		            //竖屏 ，标准模式 320x480
//		    	 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//		     }
		}
	}
}
