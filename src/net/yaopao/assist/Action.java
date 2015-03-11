package net.yaopao.assist;

import net.yaopao.activity.LoginActivity;
import net.yaopao.engine.manager.CNCloudRecord;
import android.app.Activity;
import android.content.Intent;

/**
 * 动作工具类
 * @author a
 *
 */
public class Action {
	// 同步记录后更新UI 1-首页，11-首页，需要完成时关闭提示框，2-记录列表,3-保存页面，4-登录页面
	public static int MAIN1 =1;
	public static int MAIN11 =11;
	public static int LIST =2;
	public static int SAVE =3;
	public static int LOGIN =4;
	/**
	 * 同步记录
	 * @param activityNo 同步记录后更新UI 1-首页，2-记录列表,3-保存页面，4-登录页面
	 * @param activity 当前页面 
	 */
		public void synAction(int activityNo,Activity activity){
			Variables.updateUI=activityNo;
			Variables.activity=activity;
			CNCloudRecord cloudRecord = new CNCloudRecord();
			cloudRecord.startCloud();
		}
		
		/**
		 * 账号在其他设备登陆
		 * @param activity 当前页面
		 */
		public void toLoginActivity(Activity activity){
			Intent intent = new Intent(activity,LoginActivity.class);
			Variables.islogin=3;
			DataTool.setUid(0);
			Variables.uid=0;
			Variables.headUrl="";
			
//			if (Variables.avatar!=null) {
//				Variables.avatar.recycle();
//				Variables.avatar=null;
//			}
			
			Variables.userinfo = null;
			Variables.matchinfo = null;
			//如果是首页，不关闭当前activity,如果是登录页面不跳转
			if (Variables.updateUI!=1) {
				if (Variables.updateUI==4) {
					DialogTool.synTextHandler.obtainMessage(3).sendToTarget();
					DialogTool dialog = new DialogTool(activity);
					dialog.alertLoginOnOther();
				}else {
					activity.finish();
					activity.startActivity(intent);
				}
			}else {
				DialogTool.synTextHandler.obtainMessage(3).sendToTarget();
				activity.startActivity(intent);
			}
			
			
		}
}
