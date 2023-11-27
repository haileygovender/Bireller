package com.example.birellerapp.authentication

import LoginFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class AuthenticationAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun createFragment(position: Int): Fragment {
        return if (position == 1) {
            RegisterFragment()
        } else {
            LoginFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}