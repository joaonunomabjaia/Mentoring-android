package mz.org.csaude.mentoring.dao.employee;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import mz.org.csaude.mentoring.model.employee.Employee;

@Dao
public interface EmployeeDAO {

    @Query("SELECT * FROM employee WHERE id = :id")
    Employee findById(Integer id);

    @Query("SELECT * FROM employee")
    List<Employee> getAllEmployees();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Employee employee);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Employee> employees);

    @Update
    void update(Employee employee);

    @Update
    void updateAll(List<Employee> employees);

    @Query("DELETE FROM employee WHERE id = :id")
    void deleteById(Integer id);

    @Query("SELECT * FROM employee WHERE uuid = :uuid LIMIT 1")
    Employee getByUuid(String uuid);
}
