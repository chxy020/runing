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
        private int curD1;  
        private int curD2;  
        private DistanceNumericAdapter1 distanceAdapter1;  
        private DistanceNumericAdapter2 distanceAdapter2;  
        private WheelView distanceV1,distanceV2;  
      private TextView distanceV;
        public SelectDistance(Activity context,final Handler handler) {  
            super(context);  
            mContext = context;  
            this.distance =SportTargetActivity.distance;  
        	distanceV = (TextView) mContext.findViewById(R.id.target_distance_select);
            LayoutInflater inflater = LayoutInflater.from(context);  
            mMenuView = inflater.inflate(R.layout.pop_distance, null,true);
            viewfipper = new ViewFlipper(context);  
            viewfipper.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,  
                    LayoutParams.WRAP_CONTENT));  
      
            distanceV1 = (WheelView) mMenuView.findViewById(R.id.user_distance1);  
            distanceV2 = (WheelView) mMenuView.findViewById(R.id.user_distance2);  
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
                    updateDistance(distanceV1,distanceV2);  
      
                }  
            };  
            if (distance != null && distance.contains(".")) {
    			String str[] = distance.split("\\.");
    			curD1 = Integer.parseInt(str[0]) ;
    			curD2 = Integer.parseInt(str[1]);
    		}
            distanceAdapter1=new DistanceNumericAdapter1(context, 0, 99, curD1);
            distanceAdapter2=new DistanceNumericAdapter2(context, 0, 9, curD2);
            
            distanceV1.setViewAdapter(distanceAdapter1);
            distanceV2.setViewAdapter(distanceAdapter2);
            distanceV1.setCurrentItem(curD1); 
            distanceV2.setCurrentItem(curD2); 
            distanceV1.addChangingListener(listener);  
            distanceV2.addChangingListener(listener);  
            updateDistance(distanceV1,distanceV2);
            
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
      
      
        private void updateDistance(WheelView view1,WheelView view2) {  
      
      
        	distanceAdapter1 = new DistanceNumericAdapter1(mContext, 0, 99, curD1);  
        	view1.setViewAdapter(distanceAdapter1);  
            String curDistance= view1.getCurrentItem()+".";
            view1.setCurrentItem(view1.getCurrentItem(), true);  
            
            distanceAdapter2 = new DistanceNumericAdapter2(mContext, 0,9, curD2);  
            view2.setViewAdapter(distanceAdapter2);  
            curDistance += view2.getCurrentItem()+"";
            view2.setCurrentItem(view2.getCurrentItem(), true);  
            distance =curDistance; 
            distanceV.setText(distance+"km");
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
            public DistanceNumericAdapter1(Context context, int minValue, int maxValue, int current) {  
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
        	public DistanceNumericAdapter2(Context context, int minValue, int maxValue, int current) {  
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