package saferefactor.core.analysis;

import saferefactor.core.analysis.naive.ASMBasedAnalyzer;
import saferefactor.core.util.Project;

public class ASMBasedAnalyzerFactory extends AnalyzerFactory {

	@Override
	public TransformationAnalyzer createAnalyzer(Project source,
			Project target, String tmpDir) {
		return new ASMBasedAnalyzer(source, target, tmpDir);
	}
}
