/**
 * 
 */
package net.floodlightcontroller.flowstatistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketIn;
import org.openflow.protocol.OFType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.flowstats.FlowStorageStat;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.TCP;

/**
 * @author 浩
 * @version 创建时间：2016年3月25日  上午10:31:05
 */
public class FlowStatistics implements IFloodlightModule ,IOFMessageListener,IGetFlowStatistics{
	
	protected IFloodlightProviderService floodlightProvider;
	protected static int transactionId =0x0;
	protected static Logger logger;
	List<FlowStorageStat> flowInfo = new ArrayList<FlowStorageStat>();
	HashMap<IOFSwitch,List<FlowStorageStat>> switchStatistic = new HashMap<IOFSwitch,List<FlowStorageStat>>();
	
	@Override
	public String getName() {
		return "FlowStatistics";
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		return false;
	}

	@Override
	public HashMap<IOFSwitch, List<FlowStorageStat>> getFlowStatistics() {
		return null;
	}

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(IOFSwitch sw, OFMessage msg,
			FloodlightContext cntx) {
		if(msg.getType()==OFType.PACKET_IN){
			OFPacketIn pi = (OFPacketIn) msg;
		
			
			Ethernet eth =IFloodlightProviderService.bcStore.get(cntx,IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
			IPv4 ipv4 = (IPv4) eth.getPayload();
			int srcAdd = ipv4.getSourceAddress();
			int desAdd = ipv4.getDestinationAddress();
			TCP tcp = (TCP) ipv4.getPayload();
			int desPort = tcp.getDestinationPort();
			int srcPort = tcp.getSourcePort();
			if(!switchStatistic.containsKey(sw)){
				FlowStorageStat tempInfo = new FlowStorageStat();
				tempInfo.setNwSrc(srcAdd);
				tempInfo.setNwDst(desAdd);
				tempInfo.setDesPort(desPort);
				tempInfo.setSrcPort(srcPort);
				flowInfo.add(tempInfo);
			}
			switchStatistic.put(sw, flowInfo);
		}
		return Command.CONTINUE;
		
	
	
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IGetFlowStatistics.class);
		return l;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		Map<Class<? extends IFloodlightService>,IFloodlightService> l = new HashMap<Class<? extends IFloodlightService>,IFloodlightService>();
	    l.put(IGetFlowStatistics.class,this);
	    return l;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
	    l.add(IGetFlowStatistics.class);
	    return l;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class); 
		logger = LoggerFactory.getLogger(FlowStatistics.class);
		
	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		
	}
	

}
