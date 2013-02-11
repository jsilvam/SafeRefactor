package saferefactor.ui;

import java.io.File;

import saferefactor.core.Parameters;
import saferefactor.core.Report;
import saferefactor.core.SafeRefactor;
import saferefactor.core.SafeRefactorImp;
import saferefactor.core.util.Project;

public class SafeRefactorFacade {

	
	public static Report checkTransformation(String sourcePath,
			String targetPath, int timelimit, boolean occ) {

		File sourceFile = new File(sourcePath);
		File targetFile = new File(targetPath);
		Report result = new Report();
		Project source = new Project();
		source.setProjectFolder(sourceFile);
		source.setBuildFolder(new File(sourceFile,"bin"));
		source.setSrcFolder(new File(sourceFile,"src"));
		File libFolder = new File(sourceFile,"lib");
		if (libFolder.exists())
			source.setLibFolder(libFolder);

		Project target = new Project();
		target.setProjectFolder(targetFile);
		target.setBuildFolder(new File(targetFile,"bin"));
		target.setSrcFolder(new File(targetFile,"src"));
		File targetLibFolder = new File(targetFile,"lib");
		if (targetLibFolder.exists())
			target.setLibFolder(targetLibFolder);

		try {
			Parameters parameters = new Parameters();
			parameters.setCompileProjects(false);
			parameters.setTimeLimit(timelimit);
			parameters.setEnableOCC(occ);

			SafeRefactor saferefactor = new SafeRefactorImp(source, target,
					parameters);
			saferefactor.checkTransformation();
			result = saferefactor.getReport();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
