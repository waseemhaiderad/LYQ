package lnq.com.lnq.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;

public class FontUtils {

    private static Typeface typefaceBold, typefaceRegular, typefaceSemiBold, typefaceMedium;
    private static FontUtils fontUtils;

    private FontUtils() {
    }

    public static FontUtils getFontUtils(Context context) {
        if (fontUtils == null) {
            fontUtils = new FontUtils();
            typefaceBold = Typeface.createFromAsset(context.getAssets(), "fonts/sf_pro_display_bold.otf");
            typefaceRegular = Typeface.createFromAsset(context.getAssets(), "fonts/sf_pro_display_regular.otf");
            typefaceSemiBold = Typeface.createFromAsset(context.getAssets(), "fonts/sf_pro_display_semibold.otf");
            typefaceMedium = Typeface.createFromAsset(context.getAssets(), "fonts/sf_pro_display_medium.otf");
        }
        return fontUtils;
    }

    public void setTextViewRegularFont(TextView textView) {
        textView.setTypeface(typefaceRegular);
    }

    public void setTextViewBoldFont(TextView textView) {
        textView.setTypeface(typefaceBold);
    }

    public void setTextViewSemiBold(TextView textView) {
        textView.setTypeface(typefaceSemiBold);
    }

    public void setTextViewMedium(TextView textView) {
        textView.setTypeface(typefaceMedium);
    }

    public void setButtonRegularFont(Button button) {
        button.setTypeface(typefaceRegular);
    }

    public void setButtonBoldFont(Button button) {
        button.setTypeface(typefaceBold);
    }

    public void setButtonSemiBold(Button button) {
        button.setTypeface(typefaceSemiBold);
    }

    public void setButtonMedium(Button button) {
        button.setTypeface(typefaceMedium);
    }

    public void setEditTextRegularFont(EditText editText) {
        editText.setTypeface(typefaceRegular);
    }

    public void setEditTextBoldFont(EditText editText) {
        editText.setTypeface(typefaceBold);
    }

    public void setEditTextSemiBold(EditText editText) {
        editText.setTypeface(typefaceSemiBold);
    }

    public void setEditTextMedium(EditText editText) {
        editText.setTypeface(typefaceMedium);
    }

    public Typeface getTypefaceRegular() {
        return typefaceRegular;
    }

    public Typeface getTypefaceBold() {
        return typefaceBold;
    }

    public static Typeface getTypefaceSemiBold() {
        return typefaceSemiBold;
    }

    public static Typeface getTypefaceMedium() {
        return typefaceMedium;
    }

    public void setRadioButtonRegularFont(RadioButton radioButton) {
        radioButton.setTypeface(typefaceRegular);
    }

    public static void setFont(ViewGroup group) {
        int count = group.getChildCount();
        View v;
        for (int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if (v instanceof TextView) {
                ((TextView) v).setTypeface(typefaceSemiBold);
            } else if (v instanceof ViewGroup) setFont((ViewGroup) v);
        }
    }

    /**
     * Sets the font on TextView
     */
    public static void setFont(View v) {
        if (v instanceof TextView) {
            ((TextView) v).setTypeface(typefaceSemiBold);
        }
    }

}
