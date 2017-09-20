package com.rgy.codebuilder.controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import com.rgy.codebuilder.App;
import com.rgy.codebuilder.service.SchemeService;
import com.rgy.codebuilder.util.AlertUtil;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/**
 * @author 任冠宇
 * @date 创建时间：2017年9月20日
 */
public class SelectSchemeController extends Ctrl implements Initializable {

	// 服务层
	private SchemeService service = new SchemeService();
	// 左侧树
	@FXML
	TreeView<String> tree;
	// a0面板
	@FXML
	AnchorPane a0;
	// a1面板
	@FXML
	AnchorPane a1;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// 初始化方案树
		initSchemeTree();
	}

	private void initSchemeTree() {
		//判断是否存在方案目录，第一次启动时方案目录是不存在的，所以需要创建
		// 创建日志文件夹
		service.createLogDict();
		// 判断是否存在方案目录
		boolean exist = service.existScheme(App.schemeDict);
		// 如果存在
		if (exist) {
			// 获取方案目录的子文件夹
			String[] chilDictList = service.getSchemeChilDict(App.schemeDict);
			//设置左侧树根目录的名称,名称为方案目录
			TreeItem<String> treeRoot = new TreeItem<String>("方案目录");
			//设置左侧树根目录的子节点,把方案目录的子目录设置为子节点
			for (String chilDict : chilDictList) {
				TreeItem<String> item = new TreeItem<String>(chilDict);
				treeRoot.getChildren().add(item);
			}
			//设置节点是否展开
			treeRoot.setExpanded(true);
			// 当点击方案的时候，联动出模板数据
			tree.addEventHandler(MouseEvent.MOUSE_CLICKED, (evt) -> showSchemeArgs());
			//显示根节点
			tree.setShowRoot(true);
			//设置树的根节点
			tree.setRoot(treeRoot);
		}
		// 如果不存在
		else {
			// 创建目录
			File file = new File(App.schemeDict);
			boolean success = file.mkdirs();
			if (success) {
				// 重新获取方案目录
				initSchemeTree();
			}
		}

	}

	// 显示模板中的参数
	private void showSchemeArgs() {
		MultipleSelectionModel<TreeItem<String>> selectionModel = tree.getSelectionModel();
		TreeItem<String> item = selectionModel.getSelectedItem();
		if(item!=null){
			String name = item.getValue();
			if (!"方案目录".equals(name)) {
				// 输入方案名称,获取方案中的标记,用于在页面上显示
				Set<String> markList = service.getSchemeMarkList(name);
				// 在右侧显示出方案中文件，已KEY VALUE的形式出现
				ObservableList<Node> nodeList = a1.getChildren();
				a1.getChildren().removeAll(nodeList);
				double topPlus = 30.0;

				Label label = new Label();
				label.setText(name);
				AnchorPane.setTopAnchor(label, -30.0 + topPlus);
				AnchorPane.setLeftAnchor(label, 10.0);
				a1.getChildren().add(label);

				for (String mark : markList) {
					// 在界面上创建这些标签，用于输入参数
					label = new Label();
					label.setText(mark);
					label.prefHeight(20.0);
					label.getStyleClass().add("argLabel");
					AnchorPane.setTopAnchor(label, 0.0 + topPlus);
					AnchorPane.setLeftAnchor(label, 10.0);
					a1.getChildren().add(label);

					TextField text = new TextField("");
					text.prefHeight(20.0);
					text.getStyleClass().add("argValue");
					AnchorPane.setTopAnchor(text, 0.0 + topPlus);
					AnchorPane.setLeftAnchor(text, 100.0);
					AnchorPane.setRightAnchor(text, 10.0);
					a1.getChildren().add(text);
					topPlus += 30;

				}
			}
		}

	}

	// 打开目录
	public void openDict(ActionEvent actionEvent) {
		service.openDict(App.schemeDict);
	}

	// 刷新目录
	public void flushDict(ActionEvent actionEvent) {
		initSchemeTree();
	}

	// 生成代码
	@FXML
	public void createCode() {
		// 准备数据
		// 获取页面标签输入的参数
		Map<String, List<String>> map = getLabelAndValue();
		// 获取选中的方案名称
		MultipleSelectionModel<TreeItem<String>> selectionModel = tree.getSelectionModel();
		if(selectionModel!=null){
			TreeItem<String> item = selectionModel.getSelectedItem();
			if(item!=null){
				String name = item.getValue();
				// 获取方案里面的文件路径
				List<String> fileAbPathList = service.getSchemeFilePathList(name);
				// 获取方案里面的文件名
				List<String> fileNameList = service.getSchemeFileNameList(name);
				// 获取文件里的数据
				List<String> fileDataList = service.getFileData(fileAbPathList);
				// 替换文件的数据
				fileDataList = service.replaceFileData(fileDataList, map);
				// 输出文件的数据
				boolean success = service.outFileData(fileNameList, fileDataList, map);
				if (success) {
					AlertUtil.show("成功");
				} else {
					AlertUtil.show("失败");
				}
			}

		}



	}

	// 得到页面上的标签和值
	public Map<String, List<String>> getLabelAndValue() {
		Map<String, List<String>> result = new HashMap<>();
		//抓取key标签
		Set<Node> argsLabel = a0.lookupAll(".argLabel");
		List<String> ks = new ArrayList<>();
		argsLabel.forEach(node -> {
			Label l = (Label) node;
			String text = l.getText();
			ks.add(text);
		});
		result.put("ks", ks);
		//抓取value标签
		Set<Node> argsValue = a0.lookupAll(".argValue");
		List<String> vs = new ArrayList<>();
		argsValue.forEach(node -> {
			TextField tf = (TextField) node;
			String text = tf.getText();
			vs.add(text);
		});
		result.put("vs", vs);
		//返回
		return result;

	}
}
