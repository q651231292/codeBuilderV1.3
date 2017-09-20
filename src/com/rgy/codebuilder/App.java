package com.rgy.codebuilder;

import java.io.IOException;

import com.rgy.codebuilder.controller.SelectSchemeController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * 作者：任冠宇 时间：2017/9/16
 */
public class App extends Application {

	// 方案目录
	public static String path = "scheme";

	// 主舞台
	private Stage stage;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		// 接管主舞台
		stage = primaryStage;
		selectSchemeTo();
		// 设置代码生成器的图标
		stage.getIcons().add(new Image("/img/hammer.png"));
		// 设置代码生成器的标题
		stage.setTitle("代码生成器");
		// 显示舞台
		stage.show();
	}

	// 用于在其他类中获取到主舞台
	public Stage getStage() {
		return stage;
	}

	/**
	 * 跳转到选择方案页
	 */
	private void selectSchemeTo() throws IOException {
		// 加载页面
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(App.class.getResource("/fxml/selectScheme.fxml"));
		Parent page = loader.load();
		Scene scene = new Scene(page);
		stage.setScene(scene);
		// 绑定控制器
		SelectSchemeController controller = loader.getController();
		controller.setApp(this);
	}
}
