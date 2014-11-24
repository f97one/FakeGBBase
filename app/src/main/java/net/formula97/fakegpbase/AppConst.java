package net.formula97.fakegpbase;

/**
 * 雑多な定数値を格納するユーティリティクラス。<br />
 * Created by f97one on 14/11/02.
 */
public class AppConst {

    private AppConst() { }

    public static final String DATABASE_NAME = "GunplaInfo.db";
    public static final int DATABASE_VERSION = 1;
    public static final String DIALOG_FRAGMENT_SUFFIX = ".DIALOG_TAG";
    public static final int NO_SCRATCH_BUILT  = 0;
    public static final int PARTIAL_SCRATCH_BUILT = 1;
    public static final int FULL_SCRATCH_BUILT = 2;

    public static final String PREF_KEY_BUILDER_NAME = "builder_name";
    public static final String PREF_KEY_FIGHTER_NAME = "fighter_name";
    public static final String PREF_KEY_USE_WAKE_LOCK_TAG = "use_tag_for_wake_lock";

    public static final String CHARSET_US_ASCII = "US-ASCII";
    public static final String CHARSET_UTF8 = "UTF-8";
    public static final String CHARSET_UTF16 = "UTF-16";

    public static final String INPUT_FILTER_ALL_CAPS_WITH_NUMBER = "^[A-Za-z0-9./ ]+$";
    public static final String INPUT_FILTER_ALL_CAPS_WITH_NUMBER_AND_SIMBOLS = "^[A-Za-z0-9./(){}*+, ]+$";

    public static final String TAGLESS_TAG_ID_PREFIX = "NOTAG_";
}
