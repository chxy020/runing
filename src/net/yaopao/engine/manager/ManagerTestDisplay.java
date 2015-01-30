/*package net.yaopao.engine.manager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import zc.manager.binaryIO.BinaryIOManager;

import net.yaopao.activity.R;
import net.yaopao.activity.YaoPao01App;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ManagerTestDisplay extends Activity{
	TextView tv_base_dis;
	TextView tv_base_pace_km;
	TextView tv_base_time;
	TextView tv_base_pace_mile;
	TextView tv_base_score;
	TextView tv_base_percent;
	TextView tv_base_status;
	TextView tv_base_alt_add;
	TextView tv_base_alt_reduce;

	TextView tv_gpslist_count;
	TextView tv_gpslist_lon;
	TextView tv_gpslist_time;
	TextView tv_gpslist_lat;
	TextView tv_gpslist_dir;
	TextView tv_gpslist_alt;
	TextView tv_gpslist_speed;
	
	TextView tv_kmlist_count;
	TextView tv_kmlist_lon;
	TextView tv_kmlist_dis;
	TextView tv_kmlist_lat;
	TextView tv_kmlist_totalTime;
	TextView tv_kmlist_alt_add;
	TextView tv_kmlist_alt_reduce;
	
	TextView tv_milelist_count;
	TextView tv_milelist_lon;
	TextView tv_milelist_dis;
	TextView tv_milelist_lat;
	TextView tv_milelist_totalTime;
	TextView tv_milelist_alt_add;
	TextView tv_milelist_alt_reduce;
	
	TextView tv_minutelist_count;
	TextView tv_minutelist_lon;
	TextView tv_minutelist_time;
	TextView tv_minutelist_lat;
	TextView tv_minutelist_dis;
	TextView tv_minutelist_alt_add;
	TextView tv_minutelist_alt_reduce;
	
	
	Button button1;
	Button button2;
	
	int status = 1;
	
	private Timer timer = null;
	private TimerTask task = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manager_test2);
		tv_base_dis = (TextView)findViewById(R.id.base_dis);
		tv_base_pace_km = (TextView)findViewById(R.id.base_pace_km);
		tv_base_time = (TextView)findViewById(R.id.base_time);
		tv_base_pace_mile = (TextView)findViewById(R.id.base_pace_mile);
		tv_base_score = (TextView)findViewById(R.id.base_score);
		tv_base_percent = (TextView)findViewById(R.id.base_percent);
		tv_base_status = (TextView)findViewById(R.id.base_status);
		tv_base_alt_add = (TextView)findViewById(R.id.base_alt_add);
		tv_base_alt_reduce = (TextView)findViewById(R.id.base_alt_reduce);
		
		tv_gpslist_count = (TextView)findViewById(R.id.gpslist_count);
		tv_gpslist_lon = (TextView)findViewById(R.id.gpslist_lon);
		tv_gpslist_time = (TextView)findViewById(R.id.gpslist_time);
		tv_gpslist_lat = (TextView)findViewById(R.id.gpslist_lat);
		tv_gpslist_dir = (TextView)findViewById(R.id.gpslist_dir);
		tv_gpslist_alt = (TextView)findViewById(R.id.gpslist_alt);
		tv_gpslist_speed = (TextView)findViewById(R.id.gpslist_speed);
		
		tv_kmlist_count = (TextView)findViewById(R.id.kmlist_count);
		tv_kmlist_lon = (TextView)findViewById(R.id.kmlist_lon);
		tv_kmlist_dis = (TextView)findViewById(R.id.kmlist_dis);
		tv_kmlist_lat = (TextView)findViewById(R.id.kmlist_lat);
		tv_kmlist_totalTime = (TextView)findViewById(R.id.kmlist_totalTime);
		tv_kmlist_alt_add= (TextView)findViewById(R.id.kmlist_alt_add);
		tv_kmlist_alt_reduce= (TextView)findViewById(R.id.kmlist_alt_reduce);
		
		
		tv_milelist_count = (TextView)findViewById(R.id.milelist_count);
		tv_milelist_lon = (TextView)findViewById(R.id.milelist_lon);
		tv_milelist_dis = (TextView)findViewById(R.id.milelist_dis);
		tv_milelist_lat = (TextView)findViewById(R.id.milelist_lat);
		tv_milelist_totalTime = (TextView)findViewById(R.id.milelist_totalTime);
		tv_milelist_alt_add= (TextView)findViewById(R.id.milelist_alt_add);
		tv_milelist_alt_reduce= (TextView)findViewById(R.id.milelist_alt_reduce);
		
		tv_minutelist_count = (TextView)findViewById(R.id.minutelist_count);
		tv_minutelist_lon = (TextView)findViewById(R.id.minutelist_lon);
		tv_minutelist_time = (TextView)findViewById(R.id.minutelist_time);
		tv_minutelist_lat = (TextView)findViewById(R.id.minutelist_lat);
		tv_minutelist_dis = (TextView)findViewById(R.id.minutelist_dis);
		tv_minutelist_alt_add= (TextView)findViewById(R.id.minutelist_alt_add);
		tv_minutelist_alt_reduce= (TextView)findViewById(R.id.minutelist_alt_reduce);
		
		button1 = (Button)findViewById(R.id.button_status);
		button2 = (Button)findViewById(R.id.button_finish);
		
		button1.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(status == 1){
					status = 2;
					YaoPao01App.runManager.changeRunStatus(status);
					button1.setText("继续运动");
					stopTimer();
				}else{
					status = 1;
					YaoPao01App.runManager.changeRunStatus(status);
					button1.setText("点击暂停");
					startTimer();
				}
			}
		});
		button2.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				stopTimer();
				String filename = new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis()));
				BinaryIOManager.writeBinary(filename);
				BinaryIOManager.readBinary(filename);
				Log.v("zc","filename is "+filename);
				finish();
				YaoPao01App.runManager = null;
			}
		});
		startTimer();
		
	}
	class TimerTaskUpdate extends TimerTask{
		@Override
		public void run() {
			runOnUiThread(new Runnable() { // UI thread
				@Override
				public void run() {
					tv_base_dis.setText(String.format("%.2f", YaoPao01App.runManager.distance/1000.0));
					tv_base_pace_km.setText(BinaryIOManager.getTimeString(YaoPao01App.runManager.paceKm));
					tv_base_time.setText(BinaryIOManager.getTimeString(YaoPao01App.runManager.during()/1000));
					tv_base_pace_mile.setText(BinaryIOManager.getTimeString((int)(YaoPao01App.runManager.paceKm*1.6)));
					tv_base_score.setText(""+YaoPao01App.runManager.score);
					tv_base_percent.setText(String.format("%d", (int)(YaoPao01App.runManager.completePercent*100))+"%");
					tv_base_alt_add.setText(String.format("%.1f", YaoPao01App.runManager.altitudeAdd));
					tv_base_alt_reduce.setText(String.format("%.1f", YaoPao01App.runManager.altitudeReduce));
					
					int gpscount = YaoPao01App.runManager.GPSList.size();
					if(gpscount>0){
						tv_gpslist_count.setText(""+gpscount);
						GpsPoint point = YaoPao01App.runManager.GPSList.get(gpscount-1);
						tv_gpslist_lon.setText(String.format("%.6f", point.getLon()));
						tv_gpslist_lat.setText(String.format("%.6f", point.getLat()));
						tv_gpslist_time.setText(new SimpleDateFormat("HH:mm:ss").format(new Date(point.getTime())));
						tv_gpslist_dir.setText(""+point.getCourse());
						tv_gpslist_alt.setText(""+point.getAltitude());
						tv_gpslist_speed.setText(""+point.getSpeed());
					}
					
					int kmcount = YaoPao01App.runManager.dataKm.size();
					if(kmcount>0){
						tv_kmlist_count.setText(""+kmcount);
						OneKMInfo oneKm = YaoPao01App.runManager.dataKm.get(kmcount-1);
						tv_kmlist_lon.setText(String.format("%.6f", oneKm.getLon()));
						tv_kmlist_lat.setText(String.format("%.6f", oneKm.getLat()));
						tv_kmlist_dis.setText(""+oneKm.getDistance());
						tv_kmlist_totalTime.setText(BinaryIOManager.getTimeString(oneKm.getDuring()/1000));
						tv_kmlist_alt_add.setText(String.format("%.1f", oneKm.getAltitudeAdd()));
						tv_kmlist_alt_reduce.setText(String.format("%.1f", oneKm.getAltitudeReduce()));
					}
					
					int milecount = YaoPao01App.runManager.dataMile.size();
					if(milecount>0){
						tv_milelist_count.setText(""+milecount);
						OneMileInfo oneMile = YaoPao01App.runManager.dataMile.get(milecount-1);
						tv_milelist_lon.setText(String.format("%.6f", oneMile.getLon()));
						tv_milelist_lat.setText(String.format("%.6f", oneMile.getLat()));
						tv_milelist_dis.setText(""+oneMile.getDistance());
						tv_milelist_totalTime.setText(BinaryIOManager.getTimeString(oneMile.getDuring()/1000));
						tv_milelist_alt_add.setText(String.format("%.1f", oneMile.getAltitudeAdd()));
						tv_milelist_alt_reduce.setText(String.format("%.1f", oneMile.getAltitudeReduce()));
					}
					
					int minutecount = YaoPao01App.runManager.dataMin.size();
					if(minutecount>0){
						tv_minutelist_count.setText(""+minutecount);
						OneMinuteInfo fiveMinute = YaoPao01App.runManager.dataMin.get(minutecount-1);
						tv_minutelist_lon.setText(String.format("%.6f", fiveMinute.getLon()));
						tv_minutelist_lat.setText(String.format("%.6f", fiveMinute.getLat()));
						tv_minutelist_time.setText(BinaryIOManager.getTimeString(fiveMinute.getDuring()/1000));
						tv_minutelist_dis.setText(""+fiveMinute.getDistance());
						tv_minutelist_alt_add.setText(String.format("%.1f", fiveMinute.getAltitudeAdd()));
						tv_minutelist_alt_reduce.setText(String.format("%.1f", fiveMinute.getAltitudeReduce()));
					}
				}
			});
		}
	}
	private void startTimer(){
		task = new TimerTaskUpdate();
		timer = new Timer();
		timer.schedule(task, 0, 1000);
	}
	private void stopTimer(){
		if(timer!=null){
			timer.cancel();
			timer = null;
			if(task!=null){
				task.cancel();
				task = null;
			}
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
		    AlertDialog.Builder builder = new AlertDialog.Builder(this);  
		    builder.setMessage("确认退出")  
		           .setCancelable(false)  
		           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {  
		               public void onClick(DialogInterface dialog, int id) {  
		                    System.exit(0);
		               }  
		           })  
		           .setNegativeButton("No", new DialogInterface.OnClickListener() {  
		               public void onClick(DialogInterface dialog, int id) {  
		                    dialog.cancel();  
		               }  
		           });  
		    AlertDialog alert = builder.create();  
		    alert.show();
		}
		return false;
	}
}
*/