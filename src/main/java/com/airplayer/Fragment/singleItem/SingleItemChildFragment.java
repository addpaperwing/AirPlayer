package com.airplayer.fragment.singleItem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;
import com.airplayer.listener.AirScrollListener;
import com.airplayer.fragment.singleItem.itf.SettableRecyclerView;
import com.airplayer.listener.SimpleAirScrollListener;

/**
 * Created by ZiyiTsang on 15/6/14.
 */
public abstract class SingleItemChildFragment extends Fragment implements SettableRecyclerView {

    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AirMainActivity) getActivity()).getToolbar().setVisibility(View.INVISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_suppressible_toolbar, container, false);

        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.suppressible_toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        this.setupRecyclerView(mRecyclerView);
        AirScrollListener listener = new SimpleAirScrollListener(getResources().getInteger(R.integer.padding_action_bar), toolbar) {

            @Override
            public void onHide() {
                super.onHide();
                toolbar.setBackgroundColor(getResources().getColor(R.color.air_dark_primary_color));
            }

            @Override
            public void onShow() {
                super.onShow();
                toolbar.setBackgroundColor(getResources().getColor(R.color.air_dark_primary_color));
            }

            @Override
            public void onViewScrolled(int viewScrolledDistance) {
                super.onViewScrolled(viewScrolledDistance);
                if (viewScrolledDistance >= toolbar.getHeight()) {
                    toolbar.setBackgroundColor(getResources().getColor(R.color.air_dark_primary_color));
                }
            }

            @Override
            public void onScrollBackToTop() {
                toolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            }
        };
        listener.setHaveParallax(true);
        mRecyclerView.setOnScrollListener(listener);


        return rootView;
    }
}
