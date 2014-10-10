package net.yaopao.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import com.umeng.analytics.MobclickAgent;

/**
 */
public class MatchWebActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_web);
		init();
	}

	@SuppressLint("NewApi")
	private void init() {
		 WebView web = (WebView)findViewById(R.id.match_web); 
		//支持javascript
		web.getSettings().setJavaScriptEnabled(true); 
		// 设置可以支持缩放 
		web.getSettings().setSupportZoom(true); 
		// 设置出现缩放工具 
		web.getSettings().setBuiltInZoomControls(true);
		//扩大比例的缩放
		web.getSettings().setUseWideViewPort(true);
		//自适应屏幕
		web.getSettings().setLoadWithOverviewMode(true);

		web.loadUrl("http://www.yaopao.net/html/ssxx.html");  
	     
	     
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
	
		MobclickAgent.onPause(this);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
	}


}
