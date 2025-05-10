package com.example.gym_remember.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gym_remember.R;
import com.example.gym_remember.adapter.ActivityAdapter;
import com.example.gym_remember.localstorage.StorageServices;
import com.example.gym_remember.model.Activity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VerifyGymActivitiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VerifyGymActivitiesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private StorageServices storageServices;

    private RecyclerView recyclerView;
    private ActivityAdapter adapter;
    private List<Activity> activityList;

    public VerifyGymActivitiesFragment() {
        // Required empty public constructor
    }

    public static VerifyGymActivitiesFragment newInstance(String param1, String param2) {
        VerifyGymActivitiesFragment fragment = new VerifyGymActivitiesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
        storageServices = StorageServices.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_verify_gym_activities, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        storageServices.setOnActivitiesUpdatedListener(() -> {
            requireActivity().runOnUiThread(this::loadActivitiesFromStorage);
        });

        loadActivitiesFromStorage();
        return view;
    }

    public void loadActivitiesFromStorage() {
        Type activityListType = new TypeToken<List<Activity>>() {}.getType();
        activityList = new Gson().fromJson(
                storageServices.readActivitiesJson(this.getContext()).toString(),
                activityListType
        );

        adapter = new ActivityAdapter(activityList);
        recyclerView.setAdapter(adapter);
    }
}