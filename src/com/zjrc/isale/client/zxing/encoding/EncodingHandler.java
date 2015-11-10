package com.zjrc.isale.client.zxing.encoding;

import java.util.Hashtable;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public final class EncodingHandler {
	private static final int BLACK = 0xff000000;

	public static Bitmap createQRCode(String str,int widthAndHeight) throws WriterException {
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();  
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); 
        hints.put(EncodeHintType.ERROR_CORRECTION,  ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);
		BitMatrix matrix = new MultiFormatWriter().encode(str,
				BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight, hints);
		
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		
		int yStart = 0;
		int yEnd = 0;
		int xStart = 0;
		int xEnd = 0;
		boolean bNext = true;
		for (int y = 0; bNext && y < height; y++) {
			for (int x = 0; bNext && x < width; x++) {
				if (matrix.get(x, y)) {
					xStart = x;
					yStart = y;
					bNext = false;
					break;
				}
			}
		}
		for (int x = width - 1; x >= 0; x--) {
			if (matrix.get(x, yStart)) {
				xEnd = x;
				break;
			}
		}

		for (int y = height - 1; y >= 0; y--) {
			if (matrix.get(xStart, y)) {
				yEnd = y;
				break;
			}
		}
		
		width = xEnd - xStart;
		height = yEnd - yStart;
		
		int[] pixels = new int[width * height];
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x + xStart, y + yStart)) {
					pixels[y * width + x] = BLACK;
				}
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}
}
