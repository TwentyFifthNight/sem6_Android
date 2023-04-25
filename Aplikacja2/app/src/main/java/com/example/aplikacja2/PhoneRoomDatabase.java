package com.example.aplikacja2;

//klasa musi być abstrakcyjna
//adnotacja określa: klasy odpowiadające tabelom bazy (tablica
//entities), wersję bazy danych
//(dzięki temu klasa w uruchomi odpowiednią migracją danych),
//exportSchema = false – eliminuje
//ostrzeżenie w takcie budowania

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Phone.class}, version = 1, exportSchema = false)
public abstract class PhoneRoomDatabase extends RoomDatabase {
    //abstrakcyjna metoda zwracająca DAO
    public abstract ElementDao elementDao();

    //implementacja singletona
    private static volatile PhoneRoomDatabase INSTANCE;

    static PhoneRoomDatabase getDatabase(final Context context) {
        //tworzymy nowy obiekt tylko gdy żaden nie istnieje
        if (INSTANCE == null) {
            synchronized (PhoneRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), PhoneRoomDatabase.class, "localDB")
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static final RoomDatabase.Callback
            sRoomDatabaseCallback = new RoomDatabase.Callback() {
        //uruchamiane przy tworzeniu bazy (pierwsze
        //uruchomienie aplikacji, gdy baza nie istnieje)

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            //wykonanie operacji w osobnym wątku. Parametrem
            // metody execute() jest obiekt implementujący
            // interfejs Runnable, może być zastąpiony
            // wyrażeniem lambda
            databaseWriteExecutor.execute(() -> {
                ElementDao dao = INSTANCE.elementDao();
                //tworzenie elementów (obiektów klasy Element)
                //i dodawanie ich do bazy
                //za pomocą metody insert() z obiektu dao
                //tutaj możemy określić początkową zawartość
                //bazy danych
                //...
                dao.insert(new Phone("Pixel 4", "Google", "10", "store.google.com"));
                dao.insert(new Phone("Pixel 2", "Google", "9", "store.google.com"));
                dao.insert(new Phone("Pixel 7", "Google", "12", "google.com"));
                dao.insert(new Phone("Pixel 8", "Google", "10", "google.com"));
                dao.insert(new Phone("Pixel 3", "Google", "6", "google.com"));
            });
        }
    };
}
