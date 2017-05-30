package uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.texttabs;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pic.libphotocollage.core.model.TextStickerModel;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnMenuBackgroundListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.menu.ColorBackgroundAdapter;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.menu.MenuBackgroundAdapter;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.menu.MenuTextFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;

public class TextSettingFragment extends TextBaseFragment implements OnMenuBackgroundListener {

    private RecyclerView recyclerView = null;
    private RecyclerView recyclerViewPattern = null;
    private ColorBackgroundAdapter colorAdapter = null;
    private MenuBackgroundAdapter patternAdapter = null;
    private DiscreteSeekBar.OnProgressChangeListener mProgressChangeListener = new DiscreteSeekBar.OnProgressChangeListener() {
        @Override
        public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
            if (!(seekBar.getParent() instanceof ViewGroup))
                return;
            int id = ((ViewGroup) seekBar.getParent()).getId();
            switch (id) {
                case R.id.sb_padding_text:
                    if (getActivity() instanceof OnTextSettingListener)
                        ((OnTextSettingListener) getActivity()).onTextPaddingChangeListener(value);
                    break;
                case R.id.sb_size_text:
                    if (getActivity() instanceof OnTextSettingListener)
                        ((OnTextSettingListener) getActivity()).onTextSizeChangeListener(value + TextStickerModel.MIN_FONT_SIZE);
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
        }
    };
    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!(getActivity() instanceof OnTextSettingListener)) {
                return;
            }
            int id = view.getId();
            int idParent = ((ViewGroup) view.getParent()).getId();
            if (idParent == R.id.vg_scale && id == R.id.btn_first_option)
                ((OnTextSettingListener) getActivity()).onTextZoomInChangeListener();
            else if (idParent == R.id.vg_scale && id == R.id.btn_second_option)
                ((OnTextSettingListener) getActivity()).onTextZoomOutChangeListener();
            else if (idParent == R.id.vg_rotate && id == R.id.btn_first_option)
                ((OnTextSettingListener) getActivity()).onTextRotate5ChangeListener();
            else if (idParent == R.id.vg_rotate && id == R.id.btn_second_option)
                ((OnTextSettingListener) getActivity()).onTextMirror5ChangeListener();
        }
    };

    public TextSettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_text_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRecycleView();
        initSeekbars();
        initTextStickerOptions();
    }

    private void initTextStickerOptions() {
        int[] idViewGroupsArray = new int[]{R.id.vg_scale, R.id.vg_rotate};
        int[] describeTextsArray = new int[]{R.string.text_scale, R.string.text_rotate};
        int[] resIdFirstButtonsArray = new int[]{R.drawable.ic_zoom_in, R.drawable.ic_rotate_right_5};
        int[] resIdSecondButtonsArray = new int[]{R.drawable.ic_zoom_out, R.drawable.ic_rotate_left_5};
        for (int i = 0; i < idViewGroupsArray.length; i++)
            initTextStickerOptions(idViewGroupsArray[i], describeTextsArray[i], resIdFirstButtonsArray[i], resIdSecondButtonsArray[i]);

        /** Release arrays */
        idViewGroupsArray = null;
        describeTextsArray = null;
        resIdFirstButtonsArray = null;
        resIdSecondButtonsArray = null;
    }

    private void initTextStickerOptions(int idViewGroup, int describeText, int resIdFirstButton, int resIdSecondButton) {
        ViewGroup viewGroup = (ViewGroup) getView().findViewById(idViewGroup);
        TextView tvDescribe = (TextView) viewGroup.getChildAt(0);
        tvDescribe.setText(describeText);
        LinearLayoutCompat lnScale = (LinearLayoutCompat) viewGroup.getChildAt(1);
        ((ImageView) lnScale.getChildAt(0)).setImageResource(resIdFirstButton);
        lnScale.setOnClickListener(mClickListener);
        LinearLayoutCompat lnRotate = (LinearLayoutCompat) viewGroup.getChildAt(2);
        ((ImageView) lnRotate.getChildAt(0)).setImageResource(resIdSecondButton);
        lnRotate.setOnClickListener(mClickListener);
    }

    private void initSeekbars() {
        int[] idSeekbarsArray = new int[]{R.id.sb_size_text, R.id.sb_padding_text};
        int[] idDescribeTextsArray = new int[]{R.string.text_size, R.string.padding_size};
        int[] maxValuesArray = new int[]{TextStickerModel.MAX_FONT_SIZE - TextStickerModel.MIN_FONT_SIZE, Statistic.MAX_PADDING_SIZE};
        int[] progressValuesArray = new int[]{(int) getArguments().getFloat(MenuTextFrag.TEXT_SIZE_KEY, TextStickerModel.M_DEFULT_SIZE) - TextStickerModel.MIN_FONT_SIZE,
                (int) getArguments().getFloat(MenuTextFrag.PADDING_TEXT_KEY, 0)};
        for (int i = 0; i < idSeekbarsArray.length; i++)
            initSeekbars(idSeekbarsArray[i], idDescribeTextsArray[i], maxValuesArray[i], progressValuesArray[i]);

        /** Release arrays */
        idSeekbarsArray = null;
        idDescribeTextsArray = null;
        maxValuesArray = null;
        progressValuesArray = null;
    }

    private void initSeekbars(int idSeekbar, int idDescribeText, int maxValue, int progressValue) {
        ViewGroup viewGroup = (ViewGroup) getView().findViewById(idSeekbar);
        TextView describeText = (TextView) viewGroup.getChildAt(0);
        describeText.setText(idDescribeText);
        DiscreteSeekBar seekBar = (DiscreteSeekBar) viewGroup.getChildAt(1);
        seekBar.setMax(maxValue);
        seekBar.setProgress(progressValue);
        seekBar.setOnProgressChangeListener(mProgressChangeListener);
        int colorSb = ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark);
        seekBar.setScrubberColor(colorSb);
        seekBar.setThumbColor(colorSb, ContextCompat.getColor(getActivity(), R.color.colorPrimary));
