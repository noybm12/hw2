package com.example.hw1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MenuActivity : AppCompatActivity() {
    private lateinit var menu_BTN_buttongame : AppCompatButton
    private lateinit var menu_SWITCH_speed : SwitchCompat
    private lateinit var menu_BTN_sensorgame : AppCompatButton
    private lateinit var menu_BTN_topten: AppCompatButton



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        menu_BTN_buttongame = findViewById(R.id.menu_BTN_buttongame)
        menu_SWITCH_speed = findViewById(R.id.menu_SWITCH_speed)
        menu_BTN_sensorgame = findViewById(R.id.menu_BTN_sensorgame)
        menu_BTN_topten = findViewById(R.id.menu_BTN_topten)
        menu_BTN_buttongame.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("FastMode",menu_SWITCH_speed.isChecked)
            startActivity(intent)
        }
        menu_BTN_sensorgame.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("sensorMode",true)
            intent.putExtra("FastMode",menu_SWITCH_speed.isChecked)
            startActivity(intent)
        }
        menu_BTN_topten.setOnClickListener {
            val intent = Intent(this, ScoreActivity::class.java)
            startActivity(intent)
        }

    }
}