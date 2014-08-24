package net.yaopao.assist;

import net.yaopao.activity.R;
import net.yaopao.activity.YaoPao01App;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

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
	public static void doneSport(Context context) {
		new AlertDialog.Builder(context).setTitle("你确定运动完了吗？")	
		.setItems(
			     new String[] { "是的，完成了", "不，还没完成" }, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						
					}
				})
		.show();
		
	}
}
