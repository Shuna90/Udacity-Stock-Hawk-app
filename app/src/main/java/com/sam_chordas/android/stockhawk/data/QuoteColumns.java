package com.sam_chordas.android.stockhawk.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

public class QuoteColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    public static final String _ID = "_id";
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String SYMBOL = "symbol";
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String NAME = "name";
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String BID_PRICE = "bid_price";
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String CHANGE = "change";
    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String PERCENT_CHANGE = "percent_change";
    @DataType(DataType.Type.TEXT)
    public static final String DAY_LOW = "day_low";
    @DataType(DataType.Type.TEXT)
    public static final String DAY_High = "day_High";
    @DataType(DataType.Type.TEXT)
    public static final String YEAR_LOW = "year_low";
    @DataType(DataType.Type.TEXT)
    public static final String YEAR_HIGH = "year_high";
    @DataType(DataType.Type.TEXT)
    public static final String OPEN_PRICE = "open_price";
    @DataType(DataType.Type.TEXT)
    public static final String PREVIOUS_PRICE = "previous_price";
    @DataType(DataType.Type.TEXT)
    public static final String CREATED = "created";
    @DataType(DataType.Type.INTEGER)
    @NotNull
    public static final String ISUP = "is_up";
    @DataType(DataType.Type.INTEGER)
    @NotNull
    public static final String ISCURRENT = "is_current";


}
