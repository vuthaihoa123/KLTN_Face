package uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.texttabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pic.libphotocollage.core.util.Flog;

import uet.kltn.hoavt_58.crazyexface.R;

public class TextKeyboardFragment extends TextBaseFragment {
    private static final String TAG = TextKeyboardFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Flog.i("TextKeyboardFragment");
        return inflater.inflate(R.layout.fragment_text_keyboard, container, false);
    }
}
