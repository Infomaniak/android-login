package com.infomaniak.login.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class FragmentMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, FragmentMainFragment.newInstance())
                .commitNow()
        }
    }
}