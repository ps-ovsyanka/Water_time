package com.example.water_time.ui.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.water_time.R;
import com.example.water_time.DrinkDay;

import java.util.Stack;

class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private Stack<DrinkDay> drinkDays;

    HistoryAdapter(Context context, Stack<DrinkDay> drinkTimeList) {
        this.drinkDays = drinkTimeList;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryAdapter.ViewHolder holder, int position) {
        DrinkDay drinkDay = drinkDays.get(drinkDays.size() - 1 - position);
        holder.volume.setText(String.valueOf(drinkDay.getDayResult())+" ml");
        holder.time.setText(drinkDay.getDate());
        if (drinkDay.isCompleted()) {
            holder.checkBox.setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        return drinkDays.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView volume;
        final TextView time;
        final CheckBox checkBox;
        ViewHolder(View view){
            super(view);
            volume = (TextView) view.findViewById(R.id.history_item_volume);
            time = (TextView) view.findViewById(R.id.history_item_time);
            checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        }
    }
}