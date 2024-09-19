package mz.org.csaude.mentoring.model.professionalCategory;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.professionalcategory.ProfessionalCategoryDTO;

@Entity(tableName = ProfessionalCategory.TABLE_NAME,
        indices = {
                @Index(value = {ProfessionalCategory.COLUMN_CODE}, unique = true)
        })
public class ProfessionalCategory extends BaseModel implements Listble {

    public static final String TABLE_NAME = "professional_category";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CODE = "code";

    @NonNull
    @ColumnInfo(name = COLUMN_DESCRIPTION)
    private String description;

    @NonNull
    @ColumnInfo(name = COLUMN_CODE)
    private String code;

    public ProfessionalCategory() {
    }

    @Ignore
    public ProfessionalCategory(ProfessionalCategoryDTO professionalCategoryDTO) {
        super(professionalCategoryDTO);
        this.setDescription(professionalCategoryDTO.getDescription());
        this.setCode(professionalCategoryDTO.getCode());
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public int getDrawable() {
        return 0;
    }

    @Override
    public String getCode() {
        return this.code;
    }
}
