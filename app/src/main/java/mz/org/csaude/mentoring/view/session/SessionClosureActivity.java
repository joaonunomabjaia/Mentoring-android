package mz.org.csaude.mentoring.view.session;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Calendar;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.databinding.ActivitySessionClosureBinding;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.util.DateUtilities;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.view.ronda.CreateRondaActivity;
import mz.org.csaude.mentoring.viewmodel.session.SessionClosureVM;

public class SessionClosureActivity extends BaseActivity {


    private ActivitySessionClosureBinding binding;
    private BroadcastReceiver finishReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_closure);
        binding.setViewModel(getRelatedViewModel());

        Intent intent = this.getIntent();
        getRelatedViewModel().setSession((Session) intent.getExtras().get("session"));

        setSupportActionBar(binding.toolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.fecho_da_sess_o);

        binding.sessionEndDate.setOnClickListener(view -> {
            int mYear, mMonth, mDay;

            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(SessionClosureActivity.this, R.style.CustomDatePickerDialogTheme, (view1, year, monthOfYear, dayOfMonth) -> getRelatedViewModel().setEndtDate(DateUtilities.createDate(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year, DateUtilities.DATE_FORMAT)), mYear, mMonth, mDay);
            datePickerDialog.show();
            Button positiveButton = datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button negativeButton = datePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

            if (positiveButton != null) positiveButton.setTextColor(Color.BLACK);
            if (negativeButton != null) negativeButton.setTextColor(Color.BLACK);
        });

        binding.nextSessionDate.setOnClickListener(view -> {
            int mYear, mMonth, mDay;

            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(SessionClosureActivity.this, R.style.CustomDatePickerDialogTheme, (view1, year, monthOfYear, dayOfMonth) -> getRelatedViewModel().setNextSessionDate(DateUtilities.createDate(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year, DateUtilities.DATE_FORMAT)), mYear, mMonth, mDay);
            datePickerDialog.show();
            Button positiveButton = datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button negativeButton = datePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

            if (positiveButton != null) positiveButton.setTextColor(Color.BLACK);
            if (negativeButton != null) negativeButton.setTextColor(Color.BLACK);
        });

        finishReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("FINISH_ACTIVITY".equals(intent.getAction())) {
                    finish();
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(finishReceiver, new IntentFilter("FINISH_ACTIVITY"));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Handle the back button click
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public BaseViewModel initViewModel() {
        return new ViewModelProvider(this).get(SessionClosureVM.class);
    }

    @Override
    public SessionClosureVM getRelatedViewModel() {
        return (SessionClosureVM) super.getRelatedViewModel();
    }

    private void switchLayout(){
        getRelatedViewModel().setInitialDataVisible(!getRelatedViewModel().isInitialDataVisible());
    }
    public void changeFormSectionVisibility(View view){
        if(view.equals(binding.pontosFortes)){
            if(binding.pontosFortesLyt.getVisibility() == View.VISIBLE){
                binding.btnCollapsePontosFortes.setImageResource(R.drawable.sharp_arrow_drop_up_24);
                switchLayout();
                Utilities.collapse(binding.pontosFortesLyt);
            } else {
                switchLayout();
                Utilities.expand(binding.pontosFortesLyt);
                binding.btnCollapsePontosFortes.setImageResource(R.drawable.baseline_arrow_drop_down_24);
            }

        } else if(view.equals(binding.aspectosMelhorar)){
            if(binding.aspectosPorMelhorarLyt.getVisibility() == View.VISIBLE){
                binding.btnCollapseAspectosMelhorar.setImageResource(R.drawable.sharp_arrow_drop_up_24);
                switchLayout();
                Utilities.collapse(binding.aspectosPorMelhorarLyt);
            } else {
                switchLayout();
                Utilities.expand(binding.aspectosPorMelhorarLyt);
                binding.btnCollapseAspectosMelhorar.setImageResource(R.drawable.baseline_arrow_drop_down_24);
            }
        }  else if(view.equals(binding.planoMelhoria)){
            if(binding.planoMelhorariaLyt.getVisibility() == View.VISIBLE){
                switchLayout();
                Utilities.collapse(binding.planoMelhorariaLyt);
                binding.btnCollapsePlanoTrabalho.setImageResource(R.drawable.sharp_arrow_drop_up_24);
            } else {
                switchLayout();
                Utilities.expand(binding.planoMelhorariaLyt);
                binding.btnCollapsePlanoTrabalho.setImageResource(R.drawable.baseline_arrow_drop_down_24);
            }
        } else if(view.equals(binding.observacoes)){
            if(binding.observacoesLyt.getVisibility() == View.VISIBLE){
                switchLayout();
                Utilities.collapse(binding.observacoesLyt);
                binding.btnObservacoes.setImageResource(R.drawable.sharp_arrow_drop_up_24);
            } else {
                switchLayout();
                Utilities.expand(binding.observacoesLyt);
                binding.btnObservacoes.setImageResource(R.drawable.baseline_arrow_drop_down_24);
            }
        }

    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(finishReceiver);
        super.onDestroy();
    }
}
