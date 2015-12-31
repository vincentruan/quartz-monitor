package com.quartz.monitor.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 接口或类的说明
 * @author Administrator
 *
 */
public  abstract class PropertyReader {
	private static Logger _log = LoggerFactory.getLogger(PropertyReader.class);
	private static Hashtable<String, Properties> pptContainer = new Hashtable<String, Properties>();
	/**
	 * <B>方法描述：</B>获得属性
	 * @param propertyFilePath 属性文件(包括类路径)
	 * @param key 属性键
	 * @return 属性值
	 */
	public final static String getValue(String propertyFilePath, String key) {
		return getValue(null, propertyFilePath, key);
	}
	/**
	 * <B>方法描述：</B>获得属性
	 * @param cls
	 * @param propertyFilePath
	 * @param key
	 * @return
	 */
	public final static String getValue(Class<?> cls, String propertyFilePath, String key) {
		Properties ppts = getProperties(cls, propertyFilePath);
		return ppts == null ? null : ppts.getProperty(key);
	}
	/**
	 * <B>方法描述：</B>获得属性文件中Key所对应的值
	 * @param propertyFilePath 属性文件路径(包括类路径或文件系统中文件路径)
	 * @param key 属性的键
	 * @param isAbsolutePath 是否为绝对路径:true|false〔即是本地文件系统路径，比如：C:/test.propreties〕<br>
	 * <br><b>注：</b>不能通过类路径来获取到属性文件，而只知道属性文件的文件系统路径，即文件系统地址则用此方法来获取其中的Key所对应的Value
	 * @return key的属性值
	 */
	public final static String getValue(String propertyFilePath, String key, boolean isAbsolutePath) {
		if(isAbsolutePath){
			Properties ppts = getPropertiesByFs(propertyFilePath);
			return ppts == null ? null : ppts.getProperty(key);
		}
		return getValue(propertyFilePath, key);
	}
	
	/**
	 * 方法描述:获得属性文件的属性
	 * @param propertyFilePath 属性文件(包括类路径)
	 * @return 属性
	 */
	public final static Properties getProperties(String propertyFilePath) {
		return getProperties(null, propertyFilePath);
	}
	/**
	 * 方法描述:获得属性文件的属性
	 * @param propertyFilePath 属性文件(包括类路径)
	 * @return 属性
	 */
	public final static Properties getProperties(Class<?> cls, String propertyFilePath) {
		if(propertyFilePath==null){
			_log.error("propertyFilePath is null!");
			return null;
		}
		Properties ppts = pptContainer.get(propertyFilePath);
		if (ppts == null) {
			ppts = loadPropertyFile(cls, propertyFilePath);
			if (ppts != null) {
				pptContainer.put(propertyFilePath, ppts);
			}
		}
		return ppts;
	}
	/**
	 * <B>方法描述：</B>获得属性文件的属性
	 * @param propertyFilePath 属性文件路径(包括类路径及文件系统路径)
	 * @return 属性文件对象 Properties
	 */
	public final static Properties getPropertiesByFs(String propertyFilePath) {
		if(propertyFilePath==null){
			_log.error("propertyFilePath is null!");
			return null;
		}
		Properties ppts = pptContainer.get(propertyFilePath);
		if (ppts == null) {
			ppts = loadPropertyFileByFileSystem(propertyFilePath);
			if (ppts != null) {
				pptContainer.put(propertyFilePath, ppts);
			}
		}
		return ppts;
	}
	/**
	 * @param cl
	 * @param propertyFilePath
	 * @return
	 */
	private static Properties loadPropertyFile(Class<?> cl, String propertyFilePath) {
		InputStream is = null;
		if(null == cl) {
			is = getClassLoader(PropertyReader.class).getResourceAsStream(propertyFilePath);
		} else {
			is = getClassLoader(cl).getResourceAsStream(propertyFilePath);
		}
			
		if(is == null)
			return loadPropertyFileByFileSystem(propertyFilePath);
		
		Properties ppts = new Properties();
		try {	
			ppts.load(is);
			return ppts;
		} catch (Exception e) {
			_log.debug("加载属性文件出错:" + propertyFilePath, e);
			return null;
		}
	}
	/**
	 * <B>方法描述：</B> 从文件系统加载属性文件
	 * @param propertyFilePath 属性文件(文件系统的文件路径)
	 * @return 属性
	 */
	private static Properties loadPropertyFileByFileSystem(final String propertyFilePath) {
		FileInputStream fis = null; 
		try {
			fis = new FileInputStream(propertyFilePath);
			Properties ppts = new Properties();
			ppts.load(fis);
			return ppts;
		}catch(FileNotFoundException e){
			_log.error("FileInputStream(\"" + propertyFilePath + "\")! FileNotFoundException: ", e);
			return null;
		}catch (IOException e) {
			_log.error("Properties.load(InputStream)! IOException: ", e);
			return null;
		} finally {
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					_log.error("Close FileInputStream! IOException: ", e);
				}
			}
		}
	}
	
	/**
	 * Function:得到类加载器
	 * @param
	 */
	public static ClassLoader getClassLoader() {
		return getClassLoader(null);
	}
	
	/**
	 * 获取类加载器
	 * @param cls
	 * @return
	 * @author 张宪新
	 */
	public static ClassLoader getClassLoader(Class<?> cls) {
		ClassLoader cl = null;
		if(cls != null){
			cl = cls.getClassLoader();
		}
		if (cl == null) {
			cl = Thread.currentThread().getContextClassLoader();
        }
		if (cl == null) {
			cl = PropertyReader.class.getClassLoader();
		}
		if (cl == null) {
            throw new IllegalStateException("Cannot determine classloader");
        }
		_log.debug("ClassLoader = {}", cl);
		return cl;
	}
}