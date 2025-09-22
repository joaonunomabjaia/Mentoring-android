package mz.org.csaude.mentoring.util;

import android.widget.RadioGroup;

import androidx.databinding.BindingAdapter;

import mz.org.csaude.mentoring.R;

public class BindingAdapters {

    @BindingAdapter("checkedAnswer")
    public static void setCheckedAnswer(RadioGroup radioGroup, String answer) {
        if (answer == null) {
            radioGroup.clearCheck();
            return;
        }

        int checkedId = -1;

        switch (answer) {
            case "SIM":
                checkedId = R.id.rdb_yes;
                break;
            case "NAO":
                checkedId = R.id.rdb_no;
                break;
            case "N/A":
                checkedId = R.id.rdb_na;
                break;
        }

        // Só atualiza se for diferente para evitar loops infinitos
        if (radioGroup.getCheckedRadioButtonId() != checkedId) {
            radioGroup.check(checkedId);
        }
    }
}
