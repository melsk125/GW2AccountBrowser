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
import android.widget.ListView;
import android.widget.TextView;

import java.util.Map;
import java.util.Vector;

import jp.panot.gw2accountbrowser.R;
import jp.panot.gw2accountbrowser.data.GW2Contract;
import jp.panot.gw2accountbrowser.fetch.GW2Fetch;
import jp.panot.gw2accountbrowser.util.CommonUtils;

/**
 * Created by panot on 2/29/16.
 */
public class MaterialFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
  private static final String LOG_TAG = MaterialFragment.class.getSimpleName();

  public static final int MATERIAL_LOADER = 0;

  private GW2Fetch mFetch;
  private MaterialListAdapter mMaterialListAdapter;

  private Vector<Integer> mMaterialCategories;
  private Map<Integer, Vector<Integer>> mCategoryToItems;

  boolean attemptedMaterial;

  public static final String[] MATERIAL_COLUMNS = {
      GW2Contract.MaterialEntry._ID,
      GW2Contract.MaterialEntry.COLUMN_ID,
      GW2Contract.MaterialEntry.COLUMN_NAME,
      GW2Contract.MaterialEntry.COLUMN_ORDER,
      GW2Contract.MaterialEntry.COLUMN_LATEST_UPDATE,
  };
  public static final int COL_MATERIAL_ID_ = 0;
  public static final int COL_MATERIAL_ID = 1;
  public static final int COL_MATERIAL_NAME = 2;
  public static final int COL_MATERIAL_ORDER = 3;
  public static final int COL_MATERIAL_LATEST_UPDATE = 4;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    mFetch = GW2Fetch.getInstance(getActivity().getApplicationContext());
    mMaterialListAdapter = new MaterialListAdapter(getActivity(), null, 0);

    View rootView = inflater.inflate(R.layout.fragment_material, null);
    ListView materialList = (ListView) rootView.findViewById(R.id.material_listview);
    materialList.setAdapter(mMaterialListAdapter);

    getLoaderManager().initLoader(MATERIAL_LOADER, null, this);

    return rootView;
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    Log.d(LOG_TAG, "onCreateLoader");
    switch (id) {
      case MATERIAL_LOADER:
        return createMaterialLoader();
      default:
        return null;
    }
  }

  private Loader<Cursor> createMaterialLoader() {
    Uri materialUri = GW2Contract.MaterialEntry.CONTENT_URI;
    Log.d(LOG_TAG, "materialUri: " + materialUri);
    int julianToday = CommonUtils.getJulianDay();
    String selection = GW2Contract.MaterialEntry.COLUMN_LATEST_UPDATE + " > ? - ?";
    String[] selectionArgs = new String[]{String.valueOf(julianToday),
        String.valueOf(GW2Contract.MaterialEntry.UPDATE_FREQUENCY)};
    return new CursorLoader(getActivity(), materialUri, MATERIAL_COLUMNS, selection, selectionArgs,
        GW2Contract.MaterialEntry.COLUMN_ORDER + " ASC");
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    switch (loader.getId()) {
      case MATERIAL_LOADER:
        handleMaterialLoader(data);
        break;
    }
  }

  private void handleMaterialLoader(Cursor data) {
    if (!data.moveToFirst() && !attemptedMaterial) {
      attemptedMaterial = true;
      mFetch.updateMaterialCategoryData();
    }

    mMaterialListAdapter.swapCursor(data);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    switch (loader.getId()) {
      case MATERIAL_LOADER:
        mMaterialListAdapter.swapCursor(null);
        break;
    }
  }

  private class MaterialListAdapter extends CursorAdapter {
    public MaterialListAdapter(Context context, Cursor c, int flags) {
      super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
      return LayoutInflater.from(context).inflate(R.layout.item_block, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
      String categoryName = cursor.getString(COL_MATERIAL_NAME);

      TextView titleView = (TextView) view.findViewById(R.id.title_textview);
      titleView.setText(categoryName);
    }
  }
}
