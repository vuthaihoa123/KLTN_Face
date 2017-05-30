package uet.kltn.hoavt_58.crazyexface.photo_collage.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.MainEditorActivity;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnCollageMenuListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;


/**
 * Created by Adm on 8/3/2016.
 */
public class FooterMenuFragment extends BaseFragment {

    private static final String TAG = FooterMenuFragment.class.getSimpleName();

    private OnCollageMenuListener listener = null;
    private LinearLayoutCompat mBtLayout, mBtTextSticker, mBtIconSticker, mBtOtherOptions;
    private View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.bt_layout) menuType = Statistic.MENU_COLLAGE_TYPE_LAYOUT;
            else if (id == R.id.bt_text_sticker) menuType = Statistic.MENU_COLLAGE_TYPE_TEXT;
            else if (id == R.id.bt_icon_sticker) menuType = Statistic.MENU_COLLAGE_TYPE_STICKER;
            else if (id == R.id.bt_other_options) menuType = Statistic.MENU_COLLAGE_TYPE_OTHERS;
            else menuType = -1;
            if (listener != null && menuType != -1) {
                listener.onCollageMenuClickListener(menuType);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_footer_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listener = (MainEditorActivity) getActivity();
        initViews();
    }

    private void initViews() {

        int[] idIconsArray = new int[]{R.drawable.ic_layout, R.drawable.ic_text_sticker, R.drawable.ic_icon_sticker, R.drawable.ic_other_options};
        LinearLayoutCompat[] buttonsArray = new LinearLayoutCompat[]{mBtLayout, mBtTextSticker, mBtIconSticker, mBtOtherOptions};
        int[] idButtonsArray = new int[]{R.id.bt_layout, R.id.bt_text_sticker, R.id.bt_icon_sticker, R.id.bt_other_options};

        for (int i = 0; i < buttonsArray.length; i++)
            initButtonView(buttonsArray[i], idButtonsArray[i], idIconsArray[i]);

        getView().post(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null)
                    ((MainEditorActivity) getActivity()).onLayoutReady();
            }
        });

        /** Release arrays */
        idIconsArray = null;
        buttonsArray = null;
        idButtonsArray = null;
    }

    private void initButtonView(View viewGroup, int idButton, int idImageView) {
        viewGroup = getView().findViewById(idButton);
        ((ImageView) ((ViewGroup) viewGroup).getChildAt(0)).setImageResource(idImageView);
        viewGroup.setOnClickListener(mButtonListener);
    }
}
