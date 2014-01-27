package com.bbchen.bjcumap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.bbchen.util.ActivityList;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
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
	//public static String defaultURL = "http://www.bjwhmap.com/bjcumap/functionZone/main/video.jsp?videoName=%E6%96%87%E5%8C%96%E7%A7%91%E6%8A%80%E8%9E%8D%E5%90%88%E7%A4%BA%E8%8C%83%E5%8A%9F%E8%83%BD%E5%8C%BA&videoUrl=1";
	public static String defaultURL = "http://www.bjwhmap.com/bjcumap/index_mobile.jsp?mobileFlag=MOBILE";////
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
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
		
		webView.setDownloadListener(new MyWebViewDownLoadListener());
		ActivityList.activityListAll.add(this);
		
		//����
		MobclickAgent.updateOnlineConfig(this);
		UmengUpdateAgent.update(this);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
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
     	
     	MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
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
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (webView.canGoBack()) {
				webView.goBack();
				return true;
			}
			else {
				AlertDialog.Builder mDialog = new AlertDialog.Builder(this);  
		        mDialog.setTitle(getString(R.string.tipTitle));  
		        mDialog.setMessage(getString(R.string.tipMain));  
		        mDialog.setPositiveButton(getString(R.string.ok),  
		                new DialogInterface.OnClickListener() {  
		                    public void onClick(DialogInterface dialog, int which) {  
		                        System.exit(0);  
		                    }  
		                });  
		        mDialog.setNegativeButton(getString(R.string.cancel), null);  
		        mDialog.show(); 
			}
			
