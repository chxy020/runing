package net.yaopao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainSettingActivity extends Activity {

	/** 返回按钮 */
	private Button mSettingBackBtn = null;
	/** 个人信息 */
	private LinearLayout mPersonalLayout = null;
	/** 服务条款 */
	private LinearLayout mClauseLayout = null;
	/** 关于 */
	private LinearLayout mAboutLayout = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_setting);

		// 初始化
		this.initLoad();
	}

	/**
	 * 页面初始化,获取页面元素
	 * 
	 * @author cxy
	 * @date 2014-8-25
	 */
	private void initLoad() {
		this.mSettingBackBtn = (Button) this
				.findViewById(R.id.setting_back_btn);
		this.mPersonalLayout = (LinearLayout) this
				.findViewById(R.id.personal_layout);
		this.mClauseLayout = (LinearLayout) this
				.findViewById(R.id.clause_layout);
		this.mAboutLayout = (LinearLayout) this.findViewById(R.id.about_layout);
		// 注册事件
		this.setListener();
	}

	/**
	 * 注册事件
	 * 
	 * @author cxy
	 * @date 2014-8-25
	 */
	private void setListener() {
		// 注册设置事件
		this.mSettingBackBtn.setOnClickListener(mOnClickListener);
		this.mPersonalLayout.setOnClickListener(mOnClickListener);
		this.mClauseLayout.setOnClickListener(mOnClickListener);
		this.mAboutLayout.setOnClickListener(mOnClickListener);
	}

	/**
	 * 单击事件
	 */
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.setting_back_btn:
				// 返回
				MainSettingActivity.this.finish();
				break;
			case R.id.personal_layout:
				// 跳转到个人信息
				// Toast.makeText(MainSettingActivity.this,"跳转到个人信息",Toast.LENGTH_LONG).show();
				Intent userInfoIntent = new Intent(MainSettingActivity.this,
						UserInfoActivity.class);
				startActivity(userInfoIntent);
				break;
			case R.id.clause_layout:
				Intent clauseIntent = new Intent(MainSettingActivity.this,
						ClauseActivity.class);
				startActivity(clauseIntent);
				// Toast.makeText(MainSettingActivity.this,"跳转到服务条款",Toast.LENGTH_LONG).show();
				break;
			case R.id.about_layout:
				Intent aboutIntent = new Intent(MainSettingActivity.this,
						AboutActivity.class);
				startActivity(aboutIntent);
				// Toast.makeText(MainSettingActivity.this,"跳转到关于",Toast.LENGTH_LONG).show();
				break;
			}
		}
	};
}
