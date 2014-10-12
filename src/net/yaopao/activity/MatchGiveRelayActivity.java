package net.yaopao.activity;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

/**
 */
public class MatchGiveRelayActivity extends BaseActivity implements
		OnTouchListener {
	private TextView label_back, label_finish, confirm, cancel, label_user1,
			label_user2, label_user3, label_user4, label_myname, tip;
	private ImageView view_user1, view_user2, view_user3, view_user4, image_me,
			image_gps;

	private RelativeLayout relay_wait_header_layout1,
			relay_wait_header_layout2,text_button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_give_relay);
		init();
	}

	private void init() {
		label_back = (TextView) findViewById(R.id.relay_continue);
		label_back.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		label_finish = (TextView) findViewById(R.id.relay_end);
		label_finish.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		confirm = (TextView) findViewById(R.id.relay_chosen_confirm);
		cancel = (TextView) findViewById(R.id.relay_chosen_cancel);
		tip = (TextView) findViewById(R.id.out_delay_tip1);

		label_user1 = (TextView) findViewById(R.id.relay_wait_nickname1);
		label_user2 = (TextView) findViewById(R.id.relay_wait_nickname2);
		label_user3 = (TextView) findViewById(R.id.relay_wait_nickname3);
		label_user4 = (TextView) findViewById(R.id.relay_chosen_nickname);
		label_myname = (TextView) findViewById(R.id.relay_nickname);

		view_user1 = (ImageView) findViewById(R.id.relay_wait_head1);
		view_user2 = (ImageView) findViewById(R.id.relay_wait_head2);
		view_user3 = (ImageView) findViewById(R.id.relay_wait_head3);
		view_user4 = (ImageView) findViewById(R.id.relay_chosen_head);
		image_me = (ImageView) findViewById(R.id.relay_head);

		relay_wait_header_layout1 = (RelativeLayout) findViewById(R.id.relay_wait_head_layout);
		relay_wait_header_layout2 = (RelativeLayout) findViewById(R.id.relay_wait_header_layout2);
		text_button = (RelativeLayout) findViewById(R.id.text_button);

		label_back.setOnTouchListener(this);
		view_user1.setOnTouchListener(this);
		view_user2.setOnTouchListener(this);
		view_user3.setOnTouchListener(this);

		label_finish.setOnTouchListener(this);
		label_back.setOnTouchListener(this);
		confirm.setOnTouchListener(this);
		cancel.setOnTouchListener(this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
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
		// 点击不交棒
		case R.id.relay_continue:

			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				break;
			}
			break;
		// 点击中间的头像
		case R.id.relay_wait_head1:

			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				headerLayout1Dismiss(null,"");
				break;
			}
			break;
		// 点击左侧的头像
		case R.id.relay_wait_head2:

			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				headerLayout1Dismiss(null,"");
				break;
			}
			break;
		// 点击右侧头像
		case R.id.relay_wait_head3:

			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				headerLayout1Dismiss(null,"");
				break;
			}
			break;

		// 点击确认交棒
		case R.id.relay_chosen_confirm:

			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:

				break;
			}
			break;
		// 点击取消交棒
		case R.id.relay_chosen_cancel:

			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:

				headerLayout2Dismiss();
				break;
			}
			break;
		}
		return true;
	}

	private void headerLayout1Dismiss(Bitmap avatar,String name) {
		relay_wait_header_layout1.setVisibility(View.GONE);
		relay_wait_header_layout2.setVisibility(View.VISIBLE);
		text_button.setVisibility(View.GONE);
		if (avatar!=null) {
			view_user4.setImageBitmap(avatar);
		}
		label_user4.setText(name);
		
	}

	private void headerLayout2Dismiss() {
		relay_wait_header_layout1.setVisibility(View.VISIBLE);
		tip.setVisibility(View.VISIBLE);
		relay_wait_header_layout2.setVisibility(View.GONE);
	}

	// 搜索到队员后隐藏正在搜索提示
	private void tipDismiss() {
		tip.setVisibility(View.GONE);
		relay_wait_header_layout1.setVisibility(View.VISIBLE);
		//设置接棒队员的头像昵称
		
	}
}
