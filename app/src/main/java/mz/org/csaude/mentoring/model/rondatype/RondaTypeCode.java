package mz.org.csaude.mentoring.model.rondatype;

import androidx.annotation.NonNull;

public enum RondaTypeCode {
    ZERO("SESSAO_ZERO", "Sessão Zero"),
    MENTORIA_INTERNA("MENTORIA_INTERNA", "Mentoria Interna"),
    MENTORIA_EXTERNA("MENTORIA_EXTERNA", "Mentoria Externa"),
    SEMESTRAL("SEMESTRAL", "Semestral");
    private final String code;
    private final String defaultTitle;

    RondaTypeCode(String code, String defaultTitle) {
        this.code = code;
        this.defaultTitle = defaultTitle;
    }

    public String code() { return code; }
    public String defaultTitle() { return defaultTitle; }

    @NonNull
    public static RondaTypeCode fromCode(String code) {
        if (code == null) return MENTORIA_INTERNA;
        for (RondaTypeCode c : values()) {
            if (c.code.equalsIgnoreCase(code)) return c;
        }
        return MENTORIA_INTERNA; // fallback
    }
}
