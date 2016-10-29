package com.sam_chordas.android.stockhawk.rest;

import android.content.ContentProviderOperation;

import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteHistoryColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.network.HistoricalQuote;
import com.sam_chordas.android.stockhawk.network.Quote;

import java.util.Locale;

public class Utils {

    private static String LOG_TAG = Utils.class.getSimpleName();

    public static boolean showPercent = true;

    public static String truncateBidPrice(String bidPrice) {
        bidPrice = String.format(Locale.US, "%.2f", Float.parseFloat(bidPrice));
        return bidPrice;
    }

    public static String truncateChange(String change, boolean isPercentChange) {
        String weight = change.substring(0, 1);
        String ampersand = "";
        if (isPercentChange) {
            ampersand = change.substring(change.length() - 1, change.length());
            change = change.substring(0, change.length() - 1);
        }
        change = change.substring(1, change.length());
        double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
        change = String.format(Locale.US, "%.2f", round);
        StringBuilder changeBuffer = new StringBuilder(change);
        changeBuffer.insert(0, weight);
        changeBuffer.append(ampersand);
        change = changeBuffer.toString();
        return change;
    }

    public static ContentProviderOperation buildBatchOperation(Quote quote) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                QuoteProvider.Quotes.CONTENT_URI);
        String change = quote.getChange();
        builder.withValue(QuoteColumns.SYMBOL, quote.getSymbol());
        builder.withValue(QuoteColumns.NAME, quote.getName());
        builder.withValue(QuoteColumns.BID_PRICE, truncateBidPrice(quote.getBid()));
        builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
        builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(quote.getChangeInPercent(), true));
        builder.withValue(QuoteColumns.DAY_LOW, quote.getLow());
        builder.withValue(QuoteColumns.DAY_High, quote.getHigh());
        builder.withValue(QuoteColumns.YEAR_LOW, quote.getM52wkLow());
        builder.withValue(QuoteColumns.YEAR_HIGH, quote.getM52wkHigh());
        builder.withValue(QuoteColumns.OPEN_PRICE, quote.getOpen());
        builder.withValue(QuoteColumns.PREVIOUS_PRICE, quote.getPreviousClose());
        builder.withValue(QuoteColumns.ISUP, change.charAt(0) == '-' ? 0 : 1);
        builder.withValue(QuoteColumns.ISCURRENT, 1);
        return builder.build();
    }

    public static ContentProviderOperation buildBatchOperation(HistoricalQuote quote) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                QuoteProvider.H_QUOTES.CONTENT_URI);
        builder.withValue(QuoteHistoryColumns.SYMBOL, quote.getSymbol());
        builder.withValue(QuoteHistoryColumns.BIDPRICE, quote.getPrice());
        builder.withValue(QuoteHistoryColumns.DATE, quote.getDate());
        return builder.build();
    }
}
