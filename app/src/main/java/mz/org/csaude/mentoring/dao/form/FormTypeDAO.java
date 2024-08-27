package mz.org.csaude.mentoring.dao.form;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

import mz.org.csaude.mentoring.model.form.FormType;

@Dao
public interface FormTypeDAO {

    @Insert
    long insert(FormType formType);

    @Update
    void update(FormType formType);

    @Delete
    int delete(FormType formType);

    @Query("SELECT * FROM form_type WHERE id = :id")
    FormType getById(int id);

    @Query("SELECT * FROM form_type WHERE uuid = :uuid LIMIT 1")
    FormType getByUuid(String uuid);

    @Query("SELECT * FROM form_type")
    List<FormType> getAll();
}
