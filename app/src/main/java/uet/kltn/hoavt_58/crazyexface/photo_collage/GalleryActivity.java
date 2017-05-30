package uet.kltn.hoavt_58.crazyexface.photo_collage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;


import java.util.ArrayList;
import java.util.List;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.VerticalSpaceItemDecoration;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.gallery.DrawerAdapter;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.listener.OnGalleryListener;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.gallery.GalleryContainerFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.gallery.GalleryFooterFrag;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.DrawerItem;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.PhotoModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.statistic.Statistic;
import uet.kltn.hoavt_58.crazyexface.photo_collage.utils.FLog;
import uet.kltn.hoavt_58.crazyexface.photo_collage.utils.PlatformUtils;
import uet.kltn.hoavt_58.crazyexface.photo_collage.utils.SettingsUtil;

/**
 * Created by Adm on 8/10/2016.
 */
public class GalleryActivity extends BaseActivity implements
        OnGalleryListener, DrawerAdapter.OnNavigationItemSelectedListener {
    public static final String EXTRA_PHOTO_MODEL_LIST = "EXTRA_PHOTO_MODEL_LIST";
    public static final String EXTRA_NUM_PICKED_FRAME_KEY = "EXTRA_NUM_PICKED_FRAME_KEY";
    private static final String TAG = GalleryActivity.class.getSimpleName();
    private Toolbar mToolbar = null;
    private List<String> sortedFolderList = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gallery);
        SettingsUtil.setColorStatusBar(this, ContextCompat.getColor(this, R.color.colorPrimaryDark));

        initToolbar();
        initDrawer();
        initSpinner();

        initFragment();
    }

    private void initDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mToolbar.setTitle(R.string.all);
    }

    private void initSpinner() {
//        mSpinner = (AppCompatSpinner) findViewById(R.id.spinner_nav);

        sortedFolderList = new ArrayList<String>();
        sortedFolderList.add("ALL");

//        dataAdapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_item, sortedFolderList);
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mSpinner.setAdapter(dataAdapter);
//
//        mSpinner.setOnItemSelectedListener(this);
    }

    private void initFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.gallery_content, new GalleryContainerFrag()).commit();
        // menu footer
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_up, R.anim.no_anim)
                .add(R.id.gallery_footer, new GalleryFooterFrag()).commit();
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        // must place in this order
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(R.drawable.ic_back);

        mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        mToolbar.setTitle(R.string.collage);
        mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FLog.d(TAG, "back clicked");
                onBackPressed();
            }
        });

        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
//
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        FLog.d(TAG, "onItemSelected: " + position);
//        GalleryContainerFrag frag = (GalleryContainerFrag) getSupportFragmentManager().findFragmentById(R.id.gallery_content);
//        if (position == 0) {
//            // all
//            frag.reloadRecycleView();
//        } else {
//            String key = sortedFolderList.get(position);
//            frag.reloadRecycleView(key);
//        }
//    }
//


    @Override
    public void onPhotoPickListener(final PhotoModel photoModel, View sourceView) {
        FLog.d(TAG, "onPhotoPickListener");
        final GalleryFooterFrag galleryFooterFrag = (GalleryFooterFrag) getSupportFragmentManager().findFragmentById(R.id.gallery_footer);
        if (galleryFooterFrag.getCurrentTargetView() == null)
            return;
        performAnimation(sourceView, galleryFooterFrag.getCurrentTargetView());
        galleryFooterFrag.getImgAdapter().pushPhoto(photoModel);
        galleryFooterFrag.scrollToCurPosition();
    }

    private void performAnimation(View source, View destination) {
//        Log.d(TAG, "perform Animation");

        final FrameLayout frameLayout = new FrameLayout(this);
        final ImageView imageView = new ImageView(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(source.getWidth(), source.getHeight());
        imageView.setLayoutParams(params);
        ImageView sourceImage = (ImageView) source.findViewById(R.id.img_picked);
        imageView.setImageDrawable(sourceImage.getDrawable());

        frameLayout.addView(imageView);

        frameLayout.setX(source.getX());
        frameLayout.setY(source.getY());

        final ViewGroup root = (ViewGroup) findViewById(R.id.root_content_view);
        root.addView(frameLayout);

        int[] sourcePos = new int[2];
        int[] destPos = new int[2];
        source.getLocationInWindow(sourcePos);
        destination.getLocationInWindow(destPos);

        int xDelta = destPos[0] - sourcePos[0];
        int yDelta = destPos[1] - sourcePos[1];

        TranslateAnimation translate = new TranslateAnimation(0, xDelta, 0, yDelta);
        translate.setDuration(Statistic.GALLERY_SHARED_ITEM_INTERVAL);
        translate.setFillAfter(true);

        float scaleX = (float) destination.getWidth() / source.getWidth();
        float scaleY = (float) destination.getHeight() / source.getHeight();

        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, scaleX, 1.0f, scaleY);
        scaleAnimation.setDuration(Statistic.GALLERY_SHARED_ITEM_INTERVAL);
        scaleAnimation.setFillAfter(true);

        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                root.post(new Runnable() {
                    @Override
                    public void run() {
                        root.removeView(frameLayout);
//                        final GalleryFooterFrag galleryFooterFrag = (GalleryFooterFrag) getSupportFragmentManager().findFragmentById(R.id.gallery_footer);
//                        galleryFooterFrag.getImgAdapter().notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageView.startAnimation(scaleAnimation);
        frameLayout.startAnimation(translate);

    }

    @Override
    public void onGalleryLoaded(List<String> sortedFolderList) {
        this.sortedFolderList.addAll(sortedFolderList);
        initNavigationView();
    }

    private void initNavigationView() {
        List<DrawerItem> mDrawerItemList = new ArrayList<>();
        DrawerItem item = new DrawerItem();
        item.setTitle(getString(R.string.all));
        item.total = GalleryContainerFrag.allPhotoList.size();
        mDrawerItemList.add(item);

        for (String key : GalleryContainerFrag.sortedFolderList) {

            item = new DrawerItem();
            item.setTitle(key);
            item.total = GalleryContainerFrag.photoMap.get(key).size();
            item.iconPath = GalleryContainerFrag.photoMap.get(key).get(0).imgPath;
            mDrawerItemList.add(item);
        }

        DrawerAdapter adapter = new DrawerAdapter(mDrawerItemList);
        RecyclerView navView = (RecyclerView) findViewById(R.id.nav_view);

        navView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        navView.addItemDecoration(new VerticalSpaceItemDecoration(PlatformUtils.dpToPx(this, 1)));
        navView.setAdapter(adapter);

        adapter.setOnItemSelectedListener(this);

    }

    @Override
    public void onNavigationItemSelected(int position) {
        Log.d(TAG, "onNavigationItem selected = " + position);
        GalleryContainerFrag frag = (GalleryContainerFrag) getSupportFragmentManager().findFragmentById(R.id.gallery_content);
        if (position == 0) {
            frag.reloadRecycleView();
            mToolbar.setTitle(R.string.all);
        } else {
            String key = sortedFolderList.get(position);
            frag.reloadRecycleView(key);
            mToolbar.setTitle(key);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            Log.d("memory", "onBackPressed");
        }
    }

    private void release() {
        GalleryContainerFrag.photoMap = null;
        GalleryContainerFrag.allPhotoList = null;
        GalleryContainerFrag.sortedFolderList = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
//        release();    : hoa comment
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        release();
    }
}
