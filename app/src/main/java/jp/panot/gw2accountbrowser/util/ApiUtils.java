package jp.panot.gw2accountbrowser.util;

import android.net.Uri;

/**
 * Created by panot on 2/29/16.
 */
public class ApiUtils {
  private static final String BASE_URL = "https://api.guildwars2.com/";

  private static final String V1_BASE_URL = BASE_URL + "v1/";
  private static final String V2_BASE_URL = BASE_URL + "v2/";

  private static final String ACCOUNT_NODE = "account";
  private static final String WALLET_NODE = "wallet";
  private static final String BANK_NODE = "bank";
  private static final String CHARACTER_NODE = "characters";

  private static final String WORLDS_NODE = "worlds";
  private static final String GUILD_DETAILS_NODE = "guild_details.json";
  private static final String CURRENCY_NODE = "currencies";
  private static final String ITEM_NODE = "items";

  private static final String ACCESS_TOKEN_PARAM = "access_token";
  private static final String IDS_PARAM = "ids";
  private static final String GUILD_ID_PARAM = "guild_id";
  private static final String PAGINATION_PARAM = "page";

  /*
   * Account API info.
   */

  public static final class Account {
    public static final String JSON_NAME = "name";
    public static final String JSON_WORLD = "world";
    public static final String JSON_GUILDS = "guilds";
    public static final String JSON_CREATED = "created";

    public static final Uri NODE_URI = Uri.parse(V2_BASE_URL).buildUpon()
        .appendPath(ACCOUNT_NODE)
        .build();

    public static String url(String token) {
      Uri uri = NODE_URI.buildUpon()
          .appendQueryParameter(ACCESS_TOKEN_PARAM, token)
          .build();
      return uri.toString();
    }
  }

  public static final class Wallet {
    public static final String JSON_ID = "id";
    public static final String JSON_VALUE = "value";

    public static final Uri NODE_URI = Account.NODE_URI.buildUpon()
        .appendPath(WALLET_NODE)
        .build();

    public static String url(String token) {
      Uri uri = NODE_URI.buildUpon()
          .appendQueryParameter(ACCESS_TOKEN_PARAM, token)
          .build();
      return uri.toString();
    }
  }

  public static final class Bank {
    public static final String JSON_ID = "id";
    public static final String JSON_COUNT = "count";

    public static final Uri NODE_URI = Account.NODE_URI.buildUpon()
        .appendPath(BANK_NODE)
        .build();

    public static String url(String token) {
      Uri uri = NODE_URI.buildUpon()
          .appendQueryParameter(ACCESS_TOKEN_PARAM, token)
          .build();
      return uri.toString();
    }
  }

  public static final class Characters {
    public static final Uri NODE_URI = Uri.parse(V2_BASE_URL).buildUpon()
        .appendPath(CHARACTER_NODE)
        .build();

    public static String allNamesUrl(String token) {
      Uri uri = NODE_URI.buildUpon()
          .appendQueryParameter(ACCESS_TOKEN_PARAM, token)
          .build();
      return uri.toString();
    }

    public static String allInfoUrl(String token) {
      Uri uri = NODE_URI.buildUpon()
          .appendQueryParameter(PAGINATION_PARAM, "0")
          .appendQueryParameter(ACCESS_TOKEN_PARAM, token)
          .build();
      return uri.toString();
    }

    public static String infoUrl(String token, String name) {
      Uri uri = NODE_URI.buildUpon()
          .appendEncodedPath(name)
          .appendQueryParameter(ACCESS_TOKEN_PARAM, token)
          .build();
      return uri.toString();
    }
  }

  /*
   * General asset info.
   */

  public static final class Worlds {
    public static final String JSON_ID = "id";
    public static final String JSON_NAME = "name";
    public static final String JSON_POPULATION = "population";

    public static final Uri NODE_URI = Account.NODE_URI.buildUpon()
        .appendPath(WORLDS_NODE)
        .build();

    public static String url(int[] ids) {
      StringBuilder idsBuilder = new StringBuilder();
      if (ids.length > 0) {
        idsBuilder.append(ids[0]);
      }
      for (int i = 1; i < ids.length; i++) {
        idsBuilder.append(",").append(ids[i]);
      }
      String idsStr = idsBuilder.toString();
      Uri uri = NODE_URI.buildUpon()
          .appendQueryParameter(IDS_PARAM, idsStr)
          .build();
      return uri.toString();
    }

    public static String url(int id) {
      return url(new int[]{id});
    }
  }

  public static final class Guild {
    public static final String JSON_ID = "guild_id";
    public static final String JSON_NAME = "guild_name";
    public static final String JSON_TAG = "tag";

    public static String url(String id) {
      Uri uri = Uri.parse(V1_BASE_URL).buildUpon()
          .appendPath(GUILD_DETAILS_NODE)
          .appendQueryParameter(GUILD_ID_PARAM, id)
          .build();
      return uri.toString();
    }
  }


  public static final class Currencies {
    public static final String JSON_ID = "id";
    public static final String JSON_NAME = "name";
    public static final String JSON_DESCRIPTION = "description";
    public static final String JSON_ORDER = "order";
    public static final String JSON_ICON = "icon";

    public static final Uri NODE_URI = Uri.parse(V2_BASE_URL).buildUpon()
        .appendPath(CURRENCY_NODE)
        .build();

    public static String url() {
      Uri uri = NODE_URI.buildUpon()
          .appendQueryParameter(PAGINATION_PARAM, "0")
          .build();
      return uri.toString();
    }
  }

  public static final class Items {
    public static final String JSON_ID = "id";
    public static final String JSON_NAME = "name";
    public static final String JSON_DESCRIPTION = "description";
    public static final String JSON_ICON = "icon";

    public static final Uri NODE_URI = Uri.parse(V2_BASE_URL).buildUpon()
        .appendPath(ITEM_NODE)
        .build();

    public static String url(int[] ids) {
      StringBuilder builder = new StringBuilder();
      if (ids.length > 0) {
        builder.append(ids[0]);
      }
      for (int i = 1; i < ids.length; i++) {
        builder.append(",").append(ids[i]);
      }
      String idsStr = builder.toString();
      Uri uri = NODE_URI.buildUpon()
          .appendQueryParameter(IDS_PARAM, idsStr)
          .build();
      return uri.toString();
    }
  }

}
