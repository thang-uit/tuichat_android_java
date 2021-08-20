package com.thanguit.tuichat.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.thanguit.tuichat.fragments.ChatFragment;
import com.thanguit.tuichat.fragments.PeopleFragment;
import com.thanguit.tuichat.fragments.SettingFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: {
                return new ChatFragment();
            }
            case 1: {
                return new PeopleFragment();
            }
            case 2: {
                return new SettingFragment();
            }
            default: {
                return new ChatFragment();
            }
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
