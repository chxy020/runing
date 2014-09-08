package net.yaopao.activity;

import net.yaopao.assist.DataTool;
import net.yaopao.assist.DialogTool;
import net.yaopao.assist.Variables;
import net.yaopao.bean.DataBean;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

public class MainActivity extends Activity implements OnTouchListener,OnClickListener{
	private TextView state;
	private TextView desc;
//	private TextView toutalCount;
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
	private DataBean data;
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
		stateL.setOnClickListener(this);
		recording.setOnClickListener(this);
		matchL.setOnClickListener(this);
		
		start.setOnTouchListener(this);
		if (Variables.gpsStatus==2) {
			DialogTool dialog = new DialogTool(MainActivity.this,handler);
			WindowManager m = getWindowManager();
			Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
			dialog.alertGpsTip2(d);
		}
		this.initView();
		
	}
	//初始化总次数
	private void initCountView(DataBean data) {
		ImageView c1v = (ImageView) findViewById(R.id.main_count_num1);
		ImageView c2v = (ImageView) findViewById(R.id.main_count_num2);
		ImageView c3v = (ImageView) findViewById(R.id.main_count_num3);
		c1v.setVisibility(View.GONE);
		c2v.setVisibility(View.GONE);
		initCount(new ImageView[]{c1v,c2v,c3v},data);
	}
	private void initCount(ImageView[] views,DataBean data){
		int count = data.getCount();
		Log.v("wysport", " count="+count);
		int c1 = count /100;
		int c2 =  (count%100)/10;
		int c3 =count%10;
		if (c1 > 0) {
			views[0].setVisibility(View.VISIBLE);
			update(c1, views[0]);
		}
		if (c2 > 0) {
			views[1].setVisibility(View.VISIBLE);
			update(c2, views[1]);
		}
		update(c3, views[2]);
	}
	@Override
	protected void onResume() {
		super.onResume();
		initLayout();
	}

	private void initLayout() {
		data = YaoPao01App.db.queryData();
		if (Variables.islogin == 1) {
			JSONObject userInfo = DataTool.getUserInfo();
			if (userInfo != null) {
				if (!"".equals(userInfo.getString("nickname"))||userInfo.getString("nickname")!=null) {
					state.setText(userInfo.getString("nickname"));
				} else {
					state.setText(YaoPao01App.sharedPreferences.getString(
							"phone", ""));
				}
				desc.setText(userInfo.getString("signature"));
				head = Variables.avatar;
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
		initCountView(data);
		toutalCount.setText(data.getCount()+""); 
		avgSpeed.setText(getSeed(data.getPspeed())); 
		points.setText(data.getPoints()+"");
		

	}

	private void initMileage() {
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
		case R.id.main_start:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				start.setBackgroundResource(R.drawable.button_start_h);
				break;
			case MotionEvent.ACTION_UP:
				start.setBackgroundResource(R.drawable.button_start);
				if (Variables.gpsStatus==2) {
//				if (Variables.gpsStatus==4) {
					DialogTool dialog = new DialogTool(MainActivity.this,handler);
					WindowManager m = getWindowManager();
					Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
					dialog.alertGpsTip2(d);
				}else if (Variables.gpsStatus==0) {
//				}else if (Variables.gpsStatus==5) {
					DialogTool dialog = new DialogTool(MainActivity.this,null);
					WindowManager m = getWindowManager();
					Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
					dialog.alertGpsTip1(d);
				}else if(Variables.gpsStatus==1){
//				}else {
					Intent mainIntent = new Intent(MainActivity.this,
							SportSetActivity.class);
					startActivity(mainIntent);
				}
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
		mMainSetting.setOnClickListener(this);
		mMessageLayout.setOnClickListener(this);
	}

	/**
	 * 单击事件
	 */
//	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			switch (v.getId()) {
//		
//			}
//		}
//	};
	 Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				Log.v("wyalert", "0");
				openHtml();
			}
			super.handleMessage(msg);
		}
	};
	private void openHtml() {
		Intent intent =  new Intent(this,HelperActivity.class);
//		startActivityForResult(intent, 0);
		startActivity(intent);
		
	}


	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.main_user_info:
			Intent userIntent;
			if (Variables.islogin == 1) {
				userIntent = new Intent(MainActivity.this, UserInfoActivity.class);
			} else {
				userIntent = new Intent(MainActivity.this, RegisterActivity.class);
			}

			startActivity(userIntent);
			break;
		case R.id.main_fun_recording:
			Intent recIntent = new Intent(MainActivity.this, SportListActivity.class);
			MainActivity.this.startActivity(recIntent);
			break;
			
		case R.id.main_fun_macth:
				/*if (YaoPao01App.isGpsAvailable()) {
					Intent mainIntent = new Intent(MainActivity.this,
							MatchCountdownActivity.class);
					MainActivity.this.startActivity(mainIntent);
				}*/
				// Intent mainIntent = new Intent(MainActivity.this,
				// MatchWatchActivity.class);
				// MainActivity.this.startActivity(mainIntent);
			 Intent intent= new Intent();        
			    intent.setAction("android.intent.action.VIEW");    
			    Uri content_url = Uri.parse("http://www.yaopao.net/html/ssxx.html");   
			    intent.setData(content_url);  
			    startActivity(intent);
			break;
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
		default:
			break;
		}

	}
	
}
