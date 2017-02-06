package ar.edu.utn.frsf.isi.dam.del2016.heymozo.maps;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import ar.edu.utn.frsf.isi.dam.del2016.heymozo.R;
import ar.edu.utn.frsf.isi.dam.del2016.heymozo.modelo.Restaurante;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, BusquedaRestaurantesListener<Restaurante>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private boolean flagPermisoPedido;
    private static final int PERMISSION_REQUEST_ACCESS = 899;
    private RelativeLayout loadingPanel;
    private ListarRestaurantesTask listarRestaurantesTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
    }

    @Override
    protected void onPause() {
        if (listarRestaurantesTask != null) {
            listarRestaurantesTask.cancel(true);
        }
        super.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("MAP", "Mapa cargado");

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        askForPermission();

        listarRestaurantesTask = new ListarRestaurantesTask(this, this);
        listarRestaurantesTask.execute();
    }

    private void askForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.pedido_permiso_titulo);
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage(R.string.pedido_permiso_mensaje);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            flagPermisoPedido = true;
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.ACCESS_FINE_LOCATION}
                                    , PERMISSION_REQUEST_ACCESS);
                        }
                    });
                    builder.show();
                } else {
                    flagPermisoPedido = true;
                    ActivityCompat.requestPermissions(this,
                            new String[]
                                    {android.Manifest.permission.ACCESS_FINE_LOCATION}
                            , PERMISSION_REQUEST_ACCESS);
                }

            }
        }
        if (!flagPermisoPedido) {
            setMyLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MapsActivity.PERMISSION_REQUEST_ACCESS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setMyLocation();
                } else {
                    finish();
                    Toast.makeText(this, R.string.no_permission_fine_location, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        mGoogleApiClient.connect();
    }

    @Override
    public void busquedaFinalizada(List<Restaurante> restaurantes, int resultCode) {
        switch (resultCode) {
            //Correcto
            case ListarRestaurantesTask.OK:
                for (Restaurante x : restaurantes) {
                    View marker = View.inflate(getBaseContext(), R.layout.custom_marker_layout, null);
                    ImageView imageView = (ImageView) marker.findViewById(R.id.mapsFotoRestaurante);
                    byte[] bytes = Base64.decode(x.getImagen64(), Base64.DEFAULT);
                    Bitmap bMap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView.setImageBitmap(bMap);
	                LatLng rest1 = new LatLng(x.getLatitud(), x.getLongitud());
                    mMap.addMarker(new MarkerOptions().position(rest1)
		                    .title(x.getNombre())
		                    .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker))));
                }
                break;
            //Cancelado
            case ListarRestaurantesTask.CANCELADO:
                break;

            //Error de conexión
            case ListarRestaurantesTask.ERROR:
                loadingPanel.setVisibility(View.GONE);
                Toast.makeText(this, getString(R.string.error_servidor), Toast.LENGTH_LONG).show();
                break;
        }

        if (restaurantes.size() != 0) {
            loadingPanel.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.ubicaciones_cargadas), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void busquedaIniciada() {
        loadingPanel.setVisibility(View.VISIBLE);
        Toast.makeText(this, getString(R.string.cargando_ubicaciones), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                    .zoom(15)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

	// Convert a view to bitmap
    private Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
		view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
		view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
		view.buildDrawingCache();
		Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);

		return bitmap;
	}
}
