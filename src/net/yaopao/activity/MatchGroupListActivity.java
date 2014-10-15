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
import net.yaopao.assist.LoadingDialog;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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
	
	private ImageView totalDis1,totalDis2,totalDis3,totalDis4,totalDis5,totalDis6,totalDisDot,totalDisKm,pd1V,pd2V,pd3V,pd4V,pd5V,pd6V,pkm,pdot;
	
	private ImageView s1V;
	private ImageView s2V;
	private ImageView s3V;
	private ImageView s4V;
	private ImageView minV;
	private ImageView secV;
	
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
	private LoadingDialog loadingDialog;
	class TimerTask_request_personal extends TimerTask{
		@Override
		public void run() {
			
			runOnUiThread(new Runnable() { // UI thread
				@Override
				public void run() {
					requestPersonal();
					
				}
			});
		}
	}
	class TimerTask_request_km extends TimerTask{
		@Override
		public void run() {
			runOnUiThread(new Runnable() { // UI thread
				@Override
				public void run() {
					requestKm();
					
				}
			});
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
		initTotalMileage(0);
	}
	void requestPersonal(){
		displayLoading();
		new RequestPersonal().execute("");
	}
	void requestKm(){
		displayLoading();
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
		
		totalDis1= (ImageView) findViewById(R.id.match_milage_num1);
		totalDis2= (ImageView) findViewById(R.id.match_milage_num2);
		totalDis3= (ImageView) findViewById(R.id.match_milage_num3);
		totalDis4= (ImageView) findViewById(R.id.match_milage_num4);
		totalDis5= (ImageView) findViewById(R.id.match_milage_dec1);
		totalDis6= (ImageView) findViewById(R.id.match_milage_dec2);
		totalDisDot= (ImageView) findViewById(R.id.match_milage_dot);
		totalDisKm= (ImageView) findViewById(R.id.match_milage_km);
		totalDisDot.setImageBitmap(YaoPao01App.graphicTool.numBitmap.get(R.drawable.r_dot));
		totalDisKm.setImageBitmap(YaoPao01App.graphicTool.numBitmap.get(R.drawable.r_km));
		
		scrollview = (FrameLayout) findViewById(R.id.scrollview_List);
		button_back.setOnTouchListener(this);
		button_personal.setOnTouchListener(this);
		button_km.setOnTouchListener(this);
		
		loadingDialog= new LoadingDialog(this);
		loadingDialog.setCancelable(false);
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
				button_back.setBackgroundResource(R.color.red_h);
				break;
			case MotionEvent.ACTION_UP:
				button_back.setBackgroundResource(R.color.red);
				finish();
				break;
			}
			break;
		case R.id.match_score_list_personal:
			//点击队员
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				button_km.setBackgroundResource(R.color.gray_dark);
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
				button_personal.setBackgroundResource(R.color.gray_dark);
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
				CNAppDelegate.matchRequestResponseFilter(responseJson,Constants.listPersonal,MatchGroupListActivity.this);
				JSONObject resultDic = JSON.parseObject(responseJson);
				clearScrollview();
			    double distance = (resultDic.getDoubleValue("distancegr")+5)/1000.0;
			    initTotalMileage(resultDic.getDoubleValue("distancegr")+5);
			    
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
			            userAvatar.setImageBitmap(Variables.avatar_default);
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
			            
			            initPersonalMileage(oneRecordDic.getDoubleValue("km")+5);
			            
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
				CNAppDelegate.matchRequestResponseFilter(responseJson,Constants.listKM,MatchGroupListActivity.this);
				JSONObject resultDic = JSON.parseObject(responseJson);
				clearScrollview();
				double distance = (resultDic.getDoubleValue("distancegr")+5)/1000.0;
				initTotalMileage(resultDic.getDoubleValue("distancegr")+5);
				JSONArray dataList = resultDic.getJSONArray("list");
			    if(dataList!=null&&dataList.size()>0){
			    	for(int i=0;i<dataList.size();i++){
			    		JSONObject oneRecordDic = dataList.getJSONObject(i);//数值从oneRecordDic得到
			            int kmIndex = oneRecordDic.getIntValue("km");
			            int usetime = oneRecordDic.getIntValue("usetime");
			            View view_one_record = mInflater.inflate(R.layout.match_list_km_item,null);
			            TextView label_km = (TextView)view_one_record.findViewById(R.id.match_list_km);
			            label_km.setText("第"+kmIndex+"公里");
			            
			            s1V = (ImageView) view_one_record.findViewById(R.id.list_sport_num1);
			    		s2V = (ImageView) view_one_record.findViewById(R.id.list_sport_num2);
			    		s3V = (ImageView) view_one_record.findViewById(R.id.list_sport_num3);
			    		s4V = (ImageView) view_one_record.findViewById(R.id.list_sport_num4);
			    		minV = (ImageView) view_one_record.findViewById(R.id.list_sport_min);
			    		secV = (ImageView) view_one_record.findViewById(R.id.list_sport_sec);
			    		minV.setImageBitmap(YaoPao01App.graphicTool.numBitmap
			    				.get(R.drawable.r_min));
			    		secV.setImageBitmap(YaoPao01App.graphicTool.numBitmap
			    				.get(R.drawable.r_sec));
			    		initPspeed(usetime);
			            JSONArray array = oneRecordDic.getJSONArray("datas");
			            if(array.size()>0){
			            	JSONObject dic = array.getJSONObject(0);
			                ImageView userAvatar = (ImageView)view_one_record.findViewById(R.id.match_list_head1);
			                userAvatar.setVisibility(View.VISIBLE);
			                userAvatar.setImageBitmap(Variables.avatar_default);
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
				                	displayLoading();
			                    }
			                }
			                urlList.add(avatarUrl);
			                imageviewList.add(userAvatar);
			            }
			            if(array.size()>1){
			            	JSONObject dic = array.getJSONObject(1);
			                ImageView userAvatar = (ImageView)view_one_record.findViewById(R.id.match_list_head2);
			                userAvatar.setVisibility(View.VISIBLE);
			                userAvatar.setImageBitmap(Variables.avatar_default);
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
				                	displayLoading();
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
//	void displayLoading(){
//	    disableAllButton();
//	}
//	void hideLoading(){
//	    enableAllButton();
//	}
//	void disableAllButton(){
//	}
//	void enableAllButton(){
//	}
	void displayLoading(){
		loadingDialog.show();
	}
	void hideLoading(){
		loadingDialog.dismiss();
	}
	private void initTotalMileage(double distance) {
		totalDis1.setVisibility(View.GONE);
		totalDis2.setVisibility(View.GONE);
		totalDis3.setVisibility(View.GONE);
		int d1 = (int) distance / 1000000;
		int d2 = (int) (distance % 1000000) / 100000;
		int d3 = (int) (distance % 100000) / 10000;
		int d4 = (int) (distance % 10000) / 1000;
		int d5 = (int) (distance % 1000) / 100;
		int d6 = (int) (distance % 100) / 10;
		if (d1 > 0) {
			totalDis1.setVisibility(View.VISIBLE);
		}
		if (d2 > 0) {
			totalDis2.setVisibility(View.VISIBLE);
		}
		if (d3 > 0) {
			totalDis3.setVisibility(View.VISIBLE);
		}
		YaoPao01App.graphicTool.updateRedNum(
				new int[] { d1, d2, d3, d4, d5, d6 }, new ImageView[] {totalDis1,totalDis2,totalDis3,totalDis4,totalDis5,totalDis6});
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
	// 初始化平均配速
		private void initPspeed(int pspeed) {

			int[] speed = YaoPao01App.cal(pspeed);

			int s1 = speed[1] / 10;
			int s2 = speed[1] % 10;
			int s3 = speed[2] / 10;
			int s4 = speed[2] % 10;

			YaoPao01App.graphicTool.updateRedNum(new int[] { s1, s2, s3, s4 },
					new ImageView[] { s1V, s2V, s3V, s4V });

		}
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_HOME) {
				// Toast.makeText(SportRecordActivity.this, "", duration)
			}
			return false;
		}
}
