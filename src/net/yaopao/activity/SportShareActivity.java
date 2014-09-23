package net.yaopao.activity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.yaopao.assist.Constants;
import net.yaopao.assist.GpsPoint;
import net.yaopao.assist.LonLatEncryption;
import net.yaopao.assist.Variables;
import net.yaopao.bean.SportBean;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.alibaba.fastjson.JSONArray;
import com.amap.api.a.am;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnMapClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.PolylineOptions;
import com.umeng.analytics.MobclickAgent;

public class SportShareActivity extends Activity implements OnClickListener {
	private TextView backV;
	private TextView timeV;
	private TextView pspeedV;
	private TextView ponitV;
	private TextView dateV;
	private TextView desV;
	private TextView titleV;
	//private TextView disV;
	private ImageView typeV;
	private ImageView mindV;
	private ImageView wayV;
	private ImageView d1v;
	private ImageView d2v;
	private ImageView d3v;
	private ImageView d4v;
	/** 消息内容 */
	private ViewPager mPager = null;
	/** Tab页面列表 */
	private List<View> mListViews;
	/** 推送消息数据适配器 */
	private MessagePagerAdapter mMessageAdapter = null;
	/** 容器加载 */
	private LayoutInflater mInflater = null;

	private MapView mapView;
	private View mapLayout;
	private View phoLayout;
	private AMap aMap;
	private SportBean oneSport;
	private LonLatEncryption lonLatEncryption;
	public GpsPoint lastDrawPoint;

	private SimpleDateFormat sdf1;
	private SimpleDateFormat sdf2;
	private SimpleDateFormat sdf3;
	private SimpleDateFormat sdf4;
	//private DecimalFormat df;
	String title = "";
	int recordId = 0;
	
	
	/** 分享按钮 */
	private Button mShareBtn = null;
	
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sport_share);
		Intent intent = getIntent();
		recordId = Integer.parseInt(intent.getStringExtra("id"));
		oneSport = YaoPao01App.db.queryForOne(recordId);
		initViewPager();
		mapView = (MapView) mapLayout.findViewById(R.id.one_map);
		mapView.onCreate(savedInstanceState);
		sdf1 = new SimpleDateFormat("MM");
		sdf2 = new SimpleDateFormat("dd");
		sdf3 = new SimpleDateFormat("HH:mm");
		sdf4 = new SimpleDateFormat("yyyy");
