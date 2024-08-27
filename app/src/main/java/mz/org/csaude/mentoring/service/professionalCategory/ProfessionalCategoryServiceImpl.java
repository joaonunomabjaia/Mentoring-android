package mz.org.csaude.mentoring.service.professionalCategory;

import android.app.Application;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.professionalCategoryDAO.ProfessionalCategoryDAO;
import mz.org.csaude.mentoring.dto.professionalcategory.ProfessionalCategoryDTO;
import mz.org.csaude.mentoring.model.professionalCategory.ProfessionalCategory;
import mz.org.csaude.mentoring.model.user.User;

public class ProfessionalCategoryServiceImpl extends BaseServiceImpl<ProfessionalCategory> implements ProfessionalCategoryService {


    private ProfessionalCategoryDAO professionalCategoryDAO;


    public ProfessionalCategoryServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application ) throws SQLException {
        super.init(application );
        this.professionalCategoryDAO = dataBaseHelper.getProfessionalCategoryDAO();
    }

    @Override
    public void saveOrUpdateProfessionalCategorys(List<ProfessionalCategoryDTO> professionalCategoryDTOS) throws SQLException {
        for (ProfessionalCategoryDTO professionalCategoryDTO : professionalCategoryDTOS){
            this.saveOrUpdateProfessionalCategory(professionalCategoryDTO);
        }
    }

    @Override
    public ProfessionalCategory saveOrUpdateProfessionalCategory(ProfessionalCategoryDTO professionalCategory) throws SQLException {

        ProfessionalCategory professionalCategories = this.professionalCategoryDAO.getByUuid(professionalCategory.getUuid());

       if(professionalCategories != null){

           ProfessionalCategory professionalCategoryEntity = new ProfessionalCategory(professionalCategory);
           this.professionalCategoryDAO.insert(professionalCategoryEntity);
           return professionalCategoryEntity;
       }
        return professionalCategories;
    }

    @Override
    public ProfessionalCategory save(ProfessionalCategory record) throws SQLException {

        this.professionalCategoryDAO.insert(record);
        return record;
    }

    @Override
    public ProfessionalCategory update(ProfessionalCategory record) throws SQLException {
        this.professionalCategoryDAO.update(record);
        return record;
    }

    @Override
    public int delete(ProfessionalCategory record) throws SQLException {
        return this.professionalCategoryDAO.delete(record);
    }

    @Override
    public List<ProfessionalCategory> getAll() throws SQLException {
        return this.professionalCategoryDAO.queryForAll();
    }

    @Override
    public ProfessionalCategory getById(int id) throws SQLException {
        return this.professionalCategoryDAO.queryForId(id);
    }
}
