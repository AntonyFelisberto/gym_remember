package com.example.gym_remember.localstorage;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.gym_remember.OnActivitiesUpdatedListener;
import com.example.gym_remember.model.Activity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StorageServices {

    private static final String FILENAME = "activities.json";

    private OnActivitiesUpdatedListener listener;

    private static StorageServices instance;

    private StorageServices() {}

    public static StorageServices getInstance() {
        if (instance == null) {
            instance = new StorageServices();
        }
        return instance;
    }

    public void setOnActivitiesUpdatedListener(OnActivitiesUpdatedListener listener) {
        this.listener = listener;
    }

    public boolean insertActivity(Context context, Activity activity) {
        try {
            JSONArray jsonArray = readActivitiesJson(context);
            jsonArray.put(activity.toJson());
            writeActivitiesJson(context, jsonArray);

            if (listener != null) {
                listener.onActivitiesUpdated();
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public JSONArray readActivitiesJson(Context context) {
        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            return new JSONArray(sb.toString());
        } catch (Exception e) {
            Log.println(Log.ASSERT,"Erro ao buscar dados de atividade", String.valueOf(e));
            return new JSONArray();
        }
    }

    public void deleteActivityAsync(Context context, String title, Runnable onFinished) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                JSONArray jsonArray = readActivitiesJson(context);
                JSONArray newArray = new JSONArray();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    if (!obj.getString("title").equals(title)) {
                        newArray.put(obj);
                    }
                }

                writeActivitiesJson(context, newArray);
            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(onFinished);
        });
    }

    public void writeActivitiesJson(Context context, JSONArray jsonArray) {
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(jsonArray.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            Log.println(Log.ASSERT,"Erro ao buscar dados de atividade", String.valueOf(e));
        }
    }
}
