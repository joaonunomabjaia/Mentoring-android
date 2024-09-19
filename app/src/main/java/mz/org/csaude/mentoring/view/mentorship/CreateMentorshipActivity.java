package mz.org.csaude.mentoring.view.mentorship;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.recyclerview.form.FormAdapter;
import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.adapter.recyclerview.question.QuestionAdapter;
import mz.org.csaude.mentoring.adapter.recyclerview.tutored.TutoredAdapter;
import mz.org.csaude.mentoring.adapter.spinner.listble.ListableSpinnerAdapter;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.databinding.ActivityMentorshipBinding;
import mz.org.csaude.mentoring.listner.recyclerView.ClickListener;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.formQuestion.FormQuestion;
import mz.org.csaude.mentoring.model.location.Cabinet;
import mz.org.csaude.mentoring.model.mentorship.Door;
import mz.org.csaude.mentoring.model.mentorship.Mentorship;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.util.DateUtilities;
import mz.org.csaude.mentoring.util.SpacingItemDecoration;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.viewmodel.mentorship.MentorshipVM;

public class CreateMentorshipActivity extends BaseActivity implements ClickListener.OnItemClickListener {

    private ActivityMentorshipBinding mentorshipBinding;

    private RecyclerView formsRcv;

    private FormAdapter formAdapter;

    private TutoredAdapter tutoredAdapter;

    private ListableSpinnerAdapter sectorAdapter;

    private ListableSpinnerAdapter doorAdapter;
    private ListableSpinnerAdapter categorieAdapter;

    private QuestionAdapter questionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mentorshipBinding = DataBindingUtil.setContentView(this, R.layout.activity_mentorship);
        formsRcv = mentorshipBinding.rcvForms;

        Intent intent = this.getIntent();
        if (intent != null && intent.getExtras() != null) {

            getRelatedViewModel().setMentorship((Mentorship) intent.getExtras().get("mentorship"));

            // Execute determineMentorshipType() on a background thread
            getRelatedViewModel().getExecutorService().execute(() -> {
                loadSectorAdapter();
                loadDoorAdapter();
                if (getRelatedViewModel().getMentorship() == null) {
                    getRelatedViewModel().setSession((Session) intent.getExtras().get("session"));
                    getRelatedViewModel().setRonda((Ronda) intent.getExtras().get("ronda"));

                    // Heavy operation running in the background
                    getRelatedViewModel().determineMentorshipType();

                    // Update UI after background work is done
                    runOnUiThread(() -> {
                        if (getRelatedViewModel().getRonda() == null) {
                            getRelatedViewModel().setRonda(getRelatedViewModel().getSession().getRonda());
                        }
                        if (getRelatedViewModel().getRonda().isRondaZero()) {
                            populateFormList();
                        }

                        // Set toolbar and other UI elements after thread is done
                        setSupportActionBar(mentorshipBinding.toolbar.toolbar);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        getSupportActionBar().setDisplayShowHomeEnabled(true);
                        getSupportActionBar().setTitle(getRelatedViewModel().isMentoringMentorship() ? getString(R.string.evaluation_title)
                                : getString(R.string.zero_session_title));

                        // Set the ViewModel to Data Binding
                        mentorshipBinding.setViewModel(getRelatedViewModel());


                        // Set the current mentorship step from the intent
                        getRelatedViewModel().setCurrMentorshipStep((String) intent.getExtras().get("CURR_MENTORSHIP_STEP"));
                    });
                } else {
                    runOnUiThread(() -> {
                        getRelatedViewModel().setSession(getRelatedViewModel().getMentorship().getSession());
                        getRelatedViewModel().setRonda(getRelatedViewModel().getMentorship().getSession().getRonda());

                        // Same UI updates after thread completion
                        setSupportActionBar(mentorshipBinding.toolbar.toolbar);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        getSupportActionBar().setDisplayShowHomeEnabled(true);
                        getSupportActionBar().setTitle(getRelatedViewModel().isMentoringMentorship()
                                ? getString(R.string.evaluation_title)
                                : getString(R.string.zero_session_title));


                        mentorshipBinding.setViewModel(getRelatedViewModel());
                        //loadSectorAdapter();
                        //loadDoorAdapter();

                        getRelatedViewModel().setCurrMentorshipStep((String) intent.getExtras().get("CURR_MENTORSHIP_STEP"));
                    });
                }
            });

        }