//        seekBar.setTrackColor(ContextCompat.getColor(getActivity(), R.color.colorSecondary));
//        seekBar.setRippleColor(ContextCompat.getColor(getActivity(), R.color.colorSecondary));
    }

    private void initRecycleView() {
        int[] colorPick = getActivity().getResources().getIntArray(R.array.color_picker);

        colorAdapter = new ColorBackgroundAdapter(getActivity(), colorPick)
                .setOnMenuBackgroundListener(this);

        recyclerView = (RecyclerView) getView().findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(colorAdapter);

        patternAdapter = new MenuBackgroundAdapter(getActivity(), Statistic.BG_LIST, true)
                .setOnMenuBackgroundListener((OnMenuBackgroundListener) getActivity());

        recyclerViewPattern = (RecyclerView) getView().findViewById(R.id.recycle_view_pattern);
        recyclerViewPattern.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewPattern.setAdapter(patternAdapter);
    }

    @Override
    public void onMenuBackgroundClickListener(String bgName, boolean isPattern) {
        Log.d("hoacode", "11 onMenuBackgroundClickListener");
    }

    @Override
    public void onMenuColorBackgroundClickListener(@ColorInt int color) {
        if (getActivity() instanceof OnTextSettingListener) {
            ((OnTextSettingListener) getActivity()).onColorClickListener(color);
        }
    }

    public interface OnTextSettingListener {
        public void onColorClickListener(@ColorInt int color);

        public void onTextSizeChangeListener(int unit);

        public void onTextPaddingChangeListener(int unit);

        public void onTextZoomInChangeListener();

        public void onTextZoomOutChangeListener();

        public void onTextRotate5ChangeListener();

        public void onTextMirror5ChangeListener();
    }
}
