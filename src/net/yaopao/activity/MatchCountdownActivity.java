package net.yaopao.activity;




import java.util.Timer;
import java.util.TimerTask;

import net.yaopao.assist.Variables;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

public class MatchCountdownActivity extends Activity  {
	private ImageView time1;
	private ImageView time2;
	private ImageView time3;
	private int time=3;
//	private CountDownTimer timer;
	Timer timer = new Timer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_match_countdown);
		time1 = (ImageView)findViewById(R.id.match_countdown_1);
		time2 = (ImageView)findViewById(R.id.match_countdown_2);
		time3 = (ImageView)findViewById(R.id.match_countdown_3);
		 timer.schedule(task, 0, 1000);
	}
	
	
	  TimerTask task = new TimerTask() {  
	        @Override  
	        public void run() {  
	  
	            runOnUiThread(new Runnable() {      // UI thread  
	                @Override  
	                public void run() {  
	                	time--;
	                    int i =time/100;
	    				int j =(time%100)/10;
	    				int k=(time%100)%10;
	    				Log.v("wytime", "i="+i+" j="+j+" k="+k);
	    				update(i,time1);
	    				update(j,time2);
	    				update(k,time3); 
	    				
	                    if(time == 0){  
	                        timer.cancel();  
	                        update(0,time1);
	        				update(0,time2);
	        				update(0,time3);
	        				Intent intent = new Intent(MatchCountdownActivity.this,MatchRunActivity.class);
	        				startActivity(intent);
	        				MatchCountdownActivity.this.finish();
	        				Toast.makeText(MatchCountdownActivity.this, "stop", Toast.LENGTH_LONG).show();
	                    }
	                    
	                }  
	            });  
	        }  
	    };
	
	
protected void update(int i, ImageView view) {
	switch (i) {
	case 0:
		view.setBackgroundResource(R.drawable.r_0);
		break;
	case 1:
		view.setBackgroundResource(R.drawable.r_1);
		break;
	case 2:
		view.setBackgroundResource(R.drawable.r_2);
		break;
	case 3:
		view.setBackgroundResource(R.drawable.r_3);
		break;
	case 4:
		view.setBackgroundResource(R.drawable.r_4);
		break;
	case 5:
		view.setBackgroundResource(R.drawable.r_5);
		break;
	case 6:
		view.setBackgroundResource(R.drawable.r_6);
		break;
	case 7:
		view.setBackgroundResource(R.drawable.r_7);
		break;
	case 8:
		view.setBackgroundResource(R.drawable.r_8);
		break;
	case 9:
		view.setBackgroundResource(R.drawable.r_9);
		break;

	default:
		break;
	}
		
	}
@Override
protected void onDestroy() {
//	timer.cancel();
	super.onDestroy();
}
	/*Handler timer = new Handler();
	 Runnable timerTask = new Runnable() {
		@Override
		public void run() {
			time += 1;
			timer.postDelayed(this, 1000);
		}

	};
	public  void startTimer() {
		timer.postDelayed(timerTask, 1000);
	}

	public  void stopTimer() {
		timer.removeCallbacks(timerTask);

	}*/
}
