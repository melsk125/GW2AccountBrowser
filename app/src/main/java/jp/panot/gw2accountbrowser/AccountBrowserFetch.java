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
    String url = Utility.getWorldsURL(ids);

    fetchJsonArray(url, new Response.Listener<JSONArray>() {
      @Override
      public void onResponse(JSONArray response) {
        try {
          Vector<ContentValues> cVVector = new Vector<>(response.length());

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
    for (String id : ids) {
      String url = Utility.getGuildInfoURL(id);

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
              Log.e(LOG_TAG, "insert failed: uri is null");
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
    String url = Utility.getAllCurrencyInfo();

    fetchJsonArray(url, new Response.Listener<JSONArray>() {
      @Override
      public void onResponse(JSONArray response) {
        try {
          Vector<ContentValues> cVVector = new Vector<>(response.length());
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
    String url = Utility.getItemURL(ids);

    fetchJsonArray(url, new Response.Listener<JSONArray>() {
      @Override
      public void onResponse(JSONArray response) {
        try {
          Vector<ContentValues> cVVector = new Vector<>(response.length());

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
