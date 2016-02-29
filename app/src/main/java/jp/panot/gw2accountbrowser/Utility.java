package jp.panot.gw2accountbrowser;

import android.net.Uri;
import android.text.format.Time;

/**
 * Created by panot on 2/22/16.
 */
public class Utility {
  private static final String API_BASE_URL = "https://api.guildwars2.com/";
  private static final String API_V2_BASE_URL = API_BASE_URL + "v2/";
  private static final String API_V1_BASE_URL = API_BASE_URL + "v1/";

  private static final String API_ACCOUNT_NODE = "account";
  private static final String API_WORLDS_NODE = "worlds";
  private static final String API_GUILD_DETAILS_NODE = "guild_details.json";
  private static final String API_CHARACTER_NODE = "characters";
  private static final String API_CURRENCY_NODE = "currencies";
  private static final String API_WALLET_NODE = "wallet";
  private static final String API_BANK_NODE = "bank";
  private static final String API_ITEM_NODE = "items";

  private static final String ACCESS_TOKEN_PARAM = "access_token";
  private static final String IDS_PARAM = "ids";
  private static final String GUILD_ID_PARAM = "guild_id";
  private static final String PAGINATION_PARAM = "page";

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

  public static String getAccountURL(String token) {
    Uri uri = Uri.parse(API_V2_BASE_URL).buildUpon()
        .appendPath(API_ACCOUNT_NODE)
        .appendQueryParameter(ACCESS_TOKEN_PARAM, token)
        .build();
    return uri.toString();
  }

  public static String getWorldsURL(int[] ids) {
    StringBuilder idsBuilder = new StringBuilder();
    if (ids.length > 0) {
      idsBuilder.append(ids[0]);
    }
    for (int i = 1; i < ids.length; i++) {
      idsBuilder.append(",").append(ids[i]);
    }
    String idsStr = idsBuilder.toString();
    Uri uri = Uri.parse(API_V2_BASE_URL).buildUpon()
        .appendPath(API_WORLDS_NODE)
        .appendQueryParameter(IDS_PARAM, idsStr)
        .build();
    return uri.toString();
  }

  public static String getWorldsURL(int id) {
    return getWorldsURL(new int[]{id});
  }

  public static String getGuildInfoURL(String id) {
    Uri uri = Uri.parse(API_V1_BASE_URL).buildUpon()
        .appendPath(API_GUILD_DETAILS_NODE)
        .appendQueryParameter(GUILD_ID_PARAM, id)
        .build();
    return uri.toString();
  }

  public static String getAllCharacterName(String token) {
    Uri uri = Uri.parse(API_V2_BASE_URL).buildUpon()
        .appendPath(API_CHARACTER_NODE)
        .appendQueryParameter(ACCESS_TOKEN_PARAM, token)
        .build();
    return uri.toString();
  }

  public static String getAllCharactersInfoURL(String token) {
    Uri uri = Uri.parse(API_V2_BASE_URL).buildUpon()
        .appendPath(API_CHARACTER_NODE)
        .appendQueryParameter(PAGINATION_PARAM, "0")
        .appendQueryParameter(ACCESS_TOKEN_PARAM, token)
        .build();
    return uri.toString();
  }

  public static String getCharacterInfoURL(String token, String name) {
    Uri uri = Uri.parse(API_V2_BASE_URL).buildUpon()
        .appendPath(API_CHARACTER_NODE)
        .appendEncodedPath(name)
        .appendQueryParameter(ACCESS_TOKEN_PARAM, token)
        .build();
    return uri.toString();
  }

  public static String getAllCurrencyInfo() {
    Uri uri = Uri.parse(API_V2_BASE_URL).buildUpon()
        .appendPath(API_CURRENCY_NODE)
        .appendQueryParameter(PAGINATION_PARAM, "0")
        .build();
    return uri.toString();
  }

  public static String getWallet(String token) {
    Uri uri = Uri.parse(API_V2_BASE_URL).buildUpon()
        .appendPath(API_ACCOUNT_NODE)
        .appendPath(API_WALLET_NODE)
        .appendQueryParameter(ACCESS_TOKEN_PARAM, token)
        .build();
    return uri.toString();
  }

  public static String getItemURL(int[] ids) {
    StringBuilder builder = new StringBuilder();
    if (ids.length > 0) {
      builder.append(ids[0]);
    }
    for (int i = 1; i < ids.length; i++) {
      builder.append(",").append(ids[i]);
    }
    String idsStr = builder.toString();
    Uri uri = Uri.parse(API_V2_BASE_URL).buildUpon()
        .appendPath(API_ITEM_NODE)
        .appendQueryParameter(IDS_PARAM, idsStr)
        .build();
    return uri.toString();
  }

  public static String getBank(String token) {
    Uri uri = Uri.parse(API_V2_BASE_URL).buildUpon()
        .appendPath(API_ACCOUNT_NODE)
        .appendPath(API_BANK_NODE)
        .appendQueryParameter(ACCESS_TOKEN_PARAM, token)
        .build();
    return uri.toString();
  }
}
