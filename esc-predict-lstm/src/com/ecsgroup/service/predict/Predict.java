package com.ecsgroup.service.predict;

import com.ecsgroup.entity.EcsContent;
import com.ecsgroup.entity.InputContent;
import com.ecsgroup.service.dataTool.DataMath;
import com.ecsgroup.service.dataTool.DataPreprocess;
import com.ecsgroup.service.dataTool.GetTest;

import java.util.List;


public class Predict {
	
	/**
	 * 预测给定日期的虚拟机
	 * @param ecsContent 历史云服务申请记录 训练数据
	 * @param inputContent 服务器参数、要预测的虚拟机规格、资源利用率指标、预测日期
	 * @return results 虚拟机规格数量以及每个种类具体数量
	 */
	public static String[] predictVm(String[] ecsContent, String[] inputContent){
                                                                    
		//处理input数据，获取虚拟机参数以及预测天数
		InputContent input = new InputContent(inputContent);
		int fNum = input.flavorCont;
		int preDays = input.preDays;
		String[] fNameArray = input.fNameArray;

		//处理训练集成统一格式，获取input数据集中每种虚拟机每天出现的个数
		EcsContent ecs = new EcsContent(ecsContent, fNameArray);
		List<double[]> flavorSeriesList = ecs.flavorSeriesList;
		
		/*计算预测开始时间与训练结束时间的时间间隔*/
		String trainEndDate = ecs.content[ecs.content.length-1].split(" ")[1];
		String preStartDate = inputContent[inputContent.length-2].split(" ")[0];
		int IntervalDays = DataPreprocess.getDaysBetween(trainEndDate, preStartDate, "yyyy-MM-dd")-1;
		 
		List<double[]> testflavorList = GetTest.GetTestSeriesList(fNameArray);//获取测试数据每个规格虚拟机的总量
		double[] tfArray = new double[fNum];
		for(int i=0; i<fNum; i++){
			tfArray[i] = DataMath.sumData(testflavorList.get(i));
		}
		int[] fdataArray = new int[fNum]; //表示每种虚拟机最后预测的总数
		
		for(int i=0 ; i<fNum ; i++){      //预测是按照fNameArray的顺序来预测每个虚拟机的预测结果
			
			double[] orginalArray = flavorSeriesList.get(i);
			double[] detectArray = DataPreprocess.outlierDetect(orginalArray);  //首先对原始数据进行降噪
			int fpNum = 0;                                                  //最终预测结果

			int week = 1;
			int hDim = 1;                 //神经网络输出维数
			int xDim = 4;                 //神经网络输入维数
			int maxEpoch = 5000;           //最大迭代次数
			double lr = 0.01;             //初始步长
			double minLoss = 0.3;          //最小损失值
			
			int pWeek = (int)Math.ceil((preDays+IntervalDays+1)/week);
			
			double[] predictArray = LstmPredict.GetPredictArray(detectArray,
										pWeek, hDim, xDim, maxEpoch, lr, minLoss,week);	//LSTM模型获取预测数据
							
			double sum = DataMath.sumData(DataMath.filterNegative(predictArray));  //将预测数据经过去负值后计算总和

			if(fpNum<0){
				fpNum = 0;
			}else{
				fpNum = (int) Math.round(sum);
			}
			fdataArray[i] = fpNum;   // 将计算的结果加入最后预测结果中
			double accuracy = 0;     //计算精度
			int trueNum =  (int)tfArray[i];
			if(trueNum == fpNum){
				accuracy = 1;
			}else if(trueNum > fpNum){
				accuracy = (double)fpNum/tfArray[i];
			}else{
				accuracy = (double)tfArray[i]/fpNum;
			}
			System.out.println(fNameArray[i]+"的实际值："+(int)tfArray[i]+",预测值："+fpNum+",精度："+accuracy); //输出flavori最终预测精度

		}
		
		double acc = GetTest.GetPredictAccuracy(fdataArray, tfArray);
		System.out.println("预测总量"+DataMath.sumIntData(fdataArray)+"实际总量"+DataMath.sumData(tfArray));
		System.out.println("预测的虚拟机总精度："+acc);//输出打分公式计算的总预测精度
		String[] predictResult = GetPredictResult(fNameArray, fdataArray);
		return predictResult;    //返回输出结果
	}
	
	/**
	 * 将预测结果转换为字符串
	 * @param fNameArray
	 * @param fDataArray
	 * @return
	 */
	public static String[] GetPredictResult(String[] fNameArray, int[] fDataArray){
		int fNum = fNameArray.length;
		int sumFlavor=0;               //将处理后的数据按题目要求封装到字符串数组中
		for(int i=0 ; i<fNum ; i++){   //计算总的虚拟机个数
			sumFlavor += fDataArray[i];
		}
		String[] predictResult = new String[fNum+2];  //存放虚拟机最终预测结果字符串
		predictResult[0]=Integer.toString(sumFlavor);
		for(int i=0 ; i<fNum ; i++){
			
			predictResult[i+1] = fNameArray[i]+" "+Integer.toString(fDataArray[i]);
			
		}
		predictResult[fNum+1]="";
		
		return predictResult;
	}
}
