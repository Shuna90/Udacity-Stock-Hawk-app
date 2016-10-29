package com.sam_chordas.android.stockhawk.network;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class StocksQuery {
    private static final String LOG_TAG = StocksQuery.class.getName();
    @SerializedName("query")
    private Response mQuery;

    public List<Quote> getStockQuotes() {
        List<Quote> result = new ArrayList<>();
        List<Quote> stockQuotes = mQuery.getQuotes().getStockQuotes();
        for (Quote stockQuote : stockQuotes) {
            if (stockQuote.getBid() != null && stockQuote.getChangeInPercent() != null
                    && stockQuote.getChange() != null) {
                result.add(stockQuote);
            }
        }
        return result;
    }

    public class Response {

        @SerializedName("count")
        private int mCount;

        @SerializedName("results")
        private Quotes mQuotes;

        public Quotes getQuotes() {
            return mQuotes;
        }
    }

    public class Quotes {

        @SerializedName("quote")
        private List<Quote> mStockQuotes = new ArrayList<>();

        public List<Quote> getStockQuotes() {
            return mStockQuotes;
        }
    }

}
