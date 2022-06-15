package com.jk.codez;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jk.codez.ad.AestheticDialog;
import com.jk.codez.item.Item;
import com.loopj.android.http.TextHttpResponseHandler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class CodezViewModel extends AndroidViewModel {
    private final CodezRepository mRepo;
    MutableLiveData<ArrayList<Item>> remoteItems = new MutableLiveData<>();
    private boolean mLocationPermissionGranted;
    private Location mCurrentLocation;
    private String mCurrentConnection;

    Comparator<Item> sortByNumber = Comparator.comparing(Item::getNumber);
    Comparator<Item> sortByStreet = Comparator.comparing(Item::getStreet);
    Comparator<Item> sortByCodes = Comparator.comparing(Item::getCodesString);

    public CodezViewModel(Application application) {
        super(application);
        mRepo = new CodezRepository(application);
        mCurrentConnection = Settings.getLastConnection();
        if (mCurrentConnection != null)
            Network.setUrl(mCurrentConnection);
    }

    public boolean iHavePermission() {
        return mLocationPermissionGranted;
    }

    public void setPermissionStatus(boolean granted) {
        mLocationPermissionGranted = granted;
    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    public void setCurrentLocation(Location location) {
        mCurrentLocation = location;
    }

    public LiveData<List<Item>> getLocalItems() { return mRepo.getItems(); }

    public void setConnection(String conn) {
        Settings.setLastConnection(conn);
        Network.setUrl(conn);
        refreshRemoteItems();
    }

    public MutableLiveData<ArrayList<Item>> getRemoteItems() {
        if (this.remoteItems.getValue() == null) refreshRemoteItems();
        return this.remoteItems;
    }

    public void sortListByNumber(boolean searchReverse) {
        ArrayList<Item> itemList = remoteItems.getValue();
        Objects.requireNonNull(itemList).sort(searchReverse ? sortByNumber.reversed() : sortByNumber);
        remoteItems.postValue(itemList);
    }

    public void sortListByStreet(boolean searchReverse) {
        ArrayList<Item> itemList = remoteItems.getValue();
        Objects.requireNonNull(itemList).sort(searchReverse ? sortByStreet.reversed() : sortByStreet);
        remoteItems.postValue(itemList);
    }

    private void refreshRemoteItems() {
        Network.getItems(new TextHttpResponseHandler() {
            @Override
            public void onSuccess(final int statusCode, final Header[] headers, final String responseString) {
                System.out.println(responseString);
                Type type = new TypeToken<ArrayList<Item>>() {}.getType();
                CodezViewModel.this.remoteItems.postValue(new Gson().fromJson(responseString, type));
            }

            @Override
            public void onFailure(final int statusCode, final Header[] headers, final String responseString, final Throwable throwable) {
                System.out.println("Failed!");
            }
        });
    }

    public void addRemoteItem(Item i, AestheticDialog.Builder dialog) {
        Network.addItem(i, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                System.out.println("NOT Added!");

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Type type = new TypeToken<ArrayList<Item>>() {}.getType();
                CodezViewModel.this.remoteItems.setValue(new Gson().fromJson(responseString, type));
                dialog.dismiss();
            }
        });
    }

    public void editRemoteItem(Item i, @NonNull AestheticDialog.Builder dialog) {
        System.out.println("vm - editRemoteItem");
        Network.editItem(dialog.getItem(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                System.out.println("NOT Updated!");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                System.out.println("Updated!");
                Type type = new TypeToken<ArrayList<Item>>() {}.getType();
                CodezViewModel.this.remoteItems.setValue(new Gson().fromJson(responseString, type));
                dialog.dismiss();
            }
        });
    }

    public void deleteRemoteItem(@NonNull AestheticDialog.Builder dialog) {
        System.out.println("vm - deleteRemoteItem");
        Network.deleteItem(dialog.getItem()._id, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                System.out.println("NOT Deleted!");

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                System.out.println("Deleted!");
                Type type = new TypeToken<ArrayList<Item>>() {}.getType();
                CodezViewModel.this.remoteItems.setValue(new Gson().fromJson(responseString, type));
                dialog.dismiss();
            }
        });
    }

    public void addLocalItem(Item i, @NonNull AestheticDialog.Builder dialog) {
        mRepo.insert(dialog.getItem());
        dialog.dismiss();
    }

    public void editLocalItem(Item i, AestheticDialog.Builder dialog) {
        
    }

    public void deleteLocalItem(AestheticDialog.Builder dialog) {

    }
}