//		df = (DecimalFormat) NumberFormat.getInstance();
//		df.setMaximumFractionDigits(2);
//		df.setRoundingMode(RoundingMode.DOWN);
		initLayout();
		
		initView();
	}

	private void initViewPager() {

		this.mPager = (ViewPager) this.findViewById(R.id.oneVPager);
		this.mListViews = new ArrayList<View>();
		this.mInflater = this.getLayoutInflater();
		mapLayout = mInflater.inflate(R.layout.sport_one_slider_map, null);
		phoLayout = mInflater.inflate(R.layout.sport_one_slider_pho, null);
		if (oneSport.getSportpho()==1) {
			ImageView phoV = (ImageView) phoLayout.findViewById(R.id.one_pho_v);
			phoV.setScaleType(ScaleType.CENTER_CROP);
			phoV.setImageBitmap(getImg(Constants.sportPho_s +oneSport.getSportPhoPath()));
		}
		
		this.mListViews.add(mapLayout);
		this.mListViews.add(phoLayout);
		this.mMessageAdapter = new MessagePagerAdapter(mListViews);
		this.mPager.setAdapter(this.mMessageAdapter);
		this.mPager.setOnPageChangeListener(new MessageOnPageChangeListener());
	}

	private void initLayout() {

		backV = (TextView) findViewById(R.id.recording_one_back);
		timeV = (TextView) findViewById(R.id.one_time);
		pspeedV = (TextView) findViewById(R.id.one_pspeed);
		ponitV = (TextView) findViewById(R.id.one_ponit);
		titleV = (TextView) findViewById(R.id.recording_one_title);
		dateV = (TextView) findViewById(R.id.one_date);
		desV = (TextView) findViewById(R.id.one_desc);
		//disV = (TextView) findViewById(R.id.one_dis);
		typeV = (ImageView) findViewById(R.id.one_type);
		mindV = (ImageView) findViewById(R.id.one_mind);
		wayV = (ImageView) findViewById(R.id.one_way);
		backV = (TextView) findViewById(R.id.recording_one_back);
		
		 d1v = (ImageView) findViewById(R.id.list_sport_num1);
		 d2v = (ImageView) findViewById(R.id.list_sport_num2);
		 d3v = (ImageView) findViewById(R.id.list_sport_dec1);
		 d4v = (ImageView) findViewById(R.id.list_sport_dec2);
		
//		backV.setOnTouchListener(this);
		backV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				 SportShareActivity.this.finish();
				
			}
		});
		initMap();
	}

	private void initMap() {

		lonLatEncryption = new LonLatEncryption();
//		Intent intent = getIntent();
//		recordId = Integer.parseInt(intent.getStringExtra("id"));
//		oneSport = YaoPao01App.db.queryForOne(recordId);
		initSportData(oneSport.getDistance(), oneSport.getRunty(),
				oneSport.getMind(), oneSport.getRunway(),
				oneSport.getRemarks(), oneSport.getUtime(),
				oneSport.getPspeed(), oneSport.getPoints(),
				oneSport.getAddtime());
		List<GpsPoint> pointsArray = JSONArray.parseArray(oneSport.getRuntra(),
				GpsPoint.class);
		if (aMap == null) {
			aMap = mapView.getMap();
		}

		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		aMap.getUiSettings().setScrollGesturesEnabled(false);
		aMap.getUiSettings().setZoomGesturesEnabled(false);
		aMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng arg0) {
				Intent intent = new Intent(SportShareActivity.this,
						SportTrackMap.class);
				intent.putExtra("id", recordId + "");
				startActivity(intent);
			}
		});
		if (pointsArray.size()==0) {
			return;
		}
		//从增强还原成全量
				GpsPoint befor = null;
				GpsPoint curr = null;
