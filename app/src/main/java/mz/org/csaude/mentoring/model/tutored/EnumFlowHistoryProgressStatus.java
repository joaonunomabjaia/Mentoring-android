package mz.org.csaude.mentoring.model.tutored;

import androidx.annotation.NonNull;

public enum EnumFlowHistoryProgressStatus {

    INICIO("INICIO", "Início"),
    ISENTO("ISENTO", "Isento"),
    AGUARDA_INICIO("AGUARDA_INICIO", "Aguarda Início"),
    TERMINADO("TERMINADO", "Terminado"),
    INTERROMPIDO("INTERROMPIDO", "Interrompido");

    private final String code;
    private final String label;

    EnumFlowHistoryProgressStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String code() { return code; }

    public String label() { return label; }

    @NonNull
    public static EnumFlowHistoryProgressStatus fromCode(String code) {
        if (code == null) return INICIO;
        for (EnumFlowHistoryProgressStatus s : values()) {
            if (s.code.equalsIgnoreCase(code)) return s;
        }
        return INICIO;
    }

    @NonNull
    public static EnumFlowHistoryProgressStatus fromLabel(String label) {
        if (label == null) return INICIO;
        for (EnumFlowHistoryProgressStatus s : values()) {
            if (s.label.equalsIgnoreCase(label)) return s;
        }
        return INICIO;
    }

    @Override
    public String toString() { return label; }
}
