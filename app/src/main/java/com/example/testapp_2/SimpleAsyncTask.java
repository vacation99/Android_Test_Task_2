package com.example.testapp_2;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.testapp_2.db.Info;
import com.example.testapp_2.pojo.Object;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class SimpleAsyncTask extends AsyncTask<Void, Void, Void> {

    private ArrayList<Info> arrayList = new ArrayList<>(StaticParams.amount);
    private final RecyclerViewAdapter recyclerViewAdapter;
    private final WeakReference<Context> contextWeakReference;

    // Если false, значит интернет есть и полученные данные подгрузятся в БД (старые данные удалятся)
    // Если true, значит интернета нет и данные загрузятся из БД с последней сохранённой сессии
    private boolean offline = false;

    Realm realm;

    public SimpleAsyncTask(RecyclerViewAdapter recyclerViewAdapter, Context context) {
        this.recyclerViewAdapter = recyclerViewAdapter;
        this.contextWeakReference = new WeakReference<>(context);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        // Получение данных и сохранение в arrayList

        try {
            URL url = new URL("https://api.twitch.tv/kraken/games/top?limit=" + StaticParams.amount);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept", "application/vnd.twitchtv.v5+json");
            urlConnection.setRequestProperty("Client-ID", "sd4grh0omdj9a31exnpikhrmsu3v46");
            urlConnection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            Object object = new Gson().fromJson(stringBuilder.toString(), Object.class);

            for (int i = 0; i < object.getTop().size(); i++) {
                Info info = new Info();

                info.setImage(object.getTop().get(i).getGame().getBox().getLarge());
                info.setName(object.getTop().get(i).getGame().getName());
                info.setChannels(object.getTop().get(i).getChannels());
                info.setViewers(Integer.parseInt(object.getTop().get(i).getViewers()));

                arrayList.add(info);
            }

        } catch (Exception e) {
            offline = true;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        if (offline) {

            // Загрузка данных из БД

            loadDB();
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<Info> realmResults = realm.where(Info.class).findAll();

                    for (Info info : realmResults) {
                        arrayList.add(new Info(info.getName(), info.getImage(), info.getViewers(), info.getChannels()));
                    }

                    sort(arrayList);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    recyclerViewAdapter.setItems(arrayList);
                    StaticParams.amount = 100;

                    Log.d("check", "Offline load");
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    Log.d("check", "Offline not load");
                }
            });
        } else {

            // Удаление старых записей, затем сохранение данных в БД

            loadDB();
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (!realm.isEmpty()) {
                        realm.deleteAll();
                    }

                    for (int i = 0; i < arrayList.size(); i++) {
                        realm.copyToRealm(arrayList.get(i));
                    }
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    recyclerViewAdapter.setItems(arrayList);

                    Log.d("check", "Live save");
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    Log.d("check", "Live not save");
                }
            });
        }
        realm.close();
    }

    // Инициализация Realm

    private void loadDB() {
        Realm.init(contextWeakReference.get());
        realm = Realm.getInstance(new RealmConfiguration.Builder().name("Save_Objects_2.realm").build());
    }

    // Сортировка данных по зрителям, полученных из БД

    private void sort(ArrayList<Info> arrayList) {
        for (int i = arrayList.size() - 1; i >= 1; i--) {
            for (int a = 0; a < i; a++) {
                if (arrayList.get(a).getViewers() < arrayList.get(a + 1).getViewers()) {
                    Info info = arrayList.get(a);
                    arrayList.set(a, arrayList.get(a + 1));
                    arrayList.set(a + 1, info);
                }
            }
        }
    }
}