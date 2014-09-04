package net.yaopao.activity;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.yaopao.view.XListView;
import net.yaopao.view.XListView.IXListViewListener;
import net.yaopao.assist.DialogTool;
import net.yaopao.assist.SportListAdapter;
import net.yaopao.assist.Variables;
import net.yaopao.bean.DataBean;
import net.yaopao.bean.SportBean;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SportListActivity extends Activity implements OnClickListener,IXListViewListener {
	public TextView backV;

	private ListView listView;
	private SimpleDateFormat sdf1;
	private SimpleDateFormat sdf2;
	private SimpleDateFormat sdf3;
	private DecimalFormat df;
	/** 数据列表 */
	private XListView mListView;
	private Handler mHandler;
	private SportListAdapter mAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sport_list);
		mHandler = new Handler();
		
		initLayout();

		// chenxy 初始化头部滑块
		this.initView();
	}

	private void initLayout() {
		sdf1 = new SimpleDateFormat("MM");
		sdf2 = new SimpleDateFormat("dd");
		sdf3 = new SimpleDateFormat("HH:mm");
		df = (DecimalFormat) NumberFormat.getInstance();
		df.setMaximumFractionDigits(2);
		df.setRoundingMode(RoundingMode.DOWN);
		backV = (TextView) this.findViewById(R.id.recording_list_back);
		backV.setOnClickListener(this);
		
		mListView = (XListView) this.findViewById(R.id.recording_list_data);
		//关闭下拉刷新
		mListView.setPullRefreshEnable(false);
		//开启上拉刷新
		mListView.setPullLoadEnable(true);
		//SportListAdapter adapter = new SportListAdapter(this, getData());
		mAdapter = new SportListAdapter(this, getData());
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				TextView idV = (TextView) view.findViewById(R.id.sport_index);
				int id = Integer.parseInt((String) idV.getText());
				Intent mainIntent = new Intent(SportListActivity.this,
						SportListOneActivity.class);
				mainIntent.putExtra("id", id + "");
				startActivity(mainIntent);

			}
		});
		mListView.setXListViewListener(this);
		
	}
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		Log.e("","chxy____onRefresh");
		
	}
	
	/**
	 * 下一页刷新回调函数
	 */
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		Log.e("","chxy____onLoadMore");
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				//调用加载下一页方法
				mAdapter.addItem(getData());
				mAdapter.notifyDataSetChanged();
				
				//如果没有下一页数据直接调用,隐藏loading
				mListView.stopRefresh();
				mListView.stopLoadMore();
			}
		}, 2000);
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {

		case R.id.recording_list_back:
				SportListActivity.this.finish();
				break;
		}
	}

	private List<Map<String, Object>> getData() {

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		//这里查询列表数据
		List<SportBean> list = YaoPao01App.db.query(1);
		Map<String, Object> map = null;
		SportBean sport = null;
		for (int i = 0; i < list.size(); i++) {
			map = new HashMap<String, Object>();
			sport = list.get(i);

			if (sport.getRunty() == 1) {
				//map.put("type", R.drawable.runtype_walk);
				map.put("type", R.drawable.runtype_walk_big);
			} else if (sport.getRunty() == 2) {
				//map.put("type", R.drawable.runtype_run);
				map.put("type", R.drawable.runtype_run_big);
			} else if (sport.getRunty() == 3) {
				//map.put("type", R.drawable.runtype_ride);
				map.put("type", R.drawable.runtype_ride_big);
			}

			Date date = new Date(sport.getAddtime());

			map.put("date", sdf1.format(date) + "月" + sdf2.format(date) + "日 "
					+ YaoPao01App.getWeekOfDate(date) + " " + sdf3.format(date));

			map.put("dis", df.format((sport.getDistance() / 1000)) + "km");
			if (sport.getMind() == 1) {
				map.put("mind", R.drawable.mood1_h);
			} else if (sport.getMind() == 2) {
				map.put("mind", R.drawable.mood2_h);
			} else if (sport.getMind() == 3) {
				map.put("mind", R.drawable.mood3_h);
			} else if (sport.getMind() == 4) {
				map.put("mind", R.drawable.mood4_h);
			} else if (sport.getMind() == 5) {
				map.put("mind", R.drawable.mood5_h);
			}

			if (sport.getRunway() == 1) {
				map.put("way", R.drawable.way1_h);
			} else if (sport.getRunway() == 2) {
				map.put("way", R.drawable.way2_h);
			} else if (sport.getRunway() == 3) {
				map.put("way", R.drawable.way3_h);
			} else if (sport.getRunway() == 4) {
				map.put("way", R.drawable.way4_h);
			} else if (sport.getRunway() == 5) {
				map.put("way", R.drawable.way5_h);
			}
			map.put("id", sport.getId());
			Log.v("db", "db id =" + sport.getId());
			int[] speed = YaoPao01App.cal(sport.getPspeed());
			int s1 = speed[1] / 10;
			int s2 = speed[1] % 10;
			int s3 = speed[2] / 10;
			int s4 = speed[2] % 10;
			map.put("speed", s1 + "" + s2 + "'" + s3 + "" + s4 + "\"" + "/公里");
			
			//获取运动总时长chenxy add
			int uTime = sport.getUtime();
			//数据都是空的,先放个测试数据
			//uTime = 21230;
			//Log.e("","chxy_____utime" + uTime);
			String utime = getTimeOfSeconds(uTime);
			map.put("utime",utime);
			data.add(map);
		}

		return data;
	}

	public static String getWeekOfDate(Date dt) {
		String[] weekDays = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);

		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;

		return weekDays[w];
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
				String tm = m > 10 ? m + "" : "0" + m;
				String ts = s > 10 ? s + "" : "0" + s;
				time = tm + ":" + ts;
			}
			else{
				//超过1小时
				h = (int)second / 3600;
				m = (int)second % 3600 / 60;
				s = second % 3600 % 60;
				//小时没加0,放不下
				String th = h > 10 ? h + "" : "" + h;
				String tm = m > 10 ? m + "" : "0" + m;
				String ts = s > 10 ? s + "" : "0" + s;
				time = th + ":" + tm + ":" + ts;
			}
		}
		else{
			//刚好1分钟
			if(60 == second){
				time = "01:00";
			}
			else{
				String ts = second > 10 ? second + "" : "0" + second;
				time = "00:" + ts;
			}
		}
		return time;
	}

	/*************** chenxy add ******************/

	/**
	 * 页面初始化获取控件
	 * 
	 * @author cxy
	 * @date 2014-8-30
	 */
	/** 消息内容 */
	private ViewPager mPager = null;
	/** Tab页面列表 */
	private List<View> mListViews;
	/** 推送消息数据适配器 */
	private MessagePagerAdapter mMessageAdapter = null;
	/** 容器加载 */
	private LayoutInflater mInflater = null;
	/** 滑块imageview */
	private ImageView mSliderImage1 = null;
	private ImageView mSliderImage2 = null;
	private ImageView mSliderImage3 = null;

	private void initView() {

		this.initViewPager();
	}

	/**
	 * 初始化ViewPager
	 */
	public void initViewPager() {
		if (this.mPager == null) {
			this.mPager = (ViewPager) this.findViewById(R.id.vPager);
			this.mListViews = new ArrayList<View>();
			this.mInflater = this.getLayoutInflater();
			View totalDis = mInflater.inflate(R.layout.sport_list_slider_lay1,
					null);
			View totalCount = mInflater.inflate(
					R.layout.sport_list_slider_lay2, null);
			View totalTime = mInflater.inflate(R.layout.sport_list_slider_lay3,
					null);
			// 初始化滑动的view
			initPagerViews(new View[] { totalDis, totalCount, totalTime });
			this.mListViews.add(totalDis);
			this.mListViews.add(totalCount);
			this.mListViews.add(totalTime);
			this.mMessageAdapter = new MessagePagerAdapter(mListViews);

			this.mPager.setAdapter(this.mMessageAdapter);
			this.mPager
					.setOnPageChangeListener(new MessageOnPageChangeListener());

			// 获取滑块圆点图片
			mSliderImage1 = (ImageView) findViewById(R.id.sport_list_cursor1);
			mSliderImage2 = (ImageView) findViewById(R.id.sport_list_cursor2);
			mSliderImage3 = (ImageView) findViewById(R.id.sport_list_cursor3);
		}
	}


	/**
	 * 修改当前高亮圆点
	 * 
	 * @param current
	 * @author cxy
	 * @date 2014-6-12
	 */
	private void changeCurrentCursor(int current) {
		try {
			// 现将所有图标都换成不是高亮的
			mSliderImage1.setBackgroundResource(R.drawable.slider_cursor);
			mSliderImage2.setBackgroundResource(R.drawable.slider_cursor);
			mSliderImage3.setBackgroundResource(R.drawable.slider_cursor);
			// 把当前选中项图片换成高亮
			switch (current) {
			case 0:
				mSliderImage1
						.setBackgroundResource(R.drawable.slider_cursor_curr);
				break;
			case 1:
				mSliderImage2
						.setBackgroundResource(R.drawable.slider_cursor_curr);
				break;
			case 2:
				mSliderImage3
						.setBackgroundResource(R.drawable.slider_cursor_curr);
				break;
			}
		} catch (Exception ex) {
		}
	}

	/**
	 * 消息切换监听
	 */
	protected class MessageOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			// arg0是表示你当前选中的页面，这事件是在你页面跳转完毕的时候调用的。

			// 修改高亮圆点
			changeCurrentCursor(arg0);
			// 保存当前下标
			// mCurrentItem = arg0;
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
	private void initPagerViews(View[] views) {
		initMileageView(views[0]);
		initCountView(views[1]);
		initTimeView(views[2]);
	}

	//初始化滑动页面-总距离
	private void initMileageView(View view) {
		ImageView d1v = (ImageView) view
				.findViewById(R.id.main_milage_num1);
		ImageView d2v = (ImageView) view
				.findViewById(R.id.main_milage_num2);
		ImageView d3v = (ImageView) view
				.findViewById(R.id.main_milage_num3);
		ImageView d4v = (ImageView) view
				.findViewById(R.id.main_milage_num4);
		ImageView d5v = (ImageView) view
				.findViewById(R.id.main_milage_dec1);
		ImageView d6v = (ImageView) view
				.findViewById(R.id.main_milage_dec2);
		d1v.setVisibility(View.GONE);
		d2v.setVisibility(View.GONE);
		d3v.setVisibility(View.GONE);
		initMileage(new ImageView[]{d1v,d2v,d3v,d4v,d5v,d6v});
	}
	//初始化滑动页面-总次数
	private void initCountView(View view) {
		ImageView c1v = (ImageView) view
				.findViewById(R.id.main_count_num1);
		ImageView c2v = (ImageView) view
				.findViewById(R.id.main_count_num2);
		ImageView c3v = (ImageView) view
				.findViewById(R.id.main_count_num3);
		c1v.setVisibility(View.GONE);
		c2v.setVisibility(View.GONE);
		initCount(new ImageView[]{c1v,c2v,c3v});
	}
	//初始化滑动页面-总时间
	private void initTimeView(View view) {
		ImageView t1V = (ImageView) view.findViewById(R.id.time_h1);
		ImageView t2V = (ImageView) view.findViewById(R.id.time_h2);
		ImageView t3V = (ImageView) view.findViewById(R.id.time_h3);
		ImageView t4V = (ImageView) view.findViewById(R.id.time_h4);
		ImageView t5V = (ImageView) view.findViewById(R.id.time_m1);
		ImageView t6V = (ImageView) view.findViewById(R.id.time_m2);
		ImageView t7V = (ImageView) view.findViewById(R.id.time_s1);
		ImageView t8V = (ImageView) view.findViewById(R.id.time_s2);
		ImageView tdV = (ImageView) view.findViewById(R.id.time_d1);
		initTime(new ImageView[]{t1V,t2V,t3V,t4V,t5V,t6V,t7V,t8V,tdV});
	}
	
	private void initMileage(ImageView[] views) {
		DataBean data = YaoPao01App.db.queryData();
		double distance = data.getDistance();
		// distance = 549254;
		int d1 = (int) distance / 1000000;
		int d2 = (int) (distance % 1000000) / 100000;
		int d3 = (int) (distance % 100000) / 10000;
		int d4 = (int) (distance % 10000) / 1000;
		int d5 = (int) (distance % 1000) / 100;
		int d6 = (int) (distance % 100) / 10;
		if (d1 > 0) {
			views[0].setVisibility(View.VISIBLE);
		}
		if (d2 > 0) {
			views[1].setVisibility(View.VISIBLE);
		}
		if (d3 > 0) {
			views[2].setVisibility(View.VISIBLE);
		}
		update(d1, views[0]);
		update(d2, views[1]);
		update(d3, views[2]);
		update(d4, views[3]);
		update(d5, views[4]);
		update(d6, views[5]);
	}
	
	private void initCount(ImageView[] views){
		DataBean data = YaoPao01App.db.queryData();
		int count = data.getCount();
		Log.v("wysport", " count="+count);
		int c1 = count /100;
		int c2 =  (count%100)/10;
		int c3 =count%10;
		Log.v("wysport", " c1="+c1);
		Log.v("wysport", " c2="+c2);
		Log.v("wysport", " c3="+c3);
		if (c1 > 0) {
			views[0].setVisibility(View.VISIBLE);
			update(c1, views[0]);
		}
		if (c2 > 0) {
			views[1].setVisibility(View.VISIBLE);
			update(c2, views[1]);
		}
		update(c3, views[2]);
	}
	
	private void initTime(ImageView[] views){
		DataBean data = YaoPao01App.db.queryData();
		long total = data.getTotalTime();
		int[] time = YaoPao01App.cal(total);
		int t1 = time[0] / 1000;
		int t2 =(time[0] % 1000)/100;
		int t3 = (time[0] % 100)/10;
		int t4 = time[0] % 1000;
		
		int t5 = time[1] / 10;
		int t6 = time[1] % 10;
		int t7 = time[2] / 10;
		int t8 = time[2] % 10;
		
		if (t1>0) {
			update(t1, views[0]);
			views[0].setVisibility(View.VISIBLE);
		}
		if (t2>0) {
			update(t2, views[1]);
			views[1].setVisibility(View.VISIBLE);
		}
		if (t3>0) {
			update(t3, views[2]);
			views[2].setVisibility(View.VISIBLE);
		}
		if (t4>0) {
			update(t4, views[3]);
			views[3].setVisibility(View.VISIBLE);
			views[8].setVisibility(View.VISIBLE);
		}
		update(t5, views[4]);
		update(t6, views[5]);
		update(t7, views[6]);
		update(t8, views[7]);
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
}
