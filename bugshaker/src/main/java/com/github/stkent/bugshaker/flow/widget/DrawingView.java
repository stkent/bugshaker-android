package com.github.stkent.bugshaker.flow.widget;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.github.stkent.bugshaker.R;

public class DrawingView extends View {

	//drawing path
	private Path drawPath;
	//drawing and canvas paint
	private Paint drawPaint, canvasPaint;
	//initial color
	private int paintColor = 0xFF660000;
	//canvas
	private Canvas drawCanvas;
	//canvas bitmap
	private Bitmap canvasBitmap;

	private float brushSize, lastBrushSize;
	private boolean isFilling = false;  //for flood fill

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupDrawing();
	}

	private void setupDrawing() {

		brushSize = getResources().getInteger(R.integer.medium_size);
		lastBrushSize = brushSize;
		drawPath = new Path();
		drawPaint = new Paint();
		drawPaint.setColor(paintColor);
		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(brushSize);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);

		canvasPaint = new Paint(Paint.DITHER_FLAG);
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {

		super.onSizeChanged(width, height, oldWidth, oldHeight);

		canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(canvasBitmap);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
		canvas.drawPath(drawPath, drawPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float touchX = event.getX();
		float touchY = event.getY();

		if (isFilling) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				FloodFill(new Point((int) touchX, (int) touchY));
				break;

			default:
				return true;
			}
		}
		else {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				drawPath.moveTo(touchX, touchY);
				break;

			case MotionEvent.ACTION_MOVE:
				drawPath.lineTo(touchX, touchY);
				break;

			case MotionEvent.ACTION_UP:
				drawCanvas.drawPath(drawPath, drawPaint);
				drawPath.reset();
				break;

			default:
				return false;
			}
		}

		invalidate();
		return true;
	}

	public void setColor(String newColor) {
		invalidate();

		paintColor = Color.parseColor(newColor);
		drawPaint.setColor(paintColor);
		drawPaint.setShader(null);
	}

	public void setBrushSize(float newSize) {
		brushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
			newSize, getResources().getDisplayMetrics());
		drawPaint.setStrokeWidth(brushSize);
	}

	public void setLastBrushSize(float lastSize) {
		lastBrushSize = lastSize;
	}

	public float getLastBrushSize() {
		return lastBrushSize;
	}

	//set mErase true or false
	public void setErase(boolean isErase) {
		if (isErase) {
			drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

		}
		else {
			drawPaint.setXfermode(null);
		}
	}

	private synchronized void FloodFill(Point startPoint) {

		Queue<Point> queue = new LinkedList<>();
		queue.add(startPoint);

		int targetColor = canvasBitmap.getPixel(startPoint.x, startPoint.y);

		while (queue.size() > 0) {
			Point nextPoint = queue.poll();
			if (canvasBitmap.getPixel(nextPoint.x, nextPoint.y) != targetColor) {
				continue;
			}

			Point point = new Point(nextPoint.x + 1, nextPoint.y);

			while ((nextPoint.x > 0) && (canvasBitmap.getPixel(nextPoint.x, nextPoint.y) == targetColor)) {
				canvasBitmap.setPixel(nextPoint.x, nextPoint.y, paintColor);
				if ((nextPoint.y > 0) && (canvasBitmap.getPixel(nextPoint.x, nextPoint.y - 1) == targetColor)) {
					queue.add(new Point(nextPoint.x, nextPoint.y - 1));
				}
				if ((nextPoint.y < canvasBitmap.getHeight() - 1)
					&& (canvasBitmap.getPixel(nextPoint.x, nextPoint.y + 1) == targetColor)) {
					queue.add(new Point(nextPoint.x, nextPoint.y + 1));
				}
				nextPoint.x--;
			}

			while ((point.x < canvasBitmap.getWidth() - 1)
				&& (canvasBitmap.getPixel(point.x, point.y) == targetColor)) {
				canvasBitmap.setPixel(point.x, point.y, paintColor);

				if ((point.y > 0) && (canvasBitmap.getPixel(point.x, point.y - 1) == targetColor)) {
					queue.add(new Point(point.x, point.y - 1));
				}
				if ((point.y < canvasBitmap.getHeight() - 1)
					&& (canvasBitmap.getPixel(point.x, point.y + 1) == targetColor)) {
					queue.add(new Point(point.x, point.y + 1));
				}
				point.x++;
			}
		}

		isFilling = false;
	}
}