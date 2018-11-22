package pl.denislewandowski.bankczasu.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pl.denislewandowski.bankczasu.R;
import pl.denislewandowski.bankczasu.RecyclerItemTouchHelper;
import pl.denislewandowski.bankczasu.Service;
import pl.denislewandowski.bankczasu.adapters.ServicesToDoAdapter;
import pl.denislewandowski.bankczasu.dialogs.ConfirmServiceDialogFragment;
import pl.denislewandowski.bankczasu.dialogs.ReturnCurrencyBackDialogFragment;

public class ServicesToDoFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private RecyclerView recyclerView;
    private List<Service> services;
    private ContentLoadingProgressBar progressBar;
    private ServicesToDoAdapter adapter;
    private String currentUserId;
    private String timebankId;
    private CoordinatorLayout coordinatorLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_do_list, container, false);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        timebankId = sharedPreferences.getString("CURRENT_TIMEBANK", "null");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.services_to_do);
        recyclerView = getView().findViewById(R.id.recycler_view_to_do);
        progressBar = getView().findViewById(R.id.progress_bar);
        coordinatorLayout = getActivity().findViewById(R.id.coordinator_layout);

        services = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        getAllServicesToDo();
        attachItemTouchHelper(recyclerView);
    }


    private void getAllServicesToDo() {
        FirebaseDatabase.getInstance().getReference("Timebanks").child(timebankId).child("Services")
                .orderByChild("clientId").startAt("")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        services.clear();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Service service = ds.getValue(Service.class);
                                if (service.getServiceOwnerId().equals(currentUserId) || service.getClientId().equals(currentUserId))
                                    if (!service.getClientId().equals(""))
                                        services.add(ds.getValue(Service.class));
                            }
                        }
                        progressBar.hide();
                        adapter = new ServicesToDoAdapter(services, getContext());
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void attachItemTouchHelper(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
                new RecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ServicesToDoAdapter.ViewHolder) {
            String name = services.get(viewHolder.getAdapterPosition()).getName();

            final Service deletedItem = services.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            if(currentUserId.equals(deletedItem.getServiceOwnerId())) {
                final ReturnCurrencyBackDialogFragment dialog = ReturnCurrencyBackDialogFragment.newInstance(deletedItem);
                dialog.show(getChildFragmentManager(), "DIALOG");
                getChildFragmentManager().executePendingTransactions();
                dialog.setCancelable(false);
                dialog.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if (!dialog.currencyBacked)
                            adapter.restoreItem(deletedItem, deletedIndex);
                    }
                });
                adapter.removeItem(viewHolder.getAdapterPosition());
            } else {
                final ConfirmServiceDialogFragment dialog = ConfirmServiceDialogFragment.newInstance(deletedItem);
                dialog.show(getChildFragmentManager(), "DIALOG");
                getChildFragmentManager().executePendingTransactions();
                dialog.setCancelable(false);
                dialog.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if(!dialog.serviceConfirmed) {
                            adapter.restoreItem(deletedItem, deletedIndex);
                        }
                    }
                });
                adapter.removeItem(viewHolder.getAdapterPosition());
            }
        }
    }
}
