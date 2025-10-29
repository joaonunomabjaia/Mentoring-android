package mz.org.csaude.mentoring.adapter.radio;

import android.content.res.ColorStateList;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
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

    @BindingAdapter("srcCompat")
    public static void setSrcCompat(ImageView view, @DrawableRes Integer resId) {
        if (resId != null && resId != 0) {
            view.setImageResource(resId);
        } else {
            view.setImageDrawable(null);
        }
    }

    @BindingAdapter("tint")
    public static void setImageTint(ImageView view, @ColorInt Integer colorInt) {
        if (colorInt == null) {
            view.setImageTintList(null);
        } else {
            view.setImageTintList(ColorStateList.valueOf(colorInt));
        }
    }

}