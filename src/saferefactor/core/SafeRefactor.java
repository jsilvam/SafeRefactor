package saferefactor.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import saferefactor.core.analysis.TransformationAnalyzer;
import saferefactor.core.comparator.TestComparator;
import saferefactor.core.execution.CoverageMeter;
import saferefactor.core.execution.TestExecutor;
import saferefactor.core.generation.AbstractTestGeneratorAdapter;
import saferefactor.core.util.Compiler;
import saferefactor.core.util.Constants;
import saferefactor.core.util.Project;
import saferefactor.core.util.ast.Method;

public abstract class SafeRefactor {

	protected String tmpFolder = "";

	public String getTmpFolder() {
		return tmpFolder;
	}

	public void setTmpFolder(String tmpFolder) {
		this.tmpFolder = tmpFolder;
	}

	protected TransformationAnalyzer analyzer;
	protected AbstractTestGeneratorAdapter generator;
	protected Parameters parameters;
	protected Report report;
	protected Compiler sourceTestCompiler;
	protected Compiler targetTestCompiler;
	protected Compiler sourceCompiler;
	protected Compiler targetCompiler;
	protected TestExecutor testSourceTask;
	protected TestExecutor testAgainSourceTask;
	protected TestExecutor testTargetTask;
	protected TestComparator comparator;
	private saferefactor.core.analysis.Report analysisReport;
	protected List<Method> methodsToTest;
	protected Project source;
	protected Project target;
	protected CoverageMeter meter;

	protected Logger logger;
	protected File bin_target;
	protected File bin_source;
	protected File sourceReport;
	private File testPath;
	protected File targetReport;
	protected File sourceSecondReport;
	public static final String TARGET_REPORT = Constants.TESTS_DIR + "/target";
	public static final String SOURCE_SECOND_REPORT = Constants.TESTS_DIR
			+ "/source2";
	public static final String SOURCE_REPORT = Constants.TESTS_DIR + "/source";

	protected static final String TESTS_BIN_TARGET = Constants.TESTS_DIR
			+ "/bin_target";

	protected static final String TESTS_BIN_SOURCE = Constants.TESTS_DIR
			+ "/bin_source";

	public SafeRefactor() {
		super();
		parameters = new Parameters();
		report = new Report();
	}

	public SafeRefactor(Project source, Project target) {
		this();
		this.source = source;
		this.target = target;
	}

	public SafeRefactor(Project source, Project target, Parameters parameters) {
		this(source, target);
		this.parameters = parameters;

	}

	public void checkTransformation() throws SafeRefactorException {
		try {
			double start = System.currentTimeMillis();
			logger.info("check compilation? " + parameters.isCompileProjects());
			if (parameters.isCompileProjects())
				compileSourceAndTarget();
			if (hasNoCompilationErrors())
				checkBehavioralChanges();
			else
				logger.info("has compilation error");
			generateReport();

			double stop = System.currentTimeMillis();
			double total = ((stop - start) / 1000);
			logger.info("time to check transformation (s): " + total);
			report.setTotalTime(total);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new SafeRefactorException(e.getMessage());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new SafeRefactorException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new SafeRefactorException(e.getMessage());
		}
	}

	protected boolean hasNoCompilationErrors() {
		return (source.isCompile() && target.isCompile())
				|| !parameters.isCompileProjects();
	}

	private void compileSourceAndTarget() throws MalformedURLException,
			FileNotFoundException, SafeRefactorException {

		double start = System.currentTimeMillis();

		if (source.getLibFolder() != null)
			sourceCompiler.setLibClasspath(source.getLibFolder()
					.getAbsolutePath());
		// if (parameters.getCompilerPath() != null) {
		// sourceCompiler.setCompilerPath(parameters.getCompilerPath());
		// targetCompiler.setCompilerPath(parameters.getCompilerPath());
		// }
		// if (parameters.getBuildPath() != null) {
		// sourceCompiler.setBuildPath(parameters.getBuildPath());
		// targetCompiler.setBuildPath(parameters.getBuildPath());
		// }

		// boolean isSourceCompilable =
		sourceCompiler.compile(source.getSrcFolder().getAbsolutePath(), source
				.getBuildFolder().getAbsolutePath());
		// source.setCompile(isSourceCompilable);

		if (target.getLibFolder() != null)
			targetCompiler.setLibClasspath(target.getLibFolder()
					.getAbsolutePath());
		// boolean isTargetCompilable =
		targetCompiler.compile(target.getSrcFolder().getAbsolutePath(), target
				.getBuildFolder().getAbsolutePath());
		//target.setCompile(isTargetCompilable);

		double stop = System.currentTimeMillis();
		double total = ((stop - start) / 1000);
		logger.info("time to compile (s): " + total);
	}

