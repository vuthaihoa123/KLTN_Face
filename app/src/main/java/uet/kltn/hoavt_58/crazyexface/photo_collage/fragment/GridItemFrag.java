package uet.kltn.hoavt_58.crazyexface.photo_collage.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.OnItemLayoutListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.PageGridAdapter;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.PagePileAdapter;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.LayoutModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;

/**
 * Created by Adm on 8/3/2016.
 */
public class GridItemFrag extends BaseFragment {

    private RecyclerView recyclerView = null;
    private PageGridAdapter adapter = null;
    private int pageIndex = 0;
    private int layoutCode = Statistic.GRID_LAYOUT_CODE;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_page_grid_item, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pageIndex = getArguments().getInt("INDEX", pageIndex);
        layoutCode = getArguments().getInt("LAYOUT_CODE", layoutCode);

        initRecycleView();

    }

    private void initRecycleView() {

        List<LayoutModel> list = null;
        if (layoutCode == Statistic.GRID_LAYOUT_CODE) {
            list = Statistic.getGridListForPage(pageIndex);
            adapter = new PageGridAdapter(getActivity(), list);
        } else {
            list = Statistic.getPileListForPage(pageIndex);
            adapter = new PagePileAdapter(getActivity(), list);
        }

        if (getActivity() instanceof OnItemLayoutListener) {
            adapter.setOnItemLayoutListener((OnItemLayoutListener) getActivity());
        }


        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new RecycleViewSpaceItemDecoration(Statistic.ITEM_SPACE));
    }


    public class RecycleViewSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int mSpace;

        public RecycleViewSpaceItemDecoration(int mSpace) {
            this.mSpace = mSpace;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
//            outRect.bottom = mSpace;
//            outRect.left = mSpace;
//            outRect.right = mSpace;
            outRect.top = mSpace;
        }
    }
}
