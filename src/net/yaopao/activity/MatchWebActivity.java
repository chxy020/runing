package net.yaopao.activity;

import net.yaopao.assist.LoadingDialog;
import net.yaopao.assist.Variables;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.umeng.analytics.MobclickAgent;

/**
 */
public class MatchWebActivity extends BaseActivity {
	private LoadingDialog loadingDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_web);
		loadingDialog = new LoadingDialog(this);
		
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

//		web.loadUrl("http://www.yaopao.net/html/ssxx.html"); 
		String url =getIntent().getStringExtra("url");
		 
		loadingDialog.show();
		web.setWebViewClient(new WebViewClient() {  
		
		/*shouldOverrideUrlLoading方法指明了在loadUrl的时候，程序应该有怎样的行为。 
	             如果是返回false，则url由当前的webview载入， 
	             如果是true，则交给当前程序来决定如何处理。*/  
	    @Override  
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {  
	          return super.shouldOverrideUrlLoading(view, url); 
//	           return false;  //都可以得到自己处理链接响应的问题  
//	           return true;//则交给当前程序来处理，但是当前程序没有处理，所有页面不会显示。  
	    }  
	    @Override
        public void onPageFinished(WebView view,String url)
        {
	    	if (loadingDialog!=null&&loadingDialog.isShowing()) {
	    		loadingDialog.dismiss();
			}
        }
	}); 
		web.loadUrl(url);  
	}
	
	

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		super.activityOnFront=this.getClass().getSimpleName();
		Variables.activityOnFront=this.getClass().getSimpleName();
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
