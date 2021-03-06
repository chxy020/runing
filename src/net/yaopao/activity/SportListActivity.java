package net.yaopao.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.yaopao.assist.DataTool;
import net.yaopao.assist.DialogTool;
import net.yaopao.assist.SportListAdapter;
import net.yaopao.assist.Variables;
import net.yaopao.bean.DataBean;
import net.yaopao.bean.SportBean;
import net.yaopao.engine.manager.CNCloudRecord;
import net.yaopao.view.XListView;
import net.yaopao.view.XListView.IXListViewListener;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

@SuppressLint("SimpleDateFormat")
public class SportListActivity extends BaseActivity implements OnClickListener,IXListViewListener {
	public TextView backV;
	public TextView syncV;

	private SimpleDateFormat sdf1;
	private SimpleDateFormat sdf2;
	private SimpleDateFormat sdf3;
	/** 数据列表 */
	private XListView mListView;
	private Handler mHandler;
	private SportListAdapter mAdapter = null;
	private int mPage = 1;
	private DataBean totalData = DataTool.getTotalData();
//	private int oneRecordDistance;
//	private int oneRecordTime;
//	private int oneRecordScore;

	private View totalDis;

	private View totalCount;

	private View totalTime;
	//更新记录结束后控制ui刷新
	public static Handler synHandler;
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
//		df = (DecimalFormat) NumberFormat.getInstance();
//		df.setMaximumFractionDigits(2);
//		df.setRoundingMode(RoundingMode.DOWN);
		backV = (TextView) this.findViewById(R.id.recording_list_back);
		syncV = (TextView) this.findViewById(R.id.recording_list_sync);
		backV.setOnClickListener(this);
		syncV.setOnClickListener(this);
		
		mListView = (XListView) this.findViewById(R.id.recording_list_data);
		//关闭下拉刷新
		mListView.setPullRefreshEnable(false);
		
		//SportListAdapter adapter = new SportListAdapter(this, getData());
		final List<Map<String, Object>> data = getData(mPage);
		
		//Log.e("","chxy____" + data.size());
		if(data.size() >= 10){
			//开启上拉刷新
			mListView.setPullLoadEnable(true);
		}
		else{
			//关闭上拉刷新
			mListView.setPullLoadEnable(false);
		}
		mAdapter = new SportListAdapter(this, data);
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
		
