package jp.panot.gw2accountbrowser.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import jp.panot.gw2accountbrowser.data.GW2Contract.CurrencyEntry;
import jp.panot.gw2accountbrowser.data.GW2Contract.GuildEntry;
import jp.panot.gw2accountbrowser.data.GW2Contract.ItemEntry;
import jp.panot.gw2accountbrowser.data.GW2Contract.MaterialEntry;
import jp.panot.gw2accountbrowser.data.GW2Contract.WorldEntry;


/**
 * Created by panot on 2/24/16.
 */
public class GW2Provider extends ContentProvider {
  private static final UriMatcher sUriMatcher = buildUriMatcher();
  private GW2DbHelper mOpenHelper;

  static final int WORLD = 100;
  static final int WORLD_WITH_ID = 101;

  static final int GUILD = 200;
  static final int GUILD_WITH_ID = 201;

  static final int CURRENCY = 300;
  static final int CURRENCY_WITH_ID = 301;

  static final int ITEM = 400;
  static final int ITEM_WITH_ID = 401;

  static final int MATERIAL = 500;
  static final int MATERIAL_WITH_ID = 501;

  static UriMatcher buildUriMatcher() {
    final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    final String authority = GW2Contract.CONTENT_AUTHORITY;

    // jp.panot.gw2accountbrowser/world
    matcher.addURI(authority, GW2Contract.PATH_WORLD, WORLD);
    // jp.panot.gw2accountbrowser/world/1001
    matcher.addURI(authority, GW2Contract.PATH_WORLD + "/#", WORLD_WITH_ID);

    // jp.panot.gw2accountbrowser/guild
    matcher.addURI(authority, GW2Contract.PATH_GUILD, GUILD);
    // jp.panot.gw2accountbrowser/guild/1234-5678-9012-3456-7890
    matcher.addURI(authority, GW2Contract.PATH_GUILD + "/*", GUILD_WITH_ID);

    // jp.panot.gw2accountbrowser/currency
    matcher.addURI(authority, GW2Contract.PATH_CURRENCY, CURRENCY);
    // jp.panot.gw2accountbrowser/currency/1
    matcher.addURI(authority, GW2Contract.PATH_CURRENCY + "/#", CURRENCY_WITH_ID);

    // jp.panot.gw2accountbrowser/item
    matcher.addURI(authority, GW2Contract.PATH_ITEM, ITEM);
    // jp.panot.gw2accountbrowser/item/1234
    matcher.addURI(authority, GW2Contract.PATH_ITEM + "/#", ITEM_WITH_ID);

    // jp.panot.gw2accountbrowser/material
    matcher.addURI(authority, GW2Contract.PATH_MATERIAL, MATERIAL);
    // jp.panot.gw2accountbrowser/material/5
    matcher.addURI(authority, GW2Contract.PATH_MATERIAL + "/#", MATERIAL_WITH_ID);

    return matcher;
  }

  @Override
  public boolean onCreate() {
    mOpenHelper = new GW2DbHelper(getContext());
    return true;
  }

