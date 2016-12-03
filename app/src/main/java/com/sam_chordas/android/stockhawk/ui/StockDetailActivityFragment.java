package com.sam_chordas.android.stockhawk.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteHistoryColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class StockDetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        TabHost.OnTabChangeListener {

    private static final String LOG_TAG = StockDetailActivityFragment.class.getName();
    public static final String STOCK_DETAIL = "STOCK_DETAIL";
    private static final String TAB = "TAB";
    private String mSymbol;
    private String mSelectedTab;

    private static final int CURSOR_LOADER_ID_INFO = 1;
    private static final int CURSOR_LOADER_ID_CHART = 2;

    @BindView(R.id.stock_symbol)
    TextView stock_symbol;
    @BindView(R.id.stock_bid_price)
    TextView stock_price;
    @BindView(R.id.info_change)
    TextView stock_change;
    @BindView(R.id.key_statistics_prev_close_stock)
    TextView key_statistics_preClose;
    @BindView(R.id.key_statistics_open_stock)
    TextView key_statistics_open;
    @BindView(R.id.key_statistics_Low_stock)
    TextView key_statistics_low;
    @BindView(R.id.key_statistics_High_stock)
    TextView key_statistics_high;
    @BindView(R.id.key_statistics_52wkLow_stock)
    TextView key_statistics_52low;
    @BindView(R.id.key_statistics_52wkHigh_stock)
    TextView key_statistics_52high;
    /*
    @BindView(R.id.key_statistics_volume_stock)
    TextView key_statistics_volume;
    @BindView(R.id.key_statistics_Average_volume_stock)
    TextView key_statistics_avg_volume;
    */
    @BindView(R.id.chart)
    LineChart chart;
    @BindView(android.R.id.tabhost)
    TabHost tabHost;

    public StockDetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null){
            mSymbol = arguments.getString(STOCK_DETAIL);
        }

        if (savedInstanceState != null && savedInstanceState.getString(TAB) != null) {
            mSelectedTab = savedInstanceState.getString(TAB);
            Log.d(LOG_TAG, "onCreate tab 30days");
        } else {
            mSelectedTab = getString(R.string.tab1_14d);
            Log.d(LOG_TAG, "onCreate tab 14days");
        }
        getLoaderManager().initLoader(CURSOR_LOADER_ID_INFO, null, this);
        getLoaderManager().initLoader(CURSOR_LOADER_ID_CHART, null, this);
        Log.d(LOG_TAG, "onActivityCreated LOADERMANAGER INIT");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stock_detail, container, false);
        ButterKnife.bind(this, rootView);
        setupTabs();
        return rootView;
    }

    private void setupTabs() {
        tabHost.setup();

        TabHost.TabSpec tabSpec;
        tabSpec = tabHost.newTabSpec(getString(R.string.tab1_14d));
        tabSpec.setIndicator(getString(R.string.tab1_14d));
        tabSpec.setContent(android.R.id.tabcontent);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec(getString(R.string.tab2_1m));
        tabSpec.setIndicator(getString(R.string.tab2_1m));
        tabSpec.setContent(android.R.id.tabcontent);
        tabHost.addTab(tabSpec);

        /*
        tabSpec = tabHost.newTabSpec(getString(R.string.tab3_2m));
        tabSpec.setIndicator(getString(R.string.tab3_2m));
        tabSpec.setContent(android.R.id.tabcontent);
        tabHost.addTab(tabSpec);
*/

        if (mSelectedTab.equals(getString(R.string.tab1_14d))) {
            tabHost.setCurrentTab(0);
        } else {
            tabHost.setCurrentTab(1);
        }
        tabHost.setOnTabChangedListener(this);
        Log.d(LOG_TAG, "setupTabs");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TAB, mSelectedTab);
        Log.d(LOG_TAG, "onSaveInstanceState" + mSelectedTab);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        CursorLoader cursorLoader;
        switch (i){
            case CURSOR_LOADER_ID_INFO:
                cursorLoader = new CursorLoader(getActivity(),
                        QuoteProvider.Quotes.CONTENT_URI,
                        null,
                        QuoteColumns.SYMBOL + " = \"" + mSymbol + "\" AND " + QuoteColumns.ISCURRENT + " = 1",
                        null,
                        null
                        );
                break;
            case CURSOR_LOADER_ID_CHART:
                String sortOrder = QuoteColumns._ID + " ASC";
                cursorLoader = new CursorLoader(getActivity(),
                        QuoteProvider.H_QUOTES.CONTENT_URI,
                        null,
                        QuoteColumns.SYMBOL + " = \"" + mSymbol + "\"",
                        null,
                        sortOrder);
                Log.d(LOG_TAG, "onCreateLoader CURSOR_LOADER_ID_CHART");
                break;
            default:
                throw new IllegalStateException();
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            switch (loader.getId()){
                case CURSOR_LOADER_ID_INFO:
                    if (cursor != null && cursor.moveToFirst()) {
                        String name = cursor.getString(cursor.getColumnIndex(QuoteColumns.NAME));
                        String title = getActivity().getString(R.string.info_title, name, mSymbol);
                        stock_symbol.setText(title);
                        stock_price.setText(cursor.getString(cursor.getColumnIndex(QuoteColumns.BID_PRICE)));
                        String change = cursor.getString(cursor.getColumnIndex(QuoteColumns.CHANGE));
                        String change_percent = cursor.getString(cursor.getColumnIndex(QuoteColumns.PERCENT_CHANGE));
                        String change_total = getActivity().getString(R.string.info_change, change, change_percent);
                        stock_change.setText(change_total);
                        if (change.charAt(0) == '+'){
                            stock_change.setTextColor(Color.GREEN);
                        }else{
                            stock_change.setTextColor(Color.RED);
                        }
                        key_statistics_preClose.setText(cursor.getString(cursor.getColumnIndex(QuoteColumns.OPEN_PRICE)));
                        key_statistics_open.setText(cursor.getString(cursor.getColumnIndex(QuoteColumns.BID_PRICE)));
                        key_statistics_low.setText(cursor.getString(cursor.getColumnIndex(QuoteColumns.DAY_LOW)));
                        key_statistics_high.setText(cursor.getString(cursor.getColumnIndex(QuoteColumns.DAY_High)));
                        key_statistics_52low.setText(cursor.getString(cursor.getColumnIndex(QuoteColumns.YEAR_LOW)));
                        key_statistics_52high.setText(cursor.getString(cursor.getColumnIndex(QuoteColumns.YEAR_HIGH)));
                    }
                    break;
                case CURSOR_LOADER_ID_CHART:
                    if(cursor != null && cursor.moveToFirst()){
                        chart(cursor);
                    }
                    break;
                default:
                    throw new IllegalStateException();
            }
        }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void chart(final Cursor cursor){
        ArrayList<Entry> entries = new ArrayList<>();
        final ArrayList<String> labels = new ArrayList<>();

        float first = 0;
        float last = 0;
        int count = 14;
        if (mSelectedTab.equals(getString(R.string.tab2_1m))){
            count = cursor.getCount();
        }
        for (int i = 0; i < count; i++){
            String day = cursor.getString(cursor.getColumnIndex(QuoteHistoryColumns.DATE));
            labels.add(day);

            String price = cursor.getString(cursor.getColumnIndex(QuoteHistoryColumns.BIDPRICE));
            if (i == 0){
                last = Float.parseFloat(price);
            }else if (i == count - 1){
                first = Float.parseFloat(price);
            }
            Entry newEntry = new Entry(count - 1 - i, Float.valueOf(price));
            entries.add(newEntry);

            cursor.moveToNext();
        }
        Collections.reverse(labels);
        Collections.sort(entries, new Comparator<Entry>() {
            @Override
            public int compare(Entry e1, Entry e2) {
                return (int)e1.getX() - (int)e2.getX();
            }
        });
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(7f);
        xAxis.setLabelCount(6,false);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return labels.get((int)value);
            }

            // we don't draw numbers, so no decimal digits needed
            @Override
            public int getDecimalDigits() {  return 0; }
        };
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setEnabled(true);
        yAxis.setLabelCount(5, true);
        yAxis.setTextColor(Color.WHITE);

        LineDataSet dataSet = new LineDataSet(entries, "Prices of Stock"); // add entries to dataset
        dataSet.setDrawCircles(false);
        if (last >= first){
            dataSet.setColor(Color.GREEN);
        }else{
            dataSet.setColor(Color.RED);
        }

        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
        chart.getAxisRight().setEnabled(false);
        //mLineChart.getLegend().setTextSize(12f);
        lineData.setDrawValues(false);

        chart.setData(lineData);
        chart.animateX(1);
        chart.setBackgroundColor(Color.TRANSPARENT);
        //chart.setBackgroundColor(Color.GRAY);
        chart.setDrawGridBackground(false);
        Log.d(LOG_TAG, "chart " + mSelectedTab);
    }

    @Override
    public void onTabChanged(String s) {
        mSelectedTab = s;
        getLoaderManager().restartLoader(CURSOR_LOADER_ID_CHART, null, this);
        Log.d(LOG_TAG, "onTabChanged restartLoader " + mSelectedTab);
    }
}
