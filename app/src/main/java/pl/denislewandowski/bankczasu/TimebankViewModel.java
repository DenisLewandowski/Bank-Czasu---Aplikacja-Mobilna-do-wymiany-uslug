package pl.denislewandowski.bankczasu;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import pl.denislewandowski.bankczasu.model.TimebankData;
import pl.denislewandowski.bankczasu.model.UserItem;

public class TimebankViewModel extends ViewModel {

    public MutableLiveData<TimebankData> timebankData = new MutableLiveData<>();
    public MutableLiveData<List<UserItem>> usersData = new MutableLiveData<>();
}
