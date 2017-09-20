package com.rgy.codebuilder.controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

/**
 * 作者：任冠宇 时间：2017/9/16
 */
public class SelectSchemeController implements Initializable {

	//app
	private App app;
	// 服务层
	private SchemeService service = new SchemeService();
	// 左侧树
	@FXML
	TreeView<String> schemeTree;
	// a0面板
	@FXML
	AnchorPane a0;
	// a1面板
	@FXML
	AnchorPane a1;

	public void setApp(App app) {
		this.app = app;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//判断是否存在方案目录，第一次启动时方案目录是不存在的，所以需要创建
		// 判断是否存在方案目录
		boolean status = service.existScheme();
		// 如果存在
		if (status == true) {
			// 获取根目录的子文件夹
			String[] schemeArray = service.getRootChildrenDict();
			final TreeItem<String> treeRoot = new TreeItem<String>("方案目录");
			for (String scheme : schemeArray) {
				TreeItem<String> item = new TreeItem<String>(scheme);
				treeRoot.getChildren().addAll(Arrays.asList(item));
			}
			// 当点击方案的时候，联动出模板数据
			schemeTree.addEventHandler(MouseEvent.MOUSE_CLICKED, (evt) -> showTemplate());
			schemeTree.setShowRoot(true);
			schemeTree.setRoot(treeRoot);
			schemeTree.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
				@Override
				public TreeCell<String> call(TreeView<String> param) {
					TreeCell cell = new TreeCell();
//					cell.setText("aaa");
					return cell;
				}
			});
			treeRoot.setExpanded(true);
		}
		// 如果不存在
		else {
			// 创建目录
			File file = new File(App.path);
			boolean mkDirs = file.mkdirs();
			if (mkDirs == true) {
				// 重新获取方案目录
				getSchemeTree();
			}
		}
		// 获取方案目录
		getSchemeTree();
	}

	// 获取方案目录
	private void getSchemeTree() {


	}

	// 显示模板中的参数
	private void showTemplate() {
		MultipleSelectionModel<TreeItem<String>> selectionModel = schemeTree.getSelectionModel();
		String item = selectionModel.getSelectedItem().getValue();
		if (!"方案目录".equals(item)) {
			// 获取方案中的文件
			Set<String> tempLateList = service.getSchemeList(item);

			// 在右侧显示出方案中文件，已KEY VALUE的形式出现
			ObservableList<Node> nodeList = a1.getChildren();
			a1.getChildren().removeAll(nodeList);
			double topPlus = 30.0;

			Label label = new Label();
			label.setText(item);
			AnchorPane.setTopAnchor(label, -30.0 + topPlus);
			AnchorPane.setLeftAnchor(label, 10.0);
			a1.getChildren().add(label);

			for (String mark : tempLateList) {
				// 在界面上创建这些标签，用于输入参数
				label = new Label();
				label.setText(mark);
				label.prefHeight(20.0);
				label.getStyleClass().add("argLabel");
				AnchorPane.setTopAnchor(label, 0.0 + topPlus);
				AnchorPane.setLeftAnchor(label, 10.0);
				a1.getChildren().add(label);

				TextField text = new TextField("A");
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

	// 打开目录
	public void openDict(ActionEvent actionEvent) {
		service.openDict();
	}

	// 刷新目录
	public void flushDict(ActionEvent actionEvent) {
		getSchemeTree();
	}

	// 生成代码
	@FXML
	public void createCode() {
		// 准备数据
		// 获取页面标签输入的参数
		Map<String, List<String>> map = getLabelAndValue();
		// 获取选中的方案名称
		String name = schemeTree.getSelectionModel().getSelectedItem().getValue();
		// 获取方案里面的文件路径
		List<String> fileAbPathList = service.getSchemeFilePathList(name);
		// 获取方案里面的文件名
		List<String> fileNameList = service.getSchemeFileNameList(name);
		// 获取文件里的数据
		List<String> fileDataList = service.getFileData(fileAbPathList);
		// 替换文件的数据
		fileDataList = service.replaceFileData(fileDataList, map);
		// 输出文件的数据
		boolean status = service.outFileData(fileNameList, fileDataList, map);
		if (status) {
			AlertUtil.show("成功");
		} else {
			AlertUtil.show("失败");
		}

	}

	// 得到页面上的标签和值
	public Map<String, List<String>> getLabelAndValue() {
		Map<String, List<String>> result = new HashMap<>();
		Set<Node> argsLabel = a0.lookupAll(".argLabel");
		List<String> ks = new ArrayList<>();
		argsLabel.forEach(node -> {
			Label l = (Label) node;
			String text = l.getText();
			ks.add(text);
		});
		result.put("ks", ks);
		Set<Node> argsValue = a0.lookupAll(".argValue");
		List<String> vs = new ArrayList<>();
		argsValue.forEach(node -> {
			TextField tf = (TextField) node;
			String text = tf.getText();
			vs.add(text);
		});
		result.put("vs", vs);
		return result;

	}

	public Map<String, List<String>> getA1Args(List<String> labelList, List<String> textFieldList) {
		Map<String, List<String>> result = new HashMap<>();
		Set<Node> lookupAll;
		// a1
		lookupAll = a1.lookupAll("Label");
		lookupAll.forEach(node -> {
			Label l = (Label) node;
			String lText = l.getText();
			labelList.add(lText);
		});
		labelList.remove(0);
		lookupAll = a1.lookupAll("TextField");
		lookupAll.forEach(node -> {
			TextField tf = (TextField) node;
			String tfText = tf.getText();
			textFieldList.add(tfText);
		});
		result.put("labels", labelList);
		result.put("values", textFieldList);
		return result;
	}

	public Map<String, List<String>> getA0Args(List<String> labelList, List<String> textFieldList) {
		Map<String, List<String>> result = new HashMap<>();
		// a0
		Set<Node> lookupAll = a0.lookupAll("Label");
		lookupAll.forEach(node -> {
			Label l = (Label) node;
			String lText = l.getText();
			labelList.add(lText);
		});
		labelList.remove(0);
		lookupAll = a0.lookupAll("TextField");
		lookupAll.forEach(node -> {
			TextField tf = (TextField) node;
			String tfText = tf.getText();
			textFieldList.add(tfText);
		});
		result.put("labels", labelList);
		result.put("values", textFieldList);
		return result;
	}
}
