package com.example.aplikacja4;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Image implements Parcelable {
    private long id;
    private String name;

    public Image(long id, String name) {
        this.id = id;
        this.name = name;
    }

    protected Image(Parcel parcel){
        this.id = parcel.readLong();
        this.name = parcel.readString();
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel parcel) {
            return new Image(parcel);
        }

        @Override
        public Image[] newArray(int i) {
            return new Image[i];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "Image{ id=" + this.id +
                ", name='" + this.name + "'" + '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeLong(this.id);
        parcel.writeString(this.name);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
