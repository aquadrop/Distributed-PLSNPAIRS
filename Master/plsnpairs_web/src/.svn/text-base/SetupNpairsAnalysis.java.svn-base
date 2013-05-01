

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLCell;
import com.jmatio.types.MLChar;
import com.jmatio.types.MLDouble;
import com.jmatio.types.MLStructure;

/**
 * Servlet implementation class SetupNpairsAnalysis
 */
public class SetupNpairsAnalysis extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SetupNpairsAnalysis() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map params = request.getParameterMap();
		
		Vector<String[]> sessionProfiles = new Vector<String[]>();
		if (params.containsKey("SESSION_PROFILES") ) {
			String[] profileArray = request.getParameter("SESSION_PROFILES").split(" ");
			for (String s : profileArray) {
				String[] group = s.split(",");
				sessionProfiles.add(group);
			}
		}
		int numGroups = sessionProfiles.size();
		if (numGroups <= 0) {
			response.getWriter().println("You must include at least one sessionfile.");
			return;
		}
		
		Vector<Integer> classSelection = new Vector<Integer>();
		if (params.containsKey("CLASS_SELECTION") ) {
			String[] selections = request.getParameter("CLASS_SELECTION").split(" ");
			for (String s : selections) {
				try {
					Integer i = Integer.parseInt(s);
					classSelection.add(i);
				} catch (NumberFormatException nfex){
					response.getWriter().println("Classes in class selection must be integers.");
					return;
				}
			}
		}
		if (classSelection.size() <= 0) {
			response.getWriter().println("Class selection is empty.  You must select which classes to include in analysis.");
			return;
		}
		
		int maxNumSessions = 0;
		for (int i = 0; i < numGroups; ++i) {
			int currNumSessions = sessionProfiles.get(i).length;
			maxNumSessions = Math.max(maxNumSessions, currNumSessions);
		}

		MLStructure npairsSetupInfo = new MLStructure("npairs_setup_info", new int[] {1, 1});
		
		// If 'Event-related' analysis, datamats are loaded; if 'Blocked', data is read in
		// from image files
		int loadDatamats = 0;
		if (params.containsKey("BLOCK") && request.getParameter("BLOCK").equals("false") ) {
			loadDatamats = 1;
		}
		npairsSetupInfo.setField("load_datamats", new MLDouble("loadDatamats", 
				new double[][]{{loadDatamats}}));
		
		// Save sessionfile info for each group
		MLStructure sessionFileInfo = new MLStructure("session_file_info", new int[] {1, numGroups});
		// TODO: refine test for valid split object partition info; should consider how splits
		// include proportion of data from each grp when analyzing multiple groups 
		// (Currently just check total number of input session files to determine valid split
		// partition entries.)
		int nSessFiles = 0;
		for (int g = 0; g < numGroups; ++g) {
			int currNumSessions = sessionProfiles.get(g).length;
			nSessFiles += currNumSessions;
			MLCell currSessionFiles = new MLCell("session_files" + g, new int[] {1, currNumSessions});
			for(int sf = 0; sf < currNumSessions; ++sf) {
				currSessionFiles.set(new MLChar("session_file" + sf, sessionProfiles.get(g)[sf]), 0, sf);
			}
			
			sessionFileInfo.setField("session_files", currSessionFiles, g);
			sessionFileInfo.setField("grp_size", new MLDouble("grp_size" + g, 
						new double[][]{{currNumSessions}}), g);
		}
		npairsSetupInfo.setField("session_file_info", sessionFileInfo);
		
		int numClasses = classSelection.size();
		MLDouble classSelect = new MLDouble("class_selection", new int[]{numClasses, 1});
		for (int i = 0; i < numClasses; ++i) {
			classSelect.set(new Double(classSelection.get(i)), i);
		}
		npairsSetupInfo.setField("class_selection", classSelect);
		
		// Save NPAIRS analysis type info
		int doMSR = 0;
		if (params.containsKey("DO_MSR") && request.getParameter("DO_MSR").equals("true") ) {
			doMSR = 1;
		}
		npairsSetupInfo.setField("do_msr", new MLDouble("do_msr", new double[][] {{doMSR}}));
		
		int doGLM = 0;
		if (params.containsKey("DO_GLM") && request.getParameter("DO_GLM").equals("true") ) {
			doGLM = 1;
		}
		npairsSetupInfo.setField("do_glm", new MLDouble("do_glm", new double[][]{{doGLM}}));
		
		int doPCA = 0;
		if (params.containsKey("DO_PCA") && request.getParameter("DO_PCA").equals("true") ) {
			doPCA = 1;
		}	
		npairsSetupInfo.setField("do_pca", new MLDouble("do_pca", new double[][]{{doPCA}}));
		
		int doCVA = 0;
		if (params.containsKey("DO_CVA") ) {
			if (request.getParameter("DO_CVA").equals("true") ) {
				doCVA = 1;
				if (params.containsKey("CVA_CLASSFILE") ) {
					String cvaClassFile = request.getParameter("CVA_CLASSFILE");
					npairsSetupInfo.setField("cva_class_file", new MLChar("cva_class_file", cvaClassFile));
				}
			}
		}
		npairsSetupInfo.setField("do_cva", new MLDouble("do_cva", new double[][]{{doCVA}}));
		
		if (doCVA == 1 && doPCA == 1) {
			int normPCs = 0;
			if (params.containsKey("NORMALIZE_PCS") ) {
				if (request.getParameter("NORMALIZE_PCS").equals("true") ) {
					normPCs = 1;
				}
			}
			npairsSetupInfo.setField("norm_pcs", new MLDouble("norm_pcs", 
					new double[][]{{normPCs}}));
		}
		else {
			// for now, must do PCA + CVA
			response.getWriter().println("NPAIRS Analysis Modelling Options: PCA + CVA is the only NPAIRS analysis option currently implemented.");
		}

		boolean doResampling = false;
		if (params.containsKey("DO_RESAMP") ) {
			if (request.getParameter("DO_RESAMP").equals("true") ) {
				doResampling = true;
			}
		}
		
		// Save resampling info
		int splitHalfXvalid = 0;
		int bootstrap = 0;
		if (doResampling && params.containsKey("RESAMP_TYPE") ) {
			if (request.getParameter("RESAMP_TYPE").equals("split_half_xvalid") ) {
				splitHalfXvalid = 1;
			} else if (request.getParameter("RESAMP_TYPE").equals("bootstrap") ) {
				bootstrap = 1;
			}
			
			npairsSetupInfo.setField("split_half_xvalid", new MLDouble(
					"split_half_xvalid", new double[][]{{splitHalfXvalid}}));
			
			npairsSetupInfo.setField("bootstrap", new MLDouble("bootstrap",
					new double[][]{{bootstrap}}));
		}

		int numSplits = 0;
		if (doResampling && params.containsKey("NUM_SPLITS") ) {
			numSplits = Integer.parseInt(request.getParameter("NUM_SPLITS") );
			npairsSetupInfo.setField("num_splits", new MLDouble("num_splits", 
					new double[][]{{numSplits}}));
		}

		if (doResampling) {
			if (params.containsKey("SPLITS_INFO_FILENAME") ) {
				String splitsInfoFilename = request.getParameter("SPLITS_INFO_FILENAME");
				npairsSetupInfo.setField("splits_info_filename", new MLChar("splits_info_filename",
				splitsInfoFilename));
			} else if (params.containsKey("SPLIT_PARTITION") ) {
				String splitPartStr[] = request.getParameter("SPLIT_PARTITION").split(" ");
				
				String splitPartErrorMessage = "Invalid split partition values.\n" +
						"Values must be integers indicating number of split objects " +
						"in each split half.";
				try {
					int[] splitPartition = new int[2];
					splitPartition[0] = Integer.parseInt(splitPartStr[0]);
					splitPartition[1] = Integer.parseInt(splitPartStr[1]);
					if (splitPartition[0] <= 0 || splitPartition[1] <= 0) {
						response.getWriter().println(splitPartErrorMessage);
					}
					if (splitPartition[0] + splitPartition[1] > nSessFiles) {
						response.getWriter().println(splitPartErrorMessage);
					}
					npairsSetupInfo.setField("split_partition", new MLDouble("split_partition",
							new double[][]{{splitPartition[0], splitPartition[1]}}));
				}
				catch (NumberFormatException nfe) {
					response.getWriter().println(splitPartErrorMessage);
				}
			}
		}
		
		// Save initial feature selection info
		boolean doFeatureSelection = false;
		if (params.containsKey("DO_INIT_SVD") ) {
			if (request.getParameter("DO_INIT_SVD").equals("true") ) {
				doFeatureSelection = true;
			}
		}
		
		if (doFeatureSelection) {
			npairsSetupInfo.setField("do_init_svd", new MLDouble("do_init_svd", 
					new double[][]{{1}}));
		} else {
			npairsSetupInfo.setField("do_init_svd", new MLDouble("do_init_svd", 
					new double[][]{{0}}));
		}
		
		if (doFeatureSelection && params.containsKey("LOAD_SVD") ) {
			if (request.getParameter("LOAD_SVD").equals("true") ) {
				npairsSetupInfo.setField("load_svd", new MLDouble("load_svd", 
						new double[][]{{1}}));
				
				String svdFilePrefix = "";
				if (params.containsKey("SVD_FILE_PREFIX") ) {
					svdFilePrefix = request.getParameter("SVD_FILE_PREFIX");
				}
				
				if (svdFilePrefix.length() == 0) {
					response.getWriter().println("Please enter SVD file prefix to load SVD information.");
					return;
				}
				npairsSetupInfo.setField("svd_file_prefix", new MLChar("svd_file_prefix", 
						svdFilePrefix));
			} else {
				npairsSetupInfo.setField("load_svd", new MLDouble("load_svd", 
						new double[][]{{0}}));
			}
		}

		if (doFeatureSelection && params.containsKey("DRF") ) {
			double dataReductFactor = Double.parseDouble(request.getParameter("DRF") );
			npairsSetupInfo.setField("drf", new MLDouble("drf", new double[][]{{dataReductFactor}}));
		}
		

		if (doPCA == 1 && doCVA == 1) {
			// save pc info for passing into cva
			if (params.containsKey("SET_PC_RANGE") ) {
				String[] setPcRange = request.getParameter("SET_PC_RANGE").split(" ");
				if (setPcRange.length >= 3) {
					String pcRange = setPcRange[0];
					double pcStep = Double.parseDouble(setPcRange[1]);
					double pcMultFact = Double.parseDouble(setPcRange[2]);
					
					npairsSetupInfo.setField("pc_range", new MLChar("pc_range", pcRange));
					npairsSetupInfo.setField("pc_step", new MLDouble("pc_step", 
							new double[][]{{pcStep}}));
					npairsSetupInfo.setField("pc_mult_factor", new MLDouble("pc_step", 
							new double[][]{{pcMultFact}}));
				}
			} else {
				String pcsForSplit = "";
				if (params.containsKey("PCS_TRAINING") ) {
					pcsForSplit = request.getParameter("PCS_TRAINING");
				}
				
				String pcsForFullData = "";
				if (params.containsKey("PCS_ALL_DATA") ) {
					pcsForFullData = request.getParameter("PCS_ALL_DATA");
				}
				
				npairsSetupInfo.setField("pcs_training", new MLChar("pcs_training",
						pcsForSplit));
				npairsSetupInfo.setField("pcs_all_data", new MLChar("pcs_all_data",
						pcsForFullData));
			}
		}
		
		double saveSplits = 0;
		if (params.containsKey("SAVE_SPLIT_RESULTS") ) {
			if (request.getParameter("SAVE_SPLIT_RESULTS").equals(true) ) {
				saveSplits = 1;
			}
		}
		npairsSetupInfo.setField("save_split_results", new MLDouble("save_split_results",
				new double[][] {{saveSplits}}));

		String resultsFilename = "npairs";
		if (params.containsKey("RESULTS_FILENAME") ) {
			resultsFilename = request.getParameter("RESULTS_FILENAME");
		}
		npairsSetupInfo.setField("results_filename", new MLChar("results_filename", 
				resultsFilename));
		
		String analysisFilename = "analysis";
		if (params.containsKey("ANALYSIS_FILENAME") ) {
			analysisFilename = request.getParameter("ANALYSIS_FILENAME");
		}

		ArrayList<MLArray> list = new ArrayList<MLArray>();
		list.add(npairsSetupInfo);
		try {
			new MatFileWriter(analysisFilename, list);

		}
		catch(Exception ex) {
			response.getWriter().println("Could not save to npairs setup file " + analysisFilename + ".");
			return;
		}
	}

}
