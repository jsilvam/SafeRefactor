package saferefactor.core.execution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import saferefactor.core.execution.CoverageDataReader.CoverageReport;
import saferefactor.core.util.Constants;

public class CoverageMeter {

	private saferefactor.core.util.Project project;
	private String testFolder;
	private CoverageReport coverageReport;
	private String baseFolder;

	public CoverageMeter(saferefactor.core.util.Project project,
			String testFolder) {
		this.project = project;
		this.testFolder = testFolder;
		File tests = new File(testFolder);
		baseFolder = tests.getParent();
		
	}

	public void checkTestCoverage() throws FileNotFoundException {

		 URL buildFile = CoverageMeter.class.getResource("/build_coverage.xml");
//		File buildFile = new File("ant" + "/" + "build_coverage.xml");
		Project p = new Project();

		
				
		// propriedades
		p.setProperty("target", project.getProjectFolder().getAbsolutePath());
		p.setProperty("targetBin", project.getBuildFolder().getAbsolutePath());
		p.setProperty("targetSrc", project.getSrcFolder().getAbsolutePath());
		if (project.getLibFolder() != null)
			p.setProperty("lib", project.getLibFolder().getAbsolutePath());
		else
			p.setProperty("lib", project.getBuildFolder().getAbsolutePath());
		p.setProperty("tests.folder", testFolder);

		// log do ant no console
		DefaultLogger consoleLogger = new DefaultLogger();
		FileOutputStream fileOutputStream = new FileOutputStream(baseFolder  + Constants.SEPARATOR +  "log_saferefactor_coverage.txt");
		PrintStream ps = new PrintStream(fileOutputStream);
		consoleLogger.setOutputPrintStream(ps);		
		consoleLogger.setErrorPrintStream(ps);
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
		p.addBuildListener(consoleLogger);

		// logar em arquivo//
		DefaultLogger consoleLogger2 = new DefaultLogger();
		File f = new File("log_coverage.txt");
		try {
			f.createNewFile();
			FileOutputStream myFile;
			PrintStream myStream;
			myFile = new FileOutputStream("log_coverage.txt");
			myStream = new PrintStream(myFile);
			consoleLogger2.setErrorPrintStream(myStream);
			consoleLogger2.setOutputPrintStream(myStream);
			consoleLogger2.setMessageOutputLevel(Project.MSG_INFO);
			p.addBuildListener(consoleLogger2);
		} catch (IOException ex) {
			Logger.getLogger(CoverageMeter.class.getName()).log(Level.SEVERE,
					null, ex);
		}

		p.init();
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		p.addReference("ant.projectHelper", helper);
		helper.parse(p, buildFile);
		p.executeTarget(p.getDefaultTarget());

		coverageReport = CoverageDataReader
				.readCoverageData(new File(testFolder, "coverage/coverage.xml"));
		
	}
	
	public CoverageReport getCoverageReport() {
		return this.coverageReport;
	}

}
