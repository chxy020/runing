package net.yaopao.activity;

import net.yaopao.assist.Variables;
import net.yaopao.widget.OnChangedListener;
import net.yaopao.widget.SlipButton;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SportSetActivity extends Activity implements OnTouchListener,
		OnChangedListener {
	TextView backV;
	TextView targetV;
	TextView typeV;
	TextView startV;
	SlipButton timeSwitch;
	SlipButton voiceSwitch;
	ImageView tarIconV;
	ImageView typeIconV;
	
	LinearLayout targetL;
	LinearLayout typeL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sport_set);
		backV = (TextView) this.findViewById(R.id.sport_set_goback);
		targetV = (TextView) this.findViewById(R.id.sport_set_target_select);
		targetL = (LinearLayout) this.findViewById(R.id.sport_set_target);
		typeV = (TextView) this.findViewById(R.id.sport_set_type_select);
		typeL = (LinearLayout) this.findViewById(R.id.sport_set_type);
		startV = (TextView) this.findViewById(R.id.sport_set_start);
		
		tarIconV = (ImageView) this.findViewById(R.id.sport_set_target_icon);
		typeIconV = (ImageView) this.findViewById(R.id.sport_set_type_icon);
		
		timeSwitch = (SlipButton) this.findViewById(R.id.sport_set_time_switch);
		
		voiceSwitch = (SlipButton) this
				.findViewById(R.id.sport_set_voice_switch);

		timeSwitch.SetOnChangedListener(new OnChangedListener() {

			@Override
			public void OnChanged(boolean CheckState) {
				
				
				if (CheckState) {
					Variables.switchTime = 0;//开
				} else {
					Variables.switchTime = 1;//关
				}

			}
		});// 设置事件监听
		voiceSwitch.SetOnChangedListener(new OnChangedListener() {

			@Override
			public void OnChanged(boolean CheckState) {
				if (CheckState) {
					Variables.switchVoice = 0;
				} else {
					Variables.switchVoice = 1;
				}

			}
		});// 设置事件监听

		backV.setOnTouchListener(this);
		targetL.setOnTouchListener(this);
		typeL.setOnTouchListener(this);
		startV.setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {

		case R.id.sport_set_goback:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				SportSetActivity.this.finish();
				break;
			}
			break;

		case R.id.sport_set_target:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				Intent myIntent = new Intent();
				myIntent = new Intent(SportSetActivity.this,SportTargetActivity.class);
				startActivity(myIntent);
			}
			break;
		case R.id.sport_set_type:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				Intent myIntent = new Intent();
				myIntent = new Intent(SportSetActivity.this,SportTypeActivity.class);
				startActivity(myIntent);
			}
			break;
		case R.id.sport_set_start:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				YaoPao01App.db.saveSportParam();
				Intent myIntent = new Intent();
				if (Variables.switchTime == 0) {
					myIntent = new Intent(SportSetActivity.this,
							SportCountdownActivity.class);
					startActivityForResult(myIntent, 103);
				} else {
					myIntent = new Intent(SportSetActivity.this,
							SportRecordActivity.class);
					startActivity(myIntent);
				}

			}
			break;
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		initLayout();
	}

	private void initLayout() {
		// 检测开关状态
				switch (Variables.switchTime) {
				case 0:
					timeSwitch.setState(true);
					break;
				case 1:
					timeSwitch.setState(false);
					break;
				}
				switch (Variables.switchVoice) {
				case 0:
					voiceSwitch.setState(true);
					break;
				case 1:
					voiceSwitch.setState(false);
					break;
				}
				//检测运动目标
				switch (Variables.runtar) {
				case 0:
					targetV.setText("自由");
					tarIconV.setBackgroundResource(R.drawable.target_free);
					break;
				case 1:
					targetV.setText("距离");
					tarIconV.setBackgroundResource(R.drawable.target_dis);
					break;
				case 2:
					targetV.setText("计时");
					tarIconV.setBackgroundResource(R.drawable.target_time);
					break;

				default:
					targetV.setText("自由");
					tarIconV.setBackgroundResource(R.drawable.target_free);
					Variables.runtar=0;
					break;
				}
				//检测运动类型
				switch (Variables.runty) {
				case 1:
					typeV.setText("步行");
					typeIconV.setBackgroundResource(R.drawable.runtype_walk);
					break;
				case 2:
					typeV.setText("跑步");
					typeIconV.setBackgroundResource(R.drawable.runtype_run);
					break;
				case 3:
					typeV.setText("自行车骑行");
					typeIconV.setBackgroundResource(R.drawable.runtype_ride);
					break;
				default:
					typeV.setText("步行");
					Variables.runty=1;
					typeIconV.setBackgroundResource(R.drawable.runtype_walk);
					break;
				}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void OnChanged(boolean CheckState) {

	}

}
