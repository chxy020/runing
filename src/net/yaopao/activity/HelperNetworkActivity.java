package net.yaopao.activity;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 */
public class HelperNetworkActivity extends BaseActivity implements OnTouchListener {
	private TextView backV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_helper_network);
		init();
	}

	private void init() {
		backV = (TextView) findViewById(R.id.back);
		backV.setOnTouchListener(this);
		WebView wView = (WebView)findViewById(R.id.wv1);     
        WebSettings wSet = wView.getSettings();     
        wSet.setJavaScriptEnabled(true);  
        wView.loadUrl("file:///android_asset/web/network.html");
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
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

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {
		case R.id.back:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				backV.setBackgroundResource(R.color.red_h);
				break;
			case MotionEvent.ACTION_UP:
				backV.setBackgroundResource(R.color.red);
				finish();
				break;
			}
			break;
		}
		return true;
	}

}
