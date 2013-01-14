package com.qspin.qtaste.controlscriptbuilder.model;

import java.util.Properties;

import org.apache.log4j.Logger;

public abstract class ControlAction {

	protected ControlAction()
	{
		mParameters = new SortedProperties();
		setDescription("Undefined");
	}

	public abstract ControlActionType getType();

	public String getRawType()
	{
		return mParameters.getProperty("type", "UNDEFINED");
	}
	
	public void addParameter(String pParameterName, String pParameterValue)
	{
		mParameters.setProperty(pParameterName, pParameterValue);
	}
	
	public Properties getParameters()
	{
		return mParameters;
	}

	public boolean isActive() {
		return Boolean.parseBoolean(mParameters.getProperty(ACTIVE, "True"));
	}

	public void setActive(boolean active) {
		mParameters.setProperty(ACTIVE, Boolean.toString(active));
	}
	
	public String getDescription()
	{
		return mParameters.getProperty(DESCRIPTION);
	}
	public void setDescription(String pDescrition)
	{
		mParameters.setProperty(DESCRIPTION, pDescrition);
	}

	public String getControlScript() {
		return mParameters.getProperty("callerScript");
	}
	
	public String toString()
	{
		return getDescription();
	}

	protected static final String DESCRIPTION = "description";
	protected static final String ACTIVE = "active";
	protected Properties mParameters;
	
	protected static final Logger LOGGER = Logger.getLogger(ControlAction.class);

	public String getScriptCode() {
		StringBuilder builder = new StringBuilder();
		for( Object key : mParameters.keySet() )
		{
			if ( builder.length() > 0 )
			{
				builder.append(System.getProperty("line.separator") + "\t");
			}
			String propertyId = key.toString();
			if ( propertyId.equals("type") || propertyId.equals("controlActionID") || propertyId.equals("callerScript"))
				continue;
			if ( propertyId.equals("active") )
				builder.append(propertyId + "=" + (isActive()?"True":"False") + ",");
			else
				builder.append(propertyId + "=" + mParameters.getProperty(propertyId) + ",");
		}
		return getRawType() + "(" + builder.toString() + ")";
	}
}
