package com.bbchen.bjcumap;

import java.io.File;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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

@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();  
    private static final String APP_CACHE_DIRNAME = "/webcache"; 
	WebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		webView = (WebView)this.findViewById(R.id.webview);
		
		initWebView();
		
		 //加载需要显示的网页 
		webView.loadUrl("http://218.246.23.85/bjcumap/index.jsp");
		//点击链接继续在当前browser中响应，而不是新开Android的系统browser中响应该链接，必须覆盖 webview的WebViewClient对象
		webView.setWebViewClient(new HelloWebViewClient());
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		Intent intent = new Intent(this, SettingActivity.class);
		startActivity(intent);
		return true;
	}

	@Override
	//设置回退 
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}

	 //Web视图 
    private class HelloWebViewClient extends WebViewClient { 
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
			Log.e(TAG, "onPageStarted");  
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			String title = view.getTitle();  
            Log.e(TAG, "onPageFinished WebView title=" + title);
			super.onPageFinished(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			// TODO Auto-generated method stub
			super.onReceivedError(view, errorCode, description, failingUrl);
		} 
    } 
	
	private void initWebView() {
		//设置WebView属性，能够执行Javascript脚本 
		WebSettings settings = webView.getSettings();
		settings.setPluginState(PluginState.ON);
		settings.setJavaScriptEnabled(true);
		settings.setRenderPriority(RenderPriority.HIGH);//提高渲染的优先级,提高加载速度
		//settings.setBlockNetworkImage(true);//把图片加载放在最后来加载渲染
		settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//设置 缓存模式
		
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
		if (android.os.Build.VERSION.SDK_INT >= 14) {
			webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		}
		settings.setSupportZoom(true);
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
}
