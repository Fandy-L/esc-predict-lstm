package com.ecsgroup.entity;

import com.ecsgroup.service.dataTool.DataPreprocess;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linfang
 */
public class InputContent {

	public int flavorCont;    //输入虚拟机个数
	public int preDays;       //预测天数
	public int delayDays;     //预测的时间与训练集最后一天的间隔
	
	public String[] fNameArray;  //虚拟机名称数组
	public int[] fCpuArray;      //虚拟机CPU数组
	public int[] fMemArray;      //虚拟机MEM数组
	
	public List<Flavor> inputFList; //输入虚拟主机集合

	public InputContent(String[] inputContent){

		this.flavorCont = Integer.parseInt(inputContent[2]); //得到预测虚拟机个数
		this.fNameArray = new String[flavorCont];
		this.fCpuArray = new int[flavorCont];
		this.fMemArray = new int[flavorCont];
		for(int i=0; i<flavorCont; i++){                    //将虚拟机的名称、CPU、MEM对应放入数组
			String[] str = inputContent[3+i].split(" ");
			this.fNameArray[i] = str[0];
			this.fCpuArray[i] = Integer.parseInt(str[1]);
			this.fMemArray[i] = Integer.parseInt(str[2])/1024;
		}

		String preStartDate = inputContent[flavorCont+6];
		String preEndDate = inputContent[flavorCont+7];

		this.preDays = DataPreprocess.getDaysBetween(preStartDate, preEndDate,"yyyy-MM-dd HH:mm:ss"); //获取预测天数

			this.inputFList = new ArrayList<Flavor>();
			for(int i=0; i<flavorCont; i++){
				Flavor flavor = new Flavor(this.fNameArray[i], this.fCpuArray[i], this.fMemArray[i]);
				this.inputFList.add(flavor);
		}
	}
}
