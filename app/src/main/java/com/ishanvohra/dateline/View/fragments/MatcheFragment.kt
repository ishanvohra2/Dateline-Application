package com.ishanvohra.dateline.View.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ishanvohra.dateline.R

class MatcheFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        return inflater.inflate(R.layout.fragment_matche, container, false)
    }

    companion object {
        fun newInstance(): MatcheFragment = MatcheFragment()
    }

}
