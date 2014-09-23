package net.yaopao.activity;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import net.yaopao.assist.Constants;
import net.yaopao.assist.DataTool;
import net.yaopao.assist.DialogTool;
import net.yaopao.assist.LoadingDialog;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;
import net.yaopao.bean.DataBean;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
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
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;

public class MainActivity extends BaseActivity implements OnTouchListener,OnClickListener{
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
	private LoadingDialog dialog;
	
	private static final String IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpg";
	private Uri imageUri;//to store the big bitmap
	public String upImgJson = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		state = (TextView) this.findViewById(R.id.main_state);
//		stateL = (LinearLayout) this.findViewById(R.id.main_user_info);
		stateL = (LinearLayout) this.findViewById(R.id.main_user_info_detail);
		desc = (TextView) this.findViewById(R.id.main_state_desc);
		matchL = (LinearLayout) this.findViewById(R.id.main_fun_macth);
		start = (ImageView) this.findViewById(R.id.main_start);
		headv = (ImageView) this.findViewById(R.id.main_head);
		recording = (LinearLayout) this.findViewById(R.id.main_fun_recording);
		stateL.setOnClickListener(this);
		recording.setOnClickListener(this);
		matchL.setOnClickListener(this);
		start.setOnTouchListener(this);
		headv.setOnTouchListener(this);
		if (Variables.gpsStatus==2) {
			DialogTool dialog = new DialogTool(MainActivity.this,handler);
			WindowManager m = getWindowManager();
			Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
			dialog.alertGpsTip2(d);
			if (Variables.switchVoice == 0) {
			YaoPao01App.palyOpenGps();
			}
		}
		this.initView();
		checkLogin();
		dialog = new LoadingDialog(this);
		imageUri = Uri.parse(IMAGE_FILE_LOCATION);
		 
	}
	
	 public static int px2dip(Context context, int pxValue) {
         final float scale = context.getResources().getDisplayMetrics().density;
         return (int) (pxValue / scale + 0.5f);
 }
	//检查用户是否在其他设备上登录
	private void checkLogin() {
		if (Variables.islogin ==3) {
//			Intent mainIntent = new Intent(MainActivity.this,
//					LoginActivity.class);
//			Toast.makeText(this, "此用户已在其他设备上登录，请重新登录", Toast.LENGTH_LONG).show();
//			startActivity(mainIntent);
				DialogTool dialog = new DialogTool(MainActivity.this,null);
				WindowManager m = getWindowManager();
				Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
				dialog.alertLoginOnOther(d);
				Variables.islogin =0;
			return;
		}
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
			updateMiddleData(c1, views[0]);
		}
		if (c2 > 0) {
			views[1].setVisibility(View.VISIBLE);
			updateMiddleData(c2, views[1]);
		}
		updateMiddleData(c3, views[2]);
	}
	//初始化平均配速
	private void initPspeed(DataBean data) {
		
		int[] speed = YaoPao01App.cal((int) (data.getPspeed()));
//		int[] speed = YaoPao01App.cal((int) (200));
		
		int s1 = speed[1] / 10;
		int s2 = speed[1] % 10;
		int s3 = speed[2] / 10;
		int s4 = speed[2] % 10;
		ImageView s1V = (ImageView) findViewById(R.id.match_recoding_speed1);
		ImageView s2V = (ImageView) findViewById(R.id.match_recoding_speed2);
		ImageView s3V = (ImageView) findViewById(R.id.match_recoding_speed3);
		ImageView s4V = (ImageView) findViewById(R.id.match_recoding_speed4);
		  
		updateMiddleData(s1, s1V);
		updateMiddleData(s2, s2V);
		updateMiddleData(s3, s3V);
		updateMiddleData(s4, s4V);
		
	}
	//初始化积分
	private void initPoints(DataBean data) {
		int point = data.getPoints();
		int p1 = (int) point / 100000;
		int p2 = (int) (point % 100000) / 10000;
		int p3 = (int) (point % 10000) / 1000;
		int p4 = (int) (point % 1000) / 100;
		int p5 = (int) (point % 100) / 10;
		int p6 = (int) (point % 10);
		
		ImageView p1v = (ImageView) this.findViewById(R.id.main_point_num1);
		ImageView p2v = (ImageView) this.findViewById(R.id.main_point_num2);
		ImageView p3v = (ImageView) this.findViewById(R.id.main_point_num3);
		ImageView p4v = (ImageView) this.findViewById(R.id.main_point_num4);
		ImageView p5v = (ImageView) this.findViewById(R.id.main_point_num5);
		ImageView p6v = (ImageView) this.findViewById(R.id.main_point_num6);
		
		if (p1 > 0) {
			p1v.setVisibility(View.VISIBLE);
		}
		if (p2 > 0) {
			p2v.setVisibility(View.VISIBLE);
		}
		if (p3 > 0) {
			p3v.setVisibility(View.VISIBLE);
		}
		if (p4 > 0) {
			p4v.setVisibility(View.VISIBLE);
		}
		if (p5 > 0) {
			p5v.setVisibility(View.VISIBLE);
		}
		updateMiddleData(p1, p1v);
		updateMiddleData(p2, p2v);
		updateMiddleData(p3, p3v);
		updateMiddleData(p4, p4v);
		updateMiddleData(p5, p5v);
		updateMiddleData(p6, p6v);
		
	}
	@Override
	protected void onResume() {
		super.onResume();
		initLayout();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	private void initLayout() {
		DataBean data = YaoPao01App.db.queryData();
		if (Variables.islogin == 1) {
			JSONObject userInfo = DataTool.getUserInfo();
			if (userInfo != null) {
				if (!"".equals(userInfo.getString("nickname"))&&userInfo.getString("nickname")!=null) {
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
		//distance = 549254;
		ImageView d1v = (ImageView) this.findViewById(R.id.main_milage_num1);
		ImageView d2v = (ImageView) this.findViewById(R.id.main_milage_num2);
		ImageView d3v = (ImageView) this.findViewById(R.id.main_milage_num3);
		ImageView d4v = (ImageView) this.findViewById(R.id.main_milage_num4);
		ImageView d5v = (ImageView) this.findViewById(R.id.main_milage_dec1);
		ImageView d6v = (ImageView) this.findViewById(R.id.main_milage_dec2);
		d1v.setVisibility(View.GONE);
		d2v.setVisibility(View.GONE);
		d3v.setVisibility(View.GONE);
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
	protected void updateMiddleData(int i, ImageView view) {
		if (i > 9) {
			i = i % 10;
		}
		switch (i) {
		case 0:
			view.setBackgroundResource(R.drawable.w_0);
			break;
		case 1:
			view.setBackgroundResource(R.drawable.w_1);
			break;
		case 2:
			view.setBackgroundResource(R.drawable.w_2);
			break;
		case 3:
			view.setBackgroundResource(R.drawable.w_3);
			break;
		case 4:
			view.setBackgroundResource(R.drawable.w_4);
			break;
		case 5:
			view.setBackgroundResource(R.drawable.w_5);
			break;
		case 6:
			view.setBackgroundResource(R.drawable.w_6);
			break;
		case 7:
			view.setBackgroundResource(R.drawable.w_7);
			break;
		case 8:
			view.setBackgroundResource(R.drawable.w_8);
			break;
		case 9:
			view.setBackgroundResource(R.drawable.w_9);
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
					DialogTool dialog = new DialogTool(MainActivity.this,handler);
					WindowManager m = getWindowManager();
					Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
					dialog.alertGpsTip2(d);
				if (Variables.switchVoice == 0) {
					YaoPao01App.palyOpenGps();
				}
				}else if (Variables.gpsStatus==0) {
					DialogTool dialog = new DialogTool(MainActivity.this,null);
					WindowManager m = getWindowManager();
					Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
					dialog.alertGpsTip1(d);
				if (Variables.switchVoice == 0) {
					YaoPao01App.palyWeekGps();
				}
					
				}else if(Variables.gpsStatus==1){
					Intent mainIntent = new Intent(MainActivity.this,
							SportSetActivity.class);
					startActivity(mainIntent);
				}
				//测试代码
//					Intent mainIntent = new Intent(MainActivity.this,
//							SportSetActivity.class);
//					startActivity(mainIntent);
				//测试代码
				break;
			}
			break;
		case R.id.main_head:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				showSetPhotoDialog();
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
		case R.id.main_user_info_detail:
			Intent userIntent;
			if (Variables.islogin == 1) {
				Variables.toUserInfo=0;
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
			/*这个有冲突不知道怎么解决,
			 Intent intent= new Intent();        
			    intent.setAction("android.intent.action.VIEW");    
			    Uri content_url = Uri.parse("http://www.yaopao.net/html/ssxx.html");   
			    intent.setData(content_url);  
			 Intent intent= new Intent(MainActivity.this,MatchWebActivity.class);
			    startActivity(intent);
			*/
			
			//24小时比赛跳转页面判定,chenxy add
			//先判断比赛是否开始了,没开始都进web页面,
			//如果比赛开始了/结束了,登录了进本地比赛页面,没登录进web页面
			Intent teamIntent = null;
			
			String matchState = Variables.matchState;
			if("2" == matchState){
				//比赛未开始
				teamIntent = new Intent(MainActivity.this,WebViewActivity.class);
				teamIntent.putExtra("net.yaopao.activity.PageUrl","team_index.html");
			}
			else{
				//比赛开始了/结束了
				if (Variables.islogin == 1) {
					//登录了,跳转本地比赛页面,需要王雨添加逻辑
					
				} else {
					//未登录,跳转到web页面
					teamIntent = new Intent(MainActivity.this,WebViewActivity.class);
					teamIntent.putExtra("net.yaopao.activity.PageUrl","team_index.html");
				}
			}
			/*
			if (Variables.islogin == 1) {
				teamIntent = new Intent(MainActivity.this,WebViewActivity.class);
				teamIntent.putExtra("net.yaopao.activity.PageUrl","team_index.html");
				
			} else {
				Toast.makeText(MainActivity.this, "您必须注册并登录，才会收到系统消息，所以现在没有系统消息哦", Toast.LENGTH_LONG).show();
				teamIntent = new Intent(MainActivity.this, RegisterActivity.class);
			}
			*/
			startActivity(teamIntent);
			break;
		case R.id.main_setting:
			Intent settingIntent = new Intent(MainActivity.this,
					MainSettingActivity.class);
			startActivity(settingIntent);
			break;
		case R.id.main_message_layout:
			Intent messageIntent=null;
			if (Variables.islogin == 1) {
				messageIntent= new Intent(MainActivity.this,
						WebViewActivity.class);
				messageIntent.putExtra("net.yaopao.activity.PageUrl",
						"message_index.html");
				
			} else {
				Toast.makeText(MainActivity.this, "您必须注册并登录，才会收到系统消息，所以现在没有系统消息哦", Toast.LENGTH_LONG).show();
				messageIntent = new Intent(MainActivity.this, RegisterActivity.class);
			}
			startActivity(messageIntent);
			break;
		default:
			break;
		}

	}
	
	private void showSetPhotoDialog() {
		final String[] item_type = new String[] { "相机", "相册", "取消" };

		new AlertDialog.Builder(this).setTitle("选取来自").setIcon(R.drawable.icon_s).setItems(item_type, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Log.v("wycam", "which = "+which);
						switch (which) {
						case 0:
							goGetPhotoFromCamera();
							break;
						case 1:
							goGetPhotoFromGallery();
							break;
						}
					}
				}).show();
	}
	private void goGetPhotoFromCamera() {
	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
	startActivityForResult(intent, Constants.RET_CAMERA);
}
//
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, Constants.IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 640);
		intent.putExtra("outputY", 640);
		intent.putExtra("return-data", false);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, Constants.RET_CROP);
	}
