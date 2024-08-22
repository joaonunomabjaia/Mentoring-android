package mz.org.csaude.mentoring.model.form;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.Relation;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.model.career.Career;

@Entity(tableName = FormTarget.TABLE_NAME,
        foreignKeys = {
                @ForeignKey(entity = Form.class,
                        parentColumns = "id",
                        childColumns = FormTarget.COLUMN_FORM,
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Career.class,
                        parentColumns = "id",
                        childColumns = FormTarget.COLUMN_CAREER,
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {FormTarget.COLUMN_FORM}),
                @Index(value = {FormTarget.COLUMN_CAREER})
        })
public class FormTarget extends BaseModel {

    public static final String TABLE_NAME = "form_target";
    public static final String COLUMN_FORM = "form_id";
    public static final String COLUMN_CAREER = "career_id";
    public static final String COLUMN_TARGET = "target";

    @ColumnInfo(name = COLUMN_FORM)
    private int formId;

    @Ignore
    @Relation(parentColumn = COLUMN_FORM, entityColumn = "id")
    private Form form;

    @ColumnInfo(name = COLUMN_CAREER)
    private int careerId;

    @Ignore
    @Relation(parentColumn = COLUMN_CAREER, entityColumn = "id")
    private Career career;

    @ColumnInfo(name = COLUMN_TARGET)
    private int target;

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
        this.formId = form.getId();
    }

    public Career getCareer() {
        return career;
    }

    public void setCareer(Career career) {
        this.career = career;
        this.careerId = career.getId();
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }
}
