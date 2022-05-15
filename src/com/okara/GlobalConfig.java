package com.okara;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GlobalConfig {
	public static final String DEFAULT_CONFIG_APP = "./conf/config.properties";
    private static final Object obj;
	public static String configFilePath;
    public static ResourceBundle resourceBundle;
    public static boolean reConfig;
    
	public static final String DEFAULT_CONFIG_LOG = "./conf/forward.log";
    public static FileHandler handlerLog;
    
    static {
        obj = new Object();
        reConfig = false;
    }


	private static void config(final InputStream inputStream) {
		try {
			GlobalConfig.resourceBundle = new PropertyResourceBundle(inputStream);
		} catch (FileNotFoundException ex) {
			Logger.getLogger(GlobalConfig.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex2) {
			Logger.getLogger(GlobalConfig.class.getName()).log(Level.SEVERE, null, ex2);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				Logger.getLogger(GlobalConfig.class.getName()).log(Level.SEVERE, null, e);
			}
		}
	}

	public static void config(final String configFile) {
		GlobalConfig.configFilePath = configFile;
		try {
			final FileInputStream fis = new FileInputStream(configFile);
			config(fis);
		} catch (FileNotFoundException e) {
			if(!reConfig) {
				reConfig = true;
				config(DEFAULT_CONFIG_APP);
			}
			Logger.getLogger(GlobalConfig.class.getName()).log(Level.SEVERE, null, e);
		}
	}
	
    public static String get(final String key) {
        synchronized (GlobalConfig.obj) {
            try {
                return GlobalConfig.resourceBundle.getString(key);
            }
            catch (MissingResourceException ex) {
                return null;
            }
        }
    }
    
    public static int getInt(final String key, final int defaultValue) {
        int value = defaultValue;
        final String tm = get(key);
        if (tm != null) {
            value = Integer.parseInt(tm);
        }
        return value;
    }
    
    public static void configLog(final String configFile) {
    	try {
			handlerLog = new FileHandler(configFile, true);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
}
