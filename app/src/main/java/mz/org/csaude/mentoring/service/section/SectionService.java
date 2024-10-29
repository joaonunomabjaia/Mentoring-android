package mz.org.csaude.mentoring.service.section;

import java.util.List;

import mz.org.csaude.mentoring.base.service.BaseService;
import mz.org.csaude.mentoring.model.form.Section;

public interface SectionService extends BaseService<Section> {
    void saveOrUpdateSections(List<Section> sections);
}
