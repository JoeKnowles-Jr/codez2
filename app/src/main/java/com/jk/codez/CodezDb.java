package com.jk.codez;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.jk.codez.item.Item;

@Database(entities = {Item.class}, version = 1, exportSchema = false)
@TypeConverters({ StringArrayConverter.class })
public abstract class CodezDb extends RoomDatabase {
    public abstract CodezDao codezDao();
    private static volatile CodezDb INSTANCE;

    static CodezDb getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CodezDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    CodezDb.class, "word_database")
                            // Wipes and rebuilds instead of migrating
                            // if no Migration object.
                            // Migration is not part of this practical.
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
