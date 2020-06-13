package no.andreasmikalsen.hobbyapplication;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import no.andreasmikalsen.hobbyapplication.Model.Workshop;
import no.andreasmikalsen.hobbyapplication.Utility.ItemArrayAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {

    private ListView listView;
    private ItemArrayAdapter itemArrayAdapter;
    private ArrayList<Workshop> workshops = new ArrayList<>();

    public ListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        listView = view.findViewById(R.id.listView);
        itemArrayAdapter = new ItemArrayAdapter(getActivity().getApplicationContext(), R.layout.item_layout);

        InputStream inputStream = getResources().openRawResource(R.raw.verksted);
        getWorkshopFromFile(inputStream);
        populateListView();

    }

    private void getWorkshopFromFile(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            int test = 0;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(";");
                if(test != 0){
                    workshops.add(new Workshop(row[0], row[1], row[2], row[3]));
                }else{
                    test = 1;
                }
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }
    }


    private void populateListView() {
        Parcelable state = listView.onSaveInstanceState();
        listView.setAdapter(itemArrayAdapter);
        listView.onRestoreInstanceState(state);

        for(Workshop scoreData:workshops ) {
            itemArrayAdapter.add(scoreData);
        }
    }


}
