package jp.panot.gw2accountbrowser.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import jp.panot.gw2accountbrowser.R;

/**
 * Created by panot on 2/29/16.
 */
public class MaterialFragment extends Fragment {
  private static final String LOG_TAG = MaterialFragment.class.getSimpleName();

  private MaterialListAdapter mMaterialListAdapter;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    mMaterialListAdapter = new MaterialListAdapter(getActivity());

    View rootView = inflater.inflate(R.layout.fragment_material, null);
    ListView materialList = (ListView) rootView.findViewById(R.id.material_listview);
    materialList.setAdapter(mMaterialListAdapter);

    return rootView;
  }

  private class MaterialListAdapter extends BaseAdapter {
    private Context mContext;

    public MaterialListAdapter(Context context) {
      mContext = context;
    }

    @Override
    public int getCount() {
      return mContext.getResources().getIntArray(R.array.material_ids).length;
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
      LinearLayout ll;

      if (convertView != null) {
        ll = (LinearLayout) convertView;
      } else {
        ll = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_block, null);
      }

      int[] categories = mContext.getResources().getIntArray(R.array.material_ids);
      int categoryId = categories[position];

      TextView titleView = (TextView) ll.findViewById(R.id.title_textview);
      titleView.setText(String.valueOf(categoryId));

      return ll;
    }
  }
}
