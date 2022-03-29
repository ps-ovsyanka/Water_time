package com.example.water_time;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.water_time.MainActivity;
import com.example.water_time.R;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment implements View.OnClickListener {

    TextView waterProgressText;//отображение прогресса
    TextView waterTargetText;//отображение цели
    ProgressBar waterProgressBar;//визуальный прогресс
    Button tare_1, tare_2, tare_3;//кнопки с ёмкостями
    Button btnCancel;


    SharedPreferences pref;//объект для работы с настройками

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        Log.d("oooooooo", "CREATE VIEW HOME FRAGMENT");
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        pref = PreferenceManager.getDefaultSharedPreferences(getContext());

        //инициализация компонентов
        waterProgressBar = root.findViewById(R.id.water_progress_bar);
        waterProgressText = root.findViewById(R.id.text_water_progress);
        waterTargetText = root.findViewById(R.id.water_target);

        tare_1 = root.findViewById(R.id.tare_1);//инициализация кнопки
        tare_1.setOnClickListener(this);//установка обработчика нажатия

        tare_2 = root.findViewById(R.id.tare_2);
        tare_2.setOnClickListener(this);
        tare_2.setText(pref.getString("size_tare_2", "300"));//установка значения из настроек

        tare_3 = root.findViewById(R.id.tare_3);
        tare_3.setOnClickListener(this);
        tare_3.setText(pref.getString("size_tare_3", "99900"));//установка значения из настроек

        btnCancel = root.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);


        //обновление отображения данных
        updateData();

        return root;
    }



    //обновить прогресс и добавить приём воды
    public void updateProgress (int n) {
        MainActivity.waterProgress += n;//изменение основной переменной прогресса
        waterProgressText.setText(String.valueOf(MainActivity.waterProgress));
        waterProgressBar.setProgress(MainActivity.waterProgress);
        MainActivity.drinkDays.peek().setDayResult(MainActivity.waterProgress);
        //если цель достигнута то день выполнен
        if (MainActivity.drinkDays.peek().getDayResult() >= MainActivity.waterTarget) {
            MainActivity.drinkDays.peek().setCompleted(true);
        } else {
            MainActivity.drinkDays.peek().setCompleted(false);
        }
    }



    //обновить цель
    public void updateData () {
        //загрузка цели из настроек
        Log.d("00000000000000", "обновление данных");
        String s = pref.getString("water_target", "1700");
        MainActivity.waterTarget = Integer.parseInt(s);
        waterProgressBar.setMax(MainActivity.waterTarget);
        waterTargetText.setText(String.valueOf(MainActivity.waterTarget));
        //обновление текста кнопок
        tare_1.setText(pref.getString("size_tare_1", "333"));//установка значения из настроек
        tare_2.setText(pref.getString("size_tare_2", "333"));//установка значения из настроек
        tare_3.setText(pref.getString("size_tare_3", "333"));//установка значения из настроек
        //обновление прогресса
        int progress = MainActivity.drinkDays.peek().getDayResult();
        MainActivity.waterProgress = progress;
        waterProgressBar.setProgress(progress);
        waterProgressText.setText(String.valueOf(progress));

    }


    //обработка нажатия кнопок
    @Override
    public void onClick(View view) {
        Button btn = (Button) view;

        switch (btn.getId()) {
            //нажата кнопка отмены и прогресс не равен нулю
            case R.id.btn_cancel:
                if (!MainActivity.drinkDays.peek().drinkItems.isEmpty()){
                    int endIndex = MainActivity.drinkDays.peek().drinkItems.size() - 1;
                    updateProgress(Integer.parseInt(MainActivity.drinkDays.peek().drinkItems.get(endIndex)) * -1);
                    MainActivity.drinkDays.peek().drinkItems.remove(endIndex);
                }
                break;
            default:
                int n = Integer.parseInt(btn.getText().toString());//извлечение значения кнопки
                updateProgress(n);//обновление прогресса
                MainActivity.drinkDays.peek().drinkItems.add(String.valueOf(n));
        }
    }


}