package net.yaopao.assist;

import net.yaopao.activity.YaoPao01App;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class AutoLogin extends AsyncTask<String, Void, Boolean> {
	String loginJson;
	@Override
	protected void onPreExecute() {
		// ����ִ�еľ��������
	}

	@Override
	protected Boolean doInBackground(String... params) {
		loginJson = NetworkHandler.httpPost(Constants.endpoints	+ Constants.autoLogin, "uid=" + YaoPao01App.sharedPreferences.getInt("uid", 0));
//		Log.v("wy", loginJson);
		if (loginJson!=null&&!"".equals(loginJson)) {
			return true;
		}else {
			return false;
		}
		
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			
			JSONObject rt = JSON.parseObject(loginJson);
			int rtCode = rt.getJSONObject("state").getInteger("code");
			switch (rtCode) {
			case 0:
				Variables.islogin=1;
				Variables.uid=rt.getJSONObject("userinfo").getInteger("uid");
				Variables.utype=rt.getJSONObject("userinfo").getInteger("utype");
				SharedPreferences.Editor editor = YaoPao01App.sharedPreferences
						.edit();
				editor.putString("userInfo", loginJson);
				editor.commit();
				break;
			default:
				break;
			}
		}else {
			Toast.makeText(YaoPao01App.getAppContext(), "�����쳣�����Ժ�����", Toast.LENGTH_LONG).show();
		}
	}
}