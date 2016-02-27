package jp.panot.gw2accountbrowser;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by panot on 2/23/16.
 */
public class LruBitmapCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {
  private static LruBitmapCache mInstance;

  private LruBitmapCache(int maxSize) {
    super(maxSize);
  }

  public static synchronized LruBitmapCache getInstance(Context ctx) {
    if (mInstance == null) {
      mInstance = new LruBitmapCache(getCacheSize(ctx));
    }
    return mInstance;
  }

  @Override
  protected int sizeOf(String key, Bitmap value) {
    return value.getRowBytes() * value.getHeight();
  }

  @Override
  public Bitmap getBitmap(String url) {
    return get(url);
  }

  @Override
  public void putBitmap(String url, Bitmap bitmap) {
    put(url, bitmap);
  }

  private static int getCacheSize(Context ctx) {
    final DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
    final int screenWidth = displayMetrics.widthPixels;
    final int screenHeight = displayMetrics.heightPixels;
    // 4 bytes per pixel
    final int screenBytes = screenWidth * screenHeight * 4;

    return screenBytes * 3;
  }
}
