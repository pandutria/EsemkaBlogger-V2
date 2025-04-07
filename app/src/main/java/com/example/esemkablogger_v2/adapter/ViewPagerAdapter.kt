package com.example.esemkablogger_v2.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.esemkablogger_v2.ui.fragment.PostsFragment
import com.example.esemkablogger_v2.ui.fragment.ProfileFragment
import com.example.esemkablogger_v2.ui.fragment.UsersFragment

class ViewPagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {
    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> PostsFragment()
            1 -> UsersFragment()
            2 -> ProfileFragment()
            else -> Fragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position) {
            0 -> "POST"
            1 -> "USERS"
            2 -> "PROFILE"
            else -> ""
        }
    }
}