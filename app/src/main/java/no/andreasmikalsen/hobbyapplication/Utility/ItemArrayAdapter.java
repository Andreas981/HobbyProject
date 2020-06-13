package no.andreasmikalsen.hobbyapplication.Utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import no.andreasmikalsen.hobbyapplication.Model.Workshop;
import no.andreasmikalsen.hobbyapplication.R;

public class ItemArrayAdapter extends ArrayAdapter<Workshop> {
    private ArrayList<Workshop> scoreList = new ArrayList();

    static class ItemViewHolder {
        TextView bedriftsNavn, adresse, postnummer, poststed;
    }

    public ItemArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public void add(Workshop object) {
        scoreList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return this.scoreList.size();
    }

    @Override
    public Workshop getItem(int index) {
        return this.scoreList.get(index);
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
}