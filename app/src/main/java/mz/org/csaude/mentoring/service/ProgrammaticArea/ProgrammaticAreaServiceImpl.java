package mz.org.csaude.mentoring.service.ProgrammaticArea;

import android.app.Application;

import androidx.room.Transaction;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.program.ProgramDAO;
import mz.org.csaude.mentoring.dao.programmaticArea.ProgrammaticAreaDAO;
import mz.org.csaude.mentoring.dto.programmaticArea.ProgrammaticAreaDTO;
import mz.org.csaude.mentoring.model.program.Program;
import mz.org.csaude.mentoring.model.programmaticArea.ProgrammaticArea;
import mz.org.csaude.mentoring.model.user.User;

public class ProgrammaticAreaServiceImpl extends BaseServiceImpl<ProgrammaticArea> implements ProgrammaticAreaService {

    ProgrammaticAreaDAO programmaticAreaDAO;
    ProgramDAO programDAO;

    public ProgrammaticAreaServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application) throws SQLException {
        super.init(application);
        this.programmaticAreaDAO = getDataBaseHelper().getProgrammaticAreaDAO();
        this.programDAO = getDataBaseHelper().getProgramDAO();
    }

    @Override
    public ProgrammaticArea save(ProgrammaticArea record) throws SQLException {
        this.programmaticAreaDAO.insert(record);
        return record;
    }

    @Override
    public ProgrammaticArea update(ProgrammaticArea record) throws SQLException {
        this.programmaticAreaDAO.update(record);
        return record;
    }

    @Override
    public int delete(ProgrammaticArea record) throws SQLException {
        return this.programmaticAreaDAO.delete(record);
    }

    @Override
    public List<ProgrammaticArea> getAll() throws SQLException {
        return this.programmaticAreaDAO.queryForAll();
    }

    @Override
    public ProgrammaticArea getById(int id) throws SQLException {
        return this.programmaticAreaDAO.queryForId(id);
    }

    @Override
    public void saveOrUpdateProgrammaticAreas(List<ProgrammaticAreaDTO> programmaticAreaDTOS) throws SQLException {
        for (ProgrammaticAreaDTO programmaticAreaDTO: programmaticAreaDTOS) {
            this.saveOrUpdateProgrammaticArea(programmaticAreaDTO);
        }
    }

    @Override
    @Transaction
    public ProgrammaticArea saveOrUpdateProgrammaticArea(ProgrammaticAreaDTO programmaticAreaDTO) throws SQLException {
        ProgrammaticArea pa = this.programmaticAreaDAO.getByUuid(programmaticAreaDTO.getUuid());
        ProgrammaticArea programmaticArea = programmaticAreaDTO.getProgrammaticArea();
        Program p = programDAO.getByUuid(programmaticArea.getProgram().getUuid());
        if (p == null) {
            programDAO.insert(programmaticArea.getProgram());
        } else programmaticArea.setProgram(p);

        if(pa!=null) {
            programmaticArea.setId(pa.getId());
        }
        this.programmaticAreaDAO.createOrUpdate(programmaticArea);
        return programmaticArea;
    }

    @Override
    public ProgrammaticArea getByuuid(String uuid) throws SQLException {
        return programmaticAreaDAO.getByUuid(uuid);
    }
}
