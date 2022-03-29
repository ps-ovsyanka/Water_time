package com.example.water_time;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    public static String TARGET = "water_target",
                    SIZE_TARE_1="size_tare_1", SIZE_TARE_2="size_tare_2", SIZE_TARE_3="size_tare_3";

    public static int waterProgress;//основная переменная прогресса текущего дня
    public static int waterTarget;//дневная цель
    public static Stack<DrinkDay> drinkDays = new Stack<DrinkDay>();//массив приемов воды за день

    SQLiteDatabase db;
    DBHelper dbHelper;

    public static SharedPreferences pref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.navigation_history, R.id.navigation_home, R.id.navigation_setting).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        getSupportActionBar().hide();//убирает шапку
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//устанавливает портретную ориентацию

        pref = getPreferences(MODE_PRIVATE);//работа с сохраненными настройками

        if (pref.getBoolean("firstrun", true)) { //проверка на первый запуск
            //установка парапетров по умолчанию в первый раз
            SharedPreferences.Editor ed = pref.edit();
            ed.putString(TARGET, "1700").commit();
            ed.putString(SIZE_TARE_1, "200").commit();
            ed.putString(SIZE_TARE_2,"300").commit();
            ed.putString(SIZE_TARE_3,"500").commit();

            pref.edit().putBoolean("firstrun", false).commit();
        }

        //создание базы данных для храренния истории
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();


        getDataFromDB();
    }


    @Override
    protected void onStop() { //при закрытиии приложения требуется обновить БД
        Log.d("00000000", "activity is stop");
        updateDB();
        super.onStop();
    }



    class DBHelper extends SQLiteOpenHelper {

        public  DBHelper (Context context) {
            super(context, "myDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String s = "hello";
            db.execSQL("create table waterDayHistory ( id integer primary key autoincrement, date text, result integer, completed integer);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }

    private void getDataFromDB() {
        //запрос всех данных из БД
        Cursor cursor = db.query("waterDayHistory", null,null,null,null, null, null);
        //заполнение массива данными из БД
        if (cursor.moveToFirst()) {//если БД не пустая
            Log.d("00000000", "заполнение БД данными");

            int idIndex = cursor.getColumnIndex("id");//индекс id
            int dateIndex = cursor.getColumnIndex("date");//индекс даты
            int resultIndex = cursor.getColumnIndex("result");//индекс результатов
            int comletedIndex = cursor.getColumnIndex("completed");

            do {
                DrinkDay drinkDay = new DrinkDay();//создание объекта ДЕНЬ
                 drinkDay.setDayResult(cursor.getInt(resultIndex));//получние рез-та
                drinkDay.setCompleted(cursor.getInt(comletedIndex) > 0 ? true:false);
                drinkDays.push(drinkDay);//добавление объекта в стек
            } while (cursor.moveToNext());
            //если дата другая, создать новый день
            String s = new SimpleDateFormat("dd.MM").format(new Date());
            if (!s.equals(drinkDays.peek().getDate())) {//если даты разные
                drinkDays.push(new DrinkDay());//добавляем новый день
            }

            waterProgress = drinkDays.peek().getDayResult();
        } else {
            drinkDays.push(new DrinkDay());
        }

        pref = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void updateDB() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //запрос всех данных из БД
        Cursor cursor = db.query("waterDayHistory", null,null,null,null,null,null);
        //подготовка данных для обновления или добавления в БД
        ContentValues cv = new ContentValues();
        cv.put("date", drinkDays.peek().getDate());
        cv.put("result", drinkDays.peek().getDayResult());
        cv.put("completed", drinkDays.peek().isCompleted() == true ? 1 : 0);
        if (cursor.getCount() < drinkDays.size()) {//если добавился новый день
            db.insert("waterDayHistory", null, cv);
        } else {//если нужно обновить старый
            String idForUpd = String.valueOf(cursor.getCount());
            db.update("waterDayHistory", cv, "id = ?", new String[] {idForUpd});
        }

    }



    protected Dialog onCreateDialog(int id) {
        if (id == 1) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);

            // сообщение
            adb.setMessage("Удалить историю?");
            // иконка
            adb.setIcon(android.R.drawable.ic_dialog_info);
            // кнопка положительного ответа
            adb.setPositiveButton("Удалить", myClickListener);
            // кнопка отрицательного ответа
            adb.setNegativeButton("Отмена", myClickListener);
            // создаем диалог
            return adb.create();
        }
        return super.onCreateDialog(id);
    }

    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                // положительная кнопка
                case Dialog.BUTTON_POSITIVE:
                    drinkDays.clear();
                    finish();
                    break;
                // негативная кнопка
                case Dialog.BUTTON_NEGATIVE:
                    finish();
                    break;
            }
        }
    };

}