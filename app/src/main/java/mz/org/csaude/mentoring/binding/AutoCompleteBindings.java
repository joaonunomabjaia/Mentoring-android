package mz.org.csaude.mentoring.binding;

import android.text.Editable;
import android.text.TextUtils;
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

public final class AutoCompleteBindings {

    private AutoCompleteBindings() {}

    /* ==== Adapter ==== */
    @BindingAdapter("adapter")
    public static void setAdapter(MaterialAutoCompleteTextView view, @Nullable ArrayAdapter<?> adapter) {
        if (view.getAdapter() != adapter) {
            view.setAdapter(adapter);
        }
    }

    /* ==== VM -> UI ==== */
    @BindingAdapter("selectedOpt")
    public static void setSelectedOpt(MaterialAutoCompleteTextView view, @Nullable Listble value) {
        // suprimir notificações programáticas
        view.setTag(R.id.tag_selected_opt_suppress, Boolean.TRUE);
        try {
            String desired = labelOf(value);
            CharSequence cs = view.getText();
            String current = (cs == null) ? "" : cs.toString();

            if (!TextUtils.equals(current, desired)) {
                // false: não filtrar/abrir dropdown ao setar por código
                view.setText(desired, false);
            }

            // manter o objeto num TAG (ou limpar se null)
            view.setTag(R.id.tag_selected_opt_value, value);
        } finally {
            view.setTag(R.id.tag_selected_opt_suppress, null);
        }
    }

    /* ==== UI -> VM ==== */
    @InverseBindingAdapter(attribute = "selectedOpt", event = "selectedOptAttrChanged")
    public static Listble getSelectedOpt(MaterialAutoCompleteTextView view) {
        // 1) preferir o objeto real guardado no TAG
        Object tagged = view.getTag(R.id.tag_selected_opt_value);
        if (tagged instanceof Listble) return (Listble) tagged;

        // 2) fallback: casar pelo label
        CharSequence cs = view.getText();
        String text = (cs == null) ? "" : cs.toString().trim();
        if (text.isEmpty()) return null;

        if (view.getAdapter() instanceof ListableSpinnerAdapter) {
            ListableSpinnerAdapter adapter = (ListableSpinnerAdapter) view.getAdapter();
            for (int i = 0; i < adapter.getCount(); i++) {
                Object item = adapter.getItem(i);
                if (item instanceof Listble) {
                    if (TextUtils.equals(text, labelOf((Listble) item))) {
                        return (Listble) item;
                    }
                }
            }
        }
        return null;
    }

    @BindingAdapter("selectedOptAttrChanged")
    public static void setSelectedOptListener(MaterialAutoCompleteTextView view,
                                              final InverseBindingListener listener) {
        if (listener == null) return;

        view.setOnItemClickListener((parent, v, position, id) -> {
            Object item = parent.getItemAtPosition(position);
            if (item instanceof Listble) {
                view.setTag(R.id.tag_selected_opt_value, item);
                listener.onChange();
            }
        });

        view.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                Object suppressed = view.getTag(R.id.tag_selected_opt_suppress);
                if (suppressed instanceof Boolean && (Boolean) suppressed) return;

                // se o utilizador limpou o texto, limpar o valor selecionado
                if (s == null || s.length() == 0) {
                    view.setTag(R.id.tag_selected_opt_value, null);
                    listener.onChange();
                }
            }
        });
    }

    /* ==== Helpers ==== */
    private static String labelOf(@Nullable Listble l) {
        if (l == null) return "";
        // tenta getDescription(); se vier null/empty, cai para toString(); se ainda assim null, ""
        try {
            String d = l.getDescription();
            if (!TextUtils.isEmpty(d)) return d;
        } catch (Throwable ignored) {}
        String t = l.toString();
        return (t == null) ? "" : t;
    }
}
