package net.yaopao.activity;

import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

@SuppressLint("SetJavaScriptEnabled")
public class ServiceActivity extends BaseActivity {

	/** 返回按钮 */
	private Button backBtn = null;
	/** 服务条款WebView */
	private WebView mWebViewService = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service);

		mWebViewService = (WebView) this.findViewById(R.id.webview_service);
		WebSettings wSet = mWebViewService.getSettings();
		wSet.setJavaScriptEnabled(true);

		// asset目录
		mWebViewService.loadUrl("file:///android_asset/web/term_of_service.html");
		// sd卡
		// wView.loadUrl("content://com.android.htmlfileprovider/sdcard/index.html");
		// wView.loadUrl("http://wap.baidu.com");
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
		this.backBtn = (Button) this
				.findViewById(R.id.service_back_btn);
		// 注册事件
		this.setListener();
	}

	/**
	 * 注册事件
	 * 
	 * @author cxy
	 * @date 2014-8-25
	 */
	private void setListener() {
		// 注册设置事件
		this.backBtn.setOnClickListener(mOnClickListener);
	}

	/**
	 * 单击事件
	 */
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.service_back_btn:
				// 返回
				ServiceActivity.this.finish();
				break;
			}
		}
	};

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
