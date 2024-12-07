package com.example.michailkoupparis.songle;

import android.Manifest;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationRequest;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Calendar;

// Map Screen
public class MapsActivity
        extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private  List<Mark> markers = new ArrayList<Mark>() ;
    private Set<String> found_mark_names = new HashSet<>();
    private NetworkReceiver receiver = new NetworkReceiver();
    public static final String FOUND_MARKERS_TAG ="current_markers_tag";
    public static final String FOUND_MARKERS_KEY = "current_markers_key";
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted = false;
    // mLocationPermissionAnswered is for testing purposes to see if the Location Permission is closed
    public static boolean mLocationPermissionAnswered = false;
    private Location mLastLocation;
    private static final String TAG = "MapsActivity";
    private String dif_level;
    private String song_num;
    private String continue_or_new = "";
    private Float distance_walked;
    private String mode;
    private Integer mMapStyle ;
    private Set<String> mapMarkersTtiles =new HashSet<>();

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Find if the user selects to continue previous game or start a new one
        Bundle extras = getIntent().getExtras();
        continue_or_new = extras.getString(Home.CONTINUE_OR_NEW_GAME);

        // If this is a new game clear the found markers found
        if (continue_or_new.equals("NEW GAME")){
            found_mark_names = new HashSet<>();
            SharedPreferences.Editor editor =
                    getSharedPreferences(FOUND_MARKERS_TAG, MODE_PRIVATE).edit();
            editor.putStringSet(FOUND_MARKERS_KEY, found_mark_names);
            editor.apply();
        }
        // If the user selects to continue the previous game get the markers he found previously
        else{
            SharedPreferences found_pref =
                    getSharedPreferences(FOUND_MARKERS_TAG, MODE_PRIVATE);
            found_mark_names = found_pref.getStringSet(FOUND_MARKERS_KEY, new HashSet<String>());
        }

        // Get the difficulty level
        SharedPreferences difficulty_pref =
                getSharedPreferences(Settings.DIFFICULTY_LEVEL_TAG, MODE_PRIVATE);

        dif_level = difficulty_pref.getString(Settings.DIFFICULTY_LEVEL_KEY, "1");

        // Get the song number
        SharedPreferences song_number_pref =
                getSharedPreferences(SongSelection.SONG_PREF_TAG, MODE_PRIVATE);

        song_num = song_number_pref.getString(SongSelection.SONG_PREF_KEY, "01");

        // Get the distance walked so far
        SharedPreferences distance_walkes_pref =
                getSharedPreferences(Home.DISTANCE_TAG, MODE_PRIVATE);

        distance_walked = distance_walkes_pref.getFloat(Home.DISTANCE_KEY, 0);

        // Since the game is started set the setting preferences change to false since the game with those preferences started
        SharedPreferences.Editor editor_change =
                getSharedPreferences(Home.CHANGE_TAG, MODE_PRIVATE).edit();
        editor_change.putBoolean(Home.CHANGE_KEY, false);
        editor_change.apply();

        // Get the game mode
        SharedPreferences mode_pref =
                getSharedPreferences(Settings.MODE_TAG, MODE_PRIVATE);

        mode = mode_pref.getString(Settings.MODE_KEY, "near");

        // Check if location is enabled
        LocationManager lm = (LocationManager)
                MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);
        // If is not a warning dialog pop ups
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            ViewDialogLocation alert = new ViewDialogLocation();
            alert.showDialogLocation(this);
        }

        // Register BroadcastReceiver to track connection changes.


        IntentFilter filter = new
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);


        // Add a Listener to the Image button for accessing the Found Words Screen
        addListenerOnButton();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }



    }


    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        // Get the difficulty level
        SharedPreferences difficulty_pref =
                getSharedPreferences(Settings.DIFFICULTY_LEVEL_TAG, MODE_PRIVATE);

        dif_level = difficulty_pref.getString(Settings.DIFFICULTY_LEVEL_KEY, "1");

        // Get the Song number
        SharedPreferences song_number_pref =
                getSharedPreferences(SongSelection.SONG_PREF_TAG, MODE_PRIVATE);

        song_num = song_number_pref.getString(SongSelection.SONG_PREF_KEY, "01");

        // Get the distance walked so far
        SharedPreferences distance_walkes_pref =
                getSharedPreferences(Home.DISTANCE_TAG, MODE_PRIVATE);

        distance_walked = distance_walkes_pref.getFloat(Home.DISTANCE_KEY, 0);

        // Get the game mode
        SharedPreferences mode_pref =
                getSharedPreferences(Settings.MODE_TAG, MODE_PRIVATE);

        mode = mode_pref.getString(Settings.MODE_KEY, "near");

        // See if some preference change while the MapsActivity was on paused if yes then clear the found markers
        // Change the settings preference to false and download the markers for the current song
        SharedPreferences change_pref =
                getSharedPreferences(Home.CHANGE_TAG, MODE_PRIVATE);

        Boolean change = change_pref.getBoolean(Home.CHANGE_KEY, false);

        if(change){
            found_mark_names = new HashSet<>();
            SharedPreferences.Editor editor =
                    getSharedPreferences(FOUND_MARKERS_TAG, MODE_PRIVATE).edit();
            editor.putStringSet(FOUND_MARKERS_KEY, found_mark_names);
            editor.apply();
            SharedPreferences.Editor editor_change_pref =
                    getSharedPreferences(Home.CHANGE_TAG, MODE_PRIVATE).edit();
            editor_change_pref.putBoolean(Home.CHANGE_KEY, false);
            editor_change_pref.apply();
            // Register BroadcastReceiver to track connection changes.
            IntentFilter filter = new
                    IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            receiver = new NetworkReceiver();
            this.registerReceiver(receiver, filter);
        }


    }

    public void onBackPressed() {
        Intent activityChangeIntent = new Intent(MapsActivity.this, Home.class);
        startActivity(activityChangeIntent);
        finish();
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
    public void onMapReady (GoogleMap googleMap){

        mMap = googleMap;
        // Check the hour and use the map mode according to the hour (night and day modes)
        Calendar cal = Calendar.getInstance(); //Create Calendar-Object
        cal.setTime(new Date());               //Set the Calendar to now
        int hour = cal.get(Calendar.HOUR_OF_DAY); //Get the hour from the calendar
        if(hour <= 18 && hour >= 6)              // Check if hour is between 8 am and 11pm
        {
            try {
                // Day Mode
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                boolean success = mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.style_day_json));
                mMapStyle = R.raw.style_day_json;

                if (!success) {
                    Log.e(TAG, "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Can't find style. Error: ", e);
            }
        }
        else {

            try {
                // Night mode
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                boolean success = mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.style_night_json));
                mMapStyle = R.raw.style_night_json;

                if (!success) {
                    Log.e(TAG, "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Can't find style. Error: ", e);
            }
        }


        try {
            // Visualise current position with a small blue circle
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException se) {
            System.out.println("Security exception thrown [onMapReady]");
        }

        // Add ‘‘My location’’ button to the user interface
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Sets listener such that when user clicks on a placeMark he can collect words
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Sets Distance to an arbitrary large number so as if the location is disabled the user cannot collect the word
                Float distance = 300f;

                LatLng ltlng_marker_place = marker.getPosition();

                // mLastLocation is null if Location is Disabled and then the 300f will be used
                // else calculate the distance between the user and the marker
                if (mLastLocation != null ) {
                    Double lat_marker_place = ltlng_marker_place.latitude;
                    Double lng_marker_place = ltlng_marker_place.longitude;
                    Location marker_place = new Location("point marker");
                    marker_place.setLatitude(lat_marker_place);
                    marker_place.setLongitude(lng_marker_place);
                    distance = mLastLocation.distanceTo(marker_place);

                }

                // In near mode if the distance is under 5 meters then add the marker and display a success message
                //  and remove the marker from the map
                // else display a fail message
                if (mode.equals("near")) {
                    if (distance <= 5) {
                        found_mark_names.add(marker.getTitle());
                        marker.remove();
                        updatePreferences();
                        Toast.makeText(MapsActivity.this, "Word Added to Bag!!!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MapsActivity.this, "Sorry Too Far!!",
                                Toast.LENGTH_SHORT).show();

                    }
                }
                // Else in anywhere mode add the marker to found markers without calculating the distance and display a successful message
                // ALso remove the marker from the map
                else{
                    found_mark_names.add(marker.getTitle());
                    marker.remove();
                    updatePreferences();
                    Toast.makeText(MapsActivity.this, "Word Added to Bag!!!",
                            Toast.LENGTH_SHORT).show();
                }
                return true;

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void createLocationRequest() {
        // Set the parameters for the location request
        LocationRequest mLocationRequest = new LocationRequest();
        //mLocationRequest.setInterval(5000); // preferably every 5 seconds
        mLocationRequest.setFastestInterval(1000); // at most every second
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Can we access the user’s current location?
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

        }

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        try { createLocationRequest(); }
        catch (java.lang.IllegalStateException ise) {
            System.out.println("IllegalStateException thrown [onConnected]");
        }
        // Can we access the user’s current location?
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionAnswered = true;

            mLastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        else {
            // Ask for Location Permission
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        }
    }

    // Method for getting the response of the user in the Location Permission
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull  String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // Let the tests now the Location Permission is closed
                mLocationPermissionAnswered = true;
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    try {
                        // Visualise current position with a small blue circle

                        mMap.setMyLocationEnabled(true);

                        // Add "My location" button to the user interface

                        mMap.getUiSettings().setMyLocationButtonEnabled(true);

                    } catch (SecurityException se) {
                        System.out.println("Security exception thrown [onMapReady]");
                    }

                    mLocationPermissionGranted = true;

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    mLocationPermissionGranted = false;
                }
            }

        }
    }

    @Override
    public void onLocationChanged(Location current) {
        // mLastLocation is null if Location is Disabled
        // If location is enabled calculated the distance from current location to previous
        // and update the total distance walked

        if(mLastLocation!=null ){
            Float distance_walked_prev = mLastLocation.distanceTo(current);
            SharedPreferences.Editor editor_distance =
                    getSharedPreferences(Home.DISTANCE_TAG, MODE_PRIVATE).edit();
            editor_distance.putFloat(Home.DISTANCE_KEY, distance_walked_prev + distance_walked);
            editor_distance.apply();
        }

        mLastLocation = current;
        System.out.println(
                " [onLocationChanged] Lat/long now (" +
                        String.valueOf(current.getLatitude()) + "," +
                        String.valueOf(current.getLongitude()) + ")"

        );

    }

    @Override
    public void onConnectionSuspended(int flag) {
        System.out.println(" >>>> onConnectionSuspended");
    }
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently
        System.out.println(" >>>> onConnectionFailed");
    }


    // For downloading Map PlaceMarks for current Song and Difficulty Level
    public class NetworkReceiver extends BroadcastReceiver {

        @Override

        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null
                    && networkInfo.getType() ==
                    ConnectivityManager.TYPE_WIFI) {
                // Wi´Fi is connected, so use Wi´FI
                new DownloadXmlTask().execute("http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/"+song_num+"/map"+dif_level+".kml");
            } else if (networkInfo != null
                    && networkInfo.getType() ==
                    ConnectivityManager.TYPE_MOBILE){
                new DownloadXmlTask().execute("http://www.inf.ed.ac.uk/teaching/courses/selp/data/songs/"+song_num+"/map"+dif_level+".kml");
            }
            else {
                // No Wi´Fi and no permission, or no network connection
            }
        }
    }

    // For downloading the Xml
    private class DownloadXmlTask extends AsyncTask<String, Void , List<Mark> > {
        @Override
        protected List<Mark> doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                return null;
            } catch (XmlPullParserException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Mark> result) {
            markers = result;

            // For every Marker found in the xml file add the markers to the Map
            for (final Mark mark: markers){

                // Checks if the game is in New or Continue Mode, if it is then check if the marker is already found
                // And only add not found markers
                Boolean already_found = false;
                if(continue_or_new.equals("CONTINUE")){
                    if(found_mark_names.contains(mark.name)){
                        already_found = true;
                    }
                }
                if(!already_found) {
                    // Find the LatLng of the Marker and using the description found the image to use for the marker
                    LatLng now = new LatLng(mark.latitude, mark.longitude);
                    BitmapDescriptor markerIcon = null;
                    if (mark.description.equals("veryinteresting")) {
                        markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.very_interesting);
                    } else if (mark.description.equals("interesting")) {
                        markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.interesting);
                    } else if (mark.description.equals("notboring")) {
                        markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.not_boring);
                    } else if (mark.description.equals("boring")) {
                        markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.boring);
                    } else if (mark.description.equals("unclassified")) {
                        markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.unclassified);
                    }
                    // Add the marker to the Map using the position of the word at the lyrics (mark.name) as its title
                    // To keep record to which Marker each Mark corresponds
                    Marker marker = mMap.addMarker(new MarkerOptions().position(now).title(mark.name).icon(markerIcon));
                    // Adding the markers for test purposes on NewGameContinue Tsest
                    mapMarkersTtiles.add(marker.getTitle());

                }
            }

        }
    }


    private List<Mark> loadXmlFromNetwork(String urlString) throws
            XmlPullParserException, IOException {
        List<Mark> result = new ArrayList<Mark>();
        try (InputStream stream = downloadUrl(urlString)) {
            // Parse as KML and save the results to a List of Mak Objects
            result = parse(stream);

        }
        return result;
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // Also available: HttpsURLConnection
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    // Mark Object for representing Place Marks of the Kml file
    public static class Mark {
        // All Place MArk Attributes
        public final String name;
        public final String description;
        public final String styleURL;
        public final Double latitude;
        public final Double longitude;

        // Object Creator
        private Mark(String name, String description,String styleURL,String coordinates) {
            this.name = name;
            this.description = description;
            this.styleURL= styleURL;
            // Split the coordinates  of each Place Mark to Latitude and Longitude
            String lat =  "";
            String lon = "";
            Integer i = 0;
            while (coordinates.charAt(i)!=','){
                lon = lon + (coordinates.charAt(i));
                i++;
            }
            longitude = Double.parseDouble(lon);
            i++;
            while (coordinates.charAt(i)!=','){
                lat = lat + (coordinates.charAt(i));
                i++;
            }
            latitude = Double.parseDouble(lat);
        }
    }

    // We don’t use namespaces
    private static final String ns = null;
    List<Mark> parse(InputStream in) throws XmlPullParserException,
            IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,
                    false);
            parser.setInput(in, null);
            parser.nextTag();
            return readMarks(parser);
        } finally {
            in.close();
        }
    }

    // Read Each PLace Mark and add it to a List of Markers
    private List<Mark> readMarks(XmlPullParser parser) throws
            XmlPullParserException, IOException {
        List<Mark> marks = new ArrayList<Mark>();
        parser.require(XmlPullParser.START_TAG, ns, "kml");
        // Go to the next tag which is where all PlaceMarks are hold
        parser.nextTag();

        while (parser.next() != XmlPullParser.END_TAG ) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            // Starts by looking for the Placemark tag
            if (name.equals("Placemark")) {
                marks.add(readMark(parser));
            }
            else {
                skip(parser);
            }
        }
        return marks;
    }

    // Read each Placemark and create a MArk Object for it
    private Mark readMark(XmlPullParser parser) throws
            XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Placemark");
        String word = null; String description = null; String styleURL = null;  String coordinates = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;
            String name = parser.getName();
            if (name.equals("name")) {
                word = readName(parser);
            } else if (name.equals("description")) {
                description = readDescription(parser);
            } else if (name.equals("styleUrl")) {
                styleURL = readStyleURL(parser);
            } else if (name.equals("Point")) {
                coordinates = readPoint(parser);
            }
            else {
                skip(parser);
            }
        }
        return new Mark(word,description,styleURL, coordinates);
    }

    private String readName(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "name");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "name");
        return name;
    }

    private String readDescription(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "description");
        return description;
    }



    private String readStyleURL(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "styleUrl");
        String styleUrl = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "styleUrl");
        return styleUrl;
    }



    private String readPoint(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "Point");
        String coordinates = "";
        while (parser.next() != XmlPullParser.END_TAG ) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the coordinates tag
            if (name.equals("coordinates")) {
                coordinates = readCoordinates(parser);
            }
            else {
                skip(parser);
            }
        }
        return coordinates;
    }

    private String readCoordinates(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "coordinates");
        String styleUrl = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "coordinates");
        return styleUrl;
    }

    private String readText(XmlPullParser parser) throws IOException,
            XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // For skip tags
    private void skip(XmlPullParser parser) throws XmlPullParserException,
            IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0  ) {

            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }



    // Add Listener to Image Button which allows the user to go to the Found Words Activity
    public void addListenerOnButton() {

        ImageButton imageButton = (ImageButton) findViewById(R.id.bag_of_words);

        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent activityChangeIntent = new Intent(MapsActivity.this, FoundWords.class);
                startActivity(activityChangeIntent);

            }

        });

    }

    // For updating the found markers each time a user collects a new marker-word.
    private void updatePreferences() {
        SharedPreferences.Editor editor =
                getSharedPreferences(FOUND_MARKERS_TAG, MODE_PRIVATE).edit();
        editor.putStringSet(FOUND_MARKERS_KEY, found_mark_names);
        editor.apply();
    }


    // Dialog for location Warning when it is disabled
    private class ViewDialogLocation {

        private void showDialogLocation(final Activity activity) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.wifi_location_warning);

            TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
            text.setText(R.string.no_location_near);

            // If the user decides to continue without enabling the location
            // e.g if he don't care about calculating distance walked and he is in anywhere mode
            Button ok_button = (Button) dialog.findViewById(R.id.btn_ok_dialog);
            ok_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            // When the Retry Button is presses the app checks is the location is enabled again
            // If no the Dialog stays opened if yes it close
            Button retry_button = (Button) dialog.findViewById(R.id.btn_retry_dialog);
            retry_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LocationManager lm = (LocationManager)
                            MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);

                    if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                        dialog.dismiss();
                    }

                }
            });

            dialog.show();

        }

    }

    // For test Map mode in MapModeTest
    public Integer getmMapStyle(){
        return mMapStyle;
    }

    // For testing if found markers not appear in the Map in the NewGameContinue Test
    public Set<String> getMarkers(){
        return mapMarkersTtiles;
    }
}