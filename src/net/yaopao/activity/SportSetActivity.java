package net.yaopao.activity;

import net.yaopao.assist.Variables;
import net.yaopao.widget.OnChangedListener;
import net.yaopao.widget.SlipButton;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SportSetActivity extends Activity implements OnClickListener,
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
					Variables.switchTime = 0;// 开
				} else {
					Variables.switchTime = 1;// 关
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

		backV.setOnClickListener(this);
		targetL.setOnClickListener(this);
		typeL.setOnClickListener(this);
		startV.setOnClickListener(this);
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
		// 检测运动目标
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
			Variables.runtar = 0;
			break;
		}
		// 检测运动类型
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
			Variables.runty = 1;
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

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.sport_set_goback:
				SportSetActivity.this.finish();
			break;

		case R.id.sport_set_target:
				Intent targetIntent = new Intent();
				targetIntent = new Intent(SportSetActivity.this,
						SportTargetActivity.class);
				startActivity(targetIntent);
			break;
		case R.id.sport_set_type:
				Intent typeIntent = new Intent();
				typeIntent = new Intent(SportSetActivity.this,
						SportTypeActivity.class);
				startActivity(typeIntent);
			break;
		case R.id.sport_set_start:
				YaoPao01App.db.saveSportParam();
				Intent startIntent = new Intent();
				if (Variables.switchTime == 0) {
					startIntent = new Intent(SportSetActivity.this,
							SportCountdownActivity.class);
					startActivityForResult(startIntent, 103);
					SportSetActivity.this.finish();
				} else {
					startIntent = new Intent(SportSetActivity.this,
							SportRecordActivity.class);
					startActivity(startIntent);
					SportSetActivity.this.finish();
				}
			break;
		default:
			break;
		}
	}

}
