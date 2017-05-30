package uet.kltn.hoavt_58.crazyexface.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.activities.GalleryActivity;
import uet.kltn.hoavt_58.crazyexface.adapters.GalleryFooterAdapter;
import uet.kltn.hoavt_58.crazyexface.helpers.ConstValue;

/**
 * Created by Adm on 8/10/2016.
 */
public class GalleryFooterFrag extends Fragment implements View.OnClickListener {

    private GalleryFooterAdapter imgAdapter;
    private RecyclerView recyclerView;
    private TextView hintText;
    private int curIndex = 0;

    public GalleryFooterAdapter getImgAdapter() {
        return imgAdapter;
    }

    public View getCurrentTargetView() {
        return recyclerView.getLayoutManager().findViewByPosition(imgAdapter.getCurIndex());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_gallery_footer_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    private void initViews() {
        hintText = (TextView) getView().findViewById(R.id.gallery_text_hint);
        View nextButton = getView().findViewById(R.id.next_btn);
        nextButton.setOnClickListener(this);

        final int paddingRight = getResources().getDimensionPixelOffset(R.dimen.xx_large);
        if (getActivity().getIntent() != null)
            curIndex = getActivity().getIntent().getIntExtra(ConstValue.EXTRA_CURRENT_SELECTED_RECT, 0);
        imgAdapter = new GalleryFooterAdapter(getActivity());
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycle_view);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.right = paddingRight;
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(imgAdapter);
        scrollToCurPosition();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.next_btn:
                Intent data = new Intent();
                data.putExtra(GalleryActivity.EXTRA_OUTPUT_PATH_KEY, imgAdapter.getOutput());
//                data.putExtra(Statistic.EXTRA_CURRENT_SELECTED_RECT, curIndex);
//                data.putParcelableArrayListExtra(GalleryActivity.EXTRA_PHOTO_MODEL_LIST, imgAdapter.getChoosedModelList());
                getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().finish();
                break;
        }
    }

    public void scrollToCurPosition() {
        recyclerView.getLayoutManager().scrollToPosition(imgAdapter.getCurIndex());
    }
}
