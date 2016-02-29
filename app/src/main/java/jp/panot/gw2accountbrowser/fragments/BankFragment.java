package jp.panot.gw2accountbrowser.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jp.panot.gw2accountbrowser.R;
import jp.panot.gw2accountbrowser.data.GW2Contract;
import jp.panot.gw2accountbrowser.fetch.GW2Fetch;
import jp.panot.gw2accountbrowser.util.UrlUtils;
import jp.panot.gw2accountbrowser.util.CommonUtils;

/**
 * Created by panot on 2/25/16.
 */
public class BankFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
  private static final String LOG_TAG = BankFragment.class.getSimpleName();

  private static final int ITEM_LOADER = 0;

  private GW2Fetch mFetch;
  private BankAdapter mBankAdapter;

  private int mBankSize;
  // Map index in bank to JSONObject.
  private Map<Integer, JSONObject> mBank;
  // Map item id to icon URL.
  private Map<Integer, String> mItemIcons;
  private ArrayList<Integer> mItems;

  private boolean attemptedItem;

  public static final String[] ITEM_COLUMNS = {
      GW2Contract.ItemEntry._ID,
      GW2Contract.ItemEntry.COLUMN_ID,
      GW2Contract.ItemEntry.COLUMN_NAME,
      GW2Contract.ItemEntry.COLUMN_ICON,
      GW2Contract.ItemEntry.COLUMN_LATEST_UPDATE,
  };
  public static final int COL_ITEM_ID_ = 0;
  public static final int COL_ITEM_ID = 1;
  public static final int COL_ITEM_NAME = 2;
  public static final int COL_ITEM_ICON = 3;
  public static final int COL_ITEM_LATEST_UPDATE = 4;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    mFetch = GW2Fetch.getInstance(getActivity().getApplicationContext());
    mBankAdapter = new BankAdapter(getActivity());

    View rootView = inflater.inflate(R.layout.fragment_bank, null);
    GridView grid = (GridView) rootView.findViewById(R.id.bank_gridview);
    grid.setAdapter(mBankAdapter);

    mBank = new ArrayMap<>();
    mItemIcons = new ArrayMap<>();
    mItems = new ArrayList<>();

    // Set onClickListener

    getActivity().setTitle("Bank");

    final Response.ErrorListener errorListener = new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Log.e(LOG_TAG, "Volley error", error);
        error.printStackTrace();
      }
    };

    final LoaderManager.LoaderCallbacks<Cursor> cursorLoaderCallbacks = this;

    final Response.Listener<JSONArray> bankListener = new Response.Listener<JSONArray>() {
      @Override
      public void onResponse(JSONArray response) {
        try {
          Set<Integer> itemSet = new HashSet<>();
          mBankSize = response.length();
          for (int i = 0; i < response.length(); i++) {
            if (response.isNull(i)) {
              mBank.put(i, null);
            } else {
              JSONObject item = response.getJSONObject(i);
              itemSet.add(item.getInt("id"));
              mBank.put(i, item);
            }
          }
          mItems = new ArrayList<>(itemSet);
          getLoaderManager().initLoader(ITEM_LOADER, null, cursorLoaderCallbacks);
        } catch (JSONException e) {
          Log.e(LOG_TAG, "JSONException", e);
          e.printStackTrace();
        }
      }
    };

    String url = UrlUtils.getBank(CommonUtils.getAccessToken());
    mFetch.fetchJsonArray(url, bankListener, errorListener);

    return rootView;
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    switch (id) {
      case ITEM_LOADER:
        return createItemLoader();
      default:
        return null;
    }
  }

  private Loader<Cursor> createItemLoader() {
    Uri itemUri = GW2Contract.ItemEntry.CONTENT_URI;
    Log.d(LOG_TAG, "itemUri: " + itemUri);

    Set<String> itemIds = new HashSet<>();
    for (JSONObject item : mBank.values()) {
      if (item != null) {
        try {
          itemIds.add(String.valueOf(item.getInt("id")));
        } catch (JSONException e) {
          Log.e(LOG_TAG, "JSONException", e);
          e.printStackTrace();
        }
      }
    }
    String ids = CommonUtils.makeQuestionmarks(itemIds.size());
    String[] selectionArgs = new String[itemIds.size() + 2];
    int julianToday = CommonUtils.getJulianDay();
    String selection = GW2Contract.ItemEntry.TABLE_NAME +
        "." + GW2Contract.ItemEntry.COLUMN_ID + " IN ( " + ids + " ) AND " +
        GW2Contract.ItemEntry.COLUMN_LATEST_UPDATE + " > ? - ? ";

    itemIds.toArray(selectionArgs);
    selectionArgs[itemIds.size()] = String.valueOf(julianToday);
    selectionArgs[itemIds.size() + 1] = String.valueOf(GW2Contract.ItemEntry.UPDATE_FREQUENCY);
    return new CursorLoader(getActivity(), itemUri, ITEM_COLUMNS, selection, selectionArgs, null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    switch (loader.getId()) {
      case ITEM_LOADER:
        handleItemLoader(data);
        break;
    }
  }

  private void handleItemLoader(Cursor data) {
    Log.v(LOG_TAG, "handleItemLoader");
    if (!data.moveToFirst()) {
      Log.v(LOG_TAG, "not move to first");
      if (!attemptedItem && mItems.size() > 0) {
        attemptedItem = true;
        int[] items = new int[mItems.size()];
        int i = 0;
        for (Integer id : mItems) {
          items[i] = id;
          i++;
          Log.v(LOG_TAG, "item: " + id);
        }
        Log.v(LOG_TAG, "update: " + mItems.size());
        mFetch.updateItemData(items);
      }
      return;
    }

    if (!attemptedItem && data.getCount() != mItems.size() && mItems.size() > 0) {
      attemptedItem = true;
      int[] items = new int[mItems.size()];
      int i = 0;
      for (Integer id : mItems) {
        items[i] = id;
        i++;
      }
      mFetch.updateItemData(items);
      return;
    }

    do {
      int id = data.getInt(COL_ITEM_ID);
      String icon = data.getString(COL_ITEM_ICON);
      mItemIcons.put(id, icon);
    } while (data.moveToNext());

    mBankAdapter.notifyDataSetChanged();
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
  }

  private class BankAdapter extends BaseAdapter {
    private Context mContext;

    public BankAdapter(Context context) {
      mContext = context;
    }

    @Override
    public int getCount() {
      return mBankSize;
    }

    @Override
    public Object getItem(int position) {
      return null;
    }

    @Override
    public long getItemId(int position) {
      return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      RelativeLayout rl;

      if (convertView != null) {
        rl = (RelativeLayout) convertView;
      } else {
        rl = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.grid_item_item, null);
      }

      NetworkImageView iconView = (NetworkImageView) rl.findViewById(R.id.bank_icon_imageview);
      TextView countView = (TextView) rl.findViewById(R.id.bank_count_textview);
      iconView.setImageUrl("", null);
      countView.setText("");

      JSONObject itemJson = mBank.get(position);
      if (itemJson == null) {
        return rl;
      }

      try {
        int itemId = itemJson.getInt("id");
        int itemCount = itemJson.getInt("count");
        String itemIcon = mItemIcons.get(itemId);

        if (itemIcon != null) {
          iconView.setImageUrl(itemIcon, mFetch.getImageLoader());
        }
        if (itemCount > 1) {
          countView.setText(String.valueOf(itemCount));
        }
      } catch (JSONException e) {
        Log.e(LOG_TAG, "JSONException", e);
        e.printStackTrace();
      }

      return rl;
    }
  }
}
