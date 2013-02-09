package saferefactor.core.analysis.naive;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.designwizard.design.ClassNode;
import org.designwizard.design.MethodNode;
import org.designwizard.design.Modifier;
import org.designwizard.main.DesignWizard;

import saferefactor.core.analysis.Report;
import saferefactor.core.analysis.TransformationAnalyzer;
import saferefactor.core.util.Project;
import saferefactor.core.util.ast.ConstructorImp;
import saferefactor.core.util.ast.Method;
import saferefactor.core.util.ast.MethodImp;

public class ASMBasedAnalyzer implements TransformationAnalyzer {

	private final Project source;
	private final Project target;
	private DesignWizard dwSource;
	private DesignWizard dwTarget;
	private List<String> changedClasses;

	public ASMBasedAnalyzer(Project source, Project target, String tmpDir) {
		this.source = source;
		this.target = target;
	}

	public Report analyze(boolean enableOccOptimization) throws Exception {
		if (enableOccOptimization)
			this.changedClasses = compareBinaries(this.source.getSrcFolder());

		Report result = new Report();
		dwSource = new DesignWizard(source.getBuildFolder().getAbsolutePath());
		dwTarget = new DesignWizard(target.getBuildFolder().getAbsolutePath());

		Set<MethodNode> sourceMethods = dwSource.getAllMethods();
		Set<MethodNode> targetMethods = dwTarget.getAllMethods();
		List<Method> intersection = getIntersection(sourceMethods,
				targetMethods);

		result.setMethodsToTest(intersection);
		return result;
	}

	private List<String> getParameterList(MethodNode methodNode) {
		List<String> result = new ArrayList<String>();
		List<ClassNode> parameters = methodNode.getParameters();
		for (ClassNode classNode : parameters) {
			result.add(classNode.getClassName());
		}
		return result;
	}

	private boolean doNotTestTypeOfMethod(MethodNode methodNode) {

		if (this.changedClasses != null) {
			ClassNode declaringClass = methodNode.getDeclaringClass();

			if (!this.changedClasses.contains(declaringClass.getName()))
				return true;
		}

		boolean isPublic = false;
		Collection<Modifier> methodModifiers = methodNode.getModifiers();
		for (Modifier modifier : methodModifiers) {
			if (modifier == Modifier.PUBLIC) {
				isPublic = true;
				break;
			}
		}
		if (!isPublic)
			return true;
		isPublic = false;
		// do not consider methods declared in interface
		// only consider when it its declared in the implemented class
		if (methodNode.getDeclaringClass().isInterface())
			return true;
		if (methodNode.getDeclaringClass().isAbstract())
			return true;
		Collection<Modifier> modifiers = methodNode.getDeclaringClass()
				.getModifiers();
		isPublic = false;
		for (Modifier modifier : modifiers) {
			if (modifier == Modifier.PUBLIC) {
				isPublic = true;
				break;
			}
		}
		if (!isPublic)
			return true;

		if (methodNode.getDeclaringClass().isInnerClass()) {
			ClassNode outerClass = methodNode.getDeclaringClass()
					.getOuterClass();
			if (outerClass.getVisibility() != null
					&& !outerClass.getVisibility().equals(Modifier.PUBLIC))
				return true;
		}

		ClassNode returnType = methodNode.getReturnType();
		Modifier visibility = returnType.getVisibility();
		if (visibility != null && !visibility.equals(Modifier.PUBLIC)) {
			return true;
		}

		return false;
	}

