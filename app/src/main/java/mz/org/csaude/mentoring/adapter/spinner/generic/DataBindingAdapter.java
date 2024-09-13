package mz.org.csaude.mentoring.adapter.spinner.generic;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;


public class DataBindingAdapter {

    @BindingAdapter(value = {"selectedOpt", "selectedOptAttrChanged"}, requireAll = false)
    public static void setSelectedOpt(final AppCompatSpinner spinner,
                                      final Listble selectedOpt,
                                      final InverseBindingListener changeListener) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                changeListener.onChange();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                changeListener.onChange();
            }
        });

        spinner.setSelection(getIndexOfItem(spinner, selectedOpt));
    }

    @InverseBindingAdapter(attribute = "selectedOpt", event = "selectedOptAttrChanged")
    public static Listble getSelectedOpt(final AppCompatSpinner spinner) {
        if (spinner == null || spinner.getAdapter() == null || spinner.getAdapter().getCount() == 0) {
            // If spinner or its adapter is null, or the adapter has no items, return null
            return null;
        }

        int selectedPosition = spinner.getSelectedItemPosition();

        // Ensure the selected position is valid
        if (selectedPosition < 0 || selectedPosition >= spinner.getAdapter().getCount()) {
            // Return null if the selected position is invalid
            return null;
        }

        // Return the selected item if everything is valid
        return (Listble) spinner.getItemAtPosition(selectedPosition);
    }

    private static int getIndexOfItem(AppCompatSpinner spinner, Listble item){
        Adapter a = spinner.getAdapter();
        if (a == null) return 0;

        for(int i=0; i<a.getCount(); i++){
            if (a.getItem(i) == null) return 0;

            if((a.getItem(i)).equals(item)){
                return i;
            }
        }
        return 0;
    }

    @BindingAdapter("android:layout_marginBottom")
    public static void setBottomMargin(View view, float bottomMargin) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin,
                layoutParams.rightMargin, Math.round(bottomMargin));
        view.setLayoutParams(layoutParams);
    }
}
