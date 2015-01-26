package net.yaopao.activity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.yaopao.assist.Constants;
import net.yaopao.assist.DataTool;
import net.yaopao.assist.Variables;
import net.yaopao.engine.manager.binaryIO.BinaryIOManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

@SuppressLint({ "SdCardPath", "SimpleDateFormat" })
public class SportSaveActivity extends BaseActivity implements OnTouchListener {
	public TextView deleV;
	public TextView saveV;
	public TextView titleV;
	public EditText descV;

	public ImageView mind1V;
	public ImageView mind2V;
	public ImageView mind3V;
	public ImageView mind4V;
	public ImageView mind5V;

	public ImageView way1V;
	public ImageView way2V;
	public ImageView way3V;
	public ImageView way4V;
	public ImageView way5V;
	public ImageView phoV;
	public ImageView phoButton;
	private Bitmap mPhotoBmp;
	private Bitmap mPhotoBackground;
	private String title;
	private SimpleDateFormat sdf1;
	private SimpleDateFormat sdf2;
	
	private static final String IMAGE_FILE_LOCATION = "file:///sdcard/YaoPao/temp.jpg";
	
	private Uri imageUri;//to store the big bitmap
	private int lastId=-1;//获取保存数据的id,-1是无返回或插入错误
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sport_save);
		initLayout();
		imageUri = Uri.parse(IMAGE_FILE_LOCATION);
	}

	private void initLayout() {
		YaoPao01App.runManager.setFeeling(0);
		YaoPao01App.runManager.setRunway(0);
		deleV = (TextView) this.findViewById(R.id.recording_save_dele);
		saveV = (TextView) this.findViewById(R.id.recording_save);
		titleV = (TextView) this.findViewById(R.id.recording_save_title);
		initType();
		Date date = new Date();
		
		sdf1 = new SimpleDateFormat("MM");
		sdf2 = new SimpleDateFormat("dd");
		titleV.setText(sdf1.format(date) + "月" + sdf2.format(date) + "日" + title);
		descV = (EditText) this.findViewById(R.id.recording_save_desc);
		mind1V = (ImageView) this.findViewById(R.id.recording_save_mind1);
		mind2V = (ImageView) this.findViewById(R.id.recording_save_mind2);
		mind3V = (ImageView) this.findViewById(R.id.recording_save_mind3);
		mind4V = (ImageView) this.findViewById(R.id.recording_save_mind4);
		mind5V = (ImageView) this.findViewById(R.id.recording_save_mind5);

		way1V = (ImageView) this.findViewById(R.id.recording_save_way1);
		way2V = (ImageView) this.findViewById(R.id.recording_save_way2);
		way3V = (ImageView) this.findViewById(R.id.recording_save_way3);
		way4V = (ImageView) this.findViewById(R.id.recording_save_way4);
		way5V = (ImageView) this.findViewById(R.id.recording_save_way5);
		phoV = (ImageView) this.findViewById(R.id.recording_save_pho);
		initPhobackgroundImg();
		phoV.setImageBitmap(mPhotoBackground);
		phoV.setScaleType(ScaleType.CENTER_CROP);
		phoButton = (ImageView) this.findViewById(R.id.recording_save_pho_icon);
		deleV.setOnTouchListener(this);
		saveV.setOnTouchListener(this);
		way1V.setOnTouchListener(this);
		mind1V.setOnTouchListener(this);
		mind2V.setOnTouchListener(this);
		mind3V.setOnTouchListener(this);
		mind4V.setOnTouchListener(this);
		mind5V.setOnTouchListener(this);
		way1V.setOnTouchListener(this);
		way2V.setOnTouchListener(this);
		way3V.setOnTouchListener(this);
		way4V.setOnTouchListener(this);
		way5V.setOnTouchListener(this);
		phoV.setOnTouchListener(this);
		phoButton.setOnTouchListener(this);

	}
	private void initPhobackgroundImg() {
		InputStream is= null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		options.inPreferredConfig =Bitmap.Config.RGB_565;     
		options.inPurgeable = true;    
		options.inInputShareable = true; 
		try {
			is = this.getResources().openRawResource(R.drawable.photo_bg);  
			mPhotoBackground = BitmapFactory.decodeStream(is,null,options);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if (is!=null) {
					is.close();
					is=null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		 
		
	}
	private void initType() {
		switch (YaoPao01App.runManager.getHowToMove()) {
		case 1:
			title = "的跑步行";
			break;
		case 2:
			title = "的步行";
			break;
		case 3:
			title = "的自行车骑行";
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int action = event.getAction();
		switch (view.getId()) {

		case R.id.recording_save_dele:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				deleV.setBackgroundResource(R.color.red_h);
				break;
			case MotionEvent.ACTION_UP:
				deleV.setBackgroundResource(R.color.red);
				// 这里要做的是将所有与运动有关的参数还原成默认值
				reset();
				SportSaveActivity.this.finish();
				break;
			}
			break;
		case R.id.recording_save_pho_icon:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				phoButton.setBackgroundResource(R.drawable.button_photo_h);
				break;
			case MotionEvent.ACTION_UP:
				phoButton.setBackgroundResource(R.drawable.button_photo);
				showSetPhotoDialog();
				break;
			}
			break;

		case R.id.recording_save_pho:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				if (phoButton.getVisibility()==View.VISIBLE) {
					showSetPhotoDialog();
				}
				
				break;
			}
			break;
		case R.id.recording_save:
			saveV.setBackgroundResource(R.color.red_h);
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				saveV.setBackgroundResource(R.color.red);
				
				
				saveDataToVarAndManager();
				
				lastId =YaoPao01App.db.saveOneSport();
				DataTool.saveTotalData(YaoPao01App.runManager.distance,YaoPao01App.runManager.during(),YaoPao01App.runManager.score);
				Log.v("wysport", "lastId = "+lastId);
//				Intent myIntent = new Intent();
				// 这里要做的是将所有与运动有关的参数还原成默认值
				reset();
				
				//改成跳转到分享页面 chenxy add
//				myIntent = new Intent(SportSaveActivity.this,
//						SportListActivity.class);
//				startActivity(myIntent);
//				SportSaveActivity.this.finish();
				//分享页面
				Intent myIntent = new Intent(SportSaveActivity.this,SportShareActivity.class);
				myIntent.putExtra("id", lastId + "");
				startActivity(myIntent);
				//end
				SportSaveActivity.this.finish();
//				Log.v("wysport","save  Variables.utime="+Variables.utime);
				break;
			}
			break;
		case R.id.recording_save_mind1:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				mind1V.setBackgroundResource(R.drawable.mood1_h);
				mind2V.setBackgroundResource(R.drawable.mood2);
				mind3V.setBackgroundResource(R.drawable.mood3);
				mind4V.setBackgroundResource(R.drawable.mood4);
				mind5V.setBackgroundResource(R.drawable.mood5);
				break;
			case MotionEvent.ACTION_UP:
//				Variables.mind = 1;
				YaoPao01App.runManager.setFeeling(1);
				break;
			}
			break;
		case R.id.recording_save_mind2:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				mind1V.setBackgroundResource(R.drawable.mood1);
				mind2V.setBackgroundResource(R.drawable.mood2_h);
				mind3V.setBackgroundResource(R.drawable.mood3);
				mind4V.setBackgroundResource(R.drawable.mood4);
				mind5V.setBackgroundResource(R.drawable.mood5);
				break;
			case MotionEvent.ACTION_UP:
