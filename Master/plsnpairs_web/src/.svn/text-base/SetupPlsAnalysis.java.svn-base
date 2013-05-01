

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JOptionPane;

import pls.shared.MLFuncs;
import pls.shared.PlsAnalysisSetupFileFilter;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLCell;
import com.jmatio.types.MLChar;
import com.jmatio.types.MLDouble;
import com.jmatio.types.MLStructure;

import extern.NewMatFileReader;

/**
 * Servlet implementation class SetupAnalysis
 */
public class SetupPlsAnalysis extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SetupPlsAnalysis() {
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
		
		String plsType = "";
		if (params.containsKey("PLSTYPE") ) {
			plsType = request.getParameter("PLSTYPE");
		}
		if (!plsType.equals("mean-centering_PLS") && !plsType.equals("behavior_PLS") &&
				!plsType.equals("non-rotated_task_PLS") && !plsType.equals("multiblock_PLS") ) {
			response.getWriter().println("The pls type must be one of:");
			response.getWriter().println("mean-centering_PLS, behavior_PLS, non-rotated_task_PLS, multiblock_PLS");
			return;
		}
		
		String fileName = "";
		if (params.containsKey("FILENAME") ) {
			fileName = request.getParameter("FILENAME");
		} else {
			fileName = "analysis_setup_file_" + System.currentTimeMillis();
			fileName += PlsAnalysisSetupFileFilter.EXTENSION;
			response.getWriter().println("Automatically generated analysis setup file name used: " + fileName);
		}
		
		Vector<String[]> sessionProfiles = new Vector<String[]>();
		if (params.containsKey("SESSIONFILES") ) {
			String[] sessionFiles = request.getParameter("SESSIONFILES").split("\n");
			for(String s : sessionFiles ) {
				sessionProfiles.add(s.split(" "));
			}
		}
		if (sessionProfiles.size() == 0) {
			response.getWriter().println("You must have at least one group with at least one session profile.");
		}
		
		String contrastFilename = "";
		if (params.containsKey("CONTRAST_FILENAME") ) {
			contrastFilename = request.getParameter("CONTRAST_FILENAME");
		}
		
		String behaviorFilename = "";
		if (params.containsKey("BEHAVIOR_FILENAME") ) {
			contrastFilename = request.getParameter("BEHAVIOR_FILENAME");
		}
		
		String posthocFilename = "";
		if (params.containsKey("POSTHOC_FILENAME") ) {
			posthocFilename = request.getParameter("POSTHOC_FILENAME");
		}
		
		String resultsFilename = "";
		if (params.containsKey("RESULTS_FILENAME") ) {
			resultsFilename = request.getParameter("RESULTS_FILENAME");
		}
		
		Vector<Integer> conditionSelection = new Vector<Integer>();
		if (params.containsKey("CONDITION_SELECTION") ) {
			String[] conditions = request.getParameter("CONDITION_SELECTION").split(" ");
			for (String s : conditions) {
				try {
					conditionSelection.add(Integer.parseInt(s) );
				} catch (NumberFormatException e) {
					System.out.println("Condition selection must be a list of integers.");
				}
			}
		}
		
		Vector<Integer> behaviorBlockConditionSelection = new Vector<Integer>();
		if (params.containsKey("BEHAVIOR_BLOCK_CONDITION_SELECTION") ) {
			String[] behaviorBlocks = request.getParameter("BEHAVIOR_BLOCK_CONDITION_SELECTION").split(" ");
			for (String s : behaviorBlocks) {
				try {
					behaviorBlockConditionSelection.add(Integer.parseInt(s) );
				} catch (NumberFormatException e) {
					System.out.println("Behavior block condition selection must be a list of integers.");
				}
			}
		}
		
		int numPermutations = 0;
		if (params.containsKey("NUM_PERMUTATIONS") ) {
			try {
				numPermutations = Integer.parseInt(request.getParameter("NUM_PERMUTATIONS") );
			} catch (NumberFormatException e) {
				System.out.println("Number of permutations must be an integer.");
			}
		}
		
		int numBootstraps = 0;
		if (params.containsKey("NUM_BOOTSTRAPS") ) {
			try {
				numBootstraps = Integer.parseInt(request.getParameter("NUM_BOOTSTRAPS") );
			} catch (NumberFormatException e) {
				System.out.println("Number of bootstraps must be an integer.");
			}
		}
		
		double confidenceLevel = 95;
		if (params.containsKey("CONFIDENCE_LEVEL") ) {
			try {
				confidenceLevel = Double.parseDouble(request.getParameter("CONFIDENCE_LEVEL") );
			} catch (NumberFormatException e) {
				System.out.println("Confidence level must be a number.");
			}
		}
		
