package jp.panot.gw2accountbrowser.fetch;

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

import jp.panot.gw2accountbrowser.LruBitmapCache;
import jp.panot.gw2accountbrowser.util.ApiUtils;
import jp.panot.gw2accountbrowser.util.CommonUtils;
import jp.panot.gw2accountbrowser.data.GW2Contract;

/**
 * Created by panot on 2/22/16.
 */
public class GW2Fetch {
  private static final String LOG_TAG = GW2Fetch.class.getSimpleName();

  private static GW2Fetch mInstance;
  private RequestQueue mRequestQueue;
  private ImageLoader mImageLoader;
  private final Context mContext;

  private GW2Fetch(Context context) {
    mContext = context;
    mRequestQueue = getRequestQueue();
    mImageLoader = new ImageLoader(mRequestQueue, LruBitmapCache.getInstance(context));
  }

  public static synchronized GW2Fetch getInstance(Context context) {
    if (mInstance == null) {
      mInstance = new GW2Fetch(context);
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
    String url = ApiUtils.Worlds.url(ids);

    fetchJsonArray(url, new Response.Listener<JSONArray>() {
      @Override
      public void onResponse(JSONArray response) {
        try {
          Vector<ContentValues> cVVector = new Vector<>(response.length());

          for (int i = 0; i < response.length(); i++) {
            JSONObject world = response.getJSONObject(i);
            int id = world.getInt(ApiUtils.Worlds.JSON_ID);
            String name = world.getString(ApiUtils.Worlds.JSON_NAME);
            String population = world.getString(ApiUtils.Worlds.JSON_POPULATION);

            ContentValues worldValues = new ContentValues();
            worldValues.put(GW2Contract.WorldEntry.COLUMN_WORLD_ID, id);
            worldValues.put(GW2Contract.WorldEntry.COLUMN_NAME, name);
            worldValues.put(GW2Contract.WorldEntry.COLUMN_POPULATION, population);
            worldValues.put(GW2Contract.WorldEntry.COLUMN_LATEST_UPDATE,
                CommonUtils.getJulianDay());

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
      String url = ApiUtils.Guild.url(id);

      fetchJsonObject(url, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
          try {
            String guildId = response.getString(ApiUtils.Guild.JSON_ID);
            String guildName = response.getString(ApiUtils.Guild.JSON_NAME);
            String guildTag = response.getString(ApiUtils.Guild.JSON_TAG);

            ContentValues guildValues = new ContentValues();
            guildValues.put(GW2Contract.GuildEntry.COLUMN_GUILD_ID, guildId);
            guildValues.put(GW2Contract.GuildEntry.COLUMN_GUILD_NAME, guildName);
            guildValues.put(GW2Contract.GuildEntry.COLUMN_TAG, guildTag);
            guildValues.put(GW2Contract.GuildEntry.COLUMN_LATEST_UPDATE,
                CommonUtils.getJulianDay());

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
    String url = ApiUtils.Currencies.url();

    fetchJsonArray(url, new Response.Listener<JSONArray>() {
      @Override
      public void onResponse(JSONArray response) {
        try {
          Vector<ContentValues> cVVector = new Vector<>(response.length());
          for (int i = 0; i < response.length(); i++) {
            JSONObject currency = response.getJSONObject(i);
            int id = currency.getInt(ApiUtils.Currencies.JSON_ID);
            String name = currency.getString(ApiUtils.Currencies.JSON_NAME);
            String desc = currency.getString(ApiUtils.Currencies.JSON_DESCRIPTION);
            int order = currency.getInt(ApiUtils.Currencies.JSON_ORDER);
            String icon = currency.getString(ApiUtils.Currencies.JSON_ICON);

            ContentValues currencyValues = new ContentValues();
            currencyValues.put(GW2Contract.CurrencyEntry.COLUMN_ID, id);
            currencyValues.put(GW2Contract.CurrencyEntry.COLUMN_NAME, name);
            currencyValues.put(GW2Contract.CurrencyEntry.COLUMN_DESCRIPTION, desc);
            currencyValues.put(GW2Contract.CurrencyEntry.COLUMN_ORDER, order);
            currencyValues.put(GW2Contract.CurrencyEntry.COLUMN_ICON, icon);
            currencyValues.put(GW2Contract.CurrencyEntry.COLUMN_LATEST_UPDATE,
                CommonUtils.getJulianDay());
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
    String url = ApiUtils.Items.url(ids);

    fetchJsonArray(url, new Response.Listener<JSONArray>() {
      @Override
      public void onResponse(JSONArray response) {
        try {
          Vector<ContentValues> cVVector = new Vector<>(response.length());

          for (int i = 0; i < response.length(); i++) {
            JSONObject item = response.getJSONObject(i);
            int id = item.getInt(ApiUtils.Items.JSON_ID);
            String name = item.getString(ApiUtils.Items.JSON_NAME);
            String desc;
            if (!item.isNull(ApiUtils.Items.JSON_DESCRIPTION)) {
              desc = item.getString(ApiUtils.Items.JSON_DESCRIPTION);
            } else {
              desc = "";
            }
            String icon = item.getString(ApiUtils.Items.JSON_ICON);
            String json = item.toString();

            ContentValues itemValues = new ContentValues();
            itemValues.put(GW2Contract.ItemEntry.COLUMN_ID, id);
            itemValues.put(GW2Contract.ItemEntry.COLUMN_NAME, name);
            itemValues.put(GW2Contract.ItemEntry.COLUMN_DESCRIPTION, desc);
            itemValues.put(GW2Contract.ItemEntry.COLUMN_ICON, icon);
            itemValues.put(GW2Contract.ItemEntry.COLUMN_JSON, json);
            itemValues.put(GW2Contract.ItemEntry.COLUMN_LATEST_UPDATE, CommonUtils.getJulianDay());
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

  public void updateMaterialCategoryData() {
    Log.v(LOG_TAG, "updateMaterialCategoryData");
    String url = ApiUtils.Materials.url();

    fetchJsonArray(url, new Response.Listener<JSONArray>() {
      @Override
      public void onResponse(JSONArray response) {
        try {
          Vector<ContentValues> cVVector = new Vector<>(response.length());
          for (int i = 0; i < response.length(); i++) {
            JSONObject material = response.getJSONObject(i);
            int id = material.getInt(ApiUtils.Materials.JSON_ID);
            String name = material.getString(ApiUtils.Materials.JSON_NAME);
            int order = material.getInt(ApiUtils.Materials.JSON_ORDER);

            ContentValues materialValues = new ContentValues();
            materialValues.put(GW2Contract.MaterialEntry.COLUMN_ID, id);
            materialValues.put(GW2Contract.MaterialEntry.COLUMN_NAME, name);
            materialValues.put(GW2Contract.MaterialEntry.COLUMN_ORDER, order);
            materialValues.put(GW2Contract.MaterialEntry.COLUMN_LATEST_UPDATE,
                CommonUtils.getJulianDay());
            cVVector.add(materialValues);
          }

          int inserted = bulkInsert(GW2Contract.MaterialEntry.CONTENT_URI, cVVector);
          Log.d(LOG_TAG, "updateMaterialCategoryData complete. " + inserted + " inserted");
        } catch (JSONException e) {
          Log.e(LOG_TAG, "JSONException", e);
          e.printStackTrace();
        }
      }
    }, errorListener);
  }
}
