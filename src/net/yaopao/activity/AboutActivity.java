package net.yaopao.activity;

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
public class AboutActivity extends Activity {

	/** 返回按钮 */
	private Button mSettingBackBtn = null;
	/** 服务条款WebView */
	private WebView mWebViewAbout = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		mWebViewAbout = (WebView) this.findViewById(R.id.webview_about);
		WebSettings wSet = mWebViewAbout.getSettings();
		wSet.setJavaScriptEnabled(true);

		// asset目录
		mWebViewAbout.loadUrl("file:///android_asset/setting/about.html");
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
		this.mSettingBackBtn = (Button) this
				.findViewById(R.id.setting_back_btn);
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
		this.mSettingBackBtn.setOnClickListener(mOnClickListener);
	}

	/**
	 * 单击事件
	 */
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.setting_back_btn:
				// 返回
				AboutActivity.this.finish();
				break;
			}
		}
	};
}
