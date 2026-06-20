package com.example.hw1


import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.ListFragment
import com.example.hw1.fragments.MapFragment
import com.example.hw1.fragments.ScoreListFragment
import com.example.hw1.interfaces.CallBack_HighScoreCliked


class ScoreActivity : AppCompatActivity() {
    private lateinit var mapFragment: MapFragment
    private lateinit var listFragment: ScoreListFragment
    private lateinit var score_BTN_back: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)
        listFragment = ScoreListFragment()
        mapFragment = MapFragment()
        score_BTN_back = findViewById(R.id.score_BTN_back)
        score_BTN_back.setOnClickListener {  val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.score_FRAME_list,listFragment)
            .replace(R.id.score_FRAME_map, mapFragment)
            .commit()
        listFragment.setCallback(object : CallBack_HighScoreCliked{
            override fun highScoreItemClicked(lat: Double, lon: Double) {
                mapFragment.zoomToLocation(lat,lon)
            }
        })

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }}