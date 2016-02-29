package jp.panot.gw2accountbrowser.util;

import android.net.Uri;
import android.text.format.Time;

import jp.panot.gw2accountbrowser.BuildConfig;

/**
 * Created by panot on 2/22/16.
 */
public class CommonUtils {

  public static String getAccessToken() {
    return BuildConfig.GW2_ACCESS_TOKEN;
  }

  public static int getJulianDay() {
    Time dayTime = new Time();
    dayTime.setToNow();
    return Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
  }

  public static String makeQuestionmarks(int n) {
    if (n <= 0) {
      return "";
    }
    StringBuilder builder = new StringBuilder("?");
    for (int i = 1; i < n; i++) {
      builder.append(",?");
    }
    return builder.toString();
  }

}