  @Nullable
  @Override
  public String getType(Uri uri) {
    final int match = sUriMatcher.match(uri);
    switch (match) {
      case WORLD:
        return WorldEntry.CONTENT_TYPE;
      case WORLD_WITH_ID:
        return WorldEntry.CONTENT_ITEM_TYPE;
      case GUILD:
        return GuildEntry.CONTENT_TYPE;
      case GUILD_WITH_ID:
        return GuildEntry.CONTENT_ITEM_TYPE;
      case CURRENCY:
        return CurrencyEntry.CONTENT_TYPE;
      case CURRENCY_WITH_ID:
        return CurrencyEntry.CONTENT_ITEM_TYPE;
      case ITEM:
        return ItemEntry.CONTENT_TYPE;
      case ITEM_WITH_ID:
        return ItemEntry.CONTENT_ITEM_TYPE;
      case MATERIAL:
        return MaterialEntry.CONTENT_TYPE;
      case MATERIAL_WITH_ID:
        return MaterialEntry.CONTENT_ITEM_TYPE;
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
  }

  @Nullable
  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                      String sortOrder) {
    final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
    final int match = sUriMatcher.match(uri);
    Cursor c;
    switch (match) {
      // "world"
      case WORLD: {
        c = db.query(WorldEntry.TABLE_NAME, projection, selection, selectionArgs, null, null,
            sortOrder);
        break;
      }
      // "world/1001"
      case WORLD_WITH_ID: {
        long worldId = WorldEntry.getWorldIdFromUri(uri);
        String sel = WorldEntry.COLUMN_WORLD_ID + " = ? ";
        String[] selArgs = new String[]{String.valueOf(worldId)};
        c = db.query(WorldEntry.TABLE_NAME, projection, sel, selArgs, null, null, sortOrder);
        break;
      }
      // "guild"
      case GUILD: {
        c = db.query(GuildEntry.TABLE_NAME, projection, selection, selectionArgs, null, null,
            sortOrder);
        break;
      }
      // "guild/1234-5678-9012-3456"
      case GUILD_WITH_ID: {
        String guildId = GuildEntry.getGuildIdFromUri(uri);
        String sel = GuildEntry.COLUMN_GUILD_ID + " = ? ";
        String[] selArgs = new String[]{String.valueOf(guildId)};
        c = db.query(GuildEntry.TABLE_NAME, projection, sel, selArgs, null, null, sortOrder);
        break;
      }
      // "guild"
      case CURRENCY: {
        c = db.query(CurrencyEntry.TABLE_NAME, projection, selection, selectionArgs, null, null,
            sortOrder);
        break;
      }
      // "guild/1234-5678-9012-3456"
      case CURRENCY_WITH_ID: {
        String currencyId = CurrencyEntry.getCurrencyIdFromUri(uri);
        String sel = CurrencyEntry.COLUMN_ID + " = ? ";
        String[] selArgs = new String[]{String.valueOf(currencyId)};
        c = db.query(CurrencyEntry.TABLE_NAME, projection, sel, selArgs, null, null, sortOrder);
        break;
      }
      case ITEM: {
        c = db.query(ItemEntry.TABLE_NAME, projection, selection, selectionArgs, null, null,
            sortOrder);
        break;
      }
      case ITEM_WITH_ID: {
        String itemId = ItemEntry.getItemIdFromUri(uri);
        String sel = ItemEntry.COLUMN_ID + " = ? ";
        String[] selArgs = new String[]{String.valueOf(itemId)};
        c = db.query(ItemEntry.TABLE_NAME, projection, sel, selArgs, null, null, sortOrder);
        break;
      }
      case MATERIAL: {
        c = db.query(MaterialEntry.TABLE_NAME, projection, selection, selectionArgs, null, null,
            sortOrder);
        break;
      }
      case MATERIAL_WITH_ID: {
        String itemId = MaterialEntry.getMaterialIdFromUri(uri);
        String sel = MaterialEntry.COLUMN_ID + " = ? ";
        String[] selArgs = new String[]{String.valueOf(itemId)};
        c = db.query(MaterialEntry.TABLE_NAME, projection, sel, selArgs, null, null, sortOrder);
        break;
      }
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
    c.setNotificationUri(getContext().getContentResolver(), uri);
    return c;
  }

  @Nullable
  @Override
  public Uri insert(Uri uri, ContentValues values) {
    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);
    Uri returnUri;
    switch (match) {
      case WORLD: {
        long _id = db.insertWithOnConflict(WorldEntry.TABLE_NAME, null, values,
            SQLiteDatabase.CONFLICT_REPLACE);
        if (_id > 0) {
          returnUri = WorldEntry.buildWorldUri(_id);
        } else {
          throw new SQLException("Failed to insert row into " + uri);
        }
        break;
      }
      case GUILD: {
        long _id = db.insertWithOnConflict(GuildEntry.TABLE_NAME, null, values,
            SQLiteDatabase.CONFLICT_REPLACE);
        if (_id > 0) {
          returnUri = GuildEntry.buildGuildUri(_id);
        } else {
          throw new SQLException("Failed to insert row into " + uri);
        }
        break;
      }
      case CURRENCY: {
        long _id = db.insertWithOnConflict(CurrencyEntry.TABLE_NAME, null, values,
            SQLiteDatabase.CONFLICT_REPLACE);
        if (_id > 0) {
          returnUri = CurrencyEntry.buildCurrencyUri(_id);
        } else {
          throw new SQLException("Failed to insert row into " + uri);
        }
        break;
      }
      case ITEM: {
        long _id = db.insertWithOnConflict(ItemEntry.TABLE_NAME, null, values,
            SQLiteDatabase.CONFLICT_REPLACE);
        if (_id > 0) {
          returnUri = ItemEntry.buildItemUri(_id);
        } else {
          throw new SQLException("Failed to insert row into " + uri);
        }
        break;
      }
      case MATERIAL: {
        long _id = db.insertWithOnConflict(MaterialEntry.TABLE_NAME, null, values,
            SQLiteDatabase.CONFLICT_REPLACE);
        if (_id > 0) {
          returnUri = MaterialEntry.buildMaterialUri(_id);
        } else {
          throw new SQLException("Failed to insert row into " + uri);
        }
        break;
      }
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return returnUri;
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);
    int rowsDeleted;
    if (selection == null) selection = "1";
    switch (match) {
      case WORLD:
        rowsDeleted = db.delete(WorldEntry.TABLE_NAME, selection, selectionArgs);
        break;
      case GUILD:
        rowsDeleted = db.delete(GuildEntry.TABLE_NAME, selection, selectionArgs);
        break;
      case CURRENCY:
        rowsDeleted = db.delete(CurrencyEntry.TABLE_NAME, selection, selectionArgs);
        break;
      case ITEM:
        rowsDeleted = db.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
        break;
      case MATERIAL:
        rowsDeleted = db.delete(MaterialEntry.TABLE_NAME, selection, selectionArgs);
        break;
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
    if (rowsDeleted != 0) {
      getContext().getContentResolver().notifyChange(uri, null);
    }
    return rowsDeleted;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);
    int rowsUpdated;
    switch (match) {
      case WORLD:
        rowsUpdated = db.updateWithOnConflict(WorldEntry.TABLE_NAME, values,
            selection, selectionArgs, SQLiteDatabase.CONFLICT_REPLACE);
        break;
      case GUILD:
        rowsUpdated = db.updateWithOnConflict(GuildEntry.TABLE_NAME, values,
            selection, selectionArgs, SQLiteDatabase.CONFLICT_REPLACE);
        break;
      case CURRENCY:
        rowsUpdated = db.updateWithOnConflict(CurrencyEntry.TABLE_NAME, values,
            selection, selectionArgs, SQLiteDatabase.CONFLICT_REPLACE);
        break;
      case ITEM:
        rowsUpdated = db.updateWithOnConflict(ItemEntry.TABLE_NAME, values,
            selection, selectionArgs, SQLiteDatabase.CONFLICT_REPLACE);
        break;
      case MATERIAL:
        rowsUpdated = db.updateWithOnConflict(MaterialEntry.TABLE_NAME, values,
            selection, selectionArgs, SQLiteDatabase.CONFLICT_REPLACE);
        break;
      default:
        throw new UnsupportedOperationException("Unknown Uri: " + uri);
    }
    if (rowsUpdated != 0) {
      getContext().getContentResolver().notifyChange(uri, null);
    }
    return rowsUpdated;
  }

