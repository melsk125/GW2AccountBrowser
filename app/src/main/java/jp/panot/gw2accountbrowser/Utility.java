package jp.panot.gw2accountbrowser;

import android.text.format.Time;

/**
 * Created by panot on 2/22/16.
 */
public class Utility {
  public static String getAccessToken() {
    return BuildConfig.GW2_ACCESS_TOKEN;
  }

  public static String parseAccess(String access) {
    if (access.equals("PlayForFree")) {
      return "Play for free";
    }
    if (access.equals("GuildWars2")) {
      return "Guild Wars 2";
    }
    if (access.equals("HeartOfThorns")) {
      return "Heart of Thorns";
    }
    return "";
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
