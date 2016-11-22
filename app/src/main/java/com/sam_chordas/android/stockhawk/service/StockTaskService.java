package com.sam_chordas.android.stockhawk.service;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteHistoryColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.network.HistoricalQuote;
import com.sam_chordas.android.stockhawk.network.Quote;
import com.sam_chordas.android.stockhawk.network.StockHistoricQuery;
import com.sam_chordas.android.stockhawk.network.StockQuery;
import com.sam_chordas.android.stockhawk.network.StockServices;
import com.sam_chordas.android.stockhawk.network.StocksQuery;
import com.sam_chordas.android.stockhawk.rest.Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by sam_chordas on 9/30/15.
 * The GCMTask service is primarily for periodic tasks. However, OnRunTask can be called directly
 * and is used for the initialization and adding task as well.
 */
public class StockTaskService extends GcmTaskService {
    private String LOG_TAG = StockTaskService.class.getSimpleName();
    public static final String PERIOD_TAG = "periodic";
    private static final String INIT_SYMBOL = "\"YHOO\",\"AAPL\",\"GOOG\",\"MSFT\"";
    private static final String URL_QUERY_QUOTES = "select * from yahoo.finance.quotes where symbol in (";
    private static final String URL_QUERY_HIST_QUOTES = "select * from yahoo.finance.historicaldata where symbol= \"";
    private Context mContext;
    private StringBuilder mStoredSymbols = new StringBuilder();
    private boolean isUpdate;
    private boolean oneCount;

    public StockTaskService() {
    }

    public StockTaskService(Context context) {
        mContext = context;
    }

    @Override
    public int onRunTask(TaskParams params) {
        if (mContext == null) {
            mContext = this;
        }

        int result = GcmNetworkManager.RESULT_FAILURE;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(StockServices.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        StockServices service = retrofit.create(StockServices.class);
        String query = URL_QUERY_QUOTES + urlBuilder(params) + ")";
        //Log.d(LOG_TAG, query);

        int code = 200;
        try {
            //just add one stock or update one stock
            if (oneCount) {
                Call<StockQuery> call = service.getStock(query);
                Response<StockQuery> response = call.execute();
                code = response.code();
                StockQuery responseGetStock = response.body();
                saveDatabase(responseGetStock.getStockQuotes());
            } else {
                Call<StocksQuery> call = service.getStocks(query);
                Response<StocksQuery> response = call.execute();
                code = response.code();
                StocksQuery responseGetStocks = response.body();
                saveDatabase(responseGetStocks.getStockQuotes());
            }
        } catch (NullPointerException e) {
            catchErrorCode(code);
        }catch (IOException | RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void catchErrorCode(int code){
        Toast toast = null;
        if (code == 401) {
            toast = Toast.makeText(mContext, "Unauthenticated", Toast.LENGTH_SHORT);
            Log.d(LOG_TAG, "401");
        } else if (code >= 400 && code < 500) {
            toast = Toast.makeText(mContext, "Client Error " + code, Toast.LENGTH_SHORT);
            Log.d(LOG_TAG, "400");
        }else if (code >= 500){
            toast = Toast.makeText(mContext, "Server Error " + code, Toast.LENGTH_SHORT);
            Log.d(LOG_TAG, "500");
        }
        if (toast != null){
            toast.show();
        }
    }

    private String urlBuilder(TaskParams params){
        ContentResolver resolver = mContext.getContentResolver();
        oneCount = false;
        if (params.getTag().equals(StockIntentService.ACTION_INIT) || params.getTag().equals(PERIOD_TAG)) {
            isUpdate = true;
            Cursor cursor = resolver.query(
                    QuoteProvider.Quotes.CONTENT_URI,
                    new String[]{"Distinct " + QuoteColumns.SYMBOL},
                    null,
                    null,
                    null);

            if (cursor != null && cursor.getCount() == 0 || cursor == null) {
                // Init task. Populates DB with quotes for the symbols seen below
                //Log.d(LOG_TAG, "INIT");
                return INIT_SYMBOL;
            } else {
                DatabaseUtils.dumpCursor(cursor);
                cursor.moveToFirst();
                if (cursor.getCount() == 1){
                    oneCount = true;
                }
                for (int i = 0; i < cursor.getCount(); i++) {
                    mStoredSymbols.append("\"");
                    mStoredSymbols.append(cursor.getString(
                            cursor.getColumnIndex(QuoteColumns.SYMBOL)));
                    mStoredSymbols.append("\",");
                    cursor.moveToNext();
                }
                mStoredSymbols.replace(mStoredSymbols.length() - 1, mStoredSymbols.length(), "");
                return mStoredSymbols.toString();
            }
        } else if (params.getTag().equals(StockIntentService.ACTION_ADD)) {
            oneCount = true;
            isUpdate = false;
            // Get symbol from params.getExtra and build query
            String stockInput = params.getExtras().getString(StockIntentService.SERVICE_SYMBOL).toUpperCase();
            return "\"" + stockInput + "\"";
        } else {
            throw new IllegalStateException("Action not specified in TaskParams.");
        }
    }

    private void saveDatabase(List<Quote> quotes) throws RemoteException, OperationApplicationException{
        ContentResolver resolver = mContext.getContentResolver();

        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
        for (Quote quote : quotes) {
            batchOperations.add(Utils.buildBatchOperation(quote));
        }
        //Log.d(LOG_TAG, batchOperations.size() + "");

        if (isUpdate) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(QuoteColumns.ISCURRENT, 0);
            resolver.update(QuoteProvider.Quotes.CONTENT_URI,
                    contentValues,
                    null,
                    null);
        }

        resolver.applyBatch(QuoteProvider.AUTHORITY, batchOperations);

        for (Quote quote : quotes) {
            loadHistoricalData(quote);
        }
    }

    private void loadHistoricalData(Quote quote) throws RemoteException, OperationApplicationException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date currentDate = new Date();

        Calendar calStart = Calendar.getInstance();
        calStart.setTime(currentDate);
        calStart.add(Calendar.MONTH, -1);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(currentDate);

        String startDate = dateFormat.format(calStart.getTime());
        String endDate = dateFormat.format(calEnd.getTime());

        String query = URL_QUERY_HIST_QUOTES + quote.getSymbol()
                + "\" and startDate=\"" + startDate + "\" and endDate=\"" + endDate + "\"";

        int code = 200;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(StockServices.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        StockServices service = retrofit.create(StockServices.class);
        Call<StockHistoricQuery> call = service.getHistoricalData(query);
        Response<StockHistoricQuery> response;
        StockHistoricQuery responseGetHistoricalData = null;
        try {
            response = call.execute();
            code = response.code();
            responseGetHistoricalData = response.body();
        } catch (NullPointerException e) {
            catchErrorCode(code);
        }catch (IOException e) {
            e.printStackTrace();
        }

        if (responseGetHistoricalData != null) {
            saveHistoricalDatabase(responseGetHistoricalData.getHistoricData());
        }
    }

    private void saveHistoricalDatabase(List<HistoricalQuote> quotes)
            throws RemoteException, OperationApplicationException {
        ContentResolver resolver = mContext.getContentResolver();
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
        for (HistoricalQuote quote : quotes) {
            resolver.delete(QuoteProvider.H_QUOTES.CONTENT_URI,
                    QuoteHistoryColumns.SYMBOL + " = \"" + quote.getSymbol() + "\"",
                    null);
            batchOperations.add(Utils.buildBatchOperation(quote));
        }

        resolver.applyBatch(QuoteProvider.AUTHORITY, batchOperations);
    }

}
