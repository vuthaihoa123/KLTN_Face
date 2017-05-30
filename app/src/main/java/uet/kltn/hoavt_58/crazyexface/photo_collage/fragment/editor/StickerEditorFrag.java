package uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.editor;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.MainEditorActivity;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.StickerEditorAdapter;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.BaseFragment;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;

/**
 * Created by Adm on 10/19/2016.
 */
public class StickerEditorFrag extends BaseFragment {
    private static final String TAG = StickerEditorFrag.class.getSimpleName();

    private RecyclerView recyclerView = null;
    private StickerEditorAdapter adapter = null;
    private int mCurIndex = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sticker_editor_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecycleView();

    }

    private void initRecycleView() {
        adapter = new StickerEditorAdapter(getActivity()).setOnStickerEditorListener((MainEditorActivity) getActivity());
        adapter.setCurIndex(mCurIndex);

        recyclerView = (RecyclerView) getView().findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new RecycleViewSpaceItemDecoration(Statistic.ITEM_MENU_COLLAGE_SPACE));

        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                ((MainEditorActivity) getActivity()).onLayoutReady();
            }
        });
    }

    public StickerEditorFrag setCurIndex(int curIndex) {
        mCurIndex = curIndex;
        return this;
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
            outRect.left = (int) (1.5 * mSpace);
//            outRect.right = (int) (1.5 * mSpace);
//            outRect.top = mSpace;
        }
    }
}
