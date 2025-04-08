package com.example.esemkablogger_v2.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.esemkablogger_v2.ui.fragment.profileTab.MyLikeFragment
import com.example.esemkablogger_v2.ui.fragment.profileTab.MyPostFragment

class ViewPagerAdapter2(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> MyPostFragment()
            1 -> MyLikeFragment()
            else -> Fragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "MY POST"
            1 -> "LIKED POST"
            else -> ""
        }

    }
}