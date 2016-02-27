package jp.panot.gw2accountbrowser;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

import jp.panot.gw2accountbrowser.data.GW2Contract;

/**
 * Created by panot on 2/22/16.
 */
public class AccountBrowserFetch {
  private static final String LOG_TAG = AccountBrowserFetch.class.getSimpleName();

  private static final String API_BASE_URL = "https://api.guildwars2.com/";
  private static final String API_V1_BASE_URL = API_BASE_URL + "v1/";
  private static final String API_V2_BASE_URL = API_BASE_URL + "v2/";

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

  private static AccountBrowserFetch mInstance;
  private RequestQueue mRequestQueue;
  private ImageLoader mImageLoader;
  private final Context mContext;

  private AccountBrowserFetch(Context context) {
    mContext = context;
    mRequestQueue = getRequestQueue();
    mImageLoader = new ImageLoader(mRequestQueue, LruBitmapCache.getInstance(context));
  }

  public static synchronized AccountBrowserFetch getInstance(Context context) {
    if (mInstance == null) {
      mInstance = new AccountBrowserFetch(context);
    }
    return mInstance;
  }

  public RequestQueue getRequestQueue() {
    if (mRequestQueue == null) {
      mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
    }
    return mRequestQueue;
  }

  public <T> void addToRequestQueue(Request<T> req) {
    getRequestQueue().add(req);
  }

