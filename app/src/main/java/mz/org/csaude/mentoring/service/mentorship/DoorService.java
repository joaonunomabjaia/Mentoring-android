package mz.org.csaude.mentoring.service.mentorship;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.base.service.BaseService;
import mz.org.csaude.mentoring.dto.mentorship.DoorDTO;
import mz.org.csaude.mentoring.model.mentorship.Door;

public interface DoorService extends BaseService<Door> {
    void saveOrUpdateDoors(List<DoorDTO> doorDTOS) throws SQLException;
    Door saveOrUpdateDoor(DoorDTO doorDTO) throws SQLException;

    Door getByCode(String communityDoor);
}
