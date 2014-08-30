package net.yaopao.activity;

import java.util.Calendar;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class SelectWeight extends PopupWindow implements OnClickListener {

	private Activity mContext;
	private View mMenuView;
	private ViewFlipper viewfipper;
	private Button btn_submit, btn_cancel;
	private String weight;
	private int curW = 30;
	private WeightNumericAdapter weightAdapter;
	private WheelView weightV;

	public SelectWeight(Activity context, final Handler handler) {
		super(context);
		mContext = context;
		this.weight = "50";
		LayoutInflater inflater = LayoutInflater.from(context);

		mMenuView = inflater.inflate(R.layout.pop_weight, null, true);
		viewfipper = new ViewFlipper(context);
		viewfipper.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		weightV = (WheelView) mMenuView.findViewById(R.id.user_weight);
		btn_submit = (Button) mMenuView.findViewById(R.id.user_weight_submit);
		btn_cancel = (Button) mMenuView.findViewById(R.id.user_weight_cancel);
		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Message msg = new Message();
				Bundle b = new Bundle();// ������
				b.putString("weight", weight);
				msg.setData(b);
				handler.sendMessage(msg);
				SelectWeight.this.dismiss();
			}
		});
		btn_cancel.setOnClickListener(this);
		Calendar calendar = Calendar.getInstance();
		OnWheelChangedListener listener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				updateWeight(weightV);

			}
		};

		weightV.setCurrentItem(curW);
		updateWeight(weightV);
		weightV.addChangingListener(listener);

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

	private void updateWeight(WheelView view) {

		weightAdapter = new WeightNumericAdapter(mContext, 20, 220, 40);
		weightAdapter.setTextType("kg");
		view.setViewAdapter(weightAdapter);
		// int curWeight = Math.min(220, view.getCurrentItem());
		int curWeight = view.getCurrentItem() + 20;

		view.setCurrentItem(view.getCurrentItem(), true);
		weight = curWeight + "kg";
	}

	/**
	 * Adapter for numeric wheels. Highlights the current value.
	 */
	private class WeightNumericAdapter extends NumericWheelAdapter {
		// Index of current item
		int currentItem;
		// Index of item to be highlighted
		int currentValue;

		/**
		 * Constructor
		 */
		public WeightNumericAdapter(Context context, int minValue,
				int maxValue, int current) {
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