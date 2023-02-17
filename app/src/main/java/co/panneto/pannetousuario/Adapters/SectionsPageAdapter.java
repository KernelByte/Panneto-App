package co.panneto.pannetousuario.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import co.panneto.pannetousuario.Fragments.MapsFragment;
import co.panneto.pannetousuario.Fragments.NotificationsFragment;
import co.panneto.pannetousuario.Fragments.ReservationsFragment;

public class SectionsPageAdapter extends FragmentStatePagerAdapter {

    private int tabs;

    public SectionsPageAdapter(FragmentManager fm, int numberTabs) {
        super(fm);
        this.tabs = numberTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                MapsFragment map = new MapsFragment();
                return map;
            case 1:
                NotificationsFragment notifications = new NotificationsFragment();
                return notifications;
            case 2:
                ReservationsFragment reservations = new ReservationsFragment();
                return reservations;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabs;
    }

}
