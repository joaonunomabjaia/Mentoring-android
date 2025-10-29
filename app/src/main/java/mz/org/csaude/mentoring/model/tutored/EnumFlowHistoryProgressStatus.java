package mz.org.csaude.mentoring.model.tutored;

public enum EnumFlowHistoryProgressStatus {

    INICIO("INICIO"),
    ISENTO("ISENTO"),
    AGUARDA_INICIO("AGUARDA INICIO"),
    TERMINADO("TERMINADO"),
    INTERROMPIDO("INTERROMPIDO");

    private final String label;

    EnumFlowHistoryProgressStatus(String label) {
        this.label = label;
    }
}
