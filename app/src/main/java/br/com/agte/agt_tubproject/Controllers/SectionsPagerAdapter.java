package br.com.agte.agt_tubproject.Controllers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import br.com.agte.agt_tubproject.Fragments.AddFragments.AddControlFragment;
import br.com.agte.agt_tubproject.Fragments.TubFragments.TubControlFragment;
import br.com.agte.agt_tubproject.Fragments.WifiFragments.WifiControlFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position){
            case 0: return new TubControlFragment();
            case 1: return new WifiControlFragment();
            case 2: return new AddControlFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 3;
    }
}