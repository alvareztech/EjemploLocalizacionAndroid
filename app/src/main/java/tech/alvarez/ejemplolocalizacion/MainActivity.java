package tech.alvarez.ejemplolocalizacion;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient googleApiClient;

    private TextView longitudTextView;
    private TextView latitudTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        longitudTextView = (TextView) findViewById(R.id.longitudTextView);
        latitudTextView = (TextView) findViewById(R.id.latitudTextView);


        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.i(Constantes.TAG, "onConnected");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.i(Constantes.TAG, " Tenemos permiso");

//            obtenerUltimaUbicacion();
            obtenerActualizacionUbicaciones();


        } else {
            Log.i(Constantes.TAG, " No tenemos permiso");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.i(Constantes.TAG, " Ya preguntamos, no podemos preguntar de nuevo");

            } else {
                Log.i(Constantes.TAG, " Nunca solicitamos permiso, lo pedimos ahora");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constantes.CODIGO_PERMISO_LOCALIZACION);
            }
        }


    }

    private void obtenerActualizacionUbicaciones() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationRequest locationRequest = new LocationRequest()
                .setInterval(1000) // 1 segundo
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.i(Constantes.TAG, "onRequestPermissionsResult");

        if (requestCode == Constantes.CODIGO_PERMISO_LOCALIZACION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(Constantes.TAG, " El usuario autorizó el permiso");

//                obtenerUltimaUbicacion();
                obtenerActualizacionUbicaciones();

            } else {
                Log.i(Constantes.TAG, " El ususario denego el permiso");
            }
        }
    }

    private void obtenerUltimaUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location ubicacion = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        double latitud = ubicacion.getLatitude();
        double longitud = ubicacion.getLongitude();

        latitudTextView.setText(latitud + "");
        longitudTextView.setText(longitud + "");

        Log.i(Constantes.TAG, latitud + "," + longitud);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(Constantes.TAG, "onLocationChanged");

        double latitud = location.getLatitude();
        double longitud = location.getLongitude();

        latitudTextView.setText(latitud + "");
        longitudTextView.setText(longitud + "");
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }
}
