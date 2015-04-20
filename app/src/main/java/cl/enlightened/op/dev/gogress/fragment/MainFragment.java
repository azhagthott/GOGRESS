package cl.enlightened.op.dev.gogress.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import cl.enlightened.op.dev.gogress.R;
import cl.enlightened.op.dev.gogress.activity.LoginActivity;
import cl.enlightened.op.dev.gogress.user.ImageProfile;
import cl.enlightened.op.dev.gogress.util.CircularImageView;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    public MainFragment() {
    }

    public final static String DEBUG_MESSAGE="::MainFragment::";
    private final static int PROFILE_PIC_SIZE = 300;

    private GoogleApiClient mGoogleApiClient;

    private TextView textViewUserName;
    private TextView textViewEmail;
    private TextView textViewData;

    private Button buttonCloseApp;

    public String agentName;
    public String agentEmail;
    public CircularImageView circularImageView;


    private boolean mSignInButtonClicked = false;
    private int mLoginAction;
    public final static boolean DEBUGMODE=true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        textViewUserName =(TextView) rootView.findViewById(R.id.textViewUserName);
        textViewEmail = (TextView) rootView.findViewById(R.id.textViewEmail);
        circularImageView =(CircularImageView) rootView.findViewById(R.id.circularImageView);

        buttonCloseApp = (Button) rootView.findViewById(R.id.buttonCloseApp);
        buttonCloseApp.setText(R.string.button_text_revoke_access);
        buttonCloseApp.setOnClickListener(this);

        textViewData = (TextView) rootView.findViewById(R.id.textViewData);






    return rootView;
    }

    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        getProfileInformation();
    }

    @Override
    public void onStart(){
        super.onStart();
        //mGoogleApiClient.connect();
        //getProfileInformation();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mLoginAction== LoginActivity.Action.LOGOUT){
            logOut();
        }else{
            mSignInButtonClicked=false;
            logIn();
            getProfileInformation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        getProfileInformation();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    private void logIn() {
        if (mGoogleApiClient.isConnected()) {
            Person user = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
            if (user == null) {
                Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(),R.string.welcome_user,Toast.LENGTH_LONG).show();
            }
        }
    }
    private void logOut() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            Toast.makeText(getActivity(), getString(R.string.logged_out), Toast.LENGTH_LONG)
                    .show();
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        }
    }

    public void revokeGplusAccess() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status arg0) {
                            Log.e(DEBUG_MESSAGE, "User access revoked!");
                            mGoogleApiClient.connect();
                        }
                    });
            Toast.makeText(getActivity(), getString(R.string.logged_out), Toast.LENGTH_SHORT).show();
        }
    }

    public void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onClick(View v) {

        Log.v(DEBUG_MESSAGE,"CLICK!!!!");

        revokeGplusAccess();
        signOutFromGplus();

        getActivity().finish();
    }

    public void getProfileInformation() {
        try {

            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);

                agentName = currentPerson.getDisplayName();
                agentEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
                String personPhotoUrl = currentPerson.getImage().getUrl();

                Log.v(DEBUG_MESSAGE, "Name: " + agentName + ", plusProfile: "
                        + ", email: " + agentEmail
                        + ", Image: " + personPhotoUrl);

                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;

                textViewUserName.setText("Agente: " + agentName);
                textViewEmail.setText("Email: " + agentEmail);

                circularImageView.setBorderWidth(6);
                circularImageView.setBorderColor(Color.LTGRAY);
                new ImageProfile(circularImageView).execute(personPhotoUrl);

            } else {
                Toast.makeText(getActivity(),
                        getString(R.string.getting_profile_information_error),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
