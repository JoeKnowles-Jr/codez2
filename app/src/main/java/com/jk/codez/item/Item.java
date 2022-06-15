package com.jk.codez.item;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Entity(tableName = "items")
public class Item {

    public Item(@NonNull String id, Integer number, String street, String[] codes, String notes, Double lat, Double lng) {
        this._id = id;
        this.number = number;
        this.street = street;
        this.codes = codes;
        this.notes = notes;
        this.lat = lat;
        this.lng = lng;
    }

    @Ignore
    public Item(Integer number, String street, String[] codes, String notes, Double lat, Double lng) {
        this._id = String.valueOf(System.currentTimeMillis());
        this.number = number;
        this.street = street;
        this.codes = codes;
        this.notes = notes;
        this.lat = lat;
        this.lng = lng;
    }

    @Ignore
    public Item() {
        this._id = String.valueOf(System.currentTimeMillis());
        this.number = null;
        this.street = "";
        this.codes = null;
        this.notes = "";
        this.lat = null;
        this.lng = null;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%d %s - %d codes - %s\n%f - %f",
                number, street, codes.length, notes, lat, lng);
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String address) {
        this.street = address;
    }

    public String[] getCodes() {
        return codes;
    }

    public String getCodesString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < codes.length; ++ i) {
            sb.append(codes[i]);
            if (i < codes.length - 1) sb.append(" ");
        }
        return sb.toString();
    }

    public void setCodes(String[] codes) {
        this.codes = codes;
    }

    public void addCode(String code) {
        List<String> arrayList = Arrays.asList(this.codes);
        arrayList.add(code);
        this.codes = arrayList.toArray(this.codes);
    }

    public void deleteCode(String code) {
        List<String> arrayList = Arrays.asList(this.codes);
        arrayList.remove(code);
        this.codes = arrayList.toArray(this.codes);
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public Integer getNumber() {
        return number;
    }
    public void setNumber(Integer number) {
        this.number = number;
    }
    public String get_id() {
        return _id;
    }
    public void set_id(String _id) {
        this._id = _id;
    }
    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }
    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }
    public Boolean getPrecise() { return precise; }
    public void setPrecise(Boolean precise) { this.precise = precise; }

    @NonNull
    @PrimaryKey
    public String _id;
    Integer number;
    String street;
    String[] codes;
    String notes;
    public Double lat;
    public Double lng;
    public Boolean precise;
}
