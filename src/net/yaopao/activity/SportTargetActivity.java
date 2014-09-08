package net.yaopao.activity;

import net.yaopao.assist.Variables;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SportTargetActivity extends Activity implements OnTouchListener {
	TextView backV;
	TextView distanceTxtV;
	TextView timeTxtV;
	RelativeLayout freeV;
	RelativeLayout distanceV;
	RelativeLayout timeV;

	ImageView freeImgV;
	ImageView distanceImgV;
	ImageView timeImgV;

	// public static String distance;
	// public static String time;
	// int selected = 1;// 1-自由，2-距离，3-计时

	public SelectDistance distanceWindow;
	public SelectTime timeWindow;
	
	/** 运动距离 */
	private int distanceData = 0;
	/** 运动计时 */
	private int distanceTime = 30;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sport_target);
		backV = (TextView) this.findViewById(R.id.target_goback);
		freeV = (RelativeLayout) this.findViewById(R.id.target_type_free);
		distanceV = (RelativeLayout) this
				.findViewById(R.id.target_type_distance);
		timeV = (RelativeLayout) this.findViewById(R.id.target_type_time);

		distanceTxtV = (TextView) this
				.findViewById(R.id.target_distance_select);
		timeTxtV = (TextView) this.findViewById(R.id.target_time_select);

		freeImgV = (ImageView) this.findViewById(R.id.target_free_select_icon);
		distanceImgV = (ImageView) this
				.findViewById(R.id.target_distance_select_icon);
		timeImgV = (ImageView) this.findViewById(R.id.target_time_select_icon);

		backV.setOnTouchListener(this);
		freeV.setOnTouchListener(this);
		distanceV.setOnTouchListener(this);
		timeV.setOnTouchListener(this);
		
		//通过数据获取已保存的运动距离数据,没有的话给默认值
		distanceData = Variables.runtarDis;
		distanceTime = Variables.runtarTime;
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {

		case R.id.target_goback:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				backV.setBackgroundResource(R.color.red_h);
				break;
			case MotionEvent.ACTION_UP:
				backV.setBackgroundResource(R.color.red);
				SportTargetActivity.this.finish();
				break;
			}
			break;
		case R.id.target_type_free:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				freeV.setBackgroundResource(R.color.white_h);
				break;
			case MotionEvent.ACTION_UP:
				freeV.setBackgroundResource(R.color.white);
				Variables.runtar = 0;
				freeImgV.setBackgroundResource(R.drawable.check);
				distanceImgV.setBackgroundResource(0);
				timeImgV.setBackgroundResource(0);
				break;
			}
			break;
		case R.id.target_type_distance:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				distanceV.setBackgroundResource(R.color.white_h);
				break;
			case MotionEvent.ACTION_UP:
				distanceV.setBackgroundResource(R.color.white);
				Variables.runtar = 1;
				freeImgV.setBackgroundResource(0);
				distanceImgV.setBackgroundResource(R.drawable.check);
				timeImgV.setBackgroundResource(0);
				final Handler handler = new Handler() {
					public void handleMessage(Message msg) {
						// distance = msg.getData().getString("distance");
						double runtarDis = Double.parseDouble(msg.getData()
								.getString("distance"));
						distanceData = (int) (runtarDis);
						Variables.runtarDis = (int) (runtarDis);
					}
				};
				// 实例化SelectPicPopupWindow
				distanceWindow = new SelectDistance(SportTargetActivity.this,
						handler,distanceData);
				distanceWindow.showAtLocation(SportTargetActivity.this
						.findViewById(R.id.target_distance_select),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置

				// }
				break;
			}
			break;
		case R.id.target_type_time:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				timeV.setBackgroundResource(R.color.white_h);
				break;
			case MotionEvent.ACTION_UP:
				timeV.setBackgroundResource(R.color.white);
				Variables.runtar = 2;
				freeImgV.setBackgroundResource(0);
				distanceImgV.setBackgroundResource(0);
				timeImgV.setBackgroundResource(R.drawable.check);
				final Handler handler = new Handler() {
					public void handleMessage(Message msg) {
						Variables.runtarTime =msg.getData().getInt("time");
						distanceTime = msg.getData().getInt("time");
						Log.v("wysport", "set time = "+Variables.runtarTime);
						super.handleMessage(msg);
					}
				};
				// 实例化SelectPicPopupWindow
				timeWindow = new SelectTime(SportTargetActivity.this, handler,distanceTime);
				timeWindow.showAtLocation(SportTargetActivity.this
						.findViewById(R.id.target_time_select), Gravity.BOTTOM
						| Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
				break;
			}
			break;
		}
		return true;
	}

	@Override
	protected void onResume() {
		// if (distance!=null) {
		distanceTxtV.setText(Variables.runtarDis + "km");
		// }
		// Log.v("wydb", "time2 ="+time);
		// if (time!=null) {
		//timeTxtV.setText(Variables.runtarTime + "分钟");
		timeTxtV.setText(getTimeOfSeconds(Variables.runtarTime * 60) + "");
		// }

		switch (Variables.runtar) {
		case 0:
			freeImgV.setBackgroundResource(R.drawable.check);
			distanceImgV.setBackgroundResource(0);
			timeImgV.setBackgroundResource(0);
			break;
		case 1:
			freeImgV.setBackgroundResource(0);
			distanceImgV.setBackgroundResource(R.drawable.check);
			timeImgV.setBackgroundResource(0);
			break;
		case 2:
			freeImgV.setBackgroundResource(0);
			distanceImgV.setBackgroundResource(0);
			timeImgV.setBackgroundResource(R.drawable.check);
			break;

		default:
			break;
		}

		super.onResume();
	}

	private void initLayout() {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	public static String getTimeOfSeconds(int second){
		String time = "";
		int h = 0;
		int m = 0;
		int s = 0;
		if(second > 60){
			//判断是否大于1小时
			if(second > 60 && second < 3600){
				//不超过1小时
				m = (int)second / 60;
				s = second % 60;
				String tm = m >= 10 ? m + "" : "0" + m;
				String ts = s >= 10 ? s + "" : "0" + s;
				time = tm + ":" + ts;
			}
			else{
				//超过1小时
				h = (int)second / 3600;
				m = (int)second % 3600 / 60;
				s = second % 3600 % 60;
				//小时没加0,放不下
				String th = h > 10 ? h + "" : "" + h;
				String tm = m >= 10 ? m + "" : "0" + m;
				String ts = s >= 10 ? s + "" : "0" + s;
				time = th + ":" + tm + ":" + ts;
			}
		}
		else{
			//刚好1分钟
			if(60 == second){
				time = "01:00";
			}
			else{
				String ts = second >= 10 ? second + "" : "0" + second;
				time = "00:" + ts;
			}
		}
		return time;
	}
}
