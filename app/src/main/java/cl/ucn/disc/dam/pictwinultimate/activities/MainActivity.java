package cl.ucn.disc.dam.pictwinultimate.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import cl.ucn.disc.dam.pictwinultimate.R;
import cl.ucn.disc.dam.pictwinultimate.domain.Pic;
import cl.ucn.disc.dam.pictwinultimate.domain.ImageAdapter;
import cl.ucn.disc.dam.pictwinultimate.domain.Twin;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import cl.ucn.disc.dam.pictwinultimate.util.DeviceUtils;

import static java.lang.Thread.sleep;

/**
 * @author David Meza Astengo
 */
public class MainActivity extends Activity {

    private String urlphp = "http://192.168.0.8";
    Button addImage;
    private Pic picUni;
    Uri uriUni;
    String encodedImageUni = "asdasd";

    private static final int CAMERA_REQUEST = 1;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // Cuchillo con mantequilla !
        ButterKnife.bind(this);
        // lista para el adapter
        ArrayList<Twin> imageArry = new ArrayList<Twin>();
        {
            FlowManager.init(new FlowConfig.Builder(getApplicationContext())
                    .openDatabasesOnInit(true).build());

        }
        {
            ListView dataList = (ListView) findViewById(R.id.list);

            List<Twin> twins = SQLite.select().from(Twin.class).queryList();

            for (Twin cn : twins) {
                String log = "ID:" + cn.getLocal().getId() + " Name: "
                        + " ,Image: " + cn.getLocal().getUrl();

                // Writing Contacts to log
                Log.d("Result: ", log);
                // add contacts data in arrayList
                imageArry.add(cn);

            }

            /**
             * Set Data base Item into listview
             */
            ImageAdapter adapter = new ImageAdapter(this, imageArry);
            dataList.setAdapter(adapter);

        }
        /**
         * open dialog for choose camera
         */

