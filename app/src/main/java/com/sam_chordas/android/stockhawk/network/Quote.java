package com.sam_chordas.android.stockhawk.network;

import com.google.gson.annotations.SerializedName;

public class Quote {

    @SerializedName("symbol")
    private String mSymbol;

    @SerializedName("Name")
    private String mName;

    @SerializedName("Bid")
    private String mBid;

    @SerializedName("Change")
    private String mChange;

    @SerializedName("ChangeinPercent")
    private String mChangeInPercent;

    @SerializedName("DaysLow")
    private String mLow;

    @SerializedName("DaysHigh")
    private String mHigh;

    @SerializedName("YearLow")
    private String m52wkLow;

    @SerializedName("YearHigh")
    private String m52wkHigh;

    @SerializedName("Open")
    private String mOpen;

    @SerializedName("PreviousClose")
    private String mPreviousClose;

    public String getSymbol() {
        return mSymbol;
    }

    public String getName() {
        return mName;
    }

    public String getBid() {
        return mBid;
    }

    public String getChange() {
        return mChange;
    }

    public String getChangeInPercent() {
        return mChangeInPercent;
    }

    public String getLow(){
        return mLow;
    }

    public String getHigh(){
        return mHigh;
    }

    public String getM52wkLow(){
        return m52wkLow;
    }

    public String getM52wkHigh(){
        return m52wkHigh;
    }

    public String getOpen(){
        return mOpen;
    }

    public String getPreviousClose(){
        return mPreviousClose;
    }
}
