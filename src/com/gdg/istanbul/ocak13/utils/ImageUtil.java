package com.gdg.istanbul.ocak13.utils;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class ImageUtil {

	public static byte[] codec(Bitmap src, Bitmap.CompressFormat format,
			int quality) {
		boolean resize = false;
		int resizeValueH = 1;
		int resizeValueW = 1;
		if (src.getHeight() > 1024) {
			resizeValueH = (int) Math.ceil(src.getHeight() / 1024f);
			resize = true;
		}
		if (src.getWidth() > 1024) {
			resizeValueW = (int) Math.ceil(src.getWidth() / 1024f);
			resize = true;
		}
		if (resize) {
			if (resizeValueH < resizeValueW) {
				resizeValueH = resizeValueW;
			}
			src = getResizedBitmap(src, src.getHeight() / resizeValueH,
					src.getWidth() / resizeValueH);
		}
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		src.compress(format, quality, os);
		return os.toByteArray();
	}

	public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, false);
		return resizedBitmap;
	}

}
