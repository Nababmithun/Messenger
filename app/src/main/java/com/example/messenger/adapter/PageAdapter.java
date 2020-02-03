package com.example.messenger.adapter;



import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.messenger.fragment.ChatFragment;
import com.example.messenger.fragment.ContactsFragment;
import com.example.messenger.fragment.GroupsFragment;
import com.example.messenger.fragment.RequestsFragment;


public class PageAdapter extends FragmentPagerAdapter {

    private int numOfTabs;
    public PageAdapter(@NonNull FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ChatFragment();
            case 1:

                return new ContactsFragment();

            //return new GroupsFragment();
            case 2:

                return new RequestsFragment();

            //return new ContactsFragment();

           /* case 3:
                return new RequestsFragment();*/
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
