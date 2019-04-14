package com.example.map.adapters


import android.app.Activity
import android.content.Context
import android.location.Geocoder
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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

        Glide.with(context.applicationContext)
            .load(user.imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .error(R.drawable.placeholder)
            .into(view.image)

        view.name.text = user.name
        view.address.text = coordinatesToStreet(context, user.lat, user.lon)

        return view
    }

    /**
     * Convert longitude and latitude to street name
     */
    fun coordinatesToStreet(context: Context, lat: Double, lon: Double): String {
        val geo = Geocoder(context);
        var address = "";
        if (geo.getFromLocation(lat, lon, 1).size > 0) {
            val location = geo.getFromLocation(lat, lon, 1)[0]
            if (TextUtils.isEmpty(location.thoroughfare)) {
                address = location.getAddressLine(0)
            } else  if (TextUtils.isEmpty(location.subThoroughfare)) {
                address = location.thoroughfare
            } else {
                address = "${location.thoroughfare} ${location.subThoroughfare}"
            }
        }
        return address;
    }
}