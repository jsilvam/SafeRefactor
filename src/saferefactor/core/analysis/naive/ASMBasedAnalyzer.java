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
import org.designwizard.exception.InexistentEntityException;
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
	private List<String> impactedClasses;

	public ASMBasedAnalyzer(Project source, Project target, String tmpDir) {
		this.source = source;
		this.target = target;
	}

	public Report analyze(boolean enableOccOptimization) throws Exception {

		dwSource = new DesignWizard(source.getBuildFolder().getAbsolutePath());
		dwTarget = new DesignWizard(target.getBuildFolder().getAbsolutePath());

		if (enableOccOptimization) {

			List<String> changedClasses = compareBinaries(this.source
					.getBuildFolder());
			impactedClasses = new ArrayList<String>();
			impactedClasses.addAll(changedClasses);

			for (String clazz : changedClasses) {
				// TODO do the same with dwTarget?
				try {
					ClassNode classNode = dwSource.getClass(clazz);

					Set<ClassNode> calleeClasses = classNode.getCalleeClasses();
					for (ClassNode calleeClass : calleeClasses) {
						impactedClasses.add(calleeClass.getClassName());
					}
					// Set<ClassNode> subClasses = classNode.getSubClasses();
					// for (ClassNode subClass : subClasses) {
					// impactedClasses.add(subClass.getClassName());
					// }
					//
					// Set<ClassNode> callerClasses =
					// classNode.getCallerClasses();
					// for (ClassNode callerClass : callerClasses) {
					// impactedClasses.add(callerClass.getClassName());
					// }
					// //TODO get only declared or all methods?
					// Set<MethodNode> declaredMethods =
					// classNode.getDeclaredMethods();
					// for (MethodNode methodNode : declaredMethods) {
					// Set<ClassNode> callerClasses2 =
					// methodNode.getCallerClasses();
					// for (ClassNode callerClass : callerClasses2) {
					// impactedClasses.add(callerClass.getClassName());
					// }
					// }
				} catch (Exception e) {
					System.out.println("class not found on source: " + clazz);
				}

			}

		}

		Report result = new Report();

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

		ClassNode declaringClass = methodNode.getDeclaringClass();
		if (this.impactedClasses != null) {

			if (!this.impactedClasses.contains(declaringClass.getName()))
				return true;
		}

		if (declaringClass.containsModifiers(Modifier.PRIVATE))
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
			
			if (targetClass.isAbstract() || targetClass.isInterface())
				return true;

			// FIXME Hack to avoid this class. This class is not public on
			// target of jpider, but designwizard says it is.
			if (targetClass.getName().equals(
					"net.javacoding.jspider.core.storage.jdbc.DBUtil"))
				return true;
			if (targetClass.containsModifiers(Modifier.PRIVATE))
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

		
			boolean isPrivate = false;
			Collection<Modifier> methodModifiers = methodNode.getModifiers();
			for (Modifier modifier : methodModifiers) {
				if (modifier == Modifier.PRIVATE) {
					isPrivate = true;
					break;
				}
			}
			if (isPrivate)
				return true;
			isPrivate = false;
			Collection<Modifier> modifiers = methodNode.getDeclaringClass()
					.getModifiers();

			for (Modifier modifier : modifiers) {
				if (modifier == Modifier.PRIVATE) {
					isPrivate = true;
					break;
				}
			}
			if (isPrivate)
				return true;

			if (methodNode.getDeclaringClass().isInnerClass()) {
				ClassNode outerClass = methodNode.getDeclaringClass()
						.getOuterClass();
				if (outerClass.getVisibility() != null
						&& outerClass.getVisibility().equals(Modifier.PRIVATE))
					return true;
			}

			ClassNode returnType = methodNode.getReturnType();
			Modifier visibility = returnType.getVisibility();
			if (visibility != null && visibility.equals(Modifier.PRIVATE)) {
				return true;
			}

		// do not consider methods declared in interface
		// only consider when it its declared in the implemented class
		if (methodNode.getDeclaringClass().isInterface())
			return true;
		if (methodNode.getDeclaringClass().isAbstract())
			return true;

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
				result.add(convertToMethod);
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

		 if (declaringClass.containsModifiers(Modifier.PRIVATE))
		 return false;
		
		 if (declaringClass.getModifiers().contains(Modifier.PRIVATE))
		 return false;

		return true;
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
