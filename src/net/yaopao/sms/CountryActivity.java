package net.yaopao.sms;



import java.util.HashMap;

import android.os.Handler;
import cn.smssdk.SMSSDK;
import cn.smssdk.framework.FakeActivity;

public class CountryActivity extends FakeActivity  {
	public  String country;
	public  String code;
	public  String id;
	public  Handler handler;
	public void onResult(HashMap<String, Object> data) {
		if (data != null) {
			int page = (Integer) data.get("page");
			if (page == 1) {
				String[] countrys = SMSSDK.getCountry((String) data.get("id"));
				// 国家列表返回
				if (countrys!=null) {
					code = (String) countrys[1];
					country=countrys[0];
					id=(String) data.get("id");
				}
				handler.obtainMessage(0, new String[]{code,country,id}).sendToTarget();
				
			} 			
		}
	}
	
//	@SuppressWarnings("unchecked")
//	public void onResult(HashMap<String, Object> data) {
//		if (data != null) {
//			int page = (Integer) data.get("page");
//			if (page == 1) {
//				// 国家列表返回
//				currentId = (String) data.get("id");
//				countryRules = (HashMap<String, String>) data.get("rules");
//				String[] country = SMSSDK.getCountry(currentId);
//				if (country != null) {
//					currentCode = country[1];
//					tvCountryNum.setText("+" + currentCode);
//					tvCountry.setText(country[0]);
//				}
//			} else if (page == 2) {
//				// 验证码校验返回
//				Object res = data.get("res");
//				HashMap<String, Object> phoneMap = (HashMap<String, Object>) data.get("phone");
//				if (res != null && phoneMap != null) {
//					int resId = getStringRes(activity, "smssdk_your_ccount_is_verified");
//					if (resId > 0) {
//						Toast.makeText(activity, resId, Toast.LENGTH_SHORT).show();
//					}
//
//					if (callback != null) {
//						callback.afterEvent(SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE, SMSSDK.RESULT_COMPLETE, phoneMap);
//					}
//					finish();
//				}
//			}
//		}
//	}
}
