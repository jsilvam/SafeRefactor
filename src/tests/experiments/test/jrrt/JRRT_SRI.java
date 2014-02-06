package tests.experiments.test.jrrt;

import io.OutputManager;
import io.OutputManagerASCII;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import saferefactor.safira.analyzer.Tools;
import tests.SRImpact;



public class JRRT_SRI {
		
	
	private static final String WARNING_STATUS = "REFACTORING_INAPPLICABLE";
	private static final String COMPILATION_ERROR = "POST_REFACTOR_NOT_COMPILE";
	private static final String INPUT_COMPILATION_ERROR = "PRE_REFACTOR_NOT_COMPILE";
	private static final String ENGINE_CRASH = "ENGINE_CRASH";
	private static final String NO_ERROR = "NO_ERROR";
	private static final String NULL_POINTER = "NULL_POINTER";
	
	
	List<Integer> compilation_errors = new ArrayList<Integer>();
	List<Integer> input_compilation_errors = new ArrayList<Integer>();
	List<Integer> engine_crash = new ArrayList<Integer>();
	List<Integer> warning_status = new ArrayList<Integer>();
	List<Integer> null_pointer = new ArrayList<Integer>();
	List<Integer> behavioral_changes = new ArrayList<Integer>();
	List<Integer> test_compilation_error = new ArrayList<Integer>();
	
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
			}else if (file.equals(NULL_POINTER)) {
				null_pointer.add(number);
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
		
		file.writeLine("null_pointer");
		file.writeLine(null_pointer.toString());
		
		file.writeLine("behavioral_changes");
		file.writeLine(behavioral_changes.toString());
		
		file.writeLine("test_compilation_error");
		file.writeLine(test_compilation_error.toString());
	}
	
	public void evaluate(String refactoring) throws IOException {
		
		

		String path = "/private/var/folders/uB/uBSypadvEUa5OUqqynr2Tk+++TI/-Tmp-/"+refactoring+"/alloyASTGenerator1/";
		path = "/Users/melmongiovi/Documents/doutorado/experimentos/eclipse/pushdownmethod/alloyASTGenerator_skip10/";
		path = "/Users/melmongiovi/Documents/doutorado/experimentos/jrrt3/pullupmethod_noSkip_new/PullUpMethod0/";
		path = "/Users/melmongiovi/Documents/doutorado/experimentos/jrrt3/pushdownmethod/skip20/";
		String name ="jrrt3";
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

		long totalTimeExper = System.currentTimeMillis();
//		long time = System.currentTimeMillis();
		int [] bc = {1, 30, 33, 36, 37, 38, 39, 51, 54, 56, 57, 58, 74, 88, 93, 94, 96, 110, 115, 146, 171, 178, 179, 181, 183, 186, 191, 196, 232, 235, 266, 322, 323, 324, 352, 353, 382, 385, 386, 391, 415, 438, 440, 442, 445, 446, 460, 461, 483, 489, 495, 502, 503, 504, 525, 526, 527, 528, 538, 539, 540, 568, 574, 618, 627, 641, 644, 655, 682, 684, 686, 689, 690, 691, 697, 708, 718, 719, 721, 722, 723, 736, 740, 743, 754, 755, 764, 773, 774, 780, 800, 817, 818, 820, 832, 834, 837, 840, 856, 861, 870, 877, 896, 901, 910, 913, 932, 933, 934, 936, 946, 949, 981, 994, 996, 1000, 1001, 1027};
//		for (Integer number : numbers) {
		for (int number = 1; number <= 1027	; number++) {
//			for (int i : bc) {
//				if (number == i) {
			
			try {
			String source = path + "test"+number+"/in/";
			
			String target = path + "test"+number+"/out/jrrt/";
			
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
			if (c.isTestCompilationError()) {
				test_compilation_error.add(number);
//				c = new SRImpact(tests, source, target, lib, timeLimit,bin, true, "m_0");
//				isRefactoring = c.isRefactoring();
			}
			System.out.println(isRefactoring);
			System.out.println("is compilation error? "+c.isTestCompilationError());
			
//			c.getIa().print();
			
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
//			}
//			}
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

		
		JRRT_SRI e = new JRRT_SRI();
		e.evaluate("pushdownmethod");
	}
	
	
}
