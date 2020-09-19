package com.ishanvohra.dateline.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import com.ishanvohra.dateline.R
import com.ishanvohra.dateline.adapters.DoubleTakeAdapter
import com.ishanvohra.dateline.data.User
import com.yuyakaido.android.cardstackview.*
import kotlinx.android.synthetic.main.fragment_swipe.*

class SwipeFragment : Fragment(), CardStackListener {

    private lateinit var recyclerView: CardStackView

    private val adapter: DoubleTakeAdapter = DoubleTakeAdapter()
    private lateinit var layoutManager: CardStackLayoutManager

    private lateinit var database: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_swipe, container, false)

        recyclerView = view.findViewById(R.id.stack_view)

        database = FirebaseDatabase.getInstance().reference

        layoutManager = CardStackLayoutManager(context, this).apply {
            setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
            setOverlayInterpolator(LinearInterpolator())
        }

        adapter.context = context
        adapter.profiles = ArrayList()

        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        database.child("Users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var profiles:ArrayList<User> = ArrayList()

                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    if(!user!!.uid.equals(FirebaseAuth.getInstance().currentUser?.uid))
                        profiles.add(user)
                }
                adapter.profiles = profiles
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        return view
    }

    companion object {
        fun newInstance(): SwipeFragment = SwipeFragment()
    }

    override fun onCardDisappeared(view: View?, position: Int) {

    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {

    }

    override fun onCardSwiped(direction: Direction?) {

    }

    override fun onCardCanceled() {

    }

    override fun onCardAppeared(view: View?, position: Int) {

    }

    override fun onCardRewound() {

    }
}
