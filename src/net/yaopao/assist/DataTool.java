package net.yaopao.assist;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.BitSet;

import net.yaopao.activity.YaoPao01App;
import android.R.integer;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class DataTool {
	// �û���Ϣ
	public static JSONObject getUserInfo() {
		JSONObject rt = JSON.parseObject(YaoPao01App.sharedPreferences
				.getString("userInfo", null));
		JSONObject userInfo = rt.getJSONObject("userinfo");
		return userInfo;
	}

	public static void setUserInfo(String data) {
		SharedPreferences.Editor editor = YaoPao01App.sharedPreferences.edit();
		editor.putString("userInfo", data);
		editor.putInt("uid", Variables.uid);
		editor.commit();

	}

	// ͷ��
	public static String getHeadUrl() {
		JSONObject rt = JSON.parseObject(YaoPao01App.sharedPreferences
				.getString("head", null));
		return Constants.endpoints + rt.getString("path120");
	}
	@SuppressLint("NewApi")
	public static boolean saveHead(Bitmap mPhotoBmp){
		Log.v("wy", "save head="+mPhotoBmp.getByteCount());
		FileOutputStream  m_fileOutPutStream = null;
		try {
		    m_fileOutPutStream = new FileOutputStream(Constants.imagePath);//д����ļ�·��
		} catch (FileNotFoundException e) {
		e.printStackTrace();
		}
		mPhotoBmp.compress(CompressFormat.PNG, 100, m_fileOutPutStream);
		try {
		m_fileOutPutStream.flush();
		m_fileOutPutStream.close();
		} catch (IOException e) {
		e.printStackTrace();
		return false;
		}
		Log.v("wy", "save head=true");
		return true;
	}
	public static Bitmap getHead(){
		Bitmap bitmap=null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(Constants.imagePath);
			bitmap  = BitmapFactory.decodeStream(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				fis.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return bitmap;
	}

	public static void setHead(String data) {
		SharedPreferences.Editor editor = YaoPao01App.sharedPreferences.edit();
		editor.putString("head", data);
		editor.commit();

	}

	// �绰����
	public static String getPhone() {
		return YaoPao01App.sharedPreferences.getString("phone", "");
	}

	public static void setPhone(String phone) {
		SharedPreferences.Editor editor = YaoPao01App.sharedPreferences.edit();
		editor.putString("phone", phone);
		editor.commit();
	}

	// uid
	public static int getUid() {
		return YaoPao01App.sharedPreferences.getInt("uid", 0);
	}
}
