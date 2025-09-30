package mz.org.csaude.mentoring.base.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.base.dto.BaseEntityDTO;
import mz.org.csaude.mentoring.util.LifeCycleStatus;
import mz.org.csaude.mentoring.util.SyncSatus;

@Entity(indices = {@Index(value = "uuid", unique = true)})
public abstract class BaseModel implements Serializable, Listble {

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_UUID = "uuid";
    public static final String COLUMN_LIFE_CYCLE_STATUS = "life_cycle_status";
    public static final String COLUMN_SYNC_STATUS = "sync_status";
    public static final String COLUMN_CREATED_AT = "created_at";
    public static final String COLUMN_UPDATED_AT = "updated_at";
    public static final String COLUMN_CREATED_BY = "created_by";
    public static final String COLUMN_UPDATED_BY = "updated_by";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    private Integer id;

    @ColumnInfo(name = COLUMN_UUID)
    private String uuid;

    @Ignore
    protected String listTyp;

    @ColumnInfo(name = COLUMN_SYNC_STATUS)
    private SyncSatus syncStatus;

    @ColumnInfo(name = COLUMN_CREATED_AT)
    private Date createdAt;

    @ColumnInfo(name = COLUMN_UPDATED_AT)
    private Date updatedAt;

    @Ignore
    protected boolean selected;

    @ColumnInfo(name = COLUMN_CREATED_BY)
    private String createdByUuid;

    @ColumnInfo(name = COLUMN_UPDATED_BY)
    private String updatedByUuid;

    @ColumnInfo(name = COLUMN_LIFE_CYCLE_STATUS)
    private LifeCycleStatus lifeCycleStatus = LifeCycleStatus.ACTIVE;

    @Ignore
    protected int listPosition;

    public BaseModel() {
    }

    @Ignore
    public BaseModel(String uuid) {
        this.uuid = uuid;
    }

    @Ignore
    public BaseModel(BaseEntityDTO baseEntityDTO) {
        this.uuid = baseEntityDTO.getUuid();
        this.setCreatedAt(baseEntityDTO.getCreatedAt());
        this.setUpdatedAt(baseEntityDTO.getUpdatedAt());
        this.setLifeCycleStatus(baseEntityDTO.getLifeCycleStatus());
        this.setCreatedByUuid(baseEntityDTO.getCreatedByuuid());
        this.setUpdatedByUuid(baseEntityDTO.getUpdatedByuuid());
    }

    public BaseModel(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }

    @Override
    public int getDrawable() {
        return 0;
    }

    public int getListPosition() {
        return listPosition;
    }

    @Override
    public String getDescription() {
        return null;
    }

    public LifeCycleStatus getLifeCycleStatus() {
        return lifeCycleStatus;
    }

    public void setLifeCycleStatus(LifeCycleStatus lifeCycleStatus) {
        this.lifeCycleStatus = lifeCycleStatus;
    }

    public SyncSatus getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(SyncSatus syncStatus) {
        this.syncStatus = syncStatus;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String validade() {
        return null;
    }

    @Override
    public void setListType(ListTypes type) {
        this.listTyp = String.valueOf(type);
    }

    @Override
    public String getListType() {
        return this.listTyp;
    }

    @Override
    public String getCode() {
        return null;
    }

    @Override
    public void setItemSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public String getExtraInfo() {
        return null;
    }

    @JsonIgnore
    public boolean isPending() {
        return this.syncStatus.equals(SyncSatus.PENDING);
    }

    public String getCreatedByUuid() {
        return createdByUuid;
    }

    public void setCreatedByUuid(String createdByUuid) {
        this.createdByUuid = createdByUuid;
    }

    public String getUpdatedByUuid() {
        return updatedByUuid;
    }

    public void setUpdatedByUuid(String updatedByUuid) {
        this.updatedByUuid = updatedByUuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseModel baseModel = (BaseModel) o;
        return Objects.equals(id, baseModel.id) && Objects.equals(uuid, baseModel.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uuid);
    }

    @Override
    public String toString() {
        return "BaseModel{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", lifeCycleStatus=" + lifeCycleStatus +
                ", syncStatus=" + syncStatus +
                '}';
    }
}
