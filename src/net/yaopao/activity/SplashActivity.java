package net.yaopao.activity;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import net.yaopao.assist.Constants;
import net.yaopao.assist.DataTool;
import net.yaopao.assist.LoadingDialog;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
	private Bitmap bitmap;  
//	private LoadingDialog dialog;
    @Override  
    protected void onCreate(Bundle savedInstanceState) {
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
//        Log.v("wy", "uid"+Variables.uid+"");
  if (NetworkHandler.isNetworkAvailable(this)) {
	  Variables.network=1;
	  if (Variables.uid!=0) {
//		 dialog = new LoadingDialog(this);
//		 dialog.show();
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
     public  Handler messageHandler = new Handler() {  
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
    private  Runnable connectNet = new Runnable(){  
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
    public  void saveFile() throws IOException {  
        File dirFile = new File(Constants.imagePath);  
        if(!dirFile.exists()){  
            dirFile.mkdirs();  
        }  
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dirFile));  
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);  
        bos.flush();  
        bos.close();  
    }  
    private class AutoLogin extends AsyncTask<String, Void, Boolean> {
    	private String loginJson;
    	
    	@Override
    	protected void onPreExecute() {
    	}

    	@Override
    	protected Boolean doInBackground(String... params) {
    		Log.v("wyuser", "自动登录中");
    		loginJson = NetworkHandler.httpPost(Constants.endpoints	+ Constants.autoLogin, "uid=" + YaoPao01App.sharedPreferences.getInt("uid", 0));
    		if (loginJson!=null&&!"".equals(loginJson)) {
    			return true;
    		}else {
    			return false;
    		}
    		
    	}

    	@Override
    	protected void onPostExecute(Boolean result) {
//    		dialog.dismiss();
    		if (result) {
    			 
    			JSONObject rt = JSON.parseObject(loginJson);
    			int rtCode = rt.getJSONObject("state").getInteger("code");
    			switch (rtCode) {
    			case 0:
    				Variables.islogin=1;
    				Variables.uid=rt.getJSONObject("userinfo").getInteger("uid");
    				Variables.utype=rt.getJSONObject("userinfo").getInteger("utype");
    				//下载头像
    				Variables.headUrl=Constants.endpoints+rt.getJSONObject("userinfo").getString("imgpath");
    				if (Variables.headUrl!=null&&!"".equals(Variables.headUrl)) {
    					messageHandler.obtainMessage(0).sendToTarget();  
    				}
    				DataTool.setUserInfo(loginJson);
    				Log.v("wyuser","loginJson = "+ loginJson);
    				break;
    			default:
    				break;
    			}
    		}else {
    			Toast.makeText(YaoPao01App.getAppContext(), "网络异常，请稍后重试", Toast.LENGTH_LONG).show();
    		}
    	}
    	
    }
}  
