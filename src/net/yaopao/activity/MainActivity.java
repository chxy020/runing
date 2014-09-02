package net.yaopao.activity;

import net.yaopao.assist.DataTool;
import net.yaopao.assist.DialogTool;
import net.yaopao.assist.Variables;
import net.yaopao.bean.DataBean;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

public class MainActivity extends Activity implements OnTouchListener {
	private TextView state;
	private TextView desc;
	private TextView toutalCount;
	private TextView avgSpeed;
	private TextView points;

	private ImageView start;
	private ImageView headv;
	private LinearLayout stateL;
	private LinearLayout recording;
	private LinearLayout matchL;
	private ImageView d1v;
	private ImageView d2v;
	private ImageView d3v;
	private ImageView d4v;
	private ImageView d5v;
	private ImageView d6v;
	private Bitmap head;
	private double distance;
	/** 设置 */
	private TextView mMainSetting = null;
	/** 系统消息 */
	private LinearLayout mMessageLayout = null;

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
		toutalCount = (TextView) this.findViewById(R.id.main_count);
		avgSpeed = (TextView) this.findViewById(R.id.main_speed);
		points = (TextView) this.findViewById(R.id.main_points);
		d1v = (ImageView) this.findViewById(R.id.main_milage_num1);
		d2v = (ImageView) this.findViewById(R.id.main_milage_num2);
		d3v = (ImageView) this.findViewById(R.id.main_milage_num3);
		d4v = (ImageView) this.findViewById(R.id.main_milage_num4);
		d5v = (ImageView) this.findViewById(R.id.main_milage_dec1);
		d6v = (ImageView) this.findViewById(R.id.main_milage_dec2);
		d1v.setVisibility(View.GONE);
		d2v.setVisibility(View.GONE);
		d3v.setVisibility(View.GONE);
		recording = (LinearLayout) this.findViewById(R.id.main_fun_recording);
		stateL.setOnTouchListener(this);
		matchL.setOnTouchListener(this);
		start.setOnTouchListener(this);
		recording.setOnTouchListener(this);

		this.initView();
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
		initMileage();
		DataBean data = YaoPao01App.db.queryData();
		toutalCount.setText(data.getCount()+""); 
		avgSpeed.setText(getSeed(data.getPspeed())); 
		

	}

	private void initMileage() {
		DataBean data = YaoPao01App.db.queryData();
		distance = data.getDistance();
		//distance = 549254;
		int d1 = (int) distance / 1000000;
		int d2 = (int) (distance % 1000000) / 100000;
		int d3 = (int) (distance % 100000) / 10000;
		int d4 = (int) (distance % 10000) / 1000;
		int d5 = (int) (distance % 1000) / 100;
		int d6 = (int) (distance % 100) / 10;
		if (d1 > 0) {
			d1v.setVisibility(View.VISIBLE);
		}
		if (d2 > 0) {
			d2v.setVisibility(View.VISIBLE);
		}
		if (d3 > 0) {
			d3v.setVisibility(View.VISIBLE);
		}
		update(d1, d1v);
		update(d2, d2v);
		update(d3, d3v);
		update(d4, d4v);
		update(d5, d5v);
		update(d6, d6v);
	}
	private String getSeed(double avgspeed) {
		int[] speed = YaoPao01App
				.cal((int) avgspeed);
		String s1 = (speed[1] / 10)+"";
		String s2 =( speed[1] % 10)+"";
		String s3 = (speed[2] / 10)+"";
		String s4 = (speed[2] % 10)+"";
		return s1+""+s2+"'"+s3+""+s4+"\"";
	}

	protected void update(int i, ImageView view) {
		if (i > 9) {
			i = i % 10;
		}
		switch (i) {
		case 0:
			view.setBackgroundResource(R.drawable.r_0);
			break;
		case 1:
			view.setBackgroundResource(R.drawable.r_1);
			break;
		case 2:
			view.setBackgroundResource(R.drawable.r_2);
			break;
		case 3:
			view.setBackgroundResource(R.drawable.r_3);
			break;
		case 4:
			view.setBackgroundResource(R.drawable.r_4);
			break;
		case 5:
			view.setBackgroundResource(R.drawable.r_5);
			break;
		case 6:
			view.setBackgroundResource(R.drawable.r_6);
			break;
		case 7:
			view.setBackgroundResource(R.drawable.r_7);
			break;
		case 8:
			view.setBackgroundResource(R.drawable.r_8);
			break;
		case 9:
			view.setBackgroundResource(R.drawable.r_9);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		if (head != null) {
			head.recycle();
			head = null;
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
				if (YaoPao01App.isGpsAvailable()) {
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
				if (YaoPao01App.isGpsAvailable()) {
					Intent mainIntent = new Intent(MainActivity.this,
							MatchCountdownActivity.class);
					MainActivity.this.startActivity(mainIntent);
				}
				// Intent mainIntent = new Intent(MainActivity.this,
				// MatchWatchActivity.class);
				// MainActivity.this.startActivity(mainIntent);
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
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			DialogTool.quit(MainActivity.this);
		}
		return false;
	}

	/************************* chenxy add ******************/

	/**
	 * 页面初始化
	 * 
	 * @author cxy
	 * @date 2014-8-25
	 */
	private void initView() {
		// 获取设置
		mMainSetting = (TextView) findViewById(R.id.main_setting);
		// 获取系统消息layout
		mMessageLayout = (LinearLayout) findViewById(R.id.main_message_layout);

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
		mMainSetting.setOnClickListener(mOnClickListener);
		mMessageLayout.setOnClickListener(mOnClickListener);
	}

	/**
	 * 单击事件
	 */
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.main_setting:
				Intent settingIntent = new Intent(MainActivity.this,
						MainSettingActivity.class);
				startActivity(settingIntent);
				break;
			case R.id.main_message_layout:
				Intent messageIntent = new Intent(MainActivity.this,
						WebViewActivity.class);
				messageIntent.putExtra("net.yaopao.activity.PageUrl",
						"message_index.html");
				startActivity(messageIntent);
				break;
			}
		}
	};
}
