package com.example.hw1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hw1.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapFragment : Fragment(), OnMapReadyCallback{
    private var googleMap: com.google.android.gms.maps.GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_map,container,false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.google_Map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        return v
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        val scoreManager = com.example.hw1.logic.ScoreManager(requireContext())
        val topScores = scoreManager.getTopScore()
        for (i in topScores.indices) {
            val currentScore = topScores[i]
            val location = LatLng(currentScore.lat, currentScore.lon)

            googleMap?.addMarker(
                MarkerOptions()
                    .position(location)
                    .title("Rank ${i + 1} - Score: ${currentScore.score}")
            )
        }
        if (topScores.isNotEmpty()) {
            val firstLocation = LatLng(topScores[0].lat, topScores[0].lon)
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 7f))
        } else {
            val defaultLocation = LatLng(32.0853, 34.7818)
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 7f))
        }
    }

    fun zoomToLocation(lat: Double,lon: Double) {
        if (googleMap == null)
            return
        val location = LatLng(lat,lon)
        //googleMap?.addMarker(com.google.android.gms.maps.model.MarkerOptions().position(location).title("Score Point"))
        googleMap?.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(location,15f))
    }

}