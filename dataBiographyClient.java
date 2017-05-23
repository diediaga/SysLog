package org.lalalab.databiographyclient.databiographyclient;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import emulatorview.EmulatorView;
import emulatorview.TermSession;
import pub.devrel.easypermissions.EasyPermissions;


public class dataBiographyClient extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    public static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final int REQUIRED_PERMISSIONS_REQUEST_CODE = 10;
    public static final String[] SCRUB_PERMISSIONS = {Manifest.permission.READ_PHONE_STATE};
    public static final int SCRUB_PERMISSIONS_REQUEST_CODE = 11;

    private TextView textoLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_biography_client);

        textoLog = (TextView) findViewById(R.id.textLog);

        checkRequiredPermissions();

        Button startButton = (Button) findViewById(R.id.btn_iniciar);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                iniciaLogView();

            }
        });

        Button stopButton = (Button) findViewById(R.id.btn_finalizar);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                paraLogView();
            }
        });


        LogService.setUpdateListener(this);

    }

    /**
     * Check that we were granted runtime permissions
     */
    public boolean checkRequiredPermissions(){
        // Check for permissions
        boolean hasPermissions = EasyPermissions.hasPermissions(this, REQUIRED_PERMISSIONS);
        if(!hasPermissions){
            EasyPermissions.requestPermissions(this, getString(R.string.required_permission_detail), REQUIRED_PERMISSIONS_REQUEST_CODE, REQUIRED_PERMISSIONS);
        }
        return hasPermissions;
    }

    /**
     * Check that we were granted the permissions needed to scrub the logs
     */
    public boolean checkScrubPermissions(){
        // Check for permissions
        boolean hasPermissions = EasyPermissions.hasPermissions(this, SCRUB_PERMISSIONS);
        if(!hasPermissions){
            EasyPermissions.requestPermissions(this, getString(R.string.scrub_permission_detail), SCRUB_PERMISSIONS_REQUEST_CODE, SCRUB_PERMISSIONS);
        }
        return hasPermissions;
    }

    @Override
    public void onRequestPermissionsResult(int resultCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(resultCode, permissions, grantResults);
        // Pass everything to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(resultCode, permissions, grantResults, this);
    }



    @Override
    protected void onDestroy() {
        // Antes de cerrar la aplicacion se para el servicio (el cronometro)
        super.onDestroy();
    }

    /**
     * Inicia el Log View
     */

    private void iniciaLogView() {

        Intent service = new Intent(this, LogService.class);
        startService(service);
    }

    /**
     * Finaliza el servicio
     */
    private void paraLogView() {
        Intent service = new Intent(this, LogService.class);
        stopService(service);
    }

    /**
     * Actualiza en la interfaz de usuario con el log
     */
    public void actualizarLog(String log) {
        textoLog.setText(log);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

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
            mEmulatorView = new EmulatorView(getActivity().getBaseContext(), mTermSession, metrics);

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
