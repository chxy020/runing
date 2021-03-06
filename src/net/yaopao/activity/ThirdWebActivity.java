package net.yaopao.activity;

import net.yaopao.assist.Variables;

import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

@SuppressLint("SetJavaScriptEnabled")
public class ThirdWebActivity extends BaseActivity {

	/** 返回按钮 */
	private Button mSettingBackBtn = null;
	/** 服务条款WebView */
	private WebView mWebViewThird = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_thirdweb);

		mWebViewThird = (WebView) this.findViewById(R.id.webview_third);
		WebSettings wSet = mWebViewThird.getSettings();
		wSet.setJavaScriptEnabled(true);
		
		Intent intent = getIntent();
		String url = intent.getStringExtra("net.yaopao.activity.ThirdUrl");
		mWebViewThird.loadUrl(url);
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
		this.mSettingBackBtn = (Button) this.findViewById(R.id.setting_back_btn);
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
				ThirdWebActivity.this.finish();
				break;
			}
		}
	};

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		super.activityOnFront=this.getClass().getSimpleName();
		Variables.activityOnFront=this.getClass().getSimpleName();
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
