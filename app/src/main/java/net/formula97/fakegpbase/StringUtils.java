package net.formula97.fakegpbase;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * 文字列操作に関するユーティリティクラス。<br />
 * Created by f97one on 14/11/24.
 */
public class StringUtils {

    /**
     * コンストラクタ。<br />
     * インスタンス化させないようにするため、privateにする。
     */
    private StringUtils() { }

    /**
     * EditTextで使用するInputFilterを作成する。
     *
     * @param filterRegexp 入力を許可する文字列の正規表現
     * @param limitLength 入力を許可する最大文字数、0指定は制限なし
     * @return InputFilterのオブジェクト
     */
    public static InputFilter[] makeFilter(final String filterRegexp, int limitLength) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.toString().matches(filterRegexp)) {
                    return source;
                } else {
                    return "";
                }
            }
        };

        if (limitLength == 0) {
            InputFilter[] filters = {filter};
            return filters;
        } else {
            InputFilter[] filters = {
                    filter,
                    new InputFilter.LengthFilter(limitLength)
            };
            return filters;
        }
    }

}
