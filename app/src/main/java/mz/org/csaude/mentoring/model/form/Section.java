package mz.org.csaude.mentoring.model.form;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.section.SectionDTO;

@Entity(tableName = Section.COLUMN_TABLE_NAME)
public class Section extends BaseModel {

    public static final String COLUMN_TABLE_NAME = "section";
    public static final String COLUMN_DESCRIPTION = "description";

    @NonNull
    @ColumnInfo(name = COLUMN_DESCRIPTION)
    private String description;

    public Section() {
    }

    public Section(String uuid) {
        super(uuid);
    }

    public Section(SectionDTO questionCategoryDTO){
        super(questionCategoryDTO);
        this.setDescription(questionCategoryDTO.getDescription());
    }
    @Override
    public String toString() {
        return "Section{" +
                "description='" + description + '\'' +
                '}';
    }

    @Override
    @NonNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }
}
