package mz.org.csaude.mentoring.service.section;

import android.app.Application;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.section.SectionDAO;
import mz.org.csaude.mentoring.model.form.Section;

public class SectionServiceImpl extends BaseServiceImpl<Section> implements SectionService {

    private final SectionDAO sectionDAO;

    public SectionServiceImpl(Application application) {
        super(application);
        this.sectionDAO = getDataBaseHelper().getSectionDAO();
    }

    @Override
    public Section save(Section record) throws SQLException {
        long id = sectionDAO.insert(record);
        record.setId((int) id); // Assuming Section has an int ID field
        return record;
    }

    @Override
    public Section update(Section record) throws SQLException {
        sectionDAO.update(record);
        return record;
    }

    @Override
    public int delete(Section record) throws SQLException {
        return sectionDAO.delete(record);
    }

    @Override
    public List<Section> getAll() throws SQLException {
        return sectionDAO.queryForAll();
    }

    @Override
    public Section getById(int id) throws SQLException {
        return sectionDAO.queryForId(id);
    }

    public Section saveOrUpdate(Section section) throws SQLException {
        Section s = sectionDAO.getByUuid(section.getUuid());
        if (s != null) {
            section.setId(s.getId());
            update(section);
        } else {
            save(section);
        }
        return section;
    }

    @Override
    public Section getByuuid(String uuid) throws SQLException {
        return sectionDAO.getByUuid(uuid);
    }

    @Override
    public void saveOrUpdateSections(List<Section> sections) {
        for (Section section : sections) {
            try {
                saveOrUpdate(section);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