//			long secondtime = System.currentTimeMillis();
//			if (secondtime - firstime > 1000) {
//				Toast.makeText(MainActivity.this, "�ٰ�һ�η��ؼ��˳�", Toast.LENGTH_SHORT).show();
//				firstime = System.currentTimeMillis();
//				return true;
//			} else {
//				finish();
//				System.exit(0);
//			}
			
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
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) { 
//        	Log.i(TAG, "intercept url="+url); 
//        	view.loadUrl(url); 
//            return true; 
//        }

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
	
    //DownListener �ļ�����
    private class CbbWebViewDownLoadListener implements DownloadListener{

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {        	
        	Log.i("tag", "url="+url);        	
        	Log.i("tag", "userAgent="+userAgent);
        	Log.i("tag", "contentDisposition="+contentDisposition);        	
        	Log.i("tag", "mimetype="+mimetype);
        	Log.i("tag", "contentLength="+contentLength);
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);        	 
        }
    }

	// �ڲ���
	private class MyWebViewDownLoadListener implements DownloadListener {

		@Override
		public void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength) {
			if (!Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				Toast t = Toast.makeText(MainActivity.this, "��ҪSD����",
						Toast.LENGTH_LONG);
				t.setGravity(Gravity.CENTER, 0, 0);
				t.show();
				return;
			}
			DownloaderTask task = new DownloaderTask();
			task.execute(url);
		}

	}

	// �ڲ���
	private class DownloaderTask extends AsyncTask<String, Void, String> {

		public DownloaderTask() {
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = params[0];
			// Log.i("tag", "url="+url);
			String fileName = url.substring(url.lastIndexOf("/") + 1);
			fileName = URLDecoder.decode(fileName);
			Log.i("tag", "fileName=" + fileName);

			File directory = Environment.getExternalStorageDirectory();
			File file = new File(directory, fileName);
			if (file.exists()) {
				Log.i("tag", "The file has already exists.");
				return fileName;
			}
			try {
				HttpClient client = new DefaultHttpClient();
				// client.getParams().setIntParameter("http.socket.timeout",3000);//���ó�ʱ
				HttpGet get = new HttpGet(url);
				HttpResponse response = client.execute(get);
				if (HttpStatus.SC_OK == response.getStatusLine()
						.getStatusCode()) {
					HttpEntity entity = response.getEntity();
					InputStream input = entity.getContent();

					writeToSDCard(fileName, input);

					input.close();
					// entity.consumeContent();
					return fileName;
				} else {
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			closeProgressDialog();
			if (result == null) {
				Toast t = Toast.makeText(MainActivity.this, "���Ӵ������Ժ����ԣ�",
						Toast.LENGTH_LONG);
				t.setGravity(Gravity.CENTER, 0, 0);
				t.show();
				return;
			}

			Toast t = Toast.makeText(MainActivity.this, "�ѱ��浽SD����",
					Toast.LENGTH_LONG);
			t.setGravity(Gravity.CENTER, 0, 0);
			t.show();
			File directory = Environment.getExternalStorageDirectory();
			File file = new File(directory, result);
			Log.i("tag", "Path=" + file.getAbsolutePath());

			Intent intent = getFileIntent(file);

			startActivity(intent);

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			//showProgressDialog();
			Toast.makeText(MainActivity.this, "�ѿ�ʼ��̨���أ���ȴ�...", Toast.LENGTH_LONG).show();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}
		
		private ProgressDialog mDialog;
		private void showProgressDialog(){
			if(mDialog==null){
				mDialog = new ProgressDialog(MainActivity.this);  
				mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//���÷��ΪԲ�ν�����  
				mDialog.setMessage("���ڼ��� ����ȴ�...");  
				mDialog.setIndeterminate(false);//���ý������Ƿ�Ϊ����ȷ  
				mDialog.setCancelable(true);//���ý������Ƿ���԰��˻ؼ�ȡ��  
				mDialog.setCanceledOnTouchOutside(false);
				mDialog.setOnDismissListener(new OnDismissListener() {
					
					public void onDismiss(DialogInterface dialog) {
						// TODO Auto-generated method stub
						mDialog=null;
					}
				});
				mDialog.show();
				
			}
		}
		private void closeProgressDialog(){
			if(mDialog!=null){
				mDialog.dismiss();
				mDialog=null;
			}
		}
		 public Intent getFileIntent(File file){
//			 Uri uri = Uri.parse("http://m.ql18.com.cn/hpf10/1.pdf");
			Uri uri = Uri.fromFile(file);
			String type = getMIMEType(file);
			Log.i("tag", "type="+type);
		    Intent intent = new Intent("android.intent.action.VIEW");
		    intent.addCategory("android.intent.category.DEFAULT");
		    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    intent.setDataAndType(uri, type);
		    return intent;
		  }
		 
		public void writeToSDCard(String fileName,InputStream input){
			
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				File directory=Environment.getExternalStorageDirectory();
				File file=new File(directory,fileName);
//				if(file.exists()){
//					Log.i("tag", "The file has already exists.");
//					return;
//				}
				try {
					FileOutputStream fos = new FileOutputStream(file);
					byte[] b = new byte[2048];
					int j = 0;
					while ((j = input.read(b)) != -1) {
						fos.write(b, 0, j);
					}
					fos.flush();
					fos.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				Log.i("tag", "NO SDCard.");
			}
		}
		
		private String getMIMEType(File f){   
	      String type="";  
	      String fName=f.getName();  
	      /* ȡ����չ�� */  
	      String end=fName.substring(fName.lastIndexOf(".")+1,fName.length()).toLowerCase();
	      
	      /* ����չ�������;���MimeType */
	      if(end.equals("pdf")){
	    	  type = "application/pdf";//
	      }
	      else if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||  
	      end.equals("xmf")||end.equals("ogg")||end.equals("wav")){  
	        type = "audio/*";   
	      }  
	      else if(end.equals("3gp")||end.equals("mp4")){  
	        type = "video/*";  
	      }  
	      else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||  
	      end.equals("jpeg")||end.equals("bmp")){  
	        type = "image/*";  
	      }  
	      else if(end.equals("apk")){   
	        /* android.permission.INSTALL_PACKAGES */   
	        type = "application/vnd.android.package-archive"; 
	      }
//	      else if(end.equals("pptx")||end.equals("ppt")){
//	    	  type = "application/vnd.ms-powerpoint"; 
//	      }else if(end.equals("docx")||end.equals("doc")){
//	    	  type = "application/vnd.ms-word";
//	      }else if(end.equals("xlsx")||end.equals("xls")){
//	    	  type = "application/vnd.ms-excel";
//	      }
	      else{
//	    	  /*����޷�ֱ�Ӵ򿪣�����������б���û�ѡ�� */  
	        type="*/*";
	      }
	      return type;
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
