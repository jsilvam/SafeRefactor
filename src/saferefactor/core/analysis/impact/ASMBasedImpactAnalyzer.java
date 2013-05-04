package saferefactor.core.analysis.impact;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.designwizard.design.ClassNode;
import org.designwizard.design.MethodNode;
import org.designwizard.design.Modifier;
import org.designwizard.exception.InexistentEntityException;
import org.designwizard.main.DesignWizard;

import saferefactor.core.analysis.Report;
import saferefactor.core.analysis.TransformationAnalyzer;
import saferefactor.core.util.Project;
import saferefactor.core.util.ast.ConstructorImp;
import saferefactor.core.util.ast.Method;
import saferefactor.core.util.ast.MethodImp;

public class ASMBasedImpactAnalyzer implements TransformationAnalyzer {

	private final Project source;
	private final Project target;
	private DesignWizard dwSource;
	private DesignWizard dwTarget;
	private List<String> impactedClasses;

	public ASMBasedImpactAnalyzer(Project source, Project target, String tmpDir) {
		this.source = source;
		this.target = target;
	}

	public Report analyze() throws Exception {

		dwSource = new DesignWizard(source.getBuildFolder().getAbsolutePath());
		dwTarget = new DesignWizard(target.getBuildFolder().getAbsolutePath());

		List<String> changedClasses = compareBinaries(this.source
				.getBuildFolder());
		impactedClasses = new ArrayList<String>();
		impactedClasses.addAll(changedClasses);
		
		for (String clazz : changedClasses) {
			addCallersAndCallees(clazz);
		}

		Report result = new Report();

		Set<MethodNode> sourceMethods = dwSource.getAllMethods();
		Set<MethodNode> targetMethods = dwTarget.getAllMethods();

		List<Method> intersection = getIntersection(sourceMethods,
				targetMethods);

		result.setMethodsToTest(intersection);
		return result;
	}

	private void addCallersAndCallees(String clazz) {

		try {
			ClassNode classNode = dwSource.getClass(clazz);

			Set<ClassNode> callerClasses = classNode.getCallerClasses();
			for (ClassNode callerClass : callerClasses) {
				if (callerClass.getClassName().equals(clazz)
						|| this.impactedClasses.contains(callerClass
								.getClassName()))
					continue;
				this.impactedClasses.add(callerClass.getClassName());
				addCallersAndCallees(callerClass.getClassName());
			}

			// Set<ClassNode> calleeClasses = classNode.getCalleeClasses();
			// for (ClassNode calleeClass : calleeClasses) {
			// result.add(calleeClass.getClassName());
			// }
			Set<MethodNode> allMethods = classNode.getAllMethods();

			for (MethodNode methodNode : allMethods) {
				List<ClassNode> parameters = methodNode.getParameters();
				for (ClassNode parameter : parameters) {
					if (impactedClasses.contains(parameter.getClassName()))
						continue;
					impactedClasses.add(parameter.getClassName());
				}
			}

			// Set<ClassNode> subClasses = classNode.getSubClasses();
			// for (ClassNode subClass : subClasses) {
			// result.add(subClass.getClassName());
			// }

		} catch (Exception e) {
			System.out.println("class not found on source: " + clazz);
		}
	}

	private List<String> getParameterList(MethodNode methodNode) {
		List<String> result = new ArrayList<String>();
		List<ClassNode> parameters = methodNode.getParameters();
		if (parameters != null)
			for (ClassNode classNode : parameters) {
				result.add(classNode.getClassName());
			}
		return result;
	}

	private boolean doNotTestTypeOfMethod(MethodNode methodNode) {

		ClassNode declaringClass = methodNode.getDeclaringClass();

		if (this.impactedClasses != null) {
			Method method = convertToMethod(methodNode);
			Set<String> allowedClasses = method.getAllowedClasses();
			boolean doNotTest = true;
			for (String clazz : allowedClasses) {
				if (this.impactedClasses.contains(clazz))
					doNotTest = false;
			}
			if (!this.impactedClasses.contains(declaringClass.getName())
					&& doNotTest)
				return true;
		}

		if (!declaringClass.containsModifiers(Modifier.PUBLIC))
			return true;

		// FIXME check why Design wizard says that this class is public
		if (declaringClass
				.getClassName()
				.contains(
						"com.atlassw.tools.eclipse.checkstyle.config.configtypes.RemoteConfigurationType$RemoteConfigAuthenticator"))
			return true;

		ClassNode targetClass;
		try {
			String className = declaringClass.getClassName();
			targetClass = this.dwTarget.getClass(className);

			if (targetClass.isInterface())
				return true;

			// FIXME Hack to avoid this class. This class is not public on
			// target of jpider, but designwizard says it is.
			if (targetClass.getName().equals(
					"net.javacoding.jspider.core.storage.jdbc.DBUtil"))
				return true;
			if (!targetClass.containsModifiers(Modifier.PUBLIC))
				return true;
		} catch (InexistentEntityException e) {
			// e.printStackTrace();
		}

		// FIXME design wizard says the class has public modifier but it has not
		if (declaringClass
				.getName()
				.equals("com.atlassw.tools.eclipse.checkstyle.builder.PackageObjectFactory"))
			return true;

		// TODO HACK: do not test methods from aspects
		if (declaringClass.toString().contains(".aspectOf()"))
			return true;

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

		// FIXME: to strong. Should not test dispose methods only from java.awt
		// because they stop the application and the test suite
		if (methodNode.getShortName().equals("dispose"))
			return true;
		if (methodNode.getShortName().equals("remove")
				&& declaringClass.getName().equals(
						"org.jhotdraw.app.DefaultSDIApplication"))
			return true;

		// FIXME: see fix me above
		Set<MethodNode> calleeMethods = methodNode.getCalleeMethods();
		for (MethodNode methodNode2 : calleeMethods) {
			if (methodNode2.getShortName().equals("dispose"))
				return true;
		}

		return false;
	}

