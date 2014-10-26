package net.formula97.fakegpbase;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by f97one on 14/10/26.
 */
public class TypeFacedTextView extends TextView {

    private String mFontName = "formation_sans_regular.ttf";

    public TypeFacedTextView(Context context) {
        super(context);
        init();
    }

    public TypeFacedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getFont(context, attrs);
        init();
    }

    public TypeFacedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getFont(context, attrs);
        init();
    }

    public void getFont(Context context, AttributeSet attributeSet) {
        if (isInEditMode()) {
            return;
        }

        TypedArray ta = context.obtainStyledAttributes(attributeSet, R.styleable.TypeFacedTextView);
        mFontName = ta.getString(R.styleable.TypeFacedTextView_font);
        ta.recycle();
    }

    public void init() {
        if (isInEditMode()) {
            return;
        }

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), mFontName);
        setTypeface(typeface);
    }
}
