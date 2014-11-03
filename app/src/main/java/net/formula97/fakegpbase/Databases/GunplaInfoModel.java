package net.formula97.fakegpbase.Databases;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by f97one on 14/11/02.
 */
public class GunplaInfoModel {

    private DbHelper helper;

    public GunplaInfoModel(Context context) {
        helper = DbHelper.getDatabase(context);
    }

    public void save(final GunplaInfo gunplaInfoEntity) {
        try {
            final Dao<GunplaInfo, Integer> dao = helper.getDao(GunplaInfo.class);

            TransactionManager.callInTransaction(
                    dao.getConnectionSource(), new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    dao.createOrUpdate(gunplaInfoEntity);

                    return null;
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 指定したタグIDに一致するガンプラデータを取得する。
     *
     * @param tagId 指定するタグID
     * @return 指定したタグIDに一致するガンプラデータ、一致しない場合はnullを返す
     */
    public GunplaInfo findGunplaInfoByTagId(String tagId) {
        List<GunplaInfo> infos = null;
        try {
            Dao<GunplaInfo, Integer> dao = helper.getDao(GunplaInfo.class);
            infos = dao.queryForEq(GunplaInfo.FIELD_TAG_ID, tagId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return infos == null ? null : infos.get(0);
    }

    /**
     * NFCタグに書き込むタグIDを作成する。
     * @return 現在時刻をSHA-256でハッシュ化したものの文字列
     */
    public String makeInitialTagId() {
        // SHA-256で現在時刻のハッシュを作成
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        long now = Calendar.getInstance().getTimeInMillis();
        byte[] digestArray = digest.digest(String.valueOf(now).getBytes());

        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < digestArray.length; i++) {
            // 符号を反転して１６進数に変換
            String tmpHex = Integer.toHexString(digestArray[i] & 0xff);

            // １桁だけの時は、先頭に「0」を付加
            if (tmpHex.length() == 1) {
                buffer.append('0').append(tmpHex);
            } else {
                buffer.append(tmpHex);
            }
        }

        return buffer.toString();
    }
}