  public ImageLoader getImageLoader() {
    return mImageLoader;
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

  private static final Response.ErrorListener errorListener = new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
      Log.e(LOG_TAG, "Volley error", error);
      error.printStackTrace();
    }
  };

  public void fetchJsonObject(String url, Response.Listener<JSONObject> successListener,
                              Response.ErrorListener errorListener) {
    addToRequestQueue(
        new JsonObjectRequest(Request.Method.GET, url, null, successListener, errorListener));
  }

  public void fetchJsonArray(String url, Response.Listener<JSONArray> successListener,
                             Response.ErrorListener errorListener) {
    addToRequestQueue(
        new JsonArrayRequest(Request.Method.GET, url, null, successListener, errorListener));
  }

  private int bulkInsert(Uri uri, Vector<ContentValues> cVVector) {
    if (cVVector.size() <= 0) {
      return 0;
    }
    ContentValues[] cvArray = new ContentValues[cVVector.size()];
    cVVector.toArray(cvArray);
    return mContext.getContentResolver().bulkInsert(uri, cvArray);
  }

  // TODO: Make separate update and insert, so that we don't delete all rows then insert.

  public void updateWorlds(int[] ids) {
    Log.v(LOG_TAG, "updateWorlds");
    String url = getWorldsURL(ids);

    fetchJsonArray(url, new Response.Listener<JSONArray>() {
      @Override
      public void onResponse(JSONArray response) {
        try {
          Vector<ContentValues> cVVector = new Vector<ContentValues>(response.length());

          for (int i = 0; i < response.length(); i++) {
            JSONObject world = response.getJSONObject(i);
            int id = world.getInt("id");
            String name = world.getString("name");
            String population = world.getString("population");

            ContentValues worldValues = new ContentValues();
            worldValues.put(GW2Contract.WorldEntry.COLUMN_WORLD_ID, id);
            worldValues.put(GW2Contract.WorldEntry.COLUMN_NAME, name);
            worldValues.put(GW2Contract.WorldEntry.COLUMN_POPULATION, population);
            worldValues.put(GW2Contract.WorldEntry.COLUMN_LATEST_UPDATE, Utility.getJulianDay());

            cVVector.add(worldValues);
          }

          int inserted = bulkInsert(GW2Contract.WorldEntry.CONTENT_URI, cVVector);
          Log.d(LOG_TAG, "updateWorlds complete. " + inserted + " inserted");
        } catch (JSONException e) {
          Log.e(LOG_TAG, "JSONException", e);
          e.printStackTrace();
        }
      }
    }, errorListener);
  }

  public void updateGuilds(String[] ids) {
    Log.v(LOG_TAG, "updateGuilds");
    for (int i = 0; i < ids.length; i++) {
      String id = ids[i];
      String url = getGuildInfoURL(id);

      fetchJsonObject(url, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
          try {
            String guildId = response.getString("guild_id");
            String guildName = response.getString("guild_name");
            String guildTag = response.getString("tag");

            ContentValues guildValues = new ContentValues();
            guildValues.put(GW2Contract.GuildEntry.COLUMN_GUILD_ID, guildId);
            guildValues.put(GW2Contract.GuildEntry.COLUMN_GUILD_NAME, guildName);
            guildValues.put(GW2Contract.GuildEntry.COLUMN_TAG, guildTag);
            guildValues.put(GW2Contract.GuildEntry.COLUMN_LATEST_UPDATE, Utility.getJulianDay());

            Uri uri = mContext.getContentResolver().insert(GW2Contract.GuildEntry.CONTENT_URI,
                guildValues);
            if (uri == null) {
              Log.e(LOG_TAG, "insert failed: " + uri.toString());
            }
          } catch (JSONException e) {
            Log.e(LOG_TAG, "JSONException", e);
            e.printStackTrace();
          }
        }
      }, errorListener);
    }
  }

  public void updateCurrencyData() {
    Log.v(LOG_TAG, "updateCurrencyData");
    String url = getAllCurrencyInfo();

    fetchJsonArray(url, new Response.Listener<JSONArray>() {
      @Override
      public void onResponse(JSONArray response) {
        try {
          Vector<ContentValues> cVVector = new Vector<ContentValues>(response.length());
          for (int i = 0; i < response.length(); i++) {
            JSONObject currency = response.getJSONObject(i);
            int id = currency.getInt("id");
            String name = currency.getString("name");
            String desc = currency.getString("description");
            int order = currency.getInt("order");
            String icon = currency.getString("icon");

            ContentValues currencyValues = new ContentValues();
            currencyValues.put(GW2Contract.CurrencyEntry.COLUMN_ID, id);
            currencyValues.put(GW2Contract.CurrencyEntry.COLUMN_NAME, name);
            currencyValues.put(GW2Contract.CurrencyEntry.COLUMN_DESCRIPTION, desc);
            currencyValues.put(GW2Contract.CurrencyEntry.COLUMN_ORDER, order);
            currencyValues.put(GW2Contract.CurrencyEntry.COLUMN_ICON, icon);
            currencyValues.put(GW2Contract.CurrencyEntry.COLUMN_LATEST_UPDATE,
                Utility.getJulianDay());
            cVVector.add(currencyValues);
          }

          int inserted = bulkInsert(GW2Contract.CurrencyEntry.CONTENT_URI, cVVector);
          Log.d(LOG_TAG, "updateCurrencies complete. " + inserted + " inserted");
        } catch (JSONException e) {
          Log.e(LOG_TAG, "JSONException", e);
          e.printStackTrace();
        }
      }
    }, errorListener);
  }

  public void updateItemData(int[] ids) {
    Log.v(LOG_TAG, "updateItemData");
    String url = getItemURL(ids);

    fetchJsonArray(url, new Response.Listener<JSONArray>() {
      @Override
      public void onResponse(JSONArray response) {
        try {
          Vector<ContentValues> cVVector = new Vector<ContentValues>(response.length());

          for (int i = 0; i < response.length(); i++) {
            JSONObject item = response.getJSONObject(i);
            int id = item.getInt("id");
            String name = item.getString("name");
            String desc;
            if (!item.isNull("description")) {
              desc = item.getString("description");
            } else {
              desc = "";
            }
            String icon = item.getString("icon");
            String json = item.toString();

            ContentValues itemValues = new ContentValues();
            itemValues.put(GW2Contract.ItemEntry.COLUMN_ID, id);
            itemValues.put(GW2Contract.ItemEntry.COLUMN_NAME, name);
            itemValues.put(GW2Contract.ItemEntry.COLUMN_DESCRIPTION, desc);
            itemValues.put(GW2Contract.ItemEntry.COLUMN_ICON, icon);
            itemValues.put(GW2Contract.ItemEntry.COLUMN_JSON, json);
            itemValues.put(GW2Contract.ItemEntry.COLUMN_LATEST_UPDATE, Utility.getJulianDay());
            cVVector.add(itemValues);
          }

          int inserted = bulkInsert(GW2Contract.ItemEntry.CONTENT_URI, cVVector);
          Log.d(LOG_TAG, "updateItemData complete. " + inserted + " inserted");
        } catch (JSONException e) {
          Log.e(LOG_TAG, "JSONException", e);
          e.printStackTrace();
        }
      }
    }, errorListener);
  }
}
