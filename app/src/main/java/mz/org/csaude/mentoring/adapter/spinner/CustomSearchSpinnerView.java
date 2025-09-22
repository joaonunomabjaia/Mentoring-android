package mz.org.csaude.mentoring.adapter.spinner;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.adapter.spinner.listble.ListableSpinnerAdapter;

public class CustomSearchSpinnerView extends LinearLayout {

    private AutoCompleteTextView autoCompleteTextView;
    private ListableSpinnerAdapter adapter;
    private OnItemSelectedListener onItemSelectedListener;
    private Listble selectedItem;

    public interface OnItemSelectedListener {
        void onItemSelected(Listble item);
    }

    public CustomSearchSpinnerView(Context context) {
        super(context);
        init(context);
    }

    public CustomSearchSpinnerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.custom_search_spinner_view, this, true);
        autoCompleteTextView = findViewById(R.id.autoCompleteSpinner);

        autoCompleteTextView.setOnClickListener(v -> autoCompleteTextView.showDropDown());

        autoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) autoCompleteTextView.showDropDown();
        });

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            selectedItem = (Listble) parent.getItemAtPosition(position);
            if (onItemSelectedListener != null) {
                onItemSelectedListener.onItemSelected(selectedItem);
            }
        });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Clear selection if user types something new
                selectedItem = null;
            }
            @Override public void afterTextChanged(Editable s) { }
        });
    }

    public void setAdapter(ListableSpinnerAdapter adapter) {
        this.adapter = adapter;
        autoCompleteTextView.setAdapter(adapter);
    }

    public void setHint(String hint) {
        autoCompleteTextView.setHint(hint);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.onItemSelectedListener = listener;
    }

    @Nullable
    public Listble getSelectedItem() {
        return selectedItem;
    }

    public void setText(String text) {
        autoCompleteTextView.setText(text);
    }

    public void clearSelection() {
        autoCompleteTextView.setText("");
        selectedItem = null;
    }
}
