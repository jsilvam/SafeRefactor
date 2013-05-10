package saferefactor.core.analysis;

import saferefactor.core.util.Project;

public abstract class AnalyzerFactory {

	public static AnalyzerFactory getFactory(boolean impactAnalysis, boolean dependenceAnalaysis) {
		if (impactAnalysis)
			return new ASMBasedImpactAnalyzerFactory();
		if (dependenceAnalaysis)
			return new ASMBasedDependenceAnalyzerFactory();
		if (AnalyzerConfiguration.getCurrentAnalyzer() == Analyzer.ASM_BASED)
			return new ASMBasedAnalyzerFactory();
		else
			return new ReflectionBasedAnalyzerFactory();
	}

	enum Analyzer {
		REFLECTION_BASED, ASM_BASED
	}

	static class AnalyzerConfiguration {

		static Analyzer getCurrentAnalyzer() {
			return Analyzer.ASM_BASED;
		}
	}

	public abstract TransformationAnalyzer createAnalyzer(Project source,
			Project target, String tmpDir, String string);

}
