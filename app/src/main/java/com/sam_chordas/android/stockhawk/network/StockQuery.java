package com.sam_chordas.android.stockhawk.network;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class StockQuery {
    private static final String LOG_TAG = StockQuery.class.getName();
    @SerializedName("query")
    private Response mQuery;

    public List<Quote> getStockQuotes() {
        List<Quote> result = new ArrayList<>();
        if (mQuery != null && mQuery.getQuotes() != null) {
            Quote stockQuote = mQuery.getQuotes().getStockQuotes();
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
        private Quote mStockQuotes;

        public Quote getStockQuotes() {
            return mStockQuotes;
        }
    }
}
