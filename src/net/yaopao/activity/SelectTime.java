package net.yaopao.activity;

import net.yaopao.widget.ArrayWheelAdapter;
import net.yaopao.widget.OnWheelChangedListener;
import net.yaopao.widget.WheelView;
import android.app.Activity;
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
	private int time;
	private WheelView timeV;
	private TextView timeTextV;
	/** 运动计时数据 */
	private Integer[] timeArray = {5,10,15,20,25,30,35,40,45,50,55,60,65,70,75,80,85,90,95,100,105,110,115,120,125,130,135,140,145,150,155,160,165,170,175,180,185,190,195,200,205,210,215,220,225,230,235,240,245,250,255,260,265,270,275,280,285,290,295,300,305,310,315,320,325,330,335,340,345,350,355,360};
	private String[] timeTxtArray = {"05:00","10:00","15:00","20:00","25:00","30:00","35:00","40:00","45:00","50:00","0:55:00","1:00:00","1:05:00","1:10:00","1:15:00","1:20:00","1:25:00","1:30:00","1:35:00","1:40:00","1:45:00","1:50:00","1:55:00","2:00:00","2:05:00","2:10:00","2:15:00","2:20:00","2:25:00","2:30:00","2:35:00","2:40:00","2:45:00","2:50:00","2:55:00","3:00:00","3:05:00","3:10:00","3:15:00","3:20:00","3:25:00","3:30:00","3:35:00","3:40:00","3:45:00","3:50:00","3:55:00","4:00:00","4:05:00","4:10:00","4:15:00","4:20:00","4:25:00","4:30:00","4:35:00","4:40:00","4:45:00","4:50:00","4:55:00","5:00:00","5:05:00","5:10:00","5:15:00","5:20:00","5:25:00","5:30:00","5:35:00","5:40:00","5:45:00","5:50:00","5:55:00","6:00:00"};
	
	public SelectTime(Activity context, final Handler handler,int distanceTime) {
		super(context);
		mContext = context;
		//this.time = Variables.runtarTime + "";
		this.time = distanceTime;
		
		Log.v("wydb", "time1 =" + time);
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
				b.putInt("time", time);
				msg.setData(b);
				handler.sendMessage(msg);
				SelectTime.this.dismiss();
			}
		});
		btn_cancel.setOnClickListener(this);
		OnWheelChangedListener listener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				updateDistance(timeV,false);

			}
		};
		
		updateDistance(timeV,true);
		timeV.addChangingListener(listener);
		
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

	private void updateDistance(WheelView view1,boolean setDefault) {
		view1.setViewAdapter(new ArrayWheelAdapter<String>(mContext, timeTxtArray));
		if(setDefault){
			int sub = countItemSub(this.time);
			view1.setCurrentItem(sub);
		}
		
		int st = view1.getCurrentItem();
		String t = this.timeTxtArray[st];
		this.time = this.timeArray[st];
		timeTextV.setText(t);
	}
	
	/**
	 * 计算当前数据在数组中的下标
	 * @param dis
	 * @return
	 * @author cxy
	 * @date 2014-9-6
	 */
	private int countItemSub(int time){
		int sub = 0;
		int len = this.timeArray.length;
		for(int i = 0; i < len; i++){
			int d = this.timeArray[i];
			if(time == d){
				sub = i;
				break;
			}
		}
		return sub;
	}
	
	public void onClick(View v) {
		this.dismiss();
	}

}
