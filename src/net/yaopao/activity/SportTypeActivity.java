package net.yaopao.activity;

import net.yaopao.assist.Variables;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

public class SportTypeActivity extends BaseActivity implements OnClickListener {
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
		runV = (RelativeLayout) this.findViewById(R.id.type_run);
		rideV = (RelativeLayout) this.findViewById(R.id.type_ride);

		walkImgV = (ImageView) this.findViewById(R.id.type_step_select_icon);
		runImgV = (ImageView) this.findViewById(R.id.type_run_select_icon);
		rideImgV = (ImageView) this.findViewById(R.id.type_bike_select_icon);

		backV.setOnClickListener(this);
		walkV.setOnClickListener(this);
		runV.setOnClickListener(this);
		rideV.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {

		case R.id.type_goback:
			case MotionEvent.ACTION_UP:
				SportTypeActivity.this.finish();
				break;
		case R.id.type_run:
				Variables.runType = 1;
				YaoPao01App.runManager.setHowToMove(1);
				walkImgV.setBackgroundResource(0);
				runImgV.setBackgroundResource(R.drawable.check);
				rideImgV.setBackgroundResource(0);
				break;
		case R.id.type_walk:
			
				Variables.runType = 2;
			YaoPao01App.runManager.setHowToMove(2);
				walkImgV.setBackgroundResource(R.drawable.check);
				runImgV.setBackgroundResource(0);
				rideImgV.setBackgroundResource(0);
			break;
		case R.id.type_ride:
				Variables.runType = 3;
			    YaoPao01App.runManager.setHowToMove(3);
				walkImgV.setBackgroundResource(0);
				runImgV.setBackgroundResource(0);
				rideImgV.setBackgroundResource(R.drawable.check);

			break;
		}
	}

	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);
		switch (YaoPao01App.runManager.getHowToMove()) {
		case 1:
			walkImgV.setBackgroundResource(0);
			runImgV.setBackgroundResource(R.drawable.check);
			rideImgV.setBackgroundResource(0);
			break;
		case 2:
			walkImgV.setBackgroundResource(R.drawable.check);
			runImgV.setBackgroundResource(0);
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
		super.activityOnFront=this.getClass().getSimpleName();
		Variables.activityOnFront=this.getClass().getSimpleName();
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
