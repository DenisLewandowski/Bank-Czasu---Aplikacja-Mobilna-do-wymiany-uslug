package pl.denislewandowski.bankczasu.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import pl.denislewandowski.bankczasu.TimeCurrency;
import pl.denislewandowski.bankczasu.R;

public class TimeCurrencySpinnerAdapter extends ArrayAdapter<TimeCurrency> {

    public TimeCurrencySpinnerAdapter(@NonNull Context context, @NonNull List objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.spinner_item, parent, false);
        }

        TextView timeCurrencyTextView, currencyInMinutesTextView;
        timeCurrencyTextView = convertView.findViewById(R.id.time_currency_value);
        currencyInMinutesTextView = convertView.findViewById(R.id.time_in_minutes);

        TimeCurrency timeCurrency = getItem(position);

        timeCurrencyTextView.setText(String.valueOf(timeCurrency.getTimeCurrencyValue()));
        currencyInMinutesTextView.setText("(" + String.valueOf(timeCurrency.getTimeInMinutes()) + " min)");

        return convertView;
    }
}
