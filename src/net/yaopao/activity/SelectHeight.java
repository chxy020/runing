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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class SelectHeight extends PopupWindow implements OnClickListener {

	private Activity mContext;
	private View mMenuView;
	private ViewFlipper viewfipper;
	private Button btn_submit, btn_cancel;
	private String height;
	private int curH = 10;
	private HeightNumericAdapter heightAdapter;
	private WheelView heightV;

	public SelectHeight(Activity context, final Handler handler,String heightData) {
		super(context);
		mContext = context;
		//this.height = "185";
		if (heightData!=null&&!"".equals(heightData)) {
			this.curH = Integer.parseInt(heightData.substring(0,heightData.length() - 2)) - 100;
		}
		
		
		LayoutInflater inflater = LayoutInflater.from(context);

		mMenuView = inflater.inflate(R.layout.pop_height, null, true);
		viewfipper = new ViewFlipper(context);
		viewfipper.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		heightV = (WheelView) mMenuView.findViewById(R.id.user_height);
		btn_submit = (Button) mMenuView.findViewById(R.id.user_height_submit);
		btn_cancel = (Button) mMenuView.findViewById(R.id.user_height_cancel);
		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Message msg = new Message();
				Bundle b = new Bundle();// ������
				b.putString("height", height);
				msg.setData(b);
				handler.sendMessage(msg);
				SelectHeight.this.dismiss();
			}
		});
		btn_cancel.setOnClickListener(this);
		Calendar calendar = Calendar.getInstance();
		OnWheelChangedListener listener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				updateheight(heightV,false);

			}
		};
		
		updateheight(heightV,true);
		
		
		heightV.addChangingListener(listener);

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

	private void updateheight(WheelView view,boolean setDefault) {

		heightAdapter = new HeightNumericAdapter(mContext, 100, 240,this.curH);
		heightAdapter.setTextType("cm");
		view.setViewAdapter(heightAdapter);
		
		if(setDefault){
			view.setCurrentItem(this.curH);
		}
		
		int curheight = view.getCurrentItem() + 100;
		height = curheight + "cm";
	}

	/**
	 * Adapter for numeric wheels. Highlights the current value.
	 */
	private class HeightNumericAdapter extends NumericWheelAdapter {
		// Index of current item
		int currentItem;
		// Index of item to be highlighted
		int currentValue;

		/**
		 * Constructor
		 */
		public HeightNumericAdapter(Context context, int minValue,
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