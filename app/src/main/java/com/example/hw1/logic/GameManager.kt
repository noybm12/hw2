package com.example.hw1.logic

class GameManager(private val lifeCount: Int = 3) {
    var crashCount : Int = 0
        private set
    var coinsCount : Int = 0
    var currentLane : Int = 2
        private set
    var distance : Int = 0
    val isObstacleActive = booleanArrayOf(false, false, false,false,false)
    val isCoinActive = booleanArrayOf(false, false, false,false,false)

    val obstacleMargins = intArrayOf(0, 0, 0, 0, 0)
    val coinsMargins = intArrayOf(0,0,0,0,0)

    val isGameEnded : Boolean
        get() = crashCount == lifeCount

    fun restartGame(){
        crashCount = 0
        coinsCount = 0
        distance = 0
    }

    fun moveLeft()
    {
        if(currentLane>0)
            currentLane--
    }
    fun moveRight()
    {
        if(currentLane<4)
            currentLane++
    }
    fun moveObstacleDown(speed: Int ,screenHeight: Int, hitZone: Int,characterHight:Int): Boolean{
        var crashed = false
        val pass = hitZone+characterHight-100
        for(i in isObstacleActive.indices)
        {
            if(isObstacleActive[i])
            {
                obstacleMargins[i] += speed
                if(i==currentLane && obstacleMargins[i]>=hitZone && obstacleMargins[i]<= pass)
                {
                    crashed = true
                    crashCount++
                    isObstacleActive[i] = false
                    obstacleMargins[i] = 0
                }
                if(obstacleMargins[i]>=screenHeight)
                {
                    isObstacleActive[i] = false
                    obstacleMargins[i] = 0
                }
            }
        }
        return crashed
    }
    fun spawnNewObstacle() {
        val randomLane = (0..4).random()

        if (!isObstacleActive[randomLane]) {
            isObstacleActive[randomLane] = true
            obstacleMargins[randomLane] = -200
        }
    }
    fun spawnNewCoin() {
        val randomLane = (0..4).random()
        if(!isCoinActive[randomLane]){
            isCoinActive[randomLane]=true
            coinsMargins[randomLane] = -200
        }
    }

    fun moveCoinsDown(speed:Int,screenHeight: Int,hitZone: Int,characterHight: Int){
        val pass = hitZone+characterHight-100
        for(i in isCoinActive.indices)
        {
            if(isCoinActive[i])
            {
                coinsMargins[i]+= speed
                if(i==currentLane && coinsMargins[i]>=hitZone && coinsMargins[i]<=pass)
                {
                    coinsCount++
                    isCoinActive[i] = false
                    coinsMargins[i] = 0
                }
                if(coinsMargins[i]>=screenHeight)
                {
                    isCoinActive[i] = false
                    coinsMargins[i] = 0
                }
            }
        }
    }




}