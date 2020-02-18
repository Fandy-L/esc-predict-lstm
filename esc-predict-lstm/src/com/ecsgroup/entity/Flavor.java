package com.ecsgroup.entity;

/**
 * @ProjectName ecsTest
 * @author linfang
 */
public class Flavor {

	public String name;
	public int mem;
	public int cpu;

	/**
	 * 构造函数
	 * @param name
	 * @param cpu
	 * @param mem
	 */
	public Flavor(String name, int cpu, int mem/*, int serverId*/){
		this.cpu = cpu;
		this.mem = mem;
		this.name = name;
		//this.serverId = serverId;
	}
	
}
