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
		
		 //������Ҫ��ʾ����ҳ 
		webView.loadUrl("http://218.246.23.85/bjcumap/index.jsp");
		//������Ӽ����ڵ�ǰbrowser����Ӧ���������¿�Android��ϵͳbrowser����Ӧ�����ӣ����븲�� webview��WebViewClient����
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
	//���û��� 
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}

	 //Web��ͼ 
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
		//����WebView���ԣ��ܹ�ִ��Javascript�ű� 
		WebSettings settings = webView.getSettings();
		settings.setPluginState(PluginState.ON);
		settings.setJavaScriptEnabled(true);
		settings.setRenderPriority(RenderPriority.HIGH);//�����Ⱦ�����ȼ�,��߼����ٶ�
		//settings.setBlockNetworkImage(true);//��ͼƬ���ط��������������Ⱦ
		settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//���� ����ģʽ
		
		// ���� DOM storage API ����  
		settings.setDomStorageEnabled(true);
		//���� database storage API ����  
		settings.setDatabaseEnabled(true);
		String cacheDirPath = getFilesDir().getAbsolutePath()+APP_CACHE_DIRNAME;
		Log.i(TAG, "cacheDirPath="+cacheDirPath);
		//�������ݿ⻺��·��
		settings.setDatabasePath(cacheDirPath);
		//���� Application Caches ����Ŀ¼
		settings.setAppCachePath(cacheDirPath);
		//����Application Caches����
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
}
