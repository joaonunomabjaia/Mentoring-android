package mz.org.csaude.mentoring.dao.user;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import mz.org.csaude.mentoring.model.user.User;

@Dao
public interface UserDao {

    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Query("SELECT * FROM user WHERE user_name = :userName AND password = :password LIMIT 1")
    User getByCredentials(String userName, String password);

    @Query("SELECT * FROM user WHERE user_name = :userName LIMIT 1")
    User getByUserName(String userName);

    @Query("SELECT * FROM user WHERE uuid = :uuid LIMIT 1")
    User getByUuid(String uuid);
}
