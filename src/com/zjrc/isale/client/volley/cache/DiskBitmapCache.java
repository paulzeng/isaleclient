package com.zjrc.isale.client.volley.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageLoader;
import com.zjrc.isale.client.util.BitmapUtil;

import java.io.File;

/**
 * 项目名:销售管家
 * 功能描述：
 * 创建者:贺彬
 * 创建时间: 2015-04-09
 * 版本：V2.1
 */
	/*
	 * Extends from DisckBasedCache --> Utility from volley toolbox.
	 * Also implements ImageCache, so that we can pass this custom implementation
	 * to ImageLoader.
	 */
public  class DiskBitmapCache extends DiskBasedCache implements ImageLoader.ImageCache {

    public DiskBitmapCache(File rootDirectory, int maxCacheSizeInBytes) {
        super(rootDirectory, maxCacheSizeInBytes);
    }

    public DiskBitmapCache(File cacheDir) {
        super(cacheDir);
    }

    public Bitmap getBitmap(String url) {
        final Entry requestedItem = get(url);

        if (requestedItem == null)
            return null;

        return BitmapFactory.decodeByteArray(requestedItem.data, 0, requestedItem.data.length);
    }

    public void putBitmap(String url, Bitmap bitmap) {

        final Entry entry = new Entry();

/*			//Down size the bitmap.If not done, OutofMemoryError occurs while decoding large bitmaps.
 			// If w & h is set during image request ( using ImageLoader ) then this is not required.
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Bitmap downSized = BitmapUtil.downSizeBitmap(bitmap, 50);

			downSized.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] data = baos.toByteArray();
	        entry.data = data ; */

        entry.data = BitmapUtil.convertBitmapToBytes(bitmap) ;
        put(url, entry);
    }

    @Override
    public File getFileForKey(String key) {
        return super.getFileForKey(key);
    }

}