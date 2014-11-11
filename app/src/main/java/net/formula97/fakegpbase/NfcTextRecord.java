package net.formula97.fakegpbase;

import android.nfc.NdefRecord;

/**
 * NFC TEXTレコードを保持するDTOクラス。<br />
 * Created by f97one on 14/11/11.
 */
public class NfcTextRecord {

    private String mText;
    private String mLanguageCode;
    private boolean mEncodeUtf8;

    public NfcTextRecord() { }

    public NfcTextRecord(String text, String languageCode, boolean encodeUtf8) {
        this.mText = text;
        this.mLanguageCode = languageCode;
        this.mEncodeUtf8 = encodeUtf8;
    }

    public String getText() {
        return this.mText;
    }

    public String getLanguageCode() {
        return this.mLanguageCode;
    }

    public boolean isEncodeUtf8() {
        return this.mEncodeUtf8;
    }

    public void setText(String text) {
        this.mText = text;
    }

    public void setLanguageCode(String languageCode) {
        this.mLanguageCode = languageCode;
    }

    public void setEncodeUtf8(boolean encodeUtf8) {
        this.mEncodeUtf8 = encodeUtf8;
    }
}
