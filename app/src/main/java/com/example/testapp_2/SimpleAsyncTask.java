package com.example.testapp_2;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.testapp_2.db.Info;
import com.example.testapp_2.pojo.Object;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class SimpleAsyncTask extends AsyncTask<Void, Void, Void> {
    private ArrayList<Info> arrayList = new ArrayList<>(20);
    private RecyclerViewAdapter recyclerViewAdapter;
    private boolean offline = false;
    private Context context;
    Realm realm;

    public SimpleAsyncTask(RecyclerViewAdapter recyclerViewAdapter, Context context) {
        this.recyclerViewAdapter = recyclerViewAdapter;
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL("https://api.twitch.tv/kraken/games/top?limit=20");
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
                info.setViewers(object.getTop().get(i).getViewers());

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
            loadDB();
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<Info> realmResults = realm.where(Info.class).findAll();

                    for (Info info : realmResults) {
                        arrayList.add(new Info(info.getName(), info.getImage(), info.getViewers(), info.getChannels()));
                    }
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    recyclerViewAdapter.setItems(arrayList);

                    Log.d("check", "Offline load");
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    Log.d("check", "Offline not load");
                }
            });
        } else {
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

    private void loadDB() {
        Realm.init(context);
        realm = Realm.getInstance(new RealmConfiguration.Builder().name("Save_Objects.realm").build());
    }
}