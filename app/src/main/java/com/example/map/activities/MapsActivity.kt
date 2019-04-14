package com.example.map.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.map.R
import com.example.map.adapters.UserInfoWindowAdapter
import com.example.map.presenter.MapsPresenter

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private val RIGA = LatLng(56.9493977, 24.1051846)
    private val ZOOM = 10f;
    private var mPresenter : MapsPresenter = MapsPresenter()
    private var markers = HashMap<Int, Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.closeConnection()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        setupMap(googleMap);
        setupUsersLocation()
    }

    private fun setupMap(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(RIGA, ZOOM))
        mMap.setInfoWindowAdapter(UserInfoWindowAdapter(this))

      // mMap.setOnMarkerClickListener { marker ->
      //     true
      // }
    }

    private fun setupUsersLocation() {
        GlobalScope.launch(Dispatchers.IO) {
            var users = mPresenter.authorize()
            withContext(Dispatchers.Main) {
                for (user in users) {
                    val marker = mMap.addMarker(
                        MarkerOptions()
                            .title(user.username)
                            .position(LatLng(user.lat, user.lon))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                    marker.tag = user
                    markers[user.id] = marker;

                }
            }
        }
    }

}
