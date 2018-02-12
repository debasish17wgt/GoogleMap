package com.wgt.mapintegration.activity;

import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.wgt.mapintegration.R;
import com.wgt.mapintegration.database.AppDatabase;
import com.wgt.mapintegration.model.LocationModel;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_GREEN_ARGB = 0xff388E3C;
    private static final int COLOR_PURPLE_ARGB = 0xff81C784;
    private static final int COLOR_ORANGE_ARGB = 0xffF57F17;
    private static final int COLOR_BLUE_ARGB = 0xffF9A825;

    private static final int POLYLINE_STROKE_WIDTH_PX = 5;
    private static final int POLYGON_STROKE_WIDTH_PX = 8;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final int PATTERN_GAP_LENGTH_PX = 20;

    private GoogleMap mMap;
    //private Location location;
    private List<LocationModel> listOfLoc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //get location from previous activity
        /*Location loc = (Location) getIntent().getParcelableExtra("location");
        if (loc != null) {
            location = loc;
        }*/
        listOfLoc = AppDatabase.getDatabase(this).locationDao().getAllLocations();
        if (listOfLoc.size() < 0 ) {
            Toast.makeText(this, "No Location found", Toast.LENGTH_SHORT).show();
        }
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


        // Add polylines to the map.
        // Polylines are useful to show a route or some other connection between points.
        PolylineOptions options = new PolylineOptions().clickable(true);
        if (listOfLoc != null) {
            for (LocationModel loc : listOfLoc) {
                options.add(new LatLng(loc.getLatitude(), loc.getLongitude()));
            }
        }

        Polyline polyline1 = googleMap.addPolyline(options);
        // Store a data object with the polyline, used here to indicate an arbitrary type.
        polyline1.setTag("A");
        // Style the polyline.
        stylePolyline(polyline1);

        // Add a marker in Sydney and move the camera
        LatLng position = new LatLng(listOfLoc.get(listOfLoc.size()-1).getLatitude(), listOfLoc.get(listOfLoc.size()-1).getLongitude());
        mMap.addMarker(new MarkerOptions().position(position).title("Current Position").snippet(""+listOfLoc.get(listOfLoc.size()-1).getLatitude()+","+listOfLoc.get(listOfLoc.size()-1).getLongitude()));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(position.latitude, position.longitude), 17));
        setZoomBoundery(listOfLoc);




//        // Add polygons to indicate areas on the map.
//        Polygon polygon1 = googleMap.addPolygon(new PolygonOptions()
//                .clickable(true)
//                .add(
//                        new LatLng(-27.457, 153.040),
//                        new LatLng(-33.852, 151.211),
//                        new LatLng(-37.813, 144.962),
//                        new LatLng(-34.928, 138.599)));
//        // Store a data object with the polygon, used here to indicate an arbitrary type.
//        polygon1.setTag("alpha");
    }

    /**
     * Styles the polyline, based on type.
     * @param polyline The polyline object that needs styling.
     */
    private void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "A":
                // Use a custom bitmap as the cap at the start of the line.
                polyline.setStartCap(
                        new CustomCap(
                                BitmapDescriptorFactory.fromResource(android.R.drawable.arrow_up_float), 10));
                break;
            case "B":
                // Use a round cap at the start of the line.
                polyline.setStartCap(new RoundCap());
                break;
        }

        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_GREEN_ARGB);
        polyline.setJointType(JointType.BEVEL);
    }

    private void setZoomBoundery(List<LocationModel> list) {
        if (list== null || list.size() == 0 ) {
            return;
        }

        /**create for loop/manual to add LatLng's to the LatLngBounds.Builder*/
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LocationModel ll : list) {
            builder.include(new LatLng(ll.getLatitude(), ll.getLongitude()));
        }

        /**initialize the padding for map boundary*/
        int padding = 50;
        /**create the bounds from latlngBuilder to set into map camera*/
        LatLngBounds bounds = builder.build();
        /**create the camera with bounds and padding to set into map*/
        final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        /**call the map call back to know map is loaded or not*/
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                /**set animated zoom camera into map*/
                mMap.animateCamera(cu);
            }
        });
    }
}
