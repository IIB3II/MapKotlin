package com.example.map.adapters


import android.app.Activity
import android.content.Context
import android.location.Geocoder
import android.view.View
import com.bumptech.glide.Glide
import com.example.map.R
import com.example.map.models.User
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.userinfo_layout.view.*

class UserInfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        val view = (context as Activity).layoutInflater
            .inflate(R.layout.userinfo_layout, null)

        val user = marker.tag as User
        view.name.text = user.username
        view.address.text = coordinatesToStreet(context, user.lat, user.lon)
        Glide.with(view).load(user.imageUrl).into(view.image);

        return view
    }

    /**
     * Convert longitude and latitude to street name
     */
    fun coordinatesToStreet(context: Context, lat: Double, lon: Double): String {
        val geo = Geocoder(context);
        var address = "";
        //TODO add safe check
        address = geo.getFromLocation(lat, lon, 1)[0].getAddressLine(0);
        return address;
    }
}