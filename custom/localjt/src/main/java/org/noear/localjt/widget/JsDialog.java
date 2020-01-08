package org.noear.localjt.widget;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class JsDialog {
    public static void alert(String content) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(content);

        ButtonType okBtn = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(okBtn);

        alert.showAndWait();
        alert.hide();
    }

    public static boolean confirm(String content) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("确认");
        alert.setHeaderText(null);
        alert.setContentText(content);

        ButtonType okBtn = new ButtonType("确定", ButtonBar.ButtonData.YES);
        ButtonType cancelBtn = new ButtonType("取消", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(cancelBtn, okBtn);

        Optional<ButtonType> rst = alert.showAndWait();
        alert.hide();

        return (rst.get() == okBtn);
    }
}
