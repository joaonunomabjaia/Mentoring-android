package mz.org.csaude.mentoring.service.user;

import mz.org.csaude.mentoring.listner.rest.RestResponseListener;
import mz.org.csaude.mentoring.model.user.User;

public interface UserSyncService {

    void doOnlineLogin (RestResponseListener listener);

    void getUserByCedencials(User user);
}
