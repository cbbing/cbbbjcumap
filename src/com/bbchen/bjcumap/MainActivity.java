package com.bbchen.bjcumap;

import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;

@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity {

	WebView webView;
	private final Handler handler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		webView = (WebView)this.findViewById(R.id.webview);
		//设置WebView属性，能够执行Javascript脚本 
		WebSettings settings = webView.getSettings();
		settings.setPluginState(PluginState.ON);
		settings.setJavaScriptEnabled(true); 
		
		if(android.os.Build.VERSION.SDK_INT>=8){
			settings.setPluginState(PluginState.ON);
		}
		settings.setAllowFileAccess(true);
		settings.setDefaultTextEncodingName("UTF-8");
		settings.setAllowFileAccessFromFileURLs(true);
		if(android.os.Build.VERSION.SDK_INT >= 14) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		}
		settings.setSupportZoom(true);
		settings.setAppCacheEnabled(true);
    
		//if (check()) {
			 //加载需要显示的网页 
			webView.loadUrl("http://218.246.23.85/bjcumap/index.jsp");
			//点击链接继续在当前browser中响应，而不是新开Android的系统browser中响应该链接，必须覆盖 webview的WebViewClient对象
			webView.setWebViewClient(new HelloWebViewClient());
//		}
//		else {
//			install();
//		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
            view.loadUrl(url); 
            return true; 
        } 
    } 
    
    private boolean check() {
		PackageManager pm = getPackageManager();
		List<PackageInfo> infoList = pm
				.getInstalledPackages(PackageManager.GET_SERVICES);
		for (PackageInfo info : infoList) {
			if ("com.adobe.flashplayer".equals(info.packageName)) {
				return true;
			}
		}
		return false;
	}
	
	private class AndroidBridge {
		public void goMarket() {
			handler.post(new Runnable() {
				public void run() {
					Intent installIntent = new Intent(
							"android.intent.action.VIEW");
					installIntent.setData(Uri
							.parse("market://details?id=com.adobe.flashplayer"));
					startActivity(installIntent);
				}
			});
		}
	}
	@SuppressLint("JavascriptInterface")
	private void install() {
		webView.addJavascriptInterface(new AndroidBridge(),"android");
		webView.loadUrl("file:///android_asset/go_market.html");
	}
	
}
