package com.example.aplikacja2;

import android.app.Application;
import androidx.lifecycle.LiveData;

import java.util.List;

public class PhoneRepository {

    private final ElementDao mElementDao;
    private final LiveData<List<Phone>> mAllElements;

    PhoneRepository(Application application) {
        PhoneRoomDatabase phoneRoomDatabase = PhoneRoomDatabase.getDatabase(application);
        //repozytorium korzysta z obiektu DAO do odwołań
        //do bazy
        mElementDao = phoneRoomDatabase.elementDao();
        mAllElements = mElementDao.getAlphabetizedElements();
    }

    LiveData<List<Phone>> getAllElements() {
        //… metdoda zwraca wszystkie elementy
        return mAllElements;
    }

    void deleteAll() {
        //… skasowanie wszystkich elementów za pomocą
        //obiektuDAO
        PhoneRoomDatabase.databaseWriteExecutor.execute(mElementDao::deleteAll);
    }

    void insert(Phone phone){
        PhoneRoomDatabase.databaseWriteExecutor.execute(() -> mElementDao.insert(phone));
    }

    void delete(Phone phone){
        PhoneRoomDatabase.databaseWriteExecutor.execute(() -> mElementDao.delete(phone));
    }

    void update(Phone phone){
        PhoneRoomDatabase.databaseWriteExecutor.execute(() -> mElementDao.update(phone));
    }

}
