package mz.org.csaude.mentoring.model.rondatype;

import androidx.annotation.NonNull;

public enum EnumRondaType {
    SESSAO_ZERO("SESSAO_ZERO", "Sessão Zero"),
    RONDA_MENTORIA("RONDA_MENTORIA", "Ciclo/Ronda"),
    RONDA_SEMESTRAL("RONDA_SEMESTRAL", "Semestral");

    private final String code;
    private final String defaultTitle;

    EnumRondaType(String code, String defaultTitle) {
        this.code = code;
        this.defaultTitle = defaultTitle;
    }

    public String code() { return code; }
    public String defaultTitle() { return defaultTitle; }

    @NonNull
    public static EnumRondaType fromCode(String code) {
        if (code == null) return RONDA_MENTORIA;
        for (EnumRondaType t : values()) {
            if (t.code.equalsIgnoreCase(code)) return t;
        }
        return RONDA_MENTORIA; // fallback
    }

    @NonNull
    public static EnumRondaType fromDescription(String description) {
        if (description == null) return RONDA_MENTORIA;
        for (EnumRondaType t : values()) {
            if (t.defaultTitle.equalsIgnoreCase(description)) return t;
        }
        return RONDA_MENTORIA;
    }
}
