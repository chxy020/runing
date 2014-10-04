package net.yaopao.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.yaopao.assist.DialogTool;
import net.yaopao.assist.GpsPoint;
import net.yaopao.assist.Variables;
import net.yaopao.voice.PlayVoice;
import net.yaopao.widget.SliderRelativeLayout;
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

import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.umeng.analytics.MobclickAgent;

public class SportRecordActivity extends BaseActivity implements
		OnTouchListener {
	public static List<GpsPoint> points;

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
	// 记录ProgressBar的完成进度
	private SimpleDateFormat formatterM;
	private SimpleDateFormat formatterS;
	public int status = 0;
	public int target = 0;
	public int speedPerKm = 0;
	public int timePerKm = 0;

	private Timer timer = null;
	private TimerTask task = null;

	private Timer gpsTimer = null;
	private TimerTask gpsTask = null;

	public static Handler timerListenerHandler;
	public static Handler gpsListenerHandler;

	private boolean isAchieveHalfGoal = false;// 记录是否达成目标的一半，true-已经播放过，false-没有播放过，
	private boolean isAchieveHalfGoalPlayed = false;// 本次是否播放过运动到一半距离true-是，false-否

	private boolean isAchieveGoal = false;// 记录是否达成目标
	private boolean isAchieveGoalPlayed = false;// 本次是否播放过达成目标语音

	private boolean isPerKm = false;// 本次是否是整公里
	private boolean isPer5m = false;// 本次是否是整5分钟

	private long startTime = 0;
	private long sprortTime = 0;// 累计时间，暂停后计算时间要加上此时间

	private double sprortDis = 0;// 累计距离，暂停后计算距离要加上此距离

	// 测试代码
	 public static double lon = 116.395823;
	 public static double lat = 39.839016;

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
		if (Variables.runtar == 1) {
			progressHorizontal.setMax(Variables.runtarDis * 1000);
			target = Variables.runtarDis * 1000;//
		} else if (Variables.runtar == 2) {
			progressHorizontal.setMax(Variables.runtarTime * 60);
			target = Variables.runtarTime * 60;
		}
		Log.v("wysport", "target  = " + target);
		if (Variables.sportStatus == 0) {
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
		mapV.setOnTouchListener(this);
		resumeV.setOnTouchListener(this);
		doneV.setOnTouchListener(this);
		// 根据运动类型初始化跑步界面的布局
		initRunLayout();
		points = new ArrayList<GpsPoint>();
		slider.setMainHandler(slipHandler);
		startRecordGps();
		startTimer();
		IntentFilter filter = new IntentFilter(MapActivity.closeAction);
		registerReceiver(broadcastReceiver, filter);

		timerListenerHandler = new Handler() {
			public void handleMessage(Message msg) {
				// 1-开始，2-停止
				if (msg.what == 1) {
					startTimer();
				} else if (msg.what == 2) {
					stopTimer();
				}
				super.handleMessage(msg);
			}
		};
		gpsListenerHandler = new Handler() {
			public void handleMessage(Message msg) {
				// 3-开始，4-停止
				if (msg.what == 3) {
					startRecordGps();
				} else if (msg.what == 4) {
					stopRecordGps();
				}
				super.handleMessage(msg);
			}
		};
	}

	private void initRunLayout() {
		if (Variables.runtar == 0) {
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
		if (Variables.runtar == 1) {
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
		if (Variables.runtar == 2) {
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
		stopTimer();
		stopRecordGps();
		Variables.distancePlayed = 0;
		Variables.timePlayed = 0;
		//releaseBitmap();
		YaoPao01App.graphicTool.unbindDrawables(new ImageView[]{d1v,d2v,d3v,d4v,t1V,t2V,t3V,t4V,t5V,t6V,s1V,s2V,s3V,s4V});
		unregisterReceiver(broadcastReceiver);
	}


	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);
		if (Variables.sportStatus == 0) {
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
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
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
				Intent intent = new Intent(SportRecordActivity.this,
						MapActivity.class);
				SportRecordActivity.this.startActivity(intent);
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
				final Handler sliderHandler = new Handler() {
					public void handleMessage(Message msg) {
						if (msg.what == 0) {
							Intent intent = new Intent(
									SportRecordActivity.this,
									SportSaveActivity.class);
							stopRecordGps();
							SportRecordActivity.this.finish();
							SportRecordActivity.this.startActivity(intent);

						} else if (msg.what == 1) {
							// 运动距离小于50米
							Toast.makeText(SportRecordActivity.this,
									"您运动距离也太短了吧！这次就不给您记录了，下次一定要加油！",
									Toast.LENGTH_LONG).show();
							Variables.utime = 0;
							Variables.pspeed = 0;
							Variables.distance = 0;
							Variables.points = 0;
							if (points != null) {
								points = null;
							}
							SportRecordActivity.this.finish();
						}
						super.handleMessage(msg);
					}
				};
				DialogTool.doneSport(SportRecordActivity.this, sliderHandler);

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
				startTimer();
				sliderIconV.setVisibility(View.VISIBLE);
				sliderTextV.setVisibility(View.VISIBLE);
				sliderTextV.setText("滑动暂停");
				doneV.setVisibility(View.GONE);
				resumeV.setVisibility(View.GONE);
				if (Variables.switchVoice == 0) {
					PlayVoice.ProceedSportsVoice(SportRecordActivity.this);
				}
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
				if (Variables.sportStatus == 0) {
					stopTimer();
					doneV.setVisibility(View.VISIBLE);
					resumeV.setVisibility(View.VISIBLE);
					sliderIconV.setVisibility(View.GONE);
					sliderTextV.setVisibility(View.GONE);
					if (Variables.switchVoice == 0) {
						PlayVoice.PauseSportsVoice(SportRecordActivity.this);
					}
				}
				Log.v("wyvoice", "运动暂停");
			}
		};
	};

	public static GpsPoint getOnePoint() {
		GpsPoint point = null;
		if (YaoPao01App.loc != null) {
			point = new GpsPoint();
			point.lon = YaoPao01App.loc.getLongitude();
			point.lat = YaoPao01App.loc.getLatitude();
			point.time = YaoPao01App.loc.getTime();
			point.altitude = YaoPao01App.loc.getAltitude();
			point.course = YaoPao01App.loc.getBearing();
			point.speed = YaoPao01App.loc.getSpeed();
			point.status = Variables.sportStatus;
		}
		// 测试代码
//		 Random random1 = new Random();
//		 lat = lat + random1.nextFloat() / 1000;
//		 lon = lon + random1.nextFloat() / 1000;
//		 Log.v("wysport", "lat =" + random1.nextFloat() / 1000 + " lon="
//		 + random1.nextFloat() / 1000);
//		 GpsPoint point = new GpsPoint(lon, lat, Variables.sportStatus,
//		 new Date().getTime());
		// 测试代码
		return point;

	}

	public boolean pushOnePoint() {
		boolean result = false;
		double meter = 0;
		GpsPoint last = null;
		GpsPoint point = getOnePoint();
		if (point != null) {
			if (points == null) {
				return false;
			}
			if (points.size() == 0) {
				points.add(point);
				last = point;
				// lastSportTime=last.getTime();
				result = false;
			}
			last = points.get(points.size() - 1);
			meter = getDistanceFrom2ponit(last, point);
			// 判断，如果距离小于5，并且两点状态相同，抛掉当前点，如果状态不同，记录，算距离，更新ui
			if (meter < 5) {
				if (last.status == point.status) {
					last.time = point.time;
					if (last.status == 0) {
						result = true;
					} else {
						result = false;
					}

				} else {
					if (last.status == 0) {
						Variables.distance += meter;
					}
					points.add(point);
					result = false;
				}

			} else {
				if (point.status == 0) {
					Variables.distance += meter;
				}
				points.add(point);
				result = true;
			}

			// 计算积分,播放语音
			if (point.status == 0) {
				// 判断运动距离是否达到整公里条件
				if (Variables.distance - Variables.distancePlayed >= 1000) {
					Variables.distancePlayed += 1000;
					// 计算积分
					calculatePoint();
					isPerKm = true;
				}
				// 是否播放语音
				if (Variables.switchVoice == 0) {
					// 运动类型
					if (Variables.runtar == 0) {
						if (isPerKm) {
							isPerKm = false;
							YaoPao01App.playPerKmVoice();
						}
					} else if (Variables.runtar == 1) {
						if (Variables.runtarDis > 4) {

							// 运动距离大于目标
							if (Variables.distance >= Variables.runtarDis * 1000) {
								if (!isAchieveGoal) {
									// 播放达成目标语音方法里需要判断当前运动距离是否小于3公里，因为第一公里和第二公里的配速要特殊处理
									YaoPao01App.playAchieveGoalVoice();
									isAchieveGoal = true;
									isAchieveGoalPlayed = true;
								} else {
									// 超过目标
									if (Variables.distancePlayed > Variables.runtarDis * 1000) {
										if (isAchieveGoalPlayed) {
											isAchieveGoalPlayed = false;
										} else if (isPerKm) {
											isPerKm = false;
											YaoPao01App.playOverGoalVoice();
										}
									}
								}
							} else if (Variables.distance > (Variables.runtarDis * 500)
									&& Variables.distance < Variables.runtarDis * 1000) {
								// // 是否播放过超过一半的语音
								if (!isAchieveHalfGoal) {
									// 此处播放运动了一半
									YaoPao01App.playHalfDisVoice();
									isAchieveHalfGoal = true;
									isAchieveHalfGoalPlayed = true;
								} else if ((Variables.runtarDis * 1000 - Variables.distance) <= 2000
										&& (Variables.runtarDis * 1000 - Variables.distance) > 0) {
									// 此处播放距离目标小于2公里
									if (isPerKm) {
										isPerKm = false;
										YaoPao01App.playLess2Voice();
									}
								} else {
									if (isPerKm) {
										isPerKm = false;
										if (isAchieveHalfGoalPlayed) {
											isAchieveHalfGoalPlayed = false;
											if (Variables.runtarDis % 2 != 0) {
												YaoPao01App.playPerKmVoice();
											}
										} else {
											YaoPao01App.playPerKmVoice();
										}
									}
								}
							} else if ((Variables.runtarDis * 1000 - Variables.distance) <= 2000
									&& (Variables.runtarDis * 1000 - Variables.distance) > 0) {
								// 此处播放距离目标小于2公里
								if (isPerKm) {
									isPerKm = false;
									YaoPao01App.playLess2Voice();
								}
							} else {
								if (isPerKm) {
									isPerKm = false;
									YaoPao01App.playPerKmVoice();
								}
							}

						} else {
							// 小于4000米
							// 运动距离大于目标
							if (Variables.distance >= Variables.runtarDis * 1000) {
								if (!isAchieveGoal) {
									// 播放达成目标语音方法里需要判断当前运动距离是否小于3公里，因为第一公里和第二公里的配速要特殊处理
									YaoPao01App.playAchieveGoalVoice();
									isAchieveGoal = true;
									isAchieveGoalPlayed = true;
								} else {
									// 超过目标
									if (Variables.distancePlayed > Variables.runtarDis * 1000) {
										if (isAchieveGoalPlayed) {
											isAchieveGoalPlayed = false;
										} else if (isPerKm) {
											isPerKm = false;
											YaoPao01App.playOverGoalVoice();
										}
									}

								}
							} else {
								if (isPerKm) {
									isPerKm = false;
									YaoPao01App.playPerKmVoice();
								}
							}
						}
					} else if (Variables.runtar == 2) {
						if (Variables.utime - Variables.timePlayed * 1000 >= Variables.intervalTime * 1000) {
							Variables.timePlayed += Variables.intervalTime;
							isPer5m = true;
						}

						if (Variables.runtarTime > 20) {

							// 运动时间大于目标
							if (Variables.utime >= Variables.runtarTime * 60000) {
								// 目标达成
								if (!isAchieveGoal) {
									YaoPao01App.playAchieveTimeGoalVoice();
									isAchieveGoal = true;
									isAchieveGoalPlayed = true;
								} else {
									// 超过目标
									if (Variables.timePlayed > Variables.runtarTime * 60) {
										if (isAchieveGoalPlayed) {
											isAchieveGoalPlayed = false;
										} else if (isPer5m) {
											isPer5m = false;
											YaoPao01App.playOverTimeGoalVoice();
										}
									}
								}
							} else if (Variables.utime > (Variables.runtarTime * 30000)
									&& Variables.utime < Variables.runtarTime * 60000) {
								// // 是否播放过超过一半的语音
								if (!isAchieveHalfGoal) {
									// 此处播放运动了一半
									YaoPao01App.playHalfTimeVoice();
									isAchieveHalfGoal = true;
									isAchieveHalfGoalPlayed = true;
								} else if ((Variables.runtarTime * 60000 - Variables.utime) <= (10 * 60 * 1000)
										&& (Variables.runtarTime * 60000 - Variables.utime) > 0) {
									// 此处播放距离目标小于10分钟
									if (isPer5m) {
										isPer5m = false;
										YaoPao01App.playLess10minVoice();
									}
								} else {
									if (isPer5m) {
										isPer5m = false;
										if (isAchieveHalfGoalPlayed) {
											isAchieveHalfGoalPlayed = false;
											if (Variables.runtarTime % 2 != 0) {
												YaoPao01App.playPer5minVoice();
											}
										} else {
											YaoPao01App.playPer5minVoice();
										}
									}
								}
							} else if ((Variables.runtarTime * 60000 - Variables.utime) <= (10 * 60 * 1000)
									&& (Variables.runtarTime * 60000 - Variables.utime) > 0) {
								// 此处播放距离目标小于10分钟
								if (isPer5m) {
									isPer5m = false;
									YaoPao01App.playLess10minVoice();
								}
							} else {
								if (isPer5m) {
									isPer5m = false;
									YaoPao01App.playPer5minVoice();
								}
							}

						} else {
							// 小于20分钟
							// 运动时间大于目标
							if (Variables.utime >= Variables.runtarTime * 60000) {
								if (!isAchieveGoal) {
									YaoPao01App.playAchieveTimeGoalVoice();
									isAchieveGoal = true;
									isAchieveGoalPlayed = true;
								} else {
									// 超过目标
									if (Variables.timePlayed > Variables.runtarTime * 60) {
										if (isAchieveGoalPlayed) {
											isAchieveGoalPlayed = false;
										} else if (isPer5m) {
											isPer5m = false;
											YaoPao01App.playOverTimeGoalVoice();
										}
									}

								}
							} else {
								if (isPer5m) {
									isPer5m = false;
									YaoPao01App.playPer5minVoice();
								}
							}
						}

					}
				}
			}
		}
		return result;
	}

	/**
	 * 整公里计算积分
	 */
	private void calculatePoint() {
		int minute = (int) Math.round(timePerKm / 60000.0);
		Variables.points += YaoPao01App.calPspeedPoints(minute);
		// 计算完毕，每公里用时清零
		timePerKm = 0;
	}

	public void startTimer() {
		Variables.sportStatus = 0;
		startTime = new Date().getTime();

		timer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {

				runOnUiThread(new Runnable() { // UI thread
					@Override
					public void run() {

						if (Variables.sportStatus == 0) {
							if (startTime != 0) {

								long duringTime = (new Date().getTime() - startTime)
										- (Variables.utime - sprortTime);
								timePerKm += duringTime;
								Variables.utime = new Date().getTime()
										- startTime + sprortTime;
							}
						}
						int[] time = YaoPao01App.cal(Variables.utime / 1000);
						int t1 = time[0] / 10;
						int t2 = time[0] % 10;
						int t3 = time[1] / 10;
						int t4 = time[1] % 10;
						int t5 = time[2] / 10;
						int t6 = time[2] % 10;
						
						YaoPao01App.graphicTool.updateWhiteNum(new int[]{t1,t2,t3,t4,t5,t6},new ImageView[]{t1V,t2V,t3V,t4V,t5V,t6V});
						
					}
				});
			}
		};
		timer.schedule(task, 0, 1000);
	}

	public void stopTimer() {
		if (task != null) {
			task.cancel();
			task = null;
		}
		if (timer != null) {
			timer.cancel(); // Cancel timer
			timer.purge();
			timer = null;
		}
		Variables.sportStatus = 1;
		sprortTime = Variables.utime;

	}

	public void startRecordGps() {
		gpsTimer = new Timer();
		gpsTask = new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() { // UI thread
					@Override
					public void run() {
						if (pushOnePoint()) {
							updateUI();
							if (Variables.runtar != 0) {
								if (status < target) {
									if (Variables.runtar == 1) {
										status = (int) (Variables.distance);
									} else if (Variables.runtar == 2) {
										status = (int) Variables.utime / 1000;
									}
									// 发送消息到Handler
									Message m = new Message();
									m.what = 0x111;
									// 发送消息
									procesHandler.sendMessage(m);
								}
							}
						}
					}
				});
			}
		};
		gpsTimer.schedule(gpsTask, 0, 2000);
	}

	//
	public void stopRecordGps() {
		if (gpsTask != null) {
			gpsTask.cancel();
			gpsTask = null;
		}
		if (gpsTimer != null) {
			gpsTimer.cancel(); // Cancel timer
			gpsTimer.purge();
			gpsTimer = null;
		}
	}

	private static double getDistanceFrom2ponit(GpsPoint before, GpsPoint now) {
		return AMapUtils.calculateLineDistance(new LatLng(now.lat, now.lon),
				new LatLng(before.lat, before.lon));

	}

	private void updateUI() {
		Log.v("wygps", "distance =" + Variables.distance);
		int d1 = (int) Variables.distance / 10000;
		int d2 = (int) (Variables.distance % 10000) / 1000;
		int d3 = (int) (Variables.distance % 1000) / 100;
		int d4 = (int) (Variables.distance % 100) / 10;
		if (d1 > 0) {
			d1v.setVisibility(View.VISIBLE);
		}
		YaoPao01App.graphicTool.updateWhiteNum(new int[]{d1,d2,d3,d4},new ImageView[]{d1v,d2v,d3v,d4v});

		Variables.pspeed = (int) Math.round(Variables.utime / Variables.distance);
		int[] speed = YaoPao01App.cal(Variables.pspeed);

		int s1 = speed[1] / 10;
		int s2 = speed[1] % 10;
		int s3 = speed[2] / 10;
		int s4 = speed[2] % 10;
		YaoPao01App.graphicTool.updateWhiteNum(new int[]{s1,s2,s3,s4},new ImageView[]{s1V,s2V,s3V,s4V});

	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			// Toast.makeText(SportRecordActivity.this, "", duration)
		}
		return false;
	}

	// 控制进度条
	Handler procesHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 表明消息是由该程序发送的。
			if (msg.what == 0x111) {
				progressHorizontal.setProgress(status);
			}
		}
	};
	
	// 在地图页面关闭此页面
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if ("close".equals(intent.getExtras().getString("data"))) {
				unregisterReceiver(this);
				SportRecordActivity.this.finish();
			}
		}
	};
}
