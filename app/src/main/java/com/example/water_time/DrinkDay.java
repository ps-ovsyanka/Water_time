package com.example.water_time;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

public class DrinkDay {

    private int dayResult;//результат за день
    private String date;//Дата
    private boolean completed;//выполнена цель или нет

    public List<String> drinkItems = new ArrayList<>();//список приемов воды

    //конструктор
    public DrinkDay () {
        date = new SimpleDateFormat("dd.MM").format(new Date());
        dayResult = 0;
        completed = false;
    }

    public int getDayResult() {
        return dayResult;
    }

    public void setDayResult(int dayResult) {
        this.dayResult = dayResult;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String s) { date = s; }

    public void setCompleted (boolean completed) { this.completed = completed;}

    public boolean isCompleted () { return completed; }

}
