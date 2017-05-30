package uet.kltn.hoavt_58.crazyexface.photo_collage;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;

/**
 * Created by Adm on 9/29/2016.
 */
public class MyGlideModule implements GlideModule {
    @Override public void applyOptions(Context context, GlideBuilder builder) {
        // todo
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override public void registerComponents(Context context, Glide glide) {
        // todo
    }
}