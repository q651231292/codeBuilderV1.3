package codebuilder.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;


public class FileUtil {

    public static boolean write(String path, String fileName, String data) {
        fileName = fileName+".java";
        File file = new File(path);
        //如果文件夹不存在则创建
        if (!file.exists() && !file.isDirectory()) {
            boolean mkdirs = file.mkdirs(); // 创建多级目录
            if (mkdirs) {
                return write(path + "/" + fileName, data);
            }
        } else {
            return write(path + "/" + fileName, data);
        }
        return false;
    }

    public static boolean write(String path, String data) {
        try (PrintWriter pw = new PrintWriter(new File(path));) {
            pw.write(data);
            pw.flush();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
