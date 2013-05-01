import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import matlib.Matrix;
import matlib.MatrixException;
import matlib.EigenvalueDecomposition;

import io.NiftiIO;

/** This class uses matlib.Matrix library to perform EVD test on given input
 *  nifti data (4d or 3d input OK). Default Matrix library type being tested is 
 *  'ParallelColt'. See inline comments for constant variables 'MATLIB_TYPE' and 
 *  'VERBOSE'. See further details in main method documentation and EVDtest(Matrix data) 
 *  constructor documentation. */
public class EVDtest {
		
	final static String MATLIB_TYPE = "ParallelColt"; // see matlib.Matrix source documentation for other options
	final static boolean VERBOSE = true; // set to false to suppress output to stdout
	
	/** 
	 * input arg 1 == name (incl path) of textfile containing data file prefixes 
	 * input arg 2 == nifti mask filename (incl path and .nii suffix)
	 * input arg 3 == path to nifti test data 
	 * input arg 4 (optional) == name of ASCII file in which to save masked data matrix
	 *  		(if no name provided, data matrix will not be saved)
	 */
	public static void main (String[] args) {
		
		String dataSavename = "";
		try {
			String textFilename = args[0];
			String maskFilename = args[1];
			String dataPath = args[2];	
			if (args.length > 3) {
				dataSavename = args[3];
			}
			
			if (VERBOSE) System.out.print("Loading data... ");
			double sTime = System.currentTimeMillis();
			Matrix data = loadData(textFilename, dataPath, maskFilename);	
			double tTime = (System.currentTimeMillis() - sTime) / 1000;
			if (VERBOSE) System.out.println("DONE [" + tTime + " s]");
			
			if (!dataSavename.isEmpty()) {
				// save the data as matrix into text file
				if (VERBOSE) System.out.print("Saving data Matrix to file \"" + dataSavename
						+ "\"...");
				sTime = System.currentTimeMillis();
				data.printToFile(dataSavename, "default");
				tTime = (System.currentTimeMillis() - sTime) / 1000;
				if (VERBOSE) System.out.println("DONE [" + tTime + " s]");				
			}
			
			EVDtest evdTest = new EVDtest(data);
			if (VERBOSE) System.out.println("That's all, folks...");			
		}		
		catch (ArrayIndexOutOfBoundsException e) {
			System.out.print("Error - must include text filename, mask filename and data path as input args.");
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
		catch (MatrixException e) {
			System.out.println(e.getMessage());
		}		
	}	
	
	
	/** Constructor.  First calculates 'SSP' (sum of squares and products matrix) given input 
	 *  data matrix; see matlib.Matrix.sspByRow() for details.  Next EVD (eigenvalue decomposition)
	 *  is performed on SSP matrix.  Assumption is that num rows <= num cols; otherwise 
	 *  use sspByCol() (to get smaller of 2 possible SSP matrices).  
	 *  
	 * @param data 2D Matrix on which to perform EVD.
	 */
	public EVDtest(Matrix data) {
			
		if (VERBOSE) System.out.print("Creating SSP matrix from data matrix...");
		double sspSTime = System.currentTimeMillis();
		Matrix sspData = data.sspByRow();
		double sspTTime = (System.currentTimeMillis() - sspSTime) / 1000;
		if (VERBOSE) System.out.println("[" + sspTTime + "s]");
		
		if (VERBOSE) System.out.print("Running EVD on SSP matrix...");
		double evdSTime = System.currentTimeMillis();
		EigenvalueDecomposition evd = sspData.eigenvalueDecomposition();
		double evdTTime = (System.currentTimeMillis() - evdSTime) / 1000;
		if (VERBOSE) System.out.println("[" + evdTTime + "s]");
		
//		// IF YOU WANT TO RETRIEVE EVALS/EVECTS, YOU CAN DO THIS:
//		Matrix evdEvals = evd.getRealEvalMat();
//		Matrix evdEvects = evd.getEvects();
	
	}
	
	
	/** Adds dataPath and nifti .nii suffix to each data file prefix contained
	 *  in input text file (file isn't modified).
	 * @param textFilename
	 * @param dataPath
	 * @return String array containing full data filenames incl. path and suffix.
	 * @throws IOException
	 */
	private static String[] loadDataFilenames(String textFilename, String dataPath) 
		throws IOException {

		ArrayList<String> filenames = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(textFilename));

		String line;
		try {
			while ((line = br.readLine()) != null) {
				if (!line.isEmpty()) {
					// add nifti file suffix to each prefix given in text file
					filenames.add(line.trim() + ".nii");	
				}
			}	 
		}
		catch (Exception e) {
			throw new IOException("Error loading data from file " +
					textFilename + ". " + e.getMessage());
		}
		finally {
			br.close();
		}
		
		// add given path to filenames 
		String[] dataFilenames = new String[filenames.size()];
		if (!dataPath.endsWith(System.getProperty("file.separator"))) {
			dataPath = dataPath.concat(System.getProperty("file.separator"));
		}
		for (int i = 0; i < dataFilenames.length; ++i) {
			dataFilenames[i] = dataPath.concat(filenames.get(i));
		}
		
		return dataFilenames;	
	}
	
	
	/** Returns Matrix (of type MATLIB_TYPE) containing masked data
	 * 
	 * @param textFilename name of file containing nifti file prefixes 
	 * 	(not incl. path or .nii suffix)
	 * @param dataPath
	 * @param maskFilename
	 * @return masked data Matrix: rows == tmpts, cols == masked voxels
	 * @throws IOException
	 * @throws MatrixException
	 */
	private static Matrix loadData(String textFilename, String dataPath, String maskFilename) 
			throws IOException, MatrixException {
		String[] dataFilenames = loadDataFilenames(textFilename, dataPath);
		//print(dataFilenames);	
		
		return NiftiIO.getMaskedDatamat(dataFilenames, maskFilename, MATLIB_TYPE);			
	}
	
	
	/** Prints 1D String array to stdout
	 * 
	 * @param array
	 */
	private static void print(String[] data) {
		for (int i = 0; i < data.length; ++i) {
			System.out.println(data[i]);
		}
	}

}
