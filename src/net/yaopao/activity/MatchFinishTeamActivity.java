package net.yaopao.activity;

import net.yaopao.bean.DataBean;
import android.app.Activity;
import android.os.Bundle;
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
	
	
	private TextView button_back,label_tname,button_personal,button_km;
	private ScrollView scrollview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_score_list);
		initinitSymbol();
		init();
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
				break;
			case MotionEvent.ACTION_UP:
				finish();
				break;
			}
			break;
		case R.id.match_score_list_mileage:
			//点击里程
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				finish();
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
	
}
