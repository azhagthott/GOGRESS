package cl.enlightened.op.dev.gogress.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import cl.enlightened.op.dev.gogress.R;

public class LoginActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,OnClickListener{

    public final static boolean DEBUG_MODE=false;

    public final static String DEBUG_MESSAGE="LoginActivity::::";
    public static final int LOGIN = 2000;
    public static final int LOGOUT = 2001;

    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final int RC_SIGN_IN = 0;

    private static final String DIALOG_ERROR = "dialog_error";

    public GoogleApiClient mGoogleApiClient;

    private SignInButton btnSignIn;
    private ConnectionResult mConnectionResult;
    private boolean mResolvingError = false;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        Log.v(DEBUG_MESSAGE,"onCreate");

        mProgressDialog = initializeProgressDialog();

        btnSignIn = (SignInButton) findViewById(R.id.sign_in_button);
        btnSignIn.setOnClickListener(this);
    }

    public static class Action{
        public static final int LOGIN = 2000;
        public static final int LOGOUT = 2001;
        private Action() {}
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.v(DEBUG_MESSAGE,"onStart");
        //mGoogleApiClient.connect();
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.v(DEBUG_MESSAGE, "onStop");

        if (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        dismissProgressDialog();
    }

    protected void onResume() {
        super.onResume();
        Log.v(DEBUG_MESSAGE, "onResume");
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {

        Log.v(DEBUG_MESSAGE, "onActivityResult");

        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (responseCode == RESULT_OK) {

                if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                    showProgressDialog();
                }
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v(DEBUG_MESSAGE, "onConnected");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v(DEBUG_MESSAGE, "onConnectionSuspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        Log.v(DEBUG_MESSAGE, "onConnectionFailed");

        //TODO: enviar intento de conexion

        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    private void showErrorDialog(int errorCode) {

        Log.v(DEBUG_MESSAGE,"showErrorDialog");

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getApplicationContext());
        if (resultCode == ConnectionResult.SUCCESS) {

        } else if (resultCode == ConnectionResult.SERVICE_MISSING ||
                resultCode == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED ||
                resultCode == ConnectionResult.SERVICE_DISABLED) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 1);
            dialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        mGoogleApiClient.connect();
    }

    private ProgressDialog initializeProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setMessage(getString(R.string.progress_dialog));
        return dialog;
    }

    private void showProgressDialog() {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }



}
