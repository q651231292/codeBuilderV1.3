package codebuilder.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import codebuilder.App;
import codebuilder.util.FileUtil;

/**
 * 作者：任冠宇 时间：2017/9/16
 */
public class SchemeService {

	// 提取文件中的标记
	private Set<String> getMarks(String data) {
		Set<String> set = new TreeSet<>();
		String regex = "\\$\\{(.*?)\\}";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(data); // 获取 matcher 对象
		while (m.find()) {
			String mark = m.group(1);
			set.add(mark);
		}
		return set;
	}

	public String[] getRootChildrenDict() {
		File file = new File(App.path);
		String[] list = file.list();
		return list;
	}

	public void openDict() {
		File file = new File(App.path);
		String abPath = file.getAbsolutePath();
		try {
			Runtime.getRuntime().exec("explorer.exe " + abPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 判断是否存在方案目录
	public boolean existScheme() {
		File file = new File(App.path);
		return file.exists();
	}

	// 获取方案里面的标记
	public Set<String> getSchemeList(String item) {
		String scheme = App.path + "/" + item;
		File file = new File(scheme);
		String[] list = file.list();
		Set<String> markAll = new TreeSet<>();
		for (String f : list) {
			String data = getFileContent(scheme + "/" + f);
			// 提取模板数据中所有的标记
			Set<String> markSet = getMarks(data);
			// 合并set
			markAll.addAll(markSet);
		}
		return markAll;
	}

	// 获取文件的里面的数据
	public String getFileContent(String f) {
		String str = "";
		StringBuilder result = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));// 构造一个BufferedReader类来读取文件
			String s = null;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				result.append(System.lineSeparator() + s);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		str = result.toString();
		return str;
	}

	public List<String> getSchemeFilePathList(String schemeName) {
		List<String> abPathList = new ArrayList<>();
		File file = new File(App.path + File.separator + schemeName);
		File[] listFiles = file.listFiles();
		for (File f : listFiles) {
				String abPath = f.getAbsolutePath();
				abPathList.add(abPath);
			}

		return abPathList;

	}

	public List<String> getFileData(List<String> fileAbPathList) {
		List<String> fileDataList = new ArrayList<>();

		fileAbPathList.forEach(abPath -> {
			StringBuilder result = new StringBuilder();
			try (BufferedReader br = new BufferedReader(new FileReader(abPath));) {
				String s = null;

				while ((s = br.readLine()) != null) {
					result.append(System.lineSeparator() + s);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			fileDataList.add(result.toString());
		});
		return fileDataList;
	}

	public List<String> replaceFileData(List<String> fileDataList) {

		return null;
	}

	public List<String> replaceFileData(List<String> fileDataList, Map<String, List<String>> map) {
		List<String> rs = new ArrayList<>();
		//所有的key
		List<String> ks = map.get("ks");
		//所有的value
		List<String> vs = map.get("vs");
		//替换文件数据
		for (int i = 0; i < fileDataList.size(); i++) {
			String fileData = fileDataList.get(i);
			for(int j=0;j<ks.size();j++){
				String k = ks.get(j);
				String v = vs.get(j);
				fileData = fileData.replaceAll("\\$\\{("+k+")\\}", v);
			}
			rs.add(fileData);
		}
		return rs;
	}

	public List<String> getSchemeFileNameList(String name) {
		List<String> rs = new ArrayList<>();
		File file = new File(App.path + File.separator + name);
		String suffix = "txt";
		String[] fs = file.list();
		for(String f:fs){
			rs.add(f.substring(0, f.indexOf(suffix)-1));
		}
		return rs;
	}

	public boolean outFileData(List<String> fileNameList, List<String> fileDataList, Map<String, List<String>> map) {
		boolean status = false;
		List<String> list = map.get("vs");
		String prefix = list.get(1);
		for (int i = 0; i < fileNameList.size(); i++) {
			String outPath = list.get(0);
			String fn = fileNameList.get(i);
			String fd = fileDataList.get(i);
			outPath = getFileOutPath(fd,outPath);
			status = FileUtil.write(outPath, prefix+fn, fd);
		}
		return status;
	}

    private String getFileOutPath(String fileData, String outPath) {
        Pattern p = Pattern.compile("package(.*);");
        Matcher m = p.matcher(fileData); // 获取 matcher 对象
        while(m.find()) {
            String name = m.group(1);
            outPath = outPath+toFileSysPath(name);//把.转换成/
        }
        return outPath;
    }
    //把包名转换成操作系统的路径
    private String toFileSysPath(String packageName) {
        packageName = packageName.trim();
        packageName = packageName.replace(".",File.separator);
        packageName = File.separator+packageName;
        return packageName;
    }

}
