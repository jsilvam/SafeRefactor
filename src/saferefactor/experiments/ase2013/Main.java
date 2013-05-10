package saferefactor.experiments.ase2013;

import java.util.ArrayList;
import java.util.List;

import saferefactor.core.Report;
import saferefactor.experiments.SafeRefactorFacade;

public class Main {

	public static void main(String[] args) {

		String[] classes = {
				"org.apache.commons.collections.ArrayStack",
				"org.apache.commons.collections.BinaryHeap",
				"org.apache.commons.collections.BoundedFifoBuffer",
				"org.apache.commons.collections.BufferOverflowException",
				"org.apache.commons.collections.BufferUnderflowException",
				"org.apache.commons.collections.ExtendedProperties",
				"org.apache.commons.collections.FunctorException",
				"org.apache.commons.collections.HashBag",
				"org.apache.commons.collections.IteratorUtils",
				"org.apache.commons.collections.LRUMap",
				"org.apache.commons.collections.MultiHashMap",
				"org.apache.commons.collections.ReferenceMap",
				"org.apache.commons.collections.TreeBag",
				"org.apache.commons.collections.UnboundedFifoBuffer",
				"org.apache.commons.collections.bag.HashBag",
				"org.apache.commons.collections.bag.TransformedBag",
				"org.apache.commons.collections.bag.TransformedSortedBag",
				"org.apache.commons.collections.bag.TreeBag",
				"org.apache.commons.collections.bag.UnmodifiableBag",
				"org.apache.commons.collections.bag.UnmodifiableSortedBag",
				"org.apache.commons.collections.bidimap.DualHashBidiMap",
				"org.apache.commons.collections.bidimap.DualTreeBidiMap",
				"org.apache.commons.collections.bidimap.UnmodifiableOrderedBidiMap",
				"org.apache.commons.collections.bidimap.UnmodifiableSortedBidiMap",
				"org.apache.commons.collections.buffer.BoundedFifoBuffer",
				"org.apache.commons.collections.buffer.CircularFifoBuffer",
				"org.apache.commons.collections.buffer.SynchronizedBuffer",
				"org.apache.commons.collections.buffer.TransformedBuffer",
				"org.apache.commons.collections.buffer.UnboundedFifoBuffer",
				"org.apache.commons.collections.buffer.UnmodifiableBuffer",
				"org.apache.commons.collections.collection.UnmodifiableBoundedCollection",
				"org.apache.commons.collections.iterators.ArrayListIterator",
				"org.apache.commons.collections.iterators.ObjectArrayListIterator",
				"org.apache.commons.collections.iterators.UniqueFilterIterator",
				"org.apache.commons.collections.list.TreeList",
				"org.apache.commons.collections.map.CaseInsensitiveMap",
				"org.apache.commons.collections.map.CompositeMap",
				"org.apache.commons.collections.map.DefaultedMap",
				"org.apache.commons.collections.map.FixedSizeMap",
				"org.apache.commons.collections.map.FixedSizeSortedMap",
				"org.apache.commons.collections.map.HashedMap",
				"org.apache.commons.collections.map.LazyMap",
				"org.apache.commons.collections.map.LazySortedMap",
				"org.apache.commons.collections.map.LinkedMap",
				"org.apache.commons.collections.map.ListOrderedMap",
				"org.apache.commons.collections.map.MultiValueMap",
				"org.apache.commons.collections.map.PredicatedMap",
				"org.apache.commons.collections.map.PredicatedSortedMap",
				"org.apache.commons.collections.map.ReferenceIdentityMap",
				"org.apache.commons.collections.map.ReferenceMap",
				"org.apache.commons.collections.map.TransformedMap",
				"org.apache.commons.collections.map.TransformedSortedMap",
				"org.apache.commons.collections.map.UnmodifiableEntrySet",
				"org.apache.commons.collections.map.UnmodifiableMap",
				"org.apache.commons.collections.map.UnmodifiableOrderedMap",
				"org.apache.commons.collections.map.UnmodifiableSortedMap",
				"org.apache.commons.collections.set.TransformedSortedSet",
				"org.apache.commons.collections.set.UnmodifiableSortedSet", };

		double timelimit = 120;
		String sourcePath = "/Users/alan/Dropbox/experiments/ase2013/jaxen/subject/jaxen";
		// sourcePath = "/Users/alan/Documents/workspace/original";
		String targetPath = "/Users/alan/Dropbox/experiments/ase2013/jaxen/mutants/jaxen-m1";
		// targetPath =
		// "/Users/alan/Dropbox/experiments/ase2013/jaxen/subject/jaxen";

		sourcePath = "/Users/alan/Dropbox/ase2013_/artefatos/experiments/commons-collections/original";
		targetPath = sourcePath;

		List<Report> reports = new ArrayList<Report>();

		for (String classe : classes) {
			System.out.println("classe: " + classe);
			try {
				Report report;
				report = SafeRefactorFacade.checkTransformation(sourcePath,
						targetPath, "bin", "src", "", false, timelimit, false,
						classe, true);
				reports.add(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		for (Report report : reports) {
			System.out.println("------REPORT------------");
			System.out.println("equivalent: " + report.isRefactoring());
			// System.out.println(report.getChanges());
			System.out.println(report.getTmpFolder());
		}

	}
}
