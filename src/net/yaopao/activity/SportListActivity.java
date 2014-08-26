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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
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
	}

	private void initLayout() {
		sdf1 = new SimpleDateFormat("MM");
		sdf2 = new SimpleDateFormat("dd");
		sdf3 = new SimpleDateFormat("HH:mm");
	    df=(DecimalFormat)NumberFormat.getInstance(); 
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
		
			map.put("dis", df.format((sport.getDistance()/1000)) + "km");
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
			map.put("speed",  s1+""+s2+"'"+s3+""+s4+"\""+ "/公里");
			data.add(map);
			Log.v("wy", "l dis1 ="+sport.getDistance());
			Log.v("wy", "l dis2 ="+df.format((sport.getDistance()/1000)) + "km");
			Log.v("wy", "l speed1 ="+sport.getPspeed());
			Log.v("wy", "l speed2 ="+speed);
			Log.v("wy", "l speed3 ="+s1+""+s2+"'"+s3+""+s4+"\""+ "/公里");
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
}
