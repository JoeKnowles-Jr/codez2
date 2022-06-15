package com.jk.codez.fragments;

import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.jk.codez.AcAdapter;
import com.jk.codez.AutoCompTvWithButtons;
import com.jk.codez.CodezViewModel;
import com.jk.codez.item.Item;
import com.jk.codez.item.ItemAdapter;
import com.jk.codez.item.ItemClickSupport;
import com.jk.codez.R;
import com.jk.codez.ad.AestheticDialog;
import com.jk.codez.ad.ButtonClickListener;
import com.jk.codez.ad.DialogAnimation;
import com.jk.codez.ad.DialogStyle;
import com.jk.codez.databinding.FragmentRemoteBinding;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.Locale;

public class RemoteFragment extends Fragment implements LocationListener {

    private static final String DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID";

    private FragmentRemoteBinding binding;
    private ItemAdapter mAdapter;
    private CodezViewModel mViewModel;
    private LocationComponent mLocationComponent;
    private LocationManager mLocationManager;
    private MapView mMapView;
    private MapboxMap mMapboxMap;
    private ImageView hoveringMarker;
    private Layer droppedMarkerLayer;
    private boolean searchReverse = false;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentRemoteBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(CodezViewModel.class);

        mViewModel.getRemoteItems().observe(getViewLifecycleOwner(), items -> {
            ((Toolbar) requireActivity().findViewById(R.id.toolbar)).setTitle(String.format(Locale.getDefault(), "Remote - %d", items.size()));
            mAdapter = new ItemAdapter(items, null);
            binding.rvCodes.setAdapter(mAdapter);
            binding.searchView.setAdapter(
                    new AcAdapter(requireContext(), R.layout.item_search, items)
            );
            binding.rvCodes.setVisibility(View.VISIBLE);
            binding.pb.setVisibility(View.GONE);
        });

        AutoCompTvWithButtons actv = binding.searchView;
        actv.setOnItemClickListener((adapterView, view12, i, l) -> {

        });

        ItemClickSupport.addTo(binding.rvCodes)
                .setOnItemClickListener((recyclerView, position, view1) -> showItem(mAdapter.get(position)))
                .setOnItemLongClickListener((recyclerView, position, view1) -> {
                    System.out.println(mAdapter.get(position).toString());
                    showDialog(mAdapter.get(position));
                    return true;
                });

        DividerItemDecoration div = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);

        div.setDrawable(getResources().getDrawable(R.drawable.divider_blue, requireActivity().getTheme()));

        binding.rvCodes.addItemDecoration(div);

        binding.fab1.setOnClickListener(view1 -> showDialog(null));

        binding.btnNumber.setOnClickListener(v -> mViewModel.sortListByNumber(searchReverse));

        binding.btnStreet.setOnClickListener(v -> mViewModel.sortListByStreet(searchReverse));

        binding.btnReverse.setOnClickListener(v -> reverseSearch());
    }

    private void reverseSearch() {
        searchReverse = !searchReverse;
        binding.btnReverse.setTextColor(searchReverse ? Color.GREEN : Color.BLACK);
    }

    @SuppressLint("MissingPermission")
    private void displayItemDialog(@Nullable final Item item) {
        AestheticDialog.Builder builder = new AestheticDialog.Builder(requireActivity(), DialogStyle.DETAIL)
                .setItem(item == null ? new Item() : item)
                .setIsEdit(item != null)
                .setButtonClickListener(new ButtonClickListener() {
                    @Override
                    public void onSave(@NonNull AestheticDialog.Builder dialog) {
                        System.out.println("ff - onsave");
                        Item i = dialog.getItem();
                        if (item == null) {
                            mViewModel.addRemoteItem(i, dialog);
                        } else {
                            mViewModel.editRemoteItem(i, dialog);
                        }
                    }

                    @Override
                    public void onDelete(@NonNull AestheticDialog.Builder dialog) {
                        mViewModel.deleteRemoteItem(dialog);
                    }
                })
                .setAnimation(DialogAnimation.IN_OUT);
        this.mMapView = builder.getMapView();
        assert this.mMapView != null;
        this.mMapView.getMapAsync(map -> {
            this.mMapboxMap = map;
            this.mMapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41"), style -> {
                assert item != null;
                initDroppedMarker(style);
                // Show the SymbolLayer icon to represent the selected map location
                if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {
                    GeoJsonSource source = style.getSourceAs("dropped-marker-source-id");
                    if (source != null) {
                        source.setGeoJson(Point.fromLngLat(item.getLng(), item.getLat()));
                    }
                    droppedMarkerLayer = style.getLayer(DROPPED_MARKER_LAYER_ID);
                    if (droppedMarkerLayer != null) {
                        droppedMarkerLayer.setProperties(visibility(VISIBLE));
                    }
                }
                if (item.lat != null) {
                    mMapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng((item.lat), item.lng))
                                    .zoom(17)
                                    .build()), 1000);
                }
            });
        });
        builder.show();
    }

    private LocationManager getLM() {
        if (mLocationManager == null)
            mLocationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        return mLocationManager;
    }


