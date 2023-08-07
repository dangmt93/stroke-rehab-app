package au.edu.utas.dangmt.kit305_ass2.main

import android.os.Bundle
import android.util.Log
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import au.edu.utas.dangmt.kit305_ass2.databinding.ActivityMainBinding

const val TAB_POSITION_KEY = "TAB_POSITION"

class MainActivity : AppCompatActivity() {

    private lateinit var ui: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityMainBinding.inflate(layoutInflater)
        setContentView(ui.root)

        // Set viewPager adapter to created adapter
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = ui.viewPager
        viewPager.adapter = sectionsPagerAdapter

        val tabs: TabLayout = ui.tabs
        tabs.setupWithViewPager(viewPager)

        Log.d("PAGER", sectionsPagerAdapter.getItem(0).toString())
        //Log.d("PAGER", sectionsPagerAdapter.getPageTitle(0).toString())

        val tabPosition = intent.getIntExtra(TAB_POSITION_KEY, 0)

        Log.d("TAB_POSITION", tabPosition.toString())
        viewPager.currentItem = tabPosition

        val exerciseTab = tabs.getTabAt(0)
//        exerciseTab?.icon = R.drawable.ic_baseline_touch_app_24
    }
}