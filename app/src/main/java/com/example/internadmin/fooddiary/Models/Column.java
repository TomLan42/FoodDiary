package com.example.internadmin.fooddiary.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class to pass the column name and column value from activity
 * to recyclerview for rendering on the chart.
 */

public class Column implements Parcelable {

    private String colname;
    private float colval;

    public Column(String colname, float colval){
        this.colname = colname;
        this.colval = colval;
    }

    public String getColName(){
        return colname;
    }

    public float getColVal(){
        return colval;
    }

    protected Column(Parcel in) {
        colname = in.readString();
        colval = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(colname);
        dest.writeFloat(colval);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Column> CREATOR = new Parcelable.Creator<Column>() {
        @Override
        public Column createFromParcel(Parcel in) {
            return new Column(in);
        }

        @Override
        public Column[] newArray(int size) {
            return new Column[size];
        }
    };
}