package net.yaopao.activity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.yaopao.assist.CNAppDelegate;
import net.yaopao.assist.Constants;
import net.yaopao.assist.NetworkHandler;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;

/**
 */
public class MatchGroupListActivity extends BaseActivity implements OnTouchListener {
	
	
	private TextView button_back,label_tname,button_personal,button_km;
	FrameLayout scrollview = null;
	
	Timer timer_personal = null;
	Timer timer_km = null;
	TimerTask_request_personal task_request_personal = null;
	TimerTask_request_km task_request_km = null;
	int tabIndex = 0;
	List<ImageView> imageviewList = new ArrayList<ImageView>();
	List<String> urlList = new ArrayList<String>();
	LayoutInflater mInflater = null;
	Resources r; 
	class TimerTask_request_personal extends TimerTask{
		@Override
		public void run() {
			requestPersonal();
		}
	}
	class TimerTask_request_km extends TimerTask{
		@Override
		public void run() {
			requestKm();
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_score_list);
		initinitSymbol();
		r = getResources();
		mInflater = this.getLayoutInflater();
		init();
		label_tname.setText(CNAppDelegate.matchDic.getString("groupname"));
		task_request_personal = new TimerTask_request_personal();
		timer_personal = new Timer();
		timer_personal.schedule(task_request_personal, 0, CNAppDelegate.kMatchReportInterval*1000);
//	    self.big_div = [[CNDistanceImageView alloc]initWithFrame:CGRectMake(4, 60+IOS7OFFSIZE, 260, 64)];
//	    self.big_div.distance = 0;
//	    self.big_div.color = @"red";
//	    [self.big_div fitToSize];
//	    [self.view addSubview:self.big_div];
//	    self.image_km = [[UIImageView alloc]initWithFrame:CGRectMake(self.big_div.frame.origin.x+self.big_div.frame.size.width, 60+IOS7OFFSIZE,52, 64)];
//	    self.image_km.image = [UIImage imageNamed:@"redkm.png"];
//	    [self.view addSubview:self.image_km];needwy
	}
	void requestPersonal(){
		new RequestPersonal().execute("");
	}
	void requestKm(){
		new RequestKM().execute("");
	}
	void clearScrollview(){
		scrollview.removeAllViews();
	    imageviewList.clear();
	    urlList.clear();
	}
	private void init() {
		button_back = (TextView) findViewById(R.id.match_score_list_back);
		label_tname = (TextView) findViewById(R.id.match_score_list_title);
		button_personal = (TextView) findViewById(R.id.match_score_list_personal);
		button_km = (TextView) findViewById(R.id.match_score_list_mileage);
		
		scrollview = (FrameLayout) findViewById(R.id.scrollview_List);
		button_back.setOnTouchListener(this);
		button_personal.setOnTouchListener(this);
		button_km.setOnTouchListener(this);
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		if(timer_personal!=null){
			timer_personal.cancel();
			timer_personal = null;
			if(task_request_personal != null){
				task_request_personal.cancel();
				task_request_personal = null;
			}
		}
		if(timer_km!=null){
			timer_km.cancel();
			timer_km = null;
			if(task_request_km != null){
				task_request_km.cancel();
				task_request_km = null;
			}
		}
		
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
		case R.id.match_score_list_back:
			//返回
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				finish();
				break;
			}
			break;
		case R.id.match_score_list_personal:
			//点击队员
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				button_personal.setBackgroundResource(R.color.gray_dark);
				break;
			case MotionEvent.ACTION_UP:
				button_personal.setBackgroundResource(R.color.blue_dark);
				if(tabIndex == 0)return true;
	            tabIndex = 0;
	            if(timer_km != null){
	            	timer_km.cancel();
	            	timer_km = null;
	            	if(task_request_km != null){
	            		task_request_km.cancel();
	            		task_request_km = null;
	            	}
	            }
	            task_request_personal = new TimerTask_request_personal();
	            timer_personal = new Timer();
	            timer_personal.schedule(task_request_personal, 0, CNAppDelegate.kMatchReportInterval*1000);
				break;
			}
			break;
		case R.id.match_score_list_mileage:
			//点击里程
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				button_km.setBackgroundResource(R.color.gray_dark);
				break;
			case MotionEvent.ACTION_UP:
				button_km.setBackgroundResource(R.color.blue_dark);
				if(tabIndex == 1)return true;
	            tabIndex = 1;
	            if(timer_personal!=null){
	            	timer_personal.cancel();
	            	timer_personal = null;
	            	if(task_request_personal != null){
	            		task_request_personal.cancel();
	            		task_request_personal = null;
	            	}
	            }
	            task_request_km = new TimerTask_request_km();
	            timer_km = new Timer();
	            timer_km.schedule(task_request_km, 0, CNAppDelegate.kMatchReportInterval*1000);
				break;
			}
			break;
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
			hideLoading();
			if (result) {
				CNAppDelegate.matchRequestResponseFilter(responseJson,Constants.matchReport,MatchGroupListActivity.this);
				JSONObject resultDic = JSON.parseObject(responseJson);
				clearScrollview();
			    double distance = (resultDic.getDoubleValue("distancegr")+5)/1000.0;
//			    self.big_div.distance = distance;
//			    self.big_div.color = @"red";
//			    [self.big_div fitToSize];
//			    self.image_km.frame = CGRectMake(self.big_div.frame.origin.x+self.big_div.frame.size.width, 60+IOS7OFFSIZE,52, 64);needwy
			    
			    JSONArray dataList = resultDic.getJSONArray("list");
			    if(dataList!=null&&dataList.size()>0){
			        for(int i=0;i<dataList.size();i++){
			            JSONObject oneRecordDic = dataList.getJSONObject(i);//数值从oneRecordDic得到
			            View view_one_record = mInflater.inflate(R.layout.match_list_personal_item,null);
			            ImageView userAvatar = (ImageView)view_one_record.findViewById(R.id.match_watch_head);
			            userAvatar.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.avatar_default, null));
			            String avatarUrl = oneRecordDic.getString("imgpath");
			            if(avatarUrl == null){
			                avatarUrl = "";
			            }else{
			                Bitmap image = CNAppDelegate.avatarDic.get(avatarUrl);
			                if(image != null){//缓存中有
			                	Log.v("zc","缓存中有");
			                    userAvatar.setImageBitmap(image);
			                }else{//下载
			                	RequestImageTask requestTask = new RequestImageTask();
			                	requestTask.index = i;
			                	requestTask.avatarUrl = avatarUrl;
			                	requestTask.execute("");
			                }
			            }
			            urlList.add(avatarUrl);
			            imageviewList.add(userAvatar);
			            
			            TextView label_name = (TextView)view_one_record.findViewById(R.id.username);
			            label_name.setText(oneRecordDic.getString("nickname"));
			            
//			            double distance = [[oneRecordDic objectForKey:@"km"]doubleValue];
//			            CNDistanceImageView* div = [[CNDistanceImageView alloc]initWithFrame:CGRectMake(160, 14, 130, 32)];
//			            div.distance = (distance+5)/1000.0;
//			            div.color = @"red";
//			            [div fitToSize];
//			            UIImageView* image_km_one = [[UIImageView alloc]initWithFrame:CGRectMake(div.frame.origin.x+div.frame.size.width, 14,26, 32)];
//			            image_km_one.image = [UIImage imageNamed:@"redkm.png"];
//			            [view_one_record addSubview:div];
//			            [view_one_record addSubview:image_km_one];needwy
			            
			            int height = (int) r.getDimension(R.dimen.sport_set_height);
						FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, height); 
						lp.setMargins(0,height*i,0,0);
						scrollview.addView(view_one_record, lp);
			        }
			    }
			}
		}
	}
	private class RequestKM extends AsyncTask<String, Void, Boolean> {
		private String responseJson;

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
		    String request_params = String.format("uid=%s&mid=%s&gid=%s",CNAppDelegate.uid,CNAppDelegate.mid,CNAppDelegate.gid);
		    Log.v("zc","按照公里查询成绩 is "+request_params);
		    responseJson = NetworkHandler.httpPost(Constants.endpoints	+ Constants.listKM, request_params);
		    Log.v("zc","按照公里查询返回 is "+responseJson);
			if (responseJson != null && !"".equals(responseJson)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			hideLoading();
			if (result) {
				CNAppDelegate.matchRequestResponseFilter(responseJson,Constants.matchReport,MatchGroupListActivity.this);
				JSONObject resultDic = JSON.parseObject(responseJson);
				clearScrollview();
				double distance = (resultDic.getDoubleValue("distancegr")+5)/1000.0;
//			    self.big_div.distance = distance;
//			    self.big_div.color = @"red";
//			    [self.big_div fitToSize];
//			    self.image_km.frame = CGRectMake(self.big_div.frame.origin.x+self.big_div.frame.size.width, 60+IOS7OFFSIZE,52, 64);needwy
				
				JSONArray dataList = resultDic.getJSONArray("list");
			    if(dataList!=null&&dataList.size()>0){
			    	for(int i=0;i<dataList.size();i++){
			    		JSONObject oneRecordDic = dataList.getJSONObject(i);//数值从oneRecordDic得到
			            int kmIndex = oneRecordDic.getIntValue("km");
			            int usetime = oneRecordDic.getIntValue("usetime");
			            View view_one_record = mInflater.inflate(R.layout.match_list_km_item,null);
			            TextView label_km = (TextView)view_one_record.findViewById(R.id.match_list_km);
			            label_km.setText("第"+kmIndex+"公里");
			            
//			            CNSpeedImageView* siv = [[CNSpeedImageView alloc]initWithFrame:CGRectMake(220, 14, 100, 32)];
//			            siv.time = usetime;
//			            siv.color = @"red";
//			            [siv fitToSize];
//			            [view_one_record addSubview:siv];needwy
			            
			            JSONArray array = oneRecordDic.getJSONArray("datas");
			            if(array.size()>0){
			            	JSONObject dic = array.getJSONObject(0);
			                ImageView userAvatar = (ImageView)view_one_record.findViewById(R.id.match_list_head1);
			                userAvatar.setVisibility(View.VISIBLE);
			                userAvatar.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.avatar_default, null));
			                String avatarUrl = dic.getString("imgpath");
			                if(avatarUrl == null){
			                    avatarUrl = "";
			                }else{
			                    Bitmap image = CNAppDelegate.avatarDic.get(avatarUrl);
			                    if(image != null){//缓存中有
			                    	Log.v("zc","缓存中有");
			                        userAvatar.setImageBitmap(image);
			                    }else{//下载
			                        RequestImageTask requestTask = new RequestImageTask();
				                	requestTask.index = imageviewList.size();
				                	requestTask.avatarUrl = avatarUrl;
				                	requestTask.execute("");
			                    }
			                }
			                urlList.add(avatarUrl);
			                imageviewList.add(userAvatar);
			            }
			            if(array.size()>1){
			            	JSONObject dic = array.getJSONObject(1);
			                ImageView userAvatar = (ImageView)view_one_record.findViewById(R.id.match_list_head2);
			                userAvatar.setVisibility(View.VISIBLE);
			                userAvatar.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.avatar_default, null));
			                String avatarUrl = dic.getString("imgpath");
			                if(avatarUrl == null){
			                    avatarUrl = "";
			                }else{
			                    Bitmap image = CNAppDelegate.avatarDic.get(avatarUrl);
			                    if(image != null){//
			                    	Log.v("zc","缓存中有");
			                        userAvatar.setImageBitmap(image);
			                    }else{//下载
			                        RequestImageTask requestTask = new RequestImageTask();
				                	requestTask.index = imageviewList.size();
				                	requestTask.avatarUrl = avatarUrl;
				                	requestTask.execute("");
			                    }
			                }
			                urlList.add(avatarUrl);
			                imageviewList.add(userAvatar);
			            }
			            int height = (int) r.getDimension(R.dimen.sport_set_height);
						FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, height); 
						lp.setMargins(0,height*i,0,0);
						scrollview.addView(view_one_record, lp);
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
	void displayLoading(){
	    disableAllButton();
	}
	void hideLoading(){
	    enableAllButton();
	}
	void disableAllButton(){
	}
	void enableAllButton(){
	}
	
}