        final String[] option = new String[] { "Take from Camera" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.select_dialog_item, option);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select Option");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Log.e("Selected Item", String.valueOf(which));
                if (which == 0) {
                    callCamera();
                }

            }
        });
        final AlertDialog dialog = builder.create();

        addImage = (Button) findViewById(R.id.btnAdd);

        addImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case CAMERA_REQUEST:
                Intent i = new Intent(MainActivity.this, MainActivity.class);
                startActivity(i);
                finish();

        }
    }

	/*
	 * @Override protected void onActivityResult(int requestCode, int
	 * resultCode, Intent data) { if (requestCode == REQUEST_TAKE_PHOTO &&
	 * resultCode == Activity.RESULT_OK) {
	 *
	 * Bitmap bm = BitmapFactory.decodeFile(uriUni.toString());
	 * ByteArrayOutputStream baos = new ByteArrayOutputStream();
	 * bm.compress(Bitmap.CompressFormat.JPEG, 50, baos); byte[] byteArray1 =
	 * baos.toByteArray(); String encodedImage =
	 * Base64.encodeToString(byteArray1,Base64.DEFAULT); encodedImageUni =
	 * encodedImage;
	 *
	 * }
	 *
	 * }
	 */

    static final int REQUEST_TAKE_PHOTO = 1;

    public void callCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go

            File pictureDirectory = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            String pictureName = getPictureName();
            File imageFile = new File(pictureDirectory, pictureName);
            Uri pictureUri = Uri.fromFile(imageFile);// variable con uri


            // direccion
            intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
            uriUni = pictureUri;
            startActivityForResult(intent, CAMERA_REQUEST);

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String deviceID = DeviceUtils.getDeviceId(getApplicationContext());
            String url = urlphp + "/insert/pic";

            Log.d("Insert: ", "Inserting ..");

            Long date = getDate();
            Double[] positions = getPosicion();
            Double latitud = positions[0];
            Double longitud = positions[1];
            {
                FlowManager
                        .init(new FlowConfig.Builder(getApplicationContext())
                                .openDatabasesOnInit(true).build());

            }
            Pic pic = Pic.builder().deviceId(deviceID).latitude(latitud)
                    .longitude(longitud).negative(0).positive(0).warning(0)
                    .date(date).url(pictureUri.toString()).build();
            pic.save();

            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("pic", encodedImageUni);
                jsonBody.put("deviceId", DeviceUtils.getDeviceId(this));
                jsonBody.put("date", date);
                jsonBody.put("latitud", latitud);
                jsonBody.put("longitud", longitud);
                final String mRequestBody = jsonBody.toString();
                StringRequest stringRequest = new StringRequest(
                        Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i("VOLLEY", response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", error.toString());
                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return mRequestBody == null ? null : mRequestBody
                                    .getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog
                                    .wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                            mRequestBody, "utf-8");
                            return null;
                        }
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(
                            NetworkResponse response) {
                        String responseString = "";
                        if (response != null) {
                            responseString = String
                                    .valueOf(response.statusCode);
                            // can get more details such as response.headers
                        }
                        return Response.success(responseString,
                                HttpHeaderParser.parseCacheHeaders(response));
                    }
                };

                requestQueue.add(stringRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // get remota

            url = urlphp + "/get/pic";

            RequestQueue queue = Volley.newRequestQueue(this);

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("deviceId", deviceID);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final String mRequestBody = jsonBody.toString();
            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {

                        File pictureDirectory = Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                        String pictureName1 = getPictureName();
                        File imageFile1 = new File(pictureDirectory,
                                pictureName1);
                        Uri pictureUri1 = Uri
                                .fromFile(pictureDirectory);// variable
                        // con uri
                        // direccion

                        JSONObject jsonObject = new JSONObject(response);
                        jsonObject.get("pic").toString();

                        byte[] bytes = Base64.decode(
                                jsonObject.get("pic").toString(), 0);

                        FileOutputStream fileOutputStream = new FileOutputStream(
                                imageFile1, true);
                        fileOutputStream.write(bytes);
                        fileOutputStream.flush();
                        fileOutputStream.close();

                        Pic pic1 = Pic
                                .builder()
                                .deviceId(
                                        jsonObject.get("deviceId")
                                                .toString())
                                .latitude(
                                        Double.parseDouble(jsonObject
                                                .get("latitud")
                                                .toString()))
                                .longitude(
                                        Double.parseDouble(jsonObject
                                                .get("longitud")
                                                .toString()))
                                .negative(0)
                                .positive(0)
                                .warning(0)
                                .date(Long.parseLong(jsonObject.get(
                                        "date").toString()))
                                .url(pictureUri1.toString()).build();
                        pic1.save();
                        picUni = pic1;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody
                                .getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog
                                .wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                        mRequestBody, "utf-8");
                        return null;
                    }
                }
            };
            queue.add(stringRequest);

			/*
			 * insertar twins
			 */

            Pic pic1 = Pic.builder().deviceId(deviceID).latitude(latitud)
                    .longitude(longitud).negative(0).positive(0).warning(0)
                    .date(date).url(pictureUri.toString()).build();
            pic1.save();

            Twin twin = Twin.builder().local(pic).remote(pic1).build();
            twin.save();

        }

    }

    private String getPictureName() {

        SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timeStamp = date.format(new Date());
        return "PicTwin" + timeStamp + ".jpg";

    }

    private Double[] getPosicion() {
        LocationManager locationManager = (LocationManager) getApplication()
                .getSystemService(Context.LOCATION_SERVICE);
        final Double[] salida = new Double[2];

        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        Double longitud;
        Double latitud;
        if (isGPSEnabled) {
            try {
                Criteria criteria = new Criteria();
                String provider = locationManager.getBestProvider(criteria,
                        false);
                Location location = locationManager
                        .getLastKnownLocation(provider);

                if (location != null) {
                    longitud = location.getLongitude();
                    latitud = location.getLatitude();
                    salida[0] = latitud;
                    salida[1] = longitud;

                } else {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, 0, 0,
                            new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                    Double longitud = location.getLongitude();
                                    Double latitud = location.getLatitude();
                                    salida[0] = latitud;
                                    salida[1] = longitud;

                                }

                                @Override
                                public void onStatusChanged(String s, int i,
                                                            Bundle bundle) {

                                }

                                @Override
                                public void onProviderEnabled(String s) {

                                }

                                @Override
                                public void onProviderDisabled(String s) {

                                }
                            });
                }

            } catch (SecurityException e) {
                e.printStackTrace();
            }

        }
        return salida;
    }

    private Long getDate() {
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
        Long timeStamp = Long.parseLong(date.format(new Date()));
        return timeStamp;
    }

}
