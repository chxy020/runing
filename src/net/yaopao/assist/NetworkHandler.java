package net.yaopao.assist;


import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * @author zc 网络请求类，各个函数对应服务器各个接口
 * 
 */
public class NetworkHandler extends Activity {

	/**
	 * 事例函数
	 * 
	 * @return
	 * @throws Exception
	 */
	private static final int REQUEST_TIMEOUT = 60 * 1000;// 设置请求超时一分钟
	private static final int SO_TIMEOUT = 10 * 1000; // 设置等待数据超时时间10秒钟

	public static HttpClient getHttpClient() {
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		HttpClient client = new DefaultHttpClient();
		return client;
	}

	public static String httpPost(String url, String param) {
		if (Variables.network == 0) {
			return null;
		}
		String rtStr = "";
		HttpPost httpRequest = new HttpPost(url);
		try {
			httpRequest.addHeader("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			httpRequest.setEntity(new StringEntity(param, "utf-8"));
			httpRequest.addHeader("Accept", "text/json");
			httpRequest.addHeader("X-PID", Variables.pid);
			httpRequest.addHeader("ua", Variables.ua);
			Log.v("wyuser","url="+url+ " X-PID="+Variables.pid+" User-Agent="+Variables.ua);
			HttpClient httpclient = getHttpClient();
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			Log.v("wyuser", "status:"+ httpResponse.getStatusLine().getStatusCode());
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				rtStr = EntityUtils.toString(httpResponse.getEntity());
			} else {
				return "";
			}
		} catch (Exception e) {
			Log.e("wy", "network error", e);
		}
		return rtStr;
	}
	public static String zcHttpPost(String url, HashMap<String,String> param, byte[] file) {
		if (Variables.network == 0) {
			return null;
		}
		String rtStr = "";
		HttpPost httpRequest = new HttpPost(url);
		try {
			if(file == null){
				StringBuilder sb = new StringBuilder();
				Iterator<String> iterator = param.keySet().iterator();
				while (iterator.hasNext()){
					String key = iterator.next();
					String value = param.get(key);
					sb.append(key);
					sb.append("=");
					sb.append(value);
					if(iterator.hasNext()){
						sb.append("&");
					}
				}
				Log.v("zc","参数是"+sb.toString());
				httpRequest.setEntity(new StringEntity(sb.toString(), "utf-8"));
			}else{
				MultipartEntity mpEntity = new MultipartEntity();
				Iterator<String> iterator = param.keySet().iterator();
				while (iterator.hasNext()){
					String key = iterator.next();
					String value = param.get(key);
					mpEntity.addPart(key,new StringBody(value, Charset.forName("UTF-8")));
				}
				mpEntity.addPart("avatar", new ByteArrayBody(file, ""));
			}
			
			
			httpRequest.addHeader("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			httpRequest.addHeader("Accept", "text/json");
			httpRequest.addHeader("X-PID", Variables.pid);
			httpRequest.addHeader("ua", Variables.ua);
			Log.v("wyuser","url="+url+ " X-PID="+Variables.pid+" User-Agent="+Variables.ua);
			HttpClient httpclient = getHttpClient();
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			Log.v("wyuser", "status:"+ httpResponse.getStatusLine().getStatusCode());
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				rtStr = EntityUtils.toString(httpResponse.getEntity());
			} else {
				return "";
			}
		} catch (Exception e) {
			Log.e("wy", "network error", e);
		}
		
		return rtStr;
	}

	// 通用上传图片的方法
	public static String upImg(int type, String remarks, byte[] imageByte) {
		Log.v("wyuser", "Variables.network===========" + Variables.network);
		if (Variables.network == 0) {
			return null;
		}
		
		String rtStr = "";
		try {

			HttpPost httpRequest = new HttpPost(Constants.endpoints	+ Constants.upImg + "?type=" + type + "&uid="
					+ Variables.uid + "&rid=" + Variables.getRid()
					+ "&remarks=" + remarks);
			httpRequest.addHeader("Accept", "text/json");
			httpRequest.addHeader("X-PID", Variables.pid);
			httpRequest.addHeader("User-Agent", Variables.ua);
			MultipartEntity mpEntity = new MultipartEntity();
			if (imageByte != null) {
				mpEntity.addPart("avatar", new ByteArrayBody(imageByte, ""));
			}
			Log.v("wyuser", "上传图片--size=" + imageByte);
			httpRequest.setEntity(mpEntity);
			HttpClient httpclient = getHttpClient();

			HttpResponse httpResponse = httpclient.execute(httpRequest);
			Log.v("wy", "上传图片--=" + Constants.endpoints + Constants.upImg
					+ "?type=" + type + "&uid=" + Variables.uid + "&rid="
					+ Variables.getRid() + "&remarks=" + remarks);
			Log.v("wyuser", "上传图片--status="
					+ httpResponse.getStatusLine().getStatusCode());
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				rtStr = EntityUtils.toString(httpResponse.getEntity());
				Log.v("wy", "上传" + rtStr);
			}
		} catch (Exception e) {
			Log.v("wy", "上传图片4=" + e);
			e.printStackTrace();
		}
		return rtStr;
	}

	/*
	 * 判断是否有网络
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
			return false;
		} else {
			// 打印所有的网络状态
			NetworkInfo[] infos = cm.getAllNetworkInfo();
			if (infos != null) {
				for (int i = 0; i < infos.length; i++) {
					// Log.d(TAG, "isNetworkAvailable - info: " +
					// infos[i].toString());
					if (infos[i].getState() == NetworkInfo.State.CONNECTED) {
						Log.v("wy", "isNetworkAvailable -  I " + i);
					}
				}
			}

			// 如果仅仅是用来判断网络连接　　　　　　
			// 则可以使用 cm.getActiveNetworkInfo().isAvailable();
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
			if (networkInfo != null) {
				Log.v("wy",
						"isNetworkAvailable - 是否有网络： "
								+ networkInfo.isAvailable());
			} else {
				Log.v("wy", "isNetworkAvailable - 完成,没有网络！");
				return false;
			}

			// 1、判断是否有3G网络
			if (networkInfo != null
					&& networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				Log.v("wy", "isNetworkAvailable - 有3G网络");
				return true;
			} else {
				Log.v("wy", "isNetworkAvailable - 没有3G网络");
			}

			// 2、判断是否有wifi连接
			if (networkInfo != null
					&& networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				Log.v("wy", "isNetworkAvailable - 有wifi连接");
				return true;
			} else {
				Log.v("wy", "isNetworkAvailable - 没有wifi连接");
			}
		}
		return false;
	}
}
