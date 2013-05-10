package saferefactor.core.analysis;

import saferefactor.core.analysis.impact.ASMBasedImpactAnalyzer;
import saferefactor.core.util.Project;

public class ASMBasedImpactAnalyzerFactory extends AnalyzerFactory {

	@Override
	public TransformationAnalyzer createAnalyzer(Project source,
			Project target, String tmpDir, String clazz) {
		return new ASMBasedImpactAnalyzer(source, target, tmpDir, clazz);
	}

}
