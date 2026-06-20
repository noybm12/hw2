package com.example.hw1

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hw1.logic.GameManager
import android.os.VibrationEffect
import android.text.format.DateFormat
import com.example.hw1.data.ScoreRecord
import com.example.hw1.logic.ScoreManager
import com.google.android.material.textview.MaterialTextView




class MainActivity : AppCompatActivity() {
    private lateinit var main_IMG_obstacles : Array<AppCompatImageView>
    private lateinit var main_IMG_charachter : AppCompatImageView
    private lateinit var main_IMG_hearts : Array<AppCompatImageView>
    private lateinit var main_BTN_left : AppCompatImageButton
    private lateinit var main_BTN_right : AppCompatImageButton
    private lateinit var gameManager: GameManager
    private lateinit var main_startOver: LinearLayout
    private lateinit var main_TXT_coins : MaterialTextView
    private lateinit var main_BTN_menu : AppCompatButton
    private lateinit var mainLayout : RelativeLayout
    private lateinit var main_IMG_coins : Array<AppCompatImageView>
    private lateinit var main_TXT_odometer : MaterialTextView
    private var crashSound : MediaPlayer? = null
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var isSensorMode = false
    private var lastMoveTime : Long = 0
    private lateinit var fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient

    private var hitZone : Int = 0
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var obstacleSpeed : Int = 10
    private var CoinSpeed : Int = 30
    private var isCrashed = false

    private var tickCounter = 0
    val gameRunnable : Runnable = object: Runnable {
        override fun run() {
            tickCounter++
            if(tickCounter%60==0)
            {
                gameManager.spawnNewObstacle()
                gameManager.spawnNewCoin()
            }
            if(tickCounter%5 == 0)
            {
                gameManager.distance++
                main_TXT_odometer.text = String.format("%04dm",gameManager.distance)
            }
            isCrashed = gameManager.moveObstacleDown(obstacleSpeed,mainLayout.height,hitZone,main_IMG_charachter.height)
            gameManager.moveCoinsDown(CoinSpeed,mainLayout.height,hitZone,main_IMG_charachter.height)
            if(isCrashed)
            {
                vibrate()
                crashSound?.start()
                main_IMG_hearts[main_IMG_hearts.size-gameManager.crashCount].visibility = View.INVISIBLE

                if(gameManager.isGameEnded)
                {
                    endGame()
                    return
                }
                Toast.makeText(this@MainActivity, "crashed", Toast.LENGTH_SHORT).show()

            }
            updateObstacleUI()
            updateCoinUI()
            handler.postDelayed(this,20)
        }

        private fun vibrate(){
            val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
            {
                val oneShotVibrationEffect = VibrationEffect.createOneShot(
                    500,
                    VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(oneShotVibrationEffect)
            }
            else
            {
                @Suppress("DEPRECATION")
                vibrator.vibrate(500)
            }
        }

        private fun endGame() {
            handler.removeCallbacks(gameRunnable)
            crashSound?.release()
            crashSound = null
            addRecord()
            main_startOver.visibility = View.VISIBLE
            main_TXT_coins.text = "coins:"+ gameManager.coinsCount
            main_BTN_menu.setOnClickListener {
                val intent = Intent(this@MainActivity, MenuActivity::class.java)
                startActivity(intent)
                finish()
            }
            gameManager.restartGame()
            /*
            handler.postDelayed({
           recreate()},1500)*/
        }

        private fun addRecord() {
            val finalScore = gameManager.distance
            if(androidx.core.app.ActivityCompat.checkSelfPermission(this@MainActivity,android.Manifest.permission.ACCESS_FINE_LOCATION)==android.content.pm.PackageManager.PERMISSION_GRANTED)
            {
                fusedLocationClient.lastLocation.addOnSuccessListener { location:android.location.Location?->
                    if(location!=null)
                        saveScore(location.latitude,location.longitude,finalScore)
                    else
                    {
                        saveScore(32.0853,34.7818,finalScore)
                    }
                }.addOnFailureListener{
                    saveScore(32.0853,34.7818,finalScore)
                }
            }
            else
            {
                saveScore(32.0853,34.7818,finalScore)
            }

        }
        private fun saveScore(lat: Double,lon: Double, score: Int) {
            val scoreManager = ScoreManager(this@MainActivity)
            //val currentDate = "20/06/2026"
            val currentDate = android.text.format.DateFormat.format("dd/MM/yyyy HH:mm", System.currentTimeMillis()).toString()
            val newRecord = ScoreRecord(
                score = score,
                lat = lat,
                lon = lon,
                date = currentDate
            )
            scoreManager.addScore(newRecord)


        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        crashSound = MediaPlayer.create(this,R.raw.crash_sound)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViews()
        gameManager = GameManager(main_IMG_hearts.size)
        isSensorMode = intent.getBooleanExtra("sensorMode",false)
        if(isSensorMode)
        {
            main_BTN_left.visibility = View.INVISIBLE
            main_BTN_right.visibility = View.INVISIBLE
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            refreshUI()
        }
        else
        {
            initViews()
        }
        val isFast  = intent.getBooleanExtra("FastMode",false)
        if(isFast)
            obstacleSpeed = 30

        else
            obstacleSpeed = 10

        mainLayout.post {
            hitZone = mainLayout.height - main_IMG_charachter.height - main_IMG_obstacles[0].height - 50
            startGame()
        }
        fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this)
    }
    private val sensorEventListener = object : SensorEventListener{
        override fun onSensorChanged(p0: SensorEvent?) {
           if(p0 !=null && !isCrashed){
               val x = p0.values[0]
               val currentTime = System.currentTimeMillis()
               if(currentTime-lastMoveTime>500)
               {
                   if(x<-2.5f)
                   {
                       move(Direction.RIGHT)
                       lastMoveTime = currentTime
                   }
                   else if(x>2.5f)
                   {
                       move(Direction.LEFT)
                       lastMoveTime = currentTime
                   }
               }

           }
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        }
    }


    private fun initViews() {
        main_BTN_left.setOnClickListener {move(Direction.LEFT)}
        main_BTN_right.setOnClickListener { move(Direction.RIGHT) }
        refreshUI()
    }

    private fun findViews() {
        main_IMG_charachter = findViewById(R.id.main_IMG_charachter)
        main_BTN_right = findViewById(R.id.main_BTN_right)
        main_BTN_left = findViewById(R.id.main_BTN_left)
        main_IMG_obstacles = arrayOf(
            findViewById(R.id.main_IMG_obstacle0),
            findViewById(R.id.main_IMG_obstacle1),
            findViewById(R.id.main_IMG_obstacle2),
            findViewById(R.id.main_IMG_obstacle3),
            findViewById(R.id.main_IMG_obstacle4)
        )
        main_IMG_hearts = arrayOf(
            findViewById(R.id.main_IMG_heart0),
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2)
        )
        main_IMG_coins = arrayOf(
            findViewById(R.id.main_IMG_coin0),
            findViewById(R.id.main_IMG_coin1),
            findViewById(R.id.main_IMG_coin2),
            findViewById(R.id.main_IMG_coin3),
            findViewById(R.id.main_IMG_coin4)
        )
        main_startOver = findViewById(R.id.main_startOver)
        mainLayout = findViewById(R.id.main)
        main_BTN_menu = findViewById(R.id.main_BTN_menu)
        main_TXT_coins = findViewById(R.id.main_TXT_coins)
        main_TXT_odometer = findViewById(R.id.main_TXT_odometer)

    }
    private fun move(direction: Direction) {
        if (direction == Direction.LEFT)
        {
            gameManager.moveLeft()
            refreshUI()
        }
        if(direction == Direction.RIGHT)
        {
            gameManager.moveRight()
            refreshUI()
        }
        refreshUI()

    }

