package jp.panot.gw2accountbrowser;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import jp.panot.gw2accountbrowser.data.GW2Contract;

/**
 * Created by panot on 2/25/16.
 */
public class WalletFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
  private static final String LOG_TAG = WalletFragment.class.getSimpleName();

  private static final int CURRENCY_LOADER = 0;

  private AccountBrowserFetch mFetch;
  private WalletAdapter mWalletAdapter;

  private Map<Integer, Integer> mWallet;

  private boolean attemptedCurrency;

  public static final String[] CURRENCY_COLUMNS = {
      GW2Contract.CurrencyEntry._ID,
      GW2Contract.CurrencyEntry.COLUMN_ID,
      GW2Contract.CurrencyEntry.COLUMN_NAME,
      GW2Contract.CurrencyEntry.COLUMN_DESCRIPTION,
      GW2Contract.CurrencyEntry.COLUMN_ORDER,
      GW2Contract.CurrencyEntry.COLUMN_ICON,
      GW2Contract.CurrencyEntry.COLUMN_LATEST_UPDATE,
  };
  public static final int COL_CURRENCY_ID_ = 0;
  public static final int COL_CURRENCY_ID = 1;
  public static final int COL_CURRENCY_NAME = 2;
  public static final int COL_CURRENCY_DESCRIPTION = 3;
  public static final int COL_CURRENCY_ORDER = 4;
  public static final int COL_CURRENCY_ICON = 5;
  public static final int COL_CURRENCY_LATEST_UPDATE = 6;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    mFetch = AccountBrowserFetch.getInstance(getActivity().getApplicationContext());
    mWalletAdapter = new WalletAdapter(getActivity(), null, 0);

    View view = inflater.inflate(R.layout.fragment_wallet, null);
    ListView list = (ListView) view.findViewById(R.id.wallet_listview);
    list.setAdapter(mWalletAdapter);
    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CursorAdapter ca = (CursorAdapter) parent.getAdapter();
        CursorWrapper cw = (CursorWrapper) ca.getItem(position);
        Toast.makeText(getActivity(), cw.getString(COL_CURRENCY_DESCRIPTION), Toast.LENGTH_SHORT)
            .show();
      }
    });

    getActivity().setTitle("Wallet");

    mWallet = new ArrayMap<>();

    // TODO: How often to call this? Once a week?
    mFetch.updateCurrencyData();
    getLoaderManager().initLoader(CURRENCY_LOADER, null, this);

    final Response.ErrorListener errorListener = new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Log.e(LOG_TAG, "Volley error", error);
        error.printStackTrace();
      }
    };

    final Response.Listener<JSONArray> walletListener = new Response.Listener<JSONArray>() {
      @Override
      public void onResponse(JSONArray response) {
        try {
          for (int i = 0; i < response.length(); i++) {
            JSONObject wallet = response.getJSONObject(i);
            int id = wallet.getInt("id");
            int value = wallet.getInt("value");
            mWallet.put(id, value);
          }
          mWalletAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
          Log.e(LOG_TAG, "JSONException", e);
          e.printStackTrace();
        }
      }
    };

    String url = Utility.getWallet(Utility.getAccessToken());
    mFetch.fetchJsonArray(url, walletListener, errorListener);

    return view;
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    switch (id) {
      case CURRENCY_LOADER:
        return createCurrencyLoader();
      default:
        return null;
    }
  }

  private Loader<Cursor> createCurrencyLoader() {
    Uri currencyUri = GW2Contract.CurrencyEntry.CONTENT_URI;
    Log.d(LOG_TAG, "currencyUri: " + currencyUri);
    int julianToday = Utility.getJulianDay();
    String selection = GW2Contract.CurrencyEntry.COLUMN_LATEST_UPDATE + " > ? - ?";
    String[] selectionArgs = new String[]{String.valueOf(julianToday),
        String.valueOf(GW2Contract.CurrencyEntry.UPDATE_FREQUENCY)};
    return new CursorLoader(getActivity(), currencyUri, CURRENCY_COLUMNS, selection, selectionArgs,
        GW2Contract.CurrencyEntry.COLUMN_ORDER + " ASC");
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    switch (loader.getId()) {
      case CURRENCY_LOADER:
        handleCurrencyLoaded(data);
        break;
    }
  }

  private void handleCurrencyLoaded(Cursor data) {
    if (!data.moveToFirst() && !attemptedCurrency) {
      attemptedCurrency = true;
      mFetch.updateCurrencyData();
    }

    mWalletAdapter.swapCursor(data);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    switch (loader.getId()) {
      case CURRENCY_LOADER:
        mWalletAdapter.swapCursor(null);
        break;
    }
  }

  private class WalletAdapter extends CursorAdapter {
    public WalletAdapter(Context context, Cursor c, int flags) {
      super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
      return LayoutInflater.from(context).inflate(R.layout.list_item_wallet, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
      NetworkImageView iconView = (NetworkImageView) view.findViewById(R.id.wallet_icon_imageview);
      TextView nameView = (TextView) view.findViewById(R.id.wallet_name_textview);
      TextView valueView = (TextView) view.findViewById(R.id.wallet_value_textview);

      iconView.setImageUrl(cursor.getString(COL_CURRENCY_ICON), mFetch.getImageLoader());
      nameView.setText(cursor.getString(COL_CURRENCY_NAME));
      int id = cursor.getInt(COL_CURRENCY_ID);
      int value = 0;
      if (mWallet.containsKey(id)) {
        value = mWallet.get(id);
      }
      valueView.setText(String.valueOf(value));
    }
  }
}