//				Variables.mind = 2;
				YaoPao01App.runManager.setFeeling(2);
				break;
			}
			break;
		case R.id.recording_save_mind3:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				mind1V.setBackgroundResource(R.drawable.mood1);
				mind2V.setBackgroundResource(R.drawable.mood2);
				mind3V.setBackgroundResource(R.drawable.mood3_h);
				mind4V.setBackgroundResource(R.drawable.mood4);
				mind5V.setBackgroundResource(R.drawable.mood5);
				break;
			case MotionEvent.ACTION_UP:
//				Variables.mind = 3;
				YaoPao01App.runManager.setFeeling(3);
				break;
			}
			break;
		case R.id.recording_save_mind4:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				mind1V.setBackgroundResource(R.drawable.mood1);
				mind2V.setBackgroundResource(R.drawable.mood2);
				mind3V.setBackgroundResource(R.drawable.mood3);
				mind4V.setBackgroundResource(R.drawable.mood4_h);
				mind5V.setBackgroundResource(R.drawable.mood5);
				break;
			case MotionEvent.ACTION_UP:
//				Variables.mind = 4;
				YaoPao01App.runManager.setFeeling(4);
				break;
			}
			break;
		case R.id.recording_save_mind5:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				mind1V.setBackgroundResource(R.drawable.mood1);
				mind2V.setBackgroundResource(R.drawable.mood2);
				mind3V.setBackgroundResource(R.drawable.mood3);
				mind4V.setBackgroundResource(R.drawable.mood4);
				mind5V.setBackgroundResource(R.drawable.mood5_h);
				break;
			case MotionEvent.ACTION_UP:
