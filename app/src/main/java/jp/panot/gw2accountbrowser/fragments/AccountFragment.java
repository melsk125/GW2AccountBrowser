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
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jp.panot.gw2accountbrowser.R;
import jp.panot.gw2accountbrowser.data.GW2Contract;
import jp.panot.gw2accountbrowser.fetch.GW2Fetch;
import jp.panot.gw2accountbrowser.util.ApiUtils;
import jp.panot.gw2accountbrowser.util.CommonUtils;

/**
 * Created by panot on 2/22/16.
 */
public class AccountFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
  private static final String LOG_TAG = AccountFragment.class.getSimpleName();

  private static final int WORLD_LOADER = 0;
  private static final int GUILD_LOADER = 1;

  private int mWorldId;
  private String[] mGuildIds;
  private String mToken;

  private boolean attemptedWorld;
  private boolean attemptedGuild;

  public static final String[] WORLD_COLUMNS = {
      GW2Contract.WorldEntry.TABLE_NAME + "." + GW2Contract.WorldEntry._ID,
      GW2Contract.WorldEntry.COLUMN_WORLD_ID,
      GW2Contract.WorldEntry.COLUMN_NAME,
      GW2Contract.WorldEntry.COLUMN_POPULATION,
      GW2Contract.WorldEntry.COLUMN_LATEST_UPDATE,
  };
  public static final int COL_WORLD_ID_ = 0;
  public static final int COL_WORLD_ID = 1;
  public static final int COL_WORLD_NAME = 2;
  public static final int COL_WORLD_POPULATION = 3;
  public static final int COL_WORLD_LATEST_UPDATE = 4;

  public static final String[] GUILD_COLUMNS = {
      GW2Contract.GuildEntry.TABLE_NAME + "." + GW2Contract.GuildEntry._ID,
      GW2Contract.GuildEntry.COLUMN_GUILD_ID,
      GW2Contract.GuildEntry.COLUMN_GUILD_NAME,
      GW2Contract.GuildEntry.COLUMN_TAG,
      GW2Contract.GuildEntry.COLUMN_LATEST_UPDATE,
  };
  public static final int COL_GUILD_ID_ = 0;
  public static final int COL_GUILD_ID = 1;
  public static final int COL_GUILD_NAME = 2;
  public static final int COL_GUILD_TAG = 3;
  public static final int COL_GUILD_LATEST_UPDATE = 4;

  private GW2Fetch mFetch;
  private GuildAdapter mGuildAdapter;
  private ArrayAdapter<String> mCharacterAdapter;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    mFetch = GW2Fetch.getInstance(getActivity().getApplicationContext());
    mGuildAdapter = new GuildAdapter(getActivity(), null, 0);
    mCharacterAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_character,
        R.id.list_character_textview);
    mToken = CommonUtils.getAccessToken();

    View view = inflater.inflate(R.layout.fragment_account, null);
    getActivity().setTitle("Account");

    final TextView nameView = (TextView) view.findViewById(R.id.account_name_textview);
    final TextView createdDateView = (TextView) view.findViewById(R.id.account_date_created_textview);

    ListView guildListView = (ListView) view.findViewById(R.id.account_guild_listview);
    guildListView.setAdapter(mGuildAdapter);

    ListView characterListView = (ListView) view.findViewById(R.id.account_character_listview);
    characterListView.setAdapter(mCharacterAdapter);

    final Response.ErrorListener errorListener = new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Log.e(LOG_TAG, "Volley error", error);
        error.printStackTrace();
      }
    };

    final LoaderManager.LoaderCallbacks<Cursor> cursorLoaderCallbacks = this;

    // TODO: Extract JSON fields into an external static class.
    final Response.Listener<JSONObject> successListener = new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        try {
          String name = response.getString(ApiUtils.Account.JSON_NAME);
          int worldId = response.getInt(ApiUtils.Account.JSON_WORLD);
          String created = response.getString(ApiUtils.Account.JSON_CREATED);
          JSONArray guildArray = response.getJSONArray(ApiUtils.Account.JSON_GUILDS);

          nameView.setText(name);
          createdDateView.setText(created);

          // Update world
          mWorldId = worldId;
          mGuildIds = new String[guildArray.length()];
          for (int i = 0; i < guildArray.length(); i++) {
            mGuildIds[i] = guildArray.getString(i);
          }

          attemptedWorld = false;
          attemptedGuild = false;

          getLoaderManager().initLoader(WORLD_LOADER, null, cursorLoaderCallbacks);
          getLoaderManager().initLoader(GUILD_LOADER, null, cursorLoaderCallbacks);
        } catch (JSONException e) {
          Log.e(LOG_TAG, "JSONException", e);
          e.printStackTrace();
        } catch (Exception e) {
          Log.e(LOG_TAG, "Exception", e);
          e.printStackTrace();
        }
      }
    };

    String url = ApiUtils.Account.url(mToken);
    mFetch.fetchJsonObject(url, successListener, errorListener);

    final Response.Listener<JSONArray> characterListener = new Response.Listener<JSONArray>() {
      @Override
      public void onResponse(JSONArray response) {
        try {
          for (int i = 0; i < response.length(); i++) {
            mCharacterAdapter.add(response.getString(i));
          }
        } catch (JSONException e) {
          Log.e(LOG_TAG, "JSONException", e);
          e.printStackTrace();
        }
      }
    };

    String characterUrl = ApiUtils.Characters.allNamesUrl(mToken);
    mFetch.fetchJsonArray(characterUrl, characterListener, errorListener);

    return view;
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    switch (id) {
      case WORLD_LOADER:
        return createWorldCursorLoader();
      case GUILD_LOADER:
        return createGuildCursorLoader();
      default:
        return null;
    }
  }

  private Loader<Cursor> createWorldCursorLoader() {
    if (mWorldId == 0 || getActivity() == null) {
      return null;
    }
    Uri worldUri = GW2Contract.WorldEntry.buildWorld(mWorldId);
    int julianToday = CommonUtils.getJulianDay();
    String selection = GW2Contract.WorldEntry.COLUMN_LATEST_UPDATE + " > ? - ? ";
    String[] selectionArgs = new String[] {String.valueOf(julianToday),
        String.valueOf(GW2Contract.WorldEntry.UPDATE_FREQUENCY)};
    return new CursorLoader(getActivity(), worldUri, WORLD_COLUMNS, selection, selectionArgs, null);
  }

  private Loader<Cursor> createGuildCursorLoader() {
    if (mGuildIds == null || mGuildIds.length == 0 || getActivity() == null) {
      return null;
    }
    String ids = CommonUtils.makeQuestionmarks(mGuildIds.length);

    Uri guildUri = GW2Contract.GuildEntry.CONTENT_URI;

    String selection = GW2Contract.GuildEntry.TABLE_NAME +
        "." + GW2Contract.GuildEntry.COLUMN_GUILD_ID + " IN ( " + ids + " ) AND " +
        GW2Contract.GuildEntry.COLUMN_LATEST_UPDATE + " > ? - ? ";
    String[] selectionArgs = new String[mGuildIds.length + 2];
    System.arraycopy(mGuildIds, 0, selectionArgs, 0, mGuildIds.length);
    int julianToday = CommonUtils.getJulianDay();
    selectionArgs[mGuildIds.length] = String.valueOf(julianToday);
    selectionArgs[mGuildIds.length + 1] = String.valueOf(GW2Contract.GuildEntry.UPDATE_FREQUENCY);
    return new CursorLoader(getActivity(), guildUri, GUILD_COLUMNS, selection, selectionArgs, null);
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    switch (loader.getId()) {
      case WORLD_LOADER:
        handleWorldLoaded(data);
        break;
      case GUILD_LOADER:
        handleGuildLoaded(data);
        break;
    }
  }

  private void handleWorldLoaded(Cursor data) {
    if (!data.moveToFirst()) {
      if (!attemptedWorld) {
        attemptedWorld = true;
        mFetch.updateWorlds(new int[]{mWorldId});
      }
      return;
    }

    if (!attemptedWorld && data.getCount() != 1) {
      attemptedWorld = true;
      mFetch.updateWorlds(new int[]{mWorldId});
      return;
    }

    final TextView worldView = (TextView) getView().findViewById(R.id.account_world_textview);
    worldView.setText(data.getString(COL_WORLD_NAME));
  }

  private void handleGuildLoaded(Cursor data) {
    if (!data.moveToFirst()) {
      if (!attemptedGuild) {
        attemptedGuild = true;
        mFetch.updateGuilds(mGuildIds);
      }
      return;
    }

    if (!attemptedGuild && data.getCount() != mGuildIds.length) {
      attemptedGuild = true;
      mFetch.updateGuilds(mGuildIds);
      return;
    }

    mGuildAdapter.swapCursor(data);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    switch (loader.getId()) {
      case GUILD_LOADER:
        mGuildAdapter.swapCursor(null);
        break;
    }
  }

  private class GuildAdapter extends CursorAdapter {
    public GuildAdapter(Context context, Cursor c, int flags) {
      super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
      return LayoutInflater.from(context).inflate(R.layout.list_item_guild, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
      TextView nameView = (TextView) view.findViewById(R.id.list_guild_textview);
      nameView.setText(cursor.getString(AccountFragment.COL_GUILD_NAME));
    }
  }
}
