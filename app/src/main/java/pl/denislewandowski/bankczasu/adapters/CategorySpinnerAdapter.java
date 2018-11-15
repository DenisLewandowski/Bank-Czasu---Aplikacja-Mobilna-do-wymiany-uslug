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

import pl.denislewandowski.bankczasu.R;

public class CategorySpinnerAdapter extends ArrayAdapter<String> {

    public CategorySpinnerAdapter(@NonNull Context context, @NonNull List objects) {
        super(context, 0, objects);
    }

    public CategorySpinnerAdapter(@NonNull Context context, @NonNull String[] objects) {
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
                    R.layout.spinner_item_category, parent, false);
        }

        TextView categoryNameTextView;
        categoryNameTextView = convertView.findViewById(R.id.category_name);

        String categoryName = getItem(position);
        categoryNameTextView.setText(categoryName);

        return convertView;
    }
}