package net.yaopao.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {
	

	/** webview */
	private WebView mWebView = null;
	private String mPageUrl = "";

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);

		mWebView = (WebView) this.findViewById(R.id.local_webview);
		// 获取页面URL
		Intent intent = getIntent();
		mPageUrl = intent.getStringExtra("net.yaopao.activity.PageUrl");

		// web设置
		WebSettings setting = mWebView.getSettings();
		setting.setSupportZoom(false);
		setting.setJavaScriptEnabled(true);
		setting.setDomStorageEnabled(true);
		setting.setBuiltInZoomControls(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setHorizontalScrollbarOverlay(false);
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setVerticalScrollbarOverlay(false);
		mWebView.setWebViewClient(new UserWebClient());
		
		
		//设置本地接口给js调用
		mWebView.addJavascriptInterface(new Object(){
			//回到前一页
			public void gotoPrePage(){
				//Log.e("","chxy _______prepage");
				WebViewActivity.this.finish();
			}
		}, "JSAndroidBridge");

		// 加载web页面
		if (mPageUrl.equals("message_index.html")) {
			mWebView.loadUrl("file:///android_asset/web/" + mPageUrl);
		}

		// 初始化
		this.initLoad();
	}

	/**
	 * 页面初始化,获取页面元素
	 * 
	 * @author cxy
	 * @date 2014-8-25
	 */
	private void initLoad() {

	}
	
	
	
	private void jsCallbackMethod(String param){
		//Log.e("","chxy _______javascript" + param);
		mWebView.loadUrl("javascript:" + param + ";");
	}
	
	class UserWebClient extends WebViewClient{
		/* 设置webview的跳转始终在自己的activity,而不调用浏览器
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
		*/
		@Override
		public void onLoadResource(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onLoadResource(view, url);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			// if(!firstComeIn)return;
			// firstComeIn = false;

			// int isLogin = MeController.isLogin;
			// String userInfoJSON = MeController.getUserInfoJSON();
			// String ua = GetXMLByHTTP.clientInfo;
			// String pid = GetXMLByHTTP.pid;
			// DisplayMetrics dm = new DisplayMetrics();
			// getWindowManager().getDefaultDisplay().getMetrics(dm);
			// int nowWidth = dm.widthPixels; // 当前分辨率 宽度
			// int nowHeight = dm.heightPixels; // 当前分辨率高度

			String userInfo = "";
			String playInfo = "";
			String deviceInfo = "";
			//Log.e("","chxy ____url" + url);
			if(-1 != url.indexOf("message_index.html")){
				//消息首页
				userInfo = "{\"uid\":\"" + "77" + "\"}";
				playInfo = "{}";
				deviceInfo = "{\"deviceid\":\"" + "tre211" + "\"}";
				String param = "window.callbackInit('" + userInfo + "','"
						+ playInfo + "'," + "'" + deviceInfo + "')";
				jsCallbackMethod(param);
			}
		}
	}
}
