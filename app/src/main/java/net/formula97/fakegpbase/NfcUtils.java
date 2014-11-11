package net.formula97.fakegpbase;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

/**
 * NFCタグ操作ユーティリティクラス。＜br />
 * Created by f97one on 14/11/11.
 */
public class NfcUtils {

    /**
     * 書き込み処理終了の通知を行うコールバックリスナ。
     */
    public interface OnWroteCallback {
        /**
         * 書き込み処理に成功した時に呼び出される。
         */
        public void onSuccess();

        /**
         * 書き込み処理に失敗した時に呼び出される。
         *
         * @param errorMessage 失敗時のエラーメッセージ
         * @param e 例外オブジェクト
         */
        public void onFailed(String errorMessage, Throwable e);
    }


    public NfcUtils() { }

    /**
     * TXTレコードのNDEFレコードを作成する。
     *
     * @param locale
     * @param text
     * @param encodeUTF8
     * @return
     */
    public NdefRecord toNdefRecord(String locale, String text, boolean encodeUTF8) {
        // ロケールをバイト配列にする
        byte[] langBytes = locale.getBytes(Charset.forName(AppConst.CHARSET_US_ASCII));

        // 改行コードをCRLFに固定
        text = convertToCRLF(text);

        // テキストコードをバイト配列に変換
        Charset utfEncoding = encodeUTF8 ?
                Charset.forName(AppConst.CHARSET_UTF8) : Charset.forName(AppConst.CHARSET_UTF16);
        byte[] textBytes = text.getBytes(utfEncoding);

        // ステータスバイトを構築
        int utfBit = encodeUTF8 ? 0 : 0x80;
        byte status = (byte) (utfBit | langBytes.length);

        // ステータスバイト、言語バイト、テキストバイトを連結し、ペイロードを構築
        byte[] payload = concat(new byte[] { (byte) status }, langBytes, textBytes);

        // NDEFレコードを作成
        NdefRecord rec = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload);
        return rec;
    }

    /**
     * バイト配列を連結する。
     *
     * @param bytesArray
     * @return
     */
    private byte[] concat(byte[]... bytesArray) {
        byte[] tmp = null;
        for (byte[] bytes : bytesArray) {
            if (tmp == null) {
                tmp = bytes;
            } else {
                byte[] c = new byte[tmp.length + bytes.length];
                System.arraycopy(tmp, 0, c, 0, tmp.length);
                System.arraycopy(bytes, 0, c, tmp.length, bytes.length);

                tmp = c;
            }
        }

        return tmp;
    }

    /**
     * 改行コードをCRLFに固定する。
     *
     * @param text 変換対象の文字列
     * @return 改行コードをCRLFに固定した文字列
     */
    private String convertToCRLF(String text) {
        String repCrLf = text.replaceAll("\r\n", "\n");
        String repLf = repCrLf.replaceAll("\r", "\n");
        String rep = repLf.replaceAll("\n", "\r\n");

        return rep;
    }

    /**
     * 改行コードをLFに固定する。
     *
     * @param text 変換対象の文字列
     * @return 改行コードをLFに固定した文字列
     */
    private String convertToLf(String text) {
        String repCrLf = text.replaceAll("\r\n", "\n");
        String repLf = repCrLf.replaceAll("\r", "\n");

        return  repLf;
    }

    public NfcTextRecord parse(NdefRecord parseRec) throws FormatException {

        // TNF_WELL_KNOWNでない場合は例外を投げる
        if (parseRec.getTnf() != NdefRecord.TNF_WELL_KNOWN) {
            throw new FormatException("TNF is not NFC Forum well-known type.");
        }
        // RTD_TEXTでない場合も例外を投げる
        if (!Arrays.equals(parseRec.getType(), NdefRecord.RTD_TEXT)) {
            throw new FormatException("RTD Type is not RTD TEXT.");
        }

        // ペイロードの取り出し
        byte[] payload = parseRec.getPayload();
        byte status = payload[0];

        // エンコーディングの取得
        boolean encodeUtf8 = ((status & 0x80) == 0);
        final String textEncoding = encodeUtf8 ? AppConst.CHARSET_UTF8 : AppConst.CHARSET_UTF16;

        // 言語コードの長さを取得
        int langCodeLength = status & 0x3F;
        try {
            String text = new String(payload,
                    langCodeLength + 1, payload.length - langCodeLength - 1, textEncoding);
            String languageCode = new String(payload,
                    1, langCodeLength, Charset.forName(AppConst.CHARSET_US_ASCII));

            // 改行コードをLFに変換
            text = convertToLf(text);

            return new NfcTextRecord(text, languageCode, encodeUtf8);
        } catch (UnsupportedEncodingException e) {
            throw new FormatException();
        }
    }

    public void writeTag(Tag tag, NdefMessage msg, OnWroteCallback callback) {
        try {
            if (tag == null) {
                throw new RuntimeException("NFCタグが正常に読み取れませんでした。",
                        new NullPointerException());
            }
            if (msg == null) {
                throw new RuntimeException("メッセージが指定されていません。",
                        new NullPointerException());
            }
            List<String> techList = Arrays.asList(tag.getTechList());
            if (techList.contains(Ndef.class.getName())) {
                // Ndef データが格納されている場合
                // すでにNDEFデータが入っているタグです。
                // この場合は、書き込むNdefMessageを、Ndef.writeNdefMessageで書き込みます。
                Ndef ndef = Ndef.get(tag);
                try {
                    ndef.connect();
                    ndef.writeNdefMessage(msg);
                } catch (IOException e) {
                    throw new RuntimeException("タグとの通信に失敗しました。", e);
                } catch (FormatException e) {
                    throw new RuntimeException("送信するNDEFメッセージのフォーマットが不正です。", e);
                } finally {
                    try {
                        ndef.close();
                    } catch (IOException e) {
                    }
                }
            } else if (techList.contains(NdefFormatable.class.getName())) {
                // NdefFormattableが含まれている場合
                // NDEFにフォーマット可能なNFCタグです。この場合、NdefFormatable.format(...)で
                // NdefMessageを書き込むと共に、Ndefにフォーマットすることができます。
                NdefFormatable ndeffmt = NdefFormatable.get(tag);
                try {
                    ndeffmt.connect();
                    ndeffmt.format(msg);
                } catch (IOException e) {
                    throw new RuntimeException("タグとの通信に失敗しました。", e);
                } catch (FormatException e) {
                    throw new RuntimeException("送信するNDEFメッセージのフォーマットが不正です。", e);
                } finally {
                    try {
                        ndeffmt.close();
                    } catch (IOException e) {
                    }
                }
            } else {
                // 書き込むのが難しいタグ
                throw new RuntimeException("NDEFメッセージが書き込めないタグでした。",
                        new RuntimeException());
            }

            if (callback != null) {
                callback.onSuccess();
            }
        } catch (RuntimeException e) {
            if (callback != null) {
                callback.onFailed(e.getMessage(), e.getCause());
            }
        }

    }
}
