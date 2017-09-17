package codebuilder.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import codebuilder.App;
import codebuilder.service.SchemeService;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * 作者：任冠宇
 * 时间：2017/9/16
 */
public class SelectSchemeController implements Initializable{

    public App app;
    public TreeView<String> schemeTree;
    private String path = "scheme";
    private SchemeService schemeService = new SchemeService();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //获取方案目录
        getSchemeTree();
    }

    private void getSchemeTree() {

        String[] schemeArray = schemeService.getSchemeList(path);
        System.out.println(Arrays.toString(schemeArray));
        final TreeItem<String> treeRoot = new TreeItem<String>("方案目录");
        for(String scheme:schemeArray){
            treeRoot.getChildren().addAll(Arrays.asList(new TreeItem<String>(scheme)));
        }
        schemeTree.setShowRoot(true);
        schemeTree.setRoot(treeRoot);
        treeRoot.setExpanded(true);
    }

    public void openDict(ActionEvent actionEvent) {
        try {
            String schemeAbPath = schemeService.getSchemeAbPath(path);
            Runtime.getRuntime().exec("explorer.exe "+schemeAbPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void flushDict(ActionEvent actionEvent) {
        getSchemeTree();
    }
}
