package mz.org.csaude.mentoring.util;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.List;

import mz.org.csaude.mentoring.model.tutored.FlowHistory;

public class Converters {

    private static final Gson gson = new GsonBuilder().create();

    // ----- NEW: List<FlowHistory> <-> String (JSON array) -----
    @TypeConverter
    public static String fromFlowHistoryList(List<FlowHistory> list) {
        return list == null ? null : gson.toJson(list, FLOW_HISTORY_LIST_TYPE);
    }

    @TypeConverter
    public static List<FlowHistory> toFlowHistoryList(String value) {
        return (value == null || value.isEmpty())
                ? null
                : gson.fromJson(value, FLOW_HISTORY_LIST_TYPE);
    }

    @TypeConverter
    public static String fromFlowHistory(FlowHistory value) {
        return value == null ? null : gson.toJson(value);
    }

    @TypeConverter
    public static FlowHistory toFlowHistory(String value) {
        return (value == null || value.isEmpty()) ? null : gson.fromJson(value, FlowHistory.class);
    }

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
