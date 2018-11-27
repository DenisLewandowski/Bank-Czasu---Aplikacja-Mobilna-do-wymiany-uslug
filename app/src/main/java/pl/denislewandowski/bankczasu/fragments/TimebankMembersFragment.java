package pl.denislewandowski.bankczasu.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import pl.denislewandowski.bankczasu.R;
import pl.denislewandowski.bankczasu.TimebankViewModel;
import pl.denislewandowski.bankczasu.model.UserItem;
import pl.denislewandowski.bankczasu.adapters.UsersAdapter;

public class TimebankMembersFragment extends Fragment {

    private RecyclerView recyclerView;
    private ContentLoadingProgressBar progressBar;
    private TimebankViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        viewModel =  ViewModelProviders.of(getActivity()).get(TimebankViewModel.class);

        return inflater.inflate(R.layout.fragment_timebank_members, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = getView().findViewById(R.id.timebank_members_recyclerview);
        progressBar = getView().findViewById(R.id.progress_bar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        setUsersObservable();
    }

    private void setUsersObservable() {
        viewModel.usersData.observe(getActivity(), new Observer<List<UserItem>>() {
            @Override
            public void onChanged(@Nullable List<UserItem> userItems) {
                recyclerView.setAdapter(new UsersAdapter(userItems, getContext()));
                progressBar.hide();
            }
        });

    }
}
