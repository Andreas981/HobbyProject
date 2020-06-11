package no.andreasmikalsen.hobbyapplication;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import no.andreasmikalsen.hobbyapplication.Utility.EncryptedSharedPref;
import no.andreasmikalsen.hobbyapplication.Utility.InputValidator;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {


    private ActionBar actionBar;


    private Preference prefAuthentication;
    private Preference testPref;
    
    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        
        actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Settings");


        prefAuthentication = findPreference("authentication");
        prefAuthentication.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                setupAuthentication();
                return false;
            }
        });

        testPref = findPreference("feedback");
        testPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getContext().getApplicationContext(), "Feedbackl pressed", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        
    }












    private void setupAuthentication(){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity().getApplicationContext(), R.layout.dialog_setup_authentication, null);
        alertDialog.setView(view);

        alertDialog.setCancelable(false);
        Button cancelButton = view.findViewById(R.id.fragment_settings_dialog_cancel);
        Button saveButton = view.findViewById(R.id.fragment_settings_dialog_save);

        final EditText pin = view.findViewById(R.id.dialog_setauth_pin);
        final EditText repeatPin = view.findViewById(R.id.dialog_setauth_pin_repeat);
        final EditText password = view.findViewById(R.id.dialog_setauth_password);
        final EditText repeatPassword = view.findViewById(R.id.dialog_setauth_password_repeat);

        final RadioGroup radioGroup = view.findViewById(R.id.dialog_setauth_radioGroup);

        switchVisibility(false,pin,repeatPin);
        switchVisibility(false,password,repeatPassword);

        final AlertDialog alertDialog1 = alertDialog.show();

        String currentMethod = EncryptedSharedPref.readString(EncryptedSharedPref.AUTH_METHOD, null);
        if(currentMethod != null) {
            if (currentMethod.equals("biometric")) {
                radioGroup.check(R.id.dialog_setauth_radio_biometric);
            } else if (currentMethod.equals("pin")) {
                radioGroup.check(R.id.dialog_setauth_radio_pin);
            } else if (currentMethod.equals("password")) {
                radioGroup.check(R.id.dialog_setauth_radio_password);
            }
        }
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog1.cancel();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.dialog_setauth_radio_biometric:
                        switchVisibility(false, pin, repeatPin);
                        switchVisibility(false, password, repeatPassword);
                        break;
                    case R.id.dialog_setauth_radio_pin:
                        switchVisibility(true, pin, repeatPin);
                        switchVisibility(false, password, repeatPassword);
                        break;
                    case R.id.dialog_setauth_radio_password:
                        switchVisibility(false, pin, repeatPin);
                        switchVisibility(true, password, repeatPassword);
                        break;

                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(radioGroup.getCheckedRadioButtonId() != -1){
                    int selectedButton = radioGroup.getCheckedRadioButtonId();

                    if (selectedButton == R.id.dialog_setauth_radio_biometric){
                        EncryptedSharedPref.writeString(EncryptedSharedPref.AUTH_METHOD, "biometric");
                        alertDialog1.cancel();
                    }
                    if (selectedButton == R.id.dialog_setauth_radio_pin){
                        Pair<Boolean, String> inputCheck = InputValidator.isPinGood(pin.getText().toString(), repeatPin.getText().toString());
                        if (inputCheck.first) {
                            EncryptedSharedPref.writeString(EncryptedSharedPref.AUTH_METHOD, "pin");
                            EncryptedSharedPref.writeInt(EncryptedSharedPref.PASSWORD, Integer.parseInt(pin.getText().toString()));
                            alertDialog1.cancel();
                        }
                        else {
                            Toast.makeText(getContext().getApplicationContext(), inputCheck.second, Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (selectedButton == R.id.dialog_setauth_radio_password) {
                        Pair<Boolean, String> inputCheck = InputValidator.isPasswordGood(password.getText().toString(), repeatPassword.getText().toString());
                        if (inputCheck.first) {
                            EncryptedSharedPref.writeString(EncryptedSharedPref.AUTH_METHOD, "password");
                            EncryptedSharedPref.writeString(EncryptedSharedPref.PASSWORD, pin.getText().toString());
                            alertDialog1.cancel();
                        }
                        else {
                            Toast.makeText(getContext().getApplicationContext(), inputCheck.second, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    alertDialog1.cancel();
                }

            }
        });
    }
    private void switchVisibility(boolean visible, EditText text1, EditText text2){
        if (visible){
            text1.setVisibility(View.VISIBLE);
            text2.setVisibility(View.VISIBLE);
        }
        else {
            //Turn invisble
            text1.setVisibility(View.GONE);
            text2.setVisibility(View.GONE);

            //Clear text
            text1.getText().clear();
            text2.getText().clear();

            //Hide keyboard
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(text1.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(text2.getWindowToken(), 0);
        }
    }

    
}
