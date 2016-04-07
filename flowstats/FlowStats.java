package net.floodlightcontroller.flowstats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;

import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFStatisticsReply;
import org.openflow.protocol.OFStatisticsRequest;
import org.openflow.protocol.OFType;
import org.openflow.protocol.statistics.OFFlowStatisticsReply;
import org.openflow.protocol.statistics.OFFlowStatisticsRequest;
import org.openflow.protocol.statistics.OFPortStatisticsReply;
import org.openflow.protocol.statistics.OFPortStatisticsRequest;
import org.openflow.protocol.statistics.OFStatistics;
import org.openflow.protocol.statistics.OFStatisticsType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IListener.Command;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.ICMP;
import net.floodlightcontroller.packet.IPacket;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.TCP;
import net.floodlightcontroller.packet.UDP;
public class FlowStats extends TimerTask implements IFloodlightModule, IOFMessageListener,IGetFlowStatsServer{
	protected IFloodlightProviderService floodlightProvider;
	protected static int transactionIdSource=0x0;
	protected static Logger logger;
	List<FlowStorageStat>recentFlowInfo=new ArrayList<FlowStorageStat>();
    HashMap<IOFSwitch, List<FlowStorageStat>> switchStats=new HashMap<IOFSwitch, List<FlowStorageStat>>();
	@Override
	public String getName() {
		return "FlowStats";
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		return false;
		//return ((type == OFType.PACKET_IN && name.equals( "securitypolicy" )));
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		return false;
	}

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(
			IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
	
		if(msg.getType()==OFType.PACKET_IN){
			for(int i=0;i<10;i++){
			System.out.println("packet in message");
			}
			Ethernet eth =IFloodlightProviderService.bcStore.get(cntx,IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
			if(eth.getEtherType()==Ethernet.TYPE_IPv4){
				IPv4 ipv4 = (IPv4) eth.getPayload();
				if(ipv4.getProtocol()==IPv4.PROTOCOL_TCP) {
					TCP tcp = (TCP) ipv4.getPayload();
					int desPort = tcp.getDestinationPort();
					int srcPort = tcp.getSourcePort();
					if(!switchStats.containsKey(sw)){
						FlowStorageStat info = new FlowStorageStat();
						info.setSrcPort(srcPort);
						info.setDesPort(desPort);
						recentFlowInfo.add(info);
					}
					switchStats.put(sw, recentFlowInfo);
				}
			}
		}
		
		if(msg.getType()==OFType.STATS_REPLY)
		{

			OFStatisticsReply replys = (OFStatisticsReply)msg;
			
			boolean rflags=false;
			if(!switchStats.containsKey(sw))
			{
			   FlowStorageStat tempInfo=new FlowStorageStat();
			   for (OFStatistics flowStat : replys.getStatistics())
			   {	
				   OFFlowStatisticsReply tempStat=(OFFlowStatisticsReply)flowStat;
				   tempInfo.setNwSrc(tempStat.getMatch().getNetworkSource());
				   tempInfo.setNwDst(tempStat.getMatch().getNetworkDestination());
				   tempInfo.setByteNum(tempStat.getByteCount());
				   tempInfo.setPacketsNum(tempStat.getPacketCount());
				   tempInfo.setDifByteNum(0);
			       tempInfo.setDifPacketsNum(0);
				   tempInfo.setIfPairFlow(false);
				   recentFlowInfo.add(tempInfo);	   
			    }
			   switchStats.put(sw, recentFlowInfo);
			}
			else
			{
			
				for (OFStatistics flowStat : replys.getStatistics())
				{	
				   boolean lflag=false;
				   int index=0;
				   FlowStorageStat tempInfo=new FlowStorageStat();
				  
				   OFFlowStatisticsReply tempStat=(OFFlowStatisticsReply)flowStat;
				   tempInfo.setNwSrc(tempStat.getMatch().getNetworkSource());
				   tempInfo.setNwDst(tempStat.getMatch().getNetworkDestination());
				   tempInfo.setByteNum(tempStat.getByteCount());
				   tempInfo.setPacketsNum(tempStat.getPacketCount());
				   tempInfo.setDifByteNum(0);
			       tempInfo.setDifPacketsNum(0);
				   tempInfo.setIfPairFlow(false);
				
				   for(FlowStorageStat lookFlowInfo:switchStats.get(sw))
				   {
					   //找到对流
					   if((lookFlowInfo.getNwSrc()==tempInfo.getNwDst())&&(lookFlowInfo.getNwDst()==tempInfo.getNwSrc()))
					   {
						   tempInfo.setIfPairFlow(true);
						   lookFlowInfo.setIfPairFlow(true);
						   switchStats.get(sw).set(index,lookFlowInfo);
					   }
					   index++;
				   }
				   index=0;
				   for(FlowStorageStat lookFlowInfo:switchStats.get(sw))
				   {
					   //找到源目的地址相同的流
					   if((lookFlowInfo.getNwSrc()==tempInfo.getNwSrc())&&(lookFlowInfo.getNwDst()==tempInfo.getNwDst()))
					   {
						   tempInfo.setDifByteNum(tempStat.getByteCount()-lookFlowInfo.getByteNum());
						   tempInfo.setDifPacketsNum(tempStat.getPacketCount()-lookFlowInfo.getPacketsNum());
						   switchStats.get(sw).set(index,tempInfo);   
						   lflag=true;
					   }
					   
					  index++;
				   }
				   if(lflag==false)
				   {	 
					   switchStats.get(sw).add(tempInfo);
				   }
//				   System.out.println("流表的信息为:"+tempStat.toString());
//				   System.out.println("源IP"+tempInfo.getNwSrc()+"目的IP"+tempInfo.getNwDst());
//				   System.out.println("当前字节数:"+tempInfo.getByteNum()+"当前数据包数:"+tempInfo.getPacketsNum());
//				   System.out.println("字节数差:"+tempInfo.getDifByteNum()+"数据包数差:"+tempInfo.getDifPacketsNum());
//				   System.out.println("是否有对流:"+tempInfo.getIfPairFlow());
				   //System.out.println("目的端口:"+tempStat.);
				   System.out.println("************************************************");
				   
				 }
			}
			
		}
		return Command.CONTINUE;
    }


	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
	    l.add(IGetFlowStatsServer.class);
	    return l;
		    
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		Map<Class<? extends IFloodlightService>,IFloodlightService> l = new HashMap<Class<? extends IFloodlightService>,IFloodlightService>();
	    l.put(IGetFlowStatsServer.class,this);
	    return l;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		// TODO Auto-generated method stub
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
	    l.add(IGetFlowStatsServer.class);
	    return l;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		// TODO Auto-generated method stub
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class); 
		logger = LoggerFactory.getLogger(FlowStats.class);
	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		// TODO Auto-generated method stub
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		floodlightProvider.addOFMessageListener(OFType.STATS_REPLY, this);
		
