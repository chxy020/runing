package net.yaopao.activity;

import java.util.ArrayList;
import java.util.List;

import net.yaopao.activity.SportListActivity.MessageOnPageChangeListener;
import net.yaopao.activity.SportListActivity.MessagePagerAdapter;
import net.yaopao.bean.DataBean;
import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

/**
 */
public class MatchFinishTeamActivity extends BaseActivity implements OnTouchListener {
	
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
	
	
	private TextView button_ok,label_tname,label_tname2;
//	private TextView button_back,label_tname,button_personal,button_km;
//	private ScrollView scrollview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_team_finish);
//		initinitSymbol();
		init();
		initViewPager();
	}

	private void init() {
		label_tname = (TextView) findViewById(R.id.match_score_list_title);
		button_ok = (TextView) findViewById(R.id.match_fininsh_confirm);
//		label_tname = (TextView) findViewById(R.id.match_score_list_title);
//		button_personal = (TextView) findViewById(R.id.match_score_list_personal);
//		button_km = (TextView) findViewById(R.id.match_score_list_mileage);
//		
//		scrollview = (ScrollView) findViewById(R.id.match_score_list_data);
//		button_back.setOnTouchListener(this);
//		button_personal.setOnTouchListener(this);
//		button_km.setOnTouchListener(this);
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {
		case R.id.match_fininsh_confirm:
			//返回
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				break;
			}
			break;
//		case R.id.match_score_list_personal:
//			//点击队员
//			switch (action) {
//			case MotionEvent.ACTION_DOWN:
//				break;
//			case MotionEvent.ACTION_UP:
//				finish();
//				break;
//			}
//			break;
//		case R.id.match_score_list_mileage:
//			//点击里程
//			switch (action) {
//			case MotionEvent.ACTION_DOWN:
//				break;
//			case MotionEvent.ACTION_UP:
//				finish();
//				break;
//			}
//			break;
		}
		return true;
	}
	private void initMileage(int distance) {
		// distance = 549254;
		ImageView d1V = (ImageView) this.findViewById(R.id.match_milage_num1);
		ImageView d2V = (ImageView) this.findViewById(R.id.match_milage_num2);
		ImageView d3V = (ImageView) this.findViewById(R.id.match_milage_num3);
		ImageView d4V = (ImageView) this.findViewById(R.id.match_milage_num4);
		ImageView d5V = (ImageView) this.findViewById(R.id.match_milage_dec1);
		ImageView d6V = (ImageView) this.findViewById(R.id.match_milage_dec2);
		d1V.setVisibility(View.GONE);
		d2V.setVisibility(View.GONE);
		d3V.setVisibility(View.GONE);
		int d1 = (int) distance / 1000000;
		int d2 = (int) (distance % 1000000) / 100000;
		int d3 = (int) (distance % 100000) / 10000;
		int d4 = (int) (distance % 10000) / 1000;
		int d5 = (int) (distance % 1000) / 100;
		int d6 = (int) (distance % 100) / 10;
		if (d1 > 0) {
			d1V.setVisibility(View.VISIBLE);
		}
		if (d2 > 0) {
			d2V.setVisibility(View.VISIBLE);
		}
		if (d3 > 0) {
			d3V.setVisibility(View.VISIBLE);
		}
		YaoPao01App.graphicTool.updateRedNum(
				new int[] { d1, d2, d3, d4, d5, d6 }, new ImageView[] { d1V,
						d2V, d3V, d4V, d5V, d6V });
	}
	private void initinitSymbol() {
		ImageView dot = (ImageView) this.findViewById(R.id.match_milage_dot);
//		ImageView min = (ImageView) this
//				.findViewById(R.id.match_recoding_speed_d1);
//		ImageView sec = (ImageView) this
//				.findViewById(R.id.match_recoding_speed_d2);
		ImageView km = (ImageView) this.findViewById(R.id.match_milage_km);
		dot.setImageBitmap(YaoPao01App.graphicTool.numBitmap
				.get(R.drawable.r_dot));
//		min.setImageBitmap(YaoPao01App.graphicTool.numBitmap
//				.get(R.drawable.w_min));
//		sec.setImageBitmap(YaoPao01App.graphicTool.numBitmap
//				.get(R.drawable.w_sec));
//		// colon.setImageBitmap(YaoPao01App.graphicTool.numBitmap.get(R.drawable.w_colon));
		km.setImageBitmap(YaoPao01App.graphicTool.numBitmap
				.get(R.drawable.r_km));
	}
	
	/**
	 * 初始化ViewPager
	 */
	public void initViewPager() {
		if (this.mPager == null) {
			this.mPager = (ViewPager) this.findViewById(R.id.match_vPager);
			this.mListViews = new ArrayList<View>();
			this.mInflater = this.getLayoutInflater();
			
			View team_finish1 = mInflater.inflate(R.layout.activity_match_team_finish1,null);
			
			 label_tname2 = (TextView) team_finish1.findViewById(R.id.congratulation_team);
			
			View team_finish2 = mInflater.inflate(R.layout.activity_match_team_finish2, null);
			
			// 初始化滑动的view
//			initPagerViews(new View[] { totalDis, totalCount, totalTime });
			this.mListViews.add(team_finish1);
			this.mListViews.add(team_finish2);
//			this.mListViews.add(totalTime);
			this.mMessageAdapter = new MessagePagerAdapter(mListViews);

			this.mPager.setAdapter(this.mMessageAdapter);
			this.mPager.setOnPageChangeListener(new MessageOnPageChangeListener());

			// 获取滑块圆点图片
			mSliderImage1 = (ImageView) findViewById(R.id.match_list_cursor1);
			mSliderImage2 = (ImageView) findViewById(R.id.match_list_cursor2);
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
			}
		} catch (Exception ex) {
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
	
}
