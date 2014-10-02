package net.yaopao.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

import net.yaopao.assist.Constants;
import net.yaopao.assist.DataTool;
import net.yaopao.assist.DialogTool;
import net.yaopao.assist.LoadingDialog;
import net.yaopao.assist.NetworkHandler;
import net.yaopao.assist.Variables;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;

public class UserInfoActivity extends BaseActivity implements OnTouchListener {
	public TextView save;
	public TextView back;
	public TextView femaleV;
	public TextView maleV;
	public EditText nicknameV;
	public EditText realnameV;
	public TextView phoneV;
	public TextView birthdayV;
	public TextView weightV;
	public TextView heightV;
	public EditText signV;
	public ImageView headv;
	public SelectBirthday dateWindow;
	public SelectWeight weightWindow;
	public SelectHeight heightWindow;

	public String gender = "";
	public String phone = "";
	public String weight = "";
	public String height = "";
	public String sign = "";
	public String birthday = "";
	public String realname = "";
	public String nickname = "";
	public String saveJson = "";
	public byte[] imageByte;
	private LoadingDialog dialog;
	private static final String IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpg";
	private Uri imageUri;//to store the big bitmap
	public String upImgJson = "";
	
	/** 生日数据 */
	private String birthDayData = "";
	/** 身高数据 */
	private String heightData = "";
	/** 体重数据 */
	private String weigthData = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);

		save = (TextView) this.findViewById(R.id.userinfo_save);
		back = (TextView) this.findViewById(R.id.userinfo_goback);
		femaleV = (TextView) this.findViewById(R.id.userinfo_gender_female);
		maleV = (TextView) this.findViewById(R.id.userinfo_gender_male);
		nicknameV = (EditText) this.findViewById(R.id.userinfo_nickname);
		realnameV = (EditText) this.findViewById(R.id.userinfo_realname);
		birthdayV = (TextView) this.findViewById(R.id.userinfo_birthday);
		phoneV = (TextView) this.findViewById(R.id.userinfo_phone);
		weightV = (TextView) this.findViewById(R.id.userinfo_weight);
		heightV = (TextView) this.findViewById(R.id.userinfo_height);
		signV = (EditText) this.findViewById(R.id.userinfo_sign);
		headv = (ImageView) this.findViewById(R.id.userinfo_head);

		save.setOnTouchListener(this);
		back.setOnTouchListener(this);
		femaleV.setOnTouchListener(this);
		maleV.setOnTouchListener(this);
		headv.setOnTouchListener(this);
		birthdayV.setOnTouchListener(this);
		weightV.setOnTouchListener(this);
		heightV.setOnTouchListener(this);
		dialog = new LoadingDialog(this);
		nicknameV.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				nickname = nicknameV.getText().toString().trim();
				if (nicknameV.hasFocus() == false) {
					if ("".equals(nickname) || nickname == null) {
						Toast.makeText(UserInfoActivity.this, "昵称不能为空",
								Toast.LENGTH_LONG).show();
					}
				}

			}
		});
		
		//保存生日
		JSONObject user = DataTool.getUserInfo();
		birthDayData = user.getString("birthday");
		birthDayData = birthDayData == null ||"".equals(birthDayData)? "1985-7-15" : birthDayData;
		//保存身高
		heightData = user.getString("height");
		heightData = heightData == null||"".equals(heightData) ? "170cm" : heightData;
		//保存体重
		weigthData = user.getString("weight");
		weigthData = weigthData == null||"".equals(weigthData) ? "70kg" : weigthData;
		imageUri = Uri.parse(IMAGE_FILE_LOCATION);
	}

	@Override
	protected void onResume() {
		super.onResume();
		initLayout();
		MobclickAgent.onResume(this);
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	@Override
	protected void onDestroy() {
		Log.v("wynet", "user onDestroy");
		super.onDestroy();
	}
	private void initLayout() {
		if (Variables.toUserInfo==0) {
			back.setVisibility(View.GONE);
		}else {
			back.setVisibility(View.VISIBLE);
		}
		if (Variables.islogin == 1) {
			JSONObject user = DataTool.getUserInfo();
			if (user != null) {
				String nickname = user.getString("nickname");
				Log.v("wyuser", "nickname = "+nickname+"--");
				Log.v("wyuser", "is 1= "+!"".equals(nickname));
				Log.v("wyuser", "is2 = "+(nickname!=null));
				
				if (!"".equals(user.getString("nickname"))&&user.getString("nickname")!=null) {
					nicknameV.setText(user.getString("nickname"));
				} else {
					nicknameV.setText(DataTool.getPhone());
				}
				realnameV.setText(user.getString("uname"));
				//nicknameV.setText(user.getString("nickname"));
				weightV.setText(user.getString("weight"));
				heightV.setText(user.getString("height"));
				signV.setText(user.getString("signature"));
				headv.setImageBitmap(Variables.avatar);
				phoneV.setText(user.getString("phone"));
				birthdayV.setText(user.getString("birthday"));
				if ("M".equals(user.getString("gender"))) {
					gender = "M";
					femaleV.setBackgroundColor(this.getResources().getColor(
							R.color.white));
					maleV.setBackgroundColor(this.getResources().getColor(
							R.color.blue_dark));
					femaleV.setTextColor(this.getResources().getColor(
							R.color.black));
					maleV.setTextColor(this.getResources().getColor(
							R.color.white));
				} else {
					gender = "F";
					femaleV.setBackgroundColor(this.getResources().getColor(
							R.color.blue_dark));
					maleV.setBackgroundColor(this.getResources().getColor(
							R.color.white));
					femaleV.setTextColor(this.getResources().getColor(
							R.color.white));
					maleV.setTextColor(this.getResources().getColor(
							R.color.black));
				}
			} else {
				nicknameV.setText(DataTool.getPhone());
				phoneV.setText(DataTool.getPhone());
			}

		}
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {

		case R.id.userinfo_save:
			
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				save.setBackgroundResource(R.color.red_h);
				break;
			case MotionEvent.ACTION_UP:
				save.setBackgroundResource(R.color.red);
				nickname = nicknameV.getText().toString().trim();
				realname = realnameV.getText().toString().trim();
				if ("".equals(nickname) || nickname == null) {
					Toast.makeText(UserInfoActivity.this, "昵称不能为空",
							Toast.LENGTH_LONG).show();
				}else if (!checkNikeName(nickname)) {
					Toast.makeText(UserInfoActivity.this, "昵称不符合规范，应为4-30个字节，支持中英文、数字、下划线和减号", Toast.LENGTH_LONG).show() ;
				}else if(!checkName(realname)){
					Toast.makeText(UserInfoActivity.this, "真实姓名不符合规范，应为4-30个字节，支持中英文、数字、下划线和减号", Toast.LENGTH_LONG).show() ;
				}else {
					new saveAsyncTask().execute("");
				}

				break;
			}
			break;
		case R.id.userinfo_head:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				showSetPhotoDialog();
				break;
			}
			break;
		case R.id.userinfo_gender_female:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				femaleV.setBackgroundColor(this.getResources().getColor(
						R.color.blue_dark));
				maleV.setBackgroundColor(this.getResources().getColor(
						R.color.white));
				femaleV.setTextColor(this.getResources()
						.getColor(R.color.white));
				maleV.setTextColor(this.getResources().getColor(R.color.black));

				gender = "F";
				break;
			}
			break;
		case R.id.userinfo_gender_male:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				femaleV.setBackgroundColor(this.getResources().getColor(
						R.color.white));
				maleV.setBackgroundColor(this.getResources().getColor(
						R.color.blue_dark));
				femaleV.setTextColor(this.getResources()
						.getColor(R.color.black));
				maleV.setTextColor(this.getResources().getColor(R.color.white));
				gender = "M";
				break;
			}
			break;
		case R.id.userinfo_goback:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				back.setBackgroundResource(R.color.red_h);
				break;
			case MotionEvent.ACTION_UP:
				back.setBackgroundResource(R.color.red);
				UserInfoActivity.this.finish();
				break;
			}
			break;
		case R.id.userinfo_birthday:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				final Handler handler = new Handler() {
					public void handleMessage(Message msg) {
						birthday = msg.getData().getString("birthday");
						//保存数据
						birthDayData = birthday;
						birthdayV.setText(birthday);
						super.handleMessage(msg);
					}
				};
				// 实例化SelectPicPopupWindow
				dateWindow = new SelectBirthday(UserInfoActivity.this, handler,birthDayData);
				dateWindow.showAtLocation(UserInfoActivity.this
						.findViewById(R.id.userinfo_birthday), Gravity.BOTTOM
						| Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
				break;
			}
			break;
		case R.id.userinfo_height:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				final Handler handler = new Handler() {
					public void handleMessage(Message msg) {
						height = msg.getData().getString("height");
						//保存身高数据
						heightData = height;
						heightV.setText(height);
						super.handleMessage(msg);
					}
				};
				// 实例化SelectPicPopupWindow
				heightWindow = new SelectHeight(UserInfoActivity.this, handler,heightData);
				heightWindow.showAtLocation(UserInfoActivity.this
						.findViewById(R.id.userinfo_height), Gravity.BOTTOM
						| Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
				break;
			}
			break;
		case R.id.userinfo_weight:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				final Handler handler = new Handler() {
					public void handleMessage(Message msg) {
						weight = msg.getData().getString("weight");
						//保存数据
						weigthData = weight;
						weightV.setText(weight);
						super.handleMessage(msg);
					}
				};
				// 实例化SelectPicPopupWindow
				weightWindow = new SelectWeight(UserInfoActivity.this, handler,weigthData);
				weightWindow.showAtLocation(UserInfoActivity.this
						.findViewById(R.id.userinfo_weight), Gravity.BOTTOM
						| Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置

				break;
			}
			break;
		}
		return true;
	}

	private class saveAsyncTask extends AsyncTask<String, Void, Boolean> {
		//
		@Override
		protected void onPreExecute() {
			// 最先执行的就是这个。
		}

		@Override
		protected Boolean doInBackground(String... params) {
			// if (!"".equals(femaleV.getText().toString().trim())
			// && femaleV.getText().toString().trim() != null) {
			// gender = "M";
			// }
			// if (!"".equals(maleV.getText().toString().trim())
			// && maleV.getText().toString().trim() != null) {
			// gender = "F";
			// }
			phone = phoneV.getText().toString().trim();
			weight = weightV.getText().toString().trim();
			height = heightV.getText().toString().trim();
			sign = signV.getText().toString().trim();
			birthday = birthdayV.getText().toString().trim();
			realname = realnameV.getText().toString().trim();
			nickname = nicknameV.getText().toString().trim();
			saveJson = NetworkHandler.httpPost(Constants.endpoints
					+ Constants.saveUserInfo, "gender=" + gender + "&uname="
					+ realname + "&nickname=" + nickname + "&birthday="
					+ birthday + "&height=" + height + "&weight=" + weight
					+ "&signature=" + sign + "&uid=" + Variables.uid
					+ "&utype=" + Variables.utype);
			if (saveJson != null && !"".equals(saveJson)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Log.v("wy", saveJson);
				JSONObject rt = JSON.parseObject(saveJson);
				int rtCode = rt.getJSONObject("state").getInteger("code");
				Log.v("wyuser", "userinfo="+rtCode);
				switch (rtCode) {
				case 0:
					Toast.makeText(UserInfoActivity.this, "修改成功",
							Toast.LENGTH_LONG).show();
					Log.v("wyuser", "保存修改返回的 =" + saveJson);
					Log.v("wyuser", "存之前 =" + DataTool.getUserInfo());
					DataTool.setUserInfo(saveJson);
					Log.v("wyuser", "存之后 =" + DataTool.getUserInfo());
					UserInfoActivity.this.finish();
					break;
					case -7:
							Intent intent = new Intent(UserInfoActivity.this,LoginActivity.class);
							Variables.islogin=3;
							UserInfoActivity.this.finish();
							startActivity(intent);
							break;
				default:
					Toast.makeText(UserInfoActivity.this, "保存失败，请稍后重试",
							Toast.LENGTH_LONG).show();
					break;
				}

			} else {
				Toast.makeText(UserInfoActivity.this, "网络异常，请稍后重试",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	private class upImgAsyncTask extends AsyncTask<String, Void, Boolean> {
		//
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(String... params) {
			upImgJson = NetworkHandler.upImg(1, "", imageByte);
			Log.v("wyuser", upImgJson);
			if (upImgJson != null && !"".equals(upImgJson)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			dialog.dismiss();
			if (result) {
				JSONObject rt = JSON.parseObject(upImgJson);
				if (rt != null && !"".equals(upImgJson)) {
					Log.v("wyuser", "upimg ="+rt.getJSONObject("state").getInteger("code"));
					if (rt.getJSONObject("state").getInteger("code") == 0) {
						if (Variables.avatar!=null) {
							headv.setImageBitmap(Variables.avatar);
						} else {
							Toast.makeText(UserInfoActivity.this, "更新头像失败",
									Toast.LENGTH_LONG).show();
						}
						// mPhotoBmp.recycle();
						// mPhotoBmp=null;
					} else if(rt.getJSONObject("state").getInteger("code") == -7){
						Intent intent = new Intent(UserInfoActivity.this,LoginActivity.class);
						Variables.islogin=3;
						UserInfoActivity.this.finish();
						startActivity(intent);
					}else {
						Toast.makeText(UserInfoActivity.this, "更新头像失败",
								Toast.LENGTH_LONG).show();
					}

				} else {
					Toast.makeText(UserInfoActivity.this, "更新头像失败",
							Toast.LENGTH_LONG).show();
				}

			} else {
				Toast.makeText(UserInfoActivity.this, "网络异常，请稍后重试",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	private void showSetPhotoDialog() {
		final String[] item_type = new String[] { "相机", "相册", "取消" };

		new AlertDialog.Builder(this).setTitle("选取来自").setItems(item_type, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Log.v("wycam", "which = "+which);
						switch (which) {
						case 0:
							goGetPhotoFromCamera();
							break;
						case 1:
							goGetPhotoFromGallery();
							break;
						}
					}
				}).show();
	}
	private void goGetPhotoFromCamera() {
	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
	startActivityForResult(intent, Constants.RET_CAMERA);
}
//
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, Constants.IMAGE_UNSPECIFIED);
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 640);
		intent.putExtra("outputY", 640);
		intent.putExtra("return-data", false);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, Constants.RET_CROP);
		
	
		
	}
//
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case Constants.RET_CAMERA:
			if (resultCode == Activity.RESULT_OK) {
				startPhotoZoom(imageUri);
			}
			break;
		case Constants.RET_CROP:
			if (resultCode == Activity.RESULT_OK) {
				doneGetPhotoFromCamera();
			}
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void goGetPhotoFromGallery() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 640);
		intent.putExtra("outputY", 640);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", false); // no face detection
		startActivityForResult(intent, Constants.RET_CROP);
	}

	private void doneGetPhotoFromCamera() {
		if(imageUri != null){
			Variables.avatar = decodeUriAsBitmap(imageUri);
		}
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			Variables.avatar.compress(Bitmap.CompressFormat.PNG, 100, stream);
			imageByte = stream.toByteArray();
			try {
				new upImgAsyncTask().execute("");
			} catch (Exception e) {
			}
			
	}
		private Bitmap decodeUriAsBitmap(Uri uri){
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}	
	public static boolean checkNikeName(String nikeName) {
		if (nikeName == null || nikeName.length() == 0)
			return false;

		try {
			byte[] b = nikeName.getBytes("UNICODE");
			int i = 0;
			int count = 0;
			for (i = 2; i < b.length; i++) {
				if (b[i] != 0 || (i % 2 == 0)) {
					count++;
				}
			}
			if (count < 4 || count > 30) {
				return false;
			}
		} catch (Exception e) {
		}
		return true;
	}
	public static boolean checkName(String nikeName) {
		if (nikeName == null ) return true;
		if (nikeName.length()==0) return true;
		try {
			byte[] b = nikeName.getBytes("UNICODE");
			int i = 0;
			int count = 0;
			for (i = 2; i < b.length; i++) {
				if (b[i] != 0 || (i % 2 == 0)) {
					count++;
				}
			}
			if (count < 4 || count > 30) {
				return false;
			}
		} catch (Exception e) {
		}
		return true;
	}
}
