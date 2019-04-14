package com.example.map.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.map.R
import com.example.map.adapters.UserInfoWindowAdapter
import com.example.map.consts.Config
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

    private val RIGA = LatLng(56.9493977, 24.1051846)
    private val ZOOM = 14f

    private lateinit var mMap: GoogleMap
    private lateinit var mPresenter: MapsPresenter
    private lateinit var mAdapter: UserInfoWindowAdapter
    private var mMarkers = HashMap<Int, Marker>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        mPresenter = MapsPresenter(Config.SERVER, Config.PORT)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        setupMap(googleMap)
        setupUsersLocation()
        handleLocationChanges();
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.closeConnection()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private fun setupMap(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(RIGA, ZOOM))
        mAdapter = UserInfoWindowAdapter(this)
        mMap.setInfoWindowAdapter(UserInfoWindowAdapter(this))
        mMap.setOnMapClickListener { hideSystemUI() }
    }

    private fun setupUsersLocation() {
        GlobalScope.launch(Dispatchers.IO) {
            var users = mPresenter.authorize()
            withContext(Dispatchers.Main) {
                for (user in users) {
                    val marker = mMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(user.lat, user.lon))
                            .icon(BitmapDescriptorFactory.fromBitmap(
                                BitmapFactory.decodeResource(resources, R.drawable.point))))
                    marker.tag = user
                    mMarkers[user.id] = marker
                }
            }
        }
    }

    //TODO mb hide logic to presenter
    private fun handleLocationChanges() {
        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
               val user = mPresenter.update();
               if (user != null) {
                   withContext(Dispatchers.Main) {
                       val marker = mMarkers[user.id];
                       marker!!.position = LatLng(user.lat, user.lon)
                       if (marker.isInfoWindowShown) {
                           marker.hideInfoWindow()
                           marker.showInfoWindow()
                       }

                   }
               }
            }
        }
    }

}
