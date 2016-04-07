/**
 * 
 */
package net.floodlightcontroller.flowstatistics;

import java.util.HashMap;
import java.util.List;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.flowstats.FlowStorageStat;

/**
 * @author 浩
 * @version 创建时间：2016年3月25日  上午10:29:10
 */
public interface IGetFlowStatistics extends IFloodlightService{
	public HashMap<IOFSwitch, List<FlowStorageStat>> getFlowStatistics();
}
