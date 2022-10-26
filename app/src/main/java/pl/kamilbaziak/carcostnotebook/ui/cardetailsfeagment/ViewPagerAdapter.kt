package pl.kamilbaziak.carcostnotebook.ui.cardetailsfeagment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val list = mutableListOf<Triple<Fragment, String, Int>>()

    override fun getItemCount() = list.size

    override fun createFragment(position: Int): Fragment = list[position].first

    fun addFragment(fragment: Fragment, title: String, drawableResId: Int) {
        list.add(Triple(fragment, title, drawableResId))
    }

    fun getPageTitle(position: Int): CharSequence {
        return list[position].second
    }

    fun getTabDrawable(position: Int): Int {
        return list[position].third
    }
}
