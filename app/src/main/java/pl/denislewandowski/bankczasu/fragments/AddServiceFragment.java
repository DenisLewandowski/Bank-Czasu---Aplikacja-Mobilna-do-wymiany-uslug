package pl.denislewandowski.bankczasu.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import pl.denislewandowski.bankczasu.FirebaseRepository;
import pl.denislewandowski.bankczasu.model.Service;
import pl.denislewandowski.bankczasu.model.TimeCurrency;
import pl.denislewandowski.bankczasu.R;
import pl.denislewandowski.bankczasu.adapters.CategorySpinnerAdapter;
import pl.denislewandowski.bankczasu.adapters.TimeCurrencySpinnerAdapter;

public class AddServiceFragment extends Fragment {

    private EditText serviceNameEditText, descriptionEditText;
    private TextView charCounter;
    private Spinner timeCurrencySpinner, categorySpinner;
    private List<TimeCurrency> timeCurrencyList;
    private TimeCurrencySpinnerAdapter adapter;
    private CategorySpinnerAdapter categoryAdapter;
    private Button addButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.add_service);

        serviceNameEditText = getView().findViewById(R.id.service_name);
        descriptionEditText = getView().findViewById(R.id.service_description);
        charCounter = getView().findViewById(R.id.char_counter);
        timeCurrencySpinner = getView().findViewById(R.id.time_currency_spinner);
        categorySpinner = getView().findViewById(R.id.category_spinner);
        addButton = getView().findViewById(R.id.add_service_button);

        initList();
        adapter = new TimeCurrencySpinnerAdapter(getContext(), timeCurrencyList);
        timeCurrencySpinner.setAdapter(adapter);
        categoryAdapter = new CategorySpinnerAdapter(getContext(), getResources().getStringArray(R.array.category_names));
        categorySpinner.setAdapter(categoryAdapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateData()) {
                    Service service = makeServiceObject();
                    FirebaseRepository repository = new FirebaseRepository(getActivity());
                    repository.addService(service);
                }
            }
        });


        charCounter.setText("0");
        serviceNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                charCounter.setText(String.valueOf(serviceNameEditText.getText().toString().length()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initList() {
        timeCurrencyList = new ArrayList<>();
        timeCurrencyList.add(new TimeCurrency(0, 0));
        timeCurrencyList.add(new TimeCurrency(2, 30));
        timeCurrencyList.add(new TimeCurrency(3, 45));
        timeCurrencyList.add(new TimeCurrency(4, 60));
        timeCurrencyList.add(new TimeCurrency(6, 90));
    }

    private boolean validateData() {
        if (serviceNameEditText.getText().toString().length() < 4) {
            serviceNameEditText.setError("Wpisz tytuł usługi");
            serviceNameEditText.requestFocus();
            return false;
        }
        if (descriptionEditText.getText().toString().length() < 8) {
            descriptionEditText.setError("Wpisz opis usługi");
            descriptionEditText.requestFocus();
            return false;
        }
        if (timeCurrencySpinner.getSelectedItemId() == 0) {
            Toast.makeText(getContext(), "Ustaw wartość czasowaluty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private Service makeServiceObject() {
        Service service = new Service();
        service.setName(serviceNameEditText.getText().toString());
        service.setDescription(descriptionEditText.getText().toString());
        service.setId(UUID.randomUUID().toString());
        service.setCreationDate(new Date());
        TimeCurrency tc = (TimeCurrency) timeCurrencySpinner.getSelectedItem();
        service.setTimeCurrencyValue(tc.getTimeCurrencyValue());
        service.setServiceOwnerId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        service.setCategory(categorySpinner.getSelectedItemPosition());
        service.setClientId("");
        return service;
    }

}
