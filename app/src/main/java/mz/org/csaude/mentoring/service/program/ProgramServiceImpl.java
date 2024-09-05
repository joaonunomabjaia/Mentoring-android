package mz.org.csaude.mentoring.service.program;

import android.app.Application;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.program.ProgramDAO;
import mz.org.csaude.mentoring.dto.program.ProgramDTO;
import mz.org.csaude.mentoring.model.program.Program;

public class ProgramServiceImpl extends BaseServiceImpl<Program> implements ProgramService {

    ProgramDAO programDAO;

    public ProgramServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application) throws SQLException {
        super.init(application);
        this.programDAO = getDataBaseHelper().getProgramDAO();
    }

    @Override
    public Program save(Program record) throws SQLException {
        this.programDAO.insert(record);
        return record;
    }

    @Override
    public Program update(Program record) throws SQLException {
        this.programDAO.update(record);
        return record;
    }

    @Override
    public int delete(Program record) throws SQLException {
        return this.programDAO.delete(record);
    }

    @Override
    public List<Program> getAll() throws SQLException {
        return this.programDAO.queryForAll();
    }

    @Override
    public Program getById(int id) throws SQLException {
        return this.programDAO.queryForId(id);
    }

    @Override
    public void saveOrUpdatePrograms(List<ProgramDTO> programDTOS) throws SQLException {
        for (ProgramDTO programDTO: programDTOS) {
             this.saveOrUpdateProgram(programDTO);
        }
    }

    @Override
    public Program saveOrUpdateProgram(ProgramDTO programDTO) throws SQLException {
        Program p = this.programDAO.getByUuid(programDTO.getUuid());
        Program program = programDTO.getProgram();
        if(p!=null) {
            program.setId(p.getId());
        }
        this.programDAO.createOrUpdate(program);
        return program;
    }

    @Override
    public Program getByuuid(String uuid) throws SQLException {
        return programDAO.getByUuid(uuid);
    }
}
