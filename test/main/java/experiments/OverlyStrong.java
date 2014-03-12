package experiments;
import io.OutputManager;
import io.OutputManagerASCII;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import saferefactor.core.analysis.safira.analyzer.Tools;
import saferefactor.util.SRImpact;



public class OverlyStrong {
	
	private static final String WARNING_STATUS = "REFACTORING_INAPPLICABLE";
	private static final String COMPILATION_ERROR = "POST_REFACTOR_NOT_COMPILE";
	private static final String INPUT_COMPILATION_ERROR = "PRE_REFACTOR_NOT_COMPILE";
	private static final String ENGINE_CRASH = "REFACTORING_EXECUTION_ERROR";
	private static final String POSTCONDITION_FAILURE = "POSTCONDITION_FAILURE";
	private static final String NO_ERROR = "NO_ERROR";
	private static final String ENGINE_CRASH_ = "ENGINE_CRASH";
	private static final String NULL_POINTER = "NULL_POINTER";
	
	List<Integer> overly_strong_conditions_eclipse = new ArrayList<Integer>();
	List<Integer> overly_strong_conditions_jrrt = new ArrayList<Integer>();
	
	public void writeMessages(OutputManager file) throws IOException {
		file.writeLine("overly_strong_conditions_eclipse");
		file.writeLine(overly_strong_conditions_eclipse.toString());
		
		file.writeLine("overly_strong_conditions_jrrt");
		file.writeLine(overly_strong_conditions_jrrt.toString());
	}
	
	public String getError(String dir, int number) {
		File testDir = new File(dir);
		String[] files = testDir.list();
		for (String file : files) {
			if (file.equals(WARNING_STATUS) || file.equals(COMPILATION_ERROR) ||
					file.equals(INPUT_COMPILATION_ERROR) || file.equals(ENGINE_CRASH) ||
					file.equals(POSTCONDITION_FAILURE) || file.equals(ENGINE_CRASH_) ||
					file.equals(NULL_POINTER)) {
				return file;
			}
		}
		return NO_ERROR;
	}
	
	public void evaluate(String refactoring) throws IOException {
		
		int skip = 20;
		
		String eclipsePath = "/Users/melmongiovi/Documents/doutorado/experimentos/eclipse/pushdownmethod/skip"+skip+"/";
		String jrrtPath = "/Users/melmongiovi/Documents/doutorado/experimentos/jrrt/pushdownmethod/skip"+skip+"/";
		int numberP = 1027;
		
		String name = "overly_strong_conditions";
			
		String timeLimit = "0.3";
		String pathOut = Tools.getAspectPath();
		String arquivoMessages = pathOut +"/SCP/SRI/"+name+"/"+name+"_jrrt1_"+refactoring+skip+" "+timeLimit+".txt";
		OutputManager outMessages = new OutputManagerASCII(arquivoMessages);
		outMessages.createFile();
		
		long totalTime = System.currentTimeMillis();
		
		for (int number = 1; number <= numberP	; number++) {
			
			
			String sourceE = eclipsePath + "test"+number+"/in/";
			String targetE = eclipsePath + "test"+number+"/out/eclipse/";
			
			String sourceJ = jrrtPath + "test"+number+"/in/";
			String targetJ = jrrtPath + "test"+number+"/out/jrrt/";
			
			String errorE = getError(targetE, number);
			String errorJ = getError(targetJ, number);
			
			String source = "";
			String target = "";
			
			if (errorE.equals(WARNING_STATUS) && errorJ.equals(NO_ERROR)) {
				//roda jrrt
				source = sourceJ;
				target = targetJ;
			} else if (errorJ.equals(WARNING_STATUS) && errorE.equals(NO_ERROR)) {
				//roda eclipse
				source = sourceE;
				target = targetE;
			}
			
			if (!source.equals("")) {
				//alguma ferramenta pode ter overly strong se o resultado do SRI for true
				System.out.println("sub "+number);
				
				String tests = "";
				String lib = "";
				String bin = "";
				
				SRImpact c;
				c = new SRImpact(tests, source, target, lib, timeLimit,bin, true);
				boolean isRefactoring = c.isRefactoring();
//				if (c.isTestCompilationError()) {
//					c = new SRImpact(tests, source, target, lib, timeLimit,bin, true, "m_0");
//					isRefactoring = c.isRefactoring();
//				}
				System.out.println(isRefactoring);
				
				if (isRefactoring) {
					
					if (target.contains("eclipse")) {
						overly_strong_conditions_jrrt.add(number);
					} else {
						overly_strong_conditions_eclipse.add(number);
					}
				}
			}
		}
		totalTime = System.currentTimeMillis() - totalTime;
		writeMessages(outMessages);
		outMessages.closeFile();
		System.out.println(totalTime);
	}
	public static void main(String[] args) throws IOException {
		OverlyStrong e = new OverlyStrong();
		e.evaluate("pushdownmethod");
	}
}
