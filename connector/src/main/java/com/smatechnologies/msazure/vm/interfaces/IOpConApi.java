package com.smatechnologies.msazure.vm.interfaces;

import com.smatechnologies.opcon.restapiclient.api.OpconApi;
import com.smatechnologies.opcon.restapiclient.api.OpconApiProfile;

public interface IOpConApi {

	public OpconApiProfile getOpConProfile(String address,	boolean usingTls) throws Exception;
	public OpconApi getClient(OpconApiProfile profile, boolean appClient, String userName, String password) throws Exception;
	public Boolean updateOpConProperty(OpconApi opconApi, String propertyName, String propertyValue) throws Exception;
	public String createApplicationToken(OpconApi opconApi, String user, String password) throws Exception;
	
}