//    @Nullable
//    @SuppressLint("MissingPermission")
//    private Location getCurrentLocation() {
//        Criteria criteria = new Criteria();
//        final String bestProvider = String.valueOf(getLM().getBestProvider(criteria, true));
//
//        if (mViewModel.iHavePermission()) {
//            Location location = getLM().getLastKnownLocation(bestProvider);
//            if (location != null) {
//                final double latitude = location.getLatitude();
//                final double longitude = location.getLongitude();
//                Toast.makeText(requireContext(), "latitude:" + latitude + "\nlongitude:" + longitude, Toast.LENGTH_SHORT).show();
//            } else {
//                getLM().requestLocationUpdates(bestProvider, 1000, 0, this);
//            }
//            return location;
//        }
//        return null;
//    }

    private void showItem(@Nullable final Item item) {
        if (mViewModel.iHavePermission()) {
            displayItemDialog(item);
        }
    }

    @SuppressLint("MissingPermission")
    private void showDialog(@Nullable final Item item) {
        Item i = (item == null) ? new Item() : item;
        AestheticDialog.Builder builder = new AestheticDialog.Builder(requireActivity(), DialogStyle.CODEZ);
//        Location l = getCurrentLocation();
        Location l = mViewModel.getCurrentLocation();
        if (item == null && l != null) {
            i.setLat(l.getLatitude());
            i.setLng(l.getLongitude());
        }
        builder.setItem(i);
        builder.setIsEdit(item != null);

        builder.setButtonClickListener(new ButtonClickListener() {
            @Override
            public void onSave(@NonNull AestheticDialog.Builder dialog) {
                Item i = dialog.getItem();
                if (item == null) {
                    mViewModel.addRemoteItem(i, dialog);
                } else {
                    mViewModel.editRemoteItem(i, dialog);
                }
            }

            @Override
            public void onDelete(@NonNull AestheticDialog.Builder dialog) {
                mViewModel.deleteRemoteItem(dialog);
            }
        });

        builder.setAnimation(DialogAnimation.FADE);
        View v = builder.getLayout();
        this.mMapView = v.findViewById(R.id.mv_edit);
        Button selectLocationButton = v.findViewById(R.id.move_pin_set);
        assert this.mMapView != null;

        this.mMapView.getMapAsync(map -> {
            this.mMapboxMap = map;
            this.mMapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
                mLocationComponent = mMapboxMap.getLocationComponent();
                LocationComponentOptions lco = LocationComponentOptions.builder(requireContext())
                        .pulseEnabled(true)
                        .build();
                mLocationComponent.activateLocationComponent(
                        LocationComponentActivationOptions.builder(requireContext(), style)
                                .locationComponentOptions(lco)
                                .build());
                if (mViewModel.iHavePermission())
                    mLocationComponent.setLocationComponentEnabled(true);
                mLocationComponent.setCameraMode(CameraMode.TRACKING);
                mLocationComponent.setRenderMode(RenderMode.COMPASS);

                // When user is still picking a location, we hover a marker above the mapboxMap in the center.
                // This is done by using an image view with the default marker found in the SDK. You can
                // swap out for your own marker image, just make sure it matches up with the dropped marker.
                hoveringMarker = new ImageView(requireContext());
                hoveringMarker.setImageResource(R.drawable.red_marker);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                hoveringMarker.setLayoutParams(params);
                this.mMapView.addView(hoveringMarker);

                // Initialize, but don't show, a SymbolLayer for the marker icon which will represent a selected location.
                initDroppedMarker(style);

                if (item != null && item.lat != null) {
                    mMapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng((item.lat), item.lng))
                                    .zoom(17)
                                    .build()), 1000);
                }

                // Button for user to drop marker or to pick marker back up.
                selectLocationButton.setOnClickListener(view -> {
                    if (hoveringMarker.getVisibility() == View.VISIBLE) {

                        // Use the map target's coordinates to make a reverse geocoding search
                        final LatLng mapTargetLatLng = this.mMapboxMap.getCameraPosition().target;
                        builder.setPosition(mapTargetLatLng);

                        // Hide the hovering red hovering ImageView marker
                        hoveringMarker.setVisibility(View.INVISIBLE);

                        // Transform the appearance of the button to become the cancel button
                        selectLocationButton.setBackgroundColor(
                                ContextCompat.getColor(requireContext(), R.color.blue_700));
                        selectLocationButton.setText(getString(R.string.place_pin));

                        // Show the SymbolLayer icon to represent the selected map location
                        if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {
                            GeoJsonSource source = style.getSourceAs("dropped-marker-source-id");
                            if (source != null) {
                                source.setGeoJson(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));
                            }
                            droppedMarkerLayer = style.getLayer(DROPPED_MARKER_LAYER_ID);
                            if (droppedMarkerLayer != null) {
                                droppedMarkerLayer.setProperties(visibility(VISIBLE));
                            }
                        }

                        // Use the map camera target's coordinates to make a reverse geocoding search
//                    reverseGeocode(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));

                    } else {

                        // Switch the button appearance back to select a location.
                        selectLocationButton.setBackgroundColor(
                                ContextCompat.getColor(requireContext(), R.color.cardview_dark_background));
                        selectLocationButton.setText(getString(R.string.set_pin));

                        // Show the red hovering ImageView marker
                        hoveringMarker.setVisibility(View.VISIBLE);

                        // Hide the selected location SymbolLayer
                        droppedMarkerLayer = style.getLayer(DROPPED_MARKER_LAYER_ID);
                        if (droppedMarkerLayer != null) {
                            droppedMarkerLayer.setProperties(visibility(NONE));
                        }
                    }
                });
            });
        });
        builder.show();
    }

    private void initDroppedMarker(@NonNull Style loadedMapStyle) {
        // Add the marker image to map
        loadedMapStyle.addImage("dropped-icon-image", BitmapFactory.decodeResource(
                getResources(), R.drawable.green_marker));
        loadedMapStyle.addSource(new GeoJsonSource("dropped-marker-source-id"));
        loadedMapStyle.addLayer(new SymbolLayer(DROPPED_MARKER_LAYER_ID,
                "dropped-marker-source-id").withProperties(
                iconImage("dropped-icon-image"),
                visibility(NONE),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        ));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mMapView != null) {
            mMapView.onStart();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMapView != null) {
            mMapView.onStop();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();// Prevent leaks
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}