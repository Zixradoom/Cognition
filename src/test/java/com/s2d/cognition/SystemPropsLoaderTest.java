package com.s2d.cognition;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.s2d.cognition.SystemPropsLoader;

/**
 * Test for the {@link SystemPropsLoader#init()}.
 * @author Anthony J Simon
 *
 */
public class SystemPropsLoaderTest
{
  private Properties saveSystemProperties = null;
  private Path testFilePath = null;
  
  /**
   * Build the test data
   * @return
   */
  private Properties testData ( Properties parent )
  {
    Properties properties = new Properties ( parent );
    properties.put ( "john", "jones" );
    return properties;
  }
  
  /**
   * Write a temporary file for testing
   * @param properties
   * @return
   */
  private Path writeTempFile ( Properties properties )
  {
    try
    {
      Path temp = Files.createTempFile ( SystemPropsLoader.class.getName (), ".properties" );
      try ( BufferedWriter writer = Files.newBufferedWriter ( temp, Charset.forName ( "UTF-8" ) ) )
      {
        properties.store ( writer, "Test Props" );
      }
      return temp;
    }
    catch ( IOException e )
    {
      e.printStackTrace ();
      return null;
    }
  }
  
  @Before
  public void setUpTest ()
  {
    Properties save = new Properties ();
    save.putAll ( System.getProperties () );
    saveSystemProperties = save;
    
    Properties testData = testData ( save );
    testFilePath = writeTempFile ( testData );
    System.setProperty ( SystemPropsLoader.COGNITION_SYSTEM_PROPS, testFilePath.toString () );
  }
  
  @Test
  public void testInit ()
  {
    // get the new values
    Properties oldSystemProps = testData ( System.getProperties () );
    // init with the temp file
    SystemPropsLoader.init ();
    // get the new values
    Properties newSystemProps = System.getProperties ();

    // check for equality
    for ( Entry < Object, Object > actual : newSystemProps.entrySet () )
    {
      Object actualKey = actual.getKey ();
      Object actualValue = actual.getValue ();
      Object expectedValue = oldSystemProps.getProperty ( actualKey.toString () );
      assertEquals ( "Actual: " + actualKey.toString (), expectedValue, actualValue );
    }
    for ( Entry < Object, Object > expected : oldSystemProps.entrySet () )
    {
      Object expectedKey = expected.getKey ();
      Object expectedValue = expected.getValue ();
      Object actualValue = newSystemProps.getProperty ( expectedKey.toString () );
      assertEquals ( "Expected: " + expectedKey.toString (), expectedValue, actualValue );
    }
  }
  
  @After
  public void cleanUpTest ()
  {
    try
    {
      Files.delete ( testFilePath );
    }
    catch ( IOException e )
    {
      // TODO Auto-generated catch block
      e.printStackTrace ();
    }
    System.setProperties ( saveSystemProperties );
  }
}
