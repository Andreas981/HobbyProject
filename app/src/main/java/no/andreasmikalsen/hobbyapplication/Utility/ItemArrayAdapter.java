package no.andreasmikalsen.hobbyapplication.Utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import no.andreasmikalsen.hobbyapplication.Model.Workshop;
import no.andreasmikalsen.hobbyapplication.R;

public class ItemArrayAdapter extends ArrayAdapter<Workshop> {
    private ArrayList<Workshop> workshops = new ArrayList();
    private ArrayList<Workshop> mOriginalValues = new ArrayList();

    static class ItemViewHolder {
        TextView bedriftsNavn, adresse, postnummer, poststed;
    }

    public ItemArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public void add(Workshop object) {
        workshops.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return this.workshops.size();
    }

    @Override
    public Workshop getItem(int index) {
        return this.workshops.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void clear() {
        super.clear();
        workshops.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ItemViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.item_layout, parent, false);
            viewHolder = new ItemViewHolder();
            viewHolder.bedriftsNavn = (TextView) row.findViewById(R.id.navn);
            viewHolder.adresse = (TextView) row.findViewById(R.id.adresse);
            viewHolder.postnummer = (TextView) row.findViewById(R.id.postnummer);
            viewHolder.poststed = (TextView) row.findViewById(R.id.poststed);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ItemViewHolder)row.getTag();
        }
        Workshop stat = getItem(position);
        viewHolder.bedriftsNavn.setText(stat.getBedriftsNavn());
        viewHolder.adresse.setText(stat.getAdresse());
        viewHolder.poststed.setText(stat.getPoststed());
        viewHolder.postnummer.setText(stat.getPostnummer());
        return row;
    }

    /*@Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                workshops = (ArrayList<Workshop>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                List<Workshop> FilteredArrList = new ArrayList<Workshop>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<Workshop>(workshops); // saves the original data in mOriginalValues
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                /*if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        String data = mOriginalValues.get(i);
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrList.add(data);
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }*/



}