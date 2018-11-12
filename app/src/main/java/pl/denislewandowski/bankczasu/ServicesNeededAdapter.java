package pl.denislewandowski.bankczasu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bankczasu.R;

import java.util.List;

public class ServicesNeededAdapter extends RecyclerView.Adapter<ServicesNeededAdapter.ViewHolder> {
    private List<Service> servicesNeededList;
    private Context context;

    public ServicesNeededAdapter(List<Service> servicesNeededList, Context context) {
        this.servicesNeededList = servicesNeededList;
        this.context = context;
    }

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

        void bindView(Service service) {
            titleTextView.setText(service.getName());
        }

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.offer_title);
        }
    }
}
