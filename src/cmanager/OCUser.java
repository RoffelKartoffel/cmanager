package cmanager;

import com.github.scribejava.core.model.Token;

public class OCUser 
{
	private static OCUser user = null;
	
	public static OCUser getOCUser(){
		if(user == null )
			user = new OCUser();
		return user;
	}
	
	
//////////////////////////////////////
//////////////////////////////////////
/////// Member functions
//////////////////////////////////////
//////////////////////////////////////	
	
	
	private Token okapiAccessToken = null;
	
	
	private OCUser()
	{
		try {
			okapiAccessToken = new Token(
					Settings.getS(Settings.Key.OKAPI_TOKEN),
					Settings.getS(Settings.Key.OKAPI_SECRET));
		}
		catch( IllegalArgumentException e){
			okapiAccessToken = null;
		}
	}
	
	public Token getOkapiToken()
	{
		return okapiAccessToken;
	}
	
	public void requestOkapiToken()
	{
		okapiAccessToken = OKAPI.requestAuthorization();
		if( okapiAccessToken != null )
		{
			Settings.set(Settings.Key.OKAPI_TOKEN, okapiAccessToken.getToken());
			Settings.set(Settings.Key.OKAPI_SECRET, okapiAccessToken.getSecret());

		}
	}
	

}
