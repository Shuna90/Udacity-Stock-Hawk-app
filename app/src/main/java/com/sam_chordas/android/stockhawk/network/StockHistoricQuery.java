package com.sam_chordas.android.stockhawk.network;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class StockHistoricQuery {
    @SerializedName("query")
    private Response mQuery;

    public List<HistoricalQuote> getHistoricData() {
        List<HistoricalQuote> result = new ArrayList<>();
        if (mQuery.getQuote() != null) {
            List<HistoricalQuote> quotes = mQuery.getQuote().getStockQuotes();
            for (HistoricalQuote quote : quotes) {
                result.add(quote);
            }
        }
        return result;
    }

    public class Response {

        @SerializedName("count")
        private String mCount;

        @SerializedName("results")
        private Quotes mQuote;

        public Quotes getQuote() {
            return mQuote;
        }
    }

    public class Quotes {

        @SerializedName("quote")
        private List<HistoricalQuote> mStockQuotes = new ArrayList<>();

        public List<HistoricalQuote> getStockQuotes() {
            return mStockQuotes;
        }
    }
}
