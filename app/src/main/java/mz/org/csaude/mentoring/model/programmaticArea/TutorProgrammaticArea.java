package mz.org.csaude.mentoring.model.programmaticArea;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.Relation;

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

    @ColumnInfo(name = COLUMN_TUTOR)
    private int tutorId;

    @Ignore
    @Relation(parentColumn = COLUMN_TUTOR, entityColumn = "id")
    private Tutor tutor;

    @ColumnInfo(name = COLUMN_PROGRAMMATIC_AREA)
    private int programmaticAreaId;

    @Ignore
    @Relation(parentColumn = COLUMN_PROGRAMMATIC_AREA, entityColumn = "id")
    private ProgrammaticArea programmaticArea;

    public TutorProgrammaticArea() {
    }

    public TutorProgrammaticArea(Tutor tutor, ProgrammaticArea programmaticArea) {
        this.tutor = tutor;
        this.tutorId = tutor.getId();
        this.programmaticArea = programmaticArea;
        this.programmaticAreaId = programmaticArea.getId();
    }

    public TutorProgrammaticArea(TutorProgrammaticAreaDTO tutorProgrammaticAreaDTO) {
        super(tutorProgrammaticAreaDTO);
        this.setTutor(new Tutor(tutorProgrammaticAreaDTO.getTutorDTO()));
        this.setProgrammaticArea(new ProgrammaticArea(tutorProgrammaticAreaDTO.getProgrammaticAreaDTO()));
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
