package gladun.vladimir.contactlistdemo.contact_list;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;

import gladun.vladimir.contactlistdemo.R;
import gladun.vladimir.contactlistdemo.model.Contact;
import gladun.vladimir.contactlistdemo.utilities.DBHelper;
import gladun.vladimir.contactlistdemo.utilities.VolleyHelper;

/**
 * Fragment with the contact list
 *
 * @author vvgladoun@gmail.com
 */
public class ContactListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private DBHelper mDbHelper;
    private int mSortDirection;

    // swipe container for the implementation of the swipe-to-refresh pattern
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private ContactListCursorLoader mContactsCursorLoader;

    private static final String CONTACTS_URL = "http://gladun.co.nf/users.json";
    private static final String TAG = ContactListCursorLoader.class.getSimpleName();
    // my cursor adapter
    private ContactsCursorAdapter mContactsCursorAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);
        // set initial sort direction (by default - ascending)
        mSortDirection = ContactListManager.SORT_ASCENDING;
        mContactsCursorLoader = new ContactListCursorLoader(getActivity(), getHelper(), mSortDirection);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle(R.string.app_name);
        View contactListView = inflater.inflate(R.layout.contact_list_fragment, container, false);
        initViewElements(contactListView);

        return contactListView;
    }

    private void initViewElements(View container){
        // find swipe container and add new listener on swipe refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) container.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                makeVolleyRequest();
                refreshLoader();
            }
        });
        // init recycler view
        mDbHelper = new DBHelper(getActivity());
        mRecyclerView = (RecyclerView) container.findViewById(R.id.recyclerView);
        mContactsCursorAdapter = new ContactsCursorAdapter(getActivity(), null, mDbHelper.getWritableDatabase());
        mRecyclerView.setAdapter(mContactsCursorAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // get progress bar
        mProgressBar = (ProgressBar) container.findViewById(R.id.progressBar);
        if (mContactsCursorAdapter.getItemCount() == 0) {
            showProgressBar();
        }
    }

    private void showProgressBar() {
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(){
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onStop() {
        //close db connection
        if (mDbHelper != null) {
            mDbHelper.close();
            mDbHelper = null;
        }
        super.onStop();
    }

    @Override
    public void onPause() {
        // clear swipe-to-refresh animation
        if (mSwipeRefreshLayout != null) {
            clearSwipeToRefresh();
        }
        super.onPause();
    }

    private void clearSwipeToRefresh(){
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.destroyDrawingCache();
        mSwipeRefreshLayout.clearAnimation();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                //if on top - start swipe to refresh pattern
                boolean enable = false;
                if (mRecyclerView != null && mRecyclerView.getChildCount() > 0) {
                    int topPosition = mRecyclerView.getChildAt(0).getTop();
                    enable = topPosition >= 0;
                }
                mSwipeRefreshLayout.setEnabled(enable);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        getLoaderManager().initLoader(0, null, this);
        makeVolleyRequest();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_ascending:
                changeSort(ContactListManager.SORT_ASCENDING);
                break;
            case R.id.action_sort_descending:
                changeSort(ContactListManager.SORT_DESCENDING);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get DBHelper, if empty - create new
     * @return DBHelper object
     */
    private DBHelper getHelper() {
        if (mDbHelper == null) {
            mDbHelper = new DBHelper(getActivity());
        }
        return mDbHelper;
    }

    /**
     * Change sort direction and update list
     * @param sortDirection - A-Z or Z-A
     */
    private void changeSort(int sortDirection) {
        if (mSortDirection != sortDirection) {
            mSortDirection = sortDirection;
            // get loader and force load with new sort order
            refreshLoader();
        }
    }

    /**
     * Set current sort direction and start loading
     */
    private void refreshLoader() {
        Loader<Cursor> loader = getLoaderManager().getLoader(0);
        if (loader != null && loader.isStarted()) {
            ((ContactListCursorLoader) loader).setSortDirection(mSortDirection);
            loader.forceLoad();
        }
    }


    private void makeVolleyRequest(){
        JsonArrayRequest req = new JsonArrayRequest(CONTACTS_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            String jsonString = response.toString();
                            Gson gson = new Gson();
                            Contact[] contacts = gson.fromJson(jsonString, Contact[].class);
                            boolean dataUpdated = DBHelper.batchLoadContacts(getHelper().getWritableDatabase(),
                                    new ArrayList<>(Arrays.asList(contacts)));
                            if (dataUpdated) {
                                refreshLoader();
                            }
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                            Log.e(TAG, "JSON array parsing error");
                        } catch (SQLiteException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Data load failed");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        VolleyHelper.getInstance(getActivity()).addToRequestQueue(req);
    }


    // LOADER CALLBACK IMPLEMENTATION

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mContactsCursorLoader == null) {
            mContactsCursorLoader = new ContactListCursorLoader(getActivity(), getHelper(), mSortDirection);
        }
        return mContactsCursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mSwipeRefreshLayout.setRefreshing(false);
        if (mContactsCursorAdapter.getItemCount() == 0) {
            hideProgressBar();
        }
        if (data != null) {
            mContactsCursorAdapter.swapCursor(data);
            mContactsCursorAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mContactsCursorAdapter.swapCursor(null);
    }
}
