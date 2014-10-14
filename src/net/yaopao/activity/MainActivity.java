package net.yaopao.activity;


import net.yaopao.assist.CNAppDelegate;
import net.yaopao.assist.Constants;
import net.yaopao.assist.DataTool;
import net.yaopao.assist.DialogTool;
import net.yaopao.assist.LoadingDialog;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.SyncTimeLoadingDialog;
import net.yaopao.assist.Variables;
import net.yaopao.bean.DataBean;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;

public class MainActivity extends BaseActivity implements OnTouchListener,
		OnClickListener {
	private TextView state;
	private TextView desc;
	private ImageView start;
	private ImageView headv;
	private LinearLayout stateL;
	private LinearLayout recording;
	private LinearLayout matchL;
	private Bitmap head;
	private double distance;
	public byte[] imageByte;
	/** 设置 */
	private TextView mMainSetting = null;
	/** 系统消息 */
	private LinearLayout mMessageLayout = null;
	// private LoadingDialog dialog;

	private static final String IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpg";
	private Uri imageUri;// to store the big bitmap
	public String upImgJson = "";
	
	long endRequestTime;
	long startRequestTime;
	//接收自动登录发送的消息，关闭
	public static Handler loginHandler;
	private LoadingDialog loadingDialog;
	private SyncTimeLoadingDialog syncTimeloadingDialog;
	private DialogTool dialogTool ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		state = (TextView) this.findViewById(R.id.main_state);
		// stateL = (LinearLayout) this.findViewById(R.id.main_user_info);
		stateL = (LinearLayout) this.findViewById(R.id.main_user_info_detail);
		desc = (TextView) this.findViewById(R.id.main_state_desc);
		matchL = (LinearLayout) this.findViewById(R.id.main_fun_macth);
		start = (ImageView) this.findViewById(R.id.main_start);
		headv = (ImageView) this.findViewById(R.id.main_head);
		// 初始化数字符号
		initSymbol();
		dialogTool = new DialogTool(this);
		recording = (LinearLayout) this.findViewById(R.id.main_fun_recording);
		stateL.setOnClickListener(this);
		recording.setOnClickListener(this);
		matchL.setOnClickListener(this);
		start.setOnTouchListener(this);
		headv.setOnTouchListener(this);
		if (Variables.gpsStatus == 2) {
//			DialogTool dialog = new DialogTool(this);
			dialogTool.alertGpsTip2();
			if (Variables.switchVoice == 0) {
				YaoPao01App.palyOpenGps();
			}
		}

		imageUri = Uri.parse(IMAGE_FILE_LOCATION);
		
		loginHandler  = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					initLayout();
					if (loadingDialog!=null) {
						loadingDialog.dismiss();
						prepare4match();
					}
				} 
				super.handleMessage(msg);
			}
		};
		
		checkLogin();
		this.initView();
		

		/**
		 * 测试代码 加载赛道数据，调用jts方法
		 */
		// LonLatEncryption lonLatEncryption = new LonLatEncryption();
		// TrackData trackData = new TrackData();
		// trackData.read("DaXingTest2.properties");
		// GpsPoint point =new GpsPoint(116.321122,39.751262);
		// boolean isInTrack =
		// trackData.isInTheTracks(lonLatEncryption.encrypt(point).lon,lonLatEncryption.encrypt(point).lat);
		// Log.v("wytrack",
		// "isInTrack ="+isInTrack+" claimedLength="+trackData.claimedLength+" name"+trackData.name+" pgTracks"+trackData.pgTracks);
		//

		// YaoPao01App.matchOneKmAndNotInTakeOver();
		// YaoPao01App.matchOneKmTeam();
		// YaoPao01App.matchRunningInTakeOver();
		// YaoPao01App.matchRunningTransmitRelay();
		// YaoPao01App.matchDeviateTrack();
		// YaoPao01App.matchReturnTrack();
		// YaoPao01App.matchWaitGetRelay();
	}
	void prepare4match(){//登陆成功准备比赛
		CNAppDelegate.loginSucceedAndNext = false;
		if(CNAppDelegate.gstate == 2){//已经结束比赛了且时间在比赛进行中
			CNAppDelegate.hasFinishTeamMatch = true;
        }else{
            if(CNAppDelegate.isMatch == 1){
                doRequest_checkServerTime();
                syncTimeloadingDialog = new SyncTimeLoadingDialog(this);
//              syncTimeloadingDialog.setCancelable(false);
                syncTimeloadingDialog.show();
                
            }
        }
	}
	void doRequest_checkServerTime(){
		startRequestTime = CNAppDelegate.getNowTime1000();
		Log.v("zc","startRequestTime is "+startRequestTime);
		new CheckServerTimeTask().execute("");
	}
	public static int px2dip(Context context, int pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	// 检查用户是否在其他设备上登录
	private void checkLogin() {
		if (Variables.islogin == 3) {
			// dialog = new DialogTool(this);
			dialogTool.alertLoginOnOther();
			Variables.islogin = 0;
			return;
		}else if(Variables.islogin == 2){
			loadingDialog = new LoadingDialog(this);
			loadingDialog.setCancelable(false);
			loadingDialog.show();
			
		}
	}

	// 初始化总次数
	private void initCountView(DataBean data) {
		ImageView c1v = (ImageView) findViewById(R.id.main_count_num1);
		ImageView c2v = (ImageView) findViewById(R.id.main_count_num2);
		ImageView c3v = (ImageView) findViewById(R.id.main_count_num3);
		c1v.setVisibility(View.GONE);
		c2v.setVisibility(View.GONE);
		initCount(new ImageView[] { c1v, c2v, c3v }, data);
	}

	private void initCount(ImageView[] views, DataBean data) {
		int count = data.getCount();
		Log.v("wysport", " count=" + count);
		int c1 = count / 100;
		int c2 = (count % 100) / 10;
		int c3 = count % 10;
		if (c1 > 0) {
			views[0].setVisibility(View.VISIBLE);
			// updateMiddleData(c1, views[0]);
		}
		if (c2 > 0) {
			views[1].setVisibility(View.VISIBLE);
		}
		YaoPao01App.graphicTool.updateWhiteNum(new int[] { c1, c2, c3 }, views);
	}

	// 初始化平均配速
	private void initPspeed(DataBean data) {

		int[] speed = YaoPao01App.cal((int) (data.getPspeed()));
		// int[] speed = YaoPao01App.cal((int) (200));

		int s1 = speed[1] / 10;
		int s2 = speed[1] % 10;
		int s3 = speed[2] / 10;
		int s4 = speed[2] % 10;
		ImageView s1V = (ImageView) findViewById(R.id.match_recoding_speed1);
		ImageView s2V = (ImageView) findViewById(R.id.match_recoding_speed2);
		ImageView s3V = (ImageView) findViewById(R.id.match_recoding_speed3);
		ImageView s4V = (ImageView) findViewById(R.id.match_recoding_speed4);

		YaoPao01App.graphicTool.updateWhiteNum(new int[] { s1, s2, s3, s4 },
				new ImageView[] { s1V, s2V, s3V, s4V });

	}

	// 初始化积分
	private void initPoints(DataBean data) {
		int point = data.getPoints();
		int p1 = (int) point / 100000;
		int p2 = (int) (point % 100000) / 10000;
		int p3 = (int) (point % 10000) / 1000;
		int p4 = (int) (point % 1000) / 100;
		int p5 = (int) (point % 100) / 10;
		int p6 = (int) (point % 10);

		ImageView p1V = (ImageView) this.findViewById(R.id.main_point_num1);
		ImageView p2V = (ImageView) this.findViewById(R.id.main_point_num2);
		ImageView p3V = (ImageView) this.findViewById(R.id.main_point_num3);
		ImageView p4V = (ImageView) this.findViewById(R.id.main_point_num4);
		ImageView p5V = (ImageView) this.findViewById(R.id.main_point_num5);
		ImageView p6V = (ImageView) this.findViewById(R.id.main_point_num6);

		if (p1 > 0) {
			p1V.setVisibility(View.VISIBLE);
		}
		if (p2 > 0) {
			p2V.setVisibility(View.VISIBLE);
		}
		if (p3 > 0) {
			p3V.setVisibility(View.VISIBLE);
		}
		if (p4 > 0) {
			p4V.setVisibility(View.VISIBLE);
		}
		if (p5 > 0) {
			p5V.setVisibility(View.VISIBLE);
		}

		YaoPao01App.graphicTool.updateWhiteNum(new int[] { p1, p2, p3, p4, p5,
				p6 }, new ImageView[] { p1V, p2V, p3V, p4V, p5V, p6V });

	}

	@Override
	protected void onResume() {
		super.onResume();
		initLayout();
		MobclickAgent.onResume(this);
		if(CNAppDelegate.loginSucceedAndNext){
			prepare4match();
		}
		if(CNAppDelegate.canStartButNotInStartZone){
			alertNotInTakeOver();
		}
		//alertNotInTakeOver();
		super.activityOnFront=this.getClass().getSimpleName();
		Variables.activityOnFront=this.getClass().getSimpleName();
		
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		if (dialogTool!=null) {
			if (dialogTool.dialog!=null) {
				dialogTool.dialog.dismiss();
			}
		}
	}

	private void initLayout() {
		DataBean data = YaoPao01App.db.queryData();
		if (Variables.islogin == 1) {
			JSONObject userInfo = Variables.userinfo;
			if (userInfo != null) {
				if (!"".equals(userInfo.getString("nickname"))
						&& userInfo.getString("nickname") != null) {
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
			if (head != null) {
				headv.setImageBitmap(null);
			}
			state.setText("未登录");
		}
		initMileage(data);
		initCountView(data);
		initPspeed(data);
		initPoints(data);
	}

	private void initMileage(DataBean data) {
		distance = data.getDistance();
		// distance = 549254;
		ImageView d1V = (ImageView) this.findViewById(R.id.main_milage_num1);
		ImageView d2V = (ImageView) this.findViewById(R.id.main_milage_num2);
		ImageView d3V = (ImageView) this.findViewById(R.id.main_milage_num3);
		ImageView d4V = (ImageView) this.findViewById(R.id.main_milage_num4);
		ImageView d5V = (ImageView) this.findViewById(R.id.main_milage_dec1);
		ImageView d6V = (ImageView) this.findViewById(R.id.main_milage_dec2);
		d1V.setVisibility(View.GONE);
		d2V.setVisibility(View.GONE);
		d3V.setVisibility(View.GONE);
		int d1 = (int) distance / 1000000;
		int d2 = (int) (distance % 1000000) / 100000;
		int d3 = (int) (distance % 100000) / 10000;
		int d4 = (int) (distance % 10000) / 1000;
		int d5 = (int) (distance % 1000) / 100;
		int d6 = (int) (distance % 100) / 10;
		if (d1 > 0) {
			d1V.setVisibility(View.VISIBLE);
		}
		if (d2 > 0) {
			d2V.setVisibility(View.VISIBLE);
		}
		if (d3 > 0) {
			d3V.setVisibility(View.VISIBLE);
		}
		YaoPao01App.graphicTool.updateRedNum(
				new int[] { d1, d2, d3, d4, d5, d6 }, new ImageView[] { d1V,
						d2V, d3V, d4V, d5V, d6V });
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
				if (Variables.isTest) {
					// 测试代码
					Intent mainIntent = new Intent(MainActivity.this,
							SportSetActivity.class);
					startActivity(mainIntent);
					// 测试代码
				} else {
					if (Variables.gpsStatus == 2) {
//						DialogTool dialogTool = new DialogTool(MainActivity.this);
						dialogTool.alertGpsTip2();
						if (Variables.switchVoice == 0) {
							YaoPao01App.palyOpenGps();
						}
					} else if (Variables.gpsStatus == 0) {
//						DialogTool dialog = new DialogTool(MainActivity.this);
						dialogTool.alertGpsTip1();
						if (Variables.switchVoice == 0) {
							YaoPao01App.palyWeekGps();
						}

					} else if (Variables.gpsStatus == 1) {
						Intent mainIntent = new Intent(MainActivity.this,
								SportSetActivity.class);
						startActivity(mainIntent);
					}
				}
				break;
			}
			break;
		case R.id.main_head:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				if (Variables.islogin == 0) {
					Intent mainIntent = new Intent(MainActivity.this,
							LoginActivity.class);
					startActivity(mainIntent);
				} else if (Variables.islogin == 1) {
					// showSetPhotoDialog();
					Variables.toUserInfo = 0;
					Intent userIntent = new Intent(MainActivity.this,
							UserInfoActivity.class);
					startActivity(userIntent);
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
	void gotoJSPage(){
		Intent teamIntent = new Intent(MainActivity.this,
				WebViewActivity.class);
		teamIntent.putExtra("net.yaopao.activity.PageUrl",
				"team_index.html");
		startActivity(teamIntent);
	}
	void shouldStartButNot(){
	    Toast.makeText(this, "你未能正常开始比赛，请重新登录", Toast.LENGTH_LONG).show();
	    CNAppDelegate.match_isLogin = 0;
	    
	    Intent intent = new Intent(this,LoginActivity.class);
		Variables.islogin=3;
		DataTool.setUid(0);
		Variables.headUrl="";
		if (Variables.avatar!=null) {
			Variables.avatar=null;
		}
		Variables.userinfo = null;
		Variables.matchinfo = null;
		startActivity(intent);
	}
	void gotoScorePage(){
		Intent intent = new Intent(this,
				MatchGroupInfoActivity.class);
		intent.putExtra("from", "main");
		startActivity(intent);
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.main_user_info_detail:
			Intent userIntent;
			if (Variables.islogin == 1) {
				Variables.toUserInfo = 0;
				userIntent = new Intent(MainActivity.this,
						UserInfoActivity.class);
			} else {
				userIntent = new Intent(MainActivity.this, LoginActivity.class);
			}

			startActivity(userIntent);
			break;
		case R.id.main_fun_recording:
			Intent recIntent = new Intent(MainActivity.this,
					SportListActivity.class);
			MainActivity.this.startActivity(recIntent);
			break;

		case R.id.main_fun_macth:
			// 24小时比赛跳转页面判定,chenxy add
			// 先判断比赛是否开始了,没开始都进web页面,
			// 如果比赛开始了/结束了,登录了进本地比赛页面,没登录进web页面
			
			
			if(CNAppDelegate.match_isLogin == 0){//如果没登录，进宣宇界面
                gotoJSPage();
            }else{//登录了
                String matchstage = CNAppDelegate.getMatchStage();
                if(matchstage.equals("beforeMatch")){//赛前5分钟还要之前
                    gotoJSPage();
                }else if(matchstage.equals("closeToMatch")){//赛前5分钟到比赛正式开始
                    if(CNAppDelegate.isMatch == 1){//参赛
                        shouldStartButNot();
                    }else{
                        gotoJSPage();
                    }
                }else if(matchstage.equals("isMatching")){//正式比赛时间
                    if(CNAppDelegate.isMatch == 1){//参赛
                        if(CNAppDelegate.hasFinishTeamMatch){//提前结束比赛了
                            gotoScorePage();
                        }else{
                            shouldStartButNot();
                        }
                    }else{
                        gotoJSPage();
                    }
                }else{//赛后
                    if(CNAppDelegate.isMatch == 1){//参赛
                        gotoScorePage();
                    }else{
                        gotoJSPage();
                    }
                }
            }

			break;
		case R.id.main_setting:
			Intent settingIntent = new Intent(MainActivity.this,
					MainSettingActivity.class);
			startActivity(settingIntent);
			break;
		case R.id.main_message_layout:
			Intent messageIntent = null;
			if (Variables.islogin == 1) {
				messageIntent = new Intent(MainActivity.this,
						WebViewActivity.class);
				messageIntent.putExtra("net.yaopao.activity.PageUrl",
						"message_index.html");

			} else {
				Toast.makeText(MainActivity.this,
						"您必须注册并登录，才会收到系统消息，所以现在没有系统消息哦", Toast.LENGTH_LONG)
						.show();
				messageIntent = new Intent(MainActivity.this,
						RegisterActivity.class);
			}
			startActivity(messageIntent);
			break;
		default:
			break;
		}

	}


	private void initSymbol() {
		ImageView dot = (ImageView) this.findViewById(R.id.main_milage_dot);
		ImageView min = (ImageView) this
				.findViewById(R.id.match_recoding_speed_d1);
		ImageView sec = (ImageView) this
				.findViewById(R.id.match_recoding_speed_d2);
		ImageView km = (ImageView) this.findViewById(R.id.main_milage_km);
		dot.setImageBitmap(YaoPao01App.graphicTool.numBitmap
				.get(R.drawable.r_dot));
		min.setImageBitmap(YaoPao01App.graphicTool.numBitmap
				.get(R.drawable.w_min));
		sec.setImageBitmap(YaoPao01App.graphicTool.numBitmap
				.get(R.drawable.w_sec));
		// colon.setImageBitmap(YaoPao01App.graphicTool.numBitmap.get(R.drawable.w_colon));
		km.setImageBitmap(YaoPao01App.graphicTool.numBitmap
				.get(R.drawable.r_km));
	}
	
	
	
	private class CheckServerTimeTask extends AsyncTask<String, Void, Boolean> {
		private String responseJson;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
		    responseJson = NetworkHandler.httpPost(Constants.endpoints	+ Constants.checkServerTime, "");
		    Log.v("zc","检查服务器时间返回responseJson is "+responseJson);
			if (responseJson != null && !"".equals(responseJson)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				JSONObject resultDic = JSON.parseObject(responseJson);
				long serverTime = resultDic.getLongValue("systime");
				Log.v("zc","serverTime is "+serverTime);
                endRequestTime = CNAppDelegate.getNowTime1000();
                Log.v("zc","endRequestTime is "+endRequestTime);
                Log.v("zc","endRequestTime-startRequestTime is "+(endRequestTime-startRequestTime));
                if(endRequestTime-startRequestTime < CNAppDelegate.kShortTime){
                    //如果时间满足条件，则delataTime取值确定
                    int deltaTime1000 = (int)(serverTime-(startRequestTime+endRequestTime)/2);//取得毫秒数
                    Log.v("zc","deltaTime1000 is "+deltaTime1000);
                    CNAppDelegate.deltaTime = (deltaTime1000+500)/1000;
                    Log.v("zc","CNAppDelegate.deltaTime is "+CNAppDelegate.deltaTime);
                    CNAppDelegate.hasCheckTimeFromServer = true;
                    CloseCheckTime();
                }else{
                	//失败了两秒后再请求
                    new Handler().postDelayed(new Runnable(){  
            	        public void run() {  
            	        	//execute the task
            	        	doRequest_checkServerTime();
            	        }  
            	     }, 2000); 
                }
			} else {
				
			}
		}

	}
	
	
	
	void CloseCheckTime(){
		if (syncTimeloadingDialog!=null) {
			syncTimeloadingDialog.dismiss();
		}
		if(CNAppDelegate.whatShouldIdo() == 100){
			alertNotInTakeOver();
		}
	}
	//不在出发区弹框
	private void alertNotInTakeOver(){
		dialogTool.alertNotIntakeOver();
	}
}
