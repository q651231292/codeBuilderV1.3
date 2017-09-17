package codebuilder.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import codebuilder.App;
import codebuilder.service.SchemeService;
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
import javafx.scene.layout.GridPane;

/**
 * 作者：任冠宇
 * 时间：2017/9/16
 */
public class SelectSchemeController implements Initializable{

    public App app;
    public TreeView<String> schemeTree;
    private String path = "scheme";
    private SchemeService schemeService = new SchemeService();
	@FXML AnchorPane a1;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //获取方案目录
        getSchemeTree();
    }

    private void getSchemeTree() {
    	//判断是否存在方案目录
    	boolean status = existScheme();
		//如果存在
    	if(status==true){
            String[] schemeArray = schemeService.getSchemeList(path);
            final TreeItem<String> treeRoot = new TreeItem<String>("方案目录");
            for(String scheme:schemeArray){
            	TreeItem<String> item = new TreeItem<String>(scheme);
                treeRoot.getChildren().addAll(Arrays.asList(item));
            }
          //当点击方案的时候，联动出模板数据
            schemeTree.addEventHandler(MouseEvent.MOUSE_CLICKED, (evt)->showTemplate());
            schemeTree.setShowRoot(true);
            schemeTree.setRoot(treeRoot);
            treeRoot.setExpanded(true);
    	}
    	//如果不存在
    	else{
    		//创建目录
    		File file = new File(path);
    		boolean mkDirs = file.mkdirs();
			if(mkDirs==true){
				//重新获取方案目录
				getSchemeTree();
			}
    	}
    }

    //显示模板中的参数
    private Object showTemplate() {
    	MultipleSelectionModel<TreeItem<String>> selectionModel = schemeTree.getSelectionModel();
    	String item = selectionModel.getSelectedItem().getValue();
		System.out.println(item);
		if(!"方案目录".equals(item)){
			//获取方案中的模板数据
			List<Map<String,Object>> tempLateList  = getTempLateList(item);
		}

		return null;
	}

	private List<Map<String, Object>> getTempLateList(String item) {
		String SchemeTemp = path+"/"+item;
		File file = new File(SchemeTemp);
		String absolutePath = file.getAbsolutePath();
		System.out.println(absolutePath);
		String[] list = file.list();
		System.out.println(Arrays.toString(list));
		Set<String> markAll = new TreeSet<>();
		for(String f:list){
			List<String> data = getData(SchemeTemp+"/"+f);
			//提取模板数据中所有的标记
			Set<String> markSet = getMarks(data);
			//合并set
			markAll.addAll(markSet);
		}
		System.out.println(markAll);


        ObservableList<Node> nodeList = a1.getChildren();
        a1.getChildren().removeAll(nodeList);
		double topPlus = 30.0;

		Label label = new Label();
		label.setText(item);
		AnchorPane.setTopAnchor(label, -30.0+topPlus);
		AnchorPane.setLeftAnchor(label, 10.0);
		a1.getChildren().add(label);


		for(String mark:markAll){
			//在界面上创建这些标签，用于输入参数
			label = new Label();
			label.setText(mark);
			label.prefHeight(20.0);
			AnchorPane.setTopAnchor(label, 0.0+topPlus);
			AnchorPane.setLeftAnchor(label, 10.0);
			a1.getChildren().add(label);

			TextField text = new TextField();
			text.prefHeight(20.0);
			AnchorPane.setTopAnchor(text, 0.0+topPlus);
			AnchorPane.setLeftAnchor(text, 100.0);
			AnchorPane.setRightAnchor(text, 10.0);
			a1.getChildren().add(text);
			topPlus+=30;


		}

		return null;
	}

	private Set<String> getMarks(List<String> data) {
		 Set<String> set = new TreeSet<>();
		 String regex = "\\$\\{(.*?)\\}";
		data.forEach(obj->{

	        Pattern p = Pattern.compile(regex);
	        Matcher m = p.matcher(obj); // 获取 matcher 对象
	        while(m.find()) {
	            String mark = m.group(1);
	            set.add(mark);
	        }
		});

		//System.out.println(set);
		return set;
	}

	private List<String> getData(String f) {
		File file = new File(f);
		//System.out.println(file.getAbsolutePath());
		List<String> list = new ArrayList<>();

		try (BufferedReader br = Files.newBufferedReader(Paths.get(f))) {

			//br returns as stream and convert it into a List
			list = br.lines().collect(Collectors.toList());

		} catch (IOException e) {
			e.printStackTrace();
		}

		//list.forEach(System.out::println);

		return list;
	}

	//判断是否存在方案目录
    private boolean existScheme() {
    	File file = new File(path);
		return file.exists();
	}

    //打开目录
	public void openDict(ActionEvent actionEvent) {
        try {
            String schemeAbPath = schemeService.getSchemeAbPath(path);
            Runtime.getRuntime().exec("explorer.exe "+schemeAbPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	//刷新目录
    public void flushDict(ActionEvent actionEvent) {
        getSchemeTree();
    }
}
