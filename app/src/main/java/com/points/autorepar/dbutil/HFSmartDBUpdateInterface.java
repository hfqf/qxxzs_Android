package com.points.autorepar.dbutil;

import com.points.autorepar.dbutil.bean.HFLocalDBSettingModel;

/**
 * 数据库升级关键回调接口类
 */
public interface HFSmartDBUpdateInterface {
    public boolean onNeedUpdate(HFLocalDBSettingModel newDBSetModel);
}
