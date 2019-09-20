package org.noear.solonjt;

import org.noear.solonjt.dso.DbApi;

public class Config {
    public static final String code="solonjt";
    public static final String code_db="solonjt.db";
    public static final String code_center="solonjt.center";
    public static final String code_node="solonjt.node";

    public static final String code_ext="jt_ext";

    public static final String frm_root_img = DbApi.cfgGet("_frm_root_img","/img/");


    public static final String filter_file="filter.file";
    public static final String filter_path="filter.path";

}
