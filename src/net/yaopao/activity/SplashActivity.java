package net.yaopao.activity;


import net.yaopao.assist.AutoLogin;
import net.yaopao.assist.Constants;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;
import android.app.Activity;  
import android.content.Intent;  
import android.os.Bundle;  
import android.os.Handler;  
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

/**
 * 显示起始画面
 */
public class SplashActivity extends Activity {  
	
    @Override  
    protected void onCreate(Bundle savedInstanceState) {
    	Log.v("wy","splash");
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.splash);  
        new Handler().postDelayed(new Runnable() {  
            public void run() {  
                Intent mainIntent = new Intent(SplashActivity.this,  
                        MainActivity.class);  
                SplashActivity.this.startActivity(mainIntent);  
                SplashActivity.this.finish();  
            }  
        }, Constants.SPLASH_DISPLAY_LENGHT);  
      //利用开头动画这几秒时间可以初始化变量和自动登录
        Variables.uid=YaoPao01App.sharedPreferences.getInt("uid", 0);
        Log.v("wy", "uid"+Variables.uid+"");
  if (NetworkHandler.isNetworkAvailable(this)) {
	  Variables.network=1;
	  if (Variables.uid!=0) {
     	 new AutoLogin().execute("");
		}
        	
		}else{
			Toast.makeText(this, "请检查网络",
					Toast.LENGTH_LONG).show();
		}
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	Log.v("wy", "SplashActivity destroy");
    }

}  
