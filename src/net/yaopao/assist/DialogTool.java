package net.yaopao.assist;

import net.yaopao.activity.MainActivity;
import net.yaopao.activity.MapActivity;
import net.yaopao.activity.R;
import net.yaopao.activity.RegisterActivity;
import net.yaopao.activity.UserInfoActivity;
import net.yaopao.activity.YaoPao01App;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class DialogTool {
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
	public static void doneSport(Context context,final Handler handker) {
		LayoutInflater inflater = LayoutInflater.from(context);
		final View dialogView = inflater.inflate(R.layout.alert_dialog, null);
		final TextView confirm = (TextView) dialogView.findViewById(R.id.alert_confirm);
		final TextView cancel = (TextView) dialogView.findViewById(R.id.alert_cancle);
		
		final Dialog dialog = new Dialog(context,R.style.mydialog);
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
}
