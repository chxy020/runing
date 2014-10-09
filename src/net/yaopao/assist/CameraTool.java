//package net.yaopao.assist;
//
//import java.io.ByteArrayOutputStream;
//import java.io.FileNotFoundException;
//
//import net.yaopao.activity.MainActivity;
//import net.yaopao.activity.YaoPao01App;
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.media.Image;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//
//public class CameraTool{
//	private static final String IMAGE_FILE_LOCATION = "file:///sdcard/YaoPao/avatar.jpg";
//	private Uri imageUri;
//	public byte[] imageByte;
//	public String upImgJson = "";
//	private Dialog dialog;
//	private ImageView headv;
//	private Context context;
//	
//	public CameraTool(ImageView view,Context context) {
//		headv=view;
//		this.context=context;
//		dialog = new LoadingDialog(context);
//		imageUri = Uri.parse(IMAGE_FILE_LOCATION);
//	}
//	
//	/**
//	 * 拍照并上传头像
//	 */
//public void takeAvatarAndUp(){
//	
//	final String[] item_type = new String[] { "相机", "相册", "取消" };
//
//	new AlertDialog.Builder(context).setTitle("选取来自")
//			.setItems(item_type, new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int which) {
//					Log.v("wycam", "which = " + which);
//					switch (which) {
//					case 0:
//						goGetPhotoFromCamera();
//						break;
//					case 1:
//						goGetPhotoFromGallery();
//						break;
//					}
//				}
//			}).show();
//}
//
//private void goGetPhotoFromCamera() {
//	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//	intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//	Log.v("wycam", "intent="+intent);
//	Log.v("wycam", "Constants="+Constants.RET_CAMERA);
//	startActivityForResult(intent, Constants.RET_CAMERA);
//}
//
////
//public void startPhotoZoom(Uri uri) {
//	Intent intent = new Intent("com.android.camera.action.CROP");
//	intent.setDataAndType(uri, Constants.IMAGE_UNSPECIFIED);
//	intent.putExtra("crop", "true");
//	// aspectX aspectY 是宽高的比例
//	intent.putExtra("aspectX", 1);
//	intent.putExtra("aspectY", 1);
//	// outputX outputY 是裁剪图片宽高
//	intent.putExtra("outputX", 640);
//	intent.putExtra("outputY", 640);
//	intent.putExtra("return-data", false);
//	intent.putExtra("scale", true);
//	intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//	intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//	intent.putExtra("noFaceDetection", true);
//	startActivityForResult(intent, Constants.RET_CROP);
//
//}
//
////
//@Override
//protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//	switch (requestCode) {
//	case Constants.RET_CAMERA:
//		if (resultCode == Activity.RESULT_OK) {
//			// File picture = new File(Constants.tempPath
//			// + "/"+ Constants.tempImage);
//			// startPhotoZoom(Uri.fromFile(picture));
//			startPhotoZoom(imageUri);
//		}
//		break;
//	case Constants.RET_GALLERY:
//		if (resultCode == Activity.RESULT_OK) {
//			if (data != null) {
//				Log.v("zc", "data");
//			}
//			startPhotoZoom(data.getData());
//		}
//		break;
//	case Constants.RET_CROP:
//		if (resultCode == Activity.RESULT_OK) {
//			doneGetPhotoFromCamera();
//		}
//		break;
//	}
//
//	super.onActivityResult(requestCode, resultCode, data);
//}
//
//private void goGetPhotoFromGallery() {
//	Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//	intent.setType("image/*");
//	startActivityForResult(Intent.createChooser(intent, "选择图片"),
//			Constants.RET_GALLERY);
//}
//
//private void doneGetPhotoFromCamera() {
//	if (imageUri != null) {
//		Variables.avatar = decodeUriAsBitmap(imageUri);
//	}
//	ByteArrayOutputStream stream = new ByteArrayOutputStream();
//	Variables.avatar.compress(Bitmap.CompressFormat.PNG, 100, stream);
//	imageByte = stream.toByteArray();
//	new upImgAsyncTask().execute("");
//}
//
//private Bitmap decodeUriAsBitmap(Uri uri) {
//	Bitmap bitmap = null;
//	try {
//		bitmap = BitmapFactory.decodeStream(getContentResolver()
//				.openInputStream(uri));
//	} catch (FileNotFoundException e) {
//		e.printStackTrace();
//		return null;
//	}
//	return bitmap;
//}
//
//private class upImgAsyncTask extends AsyncTask<String, Void, Boolean> {
//	
//	//
//	@Override
//	protected void onPreExecute() {
//	}	
//
//	@Override
//	protected Boolean doInBackground(String... params) {
//		upImgJson = NetworkHandler.upImg(1, "", imageByte);
//		Log.v("wyuser", upImgJson);
//		if (upImgJson != null && !"".equals(upImgJson)) {
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	@Override
//	protected void onPostExecute(Boolean result) {
//		dialog.dismiss();
//		if (result) {
//			JSONObject rt = JSON.parseObject(upImgJson);
//			if (rt != null && !"".equals(upImgJson)) {
//				if (rt.getJSONObject("state").getInteger("code") == 0) {
//					if (Variables.avatar != null) {
//						headv.setImageBitmap(Variables.avatar);
//					} else {
//						Toast.makeText(context, "更新头像失败",
//								Toast.LENGTH_LONG).show();
//					}
//					// mPhotoBmp.recycle();
//					// mPhotoBmp=null;
//				} else {
//					Toast.makeText(context, "更新头像失败",
//							Toast.LENGTH_LONG).show();
//				}
//
//			} else {
//				Toast.makeText(context, "更新头像失败",
//						Toast.LENGTH_LONG).show();
//			}
//
//		} else {
//			Toast.makeText(context, "网络异常，请稍后重试",
//					Toast.LENGTH_LONG).show();
//		}
//	}
//}
//
//}
