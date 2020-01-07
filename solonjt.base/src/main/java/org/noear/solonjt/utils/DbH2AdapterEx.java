package org.noear.solonjt.utils;

import org.noear.weed.wrap.DbH2Adapter;

public class DbH2AdapterEx extends DbH2Adapter {

    public static DbH2AdapterEx g = new DbH2AdapterEx();

    @Override
    public String preReview(String code) {
        if (code.indexOf("CREATE TABLE") < 0) {
            return code;
        } else {
            return code.replace("ENGINE=InnoDB ", "")
                    .replace("USING BTREE", "")
                    .replace("USING HASH", "")
                    .replace("([*]40)", "")
                    .replace("CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci ", "");

        }
    }
}
