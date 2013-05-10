package saferefactor.core.analysis;

import saferefactor.core.analysis.impact.ASMBasedDependenceAnalyzer;
import saferefactor.core.util.Project;

public class ASMBasedDependenceAnalyzerFactory extends AnalyzerFactory {

	@Override
	public TransformationAnalyzer createAnalyzer(Project source,
			Project target, String tmpDir, String clazz) {
		return new ASMBasedDependenceAnalyzer(source, target, tmpDir, clazz);
	}

}
