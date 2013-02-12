package saferefactor.core.generation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.ProjectHelper;

import randoop.main.Main;
import saferefactor.core.Parameters;
import saferefactor.core.util.Constants;
import saferefactor.core.util.FileUtil;
import saferefactor.core.util.Project;
import saferefactor.core.util.ast.Method;
import saferefactor.core.util.ast.ConstructorImp;
import saferefactor.core.util.ast.MethodImp;

public class RandoopAntAdapter extends AbstractTestGeneratorAdapter {

	public static String methodsToTest = "methodToTest.txt";
	private final String tmpDir;

	public RandoopAntAdapter(Project projectToTest, String tmpDir) {
		super(projectToTest);
		this.tmpDir = tmpDir;
		

	}

	// public static String TESTS_FOLDER = System.getProperty("java.io.tmpdir")
	// + "/safeRefactorAJ/tests";

	private List<String> additionalParameters;
	private double timeLimit;

	public void generateTestsForMethodList(List<Method> methods,
			double timeLimit, List<String> additionalParameters)
			throws FileNotFoundException {

		this.timeLimit = timeLimit;
		this.additionalParameters = additionalParameters;
		generateMethodListFile(methods);
		runRandoopThroughAnt();

	}

	private void runRandoopThroughAnt() throws FileNotFoundException {

		URL buildFile = RandoopAntAdapter.class
				.getResource("/build_generator.xml");
		// File buildFile = new File("ant" + "/" + "build_generator.xml");
		org.apache.tools.ant.Project p = new org.apache.tools.ant.Project();

		p.setProperty("projectBin", project.getBuildFolder().getAbsolutePath());

		if (project.getLibFolder() != null)
			p.setProperty("sourceLib", project.getLibFolder().getAbsolutePath());
		else
			p.setProperty("sourceLib", project.getProjectFolder()
					.getAbsolutePath());
		p.setProperty("timeout", String.valueOf(timeLimit));

		String isFork = "true";

		StringBuffer randoopParametersForAnt = new StringBuffer();
		for (String parameter : additionalParameters) {
			if (parameter.startsWith("fork")) {
				String[] forkParameter = parameter.split("=");
				isFork = forkParameter[1];
			} else {
				randoopParametersForAnt.append(parameter);
				randoopParametersForAnt.append(";");
			}

		}

		p.setProperty("fork", isFork);
		p.setProperty("test.folder", tmpDir);
		p.setProperty("randoopParameters", randoopParametersForAnt.toString());

		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger
				.setMessageOutputLevel(org.apache.tools.ant.Project.MSG_INFO);

		FileOutputStream fileOutputStream = new FileOutputStream(tmpDir  + Constants.SEPARATOR + 
				"log_saferefactor_generation.txt");
		PrintStream ps = new PrintStream(fileOutputStream); 
		consoleLogger.setOutputPrintStream(ps);
		consoleLogger.setErrorPrintStream(ps);

		p.addBuildListener(consoleLogger);

		p.init();
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		p.addReference("ant.projectHelper", helper);
		helper.parse(p, buildFile);

		p.executeTarget(p.getDefaultTarget());

	}

	private void generateMethodListFile(List<Method> methods) {

		Random random = new Random();
		int choice = random.nextInt(2);		
		StringBuffer lines = new StringBuffer();
//		if (choice == 0) {
//			for (Method method : methods) {
//				lines.append(method + "\n");
//			}
//		} 
//		else {
			for (Method method : methods) {
				if (method instanceof ConstructorImp)
					lines.append(method + "\n");
			}
			for (Method method : methods) {
				if (method instanceof MethodImp)
					lines.append(method + "\n");
			}

//		}

		FileUtil.makeFile(tmpDir + Constants.SEPARATOR + methodsToTest, lines.toString());

	}

	@Override
	public List<File> getTestFiles() {
		List<File> result = new ArrayList<File>();

		return result;

	}

	public static void main(String[] args) {
			
		String timeout = args[0];
		String tmpDir = args[1];
		
		String randoopParameters = "";
		if (args.length > 2) randoopParameters = args[2];
		

		Main main2 = new Main();
		String[] argsRandoop = {
				"gentests",
				"--methodlist=" + tmpDir + Constants.SEPARATOR + methodsToTest,
				"--timelimit=" + timeout,
				"--log=filewriter",
				"--junit-output-dir="
						+ tmpDir,
				"--output-nonexec=true" };

		if (randoopParameters.length() > 0) {
			String[] listRandoopParameters = randoopParameters.split(";");

			if (listRandoopParameters.length > 0)
				argsRandoop = (String[]) ArrayUtils.addAll(argsRandoop,
						listRandoopParameters);

		}

		main2.nonStaticMain(argsRandoop);
		
//		ThreadGroup rootGroup = Thread.currentThread( ).getThreadGroup( );
//		ThreadGroup parentGroup;
//		while ( ( parentGroup = rootGroup.getParent() ) != null ) {
//		    rootGroup = parentGroup;
//		    Thread[] threads = new Thread[ rootGroup.activeCount() ];
//		    while ( rootGroup.enumerate( threads, true ) == threads.length ) {
//		        threads = new Thread[ threads.length * 2 ];
//		        for (Thread thread : threads) {
//		        	System.out.println("matando threads");
//					thread.interrupt();
//				}
//		    }
//
//		}
//		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
//		for (Thread thread : threadSet) {
//			System.out.println("matando threads");
//			thread.interrupt();
//			
//		}


		
		System.exit(0);
	}

}
