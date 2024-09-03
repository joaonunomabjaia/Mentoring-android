package mz.org.csaude.mentoring.dao.question;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Upsert;

import java.util.List;

import mz.org.csaude.mentoring.model.question.QuestionsCategory;

@Dao
public interface QuestionsCategoryDAO {

    @Insert
    long insert(QuestionsCategory questionsCategory);

    @Update
    void update(QuestionsCategory questionsCategory);

    @Delete
    int delete(QuestionsCategory questionsCategory);

    @Query("SELECT * FROM question_category WHERE id = :id")
    QuestionsCategory getById(int id);

    @Query("SELECT * FROM question_category WHERE uuid = :uuid LIMIT 1")
    QuestionsCategory getByUuid(String uuid);

    @Query("SELECT * FROM question_category")
    List<QuestionsCategory> getAll();

    @Query("SELECT * FROM question_category WHERE id = :id LIMIT 1")
    QuestionsCategory queryForId(int id);

    @Query("SELECT * FROM question_category")
    List<QuestionsCategory> queryForAll();

    @Query("SELECT * FROM question_category WHERE uuid = :uuid LIMIT 1")
    QuestionsCategory queryForUuid(String uuid);


}
