package com.keepfit.app.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.keepfit.app.R;
import com.keepfit.app.utils.DataFolder;
import com.keepfit.app.utils.DataFolderAdapter;
import com.keepfit.app.utils.DataFolderLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Edward on 3/4/2016.
 */
public class ResultsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<DataFolder>> {
    private static final String TAG = "ResultsFragment";
    private final static int LOADER_ID = new Random().nextInt();

    private Context context;
    private View view;
    private View loadingPanel;
    private DataFolderAdapter adapter;
    private List<DataFolder> dataFolders = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_results, container, false);
        initialize(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, String.format("Calling Fragment initLoader with id %s!", LOADER_ID));
        getLoaderManager().initLoader(LOADER_ID, savedInstanceState, this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void initialize(View view) {
        adapter = new DataFolderAdapter(dataFolders, context);
        ExpandableListView listView = (ExpandableListView) view.findViewById(R.id.list_results);
        listView.setAdapter(adapter);

        loadingPanel = view.findViewById(R.id.loading_panel);
    }

    @Override
    public Loader<List<DataFolder>> onCreateLoader(int id, Bundle args) {
        return new DataFolderLoader(context);
    }

    @Override
    public void onLoadFinished(Loader<List<DataFolder>> loader, List<DataFolder> data) {
        Log.d(TAG, String.format("onLoadFinished() called! Loading data!"));
        adapter.setGroups(data);
        adapter.notifyDataSetChanged();
        loadingPanel.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<DataFolder>> loader) {
        loader.reset();
        adapter.reset();
        adapter.notifyDataSetChanged();
        loadingPanel.setVisibility(View.VISIBLE);
    }
}
