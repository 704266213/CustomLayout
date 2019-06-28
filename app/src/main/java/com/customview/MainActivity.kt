package com.customview

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }


    fun goToMyViewGroup(view: View) {
        val intent = Intent(this, MyViewGroupActivity::class.java)
        startActivity(intent)
    }

    fun goToFlowLayout(view: View) {
        val intent = Intent(this, FlowLayoutActivity::class.java)
        startActivity(intent)
    }

    fun goToLayoutManager(view: View) {
        val intent = Intent(this, CustomLayoutManagerActivity::class.java)
        startActivity(intent)
    }

}
