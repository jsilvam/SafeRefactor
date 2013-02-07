package saferefactor.core.util;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;

public interface Compiler {
	
	public boolean compile(String src, String dest) throws MalformedURLException, FileNotFoundException;

	public void setBinClasspath(String binClasspath);

	public void setLibClasspath(String libClasspath);
	
//	public void setCompilerPath(String path);

//	public void setBuildPath(String buildPath);

}
