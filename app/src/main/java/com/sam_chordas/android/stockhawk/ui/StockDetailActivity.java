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
            getFragmentManager().beginTransaction()
                    .replace(R.id.stock_detail_container, new StockDetailActivityFragment())
                    .commit();
        }
    }

}
