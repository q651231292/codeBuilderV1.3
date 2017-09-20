package com.rgy.codebuilder;

import java.io.IOException;
import com.rgy.codebuilder.controller.Ctrl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * @author 任冠宇
 * @date 创建时间：2017年9月20日
 */
public class App extends Application {

	// 方案目录
	public static String schemeDict = "scheme";

	// 主舞台
	private Stage stage;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage){
		// 接管主舞台
		stage = primaryStage;
		// 设置代码生成器的图标
		stage.getIcons().add(new Image("/img/hammer.png"));
		// 设置代码生成器的标题
		stage.setTitle("代码生成器");
		//设置舞台的场景,设置为选择
		replaceScene("/fxml/selectScheme.fxml");
		// 显示舞台
		stage.show();
	}

	// 用于在其他类中获取到主舞台
	public Stage getStage() {
		return stage;
	}

	/**
	 * 替换场景
	 * @param fxml 场景地址
	 */
	public void replaceScene(String fxml){
		// 加载页面
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(App.class.getResource(fxml));
		Parent page;
		try {
			page = loader.load();
			Scene scene = new Scene(page);
			stage.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 绑定控制器
		Ctrl ctrl = loader.getController();
		ctrl.setApp(this);
	}
}