		// 添加长按点击,得到点中的index，即参数arg2
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                    int position, long id) {
            	TextView idV = (TextView) view.findViewById(R.id.sport_index);
            	TextView ridV = (TextView) view.findViewById(R.id.sport_rid);
            	deleteDialog(Integer.parseInt((String) idV.getText()),(String)ridV.getText());
                return true;
            }
 
        });
	}
	
	public  void deleteDialog(final int id,final String rid) {
		new AlertDialog.Builder(this).setTitle(R.string.app_name).setIcon(R.drawable.icon_s).setMessage("确认删除这条记录？").
		setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO 
                    	//删除数据库记录，同时删除对应的二进制文件，图片，修改本地保存的总数据，
                    	//刷新当前页面的数据值，总公里，总时间，次数等，主页数据也需要刷新，为了同步，还需要保存删除记录的各种信息到特定的数据结构中
                    	deleteOneSportRecord(id);
                    	DataTool.updateDleteArray(rid);
                    	mAdapter = new SportListAdapter(SportListActivity.this, getData(mPage));
                    	mListView.setAdapter(mAdapter);
						dialog.cancel();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						dialog.cancel();
					}
				}).show();

	}
	
	
	/* // listview中点击按键弹出对话框  
    public void showInfo(final int id) {  
        new AlertDialog.Builder(this).setTitle("我的提示").setMessage("确定要删除吗？")                  
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {  
                    @Override  
                    public void onClick(DialogInterface dialog, int which) {  
                    	// TODO 
                    	//删除数据库记录，同时删除对应的二进制文件，图片，修改本地保存的总数据，
                    	//刷新当前页面的数据值，总公里，总时间，次数等，主页数据也需要刷新，为了同步，还需要保存删除记录的各种信息到特定的数据结构中
                    	
                    	deleteOneSportRecord(id);
                    	
                    	mAdapter = new SportListAdapter(SportListActivity.this, getData(mPage));
                    	mListView.setAdapter(mAdapter);
                    }  
                }).show();  
    }  */
  
    private void deleteOneSportRecord(int id ){
		SportBean data = YaoPao01App.db.queryForOne(id);
		// 删除数据库和本地参数的数据
		totalData = DataTool.deleteOneSportRecord(data.getDistance(), data.getDuration(),data.getScore(),data.getSecondPerKm(),1);
		
		// 删除二进制文件和图片
		if (data.getClientBinaryFilePath() != null
				&& !"".equals(data.getClientBinaryFilePath())) {
			File binaryFile = new File(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ "/YaoPao/binary/"
					+ data.getClientBinaryFilePath()
					+ ".yaopao");
			if (binaryFile.exists()) {
				binaryFile.delete();
			}
		}
		if (data.getClientImagePaths() != null
				&& !"".equals(data.getClientImagePaths())) {
			File sportPhoto = new File(data.getClientImagePaths());
			if (sportPhoto.exists()) {
				sportPhoto.delete();
			}
		}

		if (data.getClientImagePathsSmall() != null
				&& !"".equals(data.getClientImagePathsSmall())) {
			File sportPhotoSmall = new File(data.getClientImagePathsSmall());
			if (sportPhotoSmall.exists()) {
				sportPhotoSmall.delete();
			}
		}

		YaoPao01App.db.delete(id);
		// 刷新ui
		initPagerViews(new View[] { totalDis, totalCount, totalTime });
		mMessageAdapter.notifyDataSetChanged();
    }
	
	@Override
	public void onRefresh() {
		//Log.e("","chxy____onRefresh");
		
	}
	
	/**
	 * 下一页刷新回调函数
	 */
	@Override
	public void onLoadMore() {
		//Log.e("","chxy____onLoadMore");
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mPage++;
				List<Map<String, Object>> data = getData(mPage);
				//Log.e("","chxy____" + data.size());
				if(data.size() < 10){
					//关闭上拉刷新
					mListView.setPullLoadEnable(false);
				}
				 mAdapter.addItem(data);
				//调用加载下一页方法
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
		case R.id.recording_list_sync:
			Variables.updateUI=2;
			Variables.activity=SportListActivity.this;
			CNCloudRecord cloudRecord = new CNCloudRecord();
			cloudRecord.startCloud();
			
				synHandler = new Handler() {
					public void handleMessage(Message msg) {
						mAdapter = new SportListAdapter(SportListActivity.this, getData(mPage));
	                	mListView.setAdapter(mAdapter);
	                	totalData = DataTool.getTotalData();
						initPagerViews(new View[] { totalDis, totalCount, totalTime });
						mMessageAdapter.notifyDataSetChanged();
						super.handleMessage(msg);
					}
				};
			
			break;
		}
		
	}

	private List<Map<String, Object>> getData(int page) {

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		//这里查询列表数据
		List<SportBean> list = YaoPao01App.db.query(page);
		Map<String, Object> map = null;
		SportBean sport = null;
		for (int i = 0; i < list.size(); i++) {
			map = new HashMap<String, Object>();
			sport = list.get(i);

			if (sport.getHowToMove() == 1) {
				//map.put("type", R.drawable.runtype_walk);
				map.put("type", R.drawable.runtype_run_big);
			} else if (sport.getHowToMove() == 2) {
				//map.put("type", R.drawable.runtype_run);
				map.put("type",R.drawable.runtype_walk_big );
			} else if (sport.getHowToMove() == 3) {
				//map.put("type", R.drawable.runtype_ride);
				map.put("type", R.drawable.runtype_ride_big);
			}

			Date date = new Date(sport.getGenerateTime());

			map.put("date", sdf1.format(date) + "月" + sdf2.format(date) + "日 "
					+ YaoPao01App.getWeekOfDate(date) + " " + sdf3.format(date));

			map.put("dis",sport.getDistance());
			if (sport.getFeeling() == 1) {
				map.put("mind", R.drawable.mood1_h);
			} else if (sport.getFeeling() == 2) {
				map.put("mind", R.drawable.mood2_h);
			} else if (sport.getFeeling() == 3) {
				map.put("mind", R.drawable.mood3_h);
			} else if (sport.getFeeling() == 4) {
				map.put("mind", R.drawable.mood4_h);
			} else if (sport.getFeeling() == 5) {
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
//			map.put("hasPho", sport.sportpho);
			if (sport.getClientImagePaths()!=null&&!"".equals(sport.getClientImagePaths())) {
				map.put("phoName", sport.getClientImagePaths());
				map.put("phoNameSmall", sport.getClientImagePathsSmall());
			}
				map.put("ismatch", sport.getIsMatch()+"");
			map.put("id", sport.getId());
			map.put("rid", sport.getRid());
			Log.v("db", "db id =" + sport.getId());
			int[] speed = YaoPao01App.cal(sport.getSecondPerKm());
			int s1 = speed[1] / 10;
			int s2 = speed[1] % 10;
			int s3 = speed[2] / 10;
			int s4 = speed[2] % 10;
//			map.put("speed", s1 + "" + s2 + "'" + s3 + "" + s4 + "\"" + "/公里");
			map.put("speed", s1 + "" + s2 + "'" + s3 + "" + s4 + "\"" );
			
			//获取运动总时长chenxy add
			int uTime = sport.getDuration()/1000;
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
				String tm = m >= 10 ? m + "" : "0" + m;
				String ts = s >= 10 ? s + "" : "0" + s;
				time = tm + ":" + ts;
			}
			else{
				//超过1小时
				h = (int)second / 3600;
				m = (int)second % 3600 / 60;
				s = second % 3600 % 60;
				//小时没加0,放不下
				String th = h > 10 ? h + "" : "" + h;
				String tm = m >= 10 ? m + "" : "0" + m;
				String ts = s >= 10 ? s + "" : "0" + s;
				time = th + ":" + tm + ":" + ts;
			}
		}
		else{
			//刚好1分钟
			if(60 == second){
				time = "01:00";
			}
			else{
				String ts = second >= 10 ? second + "" : "0" + second;
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
			
			 totalDis = mInflater.inflate(R.layout.sport_list_slider_lay1,null);
			ImageView disDot = (ImageView) totalDis.findViewById(R.id.main_milage_dot);
			ImageView disKm = (ImageView) totalDis.findViewById(R.id.main_milage_km);
			disDot.setImageBitmap(YaoPao01App.graphicTool.numBitmap.get(R.drawable.r_dot));
			disKm.setImageBitmap(YaoPao01App.graphicTool.numBitmap.get(R.drawable.r_km));
			
			 totalCount = mInflater.inflate(R.layout.sport_list_slider_lay2, null);
			
			 totalTime = mInflater.inflate(R.layout.sport_list_slider_lay3,null);
			ImageView colon1 = (ImageView) totalTime.findViewById(R.id.time_d1);
			ImageView colon2 = (ImageView) totalTime.findViewById(R.id.time_d2);
			colon1.setImageBitmap(YaoPao01App.graphicTool.numBitmap.get(R.drawable.r_colon));
			colon2.setImageBitmap(YaoPao01App.graphicTool.numBitmap.get(R.drawable.r_colon));
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
		//DataBean data = YaoPao01App.db.queryData();
		
		double distance = totalData.getDistance();
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
//		update(d1, views[0]);
//		update(d2, views[1]);
//		update(d3, views[2]);
//		update(d4, views[3]);
//		update(d5, views[4]);
//		update(d6, views[5]);
		YaoPao01App.graphicTool.updateRedNum(new int[]{d1,d2,d3,d4,d5,d6},views);
	}
	
	private void initCount(ImageView[] views){
//		DataBean data = YaoPao01App.db.queryData();
		int count = totalData.getCount();
		Log.v("wysport", " count="+count);
		int c1 = count /100;
		int c2 =  (count%100)/10;
		int c3 =count%10;
		Log.v("wysport", " c1="+c1);
		Log.v("wysport", " c2="+c2);
		Log.v("wysport", " c3="+c3);
		if (c1 > 0) {
			views[0].setVisibility(View.VISIBLE);
//			update(c1, views[0]);
			YaoPao01App.graphicTool.updateRedNum(c1,views[0]);
		}
		if (c2 > 0) {
			views[1].setVisibility(View.VISIBLE);
//			update(c2, views[1]);
			YaoPao01App.graphicTool.updateRedNum(c2,views[1]);
		}
		
//		update(c3, views[2]);
		YaoPao01App.graphicTool.updateRedNum(c3,views[2]);
	}
	
	private void initTime(ImageView[] views){
//		DataBean data = YaoPao01App.db.queryData();
		long total = totalData.getTotalTime()/1000;
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
//			update(t1, views[0]);
			views[0].setVisibility(View.VISIBLE);
			YaoPao01App.graphicTool.updateRedNum(t1,views[0]);
		}
		if (t2>0) {
//			update(t2, views[1]);
			views[1].setVisibility(View.VISIBLE);
			YaoPao01App.graphicTool.updateRedNum(t2,views[1]);
		}
		if (t3>0) {
//			update(t3, views[2]);
			views[2].setVisibility(View.VISIBLE);
			YaoPao01App.graphicTool.updateRedNum(t3,views[2]);
		}
		if (t4>0) {
//			update(t4, views[3]);
			views[3].setVisibility(View.VISIBLE);
			views[8].setVisibility(View.VISIBLE);
			YaoPao01App.graphicTool.updateRedNum(t4,views[3]);
		}
//		update(t5, views[4]);
//		update(t6, views[5]);
//		update(t7, views[6]);
//		update(t8, views[7]);
		YaoPao01App.graphicTool.updateRedNum(new int[]{t5,t6,t7,t8},new ImageView[]{views[4],views[5],views[6],views[7]});
	}
	
//	protected void update(int i, ImageView view) {
//		if (i > 9) {
//			i = i % 10;
//		}
//		switch (i) {
//		case 0:
//			view.setBackgroundResource(R.drawable.r_0);
//			break;
//		case 1:
//			view.setBackgroundResource(R.drawable.r_1);
//			break;
//		case 2:
//			view.setBackgroundResource(R.drawable.r_2);
//			break;
//		case 3:
//			view.setBackgroundResource(R.drawable.r_3);
//			break;
//		case 4:
//			view.setBackgroundResource(R.drawable.r_4);
//			break;
//		case 5:
//			view.setBackgroundResource(R.drawable.r_5);
//			break;
//		case 6:
//			view.setBackgroundResource(R.drawable.r_6);
//			break;
//		case 7:
//			view.setBackgroundResource(R.drawable.r_7);
//			break;
//		case 8:
//			view.setBackgroundResource(R.drawable.r_8);
//			break;
//		case 9:
//			view.setBackgroundResource(R.drawable.r_9);
//			break;
//
//		default:
//			break;
//		}
//	}
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		super.activityOnFront=this.getClass().getSimpleName();
		Variables.activityOnFront=this.getClass().getSimpleName();
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	
	
}
