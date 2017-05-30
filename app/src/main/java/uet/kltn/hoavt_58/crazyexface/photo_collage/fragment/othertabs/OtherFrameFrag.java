package uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.othertabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnCloseFragListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnMenuFrameListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.menu.MenuFrameAdapter;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.menu.MenuBackgroundFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;
import uet.kltn.hoavt_58.crazyexface.photo_collage.utils.FLog;

/**
 * Created by vutha on 2/17/2017.
 */

public class OtherFrameFrag extends OtherBaseFrag {

    private static final String TAG = MenuBackgroundFrag.class.getSimpleName();
    private RecyclerView recyclerView = null;
    private MenuFrameAdapter adapter = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_collage_menu_frame_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecycleView();

        getView().findViewById(R.id.iv_close_option).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("onCloseCallback", "onCloseCallback");
                if (getActivity() instanceof OnCloseFragListener) {
                    ((OnCloseFragListener)getActivity()).onCloseCallback(OtherBaseFrag.TAB_FRAME_INDEX);
                }
            }
        });
    }

    private void initRecycleView() {
        adapter = new MenuFrameAdapter(getActivity(), Statistic.FRAME_LIST)
                .setOnMenuFrameListener((OnMenuFrameListener) getActivity());
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycle_view);

//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), Statistic.NUM_GRID_COLS));

        recyclerView.setAdapter(adapter);
        FLog.d(TAG, "MENU_COLLAGE_TYPE_BG");
    }
}
