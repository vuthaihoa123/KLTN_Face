package uet.kltn.hoavt_58.crazyexface.photo_collage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import uet.kltn.hoavt_58.crazyexface.R;


/**
 * Created by Adm on 11/9/2016.
 */
public class FunctionActivity extends BaseActivity {
    public static final String FRAGMENT_HOME = "fragment_home";
    public static final int SETTING = 1;
    public static final int FOLDER = 2;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);
        toolbar = getToolbar();
        toolbar.setTitle("My Studio");
        setSupportActionBar(toolbar);
        initFragment();
    }

    private void initFragment() {

        switch (getIntent().getIntExtra(FRAGMENT_HOME, 0)) {
            case SETTING:
//                getFragmentManager().beginTransaction().add(R.id.content_main, SettingFragment.newFragment()).commit();
                break;

            case FOLDER:
//                getSupportFragmentManager().beginTransaction().add(R.id.content_main, MyStudioActivity.newFragment()).commit();
                startActivity(new Intent(this, MyStudioActivity.class));
                finish();
                break;
        }

    }

    public Toolbar getToolbar() {
        if (toolbar == null) {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
        }
        return toolbar;
    }
}
