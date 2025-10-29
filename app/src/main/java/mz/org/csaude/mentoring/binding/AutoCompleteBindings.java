package mz.org.csaude.mentoring.binding;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.adapter.spinner.listble.ListableSpinnerAdapter;

/**
 * BindingAdapters para MaterialAutoCompleteTextView com itens Listble.
 * Permite usar app:adapter="@{...}" e bind:selectedOpt="@={viewModel.algumaCoisa}".
 */
public class AutoCompleteBindings {

    /* ==== Setter do adapter (conveniente) ==== */
    @BindingAdapter("adapter")
    public static void setAdapter(MaterialAutoCompleteTextView view, @Nullable ArrayAdapter<?> adapter) {
        if (view.getAdapter() != adapter) {
            view.setAdapter(adapter);
        }
    }

    /* ==== SET do valor selecionado (Listble -> UI) ==== */
    @BindingAdapter("selectedOpt")
    public static void setSelectedOpt(MaterialAutoCompleteTextView view, @Nullable Listble value) {
        // suprimir notificações programáticas
        view.setTag(R.id.tag_selected_opt_suppress, Boolean.TRUE);
        try {
            final String target = (value == null) ? "" : value.toString();
            if (!target.contentEquals(view.getText())) {
                view.setText(target, false);
            }
            // mantenha o valor coerente no TAG
            view.setTag(R.id.tag_selected_opt_value, value);
        } finally {
            view.setTag(R.id.tag_selected_opt_suppress, null);
        }
    }

    @InverseBindingAdapter(attribute = "selectedOpt", event = "selectedOptAttrChanged")
    public static Listble getSelectedOpt(MaterialAutoCompleteTextView view) {
        // 1) preferir o valor guardado no TAG (é o objeto real)
        Object tagged = view.getTag(R.id.tag_selected_opt_value);
        if (tagged instanceof Listble) return (Listble) tagged;

        // 2) fallback (caso o TAG não esteja definido): tenta casar por texto
        CharSequence cs = view.getText();
        String text = (cs == null) ? "" : cs.toString().trim();
        if (text.isEmpty()) return null;

        if (view.getAdapter() instanceof ListableSpinnerAdapter) {
            ListableSpinnerAdapter adapter = (ListableSpinnerAdapter) view.getAdapter();
            for (int i = 0; i < adapter.getCount(); i++) {
                Object item = adapter.getItem(i);
                if (item instanceof Listble && text.equals(item.toString())) {
                    return (Listble) item;
                }
            }
        }
        return null;
    }

    @BindingAdapter("selectedOptAttrChanged")
    public static void setSelectedOptListener(MaterialAutoCompleteTextView view,
                                              final InverseBindingListener listener) {
        if (listener == null) return;

        // Quando o utilizador escolhe um item, guardamos o objeto e notificamos
        view.setOnItemClickListener((parent, v, position, id) -> {
            Object item = parent.getItemAtPosition(position);
            if (item instanceof Listble) {
                view.setTag(R.id.tag_selected_opt_value, item);
                listener.onChange();
            }
        });

        // Quando apaga o texto, limpamos o valor e notificamos null
        view.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                Object suppressed = view.getTag(R.id.tag_selected_opt_suppress);
                if (suppressed instanceof Boolean && (Boolean) suppressed) return;

                if (s == null || s.length() == 0) {
                    view.setTag(R.id.tag_selected_opt_value, null);
                    listener.onChange();
                }
                // texto digitado ≠ seleção -> não notifica aqui
            }
        });
    }

}
