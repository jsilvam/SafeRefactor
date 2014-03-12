package experiments;

import io.OutputManager;
import io.OutputManagerASCII;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import saferefactor.core.analysis.safira.analyzer.Tools;
import saferefactor.util.SRImpact;


public class Eclipse_SRI {
		
	
	private static final String WARNING_STATUS = "REFACTORING_INAPPLICABLE";
	private static final String COMPILATION_ERROR = "POST_REFACTOR_NOT_COMPILE";
	private static final String INPUT_COMPILATION_ERROR = "PRE_REFACTOR_NOT_COMPILE";
	private static final String ENGINE_CRASH = "REFACTORING_EXECUTION_ERROR";
	private static final String POSTCONDITION_FAILURE = "POSTCONDITION_FAILURE";
	private static final String NO_ERROR = "NO_ERROR";
	
	List<Integer> compilation_errors = new ArrayList<Integer>();
	List<Integer> input_compilation_errors = new ArrayList<Integer>();
	List<Integer> engine_crash = new ArrayList<Integer>();
	List<Integer> warning_status = new ArrayList<Integer>();
	List<Integer> pos_condition_failure = new ArrayList<Integer>();
	List<Integer> behavioral_changes = new ArrayList<Integer>();
		
	public String getError(String dir, int number) {
		File testDir = new File(dir);
		String[] files = testDir.list();
		for (String file : files) {
			if (file.equals(WARNING_STATUS)) {
				warning_status.add(number);
				return file;
			} else if (file.equals(COMPILATION_ERROR)) {
				compilation_errors.add(number);
				return file;
			}else if(file.equals(INPUT_COMPILATION_ERROR)) {
				input_compilation_errors.add(number);
				return file;
			} else if (file.equals(ENGINE_CRASH)) {
				engine_crash.add(number);
				return file;
			} else if(file.equals(POSTCONDITION_FAILURE)) {
				pos_condition_failure.add(number);
				return file;
			}
		}
		return NO_ERROR;
	}
	
	public void writeMessages(OutputManager file) throws IOException {
		file.writeLine("compilation_errors");
		file.writeLine(compilation_errors.toString());
		
		file.writeLine("input_compilation_errors");
		file.writeLine(input_compilation_errors.toString());
		
		file.writeLine("engine_crash");
		file.writeLine(engine_crash.toString());
		
		file.writeLine("warning_status");
		file.writeLine(warning_status.toString());
		
		file.writeLine("pos_condition_failure");
		file.writeLine(pos_condition_failure.toString());
		
		file.writeLine("behavioral_changes");
		file.writeLine(behavioral_changes.toString());
	}
	
	
	public void evaluate(String refactoring) throws IOException {
		
		
		String path = "/private/var/folders/uB/uBSypadvEUa5OUqqynr2Tk+++TI/-Tmp-/"+refactoring+"/alloyASTGenerator1/";
		path = "/Users/melmongiovi/Documents/doutorado/experimentos/eclipse/pushdownmethod/skip20/";
//		path = "/Users/melmongiovi/Documents/doutorado/experimentos/jrrt3/pullupmethod_noSkip/";
//		path = "/Users/melmongiovi/Documents/doutorado/experimentos/eclipse/pullupmethod/noskip/";
		String name ="eclipse";
		String timeLimit = "0.3";
		
		String pathOut = Tools.getAspectPath();
		String arquivoMessages = pathOut +"/SCP/SRI/"+name+"/"+name+"_sri_message_"+refactoring+"_"+timeLimit+".txt";
		OutputManager outMessages = new OutputManagerASCII(arquivoMessages);
		outMessages.createFile();
		
		String arquivoResult = pathOut +"/SCP/SRI/"+name+"/"+name+"_sri_result_"+refactoring+"_"+timeLimit+".txt";
		OutputManager outResult = new OutputManagerASCII(arquivoResult);
		outResult.createFile();
		
		String arquivoMethods = pathOut +"/SCP/SRI/"+name+"/"+name+"_sri_methods_"+refactoring+"_"+timeLimit+".txt";
		OutputManager outMethods = new OutputManagerASCII(arquivoMethods);
		outMethods.createFile();
		
		String arquivoTime = pathOut +"/SCP/SRI/"+name+"/"+name+"_sri_time_"+refactoring+"_"+timeLimit+".txt";
		OutputManager outTime = new OutputManagerASCII(arquivoTime);
		outTime.createFile();
		
		
		
		
		
//		List<Integer> numbers = cal.calculate();
		
		long totalTimeExper = System.currentTimeMillis();
//		long time = System.currentTimeMillis();
		
//		for (Integer number : numbers) {
		int [] bc = { 18, 61, 133, 161, 181, 185, 219, 234, 257, 268, 282, 283, 296, 297, 303, 325, 332, 333, 368, 369, 381, 410, 454, 499, 534, 535, 555, 557, 577, 599, 600, 628, 649, 650, 699, 742, 756, 766, 800, 860, 870, 871, 876, 904};
					  System.out.println(bc.length);
		for (int number = 1; number <= 1027	; number++) {
			for (int i : bc) {
			if (number != i) continue;	
			
			try {
			String source = path + "test"+number+"/in/";
			
			String target = path + "test"+number+"/out/eclipse/";
			
			if (!getError(target, number).equals(NO_ERROR)) {
				continue;
			}
			
			int numberOfTests = 0;
			int affectedTests = 0;
			
			long randoopTime = 0;
			
			 
			int methods = 0;
			int impactedMethods = 0;
			boolean isRefactoring = true;
			
			String tests = "";
			String lib = "";
			String bin = "";
//			long time = System.currentTimeMillis();	
			
			SRImpact c;
			long analysisTime;
			long totalTime;
			c = new SRImpact(tests, source, target, lib, timeLimit,bin, true);
			totalTime = c.getTotalTime()/1000;
			isRefactoring = c.isRefactoring();
			System.out.println(isRefactoring);
			System.out.println("is compilation error? "+c.isTestCompilationError());
			
			c.getIa().print();
			
			analysisTime = c.getAnalysisTime()/1000;
			randoopTime = c.getRandoopTime()/1000;
			methods = c.getIa().getMethodsToGenerateTests();
			impactedMethods = c.getIa().getImpactedMethods();
			numberOfTests = 0;
			
			//calcular os testes afetados, os testes vao ser gerados e compilados
			
			
			if (!isRefactoring) {
				behavioral_changes.add(number);
			}
				
			outResult.writeLine(""+isRefactoring);
			outMethods.writeLine(""+methods);
			outTime.writeLine(""+totalTime);
			
			
			}catch(Exception e) {
				outResult.closeFile();
				outMethods.closeFile();
				outTime.closeFile();
			}
			}
		}
			totalTimeExper = System.currentTimeMillis() - totalTimeExper;
			System.out.println("totalTime "+totalTimeExper);
			writeMessages(outMessages);
			outResult.closeFile();
			outMethods.closeFile();
			outTime.closeFile();
			outMessages.closeFile();
		
		
	}
	public static void main(String[] args) throws IOException {
		Eclipse_SRI e = new Eclipse_SRI();
		e.evaluate("pushdownmethod");
	}
	
	
}
