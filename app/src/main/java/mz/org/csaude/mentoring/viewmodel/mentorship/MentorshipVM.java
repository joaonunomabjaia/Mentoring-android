package mz.org.csaude.mentoring.viewmodel.mentorship;

import android.app.Application;
import android.app.Dialog;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import mz.org.csaude.mentoring.BR;
import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.base.viewModel.BaseViewModel;
import mz.org.csaude.mentoring.listner.dialog.IDialogListener;
import mz.org.csaude.mentoring.model.answer.Answer;
import mz.org.csaude.mentoring.model.evaluationLocation.EvaluationLocation;
import mz.org.csaude.mentoring.model.evaluationType.EvaluationType;
import mz.org.csaude.mentoring.model.form.Form;
import mz.org.csaude.mentoring.model.form.FormSection;
import mz.org.csaude.mentoring.model.formSectionQuestion.FormSectionQuestion;
import mz.org.csaude.mentoring.model.location.Cabinet;
import mz.org.csaude.mentoring.model.mentorship.Door;
import mz.org.csaude.mentoring.model.mentorship.Mentorship;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.util.DateUtilities;
import mz.org.csaude.mentoring.util.SyncSatus;
import mz.org.csaude.mentoring.util.Utilities;
import mz.org.csaude.mentoring.view.mentorship.CreateMentorshipActivity;
import mz.org.csaude.mentoring.view.session.SessionClosureActivity;
import mz.org.csaude.mentoring.view.session.SessionSummaryActivity;

public class MentorshipVM extends BaseViewModel implements IDialogListener {

    private String CURR_MENTORSHIP_STEP = "";
    public static final String CURR_MENTORSHIP_STEP_TABLE_SELECTION = "TABLE_SELECTION";
    public static final String CURR_MENTORSHIP_STEP_MENTEE_SELECTION = "MENTEE_SELECTION";
    public static final String CURR_MENTORSHIP_STEP_PERIOD_SELECTION = "PERIOD_SELECTION";
    public static final String CURR_MENTORSHIP_STEP_QUESTION_SELECTION = "QUESTION_SELECTION";
    public static final String CURR_MENTORSHIP_STEP_DEMOSTRATION_SELECTION = "DEMOSTRATION_SELECTION";


    private Mentorship mentorship;

    private Ronda ronda;

    private Session session;

    private List<Form> forms;

    private List<Tutored> tutoreds;

    private FormSection currentFormSection;

    private boolean mentorshipCompleted;

    public void setCurrMentorshipStep(String step) {
        this.CURR_MENTORSHIP_STEP = step;
        notifyPropertyChanged(BR.currMentorshipStep);
    }

    @Bindable
    public String getCurrMentorshipStep() {
        return CURR_MENTORSHIP_STEP;
    }

    public MentorshipVM(@NonNull Application application) {
        super(application);
    }

    @Override
    public void preInit() {
    }

