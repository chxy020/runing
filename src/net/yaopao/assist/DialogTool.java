package net.yaopao.assist;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import net.yaopao.activity.R;
import net.yaopao.activity.SportRecordActivity;
import net.yaopao.activity.YaoPao01App;
import net.yaopao.voice.PlayVoice;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

public class DialogTool implements OnTouchListener {
	TextView openV;
	TextView cancelV;
	Dialog dialog;
	TextView setV;
	Context context;
	Handler handler;
	public DialogTool(Context context,Handler handler) {
		this.context = context;
		this.handler=handler;
	}

	public static void quit(Context context) {
		new AlertDialog.Builder(context).setTitle(R.string.app_name)
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

	public static void doneSport(final Context context, final Handler handler) {
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
						for (int i = (SportRecordActivity.points.size()-1); SportRecordActivity.points.get(i).status==1; i--) {
							SportRecordActivity.points.remove(i);
						}
					}
					//计算距离积分
					YaoPao01App.calDisPoints(context,handler);
					
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
	public void alertGpsTip1(Display d) {
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
	public void alertGpsTip2(Display d) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View dialogView = inflater.inflate(R.layout.tip_dialog2, null);
		dialog = new Dialog(context, R.style.mydialog);
		dialog.setContentView(dialogView);
		openV = (TextView) dialogView.findViewById(R.id.howto_open);
		cancelV = (TextView) dialogView.findViewById(R.id.tip2_cancle);
		setV = (TextView) dialogView.findViewById(R.id.tip2_set);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		p.height = (int) (d.getHeight() * 0.9);
		p.width = (int) (d.getWidth() * 0.8);
		dialogWindow.setAttributes(p);
		cancelV.getLayoutParams().width = p.width / 2;
		setV.getLayoutParams().width = p.width / 2;
		openV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		cancelV.setOnTouchListener(this);
		setV.setOnTouchListener(this);
		openV.setOnTouchListener(this);
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
				openset();
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
				handler.obtainMessage(0).sendToTarget();
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

	private void openset() {
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
}
