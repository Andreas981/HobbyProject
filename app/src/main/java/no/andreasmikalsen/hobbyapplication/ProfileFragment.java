package no.andreasmikalsen.hobbyapplication;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.concurrent.Executor;

import no.andreasmikalsen.hobbyapplication.Utility.EncryptedSharedPref;
import no.andreasmikalsen.hobbyapplication.Utility.InputValidator;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private NavController navController;

    private Executor exec;
    private BiometricPrompt bioPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        exec = ContextCompat.getMainExecutor(getActivity().getApplicationContext());
        bioPrompt = new BiometricPrompt(ProfileFragment.this, exec, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                getActivity().finish();
                Toast.makeText(getActivity().getApplicationContext(),
                        "ERROR: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getActivity().getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                EncryptedSharedPref.writeBool(EncryptedSharedPref.IS_AUTH, true);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getActivity().getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });




        Button b = view.findViewById(R.id.deleteAuth);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EncryptedSharedPref.delete(EncryptedSharedPref.AUTH_METHOD);
                EncryptedSharedPref.delete(EncryptedSharedPref.PASSWORD);
                navController.navigate(R.id.profileFragment);
            }
        });




    }

    @Override
    public void onStart() {
        super.onStart();
        boolean isAuth = EncryptedSharedPref.readBool(EncryptedSharedPref.IS_AUTH, false);
        String authMethod = EncryptedSharedPref.readString(EncryptedSharedPref.AUTH_METHOD, null);
        if(!isAuth){
            //Not authorized
            if(authMethod == null){
                //Auth not set
                //Need to set auth
                setupAuthentication();
            }else{
                //Need to sign in
                authenticate();
            }
        }
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

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog1.cancel();
                navController.navigate(R.id.action_profileFragment_to_mapFragment);
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
                        navController.navigate(R.id.profileFragment);
                    }
                    if (selectedButton == R.id.dialog_setauth_radio_pin){
                        Pair<Boolean, String> inputCheck = InputValidator.isPinGood(pin.getText().toString(), repeatPin.getText().toString());
                        if (inputCheck.first) {
                            EncryptedSharedPref.writeString(EncryptedSharedPref.AUTH_METHOD, "pin");
                            EncryptedSharedPref.writeInt(EncryptedSharedPref.PASSWORD, Integer.parseInt(pin.getText().toString()));
                            alertDialog1.cancel();
                            navController.navigate(R.id.profileFragment);
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
                            navController.navigate(R.id.profileFragment);
                        }
                        else {
                            Toast.makeText(getContext().getApplicationContext(), inputCheck.second, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    alertDialog1.cancel();
                    navController.navigate(R.id.action_profileFragment_to_mapFragment);
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

    private void authenticate(){
        String loginMethod = EncryptedSharedPref.readString(EncryptedSharedPref.AUTH_METHOD, "nothing");
        //Check onStart which login method is used, and act accordingly
        switch (loginMethod){
            case "password":
                promptAuth("password");
                break;
            case "pin":
                //Alert dialog
                promptAuth("pin");
                break;
            case "biometric":
                //SetDeviceCredientialAllowed(false) means that only biometric will work, no alternative once biometric is activated.
                promptInfo = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Biometric login for user")
                        .setSubtitle("Log in using your biometric credential")
                        .setDeviceCredentialAllowed(false)
                        .setNegativeButtonText("Exit application")
                        .build();
                bioPrompt.authenticate(promptInfo);
                break;
        }
    }
    private void promptAuth(String method){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        View view = View.inflate(getActivity().getApplicationContext(), R.layout.dialog_authenticate, null);
        alertDialog.setView(view);

        alertDialog.setCancelable(false);
        final EditText inputPin = view.findViewById(R.id.dialog_authenticate_pin);
        final EditText inputPassword = view.findViewById(R.id.dialog_authenticate_password);
        Button button = view.findViewById(R.id.dialog_authenticate_signin);
        final AlertDialog alertDialog1 = alertDialog.show();

        if(method.equals("pin")){
            final int pinCode = EncryptedSharedPref.readInt(EncryptedSharedPref.PASSWORD, 0);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if(inputPin.getText().length() >= 4) {
                            int inputCode = Integer.parseInt(inputPin.getText().toString());

                            if (inputCode == pinCode) {
                                Toast.makeText(getContext().getApplicationContext(), "Correct pin", Toast.LENGTH_SHORT).show();
                                alertDialog1.dismiss();
                            } else {
                                Toast.makeText(getContext().getApplicationContext(), "Wrong pin", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext().getApplicationContext(), "Wrong pin", Toast.LENGTH_SHORT).show();
                        }
                    }catch (NumberFormatException e){
                        Toast.makeText(getContext().getApplicationContext(), "Something went wrong, try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            final String password = EncryptedSharedPref.readString(EncryptedSharedPref.PASSWORD, null);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if(inputPassword.getText().length() >= 10) {
                            String inputCode = inputPassword.getText().toString();

                            if (inputCode == password) {
                                Toast.makeText(getContext().getApplicationContext(), "Correct password", Toast.LENGTH_SHORT).show();
                                alertDialog1.dismiss();
                                EncryptedSharedPref.writeBool(EncryptedSharedPref.IS_AUTH, true);
                            } else {
                                Toast.makeText(getContext().getApplicationContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext().getApplicationContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                        }
                    }catch (NumberFormatException e){
                        Toast.makeText(getContext().getApplicationContext(), "Something went wrong, try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }




    }
}
