package net.floodlightcontroller.ddosdetection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketIn;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFType;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;
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
import net.floodlightcontroller.flowstats.IGetFlowStatsServer;
import net.floodlightcontroller.packet.ARP;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.ICMP;
import net.floodlightcontroller.packet.IPacket;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.TCP;
import net.floodlightcontroller.packet.UDP;

public class DDosDetection implements IFloodlightModule, IOFMessageListener{
	
	protected IFloodlightProviderService floodlightProvider;
	protected IGetFlowStatsServer flowstatsProvider;
	protected static Logger logger;
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "DDosDetection";

	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		//return false;
		return (type == OFType.STATS_REPLY && name.equals( "FlowStats" ));
	}
 
	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(
			IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		// TODO Auto-generated method stub
		Ethernet eth =IFloodlightProviderService.bcStore.get(cntx,IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
//		IPacket pkt = (IPacket) eth.getPayload();
//		if (pkt instanceof ICMP)
//		System.out.println("����һ��ICMP��");
//		else if(pkt instanceof IPv4){
//			if (pkt instanceof TCP)
//				System.out.println("����һ��TCP��");
//			else if(pkt instanceof UDP)
//				System.out.println("����һ��UDP��");
//		}
		if(msg.getType()==OFType.FLOW_MOD){
			IPacket pkt = (IPacket) eth.getPayload();
			if (pkt instanceof ICMP)
			System.out.println("����һ��ICMP��");
			else if(pkt instanceof IPv4){
				if (pkt instanceof TCP)
					System.out.println("����һ��TCP��");
				else if(pkt instanceof UDP)
					System.out.println("����һ��UDP��");
		}
		String sth=eth.toString();
		System.out.println(sth);
		System.out.println("================================");
		}
		//if(msg.getType()==OFType.PACKET_IN)
//		{
//			
//			OFPacketIn pi=(OFPacketIn)msg;
//			//short inPort=pi.getInPort();
//			//int buffId=pi.getBufferId();
//			int buffId=pi.getBufferId();
//			short srcPort;
//			short dstPort;
//			
//			if(eth.getPayload() instanceof UDP){
//				System.out.println("����UDP��");
//				UDP pk_udp = (UDP)eth.getPayload().clone();
//				srcPort=pk_udp.getSourcePort();
//				dstPort=pk_udp.getDestinationPort();
//			}
//			if(eth.getPayload() instanceof TCP){
//				System.out.println("����TCP��");
//				TCP pk_tcp=(TCP)eth.getPayload().clone();
//				srcPort=pk_tcp.getSourcePort();
//				dstPort=pk_tcp.getDestinationPort();
//			}
//			if(eth.getPayload() instanceof ICMP){
//				System.out.println("����ICMP��");
//				TCP pk_tcp=(TCP)eth.getPayload().clone();
//				srcPort=pk_tcp.getSourcePort();
//				dstPort=pk_tcp.getDestinationPort();
//			}
//		    if(eth.getPayload() instanceof IPv4){
//		    	IPv4 pk_ipv4 = (IPv4)eth.getPayload().clone();
//				String dst = IPv4.fromIPv4Address(pk_ipv4.getDestinationAddress());
//				String src =IPv4.fromIPv4Address(pk_ipv4.getSourceAddress());
//				if(dst.equals("10.0.0.1")&&sw.getId()==(long)1)
//				{ 
//					setHostIPV4Flow(src,dst,cntx);
//				}
//				if(src.equals("10.0.0.1")&&sw.getId()==(long)1)
//				{
//					setServerIPV4Flow(src,dst,cntx);
//				}
//		    	System.out.println("����IPv4��");
//		    }
//		}
		return Command.CONTINUE;
    }
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		// TODO Auto-generated method stub
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
	    l.add(IFloodlightProviderService.class);
	
	    return l;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		// TODO Auto-generated method stub
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		flowstatsProvider=context.getServiceImpl(IGetFlowStatsServer.class);
		logger = LoggerFactory.getLogger(DDosDetection.class);

	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		// TODO Auto-generated method stub
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		floodlightProvider.addOFMessageListener(OFType.FLOW_MOD, this);
	}
	public void setHostIPV4Flow(String src,String dst,FloodlightContext cntx)
	{
		IOFSwitch sw1=floodlightProvider.getSwitches().get((long)1);
		OFFlowMod Flowset1=new OFFlowMod();
		OFMatch matchtest1=new OFMatch();
		String matchStr1="nw_src="+src+",nw_dst="+dst+",dl_type=0x0800";
		matchtest1.fromString(matchStr1);
		Flowset1.setMatch(matchtest1);
		List<OFAction> actions1 = new LinkedList<OFAction>();
		actions1.add(new OFActionOutput((short)1, (short) Short.MAX_VALUE));
		Flowset1.setActions(actions1);
		Flowset1.setBufferId(-1);
		Flowset1.setOutPort(OFPort.OFPP_NONE.getValue());
		Flowset1.setPriority(Short.MAX_VALUE);
		//Flowset1.setHardTimeout((short)30);
		Flowset1.setLength((short)(OFFlowMod.MINIMUM_LENGTH+8));
        try {
			
			sw1.write(Flowset1,cntx);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
	}
	public void setServerIPV4Flow(String src,String dst,FloodlightContext cntx)
	{
		IOFSwitch sw1=floodlightProvider.getSwitches().get((long)1);
		OFFlowMod Flowset2=new OFFlowMod();
		OFMatch matchtest2=new OFMatch();
		short outputPort=2;
		String tempDst;
		String matchStr2="nw_src="+src+",nw_dst="+dst+",dl_type=0x0800";
		matchtest2.fromString(matchStr2);
		Flowset2.setMatch(matchtest2);
		tempDst=dst.replace("10.0.0.","");
		outputPort=Short.parseShort(tempDst);
		List<OFAction> actions2 = new LinkedList<OFAction>();
		actions2.add(new OFActionOutput(outputPort, (short) Short.MAX_VALUE));
		Flowset2.setActions(actions2);
		Flowset2.setBufferId(-1);
		Flowset2.setOutPort(OFPort.OFPP_NONE.getValue());
		Flowset2.setPriority(Short.MAX_VALUE);
		//Flowset1.setHardTimeout((short)30);
		Flowset2.setLength((short)(OFFlowMod.MINIMUM_LENGTH+8));
        try {
			
			sw1.write(Flowset2,cntx);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
	}
//	public void setHostUDPFlow(short srcPort,short srcPort,FloodlightContext cntx)
//	{
//		IOFSwitch sw1=floodlightProvider.getSwitches().get((long)1);
//		OFFlowMod Flowset1=new OFFlowMod();
//		OFMatch matchtest1=new OFMatch();
//		String matchStr1="nw_src="+src+",nw_dst="+dst+",dl_type=0x0800";
//		matchtest1.fromString(matchStr1);
//		Flowset1.setMatch(matchtest1);
//		List<OFAction> actions1 = new LinkedList<OFAction>();
//		actions1.add(new OFActionOutput((short)1, (short) Short.MAX_VALUE));
//		Flowset1.setActions(actions1);
//		Flowset1.setBufferId(-1);
//		Flowset1.setOutPort(OFPort.OFPP_NONE.getValue());
//		Flowset1.setPriority(Short.MAX_VALUE);
//		//Flowset1.setHardTimeout((short)30);
//		Flowset1.setLength((short)(OFFlowMod.MINIMUM_LENGTH+8));
//        try {
//			
//			sw1.write(Flowset1,cntx);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			logger.error(e.getMessage());
//		}
//	}
}
