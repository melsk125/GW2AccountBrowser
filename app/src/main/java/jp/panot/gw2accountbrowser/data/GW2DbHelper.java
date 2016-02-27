package jp.panot.gw2accountbrowser.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import jp.panot.gw2accountbrowser.data.GW2Contract.CurrencyEntry;
import jp.panot.gw2accountbrowser.data.GW2Contract.GuildEntry;
import jp.panot.gw2accountbrowser.data.GW2Contract.WorldEntry;
import jp.panot.gw2accountbrowser.data.GW2Contract.ItemEntry;

/**
 * Created by panot on 2/24/16.
 */
public class GW2DbHelper extends SQLiteOpenHelper {
  private static final int DATABASE_VERSION = 1;

  static final String DATABASE_NAME = "gw2.db";

  public GW2DbHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    final String SQL_CREATE_WORLD_TABLE = "CREATE TABLE " + WorldEntry.TABLE_NAME + " (" +
        WorldEntry._ID + " INTEGER PRIMARY KEY, " +
        WorldEntry.COLUMN_LATEST_UPDATE + " INTEGER NOT NULL, " +
        WorldEntry.COLUMN_WORLD_ID + " INTEGER UNIQUE NOT NULL, " +
        WorldEntry.COLUMN_NAME + " TEXT NOT NULL, " +
        WorldEntry.COLUMN_POPULATION + " TEXT NOT NULL " +
        " );";

    final String SQL_CREATE_GUILD_TABLE = "CREATE TABLE " + GuildEntry.TABLE_NAME + " (" +
        GuildEntry._ID + " INTEGER PRIMARY KEY, " +
        GuildEntry.COLUMN_LATEST_UPDATE + " INTEGER NOT NULL, " +
        GuildEntry.COLUMN_GUILD_ID + " TEXT UNIQUE NOT NULL, " +
        GuildEntry.COLUMN_GUILD_NAME + " TEXT NOT NULL, " +
        GuildEntry.COLUMN_TAG + " TEXT NOT NULL " +
        " );";

    final String SQL_CREATE_CURRENCY_TABLE = "CREATE TABLE " + CurrencyEntry.TABLE_NAME + " (" +
        CurrencyEntry._ID + " INTEGER PRIMARY KEY, " +
        CurrencyEntry.COLUMN_LATEST_UPDATE + " INTEGER NOT NULL, " +
        CurrencyEntry.COLUMN_ID + " INTEGER UNIQUE NOT NULL, " +
        CurrencyEntry.COLUMN_NAME + " TEXT NOT NULL, " +
        CurrencyEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
        CurrencyEntry.COLUMN_ORDER + " INTEGER NOT NULL, " +
        CurrencyEntry.COLUMN_ICON + " TEXT NOT NULL " +
        " );";

    final String SQL_CREATE_ITEM_TABLE = "CREATE TABLE " + ItemEntry.TABLE_NAME + " (" +
        ItemEntry._ID + " INTEGER PRIMARY KEY, " +
        ItemEntry.COLUMN_LATEST_UPDATE + " INTEGER NOT NULL, " +
        ItemEntry.COLUMN_ID + " INTEGER UNIQUE NOT NULL, " +
        ItemEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
        ItemEntry.COLUMN_NAME + " TEXT NOT NULL, " +
        ItemEntry.COLUMN_ICON + " TEXT NOT NULL, " +
        ItemEntry.COLUMN_JSON + " TEXT NOT NULL " +
        " );";

    db.execSQL(SQL_CREATE_WORLD_TABLE);
    db.execSQL(SQL_CREATE_GUILD_TABLE);
    db.execSQL(SQL_CREATE_CURRENCY_TABLE);
    db.execSQL(SQL_CREATE_ITEM_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + WorldEntry.TABLE_NAME);
    db.execSQL("DROP TABLE IF EXISTS " + GuildEntry.TABLE_NAME);
    db.execSQL("DROP TABLE IF EXISTS " + CurrencyEntry.TABLE_NAME);
    db.execSQL("DROP TABLE IF EXISTS " + ItemEntry.TABLE_NAME);

    onCreate(db);
  }
}
