package com.example.macintosh.recycleviewgroup

import android.os.Bundle
import android.support.v4.app.FragmentActivity

class MainActivity : FragmentActivity() {

    // Whether the Log Fragment is currently shown
    private var logShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().run {
                replace(R.id.sample_content_fragment, RecyclerViewFragment())
                commit()
            }
        }
    }

    companion object {
        val TAG = "MainActivity"
    }

}
