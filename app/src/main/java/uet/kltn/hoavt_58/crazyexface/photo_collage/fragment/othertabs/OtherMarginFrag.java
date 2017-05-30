package uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.othertabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pic.libphotocollage.core.pref.LibPref;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnCloseFragListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnMenuMarginListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.CollageContainerFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;

/**
 * Created by vutha on 2/17/2017.
 */

public class OtherMarginFrag extends OtherBaseFrag implements View.OnClickListener, DiscreteSeekBar.OnProgressChangeListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_collage_menu_margin_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initButtonMargin();

        getView().findViewById(R.id.iv_close_option).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("onCloseCallback", "onCloseCallback");
                if (getActivity() instanceof OnCloseFragListener) {
                    ((OnCloseFragListener) getActivity()).onCloseCallback(OtherBaseFrag.TAB_MARGIN_INDEX);
                }
            }
        });
    }

    private void initButtonMargin() {
        DiscreteSeekBar marginSeek = (DiscreteSeekBar) getView().findViewById(R.id.margin_seek);
        DiscreteSeekBar roundSeek = (DiscreteSeekBar) getView().findViewById(R.id.round_seek);
        int curRoundVal = LibPref.getInstance().getInt(LibPref.KEY_ROUND_SIZE, 0);
        roundSeek.setProgress(curRoundVal);
        roundSeek.setMax(Statistic.MAX_ROUND_STEPS);
        int curMarginVal = LibPref.getInstance().getInt(LibPref.KEY_MARGIN_SIZE, CollageContainerFrag.INIT_STEP);
        marginSeek.setProgress(curMarginVal);
        marginSeek.setMax(Statistic.MAX_SPACING_STEPS);

        marginSeek.setOnProgressChangeListener(this);
        roundSeek.setOnProgressChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        float ratio = 1.f;
        switch (id) {
        }
    }

    @Override
    public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
        int id = seekBar.getId();
        switch (id) {
            case R.id.margin_seek:
                if (getActivity() instanceof OnMenuMarginListener) {
                    ((OnMenuMarginListener) getActivity()).onMenuMarginChangeListener(value);
                }
                break;
            case R.id.round_seek:
                if (getActivity() instanceof OnMenuMarginListener) {
                    ((OnMenuMarginListener) getActivity()).onMenuRoundChangeListener(value);
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
}