	private List<Method> getIntersection(Set<MethodNode> sourceMethods,
			Set<MethodNode> targetMethods) {

		List<Method> result = new ArrayList<Method>();

		Map<String, List<MethodNode>> differentSignaturesSource = new HashMap<String, List<MethodNode>>();
		Map<String, List<MethodNode>> differentSignaturesTarget = new HashMap<String, List<MethodNode>>();
		for (MethodNode methodNode : sourceMethods) {

			if (doNotTestTypeOfMethod(methodNode))
				continue;

			if (targetMethods.contains(methodNode)) {
				Method convertToMethod = convertToMethod(methodNode);
				result.add(convertToMethod);
			} else {
				String name = methodNode.getShortName();
				List<MethodNode> list = differentSignaturesSource.get(name);
				if (list == null)
					list = new ArrayList<MethodNode>();
				list.add(methodNode);
				differentSignaturesSource.put(name, list);
			}
		}

		for (MethodNode methodNode : targetMethods) {
			if (!sourceMethods.contains(methodNode)) {
				String name = methodNode.getShortName();
				List<MethodNode> list = differentSignaturesTarget.get(name);
				if (list == null)
					list = new ArrayList<MethodNode>();
				list.add(methodNode);
				differentSignaturesTarget.put(name, list);
			}
		}

		for (Entry<String, List<MethodNode>> entry : differentSignaturesSource
				.entrySet()) {
			String key = entry.getKey();
			List<MethodNode> targetList = differentSignaturesTarget.get(key);
			if (targetList == null)
				continue;

			List<MethodNode> sourceList = entry.getValue();
			for (MethodNode methodNode : sourceList) {
				List<ClassNode> sourceParameters = methodNode.getParameters();
				for (MethodNode targetMethod : targetList) {
					List<ClassNode> targetParameters = targetMethod
							.getParameters();
					if (sourceParameters.equals(targetParameters)) {

						ClassNode declaringClass = methodNode
								.getDeclaringClass();
						ClassNode targetDeclaringClass = targetMethod
								.getDeclaringClass();

						if (isSuper(declaringClass, targetDeclaringClass)) {
							Method convertToMethod = convertToMethod(methodNode);
							int indexOf = result.indexOf(convertToMethod);
							if (indexOf >= 0) {
								result.get(indexOf)
										.getAllowedClasses()
										.add(targetDeclaringClass
												.getClassName());
							} else {
								convertToMethod.getAllowedClasses().add(
										targetDeclaringClass.getClassName());
								result.add(convertToMethod);
							}

						}
					}
				}

			}

		}

		return result;
	}

	private Method convertToMethod(MethodNode methodNode) {

		Method result = null;
		List<String> parameterTypeNames = getParameterList(methodNode);
		if (methodNode.isConstructor()) {
			result = new ConstructorImp();
			result.setDeclaringClass(methodNode.getDeclaringClass()
					.getClassName());
			result.setSimpleName(methodNode.getDeclaringClass().getClassName());
			result.setParameterList(parameterTypeNames);
			Set<String> allowedClasses = new HashSet<String>();
			allowedClasses.add(methodNode.getDeclaringClass().getClassName());
			result.setAllowedClasses(allowedClasses);
		} else {
			result = new MethodImp();
			result.setDeclaringClass(methodNode.getDeclaringClass()
					.getClassName());
			result.setSimpleName(methodNode.getShortName());
			result.setParameterList(parameterTypeNames);
			Set<String> allowedClasses = new HashSet<String>();

			allowedClasses.add(methodNode.getDeclaringClass().getClassName());

			result.setAllowedClasses(allowedClasses);
		}
		return result;
	}

	private boolean isSuper(ClassNode declaringClass,
			ClassNode targetDeclaringClass) {

		ClassNode superClass = targetDeclaringClass.getSuperClass();

		if (superClass.getName().equals(declaringClass.getName())) {
			return true;
		}

		return false;
	}

	private List<String> compareBinaries(File buildFolder) throws IOException {
		List<String> result = new ArrayList<String>();
		if (buildFolder.isDirectory()) {
			File[] listFiles = buildFolder.listFiles();
			for (File file : listFiles) {
				List<String> compareBinaries = compareBinaries(file);
				result.addAll(compareBinaries);
			}
		} else if (buildFolder.getName().endsWith(".java")) {
			String absolutePath = buildFolder.getAbsolutePath();
			String root = source.getBuildFolder().getAbsolutePath();
			String fullNameFolder = absolutePath.substring(root.length() + 1);
			File targetFile = new File(target.getSrcFolder(), fullNameFolder);
			if (targetFile.exists()) {
				boolean isEqual = FileUtils.contentEquals(buildFolder,
						targetFile);
				if (!isEqual) {
					String fullName = fullNameFolder.replace("/", ".");
					fullName = fullName.substring(0,
							fullNameFolder.length() - 5);
					result.add(fullName);
				}
			}

		}
		return result;
	}

	

}
