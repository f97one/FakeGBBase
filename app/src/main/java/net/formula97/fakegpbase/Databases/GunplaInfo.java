package net.formula97.fakegpbase.Databases;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * ガンプラ情報を保持するDTO。<br />
 * Created by f97one on 14/11/02.
 */
@DatabaseTable(tableName = "GunplaInfo")
public class GunplaInfo {

    /**
     * テーブル名<br />
     * クラスのシンプルネームに同じ。
     */
    public static final String TABLE_NAME = GunplaInfo.class.getSimpleName();
    /**
     * フィールド:_id<br />
     * AndroidのAPIが要求する場合があるため、用意している一貫番号。
     */
    public static final String FIELD__ID = "_id";
    /**
     * フィールド:tagId<br />
     * NFCタグに書き込んでいる一意のID、NFCタグを使用していない場合は、先頭に「NOTAG_」が付加されている。
     */
    public static final String FIELD_TAG_ID = "tagId";
    /**
     * フィールド:gunplaName<br />
     * ガンプラの名称。
     */
    public static final String FIELD_GUNPLA_NAME = "gunpla_name";
    /**
     * フィールド:scaleValue<br />
     * ガンプラのスケール。
     */
    public static final String FIELD_SCALE_VALUE = "scaleValue";
    /**
     * フィールド:classValue<br />
     * ガンプラのグレード（MG、HGなど）。
     */
    public static final String FIELD_CLASS_VALUE = "classValue";
    /**
     * フィールド:scratchBuiltLevel<BR />
     * 改造、またはスクラッチビルドの程度を表す、0=改造なし、1=一部、2=すべて。
     */
    public static final String FIELD_SCRATCH_BUILT_LEVEL = "scratchBuiltLevel";
    /**
     * フィールド:modelNo<br />
     * ガンプラの型式。
     */
    public static final String FIELD_MODEL_NO = "modelNo";
    /**
     * フィールド:builderName<br />
     * ガンプラのビルダー名。
     */
    public static final String FIELD_BUILDER_NAME = "builderName";
    /**
     * フィールド:fighterName<br />
     * ガンプラのファイター名。
     */
    public static final String FIELD_FIGHTER_NAME = "fighterName";

    @DatabaseField(generatedId = true, columnName = FIELD__ID)
    private Integer _id;
    @DatabaseField(unique = true, columnName = FIELD_TAG_ID)
    private String tagId;
    @DatabaseField(columnName = FIELD_GUNPLA_NAME)
    private String gunplaName;
    @DatabaseField(columnName = FIELD_SCALE_VALUE)
    private String scaleValue;
    @DatabaseField(columnName = FIELD_CLASS_VALUE)
    private String classValue;
    @DatabaseField(defaultValue = "0", columnName = FIELD_SCRATCH_BUILT_LEVEL)
    private int scratchBuiltLevel;
    @DatabaseField(columnName = FIELD_MODEL_NO)
    private String modelNo;
    @DatabaseField(columnName = FIELD_BUILDER_NAME)
    private String builderName;
    @DatabaseField(columnName = FIELD_FIGHTER_NAME)
    private String fighterName;

    /**
     * デフォルトコンストラクタ。<br />
     * 依存ライブラリが必要とするため、実装は空としている。
     */
    public GunplaInfo() { }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getGunplaName() {
        return gunplaName;
    }

    public void setGunplaName(String gunplaName) {
        this.gunplaName = gunplaName;
    }

    public String getScaleValue() {
        return scaleValue;
    }

    public void setScaleValue(String scaleValue) {
        this.scaleValue = scaleValue;
    }

    public String getClassValue() {
        return classValue;
    }

    public void setClassValue(String classValue) {
        this.classValue = classValue;
    }

    public int getScratchBuiltLevel() {
        return scratchBuiltLevel;
    }

    public void setScratchBuiltLevel(int scratchBuiltLevel) {
        this.scratchBuiltLevel = scratchBuiltLevel;
    }

    public String getModelNo() {
        return modelNo;
    }

    public void setModelNo(String modelNo) {
        this.modelNo = modelNo;
    }

    public String getBuilderName() {
        return builderName;
    }

    public void setBuilderName(String builderName) {
        this.builderName = builderName;
    }

    public String getFighterName() {
        return fighterName;
    }

    public void setFighterName(String fighterName) {
        this.fighterName = fighterName;
    }

}
