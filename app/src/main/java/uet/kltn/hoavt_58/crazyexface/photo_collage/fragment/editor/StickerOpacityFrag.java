package uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.editor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnMenuMarginListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnOpacityStickerListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.BaseFragment;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;

/**
 * Created by vutha on 9/15/2016.
 */
public class StickerOpacityFrag extends BaseFragment {
    public static final String OPACITY_KEY = "OPACITY";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sticker_opacity_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initStickerEditor();
    }

    private void initStickerEditor() {
        DiscreteSeekBar opacitySeek = (DiscreteSeekBar) getView().findViewById(R.id.sb_opacity_sticker);
        int maxProgress = 255 - Statistic.MIN_OPACITY;
        int curRoundVal = getArguments().getInt(OPACITY_KEY, 255);
        opacitySeek.setMax(maxProgress);
        opacitySeek.setProgress(curRoundVal);

        opacitySeek.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                int id = seekBar.getId();
                switch (id) {
                    case R.id.sb_opacity_sticker:
                        if (getActivity() instanceof OnMenuMarginListener) {
                            ((OnOpacityStickerListener) getActivity()).onOpacityChangeListener(value);
                        }
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
    }
}

