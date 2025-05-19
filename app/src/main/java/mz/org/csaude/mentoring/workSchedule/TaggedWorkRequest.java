package mz.org.csaude.mentoring.workSchedule;

import androidx.work.OneTimeWorkRequest;

public class TaggedWorkRequest {
    private final OneTimeWorkRequest request;
    private final String tag;

    public TaggedWorkRequest(OneTimeWorkRequest request, String tag) {
        this.request = request;
        this.tag = tag;
    }

    public OneTimeWorkRequest getRequest() {
        return request;
    }

    public String getTag() {
        return tag;
    }
}
