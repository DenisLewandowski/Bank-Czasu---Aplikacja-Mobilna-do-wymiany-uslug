package pl.denislewandowski.bankczasu.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import pl.denislewandowski.bankczasu.fragments.TimebankMembersFragment;
import pl.denislewandowski.bankczasu.fragments.TimebankServicesFragment;
import pl.denislewandowski.bankczasu.fragments.MyServicesFragment;

public class ServicesFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] tabNames = {"Oferty", "Moje oferty", "Członkowie"};

    public ServicesFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TimebankServicesFragment();
            case 1:
                return new MyServicesFragment();
            case 2:
                return new TimebankMembersFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabNames.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabNames[position];
    }
}
