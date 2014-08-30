package com.s2d.cognition;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The {@link SystemPropsLoader} allows an application programmer to
 * load a java properties file by specifying the file path in the 
 * "com.s2d.cognition.system.props" system property. This is used so that a large
 * number of properties do not have to be set on the command line at runtime.
 * @author Anthony J Simon
 */
public final class SystemPropsLoader
{
	private static final Logger LOGGER = LogManager.getLogger ( SystemPropsLoader.class );
	
	public static final String COGNITION_SYSTEM_PROPS = "com.s2d.cognition.system.props";
	
	/**
	 * Read the "com.s2d.cognition.system.props" {@link System} property
	 * and load the {@link Properties} file specified in it. Create the new <code>Properties</code> object
	 * with the current <code>System</code> <code>Properties</code> as the parent and set the new
	 * {@link Properties</code> as the new <code>System</code> <code>Properties</code>
	 */
	public static void init ()
	{
		String propsFilePath = System.getProperty ( COGNITION_SYSTEM_PROPS );
		if ( propsFilePath == null )
		{
			LOGGER.info ( "[{}] is not set, no system property to load",
					COGNITION_SYSTEM_PROPS );
		}
		else
		{
			Path propPath = Paths.get ( propsFilePath ).toAbsolutePath ();
			try ( InputStream inputStream = Files.newInputStream ( propPath ) )
			{
				Properties props = new Properties ( System.getProperties () );
				props.load ( inputStream );
				System.setProperties ( props );
				LOGGER.info ( "Cognition properties loaded from [{}]", propPath );
			}
			catch ( IOException e )
			{
				LOGGER.catching ( Level.ERROR, e );
			}
		}
	}
}
