package uet.kltn.hoavt_58.crazyexface.photo_collage;

import android.app.Application;

import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;


/**
 * Created by Adm on 8/3/2016.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        initUniversal();

        Statistic.init();
    }

//    private void initUniversal() {
//        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
////                .cacheOnDisk(true)
//                .cacheInMemory(true)
//                .displayer(new FadeInBitmapDisplayer(200))
//                .considerExifParams(true)   // Sets whether ImageLoader will consider EXIF parameters of JPEG image (rotate, flip)
//                .build();
//
//        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
//
//        config.threadPriority(Thread.NORM_PRIORITY - 2)
//                .denyCacheImageMultipleSizesInMemory()
//                .memoryCache(new LRULimitedMemoryCache(50* 1024* 1024))
//                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
//                .threadPoolSize(3)
//                .diskCacheSize(50* 1024* 1024) // 50 MiB
//                .tasksProcessingOrder(QueueProcessingType.LIFO)
//                .defaultDisplayImageOptions(defaultOptions)
////                .writeDebugLogs() // Remove for release app
//                ;
//        // Initialize ImageLoader with configuration.
//        ImageLoader.getInstance().init(config.build());
//    }
}