    public void nextCategory() {
        int currentIndex = this.mentorship.getForm().getFormSections().indexOf(this.currentFormSection);
        // Get the next item if available
        if (currentIndex + 1 < this.mentorship.getForm().getFormSections().size()) {
            setCurrentFormSection(this.mentorship.getForm().getFormSections().get(currentIndex + 1));
            getRelatedActivity().populateQuestionList();
        } else {
            Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.no_previous_category)).show();
            return;
        }
    }

    public boolean isMentorshipCompleted() {
        return mentorshipCompleted;
    }

    public void setMentorshipCompleted(boolean mentorshipCompleted) {
        this.mentorshipCompleted = mentorshipCompleted;
    }

    private boolean allCurrentQuestionsResponded() {
        for (FormSection formSection : this.mentorship.getForm().getFormSections()) {
            for (FormSectionQuestion formSectionQuestion : formSection.getFormSectionQuestions()) {
                if (!Utilities.stringHasValue(formSectionQuestion.getAnswer().getValue())) return false;
            }
        }
        return true;
    }

    public void previousCategory() {
        int currentIndex = this.mentorship.getForm().getFormSections().indexOf(this.currentFormSection);
        if (currentIndex != -1) {  // Ensure the item is in the list
            // Get the previous item if available
            if (currentIndex - 1 >= 0) {
                setCurrentFormSection(this.mentorship.getForm().getFormSections().get(currentIndex - 1));
                getRelatedActivity().populateQuestionList();
            } else {
                Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.no_previous_category)).show();
            }
        }
    }

    public void finnalizeMentorship() {
        if (!allQuestionsResponded()) {
            Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.missising_answers)).show();
            return;
        }

        Utilities.displayCustomConfirmationDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.all_competencies_answered), getRelatedActivity().getString(R.string.yes), getRelatedActivity().getString(R.string.no),this).show();
    }

    private boolean allQuestionsResponded() {
        for (FormSection formSection : this.mentorship.getForm().getFormSections()) {
            for (FormSectionQuestion formSectionQuestion : formSection.getFormSectionQuestions()) {
                if (!Utilities.stringHasValue(formSectionQuestion.getAnswer().getValue()) || formSectionQuestion.getAnswer().getValue().length() <= 1) return false;
            }
        }
        return true;
    }

    @Bindable
    public Date getStartDate() {
        return this.mentorship.getStartDate();
    }

    public void setStartDate(Date startDate) {
        this.mentorship.setStartDate(startDate);
        notifyPropertyChanged(BR.startDate);
    }



    @Bindable
    public Date getEndDate() {
        return this.mentorship.getEndDate();
    }

    public void setEndDate(Date endDate) {
        this.mentorship.setEndDate(endDate);
         notifyPropertyChanged(BR.endDate);
    }

    @Bindable
    public Date getPerformedDate() {
        return this.mentorship.getPerformedDate();
    }

    public void setPerformedDate(Date performedDate) {
        this.mentorship.setPerformedDate(performedDate);
        notifyPropertyChanged(BR.performedDate);
    }


    @Bindable
    public Cabinet getCabinet() {
        return this.mentorship.getCabinet();
    }

    public void setCabinet(Cabinet cabinet) {
        this.mentorship.setCabinet(cabinet);
         notifyPropertyChanged(BR.cabinet);
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    @Bindable
    public Door getDoor() {
        return this.mentorship.getDoor();
    }

    public void setDoor(Door door) {
        this.mentorship.setDoor(door);
         notifyPropertyChanged(BR.door);
    }

    public List<Form> getForms() {
        return forms;
    }

    public List<Form> getTutorForms() {
        try {
            this.forms = getApplication().getFormService().getAllOfTutor(getCurrentTutor());
            return forms;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public void nextStep() {
        if (isTableSelectionStep()) {
            getRelatedActivity().populateMenteesList();
            if (this.mentorship.getForm() == null) {
                Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.no_table_selected)).show();
                return;
            }
            setCurrMentorshipStep(CURR_MENTORSHIP_STEP_MENTEE_SELECTION);

        } else if (isMenteeSelectionStep()) {
            if (this.mentorship.getTutored() == null) {
                Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.no_mentee_selected)).show();
                return;
            }
            setCurrMentorshipStep(CURR_MENTORSHIP_STEP_PERIOD_SELECTION);

        } else if (isPeriodSelectionStep()) {
            if (!isValidPeriod()) return;
            // Perform background operations (like loadQuestion and initial save) in a background thread
            getExecutorService().execute(() -> {
                if (!isMentoringMentorship()) { // Ronda Zero
                    try {
                        this.mentorship.setEvaluationLocation(getApplication().getEvaluationLocationService().getByCode(EvaluationLocation.HEALTH_FACILITY));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    if (!mentorship.getSession().getForm().getEvaluationLocation().isBoth()) {
                        this.mentorship.setEvaluationLocation(mentorship.getSession().getForm().getEvaluationLocation());
                } else if (!hasQuestionForSelectedLocation(mentorship.getSession().getForm(), mentorship.getEvaluationLocation())){
                        runOnMainThread(() -> {
                            Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.no_questions_for_selected_location)).show();
                        });

                        return;
                    }
                }

                loadQuestion();

                for (FormSection formSection : this.mentorship.getForm().getFormSections()) {
                    int responded = 0;
                    for (FormSectionQuestion formSectionQuestion : formSection.getFormSectionQuestions()) {
                        if (Utilities.stringHasValue(formSectionQuestion.getAnswer().getValue())) responded++;
                    }
                    formSection.setExtraInfo(responded + "/" + formSection.getFormSectionQuestions().size());
                }

                // Update UI on the main thread
                runOnMainThread(() -> {
                    getRelatedActivity().loadCategoryAdapter();
                    getRelatedActivity().populateQuestionList();

                    // Save the mentorship if it's not already saved
                    if (mentorship.getId() == null) {
                        // Save the mentorship in the background
                        getExecutorService().execute(() -> {
                            doMentorshipInitialSave();
                        });
                    }

                    setCurrMentorshipStep(CURR_MENTORSHIP_STEP_QUESTION_SELECTION);
                    notifyPropertyChanged(BR.currMentorshipStep);
                });
            });

        } else if (isQuestionSelectionStep()) {
            if (!allQuestionsResponded()) {
                Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.missising_answers)).show();
                return;
            }

            if (this.isMentoringMentorship() && this.mentorship.isPatientEvaluation()) {
                setCurrMentorshipStep(CURR_MENTORSHIP_STEP_DEMOSTRATION_SELECTION);
            } else {
                finnalizeMentorship();
            }

        } else if (isDemostrationSelectionStep()) {
            finnalizeMentorship();
        }

        notifyPropertyChanged(BR.currMentorshipStep);
    }

    private boolean hasQuestionForSelectedLocation(Form form, EvaluationLocation evaluationLocation) {
        return getApplication().getFormService().hasQuestionsForSelectedLocation(form, evaluationLocation);
    }

    public boolean isSelectLocation(){
        if (!isMentoringMentorship()) return false;
        return mentorship.getSession().getForm().getEvaluationLocation().isBoth();
    }

    public void setMentorship(Mentorship mentorship) {
        this.mentorship = mentorship;
    }

    public Mentorship getMentorship() {
        return mentorship;
    }

    private void doMentorshipInitialSave() {
        try {
            for (FormSection formSection : this.mentorship.getForm().getFormSections()) {
                for (FormSectionQuestion formSectionQuestion : formSection.getFormSectionQuestions()) {
                    this.mentorship.addAnswer(formSectionQuestion.getAnswer());
                }
            }
            if (ronda.isRondaZero()) {
                this.mentorship.getTutored().setZeroEvaluationDone(true);
                this.mentorship.getSession().setTutored(this.mentorship.getTutored());
                this.mentorship.getSession().setStartDate(this.mentorship.getStartDate());
                this.mentorship.getSession().setSyncStatus(null);
            } else {
                this.mentorship.setTutored(this.session.getTutored());
            }
            if (DateUtilities.isDateBeforeIgnoringTime(this.mentorship.getStartDate(), this.mentorship.getSession().getStartDate())) {
                Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.mentorship_start_date_error)).show();
                return;
            }

            this.mentorship.getSession().setForm(this.mentorship.getForm());

            this.mentorship.setPerformedDate(this.mentorship.getStartDate());
            getApplication().getMentorshipService().save(this.mentorship);
            Log.i("Mentorship initial save", this.mentorship.toString());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isValidPeriod() {
        if (this.mentorship.getStartDate() == null) {
            Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.mentorship_start_date_empty)).show();
            return false;
        }
        if (this.mentorship.getSession().getRonda().isRondaZero()) {
            this.mentorship.getSession().setStartDate(this.mentorship.getStartDate());
        }
        if (DateUtilities.isDateBeforeIgnoringTime(this.mentorship.getStartDate(), this.mentorship.getSession().getStartDate())) {
            Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.mentorship_start_date_error)).show();
            return false;

        }

        if (DateUtilities.isDateAfterIgnoringTime(this.mentorship.getStartDate(), DateUtilities.getCurrentDate())) {
            Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.mentorship_start_date_future)).show();
            return false;
        }
        if (!Utilities.stringHasValue(mentorship.getCabinet().getUuid())) {
            Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.empty_sector_error)).show();
            return false;
        }
        if (!Utilities.stringHasValue(mentorship.getDoor().getUuid())) {
            Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.empty_door_error)).show();
            return false;
        }
        if (this.mentorship.getEvaluationType() == null) {
            Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.empty_evaluation_type_error)).show();
            return false;
        }
        return true;
    }


    @Override
    public CreateMentorshipActivity getRelatedActivity() {
        return (CreateMentorshipActivity) super.getRelatedActivity();
    }

    public void previousStep() {
        if (isTableSelectionStep()) {
            setCurrMentorshipStep(CURR_MENTORSHIP_STEP_MENTEE_SELECTION);
        } else if (isMenteeSelectionStep()) {
            setCurrMentorshipStep(CURR_MENTORSHIP_STEP_PERIOD_SELECTION);
        } else if (isPeriodSelectionStep()) {
            setCurrMentorshipStep(CURR_MENTORSHIP_STEP_QUESTION_SELECTION);
        } else if (isQuestionSelectionStep()) {
            finnalizeMentorship();
        }
        notifyPropertyChanged(BR.currMentorshipStep);
    }

    public void setRonda(Ronda ronda) {
        this.ronda = ronda;
    }

    public Ronda getRonda() {
        return ronda;
    }

    public void setEvaluationType(String evaluationType) {
        // Perform the database operation in a background thread
        getExecutorService().execute(() -> {
            try {
                // Fetch evaluation type from the database
                EvaluationType evaluation = getApplication().getEvaluationTypeService().getByCode(evaluationType);

                // Switch back to the main thread to update the UI and other properties
                runOnMainThread(() -> {
                    this.mentorship.setEvaluationType(evaluation);

                    // Continue with the other logic on the main thread, move determineIterationNumber to background
                    getExecutorService().execute(() -> {
                        try {
                            if (!determineIterationNumber()) {
                                runOnMainThread(() -> this.mentorship.setEvaluationType(null));
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        // Notify that the properties have changed on the main thread
                        runOnMainThread(() -> {
                            notifyPropertyChanged(BR.consultaEvaluation);
                            notifyPropertyChanged(BR.fichaEvaluation);
                        });
                    });
                });
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public void determineMentorshipType() {
        try {
            if (this.mentorship == null) this.mentorship = new Mentorship();
            this.mentorship.setStartDate(DateUtilities.getCurrentDate());
            this.mentorship.setTutor(getApplication().getCurrMentor());
            this.mentorship.setUuid(Utilities.getNewUUID().toString());
            this.mentorship.setSyncStatus(SyncSatus.PENDING);
            this.mentorship.setCreatedAt(DateUtilities.getCurrentDate());
            this.mentorship.setPerformedDate(DateUtilities.getCurrentDate());
            if (this.ronda != null && this.ronda.isRondaZero()) {
                this.mentorship.setSession(generateZeroSession());
                this.mentorship.setIterationNumber(1);
                this.mentorship.setEvaluationType(getApplication().getEvaluationTypeService().getByCode(EvaluationType.CONSULTA));
                this.mentorship.setEvaluationLocation(getApplication().getEvaluationLocationService().getByCode(EvaluationLocation.HEALTH_FACILITY));
            } else {
                this.mentorship.setSession(this.session);
                this.session.getRonda().addSession(this.mentorship.getSession());
                this.session.getRonda().addSession(getApplication().getSessionService().getAllOfRonda(this.session.getRonda()));

                this.mentorship.setForm(this.session.getForm());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Session generateZeroSession() {
        try {
            Session session = new Session();
            session.setRonda(this.ronda);
            session.setUuid(Utilities.getNewUUID().toString());
            session.setCreatedAt(DateUtilities.getCurrentDate());
            session.setSyncStatus(SyncSatus.PENDING);
            session.setStatus(getApplication().getSessionStatusService().getByuuid("953a6a3c-a583-4b96-86ee-91bcab7d3106"));
            session.setEndDate(this.mentorship.getEndDate());
            session.setStartDate(this.mentorship.getStartDate());
            session.setPerformedDate(DateUtilities.getCurrentDate());
            session.addMentorship(this.mentorship);
            if (this.mentorship.getTutored() != null) {
                session.setTutored(this.mentorship.getTutored());
            }

            if (this.mentorship.getForm() != null) {
                session.setForm(this.mentorship.getForm());
            }
            return session;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isTableSelectionStep() {
        return this.CURR_MENTORSHIP_STEP.equals(CURR_MENTORSHIP_STEP_TABLE_SELECTION);
    }

    public boolean isMenteeSelectionStep() {
        return this.CURR_MENTORSHIP_STEP.equals(CURR_MENTORSHIP_STEP_MENTEE_SELECTION);
    }

    public boolean isPeriodSelectionStep() {
        return this.CURR_MENTORSHIP_STEP.equals(CURR_MENTORSHIP_STEP_PERIOD_SELECTION);
    }

    public boolean isQuestionSelectionStep() {
        return this.CURR_MENTORSHIP_STEP.equals(CURR_MENTORSHIP_STEP_QUESTION_SELECTION);
    }
    public boolean isDemostrationSelectionStep() {
        return this.CURR_MENTORSHIP_STEP.equals(CURR_MENTORSHIP_STEP_DEMOSTRATION_SELECTION);
    }

    public void unselectAll() {
        for (Form form: forms){
            form.setItemSelected(false);
        }
    }

    public void selectForm(int position) {
        for (Form form : getForms()) {
            if (form.isSelected()) form.setItemSelected(false);
        }
        Form form = getForms().get(position);
        form.setItemSelected(true);
        mentorship.setForm(form);
    }

    public List<Tutored> getTutoreds() {
        return tutoreds;
    }

    public List<Tutored> getMentees() {
        try {
            this.tutoreds = getApplication().getTutoredService().getAllOfRondaForZeroEvaluation(this.mentorship.getSession().getRonda());
            for (Tutored tutored :this.tutoreds) {
                tutored.setListType(Listble.ListTypes.UNDEFINED);
            }
            return this.tutoreds;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void selectMentee(int position) {
        for (Tutored tutored : getTutoreds()) {
            if (tutored.isSelected()) tutored.setItemSelected(false);
        }
        Tutored tutored = getTutoreds().get(position);
        tutored.setItemSelected(true);
        mentorship.setTutored(tutored);
    }

    @Bindable
    public Listble getSelectedDoor() {
        return this.mentorship.getDoor();
    }

    public void setSelectedDoor(Listble selectedDoor) {
        if (selectedDoor == null || !Utilities.stringHasValue(selectedDoor.getDescription())) return;

        this.mentorship.setDoor((Door) selectedDoor);
        notifyPropertyChanged(BR.selectedDoor);
    }

    @Bindable
    public Listble getSelectedEvaluationLocation() {
        return this.mentorship.getEvaluationLocation();
    }

    public void setSelectedEvaluationLocation(Listble selectedEvaluationLocation) {
        if (selectedEvaluationLocation == null || !Utilities.stringHasValue(selectedEvaluationLocation.getDescription())) return;

        this.mentorship.setEvaluationLocation((EvaluationLocation) selectedEvaluationLocation);
        notifyPropertyChanged(BR.selectedEvaluationLocation);
    }

    @Bindable
    public Listble getSelectedSector() {
        return this.mentorship.getCabinet();
    }

    public boolean isMentoringMentorship() {
        return !this.mentorship.getSession().getRonda().isRondaZero();
    }
    public void setSelectedSector(Listble selectedSector) {
        if (selectedSector == null) return;

        this.mentorship.setCabinet((Cabinet) selectedSector);
        notifyPropertyChanged(BR.selectedSector);
    }

    public List<Door> getDoors() {
        try {
            List<Door> doors = new ArrayList<>();
            doors.add(new Door());
            doors.addAll(getApplication().getDoorService().getAll());
            return doors;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Cabinet> getSectors() {
        try {
            List<Cabinet> cabinets = new ArrayList<>();
            cabinets.add(new Cabinet());
            cabinets.addAll(getApplication().getCabinetService().getAll());
            return cabinets;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Bindable
    public boolean isConsultaEvaluation() {
        if (this.mentorship.getEvaluationType() == null) return false;
        return this.mentorship.getEvaluationType().getCode().equals(EvaluationType.CONSULTA);
    }

    @Bindable
    public boolean isFichaEvaluation() {
        if (this.mentorship.getEvaluationType() == null) return false;
        return this.mentorship.getEvaluationType().getCode().equals(EvaluationType.FICHA);
    }

    private void loadQuestion() {
        try {
            this.mentorship.setForm(getApplication().getFormService().getFullByIdForEvaluation(this.mentorship.getForm().getId(), this.mentorship.getEvaluationType().getCode(), this.mentorship.getEvaluationLocation()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (this.mentorship.getId() == null) {
            for (FormSection formSection : this.mentorship.getForm().getFormSections()) {
                formSection.setExtraInfo("0/0");
                for (FormSectionQuestion formSectionQuestion : formSection.getFormSectionQuestions()) {
                    formSectionQuestion.setAnswer(new Answer());
                    formSectionQuestion.getAnswer().setQuestion(formSectionQuestion.getQuestion());
                    formSectionQuestion.getAnswer().setSyncStatus(SyncSatus.PENDING);
                    formSectionQuestion.getAnswer().setFormSectionQuestion(formSectionQuestion);
                    formSectionQuestion.getAnswer().setUuid(Utilities.getNewUUID().toString());
                    formSectionQuestion.getAnswer().setCreatedAt(DateUtilities.getCurrentDate());
                    formSectionQuestion.getAnswer().setMentorship(this.mentorship);
                    formSectionQuestion.getAnswer().setForm(this.mentorship.getForm());
                    formSectionQuestion.getAnswer().setValue("");
                }
            }
        } else {
            for (FormSection formSection : this.mentorship.getForm().getFormSections()) {
                for (FormSectionQuestion formSectionQuestion : formSection.getFormSectionQuestions()) {
                    formSectionQuestion.setAnswer(getRelatedAnswer(formSectionQuestion));
                }
            }
        }
        setCurrentFormSection(this.mentorship.getForm().getFormSections().get(0));
    }

    private Answer getRelatedAnswer(FormSectionQuestion formSectionQuestion) {
        for (Answer answer : this.mentorship.getAnswers()) {
            if (answer.getQuestion().equals(formSectionQuestion.getQuestion())) {
                return answer;
            }
        }
        return null;
    }

    @Bindable
    public Listble getCurrentFormSection() {
        return currentFormSection;
    }

    public void setCurrentFormSection(Listble currentFormSection) {
        this.currentFormSection = (FormSection) currentFormSection;
        getRelatedActivity().populateQuestionList();
        notifyPropertyChanged(BR.currentFormSection);
    }

    @Override
    public void doOnConfirmed() {
        // Show a progress dialog while the mentorship operations are performed
        Dialog progressDialog = Utilities.showLoadingDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.processando));

        if (allQuestionsResponded()) {
            // Perform mentorship save operation in the background
            getExecutorService().execute(() -> {
                try {
                    doSaveMentorship(); // Save mentorship in the background

                    // Update the UI on the main thread after saving
                    runOnMainThread(() -> {
                        // Dismiss the progress dialog after saving
                        dismissProgress(progressDialog);
                        showMentorshipSummary();
                    });

                } catch (Exception e) {
                    // Handle any exception and dismiss the progress dialog
                    runOnMainThread(() -> {
                        dismissProgress(progressDialog);
                        Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.error_saving_evaluation)).show();
                    });
                }
            });
        } else {
            // Perform mentorship state save operation in the background
            getExecutorService().execute(() -> {
                try {
                    doMentorshipStateSave(); // Save mentorship state in the background

                    // Dismiss the progress dialog after saving the state
                    runOnMainThread(() -> {
                        dismissProgress(progressDialog);
                    });

                } catch (Exception e) {
                    // Handle any exception and dismiss the progress dialog
                    runOnMainThread(() -> {
                        Log.e("MentorshipStateSave", e.getMessage());
                        dismissProgress(progressDialog);
                        Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.error_saving_evaluation)).show();
                    });
                }
            });
        }
    }


    private void showMentorshipSummary() {
    }

    private void
    doSaveMentorship() {
        try {
            this.mentorship.getAnswers().clear();
            for (FormSection formSection : this.mentorship.getForm().getFormSections()) {
                for (FormSectionQuestion formSectionQuestion : formSection.getFormSectionQuestions()) {
                    this.mentorship.addAnswer(formSectionQuestion.getAnswer());
                }
            }

            if (this.session == null) this.session = mentorship.getSession();

            if (this.session.getTutored() == null) {
                this.session.setTutored(getApplication().getTutoredService().getById(this.session.getMenteeId()));
            }

            this.mentorship.setEndDate(DateUtilities.getCurrentDate());
            if (ronda.isRondaZero()) {
                this.mentorship.getTutored().setZeroEvaluationDone(true);
                this.mentorship.getSession().setTutored(this.mentorship.getTutored());
                this.mentorship.getSession().setEndDate(this.mentorship.getEndDate());
                this.mentorship.getSession().setStartDate(this.mentorship.getStartDate());
                this.mentorship.getSession().setSyncStatus(SyncSatus.PENDING);
            } else {
                if (mentorship.getTutored() == null) {
                    this.mentorship.setTutored(this.session.getTutored());
                }
            }

            if (DateUtilities.isDateBeforeIgnoringTime(this.mentorship.getStartDate(), this.mentorship.getSession().getStartDate())) {
                runOnMainThread(()->{
                    Utilities.displayAlertDialog(getRelatedActivity(), getRelatedActivity().getString(R.string.mentorship_start_date_error)).show();
                });
                return;
            }

            this.mentorship.getSession().setForm(this.mentorship.getForm());
            if (isMentoringMentorship()) {
                List<Mentorship> completedMentorships = getApplication().getMentorshipService().getAllOfSession(this.mentorship.getSession());
                this.mentorship.getSession().setMentorships(new ArrayList<>());
                this.mentorship.getSession().addMentorships(completedMentorships);
            }

            this.mentorship.getSession().addMentorship(this.mentorship);

            this.ronda.setSessions(getApplication().getSessionService().getAllOfRonda(this.ronda));
            this.ronda.addSession(this.mentorship.getSession());
            ronda.setRondaMentees(getApplication().getRondaMenteeService().getAllOfRonda(this.ronda));

            getApplication().getMentorshipService().save(this.mentorship);
            Log.i("Saved Mentorship", this.mentorship.toString());
            if (isMentoringMentorship()) {
                if (this.mentorship.getSession().canBeClosed()) {
                    runOnMainThread(()->initSessionClosure(this.mentorship.getSession()));
                } else {
                    if (this.mentorship.isPatientEvaluation()) {
                        runOnMainThread(this::goToMentorshipSummary);
                    }
                    runOnMainThread(()->getRelatedActivity().finish());
                }
            } else {
                this.ronda.tryToCloseRonda();
                if (this.ronda.isRondaCompleted()) {
                    getApplication().getRondaService().closeRonda(this.ronda);
                }
                runOnMainThread(this::goToMentorshipSummary);
            }
        getCurrentStep().changeToList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void goToMentorshipSummary() {
        Map<String, Object> params = new HashMap<>();

        this.mentorship.getSession().setMentorships(Collections.emptyList());
        this.mentorship.getSession().getRonda().setSessions(Collections.emptyList());
        this.mentorship.getSession().getForm().setFormSections(Collections.emptyList());
        params.put("session", this.mentorship.getSession());
        params.put("mentorshipuuid", this.mentorship.getUuid());
        getRelatedActivity().nextActivityFinishingCurrent(SessionSummaryActivity.class, params);
    }

    private void initSessionClosure(Session session) {
        Map<String, Object> params = new HashMap<>();
        session.setMentorships(Collections.emptyList());
        session.getRonda().setSessions(Collections.emptyList());
        session.getForm().setFormSections(Collections.emptyList());
        params.put("session", session);
        getRelatedActivity().nextActivityFinishingCurrent(SessionClosureActivity.class, params);
    }

    private boolean determineIterationNumber() throws SQLException {
        List<Mentorship> completedMentorships = getApplication().getMentorshipService().getAllOfSession(this.mentorship.getSession());
        List<Mentorship> similarMentorships = completedMentorships.stream()
                .filter(m -> m.getEvaluationType().getCode().equals(this.mentorship.getEvaluationType().getCode()))
                .collect(Collectors.toList());

        int maxIteration = similarMentorships.stream()
                .mapToInt(Mentorship::getIterationNumber)
                .max()
                .orElse(0);

        this.mentorship.setIterationNumber(maxIteration + 1);

        return checkIterationLimit();
    }

    private boolean checkIterationLimit() {
        String evaluationTypeCode = this.mentorship.getEvaluationType().getCode();
        int target = 0;
        String messageKey;

        if (evaluationTypeCode.equals(EvaluationType.CONSULTA)) {
            target = this.mentorship.getForm().getTargetPatient();
            messageKey = getRelatedActivity().getString(R.string.error_max_consultation, target);
        } else if (evaluationTypeCode.equals(EvaluationType.FICHA)) {
            target = this.mentorship.getForm().getTargetFile();
            messageKey = getRelatedActivity().getString(R.string.error_max_file, target);
        } else {
            messageKey = null;
        }

        if (this.mentorship.getIterationNumber() > target) {
            // Run the dialog display on the UI thread
            getRelatedActivity().runOnUiThread(() -> {
                Utilities.displayAlertDialog(getRelatedActivity(), messageKey).show();
            });
            return false;
        }

        return true;
    }

    @Override
    public void doOnDeny() {
        getRelatedActivity().onBackPressed();
    }

    public void setQuestionAnswer(FormSectionQuestion formSectionQuestion, String answerValue) {
        getExecutorService().execute(()->{
            formSectionQuestion.getAnswer().setValue(answerValue);
            try {
                getApplication().getAnswerService().update(formSectionQuestion.getAnswer());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            int i=0;
            List<FormSectionQuestion> formSectionQuestionList = currentFormSection.getFormSectionQuestions();

            for (FormSectionQuestion fq : formSectionQuestionList) {
                if (Utilities.stringHasValue(fq.getAnswer().getValue()) && fq.getAnswer().getValue().length() > 1) i++;
            }
            for (FormSection formSection : this.mentorship.getForm().getFormSections()){
                if (formSection.equals(this.currentFormSection)){
                    formSection.setExtraInfo(i + "/" + formSectionQuestionList.size());
                }
            }

            currentFormSection.setExtraInfo(i + "/" + formSectionQuestionList.size());
            runOnMainThread(()->getRelatedActivity().reloadCategoryAdapter());
        });
    }

    public String getStartTime() {
        return DateUtilities.formatToHHMI(DateUtilities.getCurrentDate());
    }

    @Bindable
    public String getDemostrationDetails() {
        return this.mentorship.getDemonstrationDetails();
    }

    public void setDemostrationDetails(String demonstrationDetails) {
        this.mentorship.setDemonstrationDetails(demonstrationDetails);
        notifyPropertyChanged(BR.demostrationDetails);
    }

    public void changeDemostrationStatus() {
        this.mentorship.setDemonstration(!this.mentorship.isDemonstration());
        notifyPropertyChanged(BR.demostrationMade);
    }

    @Bindable
    public boolean isDemostrationMade() {
        return this.mentorship.isDemonstration();
    }

    public void setDemostrationMade(boolean demonstrationMade) {
        this.mentorship.setDemonstration(demonstrationMade);
        notifyPropertyChanged(BR.demostrationMade);
    }

    private void doMentorshipStateSave() {
        try {
            this.mentorship.getAnswers().clear();
            for (FormSection formSection : this.mentorship.getForm().getFormSections()) {
                for (FormSectionQuestion formSectionQuestion : formSection.getFormSectionQuestions()) {
                    this.mentorship.addAnswer(formSectionQuestion.getAnswer());
                }
            }
            getApplication().getMentorshipService().save(this.mentorship);
            Log.i("Mentorship state save", this.mentorship.toString());
            runOnMainThread(()->getRelatedActivity().onBackPressed());
        } catch (SQLException e) {
            Log.e("MentorshipVM", e.getMessage());
        }
    }

    public void tryToUpdateMentorship() {
        if (isTableSelectionStep() || isMenteeSelectionStep() || isPeriodSelectionStep() ) {
            doOnDeny();
            return;
        }
        Utilities.displayConfirmationDialog(
                getRelatedActivity(),
                getRelatedActivity().getString(R.string.confirm_save_changes),
                getRelatedActivity().getString(R.string.yes),
                getRelatedActivity().getString(R.string.no),
                this
        ).show();
    }

    @Override
    public void doOnConfirmed(String value) {
        Log.i("MentorshipVM", value);
        getExecutorService().execute(this::doSaveMentorship);
    }

    public List<EvaluationLocation> getEvaluationLocations() {
        List<EvaluationLocation> evaluationLocations = new ArrayList<>();
        evaluationLocations.add(new EvaluationLocation());
        evaluationLocations.addAll(getApplication().getEvaluationLocationService().getByCodes(List.of(EvaluationLocation.HEALTH_FACILITY, EvaluationLocation.COMMUNITY)));
        return evaluationLocations;
    }

    public void loadMentorShipData() {
        try {
            this.mentorship.setAnswers((getApplication().getAnswerService().getAllOfMentorship(mentorship)));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setEvaluationLocation(String locationCode) {
        getExecutorService().execute(() -> {
            try {
                // Busca localização no BD
                EvaluationLocation location =
                        getApplication().getEvaluationLocationService().getByCode(locationCode);

                Cabinet communityCabinet = null;
                Door communityDoor = null;

                if (location != null && location.isCommunityEvaluation()) {
                    communityCabinet = getApplication()
                            .getCabinetService()
                            .getByuuid(Cabinet.COMMUNITY_CABINET_UUID);

                    if (communityCabinet == null) {
                        runOnMainThread(() ->
                                Utilities.displayAlertDialog(
                                        getRelatedActivity(),
                                        getRelatedActivity().getString(R.string.error_evaluation_location)
                                ).show()
                        );
                        return;
                    }

                    communityDoor = getApplication()
                            .getDoorService()
                            .getByCode(Door.COMMUNITY_DOOR);
                }

                Cabinet finalCommunityCabinet = communityCabinet;
                Door finalCommunityDoor = communityDoor;

                runOnMainThread(() -> {
                    this.mentorship.setEvaluationLocation(location);

                    if (location != null && location.isCommunityEvaluation()) {
                        setCabinet(finalCommunityCabinet);
                        setSelectedDoor(finalCommunityDoor);
                    }

                    notifyPropertyChanged(BR.selectedEvaluationLocation);
                    notifyPropertyChanged(BR.communityLocation);
                    notifyPropertyChanged(BR.healthFacilityLocation);
                });

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }


    @Bindable
    public boolean isCommunityLocation() {
        EvaluationLocation loc = this.mentorship.getEvaluationLocation();
        return loc != null && loc.isCommunityEvaluation(); // usa os helpers @JsonIgnore do seu model
    }

    @Bindable
    public boolean isHealthFacilityLocation() {
        EvaluationLocation loc = this.mentorship.getEvaluationLocation();
        return loc != null && loc.isHealthFacilityEvaluation();
    }
}
