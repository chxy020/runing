package zc.manager;

import net.yaopao.activity.MainActivity;
import net.yaopao.activity.R;
import net.yaopao.activity.SportSetActivity;
import net.yaopao.activity.YaoPao01App;
import net.yaopao.assist.DialogTool;
import net.yaopao.assist.Variables;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;


public class ManagerTest extends Activity{
	int timeInterval = 2;
	int targetType = 1;
	int targetValue = 0;
	private RadioGroup rg_timeInterval=null;
	private RadioButton rb_time1=null;
	private RadioButton rb_time2=null;
	private RadioButton rb_time3=null;
	private RadioGroup rg_target=null;
	private RadioButton rb_target1=null;
	private RadioButton rb_target2=null;
	private RadioButton rb_target3=null;
	private RadioButton rb_target4=null;
	private RadioButton rb_target5=null;
	private RadioButton rb_target6=null;
	private RadioButton rb_target7=null;
	private Button button;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manager_test);
		rg_timeInterval = (RadioGroup)findViewById(R.id.timeInterval);
		rb_time1 = (RadioButton)findViewById(R.id.two);
		rb_time2 = (RadioButton)findViewById(R.id.three);
		rb_time3 = (RadioButton)findViewById(R.id.five);
		rg_target = (RadioGroup)findViewById(R.id.target);
		rb_target1 = (RadioButton)findViewById(R.id.free);
		rb_target2 = (RadioButton)findViewById(R.id.distance1);
		rb_target3 = (RadioButton)findViewById(R.id.distance2);
		rb_target4 = (RadioButton)findViewById(R.id.distance3);
		rb_target5 = (RadioButton)findViewById(R.id.time1);
		rb_target6 = (RadioButton)findViewById(R.id.time2);
		rb_target7 = (RadioButton)findViewById(R.id.time3);
		button = (Button)findViewById(R.id.button);
		rg_timeInterval.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(rb_time1.getId() == checkedId){
					timeInterval = 2;
				}
				if(rb_time2.getId() == checkedId){
					timeInterval = 3;
				}
				if(rb_time3.getId() == checkedId){
					timeInterval = 5;
				}
			}
			
		});
		rg_target.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(rb_target1.getId() == checkedId){
					targetType = 1;
					targetValue = 0;
				}
				if(rb_target2.getId() == checkedId){
					targetType = 2;
					targetValue = 1000;
				}
				if(rb_target3.getId() == checkedId){
					targetType = 2;
					targetValue = 3000;
				}
				if(rb_target4.getId() == checkedId){
					targetType = 2;
					targetValue = 5000;
				}
				if(rb_target5.getId() == checkedId){
					targetType = 3;
					targetValue = 300;
				}
				if(rb_target6.getId() == checkedId){
					targetType = 3;
					targetValue = 600;
				}
				if(rb_target7.getId() == checkedId){
					targetType = 3;
					targetValue = 1200;
				}
			}
		});
		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.v("zc","时间间隔："+timeInterval);
				Log.v("zc","目标类型："+targetType);
				Log.v("zc","目标值："+targetValue);
				if(!Variables.isTest && YaoPao01App.loc == null){
					Log.v("zc","gps模块没有数据");
					Toast.makeText(ManagerTest.this, "没有gps数据", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(ManagerTest.this,
						ManagerTestDisplay.class);
				startActivity(intent);
				YaoPao01App.runManager = new RunManager(timeInterval);
				YaoPao01App.runManager.setHowToMove(1);
				YaoPao01App.runManager.setTargetType(targetType);
				YaoPao01App.runManager.setTargetValue(targetValue);
				YaoPao01App.runManager.startRun();
			}
		});
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
		    AlertDialog.Builder builder = new AlertDialog.Builder(this);  
		    builder.setMessage("确认退出")  
		           .setCancelable(false)  
		           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {  
		               public void onClick(DialogInterface dialog, int id) {  
		                    System.exit(0);
		               }  
		           })  
		           .setNegativeButton("No", new DialogInterface.OnClickListener() {  
		               public void onClick(DialogInterface dialog, int id) {  
		                    dialog.cancel();  
		               }  
		           });  
		    AlertDialog alert = builder.create();  
		    alert.show();
		}
		return false;
	}
}
