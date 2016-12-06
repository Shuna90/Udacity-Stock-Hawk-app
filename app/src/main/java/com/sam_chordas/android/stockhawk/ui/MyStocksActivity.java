package com.sam_chordas.android.stockhawk.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.melnykov.fab.FloatingActionButton;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.QuoteCursorAdapter;
import com.sam_chordas.android.stockhawk.rest.RecyclerViewItemClickListener;
import com.sam_chordas.android.stockhawk.service.StockIntentService;
import com.sam_chordas.android.stockhawk.service.StockTaskService;
import com.sam_chordas.android.stockhawk.touch_helper.SimpleItemTouchHelperCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyStocksActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        RecyclerViewItemClickListener.OnItemClickListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private static final String LOG_TAG = MyStocksActivity.class.getName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private final String CHANGE = "CHENGE_UNIT";
    public static final int CHANGE_DOLLARS = 0;
    public static final int CHANGE_PERCENTAGES = 1;
    private CharSequence mTitle;
    private ItemTouchHelper mItemTouchHelper;
    private static final int CURSOR_LOADER_ID = 0;
    private QuoteCursorAdapter mCursorAdapter;
    private boolean twoPane;
    private int mChange = CHANGE_DOLLARS;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_stocks);
        ButterKnife.bind(this);

        if (findViewById(R.id.stock_detail_container) != null) {
            twoPane = true;
        }else{
            twoPane = false;
        }
        checkNetwork();
        // The intent service is for executing immediate pulls from the Yahoo API
        // GCMTaskService can only schedule tasks, they cannot execute immediately
        if (savedInstanceState == null) {
            // Run the initialize task service so that some stocks appear upon an empty database
            Intent mServiceIntent = new Intent(this, StockIntentService.class);
            mServiceIntent.putExtra(StockIntentService.SERVICE_TAG, StockIntentService.ACTION_INIT);
            if (checkNetwork()) {
                startService(mServiceIntent);
            } else {
                networkToast();
            }
        }else{
            mChange = savedInstanceState.getInt(CHANGE);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(this, this));
        mCursorAdapter = new QuoteCursorAdapter(this, null, mChange);
        recyclerView.setAdapter(mCursorAdapter);
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

        fab.attachToRecyclerView(recyclerView);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mCursorAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        mTitle = getTitle();
        // create a periodic task to pull stocks once every hour after the app has been opened. This
        // is so Widget data stays up to date.
        PeriodicTask periodicTask = new PeriodicTask.Builder()
                .setService(StockTaskService.class)
                .setPeriod(60 * 60)
                .setFlex(10)
                .setTag(StockTaskService.PERIOD_TAG)
                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                .setRequiresCharging(false)
                .build();
        // Schedule task with tag "periodic." This ensure that only the stocks present in the DB
        // are updated.
        GcmNetworkManager.getInstance(this).schedule(periodicTask);
    }


    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CHANGE, mChange);
    }


    public void networkToast() {
        Toast.makeText(this, getString(R.string.network_toast), Toast.LENGTH_SHORT).show();
    }

    public boolean checkNetwork() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_stocks, menu);
        restoreActionBar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_change_units) {
            if (mChange == CHANGE_DOLLARS) {
                mChange = CHANGE_PERCENTAGES;
            } else {
                mChange = CHANGE_DOLLARS;
            }
            mCursorAdapter.setChangeUnits(mChange);
            mCursorAdapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This narrows the return to only the stocks that are most current.
        return new CursorLoader(this,
                QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BID_PRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
        if (mCursorAdapter.getItemCount() == 0){
            if (!checkNetwork()){
                networkToast();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }


    @Override
    public void onItemClick(View v, int position) {
        if (twoPane) {
            Bundle args = new Bundle();
            args.putString(StockDetailActivityFragment.STOCK_DETAIL, mCursorAdapter.getSymbol(position));

            StockDetailActivityFragment fragment = new StockDetailActivityFragment();
            fragment.setArguments(args);

            getFragmentManager().beginTransaction()
                    .replace(R.id.stock_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(v.getContext(), StockDetailActivity.class);
            intent.putExtra(StockDetailActivityFragment.STOCK_DETAIL, mCursorAdapter.getSymbol(position));
            v.getContext().startActivity(intent);
        }
    }

    @OnClick(R.id.fab)
    public void AddStock() {
        if (checkNetwork()) {
            new MaterialDialog.Builder(this).title(R.string.symbol_search)
                    .content(R.string.content_test)
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input(R.string.input_hint, R.string.input_prefill, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            // On FAB click, receive user input. Make sure the stock doesn't already exist
                            // in the DB and proceed accordingly
                            Cursor c = getContentResolver().query(
                                    QuoteProvider.Quotes.CONTENT_URI,
                                    new String[]{QuoteColumns.SYMBOL},
                                    QuoteColumns.SYMBOL + "= ?",
                                    new String[]{input.toString().toUpperCase()},
                                    null);
                            if (c != null) {
                                c.close();
                            }
                            if (c != null && c.getCount() != 0) {
                                Toast toast = Toast.makeText(MyStocksActivity.this, "This stock is already saved!",
                                                Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, Gravity.CENTER, 0);
                                toast.show();
                            } else {
                                // Add the stock to DB
                                Intent stockIntentService = new Intent(MyStocksActivity.this,
                                        StockIntentService.class);
                                stockIntentService.putExtra(StockIntentService.SERVICE_TAG, StockIntentService.ACTION_ADD);
                                stockIntentService.putExtra(StockIntentService.SERVICE_SYMBOL, input.toString());
                                startService(stockIntentService);
                            }
                        }
                    })
                    .show();
        } else {
            networkToast();
        }
    }

}
