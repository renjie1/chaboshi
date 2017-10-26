package com.rj.wf.mvc.scan;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang.StringUtils;

import com.rj.wf.mvc.log.Logger;
import com.rj.wf.mvc.log.LoggerFactory;

public abstract class DefaultClassFilter {
	private static final Logger _WFLOG = LoggerFactory.getLogger(DefaultClassFilter.class);
	protected static ClassLoader DefaultClassLoader = Thread.currentThread().getContextClassLoader();
	protected final String packageName;

	public DefaultClassFilter(final String _packageName) {
		this.packageName = _packageName;
	}

	protected DefaultClassFilter(final String packageName, ClassLoader classLoader) {
		this.packageName = packageName;
		DefaultClassFilter.DefaultClassLoader = classLoader;
	}

	
	public final Set<Class<?>> getClassList() {
		// 收集符合条件的Class类容器
		Set<Class<?>> clazzes = new HashSet<Class<?>>();
		try {
			//从包名获取 URL 类型的资源
			Enumeration<URL> urls = DefaultClassLoader.getResources(packageName.replace(".", "/"));
			// 遍历 URL 资源
			URL url;
			System.out.println("包的路径：" + packageName);
			System.out.println("替换之后包的路径：" + packageName.replace(".", "/"));
			System.out.println("当前类加载的获取的路径：" + DefaultClassLoader.getResource("").toString());
			while (urls.hasMoreElements()) {
				url = (URL) urls.nextElement();
				if(url != null) {
					System.out.println("scan url >>>>>>>>>>> " + url.toString());
					// 获取协议名（分为 file 与 jar）
					String protocol = url.getProtocol();
					if(protocol.equals("file")) {
						String packagePath = url.getPath();
						addClass(clazzes, packagePath, packageName);
					} else if(protocol.equals("jar")) {
						System.out.println("jar包:" + url.getPath());
						// classPath下的.jar文件
						JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
						JarFile jarFile = jarURLConnection.getJarFile();
						Enumeration<JarEntry> jarEntries = jarFile.entries();
						while (jarEntries.hasMoreElements()) {
							JarEntry jarEntry = jarEntries.nextElement();
							String jarEntryName = jarEntry.getName();
							// 判断该 entry 是否为 class
							if (jarEntryName.endsWith(".class")) {
								// 获取类名
								String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
								// 执行添加类操作
								doAddClass(clazzes, className);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			_WFLOG.error("find class error！", e);
		}

		return clazzes;
	}


	/**
	 * 返回指定包名下的类
	 * @param clazzes
	 * @param packagePath
	 * @param packageName
	 */
	private void addClass(Set<Class<?>> clazzes, String packagePath, String packageName) {
		try {
			// 获取包名路径下的 class 文件或目录
			File[] files = new File(packagePath).listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
				}
			});
			// 遍历文件或目录
			for (File file : files) {
				String fileName = file.getName();
				// 判断是否为文件或目录
				if (file.isFile()) {
					// 获取类名
					String className = fileName.substring(0, fileName.lastIndexOf("."));
					if (StringUtils.isNotEmpty(packageName)) {
						className = packageName + "." + className;
					}
					System.out.println("获取的className为:" + className);
					// 执行添加类操作
					doAddClass(clazzes, className);
				} else {
					// 获取子包
					String subPackagePath = fileName;
					if (StringUtils.isNotEmpty(packagePath)) {
						subPackagePath = packagePath + "/" + subPackagePath;
					}
					// 子包名
					String subPackageName = fileName;
					if (StringUtils.isNotEmpty(packageName)) {
						subPackageName = packageName + "." + subPackageName;
					}
					// 递归调用
					addClass(clazzes, subPackagePath, subPackageName);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			_WFLOG.error("find class error！", e);
		}
	}


	private void doAddClass(Set<Class<?>> clazzes, String className) throws ClassNotFoundException {
		// 加载类
		Class<?> cls = DefaultClassLoader.loadClass(className);
		// 判断是否可以添加类
		if (filterCondition(cls)) {
			// 添加类
			clazzes.add(cls);
			_WFLOG.debug("add class:{}", cls.getName());
		}
	}


	/**
	 * 验证是否允许添加类
	 */
	public abstract boolean filterCondition(Class<?> clazz);
	
	
}
