//package net.yaopao.assist;
//
//import net.yaopao.activity.LoginActivity;
//import android.app.Activity;
//import android.content.Intent;
//
//public class LoginOnOthersAction  {
//
//	public void toLoginActivity(Activity activity){
//		Intent intent = new Intent(activity,LoginActivity.class);
//		Variables.islogin=3;
//		DataTool.setUid(0);
//		Variables.uid=0;
//		Variables.headUrl="";
//		if (Variables.avatar!=null) {
//			Variables.avatar.recycle();
//			Variables.avatar=null;
//		}
//		
//		Variables.userinfo = null;
//		Variables.matchinfo = null;
//		//如果是首页，不关闭当前activity,如果是登录页面不跳转
//		if (Variables.updateUI!=1) {
//			if (Variables.updateUI==4) {
//				DialogTool.synTextHandler.obtainMessage(3).sendToTarget();
//				DialogTool dialog = new DialogTool(activity);
//				dialog.alertLoginOnOther();
//			}else {
//				activity.finish();
//				activity.startActivity(intent);
//			}
//		}else {
//			DialogTool.synTextHandler.obtainMessage(3).sendToTarget();
//			activity.startActivity(intent);
//		}
//		
//		
//	}
//}
