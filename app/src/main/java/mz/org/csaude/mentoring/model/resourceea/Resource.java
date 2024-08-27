package mz.org.csaude.mentoring.model.resourceea;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;

import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.resource.ResourceDTO;

@Entity(tableName = Resource.TABLE_NAME,
        indices = {
                @Index(value = {Resource.COLUMN_RESOURCE}, unique = true)
        })
public class Resource extends BaseModel implements Listble {

    public static final String TABLE_NAME = "resources";
    public static final String COLUMN_RESOURCE = "resource";

    @ColumnInfo(name = COLUMN_RESOURCE)
    private String resource;

    public Resource(String resource) {
        this.resource = resource;
    }

    public Resource() {
    }

    @Ignore
    public Resource(ResourceDTO resourceDTO) {
        super(resourceDTO);
        this.setResource(resourceDTO.getResource());
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
