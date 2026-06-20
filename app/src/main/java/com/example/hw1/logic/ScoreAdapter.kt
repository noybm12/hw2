package com.example.hw1.logic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hw1.R
import com.example.hw1.data.ScoreRecord
import com.example.hw1.interfaces.CallBack_HighScoreCliked

class ScoreAdapter(
    private val scores: List<ScoreRecord>,
    private val callback: CallBack_HighScoreCliked?
): RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>()
{
    class ScoreViewHolder(view: View): RecyclerView.ViewHolder(view)
    {
        val rankText : TextView = view.findViewById(R.id.row_TXT_rank)
        val scoreText : TextView = view.findViewById(R.id.row_TXT_score)
        val dateText : TextView = view.findViewById(R.id.row_TXT_date)
        val locationText : TextView = view.findViewById(R.id.row_TXT_location)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ScoreViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.score_list_item,p0,false)
        return ScoreViewHolder(view)
    }

    override fun getItemCount(): Int {
        return scores.size
    }

    override fun onBindViewHolder(p0: ScoreViewHolder, p1: Int) {
        val curScore = scores[p1]
        p0.rankText.text = "${p1+1}"
        p0.scoreText.text = "Score: ${curScore.score}"
        p0.dateText.text = curScore.date
        p0.locationText.text = String.format("Lat:%.3f,Lon: %.3f", curScore.lat,curScore.lon)
        p0.itemView.setOnClickListener {
            callback?.highScoreItemClicked(curScore.lat, curScore.lon)
        }
    }

}