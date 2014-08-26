package net.yaopao.activity;

import net.yaopao.widget.NumericWheelAdapter;
import net.yaopao.widget.OnWheelChangedListener;
import net.yaopao.widget.WheelView;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class SelectTime extends PopupWindow implements OnClickListener {

	private Activity mContext;
	private View mMenuView;
	private ViewFlipper viewfipper;
	private Button btn_submit, btn_cancel;
	private String time;
	private int curTime;
	private TimeNumericAdapter timeAdapter;
	private WheelView timeV;
	private TextView timeTextV;

	public SelectTime(Activity context, final Handler handler) {
		super(context);
		mContext = context;
		this.time = SportTargetActivity.time;
		Log.v("wydb", "time1 ="+time);
		timeTextV = (TextView) mContext.findViewById(R.id.target_time_select);
		LayoutInflater inflater = LayoutInflater.from(context);
		mMenuView = inflater.inflate(R.layout.pop_time, null, true);
		viewfipper = new ViewFlipper(context);
		viewfipper.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		timeV = (WheelView) mMenuView.findViewById(R.id.user_time);
		btn_submit = (Button) mMenuView.findViewById(R.id.user_time_submit);
		btn_cancel = (Button) mMenuView.findViewById(R.id.user_time_cancel);
		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Message msg = new Message();
				Bundle b = new Bundle();
				b.putString("time", time);
				msg.setData(b);
				handler.sendMessage(msg);
				SelectTime.this.dismiss();
			}
		});
		btn_cancel.setOnClickListener(this);
		OnWheelChangedListener listener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				updateDistance(timeV);

			}
		};
		if (time != null) {
			String []times = time.split(" ");
			curTime = Integer.parseInt(times[0]);
		}
		timeAdapter = new TimeNumericAdapter(context, 5, 180, curTime);

		timeV.setViewAdapter(timeAdapter);
		timeV.setCurrentItem(curTime-5);
		timeV.addChangingListener(listener);
		updateDistance(timeV);

		viewfipper.addView(mMenuView);
		viewfipper.setFlipInterval(6000000);
		this.setContentView(viewfipper);
		this.setWidth(LayoutParams.FILL_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.PopupAnimation);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		this.setBackgroundDrawable(dw);
		this.update();

	}

	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		super.showAtLocation(parent, gravity, x, y);
		viewfipper.startFlipping();
	}

	private void updateDistance(WheelView view) {
		TimeNumericAdapter timeAd = new TimeNumericAdapter(mContext, 5, 180,curTime);
		view.setViewAdapter(timeAd);
		String curT = view.getCurrentItem()+5+"";
		view.setCurrentItem(view.getCurrentItem(), true);
		time = curT;
		Log.v("wydb", "time4 ="+SportTargetActivity.time);
		timeTextV.setText(curT + " 分钟");
	}

	/**
	 * Adapter for numeric wheels. Highlights the current value.
	 */
	private class TimeNumericAdapter extends NumericWheelAdapter {
		// Index of current item
		int currentItem;
		// Index of item to be highlighted
		int currentValue;

		/**
		 * Constructor
		 */
		public TimeNumericAdapter(Context context, int minValue, int maxValue,
				int current) {
			super(context, minValue, maxValue);
			this.currentValue = current;
			setTextSize(30);
		}

		protected void configureTextView(TextView view) {
			super.configureTextView(view);
			view.setTypeface(Typeface.SANS_SERIF);
		}

		public CharSequence getItemText(int index) {
			currentItem = index;
			return super.getItemText(index);
		}

	}

	public void onClick(View v) {
		this.dismiss();
	}

}
