package com.sam_chordas.android.stockhawk.network;

import com.google.gson.annotations.SerializedName;

public class HistoricalQuote {

    @SerializedName("Symbol")
    private String mSymbol;

    @SerializedName("Date")
    private String mDate;

    @SerializedName("Open")
    private String mPrice;

    public String getSymbol() {
        return mSymbol;
    }

    public String getDate() {
        return mDate;
    }

    public String getPrice() {
        return mPrice;
    }
}