        // Date picker listener
        mentorshipBinding.sessionDate.setOnClickListener(view -> showDatePickerDialog());

        // Time picker listener
        mentorshipBinding.sessionStartTime.setOnClickListener(view -> showTimePickerDialog(mentorshipBinding.sessionStartTime));
    }



    // Method to handle the intent and initialize the ViewModel
    private void handleIntent(Intent intent) {
        if (intent == null || intent.getExtras() == null) return;

        getRelatedViewModel().setMentorship((Mentorship) intent.getExtras().get("mentorship"));
        if (getRelatedViewModel().getMentorship() == null) {
            getRelatedViewModel().setSession((Session) intent.getExtras().get("session"));
            getRelatedViewModel().setRonda((Ronda) intent.getExtras().get("ronda"));
            getRelatedViewModel().determineMentorshipType();

            if (getRelatedViewModel().getRonda() == null) {
                getRelatedViewModel().setRonda(getRelatedViewModel().getSession().getRonda());
            }
            if (getRelatedViewModel().getRonda().isRondaZero()) {
                populateFormList();
            }
        } else {
            getRelatedViewModel().setSession(getRelatedViewModel().getMentorship().getSession());
            getRelatedViewModel().setRonda(getRelatedViewModel().getMentorship().getSession().getRonda());
        }

        getRelatedViewModel().setCurrMentorshipStep((String) intent.getExtras().get("CURR_MENTORSHIP_STEP"));
    }

    // Method to setup toolbar properties
    private void setupToolbar() {
        setSupportActionBar(mentorshipBinding.toolbar.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(getRelatedViewModel().isMentoringMentorship()
                    ? getString(R.string.evaluation_title)
                    : getString(R.string.zero_session_title));

        }
    }

    // Method to show date picker dialog
    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            getRelatedViewModel().setStartDate(DateUtilities.createDate(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year, DateUtilities.DATE_FORMAT));
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    private void showTimePickerDialog(EditText viewTe) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Show the time picker dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Handle the time set event
                String time = hourOfDay + ":" + Utilities.garantirXCaracterOnNumber(minute, 2);
                viewTe.setText(time);
            }
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private void populateFormList() {
        getRelatedViewModel().getExecutorService().execute(()-> {
            List<Form> forms = getRelatedViewModel().getTutorForms();
            runOnUiThread(()->{
                this.formAdapter = new FormAdapter(formsRcv, forms, this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                formsRcv.setLayoutManager(mLayoutManager);
                formsRcv.setItemAnimator(new DefaultItemAnimator());
                int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_spacing);
                SpacingItemDecoration itemDecoration = new SpacingItemDecoration(spacingInPixels);
                formsRcv.addItemDecoration(itemDecoration);
                formsRcv.setAdapter(formAdapter);
            });
        });
    }

    @Override
    public BaseViewModel initViewModel() {
        return new ViewModelProvider(this).get(MentorshipVM.class);
    }

    @Override
    public MentorshipVM getRelatedViewModel() {
        return (MentorshipVM) super.getRelatedViewModel();
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onLongItemClick(View view, int position) {
        if (getRelatedViewModel().isTableSelectionStep()) {
            getRelatedViewModel().selectForm(position);
            formAdapter.notifyDataSetChanged();
        } else if (getRelatedViewModel().isMenteeSelectionStep()) {
            getRelatedViewModel().selectMentee(position);
            tutoredAdapter.notifyDataSetChanged();
        }
    }



    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    public void populateMenteesList() {
        getRelatedViewModel().getExecutorService().execute(()-> {
            List<Tutored> tutoreds = getRelatedViewModel().getMentees();
            runOnUiThread(()->{
                this.tutoredAdapter = new TutoredAdapter(mentorshipBinding.rcvTutored, tutoreds, this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mentorshipBinding.rcvTutored.setLayoutManager(mLayoutManager);
                mentorshipBinding.rcvTutored.setItemAnimator(new DefaultItemAnimator());
                int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_spacing);
                SpacingItemDecoration itemDecoration = new SpacingItemDecoration(spacingInPixels);
                mentorshipBinding.rcvTutored.addItemDecoration(itemDecoration);
                mentorshipBinding.rcvTutored.setAdapter(tutoredAdapter);

            });
        });
    }

    public void loadDoorAdapter() {
        // Fetch the doors in a background thread
            // Access the database in the background thread
            List<Door> doors = getRelatedViewModel().getDoors();

            // Update the UI on the main thread
            runOnUiThread(() -> {
                if (doors != null && !doors.isEmpty()) {
                    // Initialize and set the adapter if the doors list is not null or empty
                    doorAdapter = new ListableSpinnerAdapter(CreateMentorshipActivity.this, R.layout.simple_auto_complete_item, doors);
                    mentorshipBinding.spnDoor.setAdapter(doorAdapter);
                } else {
                    // Handle the case where no doors are available
                    Utilities.displayAlertDialog(CreateMentorshipActivity.this, getString(R.string.no_doors_available)).show();
                }
            });
    }



    public void loadCategoryAdapter() {
        // Fetch the categories in a background thread
            List<Listble> categories = getRelatedViewModel().getCategories();

            // Update the UI on the main thread
            runOnUiThread(() -> {
                if (categories != null && !categories.isEmpty()) {
                    // Initialize and set the adapter if the categories list is not null or empty
                    categorieAdapter = new ListableSpinnerAdapter(CreateMentorshipActivity.this, R.layout.simple_auto_complete_item, categories);
                    mentorshipBinding.spnCurrCategory.setAdapter(categorieAdapter);
                } else {
                    // Handle the case where no categories are available (optional)
                    Utilities.displayAlertDialog(CreateMentorshipActivity.this, getString(R.string.no_categories_available)).show();
                }
            });
    }


    public void reloadCategoryAdapter() {
        // Fetch the categories in a background thread
        getRelatedViewModel().getExecutorService().execute(() -> {
            List<Listble> categories = getRelatedViewModel().getCategories();

            // Update the UI on the main thread
            runOnUiThread(() -> {
                if (categorieAdapter != null) {
                    // If the adapter already exists, notify about data change
                    categorieAdapter.notifyDataSetChanged();
                } else {
                    // Initialize and set the adapter if it doesn't exist yet
                    if (categories != null && !categories.isEmpty()) {
                        categorieAdapter = new ListableSpinnerAdapter(CreateMentorshipActivity.this, R.layout.simple_auto_complete_item, categories);
                        mentorshipBinding.spnCurrCategory.setAdapter(categorieAdapter);
                    } else {
                        // Handle the case where no categories are available
                        Utilities.displayAlertDialog(CreateMentorshipActivity.this, getString(R.string.no_categories_available)).show();
                    }
                }
            });
        });
    }



    public void loadSectorAdapter() {

        // Fetch sectors in the background thread
        getRelatedViewModel().getExecutorService().execute(() -> {
            List<Cabinet> sectors = getRelatedViewModel().getSectors();

            // Ensure we update the UI on the main thread
            runOnUiThread(() -> {

                // Check if the sectors list is null or empty
                if (sectors == null || sectors.isEmpty()) {
                    // Handle no sectors case (e.g., show a message)
                    return;
                }

                if (sectorAdapter != null) {
                    // Notify adapter only if sectors list has changed
                    sectorAdapter.notifyDataSetChanged();
                } else {
                    // Initialize and set adapter if it doesn't exist
                    sectorAdapter = new ListableSpinnerAdapter(this, R.layout.simple_auto_complete_item, sectors);
                    mentorshipBinding.spnSector.setAdapter(sectorAdapter);
                }
            });
        });
    }


    public void populateQuestionList() {
        List<FormQuestion> updatedQuestionList = getRelatedViewModel()
                .getQuestionMap()
                .get(getRelatedViewModel().getCurrQuestionCategory());


        if (questionAdapter == null) {
            int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_qtns_spacing);
            SpacingItemDecoration itemDecoration = new SpacingItemDecoration(spacingInPixels);
            mentorshipBinding.rcvQuestions.addItemDecoration(itemDecoration);
        }
        this.questionAdapter = new QuestionAdapter(mentorshipBinding.rcvQuestions, updatedQuestionList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mentorshipBinding.rcvQuestions.setLayoutManager(mLayoutManager);
        mentorshipBinding.rcvQuestions.setItemAnimator(new DefaultItemAnimator());
        mentorshipBinding.rcvQuestions.setAdapter(questionAdapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //getRelatedViewModel().tryToUpdateMentorship();
                this.getRelatedViewModel().getRelatedActivity().nextActivityFinishingCurrent(MentorshipActivity.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
