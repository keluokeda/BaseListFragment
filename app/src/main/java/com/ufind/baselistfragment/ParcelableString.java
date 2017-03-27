package com.ufind.baselistfragment;

import android.os.Parcel;
import android.os.Parcelable;



public class ParcelableString implements Parcelable {
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ParcelableString(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
    }

    public ParcelableString() {
    }

    protected ParcelableString(Parcel in) {
        this.content = in.readString();
    }

    public static final Parcelable.Creator<ParcelableString> CREATOR = new Parcelable.Creator<ParcelableString>() {
        @Override
        public ParcelableString createFromParcel(Parcel source) {
            return new ParcelableString(source);
        }

        @Override
        public ParcelableString[] newArray(int size) {
            return new ParcelableString[size];
        }
    };
}
