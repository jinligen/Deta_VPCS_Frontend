package org.deta.boot.vpc.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.deta.boot.vpc.vision.VPCSRequest;
import org.deta.boot.vpc.vision.VPCSResponse;

public class RequestRecordController {
	
	public static void requestIpRecoder(VPCSRequest vPCSRequest, VPCSResponse vPCSResponse) {
		vPCSRequest.setRequestIp(vPCSResponse.getSocket().getInetAddress().getHostAddress());
		vPCSRequest.setRequestName(vPCSResponse.getSocket().getInetAddress().getHostName());
	}

	public static void requestLinkRecoder(VPCSRequest vPCSRequest, VPCSResponse vPCSResponse) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(vPCSResponse.getSocket().getInputStream()));
		String mess = br.readLine();
		System.out.println(mess);
		if(null == mess){
			vPCSResponse.returnErrorCode(400);
			throw new Exception();
		}
		if(mess.equalsIgnoreCase("")){
			vPCSResponse.returnErrorCode(400);
			throw new Exception();
		}
		String[] type = mess.split(" ");
		if(type.length < 2){
			vPCSResponse.returnErrorCode(500);
			throw new Exception();
		}
		String[] content = type[1].split("\\?");
		if(content.length == 2){
			vPCSRequest.setRequestIsRest(true);
			if(content[1] == null){
				vPCSResponse.returnErrorCode(500);
				throw new Exception();
			}
		}
		if(content[0].contains(".")){
			//vPCSRequest.setRequestIsRest(false);
		}
		if(vPCSRequest.getRequestIsRest()){
			String[] column = content[1].split("&");
			Map<String, String> data = new ConcurrentHashMap<>();
			for(String cell:column){
				String[] cells = cell.split("=");
				data.put(cells[0], URLDecoder.decode(cells[1], "UTF-8"));
			}
			vPCSRequest.setRequestValue(data);	
		}
		vPCSRequest.setRequestLink(content[0]);	
	}
}