package mz.org.csaude.mentoring.dao.formQuestion;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Transaction;

import java.util.List;

import mz.org.csaude.mentoring.model.formQuestion.FormQuestion;

@Dao
public interface FormQuestionDAO {

    @Insert
    void insert(FormQuestion formQuestion);

    @Update
    void update(FormQuestion formQuestion);

    @Delete
    int delete(FormQuestion formQuestion);

    @Query("SELECT * FROM form_question WHERE form_id = :formId AND life_cycle_status = :lifeCycleStatus " +
            "AND evaluation_type_id IN (SELECT id FROM evaluation_type WHERE code = :evaluationType OR code = 'Ambos') " +
            "ORDER BY sequence ASC")
    List<FormQuestion> getAllOfForm(int formId, String evaluationType, String lifeCycleStatus);

    @Query("SELECT * FROM form_question WHERE uuid = :uuid LIMIT 1")
    FormQuestion getByUuid(String uuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createOrUpdate(FormQuestion fQuestion);

    @Query("SELECT * FROM form_question WHERE id = :id")
    FormQuestion queryForId(int id);

    @Query("SELECT * FROM form_question")
    List<FormQuestion> queryForAll();


}
