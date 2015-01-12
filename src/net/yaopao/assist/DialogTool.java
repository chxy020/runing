package net.yaopao.assist;

import net.yaopao.activity.HelperGpsActivity;
import net.yaopao.activity.HelperNetworkActivity;
import net.yaopao.activity.MainActivity;
import net.yaopao.activity.R;
import net.yaopao.activity.SportRecordActivity;
import net.yaopao.activity.YaoPao01App;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class DialogTool implements OnTouchListener {
	TextView openV;
	TextView cancelV;

	TextView setV;
	Context context;
	public Dialog dialog;
	Display d;
	Window dialogWindow;
	WindowManager.LayoutParams p ;
	LayoutInflater inflater ;
	public DialogTool(Context context) {
		this.context = context;
		inflater= LayoutInflater.from(context);
		WindowManager m = ((Activity) context).getWindowManager();
		d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		
		dialog = new Dialog(context, R.style.mydialog);
		dialog.setCanceledOnTouchOutside(false);
		
		dialogWindow = dialog.getWindow();
		p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		p.height = (int) (d.getHeight() * 0.9);
		p.width = (int) (d.getWidth() * 0.7);
		dialogWindow.setAttributes(p);
		
		Log.v("wysport", "11111----height = "+d.getHeight()+" width="+d.getWidth());
		Log.v("wysport", "22222----height = "+p.height+" width="+p.width);
		
		
	}


	public static void quit(Context context) {
		new AlertDialog.Builder(context).setTitle(R.string.app_name).setIcon(R.drawable.icon_s)
				.setMessage("确认退出？").setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						YaoPao01App.db.closeDB();
						dialog.cancel();
						System.exit(0);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				}).show();

	}
	

	public  void doneSport( final Handler handler) {
		//LayoutInflater inflater = LayoutInflater.from(context);
		final View dialogView = inflater.inflate(R.layout.alert_dialog, null);
		final TextView confirm = (TextView) dialogView
				.findViewById(R.id.alert_confirm);
		final TextView cancel = (TextView) dialogView
				.findViewById(R.id.alert_cancle);

		dialog.setContentView(dialogView);
		dialog.show();
		confirm.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					confirm.setBackgroundResource(R.color.gray_dark);
					break;
				case MotionEvent.ACTION_UP:
					confirm.setBackgroundResource(R.color.gray_light);
					dialog.dismiss();
					//抛掉最后暂停的点
					if (SportRecordActivity.points.size()>0) {
						Log.v("wysports", "SportRecordActivity.points.size() = "+SportRecordActivity.points.size());
//						YaoPao01App.lts.writeFileToSD("没有抛点之前的运动记录点: " +SportRecordActivity.points+"size="+SportRecordActivity.points.size(), "uploadLocation");
						int i = 0;
						try{
						for (i = (SportRecordActivity.points.size()-1); SportRecordActivity.points.get(i).status==1; i--) {
							
//							YaoPao01App.lts.writeFileToSD("运动完成抛点: " +i+" status="+SportRecordActivity.points.get(i).status, "uploadLocation");
							if (i==0) {
								break;
							}
							SportRecordActivity.points.remove(i);
						}
						}catch(Exception e){
//							YaoPao01App.lts.writeFileToSD("最后抛点异常:i = "+i+" SportRecordActivity.points.size() = "+SportRecordActivity.points.size()+" SportRecordActivity.points.size()>0=="+(SportRecordActivity.points.size()>0), "uploadLocation");
							Log.v("wysports", "i = "+i+" SportRecordActivity.points.size() = "+SportRecordActivity.points.size()+" SportRecordActivity.points.size()>0=="+(SportRecordActivity.points.size()>0));
						}
					}
					//计算距离积分
					YaoPao01App.calDisPoints(context,handler);
					Variables.gpsLevel=1;
					break;
				default:
					break;
				}
				return true;
			}
		});
		cancel.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					cancel.setBackgroundResource(R.color.gray_dark);
					break;
				case MotionEvent.ACTION_UP:
					cancel.setBackgroundResource(R.color.gray_light);
					dialog.dismiss();
					//handker.obtainMessage(1).sendToTarget();
					break;
				default:
					break;
				}
				return true;
			}
		});
	}

	// gps信号弱
	public void alertGpsTip1() {
//		LayoutInflater inflater = LayoutInflater.from(context);
		final View dialogView = inflater.inflate(R.layout.tip_dialog1, null);
		final TextView cancel = (TextView) dialogView.findViewById(R.id.tip_cancle);
		dialog.setContentView(dialogView);


//		Window dialogWindow = dialog.getWindow();
//		WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		p.height = (int) (d.getHeight() * 0.9); //
		p.width = (int) (d.getWidth() * 0.7); //
		dialogWindow.setAttributes(p);

		dialog.show();
		cancel.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					cancel.setBackgroundResource(R.color.blue_h);
					break;
				case MotionEvent.ACTION_UP:
					cancel.setBackgroundResource(R.color.blue_dark);
					dialog.dismiss();
					// handker.obtainMessage(1).sendToTarget();
					break;
				default:
					break;
				}
				return true;
			}
		});
	}

	// gps关闭
	@SuppressWarnings("deprecation")
	public void alertGpsTip2() {
		//LayoutInflater inflater = LayoutInflater.from(context);
		View dialogView = inflater.inflate(R.layout.tip_dialog2, null);
		//Dialog	dialog = new Dialog(context, R.style.mydialog);
		dialog.setContentView(dialogView);
		openV = (TextView) dialogView.findViewById(R.id.howto_open);
		cancelV = (TextView) dialogView.findViewById(R.id.tip2_cancle);
		setV = (TextView) dialogView.findViewById(R.id.tip2_set);
		p.height = (int) (d.getHeight() * 0.9);
		p.width = (int) (d.getWidth() * 0.7);
		dialogWindow.setAttributes(p);
		cancelV.getLayoutParams().width = p.width / 2;
		setV.getLayoutParams().width = p.width / 2;
		openV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		
		dialog.show();
		cancelV.setOnTouchListener(this);
		setV.setOnTouchListener(this);
		openV.setOnTouchListener(this);
	}
	/**
	 * 网络未开启提示
	 * @param d
	 */
	@SuppressWarnings("deprecation")
	public void alertNetworkTip() {
//		LayoutInflater inflater = LayoutInflater.from(context);
		View dialogView = inflater.inflate(R.layout.tip_network, null);
//		dialog = new Dialog(context, R.style.mydialog);
		dialog.setContentView(dialogView);
		openV = (TextView) dialogView.findViewById(R.id.howto_open_network);
		cancelV = (TextView) dialogView.findViewById(R.id.network_cancle);
		setV = (TextView) dialogView.findViewById(R.id.network_set);
		p.height = (int) (d.getHeight() * 0.9);
		p.width = (int) (d.getWidth() * 0.7);
		dialogWindow.setAttributes(p);
		cancelV.getLayoutParams().width = p.width / 2;
		openV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		
//		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		cancelV.setOnTouchListener(this);
		setV.setOnTouchListener(this);
		openV.setOnTouchListener(this);
	}
	// 在其他设备登陆
	@SuppressWarnings("deprecation")
	public void alertLoginOnOther() {
		View dialogView = inflater.inflate(R.layout.tip_dialog3, null);
		dialog.setContentView(dialogView);
		cancelV = (TextView) dialogView.findViewById(R.id.tip3_cancel);
		p.height = (int) (d.getHeight() * 0.9);
		p.width = (int) (d.getWidth() * 0.7);
		dialogWindow.setAttributes(p);
		dialog.show();
		cancelV.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					cancelV.setBackgroundResource(R.color.blue_h);
					break;
				case MotionEvent.ACTION_UP:
					cancelV.setBackgroundResource(R.color.blue_dark);
					dialog.dismiss();
					// handker.obtainMessage(1).sendToTarget();
					break;
				default:
					break;
				}
				return true;
			}
		});
	}
	public void alertNotIntakeOver() {
		View dialogView = inflater.inflate(R.layout.tip_dialog_not_in, null);
		dialog.setContentView(dialogView);
		p.height = (int) (d.getHeight() * 0.7);
		p.width = (int) (d.getWidth() * 0.7);
		dialogWindow.setAttributes(p);
		dialog.setCancelable(false);
		dialog.show();
	}
	public void alertSyncTime() {
		View dialogView = inflater.inflate(R.layout.tip_dialog_sync_time, null);
		dialog.setContentView(dialogView);
		p.height = (int) (d.getHeight() * 0.7);
		p.width = (int) (d.getWidth() * 0.7);
		dialogWindow.setAttributes(p);
		dialog.setCancelable(false);
		dialog.show();
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {

		int action = event.getAction();
		switch (view.getId()) {
		case R.id.tip2_cancle:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				cancelV.setBackgroundResource(R.color.blue_h);
				break;
			case MotionEvent.ACTION_UP:
				cancelV.setBackgroundResource(R.color.blue_dark);
				dialog.dismiss();
				// handker.obtainMessage(1).sendToTarget();
				break;
			default:
				break;
			}
			break;
		case R.id.tip2_set:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				setV.setBackgroundResource(R.color.blue_h);
				break;
			case MotionEvent.ACTION_UP:
				setV.setBackgroundResource(R.color.blue_dark);
				openSetGps();
				dialog.dismiss();
				break;
			default:
				break;
			}
			break;
		case R.id.network_cancle:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				cancelV.setBackgroundResource(R.color.blue_h);
				break;
			case MotionEvent.ACTION_UP:
				cancelV.setBackgroundResource(R.color.blue_dark);
				dialog.dismiss();
				// handker.obtainMessage(1).sendToTarget();
				break;
			default:
				break;
			}
			break;
		case R.id.network_set:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				setV.setBackgroundResource(R.color.blue_h);
				break;
			case MotionEvent.ACTION_UP:
				setV.setBackgroundResource(R.color.blue_dark);
				openSetNetwork();
				dialog.dismiss();
				break;
			default:
				break;
			}
			break;
		case R.id.howto_open:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				//dialog.dismiss();
				//handler.obtainMessage(0).sendToTarget();
				howToOpenGps();
				break;
			default:
				break;
			}
			break;
		case R.id.howto_open_network:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				howToOpenNetwork();
				dialog.dismiss();
				break;
			default:
				break;
			}
			break;

		default:
			break;
		}

		return true;
	}
	private void howToOpenNetwork() {
		Intent intent = new Intent(context, HelperNetworkActivity.class);
		context.startActivity(intent);
	}
	private void howToOpenGps() {
		Intent intent = new Intent(context, HelperGpsActivity.class);
		// startActivityForResult(intent, 0);
		context.startActivity(intent);

	}
	private void openSetGps() {
		Intent intent = new Intent();
		intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			context.startActivity(intent);
		} catch (Exception ex) {

			try {
				context.startActivity(intent);
			} catch (Exception e) {
			}
		}

	}
	private void openSetNetwork() {
			context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
	}
	
}
