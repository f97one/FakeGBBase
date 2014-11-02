package net.formula97.fakegpbase.Databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import net.formula97.fakegpbase.AppConst;

import java.sql.SQLException;

/**
 * Created by f97one on 14/11/02.
 */
public class DbHelper extends OrmLiteSqliteOpenHelper {

    private static DbHelper helper;

    private DbHelper(Context context) {
        super(context, AppConst.DATABASE_NAME, null, AppConst.DATABASE_VERSION);
    }

    public static DbHelper getDatabase(Context context) {
        if (helper == null) {
            helper = new DbHelper(context);
        }

        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, GunplaInfo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }
}
