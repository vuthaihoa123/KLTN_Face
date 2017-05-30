
package uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnMenuRatioListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.BaseFragment;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;

/**
 * Created by Adm on 8/9/2016.
 */
public class MenuRatioFrag extends BaseFragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_collage_menu_ratio_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initButtonRatio();
    }

    private void initButtonRatio() {
        if (getArguments() != null && !getArguments().getBoolean(Statistic.EXTRA_RATIO_FRAG)) {
            getView().findViewById(R.id.btn_ratio_11).setOnClickListener(this);
            getView().findViewById(R.id.btn_ratio_32).setVisibility(View.GONE);
            getView().findViewById(R.id.btn_ratio_23).setVisibility(View.GONE);
            getView().findViewById(R.id.btn_ratio_34).setVisibility(View.GONE);
            getView().findViewById(R.id.btn_ratio_43).setVisibility(View.GONE);
            getView().findViewById(R.id.btn_ratio_45).setVisibility(View.GONE);
            getView().findViewById(R.id.btn_ratio_54).setVisibility(View.GONE);
            getView().findViewById(R.id.btn_ratio_916).setOnClickListener(this);
            getView().findViewById(R.id.btn_ratio_169).setVisibility(View.GONE);
        } else {
            getView().findViewById(R.id.btn_ratio_11).setOnClickListener(this);
            getView().findViewById(R.id.btn_ratio_32).setOnClickListener(this);
            getView().findViewById(R.id.btn_ratio_23).setOnClickListener(this);
            getView().findViewById(R.id.btn_ratio_34).setOnClickListener(this);
            getView().findViewById(R.id.btn_ratio_43).setOnClickListener(this);
            getView().findViewById(R.id.btn_ratio_45).setOnClickListener(this);
            getView().findViewById(R.id.btn_ratio_54).setOnClickListener(this);
            getView().findViewById(R.id.btn_ratio_916).setOnClickListener(this);
            getView().findViewById(R.id.btn_ratio_169).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        Log.d("MenuRatioFrag", "onClick");
        int id = v.getId();
        float ratio = 1.f;
        switch (id) {
            case R.id.btn_ratio_11:
                ratio = 1.f;
                break;
            case R.id.btn_ratio_32:
                ratio = 2.f / 3.f;
                break;
            case R.id.btn_ratio_23:
                ratio = 3.f / 2.f;
                break;
            case R.id.btn_ratio_34:
                ratio = 4.f / 3.f;
                break;
            case R.id.btn_ratio_43:
                ratio = 3.f / 4.f;
                break;
            case R.id.btn_ratio_45:
                ratio = 5.f / 4.f;
                break;
            case R.id.btn_ratio_54:
                ratio = 4.f / 5.f;
                break;
            case R.id.btn_ratio_916:
                ratio = 16.f / 9.f;
                break;
            case R.id.btn_ratio_169:
                ratio = 9.f / 16.f;
                break;
            default:
                ratio = 1.f;
                break;
        }

        if (getActivity() instanceof OnMenuRatioListener) {
            ((OnMenuRatioListener) getActivity()).onMenuRatioClickListener(ratio);
        }
    }
}
