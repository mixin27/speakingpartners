package com.team29.speakingpartners.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.team29.speakingpartners.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecentFragment extends Fragment {

    public static final String TAG = RecentFragment.class.getSimpleName();

    TabLayout tabLayout;
    private ViewPager viewPager;

    Fragment incomingFragment;
    Fragment outgoingFragment;

    public RecentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_recent, container, false);

        tabLayout = root.findViewById(R.id.recent_tab_layout);
        setUpTabLayout();
        setCurrentTabFragment(0);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setCurrentTabFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return root;
    }

    private void setCurrentTabFragment(int position) {
        switch (position) {
            case 0:
                replaceFragment(incomingFragment);
                break;
            case 1:
                replaceFragment(outgoingFragment);
                break;
        }
    }

    private void setUpTabLayout() {
        incomingFragment = new IncomingRecentFragment();
        outgoingFragment = new OutgoingRecentFragment();

        tabLayout.addTab(tabLayout.newTab().setText("Incoming"));
        tabLayout.addTab(tabLayout.newTab().setText("Outgoing"));
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.recent_tab_container, fragment);
        /*ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);*/
        ft.commit();
    }
}