    private fun refreshUI() {
        //val params = main_IMG_charachter.layoutParams as FrameLayout.LayoutParams
        val newBias = gameManager.currentLane*0.25f
        val params = main_IMG_charachter.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
        params.horizontalBias = newBias
        main_IMG_charachter.layoutParams = params
/*
        if(gameManager.currentLane == 0)
        {
           params.gravity = Gravity.BOTTOM or Gravity.START
        }
        if(gameManager.currentLane == 1)
        {
            params.gravity = Gravity.BOTTOM or Gravity.CENTER
        }
        if(gameManager.currentLane == 2)
        {
            params.gravity = Gravity.BOTTOM or Gravity.END

        }

 */
       // main_IMG_charachter.layoutParams=params
    }
    private fun updateObstacleUI() {
        //val curLane = gameManager.obstacleLane
        for(i in main_IMG_obstacles.indices)
        {
            val params = main_IMG_obstacles[i].layoutParams as ViewGroup.MarginLayoutParams
            if(gameManager.isObstacleActive[i])
            {
                main_IMG_obstacles[i].visibility = View.VISIBLE
                params.topMargin = gameManager.obstacleMargins[i]
            }
            else
            {
                main_IMG_obstacles[i].visibility = View.INVISIBLE
                params.topMargin = 0
            }
            main_IMG_obstacles[i].layoutParams = params
        }
    }
    private fun updateCoinUI()
    {
        for(i in main_IMG_coins.indices)
        {
            val params = main_IMG_coins[i].layoutParams as ViewGroup.MarginLayoutParams
            if(gameManager.isCoinActive[i])
            {
                main_IMG_coins[i].visibility = View.VISIBLE
                params.topMargin = gameManager.coinsMargins[i]
            }
            else
            {
                main_IMG_coins[i].visibility = View.INVISIBLE
                params.topMargin = 0
            }
            main_IMG_coins[i].layoutParams = params
        }
    }

    fun startGame()
    {
        main_IMG_obstacles[1].visibility = View.VISIBLE
        handler.post(gameRunnable)
    }

    override fun onResume() {
        super.onResume()
        if(isSensorMode){
            accelerometer?.let{
                sensorManager.registerListener(sensorEventListener,it, SensorManager.SENSOR_DELAY_GAME)
            }

        }
    }

    override fun onPause() {
        super.onPause()
        if(isSensorMode)
            sensorManager.unregisterListener(sensorEventListener)
    }






}