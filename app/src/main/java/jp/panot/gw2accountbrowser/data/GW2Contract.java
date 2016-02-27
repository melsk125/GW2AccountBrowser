package jp.panot.gw2accountbrowser.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by panot on 2/24/16.
 */
public class GW2Contract {

  public static final String CONTENT_AUTHORITY = "jp.panot.gw2accountbrowser";

  public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

  public static final String PATH_WORLD = "world";
  public static final String PATH_GUILD = "guild";
  public static final String PATH_CURRENCY = "currency";
  public static final String PATH_ITEM = "item";

  /* Inner class that defines the table contents of the world table. */
  public static final class WorldEntry implements BaseColumns {
    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_WORLD).build();

    public static final String CONTENT_TYPE =
        ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORLD;
    public static final String CONTENT_ITEM_TYPE =
        ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WORLD;

    public static final String TABLE_NAME = "world";

    // World ID from field "id". INTEGER
    public static final String COLUMN_WORLD_ID = "world_id";
    // World name from field "name". TEXT
    public static final String COLUMN_NAME = "name";
    // World population from field "population". TEXT
    // Should be one of the following values: "Low", "Medium", "High", "VeryHigh", "Full".
    public static final String COLUMN_POPULATION = "population";
    // Julian date of latest update. INTEGER
    public static final String COLUMN_LATEST_UPDATE = "latest_update";

    public static final int UPDATE_FREQUENCY = 7;

    public static Uri buildWorldUri(long id) {
      return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    public static Uri buildWorld(long id) {
      return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
    }

    public static long getWorldIdFromUri(Uri uri) {
      return Long.parseLong(uri.getPathSegments().get(1));
    }
  }

  /* Inner class that defines the table contents of the guild table. */
  public static final class GuildEntry implements BaseColumns {
    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_GUILD).build();

    public static final String CONTENT_TYPE =
        ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GUILD;
    public static final String CONTENT_ITEM_TYPE =
        ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GUILD;

    public static final String TABLE_NAME = "guild";

    // Guild ID from field "guild_id". TEXT
    public static final String COLUMN_GUILD_ID = "guild_id";
    // Guild name from field "guild_name". TEXT
    public static final String COLUMN_GUILD_NAME = "guild_name";
    // Guild tag from field "tag". TEXT
    public static final String COLUMN_TAG = "tag";
    // Julian date of latest update. INTEGER
    public static final String COLUMN_LATEST_UPDATE = "latest_update";

    public static final int UPDATE_FREQUENCY = 7;

    // TODO: Add emblem info.

    public static Uri buildGuildUri(long id) {
      return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    public static Uri buildGuild(String id) {
      return CONTENT_URI.buildUpon().appendPath(id).build();
    }

    public static String getGuildIdFromUri(Uri uri) {
      return uri.getPathSegments().get(1);
    }
  }

  /* Inner class that defines the table contents of the currency table. */
  public static final class CurrencyEntry implements BaseColumns {
    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_CURRENCY).build();

    public static final String CONTENT_TYPE =
        ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CURRENCY;
    public static final String CONTENT_ITEM_TYPE =
        ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CURRENCY;

    public static final String TABLE_NAME = "currency";

    // Currency ID from field "id". INTEGER
    public static final String COLUMN_ID = "id";
    // Currency name from field "name". TEXT
    public static final String COLUMN_NAME = "name";
    // Currency description from field "description". TEXT
    public static final String COLUMN_DESCRIPTION = "description";
    // Number that indicates order of this currency in a list of currencies "order". INTEGER
    public static final String COLUMN_ORDER = "cur_order";
    // URL of the icon for this currency "icon". TEXT
    public static final String COLUMN_ICON = "icon";
    // Julian date of latest update. INTEGER
    public static final String COLUMN_LATEST_UPDATE = "latest_update";

    public static final int UPDATE_FREQUENCY = 7;

    public static Uri buildCurrencyUri(long id) {
      return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    public Uri buildCurrency(long id) {
      return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
    }

    public static String getCurrencyIdFromUri(Uri uri) {
      return uri.getPathSegments().get(1);
    }
  }

  /* Inner class that defines the table contents of the item table. */
  public static final class ItemEntry implements BaseColumns {
    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_ITEM).build();

    public static final String CONTENT_TYPE =
        ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEM;
    public static final String CONTENT_ITEM_TYPE =
        ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEM;

    public static final String TABLE_NAME = "item";

    // Item ID from field "id". INTEGER
    public static final String COLUMN_ID = "id";
    // Item name from field "name". TEXT
    public static final String COLUMN_NAME = "name";
    // Item description from field "description". TEXT
    public static final String COLUMN_DESCRIPTION = "description";
    // URL of the icon for this item "icon". TEXT
    public static final String COLUMN_ICON = "icon";
    // JSON info of this item. TEXT
    public static final String COLUMN_JSON = "json";
    // Julian date of latest update. INTEGER
    public static final String COLUMN_LATEST_UPDATE = "latest_update";

    public static final int UPDATE_FREQUENCY = 7;

    public static Uri buildItemUri(long id) {
      return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    public Uri buildItem(long id) {
      return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
    }

    public static String getItemIdFromUri(Uri uri) {
      return uri.getPathSegments().get(1);
    }
  }
}
