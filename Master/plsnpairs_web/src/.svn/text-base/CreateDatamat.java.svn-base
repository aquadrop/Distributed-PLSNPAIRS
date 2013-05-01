

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pls.sessionprofile.RunGenerateDatamat;
import pls.sessionprofile.SessionProfile;
import pls.shared.StreamedProgressHelper;

/**
 * Servlet implementation class CreateDatamat
 */
public class CreateDatamat extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateDatamat() {
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
		
		if (params == null) {
			return;
		}
		
		StreamedProgressHelper helper = new StreamedProgressHelper();
//		helper.addStream(System.out);
		
		String sessionFile = "";
		if (params.containsKey("SESSION_FILE") ) {
			sessionFile = request.getParameter("SESSION_FILE");
		} else {
			response.getWriter().println("You must specify a session file to use.");
			return;
		}
		
		double coordinateThreshold = 0.15;
		if (params.containsKey("COORDINATE_THRESHOLD") ) {
			coordinateThreshold = Double.parseDouble(request.getParameter("COORDINATE_THRESHOLD") );
		}
		
		int[] ignoreSlices = null;
		if (params.containsKey("IGNORE_SLICES") ) {
//			ArrayList<String> ints = params.get("IGNORE_SLICES");
			String[] ints = request.getParameter("IGNORE_SLICES").split(" ");
			ignoreSlices = new int[ints.length];
			for (int i = 0; i < ints.length; ++i) {
				try {
					ignoreSlices[i] = Integer.parseInt(ints[i]);
				} catch (NumberFormatException nfex) {
					response.getWriter().println("Slices to ignore must be integers.");
					return;
				}
			}
		}
		
		boolean normalizeMeanVolume = false;
		if (params.containsKey("NORMALIZE_MEAN_VOLUME") ) {
			normalizeMeanVolume = Boolean.parseBoolean(request.getParameter("NORMALIZE_MEAN_VOLUME") );
		}
		
		int windowSize = 0;
		if (params.containsKey("WINDOW_SIZE") ) {
			windowSize = Integer.parseInt(request.getParameter("WINDOW_SIZE") );
		} else {
			response.getWriter().println("You need to specify the window size.");
			return;
		}
		if (windowSize < 1) {
			response.getWriter().println("Window size must be at least one (1).");
			return;
		}
		
		SessionProfile profile;
		try {
			profile = SessionProfile.loadSessionProfile(sessionFile, windowSize==1, false);
		} catch (IOException e) {
			e.printStackTrace(response.getWriter() );
			return;
		}
		
		boolean normalizeSignalMean = false;
		if (params.containsKey("NORMALIZE_SIGNAL_MEAN") ) {
			normalizeSignalMean = Boolean.parseBoolean(request.getParameter("NORMALIZE_SIGNAL_MEAN") );
		}
		
		boolean considerAllVoxels = coordinateThreshold == 0.0;
		 
		boolean singleSubject = false;
		if (params.containsKey("SINGLE_SUBJECT") ) {
			singleSubject = Boolean.parseBoolean(request.getParameter("SINGLE_SUBJECT") );
		}
		
		RunGenerateDatamat worker = new RunGenerateDatamat(
				profile.isBlock, 
				profile.ignoreRuns,
				sessionFile,
				profile.useBrainMask,
				profile.brainMaskFile,
				coordinateThreshold, 
				ignoreSlices,
				normalizeMeanVolume,
				profile.numSkippedScans,
				windowSize, 
				profile.mergeAcrossRuns,
				normalizeSignalMean,
				considerAllVoxels, 
				singleSubject,
				profile.conditionInfo,
				profile.runInfo,
				profile.datamatPrefix);
		
		worker.progress = helper;
		worker.start();
		
		response.getWriter().println("The datamat creation process has been started.");
	}

}
