package com.heqifuhou.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

public class BitmapUtil {
	static class ToByteArrayNoCopyByteArrayOutputStream extends ByteArrayOutputStream{
		public byte[] toByteArray(){
			return this.buf;
		}
		public int getCount(){  
	        return this.count;  
	    }  
	}

	// 放大缩小图片
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if (w == width && h == height) {
			return bitmap;
		}
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newbmp;
	}

	// 缩小图片，如果图片小于指定大小，则不缩放
	public static Bitmap decodeZoomBitmap(String fileName, int w, int h) {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(fileName, options);
			int widthSize = (int) Math.ceil(options.outWidth / w);
			int heightSize = (int) Math.ceil(options.outHeight / h);
			if (widthSize > 1 && heightSize > 1) {
				if (widthSize > heightSize) {
					options.inSampleSize = widthSize;
				} else {
					options.inSampleSize = heightSize;
				}
			}
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeFile(fileName, options);
		} catch (Exception e) {
		}
		return null;
	}

	public static byte[] decodeToCompressSize(String fileName,int maxSizeKB) {
		try{
			BitmapFactory.Options options = new BitmapFactory.Options();
			Bitmap image = BitmapFactory.decodeStream(
					new FileInputStream(fileName), null, options);
			ToByteArrayNoCopyByteArrayOutputStream baos = new ToByteArrayNoCopyByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 这里100表示不压缩，将不压缩的数据存放到baos中
			int per = 100;
			while (baos.getCount()/ 1024 > maxSizeKB) { // 循环判断如果压缩后图片是否大于500kb,大于继续压缩
				baos.reset();// 重置baos即清空baos
				per -= 10;// 每次都减少10
				image.compress(Bitmap.CompressFormat.JPEG, per, baos);// 将图片压缩为原来的(100-per)%，把压缩后的数据存放到baos中
			}
			// 回收图片，清理内存
			if (image != null && !image.isRecycled()) {
				image.recycle();
				image = null;
				System.gc();
			}
			return baos.toByteArray();
		}catch(Exception e){
		}
		return null;

	}
	
	public static byte[] decodeToCompressSize(Bitmap image,int maxSizeKB) {
		try{
			ToByteArrayNoCopyByteArrayOutputStream baos = new ToByteArrayNoCopyByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 这里100表示不压缩，将不压缩的数据存放到baos中
			int per = 100;
			while (baos.getCount() / 1024 > maxSizeKB) { // 循环判断如果压缩后图片是否大于500kb,大于继续压缩
				baos.reset();// 重置baos即清空baos
				per -= 10;// 每次都减少10
				image.compress(Bitmap.CompressFormat.JPEG, per, baos);// 将图片压缩为原来的(100-per)%，把压缩后的数据存放到baos中
			}
//			// 回收图片，清理内存
//			if (image != null && !image.isRecycled()) {
//				image.recycle();
//				image = null;
//				System.gc();
//			}
			return baos.toByteArray();
		}catch(Exception e){
		}
		return null;

	}

	// 质量压缩
	public static Bitmap compressImage(Bitmap image, int size) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > size) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	// Drawable to Bitmap
	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

	// Drawable to Bitmap
	public static Bitmap DrawabletoBitmap(Drawable d) {
		BitmapDrawable bd = (BitmapDrawable) d;
		Bitmap bm = bd.getBitmap();
		return bm;
	}

	// Bitmap to Drawable
	public static Drawable BitmaptoDrawable(Bitmap bitmap) {
		return new BitmapDrawable(bitmap);
	}

	// 获得圆角图片的方法
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	// 获得带倒影的图片方法
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);
		return bitmapWithReflection;
	}

	// Bitmap转换成Byte数组：
	public static byte[] bitmapToBytes(Bitmap bitmap) {
		int size = bitmap.getWidth() * bitmap.getHeight() * 4;
		ByteArrayOutputStream out = new ByteArrayOutputStream(size);
		try {
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 重新编码Bitmap
	 * 
	 * @param src
	 *            需要重新编码的Bitmap
	 * 
	 * @param format
	 *            编码后的格式（目前只支持png和jpeg这两种格式）
	 * 
	 * @param quality
	 *            重新生成后的bitmap的质量
	 * 
	 * @return 返回重新生成后的bitmap
	 */
	public static Bitmap codec(Bitmap src, Bitmap.CompressFormat format,
			int quality) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		src.compress(format, quality, os);

		byte[] array = os.toByteArray();
		return BitmapFactory.decodeByteArray(array, 0, array.length);
	}

	// 把一个View的对象转换成bitmap
	public static Bitmap getViewBitmap(View v) {
		v.clearFocus();
		v.setPressed(false);
		// 能画缓存就返回false
		boolean willNotCache = v.willNotCacheDrawing();
		v.setWillNotCacheDrawing(false);
		int color = v.getDrawingCacheBackgroundColor();
		v.setDrawingCacheBackgroundColor(0);
		if (color != 0) {
			v.destroyDrawingCache();
		}
		v.buildDrawingCache();
		Bitmap cacheBitmap = v.getDrawingCache();
		if (cacheBitmap == null) {
			return null;
		}
		Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
		// Restore the view
		v.destroyDrawingCache();
		v.setWillNotCacheDrawing(willNotCache);
		v.setDrawingCacheBackgroundColor(color);
		return bitmap;
	}

	// 图片加阴影
	public static Bitmap createImageWithshadow(Bitmap bitmap, float radius,
			float dx, float dy, int color, boolean bCenter) {
		Paint vPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG);
		Bitmap Rtbitmap = Bitmap.createBitmap(
				bitmap.getWidth() + (int) Math.abs(dx), bitmap.getHeight()
						+ (int) Math.abs(dy), Config.ARGB_8888);
		Canvas canvas = new Canvas(Rtbitmap);

		vPaint.setColor(Color.BLACK);
		vPaint.setShadowLayer(radius, dx, dy, color);
		float x = 0;
		float y = 0;
		if (bCenter) {
			x = Math.abs(dx) / 2;
			y = Math.abs(dy) / 2;
		} else {
			if (dx < 0) {
				x = Math.abs(dx);
			}
			if (dy < 0) {
				y = Math.abs(dy);
			}
		}
		canvas.drawBitmap(bitmap, x, y, vPaint);
		return Rtbitmap;
	}

	// 图片加阴影
	public static Bitmap createBitmapWithshadow(Bitmap bitmap, float radius,
			float dx, float dy, int color, boolean bCenter) {
		Paint vPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG);
		Bitmap Rtbitmap = Bitmap.createBitmap(
				bitmap.getWidth() + (int) Math.abs(dx), bitmap.getHeight()
						+ (int) Math.abs(dy), Config.ARGB_8888);
		Canvas canvas = new Canvas(Rtbitmap);

		vPaint.setColor(color);
		vPaint.setShadowLayer(radius, dx, dy, color);
		float x = 0;
		float y = 0;
		if (bCenter) {
			x = Math.abs(dx) / 2;
			y = Math.abs(dy) / 2;
		} else {
			if (dx < 0) {
				x = Math.abs(dx);
			}
			if (dy < 0) {
				y = Math.abs(dy);
			}
		}
		canvas.drawRect(x, y, bitmap.getWidth(), bitmap.getHeight(), vPaint);
		Paint vPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG);
		canvas.drawBitmap(bitmap, x, y, vPaint2);

		return Rtbitmap;
	}

	// 将字节数组转换为ImageView可调用的Bitmap对象
	public static Bitmap getPicFromBytes(byte[] bytes,
			BitmapFactory.Options opts) {
		if (bytes != null)
			if (opts != null)
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
						opts);
			else
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return null;
	}
}
