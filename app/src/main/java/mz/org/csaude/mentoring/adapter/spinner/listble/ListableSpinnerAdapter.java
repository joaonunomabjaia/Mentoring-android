package mz.org.csaude.mentoring.adapter.spinner.listble;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.recyclerview.listable.Listble;
import mz.org.csaude.mentoring.util.Utilities;

public class ListableSpinnerAdapter extends ArrayAdapter<Listble> {

    private final Context context;
    private final LayoutInflater inflater;
    private final List<Listble> originalDataList;
    private final List<Listble> suggestions;

    public ListableSpinnerAdapter(@NonNull Activity activity, int textViewResourceId, List dataList) {
        super(activity, textViewResourceId, new ArrayList<>(dataList));
        this.context = activity.getApplicationContext();
        this.inflater = activity.getLayoutInflater();
        this.originalDataList = new ArrayList<>(dataList);
        this.suggestions = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return buildItemView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return buildItemView(position, convertView, parent);
    }

    private View buildItemView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.simple_auto_complete_item, parent, false);
        }

        Listble listble = getItem(position);
        if (listble == null) return convertView;

        TextView label = convertView.findViewById(R.id.label);
        ImageView icon = convertView.findViewById(R.id.item_icon);
        TextView info = convertView.findViewById(R.id.extra_info);

        label.setText(listble.getDescription());
        icon.setImageResource(listble.getDrawable());

        if (Utilities.stringHasValue(listble.getExtraInfo())) {
            info.setVisibility(View.VISIBLE);
            info.setText(listble.getExtraInfo());
        } else {
            info.setVisibility(View.GONE);
        }

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                suggestions.clear();

                if (constraint == null || constraint.length() == 0) {
                    // Show all items when nothing is typed
                    suggestions.addAll(originalDataList);
                } else {
                    String query = constraint.toString().toLowerCase();
                    for (Listble item : originalDataList) {
                        if (item.getDescription() != null &&
                                item.getDescription().toLowerCase().contains(query)) {
                            suggestions.add(item);
                        }
                    }
                }

                results.values = new ArrayList<>(suggestions);
                results.count = suggestions.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                clear();
                if (results != null && results.values != null) {
                    addAll((List<Listble>) results.values);
                }
                notifyDataSetChanged();
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                if (resultValue instanceof Listble) {
                    return ((Listble) resultValue).getDescription();
                }
                return super.convertResultToString(resultValue);
            }
        };
    }
}
