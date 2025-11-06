package mz.org.csaude.mentoring.model.tutored;

import androidx.annotation.NonNull;

public enum EnumFlowHistory {

    NOVO("NOVO", "Novo"),
    SESSAO_ZERO("SESSAO_ZERO", "Sessão Zero"),
    RONDA_CICLO("RONDA_CICLO", "Ronda / Ciclo ATC"),
    SESSAO_SEMESTRAL("SESSAO_SEMESTRAL", "Sessão Semestral");

    private final String code;
    private final String label;

    EnumFlowHistory(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String code() { return code; }

    public String label() { return label; }

    @NonNull
    public static EnumFlowHistory fromCode(String code) {
        if (code == null) return NOVO;
        for (EnumFlowHistory f : values()) {
            if (f.code.equalsIgnoreCase(code)) return f;
        }
        return NOVO;
    }

    @NonNull
    public static EnumFlowHistory fromLabel(String label) {
        if (label == null) return NOVO;
        for (EnumFlowHistory f : values()) {
            if (f.label.equalsIgnoreCase(label)) return f;
        }
        return NOVO;
    }

    @Override
    public String toString() { return label; }
}
