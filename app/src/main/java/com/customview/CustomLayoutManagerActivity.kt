package com.customview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.customview.adapter.SampleAdapter
import com.customview.layoutmanager.SampleLayoutManager
import kotlinx.android.synthetic.main.activity_custom_layout_manager.*

class CustomLayoutManagerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_layout_manager)

        val sampleLayoutManager = SampleLayoutManager(this)
        recyclerView.layoutManager = sampleLayoutManager
        val sampleAdapter = SampleAdapter()
        recyclerView.adapter = sampleAdapter
    }
}
