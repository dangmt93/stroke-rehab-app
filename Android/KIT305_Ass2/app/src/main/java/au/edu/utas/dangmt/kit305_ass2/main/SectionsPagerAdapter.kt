package au.edu.utas.dangmt.kit305_ass2.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import au.edu.utas.dangmt.kit305_ass2.*
import au.edu.utas.dangmt.kit305_ass2.exercises.ExercisesFragment
import au.edu.utas.dangmt.kit305_ass2.history.HistoryFragment
import au.edu.utas.dangmt.kit305_ass2.settings.SettingsFragment
import au.edu.utas.dangmt.kit305_ass2.statistics.StatisticsFragment


val TAB_TITLES = arrayOf(
    R.string.tab_text_exercises,
    R.string.tab_text_statistics,
    R.string.tab_text_history,
    R.string.tab_text_settings,
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return when (position)
        {
            0 -> ExercisesFragment()
            1 -> StatisticsFragment()
            2 -> HistoryFragment()
            3 -> SettingsFragment()
            else -> ExercisesFragment() //or something else here
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show total pages.
        return TAB_TITLES.size
    }
}