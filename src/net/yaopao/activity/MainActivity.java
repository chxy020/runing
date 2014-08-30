package net.yaopao.activity;

import net.yaopao.assist.DataTool;
import net.yaopao.assist.DialogTool;
import net.yaopao.assist.Variables;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

public class MainActivity extends Activity implements OnTouchListener {
	TextView state;
	TextView desc;
	
	ImageView start;
	ImageView headv;
	LinearLayout stateL;
	LinearLayout recording ;
	LinearLayout matchL;
	Bitmap head ;
	/** 设置*/
	private TextView mMainSetting = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		state = (TextView) this.findViewById(R.id.main_state);
		stateL = (LinearLayout) this.findViewById(R.id.main_user_info);
		desc = (TextView) this.findViewById(R.id.main_state_desc);
		matchL = (LinearLayout) this.findViewById(R.id.main_fun_macth);
		start = (ImageView) this.findViewById(R.id.main_start);
		headv = (ImageView) this.findViewById(R.id.main_head);
		
		recording =(LinearLayout) this.findViewById(R.id.main_fun_recording);
		stateL.setOnTouchListener(this);
		matchL.setOnTouchListener(this);
		start.setOnTouchListener(this);
		recording.setOnTouchListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		initLayout();
	}

	private void initLayout() {

		if (Variables.islogin == 1) {
			JSONObject userInfo = DataTool.getUserInfo();
			if (userInfo != null) {
				if (!"".equals(userInfo.getString("nickname"))) {
					state.setText(userInfo.getString("nickname"));
				} else {
					state.setText(YaoPao01App.sharedPreferences.getString(
							"phone", ""));
				}
				desc.setText(userInfo.getString("signature"));

				 head = DataTool.getHead();
				if (head != null) {
					headv.setImageBitmap(head);
				}
			} else {
				state.setText(YaoPao01App.sharedPreferences.getString("phone",
						""));
				desc.setText("一句话简介");
			}

		} else {
			state.setText("未登录");
		}
	}

	@Override
	protected void onDestroy() {
		if (head!=null) {
			head.recycle();
			head=null;
		}
		super.onDestroy();
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {
		case R.id.main_user_info:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				Intent mainIntent;
				if (Variables.islogin == 1) {
					mainIntent = new Intent(MainActivity.this,
							UserInfoActivity.class);
				} else {
					mainIntent = new Intent(MainActivity.this,
							RegisterActivity.class);
				}

				startActivity(mainIntent);
				break;
			}
			break;
		case R.id.main_start:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				if(YaoPao01App.isGpsAvailable()){
					Intent mainIntent = new Intent(MainActivity.this,
							SportSetActivity.class);
					startActivity(mainIntent);
				}
				break;
			}
			break;
		case R.id.main_fun_recording:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				Intent mainIntent = new Intent(MainActivity.this,
						SportListActivity.class);
				MainActivity.this.startActivity(mainIntent);
				break;
			}
			break;
		case R.id.main_fun_macth:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				if(YaoPao01App.isGpsAvailable()){
					Intent mainIntent = new Intent(MainActivity.this,
							MatchCountdownActivity.class);
					MainActivity.this.startActivity(mainIntent);
				}
//				Intent mainIntent = new Intent(MainActivity.this,
//						MatchWatchActivity.class);
//				MainActivity.this.startActivity(mainIntent);
				break;
			}
			break;

		default:
			break;
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		 if (keyCode == KeyEvent.KEYCODE_BACK )  
	        {  
			 DialogTool.quit(MainActivity.this);
	        }
		  return false;
	}

/*************************chenxy add ******************/
	
	/**
	 * 页面初始化
	 * @author cxy
	 * @date 2014-8-25
	 */
	private void initView(){
		//获取设置
		mMainSetting = (TextView)findViewById(R.id.main_setting);
		//注册事件
		this.setListener();
	}
	
	/**
	 * 注册事件
	 * @author cxy
	 * @date 2014-8-25
	 */
	private void setListener() {
		//注册设置事件
		mMainSetting.setOnClickListener(mOnClickListener);
	}
	
	/**
	 * 单击事件
	 */
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.main_setting:
					Intent settingIntent = new Intent(MainActivity.this,MainSettingActivity.class);
					startActivity(settingIntent);
				break;
			}
		}
	};
}
