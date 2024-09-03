package mz.org.csaude.mentoring.model.partner;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.partner.PartnerDTO;

@Entity(tableName = Partner.TABLE_NAME,
        indices = {
                @Index(value = {Partner.COLUMN_NAME}, unique = true)
        })
public class Partner extends BaseModel implements Listble {

    public static final String TABLE_NAME = "partner";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String MISAU_UUID = "398f0ffeb8fe11edafa10242ac120002";

    @NonNull
    @ColumnInfo(name = COLUMN_NAME)
    private String name;

    @NonNull
    @ColumnInfo(name = COLUMN_DESCRIPTION)
    private String description;

    public Partner() {
    }

    @Ignore
    public Partner(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Ignore
    public Partner(PartnerDTO partnerDTO) {
        super(partnerDTO);
        this.setName(partnerDTO.getName());
        this.setDescription(partnerDTO.getDescription());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int getDrawable() {
        return 0;
    }

    @Override
    public String getCode() {
        return null;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
