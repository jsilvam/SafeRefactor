package saferefactor.core.analysis;

import saferefactor.core.analysis.naive.ReflectionBasedAnalyzer;
import saferefactor.core.util.Project;

public class ReflectionBasedAnalyzerFactory extends AnalyzerFactory {

	@Override
	public TransformationAnalyzer createAnalyzer(Project source,
			Project target, String tmpDir, String clazz) {
		return new ReflectionBasedAnalyzer(source, target,tmpDir);
	}

	
}
