package pl.denislewandowski.bankczasu;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ServicesFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] tabNames = {"Oferuję", "Potrzebuję"};

    public ServicesFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ServicesNeededFragment();
            case 1:
                return new ServicesOfferedFragment();

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