  @Override
  public int bulkInsert(Uri uri, ContentValues[] values) {
    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    final int match = sUriMatcher.match(uri);
    int returnCount = 0;
    switch (match) {
      case WORLD: {
        db.beginTransaction();
        try {
          for (ContentValues value : values) {
            long _id = db.insertWithOnConflict(WorldEntry.TABLE_NAME, null, value,
                SQLiteDatabase.CONFLICT_REPLACE);
            if (_id > 0) {
              returnCount++;
            }
          }
          db.setTransactionSuccessful();
        } finally {
          db.endTransaction();
        }
        break;
      }
      case GUILD: {
        db.beginTransaction();
        try {
          for (ContentValues value : values) {
            long _id = db.insertWithOnConflict(GuildEntry.TABLE_NAME, null, value,
                SQLiteDatabase.CONFLICT_REPLACE);
            if (_id > 0) {
              returnCount++;
            }
          }
          db.setTransactionSuccessful();
        } finally {
          db.endTransaction();
        }
        break;
      }
      case CURRENCY: {
        db.beginTransaction();
        try {
          for (ContentValues value : values) {
            long _id = db.insertWithOnConflict(CurrencyEntry.TABLE_NAME, null, value,
                SQLiteDatabase.CONFLICT_REPLACE);
            if (_id > 0) {
              returnCount++;
            }
          }
          db.setTransactionSuccessful();
        } finally {
          db.endTransaction();
        }
        break;
      }
      case ITEM: {
        db.beginTransaction();
        try {
          for (ContentValues value : values) {
            long _id = db.insertWithOnConflict(ItemEntry.TABLE_NAME, null, value,
                SQLiteDatabase.CONFLICT_REPLACE);
            if (_id > 0) {
              returnCount++;
            }
          }
          db.setTransactionSuccessful();
        } finally {
          db.endTransaction();
        }
        break;
      }
      case MATERIAL: {
        db.beginTransaction();
        try {
          for (ContentValues value : values) {
            long _id = db.insertWithOnConflict(MaterialEntry.TABLE_NAME, null, value,
                SQLiteDatabase.CONFLICT_REPLACE);
            if (_id > 0) {
              returnCount++;
            }
          }
          db.setTransactionSuccessful();
        } finally {
          db.endTransaction();
        }
        break;
      }
      default:
        return super.bulkInsert(uri, values);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return returnCount;
  }
}
