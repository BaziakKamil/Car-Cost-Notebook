package pl.kamilbaziak.carcostnotebook.ui.cardetails

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val list = mutableListOf<Fragment>()

    override fun getItemCount() = list.size

    override fun createFragment(position: Int): Fragment = list[position]

    fun addFragment(fragment: Fragment) {
        list.add(fragment)
    }
}
