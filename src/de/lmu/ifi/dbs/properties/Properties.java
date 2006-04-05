package de.lmu.ifi.dbs.properties;

import de.lmu.ifi.dbs.algorithm.Algorithm;
import de.lmu.ifi.dbs.logging.LoggingConfiguration;
import de.lmu.ifi.dbs.utilities.optionhandling.Parameterizable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Provides management of properties.
 * 
 * @author Arthur Zimek (<a href="mailto:zimek@dbs.ifi.lmu.de">zimek@dbs.ifi.lmu.de</a>)
 */
public final class Properties
{
    /**
     * Holds the debug status.
     */
    private static final boolean DEBUG = LoggingConfiguration.DEBUG;
    
    /**
     * The logger for this class.
     */
    private Logger logger = Logger.getLogger(this.getClass().getName());
    
    /**
     * The pattern to split for separate entries in a property string, which is
     * a &quot;,&quot;.
     */
    public static final Pattern PROPERTY_SEPARATOR = Pattern.compile(",");

    /**
     * The Properties for the KDDFramework.
     */
    public static final Properties KDD_FRAMEWORK_PROPERTIES = new Properties(Properties.class.getPackage().getName().replace('.',File.separatorChar)+File.separatorChar + "KDDFramework.prp");

    /**
     * Stores the properties as defined by a property-file.
     */
    private final java.util.Properties PROPERTIES;

    /**
     * Provides the properties as defined in the designated file.
     * 
     * @param filename
     *            name of a file to provide property-definitions.
     */
    private Properties(String filename)
    {
        this.PROPERTIES = new java.util.Properties();
        try
        {
            PROPERTIES.load(ClassLoader.getSystemResourceAsStream(filename));
        }
        catch (Exception e)
        {
            logger.warning("Unable to load properties file " + filename + ".\n");
        }
    }

    /**
     * Provides the entries (separated by
     * {@link #PROPERTY_SEPARATOR PROPERTY_SEPARATOR}) for a specified
     * PropertyName.
     * 
     * @param propertyName
     *            the PropertyName of the property to retrieve
     * @return the entries (separated by
     *         {@link #PROPERTY_SEPARATOR PROPERTY_SEPARATOR}) for the
     *         specified PropertyName
     */
    public String[] getProperty(PropertyName propertyName)
    {
        String property = propertyName == null ? null : PROPERTIES.getProperty(propertyName.getName());
        return property == null ? new String[0] : PROPERTY_SEPARATOR.split(property);
    }

    /**
     * Provides a description string listing all classes
     * for the given superclass or interface as
     * specified in the properties.
     * 
     * @param superclass the class to be extended or interface
     * to be implemented
     * @return a description string listing all classes
     * for the given superclass or interface as
     * specified in the properties
     */
    public String conditionAndAvailableClassesFor(Class superclass)
    {
        StringBuilder info = new StringBuilder();
        info.append("name of a class ");
        if(superclass.isInterface())
        {
            info.append("implementing ");
        }
        else
        {
            info.append("extending ");
        }
        info.append(superclass.getName());
        info.append(" - available classes:\n");
        PropertyName propertyName = PropertyName.getOrCreatePropertyName(superclass);
        String[] classNames = getProperty(propertyName);
        for(String name : classNames)
        {
            try
            {
                Object propertyInstance = propertyName.getType().cast(propertyName.classForName(name).newInstance());
                info.append("--");
                info.append(name);
                info.append('\n');
            }
            catch(InstantiationException e)
            {
                logger.warning("Invalid classname \"" + name
                        + "\" for property \"" + propertyName.getName()
                        + "\" of class \"" + propertyName.getType().getName()
                        + "\" in property-file: " + e.getMessage() + " - "
                        + e.getClass().getName() + "\n");
            }
            catch(IllegalAccessException e)
            {
                logger.warning("Invalid classname \"" + name
                        + "\" for property \"" + propertyName.getName()
                        + "\" of class \"" + propertyName.getType().getName()
                        + "\" in property-file: " + e.getMessage() + " - "
                        + e.getClass().getName() + "\n");
            }
            catch(ClassNotFoundException e)
            {
                logger.warning("Invalid classname \"" + name
                        + "\" for property \"" + propertyName.getName()
                        + "\" of class \"" + propertyName.getType().getName()
                        + "\" in property-file: " + e.getMessage() + " - "
                        + e.getClass().getName() + "\n");
            }
        }
        return info.toString();
    }
    
    /**
     * Returns an array of PropertyDescription for all entries for the given
     * PropertyName.
     * 
     * @param propertyName
     *            the Propertyname of the property to retrieve
     * @return PropertyDescriptins for all entries of the given PropertyName
     */
    public PropertyDescription[] getProperties(PropertyName propertyName)
    {
        String[] entries = getProperty(propertyName);
        List<PropertyDescription> result = new ArrayList<PropertyDescription>();
        for (String entry : entries)
        {
            try
            {
                String desc = "";
                Object propertyInstance = propertyName.getType().cast(propertyName.classForName(entry).newInstance());
                if (propertyInstance instanceof Algorithm)
                {
                    // TODO: description -- check whether this provides the
                    // desired result
                    desc = ((Algorithm) propertyInstance).getDescription().toString();
                }
                else if (propertyInstance instanceof Parameterizable)
                {
                    desc = ((Parameterizable) propertyInstance).description();
                }
                result.add(new PropertyDescription(entry, desc));
            }
            catch (InstantiationException e)
            {
                logger.warning("Invalid classname \"" + entry
                        + "\" for property \"" + propertyName.getName()
                        + "\" of class \"" + propertyName.getType().getName()
                        + "\" in property-file: " + e.getMessage() + " - "
                        + e.getClass().getName() + "\n");
            }
            catch (IllegalAccessException e)
            {
                logger.warning("Invalid classname \"" + entry
                        + "\" for property \"" + propertyName.getName()
                        + "\" of class \"" + propertyName.getType().getName()
                        + "\" in property-file: " + e.getMessage() + " - "
                        + e.getClass().getName() + "\n");
            }
            catch (ClassNotFoundException e)
            {
                logger.warning("Invalid classname \"" + entry
                        + "\" for property \"" + propertyName.getName()
                        + "\" of class \"" + propertyName.getType().getName()
                        + "\" in property-file: " + e.getMessage() + " - "
                        + e.getClass().getName() + "\n");
            }
        }
        PropertyDescription[] propertyDescription = new PropertyDescription[result.size()];
        result.toArray(propertyDescription);
        return propertyDescription;

    }

}
