package pl.denislewandowski.bankczasu.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import pl.denislewandowski.bankczasu.CategoryUtils;
import pl.denislewandowski.bankczasu.R;
import pl.denislewandowski.bankczasu.Service;

public class TimebankServicesAdapter extends RecyclerView.Adapter<TimebankServicesAdapter.ViewHolder> {
    private List<Service> servicesNeededList;
    private Context context;

    public TimebankServicesAdapter(List<Service> servicesNeededList, Context context) {
        this.servicesNeededList = servicesNeededList;
        this.context = context;
    }

    public TimebankServicesAdapter() {}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.offer_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Service service = servicesNeededList.get(position);
        holder.bindView(service);
    }

    @Override
    public int getItemCount() {
        return servicesNeededList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public ImageView categoryImageView;
        public TextView timeCurrencyTextView;

        void bindView(Service service) {
            titleTextView.setText(service.getName());
            timeCurrencyTextView.setText(String.valueOf(service.getTimeCurrencyValue()));
            if (categoryImageView != null) {
                Glide.with(context).load(CategoryUtils.imageResourceIds[service.getCategory()])
//                        .apply(RequestOptions.circleCropTransform())
                        .into(categoryImageView);
            }
        }

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.offer_title);
            categoryImageView = itemView.findViewById(R.id.category_image);
            timeCurrencyTextView = itemView.findViewById(R.id.time_currency_value_list);
        }
    }
}
