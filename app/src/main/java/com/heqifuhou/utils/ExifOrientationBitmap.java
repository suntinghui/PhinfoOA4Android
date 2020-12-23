package com.heqifuhou.utils;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

public class ExifOrientationBitmap {
	private ExifOrientationBitmap()
	{
	}
	//取得转正的图片
	public static Bitmap getBitmap(String filepath) {
		try{
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = false;
			Bitmap bit = BitmapFactory.decodeFile(filepath, options);
			int degree = getExifOrientation(filepath);
			Bitmap bitRt =  rotateBitmap(bit,degree);
			if(bitRt != bit){
				bit.recycle();
			}
		}catch(Exception e){	
		}
		return null;
	}
	
	///////////////////////////////////////////////////////
	public static int getExifOrientation(String filepath) {
		int degree = 0;
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(filepath);
		} catch (IOException ex) {
		}
		if (exif != null) {
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, -1);
			if (orientation != -1) {
				// We only recognize a subset of orientation tag values.
				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
				}

			}
		}
		return degree;
	}

	public static Bitmap rotateBitmap(Bitmap b, int degrees) {
	        if (degrees != 0 && b != null) {
	            Matrix m = new Matrix();
	            m.setRotate(degrees,
	                    (float) b.getWidth() / 2, (float) b.getHeight() / 2);
	            try {
	                Bitmap b2 = Bitmap.createBitmap(
	                        b, 0, 0, b.getWidth(), b.getHeight(), m, true);
	                return b2;
	            } catch (OutOfMemoryError ex) {
	            	ex.printStackTrace();
	            }
	        }
	        return b;
	    }
	
	public static Bitmap postScaleV(Bitmap b) {
        if (b != null) {
            Matrix m = new Matrix();
            m.postScale(1, -1);   //镜像垂直翻转
            try{
                return Bitmap.createBitmap(
                        b, 0, 0, b.getWidth(), b.getHeight(), m, true);
            } catch (OutOfMemoryError ex) {
            	ex.printStackTrace();// We have no memory to rotate. Return the original bitmap.
            }
        }
        return b;
    }
	
	public static Bitmap postScaleH(Bitmap b) {
        if (b != null) {
            Matrix m = new Matrix();
            m.postScale(-1, 1);   //镜像水平翻转
            try{
                return Bitmap.createBitmap(
                        b, 0, 0, b.getWidth(), b.getHeight(), m, true);
            } catch (OutOfMemoryError ex) {
            	ex.printStackTrace();// We have no memory to rotate. Return the original bitmap.
            }
        }
        return b;
    }
	

	
}
