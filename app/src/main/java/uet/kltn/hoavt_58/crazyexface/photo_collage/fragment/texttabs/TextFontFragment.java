package uet.kltn.hoavt_58.crazyexface.photo_collage.fragment.texttabs;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pic.libphotocollage.core.util.Flog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uet.kltn.hoavt_58.crazyexface.R;
import uet.kltn.hoavt_58.crazyexface.photo_collage.adapter.text.TextFontAdapter;
import uet.kltn.hoavt_58.crazyexface.photo_collage.model.FontModel;
import uet.kltn.hoavt_58.crazyexface.photo_collage.utils.FontUtils;

public class TextFontFragment extends TextBaseFragment {

    private static final int NUMBER_CUSTOM_FONTS = 4;
    private static final String[] NAME_FONTS = new String[]{
            "Coiny-regular",
            "Fairfax station",
            "Champagne & limousines",
            "Digs my hart"
    };
    private RecyclerView recyclerView = null;
    private TextFontAdapter adapter = null;
    private List<FontModel> fontList;

    public TextFontFragment() {
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
        return inflater.inflate(R.layout.fragment_text_font, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fontList = new ArrayList<>();
        setLocalFonts();
        setCustomFonts();
        initRecycleView();
    }

    private void setCustomFonts() {
        for (int i = 1; i <= NUMBER_CUSTOM_FONTS; i++) {
            FontModel fontModel = new FontModel();
            fontModel.fontPath = "font/f" + ((i < 10) ? "0" : "") + (i) + ".ttf";
            fontModel.fontPreview = NAME_FONTS[i - 1];
            fontList.add(fontModel);
        }
    }

    private void setLocalFonts() {
        final List<FontUtils.SystemFont> fonts = FontUtils.safelyGetSystemFonts();

        FontModel fontDefault = new FontModel();
        fontDefault.fontPath = "";
        fontDefault.fontPreview = "Default";
        fontList.add(fontDefault);

        for (int i = 0; i < fonts.size(); i++) {
            FontModel fontModel = new FontModel();
            fontModel.fontPath = fonts.get(i).path;
            fontModel.fontPreview = fonts.get(i).name;
            Flog.i("path=" + fontModel.fontPath + "_preview=" + fontModel.fontPreview);
            if (new File(fontModel.fontPath).exists()) {
                fontList.add(fontModel);
            }
        }
    }

    private void initRecycleView() {
        adapter = new TextFontAdapter(getContext(), fontList, NUMBER_CUSTOM_FONTS);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }
}