		// Save the pls analysis setup file
		int maxNumSessions = 0;
		int numGroups = sessionProfiles.size();
		if (!(numGroups > 0)) {
			JOptionPane.showMessageDialog(null, "Error saving PLS setup file " + fileName + " - " + 
					"must include at least one session file.",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		for (int i = 0; i < numGroups; i++) {
			int currNumSessions = sessionProfiles.get(i).length;
			maxNumSessions = Math.max(maxNumSessions, currNumSessions);
		}
		
		MLStructure plsSetupInfo = new MLStructure("pls_setup_info", new int[] {1, 1});
		
		// Save the session file info for each group.
		MLStructure sessionFileInfo = new MLStructure("session_file_info", new int[] {1, numGroups});
		for (int i = 0; i < numGroups; i++) {
			int currNumSessions = sessionProfiles.get(i).length;
			MLCell currSessionFiles = new MLCell("session_files" + i, new int[] {1, currNumSessions});
			for (int sf = 0; sf < currNumSessions; sf++) {
				currSessionFiles.set(new MLChar("session_file" + sf, sessionProfiles.get(i)[sf]), 0, sf);
			}
			sessionFileInfo.setField("session_files", currSessionFiles, i);
			sessionFileInfo.setField("grp_size", new MLDouble("grp_size" + i, new double[][]{{currNumSessions}}), i);
		}
		plsSetupInfo.setField("session_file_info", sessionFileInfo);
		
		// Save condition selection info.
		if (conditionSelection == null) {
			String sessProfFileName = sessionProfiles.get(0)[0];
			try {
				MLStructure sessProfStruct = (MLStructure) new NewMatFileReader(sessProfFileName).getContent().get("session_info");
				String[] conditions = MLFuncs.MLCell1dRow2StrArray((MLCell)sessProfStruct.getField("condition"));
				conditionSelection = new Vector<Integer>(conditions.length);
				for (int i = 0; i < conditions.length; i++) {
					conditionSelection.add(new Integer(1));
				}
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(null, "Could not find PLS session file " + sessProfFileName +   
						".", "Error", JOptionPane.ERROR_MESSAGE);
	    		return;
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Could not load PLS session file " + sessProfFileName + 
						".", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		int numConditions = conditionSelection.size();
		MLDouble condSelect = new MLDouble("cond_selection", new int[]{numConditions, 1});
		for (int i = 0; i < numConditions; ++i) {
			condSelect.set(new Double(conditionSelection.get(i)), i);
		}
		plsSetupInfo.setField("cond_selection", condSelect);
		
		// Save condition selection info.
		if (behaviorBlockConditionSelection == null) {
			String sessProfFileName = sessionProfiles.get(0)[0];
			try {
				MLStructure sessProfStruct = (MLStructure) new NewMatFileReader(sessProfFileName).getContent().get("session_info");
				String[] conditions = MLFuncs.MLCell1dRow2StrArray((MLCell)sessProfStruct.getField("condition"));
				behaviorBlockConditionSelection = new Vector<Integer>(conditions.length);
				for (int i = 0; i < conditions.length; i++) {
					behaviorBlockConditionSelection.add(new Integer(1));
				}
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(null, "Could not find PLS session file " + sessProfFileName +   
						".", "Error", JOptionPane.ERROR_MESSAGE);
	    		return;
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Could not load PLS session file " + sessProfFileName + 
						".", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		numConditions = behaviorBlockConditionSelection.size();
		condSelect = new MLDouble("behav_block_cond_selection", new int[]{numConditions, 1});
		for (int i = 0; i < numConditions; ++i) {
			condSelect.set(new Double(behaviorBlockConditionSelection.get(i)), i);
		}
		plsSetupInfo.setField("behav_block_cond_selection", condSelect);
		
		// Save PLS analysis type info.
		
		if (plsType.equals("mean-centering_PLS")) {
			plsSetupInfo.setField("mean-centering_PLS", new MLDouble("mean-centering_PLS", new double[][]{{1}}));
		} else {
			plsSetupInfo.setField("mean-centering_PLS", new MLDouble("mean-centering_PLS", new double[][]{{0}}));
		}
		if (plsType.equals("behavior_PLS")) {
			plsSetupInfo.setField("behavior_PLS", new MLDouble("behavior_PLS", new double[][]{{1}}));
		} else {
			plsSetupInfo.setField("behavior_PLS", new MLDouble("behavior_PLS", new double[][]{{0}}));
		}
		if (plsType.equals("non-rotated_task_PLS")) {
			plsSetupInfo.setField("non-rotated_task_PLS", new MLDouble("non-rotated_task_PLS", new double[][]{{1}}));
		} else {
			plsSetupInfo.setField("non-rotated_task_PLS", new MLDouble("non-rotated_task_PLS", new double[][]{{0}}));
		}
		if (plsType.equals("multiblock_PLS")) {
			plsSetupInfo.setField("multiblock_PLS", new MLDouble("multiblock_PLS", new double[][]{{1}}));
		} else {
			plsSetupInfo.setField("multiblock_PLS", new MLDouble("multiblock_PLS", new double[][]{{0}}));
		}
		
		// Save data file fields.
		plsSetupInfo.setField("contrast_data_filename", new MLChar("contrast_data_filename", contrastFilename));
		plsSetupInfo.setField("behavior_data_filename", new MLChar("behavior_data_filename", behaviorFilename));
		plsSetupInfo.setField("posthoc_data_filename", new MLChar("posthoc_data_filename", posthocFilename));
		
		// Save permutations/bootstrap info.
		String numPerms = Integer.toString(numPermutations);
		plsSetupInfo.setField("num_permutations", new MLChar("num_permutations", numPerms));
		
		String numBoots = Integer.toString(numBootstraps);
		plsSetupInfo.setField("num_bootstraps", new MLChar("num_bootstraps", numBoots));
		
		String confidenceLevelString = Double.toString(confidenceLevel);
		plsSetupInfo.setField("confidence_level", new MLChar("confidence_level", confidenceLevelString));
		
		// Save the results file name.
		plsSetupInfo.setField("results_filename", new MLChar("results_filename", resultsFilename));
	
		ArrayList<MLArray> list = new ArrayList<MLArray>();
		list.add(plsSetupInfo);
		try {
			new MatFileWriter(fileName, list);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Could not save to PLS setup file " + fileName + ".", 
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

}
