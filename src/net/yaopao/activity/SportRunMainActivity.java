package net.yaopao.activity;

import java.util.Timer;
import java.util.TimerTask;

import net.yaopao.assist.DialogTool;
import net.yaopao.assist.Variables;
import net.yaopao.voice.PlayVoice;
import net.yaopao.widget.SliderRelativeLayout;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

@SuppressLint("HandlerLeak")
public class SportRunMainActivity extends BaseActivity implements
		OnTouchListener {

	private TextView sliderTextV;
	private TextView doneV;
	private TextView resumeV;
	private TextView processTextV;
	private TextView recodingTimeTextV;
	private TextView recodingMileageTextV;
	public static ImageView gpsV;
	private ImageView mapV;
	private SliderRelativeLayout slider;
	private ProgressBar progressHorizontal;

	private ImageView d1v;
	private ImageView d2v;
	private ImageView d3v;
	private ImageView d4v;

	private ImageView t1V;
	private ImageView t2V;
	private ImageView t3V;
	private ImageView t4V;
	private ImageView t5V;
	private ImageView t6V;

	private ImageView s1V;
	private ImageView s2V;
	private ImageView s3V;
	private ImageView s4V;

	private ImageView sliderIconV;

	private RelativeLayout leftTime;
	private RelativeLayout leftDis;
	private LinearLayout topTime;
	private LinearLayout toptDis;

	public int status = 0;
	public int target = 0;

	public int speedPerKm = 0;
	public int timePerKm = 0;

	private Timer timer = null;
	private TimerTask task = null;

	private boolean isAchieveHalfGoal = false;// 记录是否达成目标的一半，true-已经播放过，false-没有播放过，
	private boolean isAchieveHalfGoalPlayed = false;// 本次是否播放过运动到一半距离true-是，false-否

	private boolean isAchieveGoal = false;// 记录是否达成目标
	private boolean isAchieveGoalPlayed = false;// 本次是否播放过达成目标语音

	private boolean isPerKm = false;// 本次是否是整公里
	private boolean isPer5m = false;// 本次是否是整5分钟

	// private double currentDistance = 0;//最后一次在屏幕上显示的距离
	// private int currentPaceKm = 0;//最后一次在屏幕上显示的配速

	public static Handler timerListenerHandler;
	// 测试代码
	public static double testlon = 116.395823;
	public static double testlat = 39.839016;
    
	private int timeWhenPause;
	// 以上测试代码

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sport_recording);
		if (Variables.switchVoice == 0) {
			PlayVoice.StartSportsVoice(this);
		}

		doneV = (TextView) findViewById(R.id.slider_done);
		resumeV = (TextView) findViewById(R.id.slider_resume);
		mapV = (ImageView) findViewById(R.id.sport_map);
		sliderIconV = (ImageView) findViewById(R.id.slider_icon);
		slider = (SliderRelativeLayout) findViewById(R.id.slider_layout);
		sliderTextV = (TextView) findViewById(R.id.slider_text);
		processTextV = (TextView) findViewById(R.id.recoding_process_text);
		gpsV = (ImageView) findViewById(R.id.sport_gps_status);
		leftTime = (RelativeLayout) findViewById(R.id.sport_recoding_time);
		leftDis = (RelativeLayout) findViewById(R.id.sport_recoding_dis2);
		topTime = (LinearLayout) findViewById(R.id.sport_time2);
		toptDis = (LinearLayout) findViewById(R.id.sport_dis);
		recodingTimeTextV = (TextView) findViewById(R.id.sport_recoding_time_text);
		recodingMileageTextV = (TextView) findViewById(R.id.sport_recoding_mileage_text);
		s1V = (ImageView) findViewById(R.id.match_recoding_speed1);
		s2V = (ImageView) findViewById(R.id.match_recoding_speed2);
		s3V = (ImageView) findViewById(R.id.match_recoding_speed3);
		s4V = (ImageView) findViewById(R.id.match_recoding_speed4);
		progressHorizontal = (ProgressBar) findViewById(R.id.recoding_process);

		if (Variables.runTargetType == 2) {
			progressHorizontal.setMax(Variables.runTargetDis);
			target = Variables.runTargetDis;
		} else if (Variables.runTargetType == 3) {
			progressHorizontal.setMax(Variables.runTargetTime);
			target = Variables.runTargetTime;
		}
		mapV.setOnTouchListener(this);
		resumeV.setOnTouchListener(this);
		doneV.setOnTouchListener(this);
		// 根据运动类型初始化跑步界面的布局
		initRunLayout();
		slider.setMainHandler(slipHandler);
		IntentFilter filter = new IntentFilter(MapActivity.closeAction);
		registerReceiver(broadcastReceiver, filter);

		timerListenerHandler = new Handler() {
			public void handleMessage(Message msg) {
				// 1-开始，2-停止
				if (msg.what == 1) {
					changeStatus(false);
					startTimer();

				} else if (msg.what == 2) {
					changeStatus(true);
					stopTimer();

				}
				super.handleMessage(msg);
			}
		};
		// 开启引擎
		YaoPao01App.runManager.startRun();
		changeStatus(false);
	}

	class TimerTaskUpdate extends TimerTask {
		@Override
		public void run() {
			runOnUiThread(new Runnable() { // UI thread
				@Override
				public void run() {
					updateUI(YaoPao01App.runManager.during(),YaoPao01App.runManager.distance,YaoPao01App.runManager.secondPerKm);
				}
			});
		}

	}

	private void updateUI(int utime,int distance,int paceKm) {

		// 更新时间
		int[] time = YaoPao01App.cal(utime/1000);
		int t1 = time[0] / 10;
		int t2 = time[0] % 10;
		int t3 = time[1] / 10;
		int t4 = time[1] % 10;
		int t5 = time[2] / 10;
		int t6 = time[2] % 10;
		YaoPao01App.graphicTool.updateWhiteNum(new int[] { t1, t2, t3, t4, t5,
				t6 }, new ImageView[] { t1V, t2V, t3V, t4V, t5V, t6V });

		int d1 = (int)  distance/ 10000;
		int d2 = (int) (distance % 10000) / 1000;
		int d3 = (int) (distance % 1000) / 100;
		int d4 = (int) (distance % 100) / 10;
		if (d1 > 0) {
			d1v.setVisibility(View.VISIBLE);
		}
		YaoPao01App.graphicTool.updateWhiteNum(new int[] { d1, d2, d3, d4 },
				new ImageView[] { d1v, d2v, d3v, d4v });

		int[] speed = YaoPao01App.cal(paceKm);

		int s1 = speed[1] / 10;
		int s2 = speed[1] % 10;
		int s3 = speed[2] / 10;
		int s4 = speed[2] % 10;
		YaoPao01App.graphicTool.updateWhiteNum(new int[] { s1, s2, s3, s4 },
				new ImageView[] { s1V, s2V, s3V, s4V });
		checkplayVoice( utime, distance, paceKm);
		// 进度条
		progressHorizontal.setProgress(YaoPao01App.runManager.completeValue());
	}

	private void initRunLayout() {
		if (YaoPao01App.runManager.getTargetType() == 1) {
			progressHorizontal.setVisibility(View.GONE);
			processTextV.setText("自由运动");
			toptDis.setVisibility(View.VISIBLE);
			topTime.setVisibility(View.GONE);
			leftTime.setVisibility(View.VISIBLE);
			leftDis.setVisibility(View.GONE);
			recodingTimeTextV.setText("时间");
			recodingMileageTextV.setText("距离（公里）");
			d1v = (ImageView) this.findViewById(R.id.match_recoding_dis1);
			d2v = (ImageView) this.findViewById(R.id.match_recoding_dis2);
			d3v = (ImageView) this.findViewById(R.id.match_recoding_dis3);
			d4v = (ImageView) this.findViewById(R.id.match_recoding_dis4);

			t1V = (ImageView) findViewById(R.id.match_recoding_time_h1);
			t2V = (ImageView) findViewById(R.id.match_recoding_time_h2);
			t3V = (ImageView) findViewById(R.id.match_recoding_time_m1);
			t4V = (ImageView) findViewById(R.id.match_recoding_time_m2);
			t5V = (ImageView) findViewById(R.id.match_recoding_time_s1);
			t6V = (ImageView) findViewById(R.id.match_recoding_time_s2);
		}
		if (YaoPao01App.runManager.getTargetType() == 2) {
			toptDis.setVisibility(View.VISIBLE);
			topTime.setVisibility(View.GONE);
			leftTime.setVisibility(View.VISIBLE);
			leftDis.setVisibility(View.GONE);
			recodingTimeTextV.setText("时间");
			recodingMileageTextV.setText("距离（公里）");
			d1v = (ImageView) this.findViewById(R.id.match_recoding_dis1);
			d2v = (ImageView) this.findViewById(R.id.match_recoding_dis2);
			d3v = (ImageView) this.findViewById(R.id.match_recoding_dis3);
			d4v = (ImageView) this.findViewById(R.id.match_recoding_dis4);

			t1V = (ImageView) findViewById(R.id.match_recoding_time_h1);
			t2V = (ImageView) findViewById(R.id.match_recoding_time_h2);
			t3V = (ImageView) findViewById(R.id.match_recoding_time_m1);
			t4V = (ImageView) findViewById(R.id.match_recoding_time_m2);
			t5V = (ImageView) findViewById(R.id.match_recoding_time_s1);
			t6V = (ImageView) findViewById(R.id.match_recoding_time_s2);
		}
		if (YaoPao01App.runManager.getTargetType() == 3) {
			toptDis.setVisibility(View.GONE);
			topTime.setVisibility(View.VISIBLE);
			leftTime.setVisibility(View.GONE);
			leftDis.setVisibility(View.VISIBLE);
			recodingTimeTextV.setText("距离（公里）");
			recodingMileageTextV.setText("时间");
			d1v = (ImageView) this.findViewById(R.id.sport_recoding_dis2_1);
			d2v = (ImageView) this.findViewById(R.id.sport_recoding_dis2_2);
			d3v = (ImageView) this.findViewById(R.id.sport_recoding_dis2_3);
			d4v = (ImageView) this.findViewById(R.id.sport_recoding_dis2_4);

			t1V = (ImageView) findViewById(R.id.match_recoding_time2_h1);
			t2V = (ImageView) findViewById(R.id.match_recoding_time2_h2);
			t3V = (ImageView) findViewById(R.id.match_recoding_time2_m1);
			t4V = (ImageView) findViewById(R.id.match_recoding_time2_m2);
			t5V = (ImageView) findViewById(R.id.match_recoding_time2_s1);
			t6V = (ImageView) findViewById(R.id.match_recoding_time2_s2);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Variables.distancePlayed = 0;
		Variables.timePlayed = 0;
		// releaseBitmap();
		YaoPao01App.graphicTool.unbindDrawables(new ImageView[] { d1v, d2v,
				d3v, d4v, t1V, t2V, t3V, t4V, t5V, t6V, s1V, s2V, s3V, s4V });
		// unregisterReceiver(broadcastReceiver);
	}

	private void startTimer() {
		if (timer!=null) {
			return;
		}
	
		task = new TimerTaskUpdate();
		timer = new Timer();
		timer.schedule(task, 0, 1000);
	}

	private void stopTimer() {
		
		if (timer != null) {
			timer.cancel();
			timer = null;
			if (task != null) {
				task.cancel();
				task = null;
			}
		}

	}

	private void changeStatus(boolean off) {
		if (off) {
			YaoPao01App.runManager.changeRunStatus(2);
			Variables.sportStatus = 1;
			timeWhenPause=YaoPao01App.runManager.during();
		} else {
			YaoPao01App.runManager.changeRunStatus(1);
			Variables.sportStatus = 0;
		}
	}

	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);
		if (YaoPao01App.runManager.getRunStatus() == 1) {
			sliderIconV.setVisibility(View.VISIBLE);
			sliderTextV.setVisibility(View.VISIBLE);
			sliderTextV.setText("滑动暂停");
			doneV.setVisibility(View.GONE);
			resumeV.setVisibility(View.GONE);
		} else {
			doneV.setVisibility(View.VISIBLE);
			resumeV.setVisibility(View.VISIBLE);
			sliderIconV.setVisibility(View.GONE);
			sliderTextV.setVisibility(View.GONE);
		}

		super.onResume();
		super.activityOnFront = this.getClass().getSimpleName();
		Variables.activityOnFront = this.getClass().getSimpleName();
		if (Variables.sportStatus == 0) {
			startTimer();
		}else {
			updateUI(timeWhenPause,YaoPao01App.runManager.distance,YaoPao01App.runManager.secondPerKm);
		}

	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		stopTimer();
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {
		case R.id.sport_time_layout:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				break;
			}
			break;
		case R.id.sport_map:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				mapV.setBackgroundResource(R.drawable.button_map_h);
				break;
			case MotionEvent.ACTION_UP:
				mapV.setBackgroundResource(R.drawable.button_map);
				Intent intent = new Intent(SportRunMainActivity.this,
						MapActivity.class);
				SportRunMainActivity.this.startActivity(intent);
				break;
			}
			break;
		case R.id.slider_done:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				doneV.setBackgroundResource(R.color.red_h);
				break;
			case MotionEvent.ACTION_UP:
				doneV.setBackgroundResource(R.color.red);
				// 停止引擎
				final Handler sliderHandler = new Handler() {
					public void handleMessage(Message msg) {
						if (msg.what == 0) {
							
							Intent intent = new Intent(
									SportRunMainActivity.this,
									SportSaveActivity.class);
							SportRunMainActivity.this.finish();
							SportRunMainActivity.this.startActivity(intent);

						} else if (msg.what == 1) {
							// 运动距离小于50米
							Toast.makeText(SportRunMainActivity.this,
									"您运动距离也太短了吧！这次就不给您记录了，下次一定要加油！",
									Toast.LENGTH_LONG).show();
							SportRunMainActivity.this.finish();
						}
						super.handleMessage(msg);
					}
				};
				// DialogTool.doneSport(SportRecordActivity.this,
				// sliderHandler);
				DialogTool dialog = new DialogTool(SportRunMainActivity.this);
				dialog.doneSport(sliderHandler);
				break;
			}
			break;
		case R.id.slider_resume:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				resumeV.setBackgroundResource(R.color.blue_h);
				break;
			case MotionEvent.ACTION_UP:
				resumeV.setBackgroundResource(R.color.blue_dark);
				sliderIconV.setVisibility(View.VISIBLE);
				sliderTextV.setVisibility(View.VISIBLE);
				sliderTextV.setText("滑动暂停");
				doneV.setVisibility(View.GONE);
				resumeV.setVisibility(View.GONE);
				if (Variables.switchVoice == 0) {
					PlayVoice.ProceedSportsVoice(SportRunMainActivity.this);
				}

				changeStatus(false);
				startTimer();

				break;
			}
			break;
		}
		return true;
	}

	// 运动暂停
	Handler slipHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				doneV.setVisibility(View.VISIBLE);
				resumeV.setVisibility(View.VISIBLE);
				sliderIconV.setVisibility(View.GONE);
				sliderTextV.setVisibility(View.GONE);
				if (Variables.switchVoice == 0) {
					PlayVoice.PauseSportsVoice(SportRunMainActivity.this);
				}
				changeStatus(true);
				stopTimer();

				Log.v("zc", "运动暂停");
			}
		};
	};

	private void checkplayVoice(int utime,int distance,int paceKm) {
		// 判断运动距离是否达到整公里条件
		if (distance - Variables.distancePlayed >= 1000) {
			Variables.distancePlayed += 1000;
			isPerKm = true;
		}
		// 是否播放语音
		if (Variables.switchVoice == 0) {
			// 运动类型
			if (YaoPao01App.runManager.getTargetType() == 1) {// 自由
				if (isPerKm) {
					isPerKm = false;
					YaoPao01App.playPerKmVoice(utime, distance, paceKm);
				}
			} else if (YaoPao01App.runManager.getTargetType() == 2) {// 距离
				if (YaoPao01App.runManager.getTargetValue() > 4000) {

					// 运动距离大于目标
					if (distance >= YaoPao01App.runManager
							.getTargetValue()) {
						if (!isAchieveGoal) {
							// 播放达成目标语音方法里需要判断当前运动距离是否小于3公里，因为第一公里和第二公里的配速要特殊处理
							YaoPao01App.playAchieveGoalVoice(utime, distance, paceKm);
							
							isAchieveGoal = true;
							isAchieveGoalPlayed = true;
						} else {
							// 超过目标
							if (Variables.distancePlayed > YaoPao01App.runManager
									.getTargetValue()) {
								if (isAchieveGoalPlayed) {
									isAchieveGoalPlayed = false;
								} else if (isPerKm) {
									isPerKm = false;
									YaoPao01App.playOverGoalVoice(utime, distance, paceKm);
								}
							}
						}
					} else if (distance > YaoPao01App.runManager
							.getTargetValue() / 2
							&& distance < YaoPao01App.runManager
									.getTargetValue()) {
						// // 是否播放过超过一半的语音
						if (!isAchieveHalfGoal) {
							// 此处播放运动了一半
							YaoPao01App.playHalfDisVoice(utime, distance, paceKm);
							isAchieveHalfGoal = true;
							isAchieveHalfGoalPlayed = true;
						} else if ((YaoPao01App.runManager.getTargetValue() - distance) <= 2000
								&& (YaoPao01App.runManager.getTargetValue() - distance) > 0) {
							// 此处播放距离目标小于2公里
							if (isPerKm) {
								isPerKm = false;
								YaoPao01App.playLess2Voice(utime, distance, paceKm);
							}
						} else {
							if (isPerKm) {
								isPerKm = false;
								if (isAchieveHalfGoalPlayed) {
									isAchieveHalfGoalPlayed = false;
									if ((YaoPao01App.runManager
											.getTargetValue() / 1000) % 2 != 0) {
										YaoPao01App.playPerKmVoice( utime, distance, paceKm);
									}
								} else {
									YaoPao01App.playPerKmVoice(utime, distance, paceKm);
								}
							}
						}
					} else if ((YaoPao01App.runManager.getTargetValue() - distance) <= 2000
							&& (YaoPao01App.runManager.getTargetValue() - distance) > 0) {
						// 此处播放距离目标小于2公里
						if (isPerKm) {
							isPerKm = false;
							YaoPao01App.playLess2Voice(utime, distance, paceKm);
						}
					} else {
						if (isPerKm) {
							isPerKm = false;
							YaoPao01App.playPerKmVoice(utime, distance, paceKm);
						}
					}

				} else {
					// 小于4000米
					// 运动距离大于目标
					if (distance >= YaoPao01App.runManager
							.getTargetValue()) {
						if (!isAchieveGoal) {
							// 播放达成目标语音方法里需要判断当前运动距离是否小于3公里，因为第一公里和第二公里的配速要特殊处理
							YaoPao01App.playAchieveGoalVoice(utime, distance, paceKm);
							isAchieveGoal = true;
							isAchieveGoalPlayed = true;
						} else {
							// 超过目标
							if (Variables.distancePlayed > YaoPao01App.runManager
									.getTargetValue()) {
								if (isAchieveGoalPlayed) {
									isAchieveGoalPlayed = false;
								} else if (isPerKm) {
									isPerKm = false;
									YaoPao01App.playOverGoalVoice(utime, distance, paceKm);
								}
							}

						}
					} else {
						if (isPerKm) {
							isPerKm = false;
							YaoPao01App.playPerKmVoice(utime, distance, paceKm);
						}
					}
				}
			} else if (YaoPao01App.runManager.getTargetType() == 3) {
				if (utime - Variables.timePlayed
						* 1000 >= Variables.intervalTime * 1000) {
					Variables.timePlayed += Variables.intervalTime;
					isPer5m = true;
				}

				if (YaoPao01App.runManager.getTargetValue() / 60000 > 20) {

					// 运动时间大于目标
					if (utime >= YaoPao01App.runManager
							.getTargetValue()) {
						// 目标达成
						if (!isAchieveGoal) {
							YaoPao01App.playAchieveTimeGoalVoice(distance,paceKm);
							isAchieveGoal = true;
							isAchieveGoalPlayed = true;
						} else {
							// 超过目标
							if (Variables.timePlayed > YaoPao01App.runManager
									.getTargetValue() / 1000) {
								if (isAchieveGoalPlayed) {
									isAchieveGoalPlayed = false;
								} else if (isPer5m) {
									isPer5m = false;
									YaoPao01App.playOverTimeGoalVoice(distance, paceKm);
								}
							}
						}
					} else if (utime > (YaoPao01App.runManager
							.getTargetValue() / 2)
							&& utime < YaoPao01App.runManager
									.getTargetValue()) {
						// // 是否播放过超过一半的语音
						if (!isAchieveHalfGoal) {
							// 此处播放运动了一半
							YaoPao01App.playHalfTimeVoice(distance,paceKm);
							isAchieveHalfGoal = true;
							isAchieveHalfGoalPlayed = true;
						} else if ((YaoPao01App.runManager.getTargetValue() - YaoPao01App.runManager
								.during()) <= (10 * 60 * 1000)
								&& (YaoPao01App.runManager.getTargetValue() - YaoPao01App.runManager
										.during()) > 0) {
							// 此处播放距离目标小于10分钟
							if (isPer5m) {
								isPer5m = false;
								YaoPao01App.playLess10minVoice(distance,paceKm);
							}
						} else {
							if (isPer5m) {
								isPer5m = false;
								if (isAchieveHalfGoalPlayed) {
									isAchieveHalfGoalPlayed = false;
									if ((YaoPao01App.runManager
											.getTargetValue() / 60000) % 2 != 0) {
										YaoPao01App.playPer5minVoice(distance,paceKm);
									}
								} else {
									YaoPao01App.playPer5minVoice(distance,paceKm);
								}
							}
						}
					} else if ((YaoPao01App.runManager.getTargetValue() - YaoPao01App.runManager
							.during()) <= (10 * 60 * 1000)
							&& (YaoPao01App.runManager.getTargetValue() - YaoPao01App.runManager
									.during()) > 0) {
						// 此处播放距离目标小于10分钟
						if (isPer5m) {
							isPer5m = false;
							YaoPao01App.playLess10minVoice(distance,paceKm);
						}
					} else {
						if (isPer5m) {
							isPer5m = false;
							YaoPao01App.playPer5minVoice(distance,paceKm);
						}
					}

				} else {
					// 小于20分钟
					// 运动时间大于目标
					if (utime >= YaoPao01App.runManager
							.getTargetValue()) {
						if (!isAchieveGoal) {
							YaoPao01App.playAchieveTimeGoalVoice(distance,paceKm);
							isAchieveGoal = true;
							isAchieveGoalPlayed = true;
						} else {
							// 超过目标
							if (Variables.timePlayed > YaoPao01App.runManager.getTargetValue() / 1000) {
								if (isAchieveGoalPlayed) {
									isAchieveGoalPlayed = false;
								} else if (isPer5m) {
									isPer5m = false;
									YaoPao01App.playOverTimeGoalVoice(distance,paceKm);
								}
							}

						}
					} else {
						if (isPer5m) {
							isPer5m = false;
							YaoPao01App.playPer5minVoice(distance,paceKm);
						}
					}
				}

			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			// Toast.makeText(SportRecordActivity.this, "", duration)
		}
		return false;
	}

	// 在地图页面关闭此页面
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if ("close".equals(intent.getExtras().getString("data"))) {
				unregisterReceiver(this);
				SportRunMainActivity.this.finish();
			}
		}
	};
}
