package com.example.desespero2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private GoogleMap mMap;
    private String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng latLng =  null;

    public String teste;
    Pop pop = new Pop();
    Post post = new Post();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Validar permissões
        Permissoes.validarPermissoes(permissoes, this, 1);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        Gson gson = new GsonBuilder().serializeNulls().create();
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();



        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request originalRequest = chain.request();

                        Request newRequest = originalRequest.newBuilder()
                                .header("Interceptor-Header", "xyz")
                                .build();

                        return chain.proceed(newRequest);
                    }
                })
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Call<List<Post>> call = jsonPlaceHolderApi.getPosts();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                List<Post> posts = response.body();

                for (Post post : posts) {

                    LatLng localUsuario = new LatLng(post.getLatitude(), post.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(localUsuario).title(post.getNome()));
                }

            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(MapsActivity.this, t.getMessage() ,
                        Toast.LENGTH_SHORT).show();
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {

                teste = pop.teste;
                final Double latitude = latLng.latitude;
                final Double longitude = latLng.longitude;

                Bundle extras = getIntent().getExtras();

                post.setNome(extras.getString("passarNome"));

                post.setDataInclusao(new Date(System.currentTimeMillis()));
                post.setLatitude(latitude);
                post.setLongitude(longitude);

                Call<Post> call = jsonPlaceHolderApi.createPost(post);

                call.enqueue(new Callback<Post>() {
                    @Override
                    public void onResponse(Call<Post> call, Response<Post> response) {

                        Toast.makeText(MapsActivity.this,
                                "onClick Lat: " + latitude + " long:" + longitude ,
                                Toast.LENGTH_SHORT).show();

                        mMap.addMarker(
                                new MarkerOptions().position(latLng)
                                        .title(post.getNome())
                                        .snippet("não sei")
                        );
                    }

                    @Override
                    public void onFailure(Call<Post> call, Throwable t) {
                        Toast.makeText(MapsActivity.this, t.getMessage() ,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                marker.getPosition();
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng latLng = marker.getPosition();
                Map<String, String> headers = new HashMap<>();
                headers.put("Map-Header1", "def");
                headers.put("Map-Header2", "ghi");

                post.setNome(marker.getTitle());
                post.setLongitude(latLng.longitude);
                post.setLongitude(latLng.latitude);

                Call<Post> call = jsonPlaceHolderApi.putPost(headers,122, post);

                call.enqueue(new Callback<Post>() {
                    @Override
                    public void onResponse(Call<Post> call, Response<Post> response) {

                    }

                    @Override
                    public void onFailure(Call<Post> call, Throwable t) {

                    }
                });


            }
        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                marker.getId();

                marker.getPosition();

                post.getId();
                Call<Void> call = jsonPlaceHolderApi.deletePost(197);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Toast.makeText(MapsActivity.this,
                                "Marcador apagado!" ,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(MapsActivity.this, t.getMessage() ,
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }
        });


        //Objeto responsável por gerenciar a localização do usuário
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Localizacao", "onLocationChanged: " + location.toString() );

                Double latitude = location.getLatitude();
                Double longitude = location.getLongitude();

                //-23.593054, -46.663584
                //-23.590679, -46.652288


                /*
                Geocoding -> processo de transformar um endereço
                ou descrição de um local em latitude/longitude
                Reverse Geocoding -> processo de transformar latitude/longitude
                em um endereço
                */
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault() );

                try {

                    List<Address> listaEndereco = geocoder.getFromLocation(latitude, longitude,1);
                    String stringEndereco = "Avenida Paulista, 1374 - Bela Vista, São Paulo - SP";
//                    List<Address> listaEndereco = geocoder.getFromLocationName(stringEndereco,1);
                    if( listaEndereco != null && listaEndereco.size() > 0 ){
                        Address endereco = listaEndereco.get(0);

                        /*
                        * onLocationChanged:
                        * Address[
                        *   addressLines=[0:"Av. República do Líbano, 1291 - Parque Ibirapuera, São Paulo - SP, Brazil"],
                        *   feature=1291,
                        *   admin=São Paulo,
                        *   sub-admin=São Paulo,
                        *   locality=São Paulo,
                        *   thoroughfare=Avenida República do Líbano,
                        *   postalCode=null,
                        *   countryCode=BR,
                        *   countryName=Brazil,
                        *   hasLatitude=true,
                        *   latitude=-23.5926719,
                        *   hasLongitude=true,
                        *   longitude=-46.6647561,
                        *   phone=null,
                        *   url=null,
                        *   extras=null]
                        * */

                        Double lat = endereco.getLatitude();
                        Double lon = endereco.getLongitude();

                        mMap.clear();
                        LatLng localUsuario = new LatLng(lat, lon);
                        mMap.addMarker(new MarkerOptions().position(localUsuario).title("Meu local"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localUsuario,18));

                        Log.d("local", "onLocationChanged: " + endereco.getLatitude() + endereco.getLongitude() );
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        /*
                * 1) Provedor da localização
                * 2) Tempo mínimo entre atualizacões de localização (milesegundos)
                * 3) Distancia mínima entre atualizacões de localização (metros)
                * 4) Location listener (para recebermos as atualizações)
                * */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000000,
                    100000,
                    locationListener
            );

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults) {

            //permission denied (negada)
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                //Alerta
                alertaValidacaoPermissao();
            } else if (permissaoResultado == PackageManager.PERMISSION_GRANTED) {
                //Recuperar localizacao do usuario

                /*
                * 1) Provedor da localização
                * 2) Tempo mínimo entre atualizacões de localização (milesegundos)
                * 3) Distancia mínima entre atualizacões de localização (metros)
                * 4) Location listener (para recebermos as atualizações)
                * */
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            0,
                            0,
                            locationListener
                    );
                }

            }
        }

    }

    private void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

}
