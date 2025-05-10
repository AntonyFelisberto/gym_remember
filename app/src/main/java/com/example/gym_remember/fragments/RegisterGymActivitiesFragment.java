package com.example.gym_remember.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gym_remember.R;
import com.example.gym_remember.localstorage.StorageServices;
import com.example.gym_remember.model.Activity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterGymActivitiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterGymActivitiesFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private StorageServices storageServices;

    private String mParam1;
    private String mParam2;

    private EditText numberTimes,title,repetitions,relaxationMinutes;

    public RegisterGymActivitiesFragment() {
        // Required empty public constructor
    }

    public static RegisterGymActivitiesFragment newInstance(String param1, String param2) {
        RegisterGymActivitiesFragment fragment = new RegisterGymActivitiesFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_gym_activities, container, false);

        Button button = view.findViewById(R.id.button);
        relaxationMinutes = view.findViewById(R.id.relaxationMinutes);
        repetitions = view.findViewById(R.id.repetitions);
        title = view.findViewById(R.id.title);
        numberTimes = view.findViewById(R.id.numberTimes);

        button.setOnClickListener(this::registrarAtividade);

        storageServices = StorageServices.getInstance();

        return view;
    }

    public void registrarAtividade(View view){

        if(
            !numberTimes.getText().toString().isEmpty() &&
            !relaxationMinutes.getText().toString().isEmpty() &&
            !repetitions.getText().toString().isEmpty() &&
            !title.getText().toString().isEmpty()
        ){
            Activity activity = new Activity(
                title.getText().toString(),
                Integer.parseInt(repetitions.getText().toString()),
                Integer.parseInt(numberTimes.getText().toString()),
                Integer.parseInt(relaxationMinutes.getText().toString())
            );
            cleanData();
            boolean inserted = storageServices.insertActivity(this.getContext(), activity);
            Toast.makeText(this.getContext(), inserted ? "Atividade adicionada com sucesso" : "Erro ao adicionar atividade", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this.getContext(), "Preencha todos os dados para continuar", Toast.LENGTH_LONG).show();
        }
    }

    private void cleanData(){
        numberTimes.setText("");
        relaxationMinutes.setText("");
        repetitions.setText("");
        title.setText("");
    }
}