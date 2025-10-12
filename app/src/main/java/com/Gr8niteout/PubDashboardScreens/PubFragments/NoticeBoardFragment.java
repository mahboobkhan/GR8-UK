package com.Gr8niteout.PubDashboardScreens.PubFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.Gr8niteout.PubDashboardScreens.PubFragments.NoticeBoardFragments.AllFragment;
import com.Gr8niteout.PubDashboardScreens.PubFragments.NoticeBoardFragments.DeleteFragment;
import com.Gr8niteout.PubDashboardScreens.adapters.NoticeBoardViewPagerAdapter;
import com.Gr8niteout.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class NoticeBoardFragment extends Fragment {

    public static Boolean refreshAllFragment = false;
    public static Boolean refreshDeleteFragment = false;

    TabLayout tabLayout;
    ViewPager2 viewPager;
    NoticeBoardViewPagerAdapter noticeBoardViewPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice_board, container, false);
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        if(getActivity() != null) {
            noticeBoardViewPagerAdapter = new NoticeBoardViewPagerAdapter(getActivity().getSupportFragmentManager(), getLifecycle());
        }
        noticeBoardViewPagerAdapter.addFragment(new AllFragment(tabLayout));
        noticeBoardViewPagerAdapter.addFragment(new DeleteFragment(tabLayout));

        viewPager.setAdapter(noticeBoardViewPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy(){
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0: tab.setText("ALL");
                        break;
                    case 1: tab.setText("DELETE");
                        break;
                }
            }
        }).attach();
        return view;
    }
}