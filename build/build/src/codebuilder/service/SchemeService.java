package codebuilder.service;

import java.io.File;

/**
 * 作者：任冠宇
 * 时间：2017/9/16
 */
public class SchemeService {

    public String[] getSchemeList(String path) {
        File file = new File(path);
        String[] list = file.list();
        return list;
    }

    public String getSchemeAbPath(String path) {
        File file = new File(path);
        String abPath = file.getAbsolutePath();
        return abPath;
    }
}
