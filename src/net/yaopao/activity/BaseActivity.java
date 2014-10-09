package net.yaopao.activity;

import java.util.List;  

import net.yaopao.assist.Variables;
import android.app.Activity;  
import android.app.ActivityManager;  
import android.app.ActivityManager.RunningAppProcessInfo;  
import android.content.Context;  
import android.content.Intent;
import android.widget.Toast;
   
   
public class BaseActivity extends Activity { 
	public static final String registerAction = "gps.action";
        @Override  
        protected void onStop() {  
                // TODO Auto-generated method stub  
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
   
                   
                if (!Variables.isActive) {  
                        //app 从后台唤醒，进入前台  
                	 Variables.isActive = true;
                	 Intent intent = new Intent(registerAction);
						intent.putExtra("data", "register");
						sendBroadcast(intent);
                }  
        }  
   
        /** 
         * 程序是否在前台运行 
         *  
         * @return 
         */  
        public boolean isAppOnForeground() {  
                // Returns a list of application processes that are running on the  
                // device  
                   
                ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);  
                String packageName = getApplicationContext().getPackageName();  
   
                List<RunningAppProcessInfo> appProcesses = activityManager  
                                .getRunningAppProcesses();  
                if (appProcesses == null)  
                        return false;  
   
                for (RunningAppProcessInfo appProcess : appProcesses) {  
                        // The name of the process that this object is associated with.  
                        if (appProcess.processName.equals(packageName)  
                                        && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {  
                                return true;  
                        }  
                }  
   
                return false;  
        }  
}  