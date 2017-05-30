package uet.kltn.hoavt_58.crazyexface.photo_collage.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.List;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.ItemMenuAdapter;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnItemMenuListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.LayoutModel;

/**
 * Created by Adm on 8/3/2016.
 */
public abstract class CollageMenuDetailFrag extends BaseFragment implements OnItemMenuListener {

    protected RecyclerView submenuRecycleView;
    protected RecyclerView mmenuRecycleView;

    protected ItemMenuAdapter subAdapter, mAdapter;

    protected List<LayoutModel> mainList = null;
    protected List<LayoutModel> subList = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_collage_menu_detail_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList(); // pass list to view
        initRecycleView();

    }

    public abstract void initList();

    private void initRecycleView() {
        subAdapter = new ItemMenuAdapter(getActivity(), false, subList).setOnItemMenuListener(this);
        mAdapter = new ItemMenuAdapter(getActivity(), true, mainList).setCurrentLayoutModel(model).setOnItemMenuListener(this);


        submenuRecycleView = (RecyclerView) getView().findViewById(R.id.submenu);
        mmenuRecycleView = (RecyclerView) getView().findViewById(R.id.m_menu);


        submenuRecycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//        submenuRecycleView.setLayoutManager(new GridLayoutManager(getActivity(), 7));

        mmenuRecycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        submenuRecycleView.setAdapter(subAdapter);
        mmenuRecycleView.setAdapter(mAdapter);
    }
}
