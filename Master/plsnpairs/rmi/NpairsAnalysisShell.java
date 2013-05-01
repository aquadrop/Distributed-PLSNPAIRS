package rmi;

import pls.chrome.shared.ProgressDialogWatcher;
import pls.shared.GlobalVariablesFunctions;
import pls.shared.NpairsAnalysisSetupERFileFilter;
import pls.shared.NpairsAnalysisSetupFileFilter;
import pls.shared.NpairsfMRIResultFileFilter;
import npairs.Npairsj;
import npairs.io.NpairsDataLoader;
import npairs.io.NpairsjIO;
import npairs.NpairsjSetupParams;
import npairs.NpairsjException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class NpairsAnalysisShell extends ProgressDialogWatcher{
	
	String npairsSetupParamsMatFileName;
	String matlibType;
	String matlibTypeForInitFeatSel;
	
	private static int numNPAIRS = 1;
	
	boolean isBlocked;     // set to false for event-related fMRI
	boolean evdOnly;       // if true then just run initial EVD, save it and exit. Don't do analysis.
	int analysisNum = -1;  // if set to positive integer then run only that analysis number (given
	                       // multiple analyses that can be run from given setup file, e.g. when
	                       // pc range is set in setup file)
	boolean loadEVD;       // Overrides nsp.loadEVD.  Set to true if analysisNum is set ( > 0) 
	                       // because then we assume that EVD has already been run and saved,
	                       // even if nsp.loadEVD = false.
	
	
	private boolean clientmode = false;
	
	public NpairsAnalysisShell(String npairsSetupParamsMatFileName, String matlibType, 
			String matlibTypeForInitFeatSel, boolean isBlocked, boolean evdOnly, int analysisNum) {
		this.npairsSetupParamsMatFileName = npairsSetupParamsMatFileName;
		this.matlibType = matlibType;
		this.matlibTypeForInitFeatSel = matlibTypeForInitFeatSel;
		this.isBlocked = isBlocked;
		this.evdOnly = evdOnly;
		this.analysisNum = analysisNum;
	}
	
	
	public NpairsAnalysisShell(String npairsSetupParamsMatFileName, String matlibType, 
			String matlibTypeForInitFeatSel, boolean isBlocked) {
		this(npairsSetupParamsMatFileName, matlibType, matlibTypeForInitFeatSel, isBlocked, false, -1);
	}
	
	public void doTask() throws Exception {
		String dataSource = "image files";
		String analysisType = "blocked";	
		if (!isBlocked) {
			dataSource = "datamats";
			analysisType = "event-related";
		}

		

		
		NpairsjSetupParams nsp = new NpairsjSetupParams(npairsSetupParamsMatFileName, !isBlocked);
		File nspFileField = new File(nsp.resultsFilePrefix.trim());
		File resultsDir = nspFileField.getParentFile();
		if (resultsDir == null) {
			// set results dir to current working directory if none specified
			resultsDir = new File(System.getProperty("user.dir")); 
		}
		Information.resultPath = resultsDir.toString()+"/";
		if (!resultsDir.exists()) {
			String createDirMess = "Creating results directory " + resultsDir.toString();
			progress.postMessage(createDirMess + "\n");
			Npairsj.output.println(createDirMess);
			boolean created = resultsDir.mkdir();
			if (!created) {
				String errorMessage = "Unable to create results directory " + resultsDir + ".";
				progress.postMessage(errorMessage + "\n");
				Npairsj.output.println(errorMessage);
				
				progress.endTask();
				progress.complete();
				return;
			}
		}
		checkMultNPAIRS(nsp);
		
	}
	

	private void checkMultNPAIRS(NpairsjSetupParams nsp) throws NpairsjException, 
		IOException 
	{
		numNPAIRS = nsp.numNPAIRS;
	}
	
	public static int getNumNPAIRS()
	{
		return numNPAIRS;
	}
			
    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
	
  
//	public void finalize() throws Throwable {
//		System.out.println("NpairsjAnalysis killed.");
//		
//		super.finalize();
//	}
	
}