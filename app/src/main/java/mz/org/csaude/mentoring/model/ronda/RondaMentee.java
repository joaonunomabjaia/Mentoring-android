package mz.org.csaude.mentoring.model.ronda;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.util.Date;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.ronda.RondaMenteeDTO;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.util.DateUtilities;

@Entity(tableName = RondaMentee.TABLE_NAME,
        foreignKeys = {
                @ForeignKey(entity = Ronda.class,
                        parentColumns = "id",
                        childColumns = RondaMentee.COLUMN_RONDA,
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Tutored.class,
                        parentColumns = "id",
                        childColumns = RondaMentee.COLUMN_MENTEE,
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {RondaMentee.COLUMN_RONDA}),
                @Index(value = {RondaMentee.COLUMN_MENTEE})
        })
public class RondaMentee extends BaseModel {

    public static final String TABLE_NAME = "ronda_mentee";
    public static final String COLUMN_RONDA = "ronda_id";
    public static final String COLUMN_MENTEE = "mentee_id";
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_END_DATE = "end_date";

    @NonNull
    @ColumnInfo(name = COLUMN_RONDA)
    private Integer rondaId;

    @Ignore
    @Relation(parentColumn = COLUMN_RONDA, entityColumn = "id")
    private Ronda ronda;

    @NonNull
    @ColumnInfo(name = COLUMN_MENTEE)
    private Integer menteeId;

    @Relation(parentColumn = COLUMN_MENTEE, entityColumn = "id")
    @Ignore
    private Tutored tutored;

    @NonNull
    @ColumnInfo(name = COLUMN_START_DATE)
    private Date startDate;

    @ColumnInfo(name = COLUMN_END_DATE)
    private Date endDate;

    public RondaMentee() {
    }

    @Ignore
    public RondaMentee(Ronda ronda, Tutored tutored, Date startDate) {
        this.ronda = ronda;
        this.tutored = tutored;
        this.startDate = startDate;
        this.rondaId = ronda.getId();
        this.menteeId = tutored.getId();
    }

    @Ignore
    public RondaMentee(RondaMenteeDTO rondaMenteeDTO) {
        super(rondaMenteeDTO);
        this.setStartDate(rondaMenteeDTO.getStartDate());
        this.setEndDate(rondaMenteeDTO.getEndDate());
        if (rondaMenteeDTO.getMentee() != null) {
            this.setTutored(new Tutored(rondaMenteeDTO.getMentee()));
            this.menteeId = this.tutored.getId();
        }
        if (rondaMenteeDTO.getRonda() != null) {
            this.setRonda(new Ronda(rondaMenteeDTO.getRonda()));
            this.rondaId = this.ronda.getId();
        }
    }

    public static RondaMentee fastCreate(Ronda ronda, Tutored mentee) {
        return new RondaMentee(ronda, mentee, DateUtilities.getCurrentDate());
    }

    public Integer getRondaId() {
        return rondaId;
    }

    public void setRondaId(Integer rondaId) {
        this.rondaId = rondaId;
    }

    public Ronda getRonda() {
        return ronda;
    }

    public void setRonda(Ronda ronda) {
        this.ronda = ronda;
        this.rondaId = ronda.getId();
    }

    public Integer getMenteeId() {
        return menteeId;
    }

    public void setMenteeId(Integer menteeId) {
        this.menteeId = menteeId;
    }

    public Tutored getTutored() {
        return tutored;
    }

    public void setTutored(Tutored tutored) {
        this.tutored = tutored;
        this.menteeId = tutored.getId();
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
