package com.jk.codez;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.jk.codez.item.Item;

import java.util.ArrayList;
import java.util.List;

public class CodezRepository {
    private CodezDao mDao;
    private LiveData<List<Item>> mItems;

    CodezRepository(Application application) {
        CodezDb db = CodezDb.getDatabase(application);
        mDao = db.codezDao();
        mItems = mDao.getItems();
    }

    LiveData<List<Item>> getItems() {
        return mItems;
    }

    public void insert (Item item) {
        new insertAsyncTask(mDao).execute(item);
    }



    private static class insertAsyncTask extends AsyncTask<Item, Void, Void> {

        private final CodezDao mAsyncTaskDao;

        insertAsyncTask(CodezDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Item... params) {
            mAsyncTaskDao.insertItem(params[0]);
            return null;
        }
    }
}
