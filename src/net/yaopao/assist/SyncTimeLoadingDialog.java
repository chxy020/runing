package net.yaopao.assist;

import net.yaopao.activity.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SyncTimeLoadingDialog extends Dialog {

	public SyncTimeLoadingDialog(Context context) {
		super(context, R.style.loadingDialogStyle);
	}

	private SyncTimeLoadingDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_sync_time_loading);
		LinearLayout linearLayout = (LinearLayout) this
				.findViewById(R.id.LinearLayout);
		linearLayout.getBackground().setAlpha(210);
	}
}