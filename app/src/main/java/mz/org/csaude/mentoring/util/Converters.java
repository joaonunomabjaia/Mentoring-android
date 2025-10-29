package mz.org.csaude.mentoring.util;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import mz.org.csaude.mentoring.model.tutored.FlowHistory;

public class Converters {

    private static final Gson gson = new GsonBuilder().create();

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
