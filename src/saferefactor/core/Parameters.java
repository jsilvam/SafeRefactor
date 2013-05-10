package saferefactor.core;

import java.util.ArrayList;
import java.util.List;

public class Parameters {
	
	//default timelimit
	private double timeLimit = 2;
	
	private boolean verbose = false;
	
	private boolean dependenceAnalaysis = false;
	
	private boolean analyzeChangeMethods = false;

	private boolean enableImpactAnalysis = false;
	
	private boolean fork = true;
	
	private boolean ExecuteTwiceOnSource = false;
	
	private boolean compileProjects = true;
	
	private List<String> testGeneratorParameters = new ArrayList<String>();
	
//	private String buildPath;

	private boolean checkCoverage;

	private String clazz;
	
	public void addTestGeneratorParameter(String parameter) {
		testGeneratorParameters.add(parameter);
	}

	public List<String> getTestGeneratorParameters() {
		return testGeneratorParameters;
	}

	public void setTestGeneratorParameters(List<String> testGeneratorParameters) {
		this.testGeneratorParameters = testGeneratorParameters;
	}

	public double getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(double timeLimit) {
		this.timeLimit = timeLimit;
	}

	public boolean isExecuteTwiceOnSource() {
		return ExecuteTwiceOnSource;
	}

	public void setExecuteTwiceOnSource(boolean executeTwiceOnSource) {
		ExecuteTwiceOnSource = executeTwiceOnSource;
	}

	public void setCheckCoverage(boolean checkCoverage) {
		this.checkCoverage = checkCoverage;		
	}
	
	public boolean checkCoverage() {
		return this.checkCoverage;
	}

	public boolean isCompileProjects() {
		return compileProjects;
	}

	public void setCompileProjects(boolean compileProjects) {
		this.compileProjects = compileProjects;
	}

//	public String getCompilerPath() {
//		return compilerPath;
//	}
//
//	public void setCompilerPath(String compilerPath) {
//		this.compilerPath = compilerPath;
//	}
//
//	public String getBuildPath() {
//		return buildPath;
//	}
//
//	public void setBuildPath(String buildPath) {
//		this.buildPath = buildPath;
//	}

	public boolean isFork() {
		return fork;
	}

	public void setFork(boolean fork) {
		this.fork = fork;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public boolean analyzeChangeMethods() {
		return analyzeChangeMethods;
	}

	public void setAnalyzeChangeMethods(boolean analyzeChangeMethod) {
		this.analyzeChangeMethods = analyzeChangeMethod;
	}

	public boolean isEnableImpactAnalysis() {
		return enableImpactAnalysis;
	}

	public void setEnableImpactAnalysis(boolean enableImpactAnalysis) {
		this.enableImpactAnalysis = enableImpactAnalysis;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getClazz() {
		return this.clazz;
	}

	public boolean isDependenceAnalaysis() {
		return dependenceAnalaysis;
	}

	public void setDependenceAnalaysis(boolean dependenceAnalaysis) {
		this.dependenceAnalaysis = dependenceAnalaysis;
	}

	

}
