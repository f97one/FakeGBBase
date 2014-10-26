package net.formula97.fakegpbase;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by f97one on 14/10/26.
 */
public class TypeFacedTextView extends TextView {

    private String defaultFontName = "formation_sans_regular.ttf";

    public TypeFacedTextView(Context context) {
        super(context);
    }

    public TypeFacedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TypeFacedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
