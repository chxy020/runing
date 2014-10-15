package net.yaopao.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.yaopao.activity.SportListActivity.MessageOnPageChangeListener;
import net.yaopao.activity.SportListActivity.MessagePagerAdapter;
import net.yaopao.assist.CNAppDelegate;
import net.yaopao.assist.Constants;
import net.yaopao.assist.LoadingDialog;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;
import net.yaopao.bean.DataBean;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
	Resources r; 
//	private TextView button_back,label_tname,button_personal,button_km;
//	private ScrollView scrollview;
	
	private ImageView km,dot,d1V,d2V,d3V,d4V,d5V,d6V,pd1V,pd2V,pd3V,pd4V,pd5V,pd6V,pkm,pdot;
	
	private LoadingDialog loadingDialog;
	FrameLayout view_list = null; 
	List<ImageView> imageviewList = new ArrayList<ImageView>();
	List<String> urlList = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_team_finish);
		r = getResources(); 
//		initinitSymbol();
		init();
		initViewPager();
		label_tname.setText(CNAppDelegate.matchDic.getString("groupname"));
	    label_tname2.setText(String.format("恭喜%s！", CNAppDelegate.matchDic.getString("groupname")));
	    
	    displayLoading();
	    new Handler().postDelayed(new Runnable(){  
	        public void run() {  
	        	//execute the task
	        	requestPersonal();
	        }  
	     }, 10*1000); 
	}
	void requestPersonal(){
		displayLoading();
		new RequestPersonal().execute("");
	}
	private void init() {
		label_tname = (TextView) findViewById(R.id.match_score_list_title);
		button_ok = (TextView) findViewById(R.id.match_fininsh_confirm);
		loadingDialog= new LoadingDialog(this);
		loadingDialog.setCancelable(false);
		button_ok.setOnTouchListener(this);
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
		super.activityOnFront=this.getClass().getSimpleName();
		Variables.activityOnFront=this.getClass().getSimpleName();
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
				Intent intent = new Intent(MatchFinishTeamActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
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
	private void initMileage(double distance) {
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
	private void initPersonalMileage(double distance) {
		pd1V.setVisibility(View.GONE);
		pd2V.setVisibility(View.GONE);
		pd3V.setVisibility(View.GONE);
		int d1 = (int) distance / 1000000;
		int d2 = (int) (distance % 1000000) / 100000;
		int d3 = (int) (distance % 100000) / 10000;
		int d4 = (int) (distance % 10000) / 1000;
		int d5 = (int) (distance % 1000) / 100;
		int d6 = (int) (distance % 100) / 10;
		if (d1 > 0) {
			pd1V.setVisibility(View.VISIBLE);
		}
		if (d2 > 0) {
			pd2V.setVisibility(View.VISIBLE);
		}
		if (d3 > 0) {
			pd3V.setVisibility(View.VISIBLE);
		}
		YaoPao01App.graphicTool.updateRedNum(
				new int[] { d1, d2, d3, d4, d5, d6 }, new ImageView[] { pd1V,
						pd2V, pd3V,pd4V, pd5V, pd6V });
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
			
			dot = (ImageView) team_finish1.findViewById(R.id.match_milage_dot);
			dot.setImageBitmap(YaoPao01App.graphicTool.numBitmap.get(R.drawable.r_dot));
			km =  (ImageView) team_finish1.findViewById(R.id.match_milage_km);
			km.setImageBitmap(YaoPao01App.graphicTool.numBitmap.get(R.drawable.r_km));
			
			d1V = (ImageView) team_finish1.findViewById(R.id.match_milage_num1);
			d2V = (ImageView) team_finish1.findViewById(R.id.match_milage_num2);
			d3V = (ImageView) team_finish1.findViewById(R.id.match_milage_num3);
			d4V = (ImageView) team_finish1.findViewById(R.id.match_milage_num4);
			d5V = (ImageView) team_finish1.findViewById(R.id.match_milage_dec1);
			d6V = (ImageView) team_finish1.findViewById(R.id.match_milage_dec2);
			initMileage(0);
			
			label_tname2 = (TextView) team_finish1.findViewById(R.id.congratulation_team);
			
			View team_finish2 = mInflater.inflate(R.layout.activity_match_team_finish2, null);
			
			view_list = (FrameLayout)team_finish2.findViewById(R.id.scrollview_List);
			
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
//	void displayLoading(){
//    disableAllButton();
//}
//void hideLoading(){
//    enableAllButton();
//}
//void disableAllButton(){
//}
//void enableAllButton(){
//}
void displayLoading(){
	loadingDialog.show();
}
void hideLoading(){
	loadingDialog.dismiss();
}
	private class RequestPersonal extends AsyncTask<String, Void, Boolean> {
		private String responseJson;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
		    String request_params = String.format("uid=%s&mid=%s&gid=%s",CNAppDelegate.uid,CNAppDelegate.mid,CNAppDelegate.gid);
		    Log.v("zc","按照人员查询成绩 is "+request_params);
		    responseJson = NetworkHandler.httpPost(Constants.endpoints	+ Constants.listPersonal, request_params);
		    Log.v("zc","按照人员查询成绩 is "+responseJson);
			if (responseJson != null && !"".equals(responseJson)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			Variables.sportStatus =1;
			hideLoading();
			if (result) {
				JSONObject resultDic = JSON.parseObject(responseJson);
			    double distance = resultDic.getDoubleValue("distancegr")+5;
			    initMileage(distance);
			    
			    JSONArray dataList = resultDic.getJSONArray("list");
			    if(dataList!=null&&dataList.size()>0){
			        for(int i=0;i<dataList.size();i++){
			            JSONObject oneRecordDic = dataList.getJSONObject(i);//数值从oneRecordDic得到
			            View view_one_record = mInflater.inflate(R.layout.match_list_personal_item,null);
			            
			            pdot = (ImageView) view_one_record.findViewById(R.id.list_sport_dot);
						pdot.setImageBitmap(YaoPao01App.graphicTool.numBitmap.get(R.drawable.r_dot));
						pkm =  (ImageView) view_one_record.findViewById(R.id.match_milage_km);
						pkm.setImageBitmap(YaoPao01App.graphicTool.numBitmap.get(R.drawable.r_km));
						
						pd1V = (ImageView) view_one_record.findViewById(R.id.list_sport_num1);
						pd2V = (ImageView) view_one_record.findViewById(R.id.list_sport_num2);
						pd3V = (ImageView) view_one_record.findViewById(R.id.list_sport_num3);
						pd4V = (ImageView) view_one_record.findViewById(R.id.list_sport_num4);
						pd5V = (ImageView) view_one_record.findViewById(R.id.list_sport_dec1);
						pd6V = (ImageView) view_one_record.findViewById(R.id.list_sport_dec2);
						
			            
			            ImageView userAvatar = (ImageView)view_one_record.findViewById(R.id.match_watch_head);
			            userAvatar.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.avatar_default, null));
			            String avatarUrl = oneRecordDic.getString("imgpath");
			            if(avatarUrl == null){
			                avatarUrl = "";
			            }else{
			                Bitmap image = CNAppDelegate.avatarDic.get(avatarUrl);
			                if(image != null){//缓存中有
			                    userAvatar.setImageBitmap(image);
			                }else{//下载
			                	RequestImageTask requestTask = new RequestImageTask();
			                	requestTask.index = i;
			                	requestTask.avatarUrl = avatarUrl;
			                	requestTask.execute("");
			                	displayLoading();
			                }
			            }
			            urlList.add(avatarUrl);
			            imageviewList.add(userAvatar);
			            
			            TextView label_name = (TextView)view_one_record.findViewById(R.id.username);
			            label_name.setText(oneRecordDic.getString("nickname"));
			            
			            initPersonalMileage(oneRecordDic.getDoubleValue("km")+5);
			            
			            int height = (int) r.getDimension(R.dimen.sport_set_height);
						FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, height); 
						lp.setMargins(0,height*i,0,0);
						view_list.addView(view_one_record, lp);
			        }
			    }
			}
		}
	}
	private class RequestImageTask extends AsyncTask<String, Void, Boolean> {
		public int index;
		public String avatarUrl;
		Bitmap image = null;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
			
			try{
				image = BitmapFactory.decodeStream(getImageStream(Constants.endpoints_img+avatarUrl));
			}catch(Exception e){
				e.printStackTrace();
			}
			if(image != null){
				CNAppDelegate.avatarDic.put(avatarUrl, image);
		        return true;
		    }else{
		    	return false;
		    }
			
			
		}
		@Override
		protected void onPostExecute(Boolean result) {
			hideLoading();
			if(result){
				imageviewList.get(index).setImageBitmap(image);
			}
		}
	}
	public InputStream getImageStream(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return conn.getInputStream();
		}
		return null;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			// Toast.makeText(SportRecordActivity.this, "", duration)
		}
		return false;
	}
}
