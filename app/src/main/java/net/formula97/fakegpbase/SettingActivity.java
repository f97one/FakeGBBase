package net.formula97.fakegpbase;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import net.formula97.fakegpbase.fragments.AboutDialog;

/**
 * Preference画面を処理するActivity。
 */
public class SettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends PreferenceFragment {

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setting);

            // 入力制限フィルタの定義
            // ※正確には入力制限ではないが、挙動的に入力制限になっている
            EditTextPreference builderName = (EditTextPreference) findPreference(AppConst.PREF_KEY_BUILDER_NAME);
            EditTextPreference fighterName = (EditTextPreference) findPreference(AppConst.PREF_KEY_FIGHTER_NAME);
            builderName.setOnPreferenceChangeListener(listener);
            fighterName.setOnPreferenceChangeListener(listener);

            // EditTextPreferenceのサマリに、Preferenceの値を表示する
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String fname = pref.getString(AppConst.PREF_KEY_FIGHTER_NAME, "");
            String bname = pref.getString(AppConst.PREF_KEY_BUILDER_NAME, "");
            builderName.setSummary(bname);
            fighterName.setSummary(fname);

            // Formation Sans配布サイトへのリンク
            Preference f = findPreference(AppConst.PREF_KEY_FORMATION_SANS);
            f.setOnPreferenceClickListener(mFontClick);

            // 「このアプリについて」の表示
            Preference aboutThis = findPreference(AppConst.PREF_KEY_ABOUT_APP);
            aboutThis.setOnPreferenceClickListener(mAboutThis);
        }

        /**
         * Preferenceの入力値が変更されたあとの処理を行うコールバックリスナ。<br />
         * 正規表現でマッチした場合のみ、Preferenceの更新を許可する。
         */
        private Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String edited = newValue.toString();

                // フィルタの正規表現にマッチした時だけOKとし、サマリを入力値で更新する
                if (edited.matches(AppConst.INPUT_FILTER_ALL_CAPS_WITH_NUMBER)) {
                    preference.setSummary(edited);
                    return true;
                } else {
                    return false;
                }
            }
        };

        /**
         * Formation Sans配布サイトへのリンクを表示
         */
        private Preference.OnPreferenceClickListener mFontClick = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Uri uri = Uri.parse(AppConst.FORMATION_SANS_URL);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                startActivity(intent);

                return true;
            }
        };

        /**
         * 「このアプリについて」ダイアログを表示
         */
        private Preference.OnPreferenceClickListener mAboutThis = new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AboutDialog dialog = new AboutDialog();
                dialog.show(getFragmentManager(), AboutDialog.DIALOG_TAG);

                return true;
            }
        };
    }
}
