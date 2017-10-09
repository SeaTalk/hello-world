package act.tjt.rpc.test;

import java.io.Serializable;

public class ProxyMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String serviceName ;
	String methodName;
	Class<?>[] paramTypes;
	Object[] arguments;
	
	public ProxyMessage(String sn,String mn,Class[] py,Object[] args){
		serviceName = sn;
		methodName = mn;
		paramTypes = py;
		arguments = args;
	}
	
	public String toJson(){
		String ret = "{";
		ret += ("\"sn\":\"" + serviceName + "\"");
		ret += ",";
		ret += ("\"mn\":\"" + methodName + "\"");
		ret += ",";
		ret += "\"paramTypes\":[{";
		for(int i = 0; i< paramTypes.length; i++){
			ret += "\"paramType\":\"";
			ret += paramTypes[i].toString();
			ret += "\"";
			if(i!=paramTypes.length-1){
				ret+=",";
			}
		}
		ret += "}],";
		ret += "\"args\":[{";
		for(int i=0;i<arguments.length;i++){
			ret += "\"arg\":\"";
			ret += arguments[i].toString();
			ret += "\"";
			if(i != arguments.length -1){
				ret += ",";
			}
		}
		ret += "}]}";
		return ret;
	}
}
