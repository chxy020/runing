package net.yaopao.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.yaopao.assist.CNAppDelegate;
import net.yaopao.assist.Constants;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.bean.DataBean;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;

/**
 */
public class MatchGroupListActivity extends BaseActivity implements OnTouchListener {
	
	
	private TextView button_back,label_tname,button_personal,button_km;
	private ScrollView scrollview;
	
	Timer timer_personal;
	Timer timer_km;
	int tabIndex = 0;
	List<ImageView> imageviewList = new ArrayList<ImageView>();
	List<String> urlList = new ArrayList<String>();
	TimerTask task_request_personal = new TimerTask() {
		@Override
		public void run() {
			requestPersonal();
		}
	};
	TimerTask task_request_km = new TimerTask() {
		@Override
		public void run() {
			requestKm();
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_score_list);
		initinitSymbol();
		init();
		label_tname.setText(CNAppDelegate.matchDic.getString("groupname"));
		timer_personal.schedule(task_request_personal, 0, CNAppDelegate.kMatchReportInterval);
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
		
	}
	void requestKm(){
		
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
		
		scrollview = (ScrollView) findViewById(R.id.match_score_list_data);
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
		timer_personal.cancel();
	    timer_km.cancel();
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
	            timer_km.cancel();
	            timer_personal.schedule(task_request_personal, 0, CNAppDelegate.kMatchReportInterval);
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
	            timer_personal.cancel();
	            timer_km.schedule(task_request_km, 0, CNAppDelegate.kMatchReportInterval);
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
			        int y_used = 0;
			        for(int i=0;i<dataList.size();i++){
			            JSONObject oneRecordDic = dataList.getJSONObject(i);//数值从oneRecordDic得到
			            
			            
			            y_used += 60;
			        }
			    }
			} else {
				
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
				
			} else {
				
			}
		}
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
