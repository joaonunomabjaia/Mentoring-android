package mz.org.csaude.mentoring.dao.user;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import mz.org.csaude.mentoring.model.user.User;

@Dao
public interface UserDao {

    @Insert
    long insert(User user);

    @Update
    void update(User user);

    @Query("SELECT * FROM user WHERE user_name = :userName AND password = :password LIMIT 1")
    User getByCredentials(String userName, String password);

    @Query("SELECT * FROM user WHERE user_name = :userName LIMIT 1")
    User getByUserName(String userName);

    @Query("SELECT * FROM user WHERE uuid = :uuid LIMIT 1")
    User getByUuid(String uuid);

    @Query("SELECT * FROM user")
    List<User> queryForAll();

    @Query("SELECT * FROM user WHERE id = :id")
    User queryForId(int id);

    @Query("SELECT * FROM user WHERE uuid = :uuid")
    User queryForUuid(String uuid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long createOrUpdate(User user);

    @Query("SELECT * FROM user WHERE id = :id")
    User queryForEq(int id);


}
