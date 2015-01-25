package net.yaopao.activity;

import net.yaopao.assist.Variables;
import net.yaopao.widget.ArrayWheelAdapter;
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

public class SelectDistance extends PopupWindow implements OnClickListener {

	private Activity mContext;
	private View mMenuView;
	private ViewFlipper viewfipper;
	private Button btn_submit, btn_cancel;
	private String distance;
	private WheelView distanceV1;
	private TextView distanceV;
	private int distanceKm = 0;
	private Integer[] distanceArray = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,25,30,35,40,42,45,50,55,60,65,70,75,80,85,90,95,100};
	private String[] distanceTxtArray = {"1km","2km","3km","4km","5km","6km","7km","8km","9km","10km","11km","12km","13km","14km","15km","16km","17km","18km","19km","20km","21km","25km","30km","35km","40km","42km","45km","50km","55km","60km","65km","70km","75km","80km","85km","90km","95km","100km"};
	public SelectDistance(Activity context, final Handler handler,int distanceData) {
		super(context);
		mContext = context;
//		this.distance = Variables.runtarDis + "";
		this.distance = Variables.runTargetDis/1000 + "";
		
		this.distanceKm = distanceData;
		
		distanceV = (TextView) mContext
				.findViewById(R.id.target_distance_select);
		LayoutInflater inflater = LayoutInflater.from(context);
		mMenuView = inflater.inflate(R.layout.pop_distance, null, true);
		viewfipper = new ViewFlipper(context);
		viewfipper.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		distanceV1 = (WheelView) mMenuView.findViewById(R.id.user_distance1);
		btn_submit = (Button) mMenuView.findViewById(R.id.user_distance_submit);
		btn_cancel = (Button) mMenuView.findViewById(R.id.user_distance_cancel);
		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Message msg = new Message();
				Bundle b = new Bundle();
				b.putString("distance", distance);
				msg.setData(b);
				handler.sendMessage(msg);
				SelectDistance.this.dismiss();
			}
		});
		btn_cancel.setOnClickListener(this);
		OnWheelChangedListener listener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				updateDistance(distanceV1,false);

			}
		};
		
		updateDistance(distanceV1,true);
		distanceV1.addChangingListener(listener);
		
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

	private void updateDistance(WheelView view1, boolean setDefault) {
		
		view1.setViewAdapter(new ArrayWheelAdapter<String>(mContext, distanceTxtArray));
		if(setDefault){
			int sub = countItemSub(this.distanceKm);
			view1.setCurrentItem(sub);
		}
		
		int dis = view1.getCurrentItem();
		String km = this.distanceTxtArray[dis];
		int disId = this.distanceArray[dis];
		distance = disId + "";
		distanceV.setText(km);
	}
	
	/**
	 * 计算当前数据在数组中的下标
	 * @param dis
	 * @return
	 * @author cxy
	 * @date 2014-9-6
	 */
	private int countItemSub(int dis){
		int sub = 0;
		int len = this.distanceArray.length;
		for(int i = 0; i < len; i++){
			int d = this.distanceArray[i];
			if(dis == d){
				sub = i;
				break;
			}
		}
		return sub;
	}
	
	/**
	 * Adapter for numeric wheels. Highlights the current value.
	 */
	private class DistanceNumericAdapter1 extends NumericWheelAdapter {
		// Index of current item
		int currentItem;
		// Index of item to be highlighted
		int currentValue;

		/**
		 * Constructor
		 */
		public DistanceNumericAdapter1(Context context, int minValue,
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

	private class DistanceNumericAdapter2 extends NumericWheelAdapter {
		// Index of current item
		int currentItem;
		// Index of item to be highlighted
		int currentValue;

		/**
		 * Constructor
		 */
		public DistanceNumericAdapter2(Context context, int minValue,
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