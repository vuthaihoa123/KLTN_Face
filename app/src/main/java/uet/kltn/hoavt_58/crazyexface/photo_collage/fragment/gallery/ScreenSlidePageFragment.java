package uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.gallery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.DisplayPhotoActivity;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;


/**
 * Created by vutha on 10/4/2016.
 */
public class ScreenSlidePageFragment extends Fragment {

    private ImageView mIvShowPreview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    private void initViews() {
        mIvShowPreview = (ImageView) getView().findViewById(R.id.iv_show_preview);
        Bundle returnedBundle = getArguments();
        if (returnedBundle != null) {
            String filePath = returnedBundle.getString(Statistic.EXTRA_PATH_PHOTO);
            Glide.with(getActivity()).load(filePath)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .fitCenter()
                    .into(mIvShowPreview);
        }

        ((DisplayPhotoActivity)getActivity()).upToolbar();
    }
}