//				Variables.mind = 5;
				YaoPao01App.runManager.setFeeling(5);
				break;
			}
			break;
		case R.id.recording_save_way1:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				way1V.setBackgroundResource(R.drawable.way1_h);
				way2V.setBackgroundResource(R.drawable.way2);
				way3V.setBackgroundResource(R.drawable.way3);
				way4V.setBackgroundResource(R.drawable.way4);
				way5V.setBackgroundResource(R.drawable.way5);
				break;
			case MotionEvent.ACTION_UP:
//				Variables.runway = 1;
				YaoPao01App.runManager.setRunway(1);
				break;
			}
			break;
		case R.id.recording_save_way2:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				way1V.setBackgroundResource(R.drawable.way1);
				way2V.setBackgroundResource(R.drawable.way2_h);
				way3V.setBackgroundResource(R.drawable.way3);
				way4V.setBackgroundResource(R.drawable.way4);
				way5V.setBackgroundResource(R.drawable.way5);
				break;
			case MotionEvent.ACTION_UP:
//				Variables.runway = 2;
				YaoPao01App.runManager.setRunway(2);
				break;
			}
			break;
		case R.id.recording_save_way3:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				way1V.setBackgroundResource(R.drawable.way1);
				way2V.setBackgroundResource(R.drawable.way2);
				way3V.setBackgroundResource(R.drawable.way3_h);
				way4V.setBackgroundResource(R.drawable.way4);
				way5V.setBackgroundResource(R.drawable.way5);
				break;
			case MotionEvent.ACTION_UP:
//				Variables.runway = 3;
				YaoPao01App.runManager.setRunway(3);
				break;
			}
			break;
		case R.id.recording_save_way4:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				way1V.setBackgroundResource(R.drawable.way1);
				way2V.setBackgroundResource(R.drawable.way2);
				way3V.setBackgroundResource(R.drawable.way3);
				way4V.setBackgroundResource(R.drawable.way4_h);
				way5V.setBackgroundResource(R.drawable.way5);
				break;
			case MotionEvent.ACTION_UP:
//				Variables.runway = 4;
				YaoPao01App.runManager.setRunway(4);
				break;
			}
			break;
		case R.id.recording_save_way5:
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				way1V.setBackgroundResource(R.drawable.way1);
				way2V.setBackgroundResource(R.drawable.way2);
				way3V.setBackgroundResource(R.drawable.way3);
				way4V.setBackgroundResource(R.drawable.way4);
				way5V.setBackgroundResource(R.drawable.way5_h);
				break;
			case MotionEvent.ACTION_UP:
//				Variables.runway = 5;
				YaoPao01App.runManager.setRunway(5);
				break;
			}
			break;
		}
		return true;
	}

	private void saveDataToVarAndManager() {
		YaoPao01App.runManager.setRemark(descV.getText().toString());
		Variables.clientBinaryFilePath=Variables.getPhoDir()+Variables.getRid();
		BinaryIOManager.writeBinary(Variables.getRid(),Variables.getPhoDir());
	}

	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMddHHmmss");
		return dateFormat.format(date) + ".jpg";
	}
	//还原运动参数
	private void reset(){
		Variables.distance = 0;
		Variables.sport_pho = "";
		Variables.clientImagePathsSmall="";// 客户端缩略图路径
		Variables.clientImagePaths="";// 客户端图片路径
		Variables.clientBinaryFilePath="";// 客户端二进制文件路径
		Variables.serverImagePathsSmall="";// 服务端缩略图路径
		Variables.serverImagePaths="";// 服务端图路径
		Variables.serverBinaryFilePath="";// 服务端二进制文件路径
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		super.activityOnFront=this.getClass().getSimpleName();
		Variables.activityOnFront=this.getClass().getSimpleName();
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		releaseBitmap();
		System.gc();
	}	
	private void releaseBitmap() {
		try {
			releaseViewBitmap(phoV);
			
			if (mPhotoBackground!=null) {
				mPhotoBackground.recycle();
				mPhotoBackground=null;
			}
			if (mPhotoBmp!=null) {
				mPhotoBmp.recycle();
				mPhotoBmp=null;
			}
		} catch (Exception e) {
			
		}
		
	}
	private void releaseViewBitmap(ImageView view) {
		// 把历史的ImageView 图片对象（image_)释放
		if (view == null) {
			return;
		}
		BitmapDrawable bitmapDrawable = (BitmapDrawable) view.getBackground();
		if (bitmapDrawable != null) {
			Bitmap hisBitmap = bitmapDrawable.getBitmap();
			if (hisBitmap.isRecycled() == false) {
				hisBitmap.recycle();
			}
		}
	}
	//拍照
	private void showSetPhotoDialog() {
		final String[] item_type = new String[] { "相机", "相册", "取消" };

		new AlertDialog.Builder(this).setTitle("选取来自").
		// setIcon (R.drawable.icon).
				setItems(item_type, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
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

	private void goGetPhotoFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
		startActivityForResult(intent, Constants.RET_CAMERA);
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
			mPhotoBmp = decodeUriAsBitmap(imageUri);
			phoV.setImageBitmap(mPhotoBmp);
			phoButton.setVisibility(View.GONE);
			try {
				saveFile(mPhotoBmp);
			} catch (IOException e) {
				e.printStackTrace();
			}
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
	public void saveFile(Bitmap bm) throws IOException {
		String path = Constants.sportPho+Variables.getPhoDir();
		String smallPath = Constants.sportPho_s+Variables.getPhoDir();
		
		File dirFile = new File(path);
		File smallFile = new File(smallPath);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		if (!smallFile.exists()) {
			smallFile.mkdirs();
		}
		
		Variables.sport_pho = getPhotoFileName();
		Variables.clientImagePaths  = path+Variables.sport_pho;
		Variables.clientImagePathsSmall = smallPath+Variables.sport_pho;
		
//		Variables.hassportpho=1;
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(Variables.clientImagePaths)));
		bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
		bos.flush();
		bos =new BufferedOutputStream(new FileOutputStream(new File(Variables.clientImagePathsSmall)));
		resize(bm).compress(Bitmap.CompressFormat.JPEG, 100, bos);
		bos.flush();
		bos.close();
	}
	
	 private static Bitmap resize(Bitmap bitmap) {
		  Matrix matrix = new Matrix();
		  matrix.postScale(0.2f,0.2f); //长和宽放大缩小的比例
		  Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
		  return resizeBmp;
		 }
}