	private List<Method> getIntersection(Set<MethodNode> sourceMethods,
			Set<MethodNode> targetMethods) throws InexistentEntityException {

		List<Method> result = new ArrayList<Method>();

		Map<String, List<MethodNode>> differentSignaturesSource = new HashMap<String, List<MethodNode>>();
		Map<String, List<MethodNode>> differentSignaturesTarget = new HashMap<String, List<MethodNode>>();
		for (MethodNode sourceNode : sourceMethods) {

			if (doNotTestTypeOfMethod(sourceNode))
				continue;

			if (targetMethods.contains(sourceNode)) {

				MethodNode targetMethod = dwTarget.getMethod(sourceNode
						.getName());

				if (!hasSameReturnTypeAndModifiers(sourceNode, targetMethod))
					continue;

				Method convertToMethod = convertToMethod(sourceNode);

				if (convertToMethod.getAllowedClasses().size() > 0) {
					result.add(convertToMethod);
				}
			} else {
				String name = sourceNode.getShortName();
				List<MethodNode> list = differentSignaturesSource.get(name);
				if (list == null)
					list = new ArrayList<MethodNode>();
				list.add(sourceNode);
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
							convertToMethod
									.setAllowedClasses(new HashSet<String>());
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

	private boolean hasSameReturnTypeAndModifiers(MethodNode sourceMethod,
			MethodNode targetMethod) {
		if (!sourceMethod.getReturnType().getClassName()
				.equals(targetMethod.getReturnType().getClassName()))
			return false;

		Collection<Modifier> modifiers = sourceMethod.getModifiers();
		for (Modifier modifier : modifiers) {
			if (!targetMethod.getModifiers().contains(modifier))
				return false;
		}

		ClassNode declaringClass = targetMethod.getDeclaringClass();
		if (declaringClass
				.getClassName()
				.equals("com.atlassw.tools.eclipse.checkstyle.builder.PackageObjectFactory"))
			System.out.println("class");

		if (!declaringClass.containsModifiers(Modifier.PUBLIC))
			return false;

		if (!declaringClass.getModifiers().contains(Modifier.PUBLIC))
			return false;

		return true;
	}

	private Method convertToMethod(MethodNode methodNode) {

		Method result = null;
		List<String> parameterTypeNames = getParameterList(methodNode);
		ClassNode declaringClass = methodNode.getDeclaringClass();

		Set<String> allowedClasses = new HashSet<String>();
		if (!declaringClass.isAbstract())
			allowedClasses.add(declaringClass.getClassName());
		if (methodNode.isConstructor()) {
			result = new ConstructorImp();
			result.setSimpleName(declaringClass.getClassName());

		} else {
			result = new MethodImp();
			result.setSimpleName(methodNode.getShortName());
			if (methodNode.containsModifiers(Modifier.PUBLIC)) {
				List<String> allowedClasseNames = findAllowedClasses(
						methodNode, declaringClass);

				allowedClasses.addAll(allowedClasseNames);

			}
		}
		result.setParameterList(parameterTypeNames);
		result.setDeclaringClass(declaringClass.getClassName());

		result.setAllowedClasses(allowedClasses);
		return result;
	}

	private List<String> findAllowedClasses(MethodNode methodNode,
			ClassNode classNode) {
		List<String> result = new ArrayList<String>();
		Set<ClassNode> subClasses = classNode.getSubClasses();

		for (ClassNode subClass : subClasses) {
			if (subClass.getAllMethods().contains(methodNode)) {
				result.add(subClass.getClassName());
				result.addAll(findAllowedClasses(methodNode, subClass));
			}
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
		} else if (buildFolder.getName().endsWith(".class")) {
			String absolutePath = buildFolder.getAbsolutePath();
			String root = source.getBuildFolder().getAbsolutePath();
			String fullNameFolder = absolutePath.substring(root.length() + 1);
			File targetFile = new File(target.getBuildFolder(), fullNameFolder);
			if (targetFile.exists()) {
				boolean isEqual = FileUtils.contentEquals(buildFolder,
						targetFile);
				if (!isEqual) {
					String fullName = fullNameFolder.replace("/", ".");
					fullName = fullName.substring(0,
							fullNameFolder.length() - 6);
					result.add(fullName);
				}
			}

		}
		return result;
	}

}
