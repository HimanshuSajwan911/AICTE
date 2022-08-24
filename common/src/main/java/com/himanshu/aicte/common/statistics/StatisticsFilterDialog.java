package com.himanshu.aicte.common.statistics;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.himanshu.aicte.common.R;

import java.util.Objects;


public class StatisticsFilterDialog extends DialogFragment {

    private ArrayAdapter<String> adapterYear, adapterState, adapterInstitutionType, adapterLevel, adapterProgram;

    private String year, state, institutionType, level, program;

    private String[] yearArray, stateArray, institutionTypeArray, levelArray, programArray;

    private OnFilterSubmitListener onFilterSubmitListener;

    private boolean arrayAdapterProvided = false;

    public StatisticsFilterDialog(String[] yearArray, String[] stateArray, String[] institutionTypeArray, String[] levelArray, String[] programArray) {
        this.yearArray = yearArray;
        this.stateArray = stateArray;
        this.institutionTypeArray = institutionTypeArray;
        this.levelArray = levelArray;
        this.programArray = programArray;
    }

    public StatisticsFilterDialog(ArrayAdapter<String> adapterYear, ArrayAdapter<String> adapterState, ArrayAdapter<String> adapterInstitutionType, ArrayAdapter<String> adapterLevel, ArrayAdapter<String> adapterProgram) {
        this.adapterYear = adapterYear;
        this.adapterState = adapterState;
        this.adapterInstitutionType = adapterInstitutionType;
        this.adapterLevel = adapterLevel;
        this.adapterProgram = adapterProgram;
        arrayAdapterProvided = true;
    }

    public void setOnFilterSubmitListener(OnFilterSubmitListener onFilterSubmitListener) {
        this.onFilterSubmitListener = onFilterSubmitListener;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.statistics_filter, container, false);

        if (!arrayAdapterProvided) {
            adapterYear = new ArrayAdapter<>(getContext(), R.layout.drop_down_item, yearArray);
            adapterState = new ArrayAdapter<>(getContext(), R.layout.drop_down_item, stateArray);
            adapterInstitutionType = new ArrayAdapter<>(getContext(), R.layout.drop_down_item, institutionTypeArray);
            adapterLevel = new ArrayAdapter<>(getContext(), R.layout.drop_down_item, levelArray);
            adapterProgram = new ArrayAdapter<>(getContext(), R.layout.drop_down_item, programArray);
        }

        Button btCancel = view.findViewById(R.id.button_layout_statistics_filter_cancel);
        Button btSubmit = view.findViewById(R.id.button_layout_statistics_filter_submit);

        AutoCompleteTextView autoCompleteTextViewYear = view.findViewById(R.id.autoCompleteTextView_fragment_statistics_writer_year);
        autoCompleteTextViewYear.setAdapter(adapterYear);

        autoCompleteTextViewYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                year = parent.getItemAtPosition(position).toString();
            }
        });

        AutoCompleteTextView autoCompleteTextViewState = view.findViewById(R.id.autoCompleteTextView_fragment_statistics_writer_state);
        autoCompleteTextViewState.setAdapter(adapterState);

        autoCompleteTextViewState.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                state = parent.getItemAtPosition(position).toString();
            }
        });


        AutoCompleteTextView autoCompleteTextViewInstitutionType = view.findViewById(R.id.autoCompleteTextView_fragment_statistics_writer_institution_type);
        autoCompleteTextViewInstitutionType.setAdapter(adapterInstitutionType);

        autoCompleteTextViewInstitutionType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                institutionType = parent.getItemAtPosition(position).toString();
            }
        });

        AutoCompleteTextView autoCompleteTextViewLevel = view.findViewById(R.id.autoCompleteTextView_fragment_statistics_writer_level);
        autoCompleteTextViewLevel.setAdapter(adapterLevel);

        autoCompleteTextViewLevel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                level = parent.getItemAtPosition(position).toString();
            }
        });

        AutoCompleteTextView autoCompleteTextViewProgram = view.findViewById(R.id.autoCompleteTextView_fragment_statistics_writer_program);
        autoCompleteTextViewProgram.setAdapter(adapterProgram);

        autoCompleteTextViewProgram.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                program = parent.getItemAtPosition(position).toString();
            }
        });

        btCancel.setOnClickListener(v -> Objects.requireNonNull(getDialog()).dismiss());

        btSubmit.setOnClickListener(v -> submit());


        return view;
    }

    private void submit() {
        onFilterSubmitListener.onFilterSubmit(year, state, institutionType, level, program);
        getDialog().dismiss();
    }

}
