package net.yaopao.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.yaopao.assist.Constants;
import net.yaopao.assist.Variables;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class SportSaveActivity extends Activity implements OnTouchListener {
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
	private String sportPho;
	private String title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sport_save);
		initLayout();
	}

	private void initLayout() {
		Variables.mind = 1;
		Variables.runway = 1;
		sportPho = Constants.sportPho + new Date().getTime()
				+ getPhotoFileName();
		deleV = (TextView) this.findViewById(R.id.recording_save_dele);
		saveV = (TextView) this.findViewById(R.id.recording_save);
		titleV = (TextView) this.findViewById(R.id.recording_save_title);
		initType();
		Date date = new Date();
		titleV.setText(YaoPao01App.getWeekOfDate(date)+title);
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

	private void initType() {
		switch (Variables.runty) {
		case 1:
			title = "的步行";
			break;
		case 2:
			title = "的跑步";
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
				SportRecordActivity.points.clear();
				SportRecordActivity.pointsIndex.clear();
				Variables.utime = 0;
				Variables.pspeed = 0;
				Variables.distance = 0;
				Variables.points=0;
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
				showSetPhotoDialog();
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
				Variables.remarks=descV.getText().toString();
				YaoPao01App.db.saveOneSport();
				Intent myIntent = new Intent();
				// 这里要做的是将所有与运动有关的参数还原成默认值
				SportRecordActivity.points.clear();
				SportRecordActivity.pointsIndex.clear();
				Variables.utime = 0;
				Variables.pspeed = 0;
				Variables.distance = 0;
				Variables.points=0;
				myIntent = new Intent(SportSaveActivity.this,
						SportListActivity.class);
				startActivity(myIntent);
				SportSaveActivity.this.finish();
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
				Variables.mind = 1;
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
				Variables.mind = 2;
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
				Variables.mind = 3;
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
				Variables.mind = 4;
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
				Variables.mind = 5;
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
				Variables.runway = 1;
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
				Variables.runway = 2;
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
				Variables.runway = 3;
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
				Variables.runway = 4;
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
				Variables.runway = 5;
				break;
			}
			break;
		}
		return true;
	}

	private void goGetPhotoFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(sportPho)));
		startActivityForResult(intent, Constants.RET_CAMERA);
	}

	private void goGetPhotoFromGallery() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		Log.v("wydb", "1==");
		startActivityForResult(Intent.createChooser(intent, "选择图片"),
				Constants.RET_GALLERY);
	}

	private void doneGetPhotoFromCamera(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {

			mPhotoBmp = extras.getParcelable("data");
			Log.v("wydb", "mPhotoBmp2==" + mPhotoBmp);
			if (mPhotoBmp == null) {
				return;
			}
			phoV.setImageBitmap(mPhotoBmp);
			phoButton.setVisibility(View.GONE);
		}
	}

	private void doneGetPhotoFromGallery(Intent data) {
		if (data == null) {
			return;
		}
		Uri originalUri = data.getData();
		String imagePath = getAbsoluteImagePath(originalUri);

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		opts.inDither = false;
		opts.inJustDecodeBounds = false;
		opts.outWidth = 160;
		opts.outHeight = 160;
		opts.inPreferredConfig = Bitmap.Config.RGB_565;

		mPhotoBmp = BitmapFactory.decodeFile(imagePath, opts);
		Log.v("wydb", "mPhotoBmp1==" + mPhotoBmp);
		if (mPhotoBmp == null) {
			return;
		}
		phoV.setImageBitmap(mPhotoBmp);
		phoButton.setVisibility(View.GONE);
	}

	private String getAbsoluteImagePath(Uri uri) {
		// can post image
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, proj, // Which columns to return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)

		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
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
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", "JPEG");
		intent.putExtra("noFaceDetection", true);
		// intent.putExtra("output", Uri.parse(imagePath1));
		startActivityForResult(intent, Constants.RET_CROP);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case Constants.RET_CAMERA:
			Log.v("wydb", "4==" + resultCode);
			if (resultCode == Activity.RESULT_OK) {
				File picture = new File(sportPho);
				// startPhotoZoom(Uri.fromFile(picture));
			}
			break;
		case Constants.RET_GALLERY:
			Log.v("wydb", "2==" + resultCode);
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					Log.v("wydb", "3==");
				}
				startPhotoZoom(data.getData());
			}
			break;
		case Constants.RET_CROP:
			if (resultCode == Activity.RESULT_OK) {
				doneGetPhotoFromCamera(data);
			}
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void showSetPhotoDialog() {
		final String[] item_type = new String[] { "相机", "相册", "取消" };

		new AlertDialog.Builder(this).setTitle("选取来自")
				.setItems(item_type, new DialogInterface.OnClickListener() {
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

	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date) + ".jpg";
	}
}
