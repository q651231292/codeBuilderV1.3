package com.rgy.codebuilder.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
/**
 * Created by Administrator on 2017/8/4.
 */
public class AlertUtil {

    public static void show(String text) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("消息提示框");
        alert.setHeaderText(text);
        alert.showAndWait();
    }
    public static void show(String text,String title) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(text);
        alert.showAndWait();
    }

}