	private void checkBehavioralChanges() throws Exception {

		analyzeTransformation();
		generateTests();
		compileTests();
		runTestsOnSourceAndTargetProjects();
		comparator.compare();
		if (!comparator.getReport().isRefactoring()
				&& parameters.analyzeChangeMethods()) {
			String testSourceDir = getTmpFolder() + Constants.SEPARATOR
					+ Constants.TESTS_DIR + Constants.SEPARATOR;
			Set<Method> changedMethods = comparator
					.identifyMethodsWithBehavioralChanges(testSourceDir);
			report.setChangedMethods(new ArrayList<Method>(changedMethods));
		}

		if (parameters.checkCoverage())
			meter.checkTestCoverage();
	}

	protected abstract void generateReport();

	private void runTestsOnSourceAndTargetProjects()
			throws FileNotFoundException {

		double start = System.currentTimeMillis();

		// ExecutorService executor = Executors.newSingleThreadExecutor();
		ExecutorService executor = Executors.newFixedThreadPool(1);

		testSourceTask.setReportPath(this.sourceReport.getAbsolutePath());
		testSourceTask.setTests(this.bin_source.getAbsolutePath());
		executor.execute(testSourceTask);

		if (testAgainSourceTask != null) {
			testAgainSourceTask.setReportPath(this.sourceSecondReport
					.getAbsolutePath());
			testAgainSourceTask.setTests(this.bin_source.getAbsolutePath());
			executor.execute(testAgainSourceTask);

		}
		testTargetTask.setReportPath(targetReport.getAbsolutePath());
		testTargetTask.setTests(this.bin_target.getAbsolutePath());
		executor.execute(testTargetTask);
		executor.shutdown();

		while (!executor.isTerminated()) {
		}
		double stop = System.currentTimeMillis();
		double total = ((stop - start) / 1000);
		logger.info("time to run tests (s): " + total);

	}

	private void compileTests() throws MalformedURLException,
			FileNotFoundException, SafeRefactorException {
		double start = System.currentTimeMillis();

		sourceTestCompiler.setBinClasspath(source.getBuildFolder()
				.getAbsolutePath());
		if (source.getLibFolder() != null)
			sourceTestCompiler.setLibClasspath(source.getLibFolder()
					.getAbsolutePath());
		sourceTestCompiler.compile(getTestPath().getAbsolutePath(),
				bin_source.getAbsolutePath());

		targetTestCompiler.setBinClasspath(target.getBuildFolder()
				.getAbsolutePath());

		if (target.getLibFolder() != null)
			targetTestCompiler.setLibClasspath(target.getLibFolder()
					.getAbsolutePath());
		targetTestCompiler.compile(getTestPath().getAbsolutePath(),
				bin_target.getAbsolutePath());

		double stop = System.currentTimeMillis();
		double total = ((stop - start) / 1000);
		logger.info("time to compile tests (s): " + total);

	}

	private void generateTests() throws FileNotFoundException {
		double start = System.currentTimeMillis();

		generator.generateTestsForMethodList(methodsToTest,
				parameters.getTimeLimit(),
				parameters.getTestGeneratorParameters());

		double stop = System.currentTimeMillis();
		double total = ((stop - start) / 1000);
		logger.info("time to geenrate tests (s): " + total);
	}

	private void analyzeTransformation() throws Exception {
		double start = System.currentTimeMillis();
		analysisReport = analyzer.analyze(parameters.isEnableImpactAnalysis());
		methodsToTest = analysisReport.getMethodsToTest();

		double stop = System.currentTimeMillis();
		double total = ((stop - start) / 1000);
		logger.info("time to identify common methods (s): " + total);
	}

	public Report getReport() {
		return report;
	}

	public File getTestPath() {
		return testPath;
	}

	public void setTestPath(File testPath) {
		this.testPath = testPath;
	}
}