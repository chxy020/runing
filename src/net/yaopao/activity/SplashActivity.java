package net.yaopao.activity;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import net.yaopao.assist.AutoLogin;
import net.yaopao.assist.Constants;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

/**
 * 显示起始画面
 */
public class SplashActivity extends Activity {  
	private static Bitmap bitmap;  
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
     public static Handler messageHandler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {  
        	if (msg.what == 0){
        		new Thread(connectNet).start();
        	}
        	 
        }  
    };  
    /* 
     * 连接网络 
     * 由于在4.0中不允许在主线程中访问网络，所以需要在子线程中访问 
     */  
    private static Runnable connectNet = new Runnable(){  
        @Override  
        public void run() {  
            try {  
                bitmap = BitmapFactory.decodeStream(getImageStream(Variables.headUrl));  
                saveFile();
            } catch (Exception e) {  
                Toast.makeText(YaoPao01App.getAppContext(),"无法链接网络！", Toast.LENGTH_SHORT).show();  
                e.printStackTrace();  
            }  
  
        }  
  
    };  
    /**   
     * Get image from newwork   
     * @param path The path of image   
     * @return InputStream 
     * @throws Exception   
     */  
    public static InputStream getImageStream(String path) throws Exception{     
        URL url = new URL(path);     
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();     
        conn.setConnectTimeout(5 * 1000);     
        conn.setRequestMethod("GET");  
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){     
            return conn.getInputStream();        
        }     
        return null;   
    }  
    /** 
     * 保存文件 
     * @param bm 
     * @param fileName 
     * @throws IOException 
     */  
    public static void saveFile() throws IOException {  
        File dirFile = new File(Constants.imagePath);  
        if(!dirFile.exists()){  
            dirFile.mkdirs();  
        }  
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dirFile));  
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);  
        bos.flush();  
        bos.close();  
    }  
}  
