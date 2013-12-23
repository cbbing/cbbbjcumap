package com.bbchen.bjcumap;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
import android.graphics.Bitmap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
import android.widget.Toast;

@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();  
    private static final String APP_CACHE_DIRNAME = "/webcache"; 
	WebView webView;
	private long firstime = 0;
	public static String defaultURL = "http://218.246.23.89/bjcumap/index_mobile.jsp";//"http://wap.baidu.com";//
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//				.detectDiskReads()
//				.detectDiskWrites()
//				.detectNetwork() // or .detectAll() for all detectable problems
//				.penaltyLog()
//				.build());
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		webView = (WebView)this.findViewById(R.id.webview);
		
//		//���û���ģʽ
//		SharedPreferences prefs =PreferenceManager.getDefaultSharedPreferences(this) ;
//	     Boolean bCacheMode = prefs.getBoolean("set_cacheMode",false);
//     	if (bCacheMode) {
//     		webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//     	}
//     	else {
//     		webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
//     	}
     	
//		DefaultHttpClient client = new DefaultHttpClient();
//		HttpGet get = new HttpGet(defaultURL);
//		HttpContext context = new BasicHttpContext();
//		CookieStore cookieStore = new BasicCookieStore();
//		context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
//		try {
//			HttpResponse response = client.execute(get, context);
//			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//				// ��������߼����жϷ��ص�ֵ�ǲ��Ǳ�ʾ�Ѿ���¼�ɹ�
//				//if (isLoginSuccess()) {
//					List cookies = cookieStore.getCookies();
//					if (!cookies.isEmpty()) {
//						for (int i = cookies.size(); i > 0; i--) {
//							Cookie cookie = (Cookie) cookies.get(i - 1);
//							if (cookie.getName().equalsIgnoreCase("jsessionid")) {
//								// ʹ��һ���������������cookie��������session����֮��
//								//Utils.appCookie = cookie;
//								
//							}
//						}
//					}
//				//}
//			}
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		initWebView();
		
		synCookies(this, defaultURL);
		 //������Ҫ��ʾ����ҳ 
		webView.loadUrl(defaultURL);
		//������Ӽ����ڵ�ǰbrowser����Ӧ���������¿�Android��ϵͳbrowser����Ӧ�����ӣ����븲�� webview��WebViewClient����
		webView.setWebViewClient(new CbbWebViewClient());
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SharedPreferences prefs =PreferenceManager.getDefaultSharedPreferences(this) ;
     
		//�޸���ҳ
        String userUrl = prefs.getString("set_url","");//�޸ĺ��url
        String userUrlPre = prefs.getString("set_url_pre",defaultURL);//�޸�֮ǰ��url
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
        
        
        //������ת
     	Boolean bAutoRotate = prefs.getBoolean("set_rotate",false);//false��ʾû�в鵽checkbox���key�ķ���ֵ
     	changeOrientation(bAutoRotate);
     	
     	//��������
     	Boolean bZoom = prefs.getBoolean("set_zoom", false);
     	webView.getSettings().setBuiltInZoomControls(bZoom);	
     	webView.getSettings().setSupportZoom(true);
     	webView.getSettings().setUseWideViewPort(bZoom);
     	
     	//���û���ģʽ
     	Boolean bCacheMode = prefs.getBoolean("set_cacheMode",false);
     	if (bCacheMode) {
     		webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
     	}
     	else {
     		webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
     	}
     	
     	//����Javascript
     	Boolean bOpenJS= prefs.getBoolean("set_javascript",true);
     	webView.getSettings().setJavaScriptEnabled(bOpenJS);
     	
     	//����Ӳ������
     	Boolean bAccHandWare= prefs.getBoolean("set_handware",false);
     	if (bAccHandWare) {
     		webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		}else {
			webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
     	
     	
	}
	@SuppressLint("NewApi")
	private void initWebView() {
		//����WebView���ԣ��ܹ�ִ��Javascript�ű� 
		WebSettings settings = webView.getSettings();
		settings.setPluginState(PluginState.ON);
		settings.setJavaScriptEnabled(true);
		//settings.setRenderPriority(RenderPriority.HIGH);//�����Ⱦ�����ȼ�,��߼����ٶ�
		//settings.setBlockNetworkImage(true);//��ͼƬ���ط��������������Ⱦ
		//���� ����ģʽ
		//if (isNetworkConnected(this)) {
		//	settings.setCacheMode(WebSettings.LOAD_DEFAULT);
		//}
		//else {
		//	settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		//}
		
		
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
			
		settings.setAllowFileAccess(true);
		settings.setDefaultTextEncodingName("UTF-8");
		settings.setAllowFileAccessFromFileURLs(true);
		//if (android.os.Build.VERSION.SDK_INT >= 14) {
			//webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		//}
		
	}
	@Override
	//���û��� 
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
			webView.goBack();
			long secondtime = System.currentTimeMillis();
			if (secondtime - firstime > 2000) {
				Toast.makeText(MainActivity.this, "�ٰ�һ�η��ؼ��˳�", Toast.LENGTH_SHORT).show();
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

	 //Web��ͼ 
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
	 * ͨ��API��̬�ı䵱ǰ��Ļ����ʾ����
	 */
	public void changeOrientation(Boolean bAutoRotate) {
		
		if (bAutoRotate) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
		else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			
			// ȡ�õ�ǰ��Ļ����
//			Configuration config = getResources().getConfiguration();   
//		    if (config.orientation == Configuration.ORIENTATION_LANDSCAPE){   
//		            //���������� 480x320
//		    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//		     }else if(config.orientation == Configuration.ORIENTATION_PORTRAIT){   
//		            //���� ����׼ģʽ 320x480
//		    	 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//		     }
		}
	}
	
	public static String getCookie(Context context, String url){
		CookieManager cookieManager = CookieManager.getInstance();
		
		String cookie = cookieManager.getCookie(url);
		if(cookie != null){
			cookie= "mobileFlag=MOBILE";
			return cookie;
		}else{
			cookie= "mobileFlag=MOBILE";
			cookieManager.setCookie("cookie", cookie);
			return cookie;
		}
	}
	/**
	 * ͬ��һ��cookie
	 */
	public static void synCookies(Context context, String url) {
		String cookies = "mobileFlag=MOBILE";;
		CookieSyncManager.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(true);
		//cookieManager.removeSessionCookie();//�Ƴ�
		cookieManager.setCookie(url, cookies);//cookies����HttpClient�л�õ�cookie
		CookieSyncManager.getInstance().sync();
		
	}
}
