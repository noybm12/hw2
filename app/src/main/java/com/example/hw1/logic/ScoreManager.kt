package com.example.hw1.logic

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.hw1.data.ScoreRecord
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ScoreManager(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("MyGamePreferences",
        Context.MODE_PRIVATE)
    private val gson = Gson()
    fun getTopScore():List<ScoreRecord>{
        val jsonString = preferences.getString("TopScores",null)
        return if (jsonString!=null)
        {
            val type = object : TypeToken<List<ScoreRecord>>(){}.type
            gson.fromJson(jsonString,type)
        } else {
            emptyList()
        }
    }

    fun addScore(newScore: ScoreRecord)
    {
        val curScores = getTopScore().toMutableList()
        curScores.add(newScore)
        curScores.sortByDescending { it.score }
        val topTen = curScores.take(10)
        val jsonString = gson.toJson(topTen)
        preferences.edit().putString("TopScores",jsonString).apply()
    }




}