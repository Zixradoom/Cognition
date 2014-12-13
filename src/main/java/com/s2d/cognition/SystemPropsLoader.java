package com.s2d.cognition;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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
 * "com.s2d.cognition.system.props" system property or using a well known name for the file.
 * This is used so that a large
 * number of properties do not have to be set on the command line at runtime.
 * @author Anthony J Simon
 */
public final class SystemPropsLoader
{
  private static final Logger LOGGER = LogManager.getLogger ( SystemPropsLoader.class );

  public static final String COGNITION_SYSTEM_PROPS = "com.s2d.cognition.system.props";
  public static final String DEFAULT_TEST_FILE_NAME = "cognitionTest.properties";
  public static final String DEFAULT_FILE_NAME = "cognition.properties";

  /**
   * Call {@link SystemPropsLoader#getPropertiesURL()} and if
   * it is not null attempt to load the file it specifies.
   * Create the new <code>Properties</code> object
   * with the current <code>System</code> <code>Properties</code> as the parent and set the new
   * <code>Properties</code> as the new <code>System</code> <code>Properties</code>
   */
  public static void init ()
  {
    LOGGER.entry ();
    
    URL propFileURL = getPropertiesURL ();
    if ( propFileURL != null )
    {
      try ( InputStream inputStream = propFileURL.openStream () )
      {
        Properties props = new Properties ( System.getProperties () );
        props.load ( inputStream );
        System.setProperties ( props );
        LOGGER.info ( "Cognition properties loaded from [{}]", propFileURL );
      }
      catch ( IOException e )
      {
        LOGGER.catching ( Level.ERROR, e );
      }
    }
    else
    {
      LOGGER.info ( "No Cognition Properties File has been detected." );
    }
    LOGGER.exit ();
  }

  /**
   * 
   * @return The URL of the cognition properties file or null if one can not be found
   */
  public static URL getPropertiesURL()
  {
    LOGGER.entry ();
    /*
     * Search Order
     * 1) System properties
     * 2) cognitionTest.properties
     * 3) cognition.properties
     */

    URL returnValue = null;
    String sysProp = System.getProperty ( COGNITION_SYSTEM_PROPS );
    if ( sysProp != null && !sysProp.isEmpty () )
    {
      // try to get a url first
      URL url = null;
      try
      {
        url = new URL ( sysProp );
      }
      catch ( MalformedURLException e )
      {
        LOGGER.catching ( e );
      }

      // try to get path and covert to url
      if ( url == null )
      {
        try
        {
          Path propPath = Paths.get ( sysProp );
          propPath = propPath.toAbsolutePath ();
          if ( Files.exists ( propPath ) && !Files.isDirectory ( propPath ) )
            url = propPath.toUri ().toURL ();
        }
        catch ( MalformedURLException e )
        {
          LOGGER.catching ( e );
        }
      }
      returnValue = url;
    }

    if ( returnValue == null )
      returnValue = Thread.currentThread ().getContextClassLoader ().getResource ( DEFAULT_TEST_FILE_NAME );

    if ( returnValue == null )
      returnValue = Thread.currentThread ().getContextClassLoader ().getResource ( DEFAULT_FILE_NAME );

    return LOGGER.exit ( returnValue );
  }
}
