package net.yaopao.activity;

import java.util.List;  

import net.yaopao.assist.Variables;
import android.app.Activity;  
import android.app.ActivityManager;  
import android.app.ActivityManager.RunningAppProcessInfo;  
import android.content.BroadcastReceiver;
import android.content.Context;  
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
   
   
public class BaseActivity extends Activity { 
	public static final String registerAction = "gps.action";
	//public boolean isJump=false;
	public String activityOnFront="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerReceiver(jumpReceiver, new IntentFilter(YaoPao01App.forceJumpAction));
	}
        @Override  
        protected void onStop() {  
                super.onStop();  
   
                if (!isAppOnForeground()) {  
                        //app 进入后台  
                        Variables.isActive = false;  
                        Intent intent = new Intent(registerAction);
						intent.putExtra("data", "unregister");
						sendBroadcast(intent);
                }  
        }  
   
        @Override  
        protected void onResume() {  
                // TODO Auto-generated method stub  
                super.onResume();  
               // isJump=true;
                   
                if (!Variables.isActive) {  
                        //app 从后台唤醒，进入前台  
                	 Variables.isActive = true;
                	 Intent intent = new Intent(registerAction);
						intent.putExtra("data", "register");
						sendBroadcast(intent);
                }  
        }  
        @Override
        protected void onPause() {
        	super.onPause();
        	//isJump=false;
        }
   
        /** 
         * 程序是否在前台运行 
         *  
         * @return 
         */  
        public boolean isAppOnForeground() {  
                   
                ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);  
                String packageName = getApplicationContext().getPackageName();  
   
                List<RunningAppProcessInfo> appProcesses = activityManager  
                                .getRunningAppProcesses();  
                if (appProcesses == null)  
                        return false;  
   
                for (RunningAppProcessInfo appProcess : appProcesses) {  
                        if (appProcess.processName.equals(packageName)  
                                        && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {  
                                return true;  
                        }  
                }  
   
                return false;  
        }  
        
    	//跳转监听
	    private BroadcastReceiver jumpReceiver = new BroadcastReceiver() {  
	           
	        @Override  
	        public void onReceive(Context context, Intent intent) {  
	        	unregisterReceiver(this);
	        		Log.v("jump", "jump activityOnFront ="+BaseActivity.this.activityOnFront);
	        		//if (isJump) {
	        		if (BaseActivity.this.activityOnFront.equals(Variables.activityOnFront)) {
	        			//测试代码
//	        			try {
//							Thread.sleep(3000);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
	        			
	        			String target =intent.getExtras().getString("action");
	        			Log.v("jump", "jump +1");
	        			Intent jump = null;
	        			//new Intent(BaseActivity.this,MatchCountdownActivity.class);
//		        		startActivity(jump);
	        			
	        		     if("countdown".equals(target)){
	        		        	//倒计时
	        		        	//这里需要添加页面初始化参数
	        		    	 jump =new Intent(BaseActivity.this,MatchCountdownActivity.class);
	 		        		
	        		        }else if("matchWatch".equals(target)){
	        		        	//看比赛
	        		        	 jump =new Intent(BaseActivity.this,MatchGroupInfoActivity.class);
	        		        	 
	        		        }
	        		        else if("matchRun_normal".equals(target)){
	        		        	//比赛跑步，正常进
	        		        	 jump =new Intent(BaseActivity.this,MatchMainActivity.class);
	        		        }
	        		        else if("matchRun_crash".equals(target)){
	        		        	//比赛跑步，崩溃进入
	        		        	 jump =new Intent(BaseActivity.this,MatchMainRecomeActivity.class);
	        		        }
	        		        else if("finish".equals(target)){
	        		        	//结束比赛
	        		        //	 jump =new Intent(BaseActivity.this,MatchFinishActivity.class);
	        		        }
	        		        
	        		        else if("finishTeam".equals(target)){
	        		        	//结束整队比赛
	        		        	 //jump =new Intent(BaseActivity.this,MatchFinishTeamActivity.class);
	        		        }
	        		     if (jump!=null) {
	        		    	 jump.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        		    	 startActivity(jump);
						}
					}
	        		
	        }  
	    };  
}  