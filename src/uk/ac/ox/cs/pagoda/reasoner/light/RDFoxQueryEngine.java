package uk.ac.ox.cs.pagoda.reasoner.light;

import java.io.File;
import java.util.Collection;

import org.semanticweb.more.util.Logger_MORe;

import uk.ac.ox.cs.JRDFox.JRDFStoreException;
import uk.ac.ox.cs.JRDFox.Prefixes;
import uk.ac.ox.cs.JRDFox.store.DataStore;
import uk.ac.ox.cs.JRDFox.store.DataStore.StoreType;
import uk.ac.ox.cs.JRDFox.store.DataStore.UpdateType;
import uk.ac.ox.cs.JRDFox.store.Parameters;
import uk.ac.ox.cs.pagoda.query.AnswerTuples;
import uk.ac.ox.cs.pagoda.reasoner.QueryEngine;
import uk.ac.ox.cs.pagoda.reasoner.QueryReasoner;
import uk.ac.ox.cs.pagoda.tracking.AnswerTuplesWriter;
import uk.ac.ox.cs.pagoda.util.MyPrefixes;
import uk.ac.ox.cs.pagoda.util.Timer;

public abstract class RDFoxQueryEngine implements QueryEngine {
	
//	public static final int matNoOfThreads = Runtime.getRuntime().availableProcessors() * 2;
	public static final int matNoOfThreads = (Runtime.getRuntime().availableProcessors()*2 < 16) ? Runtime.getRuntime().availableProcessors()*2 : 16;//byAna
	
	protected String name; 
	protected Prefixes prefixes = MyPrefixes.PAGOdAPrefixes.getRDFoxPrefixes();

	public RDFoxQueryEngine(String name) {
		this.name = name; 
	} 
	
	public abstract DataStore getDataStore();
	public abstract Parameters getParameters();
	
	public abstract void dispose(); 
	
	public void importRDFData(String fileName, String importedFile) {
		if (importedFile == null || importedFile.isEmpty()) return ; 
		Timer t = new Timer(); 
		DataStore store = getDataStore(); 
		try {
			long oldTripleCount = store.getTriplesCount(), tripleCount;
			for (String file: importedFile.split(QueryReasoner.ImportDataFileSeparator))
//				store.importTurtleFile(new File(file), prefixes);
				store.importFiles(new File[]{new File(file)}, prefixes);
			tripleCount = store.getTriplesCount(); 
			Logger_MORe.logDebug(name + " store after importing " + fileName + ": " + tripleCount + " (" + (tripleCount - oldTripleCount) + " new)");
			store.clearRulesAndMakeFactsExplicit();
		} catch (JRDFStoreException e) {
			e.printStackTrace();
		}
		Logger_MORe.logDebug(name + " store finished importing " + fileName + " in " + t.duration() + " seconds.");
	}
	
	public void materialise(String programName, File programFile) {//changed String programText argument to File programFile
		if (programFile == null) return ; 
		Timer t = new Timer();
		DataStore store = getDataStore(); 
		try {
			long oldTripleCount = store.getTriplesCount(), tripleCount;
			store.importFiles(new File[]{programFile}, new Prefixes(), UpdateType.Add, true);
			
			store.applyReasoning();
			tripleCount = store.getTriplesCount(); 
			Logger_MORe.logDebug(name + " store after materialising " + programName + ": " + tripleCount + " (" + (tripleCount - oldTripleCount) + " new)");
			store.clearRulesAndMakeFactsExplicit();
		} catch (JRDFStoreException e) {
			e.printStackTrace();
		}
		Logger_MORe.logDebug(name + " store finished the materialisation of " + programName + " in " + t.duration() + " seconds.");
	}
	
	@Override
	public void evaluate(Collection<String> queryTexts, String answerFile) {
		if (queryTexts == null)
			return ;
		
		int queryID = 0; 
		AnswerTuplesWriter answerWriter = new AnswerTuplesWriter(answerFile); 
		AnswerTuples answerTuples;
		Timer t = new Timer(); 
		try {
			for (String query: queryTexts) {
				t.reset();
				answerTuples = null; 
				try {
					answerTuples = evaluate(query); 
					Logger_MORe.logDebug("time to answer Query " + ++queryID + ": " + t.duration());
					answerWriter.write(answerTuples.getAnswerVariables(), answerTuples);
				} finally {
					if (answerTuples != null) answerTuples.dispose();
				}
			}
		} finally {
			answerWriter.close();
		}
		
		Logger_MORe.logDebug("done computing query answers by RDFox.");
		
	}
	
	public static DataStore createDataStore() {
		DataStore instance = null; 
		try {
//			instance = new DataStore("par-head-n");
//			instance = new DataStore(StoreType.NarrowParallelHead);
			instance = new DataStore(StoreType.ParallelComplexNN);
			instance.setNumberOfThreads(RDFoxQueryEngine.matNoOfThreads);
			instance.initialize();
		} catch (JRDFStoreException e) {
			e.printStackTrace();
		}
		return instance;
	}
	
}
