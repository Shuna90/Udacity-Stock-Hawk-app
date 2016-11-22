package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.os.Bundle;

import com.sam_chordas.android.stockhawk.R;


public class StockDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        if (savedInstanceState == null){
            Bundle arguments = new Bundle();
            arguments.putString(StockDetailActivityFragment.STOCK_DETAIL,
                    getIntent().getExtras().getString(StockDetailActivityFragment.STOCK_DETAIL));

            StockDetailActivityFragment fragment = new StockDetailActivityFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.stock_detail_container, fragment)
                    .commit();
        }
    }

}
