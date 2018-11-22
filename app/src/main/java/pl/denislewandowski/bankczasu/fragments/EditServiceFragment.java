package pl.denislewandowski.bankczasu.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pl.denislewandowski.bankczasu.FirebaseRepository;
import pl.denislewandowski.bankczasu.Service;
import pl.denislewandowski.bankczasu.TimeCurrency;
import pl.denislewandowski.bankczasu.R;
import pl.denislewandowski.bankczasu.adapters.CategorySpinnerAdapter;
import pl.denislewandowski.bankczasu.adapters.TimeCurrencySpinnerAdapter;

public class EditServiceFragment extends Fragment {

    private EditText serviceNameEditText, descriptionEditText;
    private TextView charCounter;
    private Spinner timeCurrencySpinner, categorySpinner;
    private List<TimeCurrency> timeCurrencyList;
    private TimeCurrencySpinnerAdapter adapter;
    private CategorySpinnerAdapter categoryAdapter;
    private Button editButton, deleteButton;
    private Service service;
    private String timebankId;
    FirebaseRepository repository;


    private DatabaseReference timebanksReference;
    private DatabaseReference userReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        service = (Service) getArguments().getSerializable("SERVICE");

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        timebankId = sharedPreferences.getString("CURRENT_TIMEBANK", "null");
        repository = new FirebaseRepository(getActivity());

        return inflater.inflate(R.layout.fragment_edit_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.edit_service);

        serviceNameEditText = getView().findViewById(R.id.service_name);
        descriptionEditText = getView().findViewById(R.id.service_description);
        charCounter = getView().findViewById(R.id.char_counter);
        timeCurrencySpinner = getView().findViewById(R.id.time_currency_spinner);
        categorySpinner = getView().findViewById(R.id.category_spinner);
        editButton = getView().findViewById(R.id.edit_service_button);
        deleteButton = getView().findViewById(R.id.delete_service_button);

        timebanksReference = FirebaseDatabase.getInstance().getReference("Timebanks").child(timebankId);
        userReference = FirebaseDatabase.getInstance()
                .getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        initList();
        adapter = new TimeCurrencySpinnerAdapter(getContext(), timeCurrencyList);
        timeCurrencySpinner.setAdapter(adapter);
        categoryAdapter = new CategorySpinnerAdapter(getContext(), getResources().getStringArray(R.array.category_names));
        categorySpinner.setAdapter(categoryAdapter);

        serviceNameEditText.setText(service.getName());
        descriptionEditText.setText(service.getDescription());
        charCounter.setText(String.valueOf(service.getName().length()));
        categorySpinner.setSelection(service.getCategory());
        setSpinnerPosition();

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

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repository.deleteService(service, timebankId);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateData()) {
                    Service updatedService = createEditedService();
                    repository.updateService(updatedService, timebankId);
                }
            }
        });
    }

    private Service createEditedService() {
        Service updatedService = service;
        updatedService.setCategory(categorySpinner.getSelectedItemPosition());
        updatedService.setDescription(descriptionEditText.getText().toString());
        updatedService.setName(serviceNameEditText.getText().toString());
        TimeCurrency timeCurrency = (TimeCurrency) timeCurrencySpinner.getSelectedItem();
        updatedService.setTimeCurrencyValue(timeCurrency.getTimeCurrencyValue());
        return service;
    }

    private void initList() {
        timeCurrencyList = new ArrayList<>();
        timeCurrencyList.add(new TimeCurrency(2, 30));
        timeCurrencyList.add(new TimeCurrency(3, 45));
        timeCurrencyList.add(new TimeCurrency(4, 60));
        timeCurrencyList.add(new TimeCurrency(6, 90));
    }

    private void setSpinnerPosition() {
        int position = 0;
        switch (service.getTimeCurrencyValue()) {
            case 2:
                position = 0;
                break;
            case 3:
                position = 1;
                break;
            case 4:
                position = 2;
                break;
            case 6:
                position = 3;
                break;
        }
        timeCurrencySpinner.setSelection(position);
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
}
