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
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {

		case R.id.target_goback:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				SportTargetActivity.this.finish();
				break;
			}
			break;
		case R.id.target_type_free:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
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
				break;
			case MotionEvent.ACTION_UP:
				Variables.runtar = 1;
				freeImgV.setBackgroundResource(0);
				distanceImgV.setBackgroundResource(R.drawable.check);
				timeImgV.setBackgroundResource(0);
				final Handler handler = new Handler() {
					public void handleMessage(Message msg) {
						// distance = msg.getData().getString("distance");
						double runtarDis = Double.parseDouble(msg.getData()
								.getString("distance"));
						
						Variables.runtarDis = (int) (runtarDis*1000);
					}
				};
				// 实例化SelectPicPopupWindow
				distanceWindow = new SelectDistance(SportTargetActivity.this,
						handler);
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
				break;
			case MotionEvent.ACTION_UP:
				Variables.runtar = 2;
				freeImgV.setBackgroundResource(0);
				distanceImgV.setBackgroundResource(0);
				timeImgV.setBackgroundResource(R.drawable.check);
				final Handler handler = new Handler() {
					public void handleMessage(Message msg) {
						Variables.runtarTime =msg.getData().getInt("time");
						Log.v("wysport", "set time = "+Variables.runtarTime);
						super.handleMessage(msg);
					}
				};
				// 实例化SelectPicPopupWindow
				timeWindow = new SelectTime(SportTargetActivity.this, handler);
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
		distanceTxtV.setText(Variables.runtarDis + " 千米");
		// }
		// Log.v("wydb", "time2 ="+time);
		// if (time!=null) {
		timeTxtV.setText(Variables.runtarTime + " 分钟");
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

}
