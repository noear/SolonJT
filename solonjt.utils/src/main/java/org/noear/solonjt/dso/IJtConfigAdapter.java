package org.noear.solonjt.dso;

import org.noear.solonjt.model.AConfigModel;

public interface IJtConfigAdapter {
    AConfigModel cfgGet(String name) throws Exception;
}
