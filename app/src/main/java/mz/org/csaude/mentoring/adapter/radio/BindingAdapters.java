package mz.org.csaude.mentoring.adapter.radio;

import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingListener;

public class BindingAdapters {

    @BindingAdapter("selectedButton")
    public static void setSelectedButton(RadioGroup radioGroup, String selectedOption) {
        if (selectedOption == null) {
            radioGroup.clearCheck();
            return;
        }

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            if (selectedOption.equals(radioButton.getTag())) {
                radioButton.setChecked(true);
                break;
            }
        }
    }

    @BindingAdapter("selectedButtonAttrChanged")
    public static void setSelectedButtonListener(RadioGroup radioGroup, final InverseBindingListener listener) {
        if (listener == null) {
            return;
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = group.findViewById(checkedId);
            if (checkedRadioButton != null) {
                listener.onChange();
            }
        });
    }

    @BindingAdapter("app:srcCompat")
    public static void setImageResource(ImageView imageView, int resId) {
        imageView.setImageResource(resId);
    }

}