package com.jk.codez;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.jk.codez.item.Item;

import java.util.List;

@Dao
public interface CodezDao {

    @Insert
    void insertItem(Item item);
    @Query("SELECT * from items ORDER BY _id ASC")
    LiveData<List<Item>> getItems();
    @Update
    void updateItem(Item item);
    @Delete
    void deleteItem(Item item);
}
