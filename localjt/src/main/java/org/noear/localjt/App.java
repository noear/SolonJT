package org.noear.localjt;

import org.noear.solon.core.XMap;
import org.noear.solonjt.SolonJT;
import org.noear.weed.WeedConfig;

public class App {
    public static void main(String[] args) {


        WeedConfig.onExecuteBef((cmd) -> {
            System.out.println(cmd.text);
            System.out.println(cmd.paramMap());
            return true;
        });


        SolonJT.start(App.class, args);

        XMap xarg = XMap.from(args);
        int port = xarg.getInt("server.port");
        if (port < 80) {
            port = 8080;
        }

        if (java.awt.Desktop.isDesktopSupported()) {
            try {
                // 创建一个URI实例
                java.net.URI uri = java.net.URI.create("http://localhost:" + port);
                // 获取当前系统桌面扩展
                java.awt.Desktop dp = java.awt.Desktop.getDesktop();
                // 判断系统桌面是否支持要执行的功能
                if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
                    // 获取系统默认浏览器打开链接
                    dp.browse(uri);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
