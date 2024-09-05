package mz.org.csaude.mentoring.service.mentorship;

import android.app.Application;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseServiceImpl;
import mz.org.csaude.mentoring.dao.mentorship.DoorDAO;
import mz.org.csaude.mentoring.dto.mentorship.DoorDTO;
import mz.org.csaude.mentoring.model.mentorship.Door;
import mz.org.csaude.mentoring.model.user.User;

public class DoorServiceImpl extends BaseServiceImpl<Door> implements DoorService {

    DoorDAO doorDAO;

    public DoorServiceImpl(Application application) {
        super(application);
    }

    @Override
    public void init(Application application) throws SQLException {
        super.init(application);
        this.doorDAO = getDataBaseHelper().getDoorDAO();
    }

    @Override
    public Door save(Door record) throws SQLException {
        record.setId((int) this.doorDAO.insertDoor(record));
        return record;
    }

    @Override
    public Door update(Door record) throws SQLException {
        this.doorDAO.updateDoor(record);
        return record;
    }

    @Override
    public int delete(Door record) throws SQLException {
        return this.doorDAO.delete(record.getId());
    }

    @Override
    public List<Door> getAll() throws SQLException {
        return this.doorDAO.queryForAll();
    }

    @Override
    public Door getById(int id) throws SQLException {
        return this.doorDAO.queryForId(id);
    }

    @Override
    public Door getByuuid(String uuid) throws SQLException {
        return this.doorDAO.getByUuid(uuid);
    }

    @Override
    public void saveOrUpdateDoors(List<DoorDTO> doorDTOS) throws SQLException {
        for (DoorDTO doorDTO: doorDTOS) {
            this.saveOrUpdateDoor(doorDTO);
        }
    }

    @Override
    public Door saveOrUpdateDoor(DoorDTO doorDTO) throws SQLException {
        Door d = this.doorDAO.getByUuid(doorDTO.getUuid());
        Door door = doorDTO.getDoor();
        if(d!=null) {
           door.setId(d.getId());
           this.update(door);
        } else {
            this.save(door);
        }
        return door;
    }
}
