package com.sam_chordas.android.stockhawk.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.sam_chordas.android.stockhawk.R;


public class StockDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        if (savedInstanceState == null){
            Bundle arguments = new Bundle();
            String symbol = getIntent().getExtras().getString(StockDetailActivityFragment.STOCK_DETAIL);
            arguments.putString(StockDetailActivityFragment.STOCK_DETAIL, symbol);
            if (actionBar != null)
                actionBar.setTitle(symbol);
            StockDetailActivityFragment fragment = new StockDetailActivityFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.stock_detail_container, fragment)
                    .commit();
        }
    }
}
