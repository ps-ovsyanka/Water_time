package com.example.water_time.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.water_time.MainActivity;
import com.example.water_time.R;

public class HistoryFragment extends Fragment {

    public RecyclerView listHistory;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        HistoryAdapter adapter = new HistoryAdapter(this.getContext(), MainActivity.drinkDays);//создание адаптера для списка

        listHistory = root.findViewById(R.id.list_history);//инициализация списка
        listHistory.setAdapter(adapter);//присвоение адаптера списку

        return root;
    }

}