	    Timer tempTimer=new Timer();
	    tempTimer.schedule(this, 1000, 5000);

	}
	
	
	public  void setSwitchRequest(IOFSwitch sw)
	{
		int requestLength;
		OFStatisticsRequest req = new OFStatisticsRequest();
        
        OFFlowStatisticsRequest specificReq = new OFFlowStatisticsRequest();
        OFMatch match = new OFMatch();
        match.setWildcards(0xffffffff);
        specificReq.setMatch(match);
        specificReq.setOutPort(OFPort.OFPP_NONE.getValue());
        specificReq.setTableId((byte) 0xff);
        req.setStatisticType(OFStatisticsType.FLOW);
        req.setStatistics(Collections.singletonList((OFStatistics)specificReq));
        req.setXid(transactionIdSource);
        requestLength=req.getLengthU();
        requestLength += specificReq.getLength();
        req.setLengthU(requestLength);
        try {
        	sw.sendStatsQuery(req, req.getXid(), this);
		} catch (Exception e) {
	        logger.error(e.getMessage());
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(!floodlightProvider.getSwitches().isEmpty())
		{
			for (IOFSwitch sw : floodlightProvider.getSwitches().values())
			{
				if(sw.getId()==1)
				{
				setSwitchRequest(sw);
				}
			}
			transactionIdSource++;
           
		}
		
	}
	public  HashMap<IOFSwitch, List<FlowStorageStat>> getFlowStats()
	{
		if(!switchStats.isEmpty())
		{
			return switchStats;
		}
		else
		{
			return null;
		}
	}
	 public static long byteToLong(byte[] b) { 
	        long s = 0; 
	        long s0 = b[0] & 0xff;// 最低位 
	        long s1 = b[1] & 0xff; 
	        long s2 = b[2] & 0xff; 
	        long s3 = b[3] & 0xff; 
	        long s4 = b[4] & 0xff;// 最低位 
	        long s5 = b[5] & 0xff; 
//	        long s6 = b[6] & 0xff; 
//	        long s7 = b[7] & 0xff; 
	 
	        // s0不变 
	        s1 <<= 8; 
	        s2 <<= 16; 
	        s3 <<= 24; 
	        s4 <<= 8 * 4; 
	        s5 <<= 8 * 5; 
//	        s6 <<= 8 * 6; 
//	        s7 <<= 8 * 7; 
	        s = s0 | s1 | s2 | s3 | s4 | s5; 
	        return s; 
	    } 
}
