package com.github.stkent.bugshaker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Set;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.stkent.bugshaker.flow.widget.DrawingView;
import com.github.stkent.bugshaker.utilities.LogcatUtil;
import com.github.stkent.bugshaker.utilities.SendEmailUtil;
import com.github.stkent.bugshaker.utilities.SharedPreferencesUtil;

public class MainActivity extends AppCompatActivity implements OnClickListener {

	private DrawingView drawView;

	private static final String tag = "MainActivity";

	private ImageButton currPaint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		drawView = (DrawingView) findViewById(R.id.drawing);
		LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);
		currPaint = (ImageButton) paintLayout.getChildAt(0);
		currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));

		ImageButton drawBtn = (ImageButton) findViewById(R.id.draw_btn);
		drawBtn.setOnClickListener(this);

		drawView.setBrushSize(getResources().getInteger(R.integer.small_size));

		findViewById(R.id.erase_btn).setOnClickListener(this);
		findViewById(R.id.sendEmail).setOnClickListener(this);
		findViewById(R.id.speechBox).setOnClickListener(this);

		String pathOfScreenshot = getIntent().getStringExtra("uri");
		File screenshotFile = new File(pathOfScreenshot);


		if (screenshotFile.exists()) {
			Bitmap myBitmap = BitmapFactory.decodeFile(pathOfScreenshot);

			drawView = (DrawingView) findViewById(R.id.drawing);
			Drawable temp = new BitmapDrawable(getResources(), myBitmap);
			drawView.setBackgroundDrawable(temp);
		}
		else {
			Log.d(tag, "File doesn't exist");
		}
	}

	public void setButtonClickListener(final float size, ImageButton button, final Dialog dialog, boolean isMarker) {
		if (isMarker) {
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					drawView.setBrushSize(size);
					drawView.setLastBrushSize(size);
					drawView.setErase(false);
					dialog.dismiss();
				}
			});
		}
		else {
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					drawView.setErase(true);
					drawView.setBrushSize(size);
					dialog.dismiss();
				}
			});
		}
	}

	@Override
	public void onClick(View view) {
		final Dialog brushDialog = new Dialog(this);
		brushDialog.setTitle(getString(R.string.brush_dialog_title));
		brushDialog.setContentView(R.layout.brush_chooser);

		ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
		ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
		ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);

		if (view.getId() == R.id.draw_btn) {

			setButtonClickListener(getResources().getInteger(R.integer.small_size), smallBtn, brushDialog, true);
			setButtonClickListener(getResources().getInteger(R.integer.medium_size), mediumBtn, brushDialog, true);
			setButtonClickListener(getResources().getInteger(R.integer.large_size), largeBtn, brushDialog, true);

			brushDialog.show();

		}
		else if (view.getId() == R.id.erase_btn) {

			setButtonClickListener(getResources().getInteger(R.integer.small_size), smallBtn, brushDialog, false);
			setButtonClickListener(getResources().getInteger(R.integer.medium_size), mediumBtn, brushDialog, false);
			setButtonClickListener(getResources().getInteger(R.integer.large_size), largeBtn, brushDialog, false);

			brushDialog.show();
		}

		else if (view.getId() == R.id.sendEmail) {
			saveSendScreenshotAndLog();
		}

	}

	public void paintClicked(View view) {
		drawView.setBrushSize(drawView.getLastBrushSize());

		if (view != currPaint) {
			ImageButton imgView = (ImageButton) view;
			String clickedButtonColor = view.getTag().toString();
			drawView.setColor(clickedButtonColor);
			imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
			currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
			currPaint = (ImageButton) view;
		}
	}

	private Uri getImageUri(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		return Uri.parse(path);
	}

	/*public Set<String> getValueStringSet(Context context) {
		SharedPreferences settings;

		settings = context.getSharedPreferences(PREFS_NAME2, Context.MODE_PRIVATE);
		Set<String> text = (Set<String>) settings.getStringSet(PREFS_KEY2, null);
		return text;
	}*/

	/*public String getValueString(Context context) {
		SharedPreferences settings;
		String text;
		settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		text = settings.getString(PREFS_KEY, null);
		return text;
	}*/

	private void saveSendScreenshotAndLog() {
		AlertDialog.Builder sendDialog = new AlertDialog.Builder(this);

		sendDialog.setTitle(getString(R.string.send_annotated_screenshot));
		sendDialog.setMessage(getString(R.string.attach_annotated_screenshot_to_email));
		sendDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				drawView.setDrawingCacheEnabled(true);

				String imgSaved = MediaStore.Images.Media.insertImage(
					getContentResolver(), drawView.getDrawingCache(),
					ScreenshotUtil.IMAGE_FILE_NAME, ScreenshotUtil.IMAGE_DESCRIPTION);
				Bitmap screenshotBitmap = drawView.getDrawingCache();

				Uri bitmapUri = getImageUri(getApplicationContext(), screenshotBitmap);

				if (imgSaved != null) {
					Toast savedToast = Toast.makeText(getApplicationContext(),
						"Drawing saved to Gallery!", Toast.LENGTH_SHORT);

					File log = LogcatUtil.saveLogcatToFile(getApplicationContext());
					Uri logUri = Uri.fromFile(log);
					Set<String> stringSet = SharedPreferencesUtil.getValueStringSet(getBaseContext());
					String[] stringArray = stringSet.toArray(new String[stringSet.size()]);
					SendEmailUtil.sendEmailWithScreenshot(getBaseContext(), bitmapUri, logUri,
						stringArray
						, SharedPreferencesUtil.getValueString(getBaseContext()));
					savedToast.show();


				}
				else {
					Toast unsavedToast = Toast.makeText(getApplicationContext(),
						"Oops! Image could not be saved.", Toast.LENGTH_SHORT);
					unsavedToast.show();
				}
				drawView.destroyDrawingCache();
			}
		});
		sendDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		sendDialog.show();

	}


}