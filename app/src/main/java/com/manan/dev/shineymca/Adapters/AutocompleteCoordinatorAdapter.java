package com.manan.dev.shineymca.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.manan.dev.shineymca.Models.Coordinator;
import com.manan.dev.shineymca.R;

import java.util.ArrayList;
import java.util.Collections;

public class AutocompleteCoordinatorAdapter extends ArrayAdapter<Coordinator> {

    private Context context;
    //private final  ArrayList<String> coordinatorNames;
    private int resource;
    private ArrayList<Coordinator> items;//, suggestions;
    private ArrayList<Coordinator> itemsAll;
    private ArrayList<Coordinator> suggestions;

    public AutocompleteCoordinatorAdapter(@NonNull Context context, int resource, int textViewResourceId, ArrayList<Coordinator> coordiList) {
        super(context, resource, textViewResourceId, coordiList);
        this.context = context;
        this.resource = resource;
        this.items = coordiList;
        this.itemsAll = (ArrayList<Coordinator>) items.clone();
        this.suggestions = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            view = inflater.inflate(resource, parent, false);
        }
        Coordinator coordinator = items.get(position);
        if (coordinator != null) {
            TextView lblName = (TextView) view.findViewById(R.id.coordinator_item_name);
            lblName.setText(coordinator.getCoordName());
        }
        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return nameFilter;
    }


    private Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            return ((Coordinator)(resultValue)).getCoordName();
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null) {
                suggestions.clear();
                for (Coordinator coordinator : itemsAll) {
                    if(coordinator.getCoordName().toLowerCase().contains(constraint.toString().toLowerCase().trim())){
                        suggestions.add(coordinator);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                Log.d("Something", itemsAll.size()+"");
                Collections.copy(suggestions,itemsAll);
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            }
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<Coordinator> filteredList = (ArrayList<Coordinator>) results.values;
            if(results != null && results.count > 0) {
                clear();
                for (Coordinator c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };
}
