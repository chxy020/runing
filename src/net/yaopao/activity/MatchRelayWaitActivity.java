//package net.yaopao.activity;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnTouchListener;
//import android.widget.ImageView;
//
//
//
///**
// */
//public class MatchRelayWaitActivity extends Activity implements OnTouchListener {
//	private ImageView backV;
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_match_score_list);
//		init();
//	}
//
//	private void init() {
//		backV = (ImageView) findViewById(R.id.match_score_list_back);
//		backV.setOnTouchListener(this);
//	}
//
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//	}
//
//	/**
//	 * 方法必须重写
//	 */
//	@Override
//	protected void onPause() {
//		super.onPause();
//	}
//
//
//	/**
//	 * 方法必须重写
//	 */
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//	}
//
//	@Override
//	public boolean onTouch(View view, MotionEvent event) {
//		int action = event.getAction();
//		switch (view.getId()) {
//		case R.id.match_score_list_back:
//			switch (action) {
//			case MotionEvent.ACTION_DOWN:
//				break;
//			case MotionEvent.ACTION_UP:
//				finish();
//				break;
//			}
//			break;
//		}
//		return true;
//	}
//
//
//}
