package mz.org.csaude.mentoring.listner.dialog;

public interface IDialogListener {

    void doOnConfirmed();

    default void doOnDeny() {
        // Optional action
    }
    default void doOnConfirmed(String value) {};
}
