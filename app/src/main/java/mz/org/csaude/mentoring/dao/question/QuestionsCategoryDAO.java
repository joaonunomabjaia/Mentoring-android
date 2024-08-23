package mz.org.csaude.mentoring.dao.question;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

import mz.org.csaude.mentoring.model.question.QuestionsCategory;

@Dao
public interface QuestionsCategoryDAO {

    @Insert
    void insert(QuestionsCategory questionsCategory);

    @Update
    void update(QuestionsCategory questionsCategory);

    @Delete
    void delete(QuestionsCategory questionsCategory);

    @Query("SELECT * FROM question_category WHERE id = :id")
    QuestionsCategory getById(int id);

    @Query("SELECT * FROM question_category WHERE uuid = :uuid LIMIT 1")
    QuestionsCategory getByUuid(String uuid);

    @Query("SELECT * FROM question_category")
    List<QuestionsCategory> getAll();
}
