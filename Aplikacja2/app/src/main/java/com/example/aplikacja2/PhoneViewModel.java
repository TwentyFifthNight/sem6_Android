package com.example.aplikacja2;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class PhoneViewModel extends AndroidViewModel {

    private final PhoneRepository mRepository;
    private final LiveData<List<Phone>> mAllElements;

    public PhoneViewModel(@NonNull Application application)
    {
        super(application);
        mRepository = new PhoneRepository(application);
        mAllElements = mRepository.getAllElements();
    }

    LiveData<List<Phone>> getAllPhones() {
        //… zwrócenie wszystkich elementów
        return mAllElements;
    }

    public void deleteAll() {
        //… skasowanie wszystkich elementów z repozytorium
        mRepository.deleteAll();
    }

    public void insert(Phone phone){
        mRepository.insert(phone);
    }

    public void delete(Phone phone){
        mRepository.delete(phone);
    }

    public void update(Phone phone){
        mRepository.update(phone);
    }
}
