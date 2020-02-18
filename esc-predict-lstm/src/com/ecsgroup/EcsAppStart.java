package com.ecsgroup;


import com.ecsgroup.service.predict.Predict;
import com.ecsgroup.utils.FileUtil;
import com.ecsgroup.utils.LogUtil;
import com.sun.org.apache.bcel.internal.util.ClassPath;

import java.text.MessageFormat;

/**
 * 
 * 工具入口
 * 
 * @version [版本号, 2017-12-8]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class EcsAppStart {
	
	public static void main(String[] args){
		//获取训练文件、输入文件、预测结果输出文件系统路径
		String classPath = EcsAppStart.class.getClassLoader().getResource("").getPath();
		classPath = classPath.substring(0,classPath.length()-4)+"src";
		String packagePath = "com/ecsgroup/data";
		String ecsDataPath = MessageFormat.format("{0}/{1}/TrainData.txt",classPath,packagePath);
		String inputFilePath =  MessageFormat.format("{0}/{1}/input.txt",classPath,packagePath);
		String resultFilePath =  MessageFormat.format("{0}/{1}/myresult.txt",classPath,packagePath);

		//开始执行程序并记时
		LogUtil.printLog("strat predict...");

		// 读取输入文件
		String[] ecsContent = FileUtil.read(ecsDataPath, null);
		String[] inputContent = FileUtil.read(inputFilePath, null);

		// 功能实现入口
		String[] resultContents = Predict.predictVm(ecsContent, inputContent);

		// 写入输出文件
		if (hasResults(resultContents)) {
			FileUtil.write(resultFilePath, resultContents, false);
		} else {
			FileUtil.write(resultFilePath, new String[] { "NA" }, false);
		}
		//程序执行结束，打印执行时间
		LogUtil.printLog("end predict...");
		
		
	}

	private static boolean hasResults(String[] resultContents) {
		if (resultContents == null) {
			return false;
		}
		//输出结果中不能有空白字符
		for (String contents : resultContents) {
			if (contents != null && !contents.trim().isEmpty()) {
				return true;
			}
		}
		return false;
	}

}
