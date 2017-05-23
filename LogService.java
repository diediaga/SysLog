package org.lalalab.databiographyclient.databiographyclient;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import emulatorview.EmulatorView;

//import emulatorview.TermSession;

/**
 * Created by diegodiaz on 22/5/17.
 */

public class LogService extends Service {

    public static dataBiographyClient UPDATE_LISTENER;

    private static final String TAG = "MainActivity";
    public static final String MESSAGES_CHILD = "messages";
    private static final int REQUEST_INVITE = 1;
    private static final int REQUEST_IMAGE = 2;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;
    private static final String MESSAGE_URL = "http://friendlychat.firebase.google.com/message/";

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    //private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder> mFirebaseAdapter;

    /**
     * Establece quien va ha recibir las actualizaciones del cronometro
     *
     * @param poiService
     */
    public static void setUpdateListener(dataBiographyClient poiService) {
        UPDATE_LISTENER = poiService;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        // Set default username is anonymous.
        mUsername = ANONYMOUS;

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            //          finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        iniciaLog();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private void iniciaLog() {

        //Fragment frag = new LiveLogFragment();

        TermSession mTermSession = new TermSession();


        sendLog(mTermSession.notifyUpdate()); //enviamos el string a la base de datos

        Log.d("sendLog : ", mTermSession.toString()); //enviamos el string como Log.d
        UPDATE_LISTENER.actualizarLog(mTermSession.toString()); //enviamos el string a la interfaz grafica
    }

    private void restartLog(){
        Fragment frag = new LiveLogFragment();
        sendLog(frag.toString());
        Log.d("restartLog: ", frag.toString());
    }

    public void sendLog(String mMessage){
        // Send messages to firebase.
        FriendlyMessage friendlyMessage = new FriendlyMessage(String.valueOf(mMessage),
                mUsername,
                mPhotoUrl,
                null /* no image */);

        mFirebaseDatabaseReference.child(MESSAGES_CHILD)
                .push().setValue(friendlyMessage);
    }


    // TODO: 22/5/17 falta saber como obtener el numero de hijos que tiene la base de datos 
    public void deleteDBLog() {
        do {
            mFirebaseDatabaseReference.child(MESSAGES_CHILD).limitToFirst(1).getRef().setValue(null);
        } while
                (mFirebaseDatabaseReference.child(MESSAGES_CHILD).toString().length() > 100);
    }

    public static class LiveLogFragment extends Fragment {
        private EmulatorView mEmulatorView;
        private TermSession mTermSession = new TermSession();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);

        }

        /**
         * Stop the logcat process
         */
        public void stop(){
            mTermSession.stopLogcat();
            Toast.makeText(getActivity(), R.string.stopped_logcat, Toast.LENGTH_SHORT).show();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            //mEmulatorView = new EmulatorView(getActivity().getBaseContext(), mTermSession, metrics);

            return mEmulatorView;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (mTermSession.isRunning()){
                mTermSession.finish();
            }

        }

    }
}
