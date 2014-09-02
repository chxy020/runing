package net.yaopao.assist;

import net.yaopao.activity.R;
import net.yaopao.activity.YaoPao01App;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class DialogTool extends Activity {
	public static void quit(Context context) {
		new AlertDialog.Builder(context).setTitle(R.string.app_name)
				.setMessage("确认退出？").setIcon(R.drawable.ic_launcher)
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {
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

	public static void doneSport(Context context, final Handler handker) {
		LayoutInflater inflater = LayoutInflater.from(context);
		final View dialogView = inflater.inflate(R.layout.alert_dialog, null);
		final TextView confirm = (TextView) dialogView
				.findViewById(R.id.alert_confirm);
		final TextView cancel = (TextView) dialogView
				.findViewById(R.id.alert_cancle);

		final Dialog dialog = new Dialog(context, R.style.mydialog);
		dialog.setContentView(dialogView);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		confirm.setOnTouchListener(new OnTouchListener() {
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
					handker.obtainMessage(0).sendToTarget();

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
					handker.obtainMessage(1).sendToTarget();
					break;
				default:
					break;
				}
				return true;
			}
		});
	}

	// gps信号弱
	public void alertGpsTip1(Context context,Display d ) {
		LayoutInflater inflater = LayoutInflater.from(context);
		final View dialogView = inflater.inflate(R.layout.tip_dialog1, null);
		final TextView cancel = (TextView) dialogView
				.findViewById(R.id.tip_cancle);

		final Dialog dialog = new Dialog(context, R.style.mydialog);
		dialog.setContentView(dialogView);
		dialog.setCanceledOnTouchOutside(false);
		
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		p.height = (int) (d.getHeight() * 0.9); // 
		p.width = (int) (d.getWidth() * 0.8); // 
		dialogWindow.setAttributes(p);

		dialog.show();
		cancel.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					cancel.setBackgroundResource(R.color.gray_light);
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
	public  void alertGpsTip2(Context context,Display d,DisplayMetrics dm) {
		LayoutInflater inflater = LayoutInflater.from(context);
		final View dialogView = inflater.inflate(R.layout.tip_dialog2, null);
		final TextView cancel = (TextView) dialogView
				.findViewById(R.id.tip_cancle);

		final Dialog dialog = new Dialog(context, R.style.mydialog);
		dialog.setContentView(dialogView);
		dialog.setCanceledOnTouchOutside(false);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		p.height = (int) (d.getHeight() * 0.9); // 
		p.width = (int) (d.getWidth() * 0.8); // 
		dialogWindow.setAttributes(p);
		
		
		TextView openV = (TextView) dialogView.findViewById(R.id.howto_open);
		TextView cancelV = (TextView) dialogView.findViewById(R.id.tip_cancle);
		TextView setV = (TextView) dialogView.findViewById(R.id.tip_set);
		cancelV.setWidth(dm.widthPixels / 4);
		setV.setWidth(dm.widthPixels / 4);
		openV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		
		
		dialog.show();
		cancel.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					cancel.setBackgroundResource(R.color.gray_light);
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
}
