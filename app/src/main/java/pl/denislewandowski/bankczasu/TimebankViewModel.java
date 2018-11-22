package pl.denislewandowski.bankczasu;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class TimebankViewModel extends ViewModel {

    public MutableLiveData<TimebankData> timebankData = new MutableLiveData<>();
}
