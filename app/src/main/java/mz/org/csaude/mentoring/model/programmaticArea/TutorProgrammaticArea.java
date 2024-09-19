package mz.org.csaude.mentoring.model.programmaticArea;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.dto.programmaticArea.TutorProgrammaticAreaDTO;
import mz.org.csaude.mentoring.model.tutor.Tutor;

@Entity(tableName = TutorProgrammaticArea.TABLE_NAME,
        foreignKeys = {
                @ForeignKey(entity = Tutor.class,
                        parentColumns = "id",
                        childColumns = TutorProgrammaticArea.COLUMN_TUTOR,
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = ProgrammaticArea.class,
                        parentColumns = "id",
                        childColumns = TutorProgrammaticArea.COLUMN_PROGRAMMATIC_AREA,
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {TutorProgrammaticArea.COLUMN_TUTOR}),
                @Index(value = {TutorProgrammaticArea.COLUMN_PROGRAMMATIC_AREA})
        })
public class TutorProgrammaticArea extends BaseModel {

    public static final String TABLE_NAME = "tutor_programmatic_area";
    public static final String COLUMN_TUTOR = "tutor_id";
    public static final String COLUMN_PROGRAMMATIC_AREA = "programmatic_area_id";

    @NonNull
    @ColumnInfo(name = COLUMN_TUTOR)
    private Integer tutorId;

    @NonNull
    @ColumnInfo(name = COLUMN_PROGRAMMATIC_AREA)
    private Integer programmaticAreaId;

    @Ignore
    private Tutor tutor;

    @Ignore
    private ProgrammaticArea programmaticArea;

    public TutorProgrammaticArea() {
    }

    @Ignore
    public TutorProgrammaticArea(Tutor tutor, ProgrammaticArea programmaticArea) {
        this.tutor = tutor;
        this.tutorId = tutor.getId();
        this.programmaticArea = programmaticArea;
        this.programmaticAreaId = programmaticArea.getId();
    }

    @Ignore
    public TutorProgrammaticArea(TutorProgrammaticAreaDTO tutorProgrammaticAreaDTO) {
        super(tutorProgrammaticAreaDTO);
        this.setTutor(new Tutor(tutorProgrammaticAreaDTO.getTutorDTO()));
        this.setProgrammaticArea(new ProgrammaticArea(tutorProgrammaticAreaDTO.getProgrammaticAreaDTO()));
    }

    public Integer getTutorId() {
        return tutorId;
    }

    public void setTutorId(Integer tutorId) {
        this.tutorId = tutorId;
    }

    public Integer getProgrammaticAreaId() {
        return programmaticAreaId;
    }

    public void setProgrammaticAreaId(Integer programmaticAreaId) {
        this.programmaticAreaId = programmaticAreaId;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
        this.tutorId = tutor.getId();
    }

    public ProgrammaticArea getProgrammaticArea() {
        return programmaticArea;
    }

    public void setProgrammaticArea(ProgrammaticArea programmaticArea) {
        this.programmaticArea = programmaticArea;
        this.programmaticAreaId = programmaticArea.getId();
    }
}
