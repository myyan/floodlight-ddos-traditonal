package net.floodlightcontroller.flowstatistics;

import java.util.ArrayList;
import java.util.List;
import org.openflow.protocol.statistics.OFFlowStatisticsReply;

public class FlowStorageStat {
	private int nwSrc;//流的源地址
	private int nwDst;//流的目的地址
	private int srcPort;//流的源端口
	private int desPort;//流的目的地址
	private long byteNum;//当前流的字节数
	private long packetsNum;//当前流的数据包数
	private long difByteNum=0;//时间间隔内的流字节数
	private long difPacketsNum=0;//时间间隔内的流数据包数
	private boolean pairFlow_Flage=false;//记录该流是否有对流
    
    public int getNwSrc()
    {
   	 return nwSrc;
    }
    public int getSrcPort() {
		return srcPort;
	}
	public void setSrcPort(int srcPort) {
		this.srcPort = srcPort;
	}
	public int getDesPort() {
		return desPort;
	}
	public void setDesPort(int desPort) {
		this.desPort = desPort;
	}
	public void setNwSrc(int nwSrc)
    {
   	 this.nwSrc=nwSrc;
    }
    public int getNwDst()
    {
   	 return nwDst;
    }
    public void setNwDst(int nwDst)
    {
   	 this.nwDst=nwDst;
    }
    public long getByteNum()
    {
   	 return byteNum;
    }
    public void setByteNum(long byteNum)
    {
   	 this.byteNum=byteNum;
    }
    public long getPacketsNum()
    {
   	 return packetsNum;
    }
    public void setPacketsNum(long packetsNum)
    {
   	 this.packetsNum=packetsNum;
    }
    public boolean getIfPairFlow()
    {
   	 return pairFlow_Flage;
    }
    public void setIfPairFlow(boolean pairFlow_Flage)
    {
   	 this.pairFlow_Flage=pairFlow_Flage;
    }
    public long getDifByteNum()
    {
   	 return difByteNum;
    }
    public void setDifByteNum(long difByteNum)
    {
   	 this.difByteNum=difByteNum;
    }
    public long getDifPacketsNum()
    {
   	 return difPacketsNum;
    }
    public void setDifPacketsNum(long difPacketsNum)
    {
   	 this.difPacketsNum=difPacketsNum;
    }
}
