package com.airplayer.fragment.singleItem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;
import com.airplayer.adapter.AirScrollListener;
import com.airplayer.fragment.itf.SettableRecyclerView;

/**
 * Created by ZiyiTsang on 15/6/14.
 */
public abstract class SingleItemChildFragment extends Fragment implements SettableRecyclerView {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AirMainActivity) getActivity()).getToolbar().setVisibility(View.INVISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_toolbar_recycler, container, false);

        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.hideable_toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        this.setUpRecyclerView(recyclerView);
        AirScrollListener listener = new AirScrollListener(getResources().getInteger(R.integer.padding_action_bar)) {

            @Override
            public void onHide() {
                toolbar.setBackgroundColor(getResources().getColor(R.color.air_dark_primary_color));
                toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(1));
            }

            @Override
            public void onShow() {
                toolbar.setBackgroundColor(getResources().getColor(R.color.air_dark_primary_color));
                toolbar.animate().translationY(0).setInterpolator(new AccelerateInterpolator(1));
            }

            @Override
            public void onViewScrolled(int viewScrolledDistance) {
                if (viewScrolledDistance >= toolbar.getHeight()) {
                    toolbar.setBackgroundColor(getResources().getColor(R.color.air_dark_primary_color));
                }
                toolbar.setTranslationY(-viewScrolledDistance);
            }

            @Override
            public void onScrollBackToTop() {
                toolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            }
        };
        listener.setHaveParallax(true);
        recyclerView.setOnScrollListener(listener);


        return rootView;
    }
}
