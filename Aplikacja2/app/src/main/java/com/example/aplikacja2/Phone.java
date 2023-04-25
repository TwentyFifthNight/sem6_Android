package com.example.aplikacja2;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

//adnotacja oznacza, że klasa reprezentuje tabelę w bazie
@Entity(tableName = "Phones")
public class Phone {

    //pierwsza adnotacja oznacza, że pole jest kluczem głównym, wartości klucza będą generowane
    //automatycznie, druga adnotacja określa, jaka będzie nazwa kolumny w tabeli
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @NonNull
    @ColumnInfo(name = "manufacturer")
    private String manufacturer;
    //pierwsza adnotacja oznacza, że pole nie może być puste
    //druga adnotacja określa, jaka będzie nazwa kolumny w tabeli
    @NonNull
    @ColumnInfo(name = "model")
    private String model;

    @NonNull
    @ColumnInfo(name = "version")
    private String version;

    @NonNull
    @ColumnInfo(name = "website")
    private String website;

    //konstruktor wykorzystywany przez Room do tworzenia obiektów

    public Phone(@NonNull String model, @NonNull String manufacturer, @NonNull String version, @NonNull String website) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.version = version;
        this.website = website;
    }

    @Ignore
    public Phone(@NonNull Long id, @NonNull String model, @NonNull String manufacturer, @NonNull String version, @NonNull String website) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.model = model;
        this.version = version;
        this.website = website;
    }

    //jeżeli konieczne są dodatkowe konstruktory należy je poprzedzić adnotacją @Ignore
    //żeby biblioteka Room z nich nie korzystała

    //Room może wymagać również getterów i setterów także warto je utworzyć

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(@NonNull String manufacturer) {
        this.manufacturer = manufacturer;
    }

    @NonNull
    public String getModel() {
        return model;
    }

    public void setModel(@NonNull String model) {
        this.model = model;
    }

    @NonNull
    public String getVersion() {
        return version;
    }

    public void setVersion(@NonNull String version) {
        this.version = version;
    }

    @NonNull
    public String getWebsite() {
        return website;
    }

    public void setWebsite(@NonNull String website) {
        this.website = website;
    }
}
