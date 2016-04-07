package net.floodlightcontroller.flowstats;

import java.util.HashMap;
import java.util.List;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.IFloodlightService;

public interface IGetFlowStatsServer extends IFloodlightService{
	public  HashMap<IOFSwitch, List<FlowStorageStat>> getFlowStats();
	
}