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

import net.yaopao.assist.SportListAdapter;
import net.yaopao.assist.Variables;
import net.yaopao.bean.SportBean;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SportListActivity extends Activity implements OnTouchListener {
	public TextView backV;

	private ListView listView;
	private SimpleDateFormat sdf1;
	private SimpleDateFormat sdf2;
	private SimpleDateFormat sdf3;
	private DecimalFormat df;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sport_list);

		initLayout();
		
		//chenxy 初始化头部滑块
		this.initView();
	}

	private void initLayout() {
		sdf1 = new SimpleDateFormat("MM");
		sdf2 = new SimpleDateFormat("dd");
		sdf3 = new SimpleDateFormat("HH:mm");
		df = (DecimalFormat) NumberFormat.getInstance();
		df.setMaximumFractionDigits(2);
		df.setRoundingMode(RoundingMode.DOWN);
		listView = (ListView) this.findViewById(R.id.recording_list_data);

		backV = (TextView) this.findViewById(R.id.recording_list_back);
		SportListAdapter adapter = new SportListAdapter(this, getData());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

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
		backV.setOnTouchListener(this);

	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {

		case R.id.recording_list_back:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				SportListActivity.this.finish();
				break;
			}
			break;
		}
		return true;
	}

	private List<Map<String, Object>> getData() {

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		List<SportBean> list = YaoPao01App.db.query();
		Map<String, Object> map = null;
		SportBean sport = null;
		for (int i = 0; i < list.size(); i++) {
			map = new HashMap<String, Object>();
			sport = list.get(i);

			if (sport.getRunty() == 1) {
				map.put("type", R.drawable.runtype_walk);
			} else if (sport.getRunty() == 2) {
				map.put("type", R.drawable.runtype_run);
			} else if (sport.getRunty() == 3) {
				map.put("type", R.drawable.runtype_ride);
			}

			Date date = new Date(sport.getAddtime());

			map.put("date", sdf1.format(date) + "月" + sdf2.format(date) + "日 "
					+ getWeekOfDate(date) + " " + sdf3.format(date));

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
			data.add(map);
			Log.v("wy", "l dis1 =" + sport.getDistance());
			Log.v("wy", "l dis2 =" + df.format((sport.getDistance() / 1000))
					+ "km");
			Log.v("wy", "l speed1 =" + sport.getPspeed());
			Log.v("wy", "l speed2 =" + speed);
			Log.v("wy", "l speed3 =" + s1 + "" + s2 + "'" + s3 + "" + s4 + "\""
					+ "/公里");
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
	
	
	/*************** chenxy add ******************/
	
	/**
	 * 页面初始化获取控件
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
	
	private void initView(){
		
		this.initViewPager();
	}
	/**
	 * 初始化ViewPager
	 */
	public void initViewPager() {
		if(this.mPager == null){
			this.mPager = (ViewPager)this.findViewById(R.id.vPager);
			this.mListViews = new ArrayList<View>();
			this.mInflater = this.getLayoutInflater();
			this.mListViews.add(mInflater.inflate(R.layout.sport_list_slider_lay1, null));
			this.mListViews.add(mInflater.inflate(R.layout.sport_list_slider_lay2, null));
			this.mListViews.add(mInflater.inflate(R.layout.sport_list_slider_lay3, null));
			this.mMessageAdapter = new MessagePagerAdapter(mListViews);
			
			this.mPager.setAdapter(this.mMessageAdapter);
			this.mPager.setOnPageChangeListener(new MessageOnPageChangeListener());
			
			//获取滑块圆点图片
			mSliderImage1 = (ImageView)findViewById(R.id.sport_list_cursor1);
			mSliderImage2 = (ImageView)findViewById(R.id.sport_list_cursor2);
			mSliderImage3 = (ImageView)findViewById(R.id.sport_list_cursor3);
		}
	}
	
	/**
	 * 修改当前高亮圆点
	 * @param current
	 * @author cxy
	 * @date 2014-6-12
	 */
	private void changeCurrentCursor(int current){
		try{
			//现将所有图标都换成不是高亮的
			mSliderImage1.setBackgroundResource(R.drawable.slider_cursor);
			mSliderImage2.setBackgroundResource(R.drawable.slider_cursor);
			mSliderImage3.setBackgroundResource(R.drawable.slider_cursor);
			//把当前选中项图片换成高亮
			switch(current){
				case 0:
					mSliderImage1.setBackgroundResource(R.drawable.slider_cursor_curr);
				break;
				case 1:
					mSliderImage2.setBackgroundResource(R.drawable.slider_cursor_curr);
				break;
				case 2:
					mSliderImage3.setBackgroundResource(R.drawable.slider_cursor_curr);
				break;
			}
		}
		catch(Exception ex){}
	}
	
	/**
	 * 消息切换监听
	 */
	protected class MessageOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0){
			//arg0是表示你当前选中的页面，这事件是在你页面跳转完毕的时候调用的。
			
			//修改高亮圆点
			changeCurrentCursor(arg0);
			//保存当前下标
			//mCurrentItem = arg0;
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			//arg0 ==1的时候表示正在滑动，arg0==2的时候表示滑动完毕了，arg0==0的时候表示什么都没做，就是停在那。
			if(1 == arg0){
				//停止自动轮播
			}
			else if( 0 == arg0){
				//走完2之后,完全停下来之后就会走0,所以在最后重新启动轮播
				//重新启动自动轮播
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
			((ViewPager)arg0).removeView((View)arg2);
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return this.views == null ? 0 : this.views.size();
		}
		
		@Override
		public Object instantiateItem(View arg0, int arg1){
			((ViewPager)arg0).addView(this.views.get(arg1),0);
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
}
