package no.andreasmikalsen.hobbyapplication;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import no.andreasmikalsen.hobbyapplication.Model.Workshop;
import no.andreasmikalsen.hobbyapplication.Utility.ItemArrayAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {

    private ListView listView;
    private ItemArrayAdapter itemArrayAdapter;
    private ArrayList<Workshop> workshops = new ArrayList<>();
    private ArrayList<Workshop> mWorkshops = new ArrayList<>();
    private EditText filterPostSted;
    private Button filterBtn;
    private Button clearFilterBtn;

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
        filterPostSted = view.findViewById(R.id.list_filter_poststed_edittext);
        filterBtn = view.findViewById(R.id.list_filter_btn);
        clearFilterBtn = view.findViewById(R.id.list_clear_filter_btn);

        InputStream inputStream = getResources().openRawResource(R.raw.verksted);
        getWorkshopFromFile(inputStream);
        populateListView();


        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filterPostStedText = filterPostSted.getText().toString();
                if(filterPostStedText.trim().length() <= 0){
                    Snackbar.make(v, "Filter tekst er tom", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else{
                    ArrayList<Workshop> tempList= filterWorkshops(filterPostStedText);
                    if(tempList.size() <= 0){
                        Snackbar.make(v, "Fant ingen verksteder med dette filteret", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }else{
                        mWorkshops = tempList;
                        populateListView();
                        filterPostSted.setText("");
                        //Hide keyboard
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(filterPostSted.getWindowToken(), 0);
                    }
                }
            }
        });

        clearFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWorkshops = workshops;
                populateListView();
            }
        });

    }

    private ArrayList<Workshop> filterWorkshops(String value){
        ArrayList<Workshop> secondList = new ArrayList<>();

        for( int i = 0 ; i < workshops.size(); i++) {
            if (workshops.get(i).getPoststed().toLowerCase().equals(value)) {
                secondList.add(workshops.get(i));
            }
        }


        return secondList;
    }

    private void getWorkshopFromFile(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            int test = 0;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(";");
                if(test != 0){
                    workshops.add(new Workshop(row[0].trim(), row[1].trim(), row[2].trim(), row[3].trim()));
                }else{
                    test = 1;
                }
            }
            mWorkshops = workshops;
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
        itemArrayAdapter.clear();

        Parcelable state = listView.onSaveInstanceState();
        listView.setAdapter(itemArrayAdapter);
        listView.onRestoreInstanceState(state);

        for(Workshop workshop:mWorkshops ) {
            itemArrayAdapter.add(workshop);
        }
    }




}
