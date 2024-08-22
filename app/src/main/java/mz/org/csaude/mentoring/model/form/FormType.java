package mz.org.csaude.mentoring.model.form;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import mz.org.csaude.mentoring.base.model.BaseModel;

@Entity(tableName = FormType.COLUMN_TABLE_NAME,
        indices = {
                @Index(value = {FormType.COLUMN_CODE}, unique = true)
        })
public class FormType extends BaseModel {

    public static final String COLUMN_TABLE_NAME = "form_type";  // Corrected table name
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CODE = "code";

    @ColumnInfo(name = COLUMN_DESCRIPTION)
    private String description;  // Corrected typo from 'descripion' to 'description'

    @ColumnInfo(name = COLUMN_CODE)
    private String code;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
