package com.example.hw1.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.hw1.interfaces.CallBack_HighScoreCliked
import com.example.hw1.R
import com.example.hw1.data.ScoreRecord
import com.example.hw1.logic.ScoreAdapter
import com.example.hw1.logic.ScoreManager

class ScoreListFragment : Fragment(){
    private var callback : CallBack_HighScoreCliked? = null
    private lateinit var recyclerView: RecyclerView
    fun setCallback(callback: CallBack_HighScoreCliked){
        this.callback = callback
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_score_list,container,false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scoreManager = ScoreManager(requireContext())
        val topScores:List<ScoreRecord> = scoreManager.getTopScore()
        Toast.makeText(requireContext(), "There are ${topScores.size} scores saved!", Toast.LENGTH_LONG).show()
        recyclerView = view.findViewById(R.id.score_LST_records)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        val adapter = ScoreAdapter(topScores,callback)
        recyclerView.adapter = adapter
    }
}