//
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case Constants.RET_CAMERA:
			if (resultCode == Activity.RESULT_OK) {
				startPhotoZoom(imageUri);
			}
			break;
		case Constants.RET_CROP:
			if (resultCode == Activity.RESULT_OK) {
				doneGetPhotoFromCamera();
			}
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void goGetPhotoFromGallery() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 640);
		intent.putExtra("outputY", 640);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", false); // no face detection
		startActivityForResult(intent, Constants.RET_CROP);
	}

	private void doneGetPhotoFromCamera() {
		if(imageUri != null){
			Variables.avatar = decodeUriAsBitmap(imageUri);
		}
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			Variables.avatar.compress(Bitmap.CompressFormat.PNG, 100, stream);
			imageByte = stream.toByteArray();
			try {
				new upImgAsyncTask().execute("");
			} catch (Exception e) {
			}
			
	}
		private Bitmap decodeUriAsBitmap(Uri uri){
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
	private class upImgAsyncTask extends AsyncTask<String, Void, Boolean> {
		//
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
			upImgJson = NetworkHandler.upImg(1, "", imageByte);
			Log.v("wyuser", "imageByte="+imageByte.length);
			if (upImgJson != null && !"".equals(upImgJson)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			dialog.dismiss();
			if (result) {
				JSONObject rt = JSON.parseObject(upImgJson);
				if (rt != null && !"".equals(upImgJson)) {
					if (rt.getJSONObject("state").getInteger("code") == 0) {
						if (Variables.avatar!=null) {
							headv.setImageBitmap(Variables.avatar);
						}else if(rt.getJSONObject("state").getInteger("code") == -7){
							DialogTool dialog = new DialogTool(MainActivity.this,null);
							WindowManager m = getWindowManager();
							Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
							dialog.alertLoginOnOther(d);
							Variables.islogin=3;
						} else {
							Toast.makeText(MainActivity.this, "更新头像失败",
									Toast.LENGTH_LONG).show();
						}
						// mPhotoBmp.recycle();
						// mPhotoBmp=null;
					} else {
						Toast.makeText(MainActivity.this, "更新头像失败",
								Toast.LENGTH_LONG).show();
					}

				} else {
					Toast.makeText(MainActivity.this, "更新头像失败",
							Toast.LENGTH_LONG).show();
				}

			} else {
				Toast.makeText(MainActivity.this, "网络异常，请稍后重试",
						Toast.LENGTH_LONG).show();
			}
		}
	}
}
