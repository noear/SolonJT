package org.noear.thinkjt.extend.sited.dao.engine;

/**
 * Created by yuety on 15/8/2.
 */
public interface ISdViewModel {
    void loadByConfig(SdNode config);
    void loadByJson(SdNode config, String... jsons);
}