//				YaoPao01App.lts.writeFileToSD("track 取出的数组: " +pointsArray, "uploadLocation");
				for (int i = 0; i < pointsArray.size(); i++) {
					if (i==0) {
						befor=pointsArray.get(0);
						continue;
					}
					befor =pointsArray.get(i-1);
					curr = pointsArray.get(i);
					curr.setLat(curr.lat+befor.lat);
					curr.setLon(curr.lon+befor.lon);
					curr.setTime(curr.time+befor.time);
					pointsArray.set(i, curr);
				}
		GpsPoint start = lonLatEncryption.encrypt(pointsArray.get(0));
		GpsPoint end = lonLatEncryption.encrypt(pointsArray.get(pointsArray
				.size() - 1));
		aMap.addMarker(new MarkerOptions()
				.position(new LatLng(end.lat, end.lon))
				.icon(BitmapDescriptorFactory.fromBitmap(getViewBitmap(end())))
				.anchor(0.5f, 0.5f));
		aMap.addMarker(new MarkerOptions()
				.position(new LatLng(start.lat, start.lon))
				.icon(BitmapDescriptorFactory
						.fromBitmap(getViewBitmap(start()))).anchor(0.5f, 0.5f));
		drawLine(pointsArray);
		
		

	}
	private void drawLine(List<GpsPoint> pointsArray) {
		GpsPoint firstPoint = lonLatEncryption.encrypt(pointsArray.get(0));
		double min_lat = firstPoint.lat;
		double max_lat = firstPoint.lat;
		double min_lon = firstPoint.lon;
		double max_lon = firstPoint.lon;
		List<LatLng> runPoints =new ArrayList<LatLng>();
		GpsPoint crrPoint =null;
		// 先绘制黑色底线和灰色线
		aMap.addPolyline((new PolylineOptions())
				.addAll(initPoints(pointsArray)).color(Color.BLACK).width(10f));
		aMap.addPolyline((new PolylineOptions())
				.addAll(initPoints(pointsArray)).color(Color.GRAY).width(8f));
		for (int i = 0; i < pointsArray.size(); i++) {
			crrPoint = pointsArray.get(i);
			GpsPoint  encryptPoint = lonLatEncryption.encrypt(crrPoint);
			if (encryptPoint.lon < min_lon) {
				min_lon = encryptPoint.lon;
			}
			if (encryptPoint.lat < min_lat) {
				min_lat = encryptPoint.lat;
			}
			if (encryptPoint.lon > max_lon) {
				max_lon = encryptPoint.lon;
			}
			if (encryptPoint.lat > max_lat) {
				max_lat = encryptPoint.lat;
			}
			if (crrPoint.status==0) {
				LatLng latlon = new LatLng( lonLatEncryption.encrypt(crrPoint).lat, lonLatEncryption.encrypt(crrPoint).lon);
				runPoints.add(latlon);
			}else if(crrPoint.status==1){
				aMap.addPolyline((new PolylineOptions()).addAll(runPoints).color(Color.GREEN).width(8f));
				runPoints = new ArrayList<LatLng>();
			}if (i==(pointsArray.size()-1)) {
				aMap.addPolyline((new PolylineOptions()).addAll(runPoints).color(Color.GREEN).width(8f));
			}
			// 移动到中心
			LatLng latlon1 = new LatLng(min_lat, min_lon);
			LatLng latlon2 = new LatLng(max_lat, max_lon);
			LatLngBounds bounds = new LatLngBounds(latlon1, latlon2);
			aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
			}
		}
	private void initSportData(double distance, int runty, int mind,
			int runway, String remarks, int utime, int pspeed, int ponit,
			long addtime) {

		int[] time = YaoPao01App.cal(utime);
		int t1 = time[0] / 10;
		int t2 = time[0] % 10;
		int t3 = time[1] / 10;
		int t4 = time[1] % 10;
		int t5 = time[2] / 10;
		int t6 = time[2] % 10;
		Log.v("wysport", "utime =" + utime);

		timeV.setText(t1 + "" + t2 + ":" + t3 + "" + t4 + ":" + t5 + "" + t6);
		Log.v("wysport", "time =" + t1 + "" + t2 + ":" + t3 + "" + t4 + ":"
				+ t5 + "" + t6);
		int[] speed = YaoPao01App.cal(pspeed);
		int s1 = speed[1] / 10;
		int s2 = speed[1] % 10;
		int s3 = speed[2] / 10;
		int s4 = speed[2] % 10;
		pspeedV.setText(s1 + "" + s2 + "'" + s3 + "" + s4 + "\"" );
		ponitV.setText("+ " + ponit);
		initDis(distance);
		//disV.setText(df.format(distance / 1000) + " km");
		desV.setText(remarks);
		Date date = new Date(addtime);
		dateV.setText(sdf4.format(date) + "年" + sdf1.format(date) + "月"
				+ sdf2.format(date) + "日 " + YaoPao01App.getWeekOfDate(date)
				+ " " + sdf3.format(date));

		initType(runty);
		initMind(mind);
		initWay(runway);
//		titleV.setText(YaoPao01App.getWeekOfDate(date) + title);
		titleV.setText(sdf1.format(date) + "月" + sdf2.format(date) + "日" + title);

	}
	private void initDis(double distance) {
		int d1 = (int) (distance % 100000) / 10000;
		int d2 = (int) (distance % 10000) / 1000;
		int d3 = (int) (distance % 1000) / 100;
		int d4 = (int) (distance % 100) / 10;
		if (d1 > 0) {
			d1v.setVisibility(View.VISIBLE);
		}
		update(d1, d1v);
		update(d2, d2v);
		update(d3, d3v);
		update(d4, d4v);
		
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
	
	private void initMind(int mind) {
		switch (mind) {
		case 1:
			mindV.setBackgroundResource(R.drawable.mood1_h);
			break;
		case 2:
			mindV.setBackgroundResource(R.drawable.mood2_h);
			break;
		case 3:
			mindV.setBackgroundResource(R.drawable.mood3_h);
			break;
		case 4:
			mindV.setBackgroundResource(R.drawable.mood4_h);
			break;
		case 5:
			mindV.setBackgroundResource(R.drawable.mood5_h);
			break;

		default:
			break;
		}
	}

	private void initType(int type) {
		switch (type) {
		case 1:
			typeV.setBackgroundResource(R.drawable.runtype_walk_big);
			title = "的步行";
			break;
		case 2:
			typeV.setBackgroundResource(R.drawable.runtype_run_big);
			title = "的跑步";
			break;
		case 3:
			typeV.setBackgroundResource(R.drawable.runtype_ride_big);
			title = "的自行车骑行";
			break;

		default:
			break;
		}
	}

	private void initWay(int way) {
		switch (way) {
		case 1:
			wayV.setBackgroundResource(R.drawable.way1_h);
			break;
		case 2:
			wayV.setBackgroundResource(R.drawable.way2_h);
			break;
		case 3:
			wayV.setBackgroundResource(R.drawable.way3_h);
			break;
		case 4:
			wayV.setBackgroundResource(R.drawable.way4_h);
			break;
		case 5:
			wayV.setBackgroundResource(R.drawable.way5_h);
			break;

		default:
			break;
		}
	}

//	@Override
//	public boolean onTouch(View view, MotionEvent event) {
//		int action = event.getAction();
//		switch (view.getId()) {
//		case R.id.recording_one_back:
//			SportListOneActivity.this.finish();
//			break;
//		}
//		return true;
//	}

	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		MobclickAgent.onPause(this);
		// deactivate();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	private List<LatLng> initPoints(List<GpsPoint> list) {
		List points = new ArrayList<LatLng>();
		LatLng ponit = null;
		for (int i = 0; i < list.size(); i++) {
			GpsPoint one = lonLatEncryption.encrypt(list.get(i));
			ponit = new LatLng(one.lat, one.lon);
			points.add(ponit);
		}
		return points;

	}

	/**
	 * ViewPager适配器
	 */
	protected class MessagePagerAdapter extends PagerAdapter {
		public List<View> views;

		public MessagePagerAdapter(List<View> mListViews) {
			this.views = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return this.views == null ? 0 : this.views.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(this.views.get(arg1), 0);
			return this.views.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}

	/**
	 * 消息切换监听
	 */
	protected class MessageOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			// arg0是表示你当前选中的页面，这事件是在你页面跳转完毕的时候调用的。
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// arg0 ==1的时候表示正在滑动，arg0==2的时候表示滑动完毕了，arg0==0的时候表示什么都没做，就是停在那。
			if (1 == arg0) {
				// 停止自动轮播
			} else if (0 == arg0) {
				// 走完2之后,完全停下来之后就会走0,所以在最后重新启动轮播
				// 重新启动自动轮播
			}
		}
	}

	public View start() {
		return getLayoutInflater().inflate(R.layout.marker_s, null);
	}

	public View end() {
		return getLayoutInflater().inflate(R.layout.marker_e, null);
	}

	/**
	 * 把一个view转化成bitmap对象
	 * */
	public static Bitmap getViewBitmap(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}
	
	public Bitmap getImg(String path) {
		Bitmap bitmap = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(path);
			bitmap = BitmapFactory.decodeStream(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				fis.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return bitmap;
	}
	
	/*****chenxy add *******/
	/**
	 * 页面初始化
	 * 
	 * @author cxy
	 * @date 2014-8-25
	 */
	private void initView() {
		// 分享按钮
		mShareBtn = (Button) findViewById(R.id.shareBtn);
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
		// 注册分享事件
		mShareBtn.setOnClickListener(this);
	}
	
	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		//关闭sso授权
		oks.disableSSOWhenAuthorize();
		
		// 分享时Notification的图标和文字
		oks.setNotification(R.drawable.icon, getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(getString(R.string.share));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl("http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("我是分享文本");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImagePath("/sdcard/test.jpg");
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://sharesdk.cn");
		
		// 启动分享GUI
		oks.show(this);
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.shareBtn:
				Log.e("","chxy _______");
				showShare();
			break;
			default:
			break;
		}

	}
}
