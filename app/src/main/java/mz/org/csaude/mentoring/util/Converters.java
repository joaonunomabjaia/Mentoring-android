package mz.org.csaude.mentoring.util;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.Date;
import java.util.List;

import mz.org.csaude.mentoring.model.tutored.FlowHistory;

public class Converters {

    private static final Gson gson = new GsonBuilder().create();

    // ----- NEW: List<FlowHistory> <-> String (JSON array) -----
    @TypeConverter
    public static String fromFlowHistoryList(List<FlowHistory> list) {
        if (list == null) return null;
        return gson.toJson(list, new TypeToken<List<FlowHistory>>(){}.getType());
    }

    @TypeConverter
    public static List<FlowHistory> toFlowHistoryList(String value) {
        if (value == null || value.isEmpty()) return null;
        return gson.fromJson(value, new TypeToken<List<FlowHistory>>(){}.getType());
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
