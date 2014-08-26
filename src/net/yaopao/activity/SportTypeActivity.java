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

public class SportTypeActivity extends Activity implements OnTouchListener {
	TextView backV;
	RelativeLayout walkV;
	RelativeLayout runV;
	RelativeLayout rideV;

	ImageView walkImgV;
	ImageView runImgV;
	ImageView rideImgV;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sport_type);
		backV = (TextView) this.findViewById(R.id.type_goback);
		walkV = (RelativeLayout) this.findViewById(R.id.type_walk);
		runV = (RelativeLayout) this
				.findViewById(R.id.type_run);
		rideV = (RelativeLayout) this.findViewById(R.id.type_ride);


		walkImgV = (ImageView) this.findViewById(R.id.type_step_select_icon);
		runImgV = (ImageView) this
				.findViewById(R.id.type_run_select_icon);
		rideImgV = (ImageView) this.findViewById(R.id.type_bike_select_icon);

		backV.setOnTouchListener(this);
		walkV.setOnTouchListener(this);
		runV.setOnTouchListener(this);
		rideV.setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {

		case R.id.type_goback:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				SportTypeActivity.this.finish();
				break;
			}
			break;
		case R.id.type_walk:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				Variables.runty=1;
				walkImgV.setBackgroundResource(R.drawable.check);
				runImgV.setBackgroundResource(0);
				rideImgV.setBackgroundResource(0);
				break;
			}
			break;
		case R.id.type_run:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				Variables.runty=2;
				walkImgV.setBackgroundResource(0);
				runImgV.setBackgroundResource(R.drawable.check);
				rideImgV.setBackgroundResource(0);
				break;
			}
			break;
		case R.id.type_ride:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				Variables.runty=3;
				walkImgV.setBackgroundResource(0);
				runImgV.setBackgroundResource(0);
				rideImgV.setBackgroundResource(R.drawable.check);

				break;
			}
			break;
		}
		return true;
	}

	@Override
	protected void onResume() {
		
		switch (Variables.runty) {
		case 1:
			walkImgV.setBackgroundResource(R.drawable.check);
			runImgV.setBackgroundResource(0);
			rideImgV.setBackgroundResource(0);
			break;
		case 2:
			walkImgV.setBackgroundResource(0);
			runImgV.setBackgroundResource(R.drawable.check);
			rideImgV.setBackgroundResource(0);
			break;
		case 3:
			walkImgV.setBackgroundResource(0);
			runImgV.setBackgroundResource(0);
			rideImgV.setBackgroundResource(R.drawable.check);
			break;

		default:
			break;
		}
		
		
		super.onResume();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
