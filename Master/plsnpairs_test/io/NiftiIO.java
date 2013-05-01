package io;

import java.io.IOException;
import java.util.ArrayList;

import extern.niftijlib.Nifti1Dataset;
import matlib.Matrix;
import matlib.MatrixException;
import matlib.MatrixImpl;

public class NiftiIO {
	
	/** Returns 2D Matrix containing masked input data, one timepoint/row.
	 * @param dataFilenames array of data filenames (incl. path and extension)
	 * @param maskFilename incl. path and extension
	 * @param matlibType type of matlib library to use when creating Matrix 
	 * 	instance to be returned
	 * @return 2D Matrix (nTmptsAll rows X nMaskVox cols) containing masked data
	 * @throws IOException
	 * @throws MatrixException
	 * @see MatrixImpl
	 */
	public static Matrix getMaskedDatamat(String[] dataFilenames, String maskFilename, 
			String matlibType) throws IOException, MatrixException {
		
		int[] nTmptsPerFile = getNTmptsPerFile(dataFilenames);
		int nTmptsAll = sum(nTmptsPerFile);
		int[] maskCoords = getMaskCoords(maskFilename);	
		int nMskVox = maskCoords.length;
				
		// dataMat will contain all masked data
		Matrix dataMat = new MatrixImpl(nTmptsAll, nMskVox, matlibType).getMatrix();
		int currRow = 0;
		for (int i = 0; i < dataFilenames.length; ++i) {
			double[][] currMaskedData = loadNiftiData(dataFilenames[i], maskCoords);
			dataMat.setSubMatrix(currMaskedData, currRow, 0);
			currRow += nTmptsPerFile[i];				
		}
		
		return dataMat;
	}
	
	
	/** Returns array of (1D 0-relative) indices of voxels contained in mask.  (XYZ order of 1D
	 *  representation of 3D data is whatever order is used in 
	 *  Nifti1Dataset.readDoubleVol1D(short t).)
	 * 
	 * @param maskFilename name of nifti file containing zeros in non-mask voxels and
	 * 	                   ones in mask voxels
	 * @return 1D array containing (1D) indices (0-relative)
	 * @throws IllegalArgumentException if mask file is not 3D (i.e. if no. tmpts in mask
	 *  file != 1)
	 */
	private static int[] getMaskCoords(String maskFilename) throws IllegalArgumentException,
		IOException {
		
		double[][] mask2D = loadNiftiData(maskFilename, null);
		if (mask2D.length != 1) {
			throw new IllegalArgumentException("Mask must be 3D nifti volume.");
	    } 
		return findNonZero(mask2D[0]);	
	}
	
	
	private static int[] getNTmptsPerFile(String[] niftiFilenames) throws IOException {
		int[] nTmptsPerFile = new int[niftiFilenames.length];
		for (int i = 0; i < niftiFilenames.length; ++i) {
			Nifti1Dataset niftiDS = new Nifti1Dataset(niftiFilenames[i]);			
			niftiDS.readHeader();
			int nCurrTmpts = niftiDS.getTdim();
			if (nCurrTmpts == 0) nCurrTmpts = 1;
			nTmptsPerFile[i] = nCurrTmpts;
		}
		
		return nTmptsPerFile;
	}
	
	/** Loads Nifti format data file (can be 3d or 4d file)
	 *  
	 * @param niftiFilename incl full path
	 * @param maskCoords int array containing 1D 0-relative mask coords (i.e.
	 * 	1D indices of mask voxels in 3D mask volume)
	 * @return 2D array of doubles containing data
	 *         rows are timepoints (3d volumes); columns are masked vox values
	 * @throws IOException
	 */
	private static double[][] loadNiftiData(String niftiFilename, int[] maskCoords) 
		throws IOException {
			
		Nifti1Dataset niftiDS = new Nifti1Dataset(niftiFilename);
		
		niftiDS.readHeader();

		int xDim = niftiDS.getXdim();
		int yDim = niftiDS.getYdim();
		int zDim = niftiDS.getZdim();
		int tDim = niftiDS.getTdim();

		
//		System.out.println("Xdim: " + xDim);
//		System.out.println("Ydim: " + yDim);
//		System.out.println("Zdim: " + zDim);
//		System.out.println("Tdim: " + tDim);


		if (tDim == 0) tDim = 1;

		// load data 
		int volSz;
		if (maskCoords == null) {
			volSz = xDim * yDim * zDim;
		}
		else volSz = maskCoords.length;
		
		double[][] maskedData2D = new double[tDim][volSz];
		for (short t = 0; t < tDim; ++t) { // short req'd by readDoubleVol1D
			double[] currData1D = niftiDS.readDoubleVol1D(t);
			if (maskCoords == null) { 
				maskedData2D[t] = currData1D;
			}
			else { 
				for (int i = 0; i < volSz; ++i) {	
					maskedData2D[t][i] = currData1D[maskCoords[i]];
				}
			}
		}
		
		return maskedData2D;
	}
	
	/** Returns (0-relative) indices of non-zero elements in input array
	 * @param data 	the input double array
	 * @return int array of indices where data is non-zero
	 */
	private static int[] findNonZero(double[] data) {
		ArrayList<Integer> whereNZ = new ArrayList<Integer>();
		int dLength = data.length;
		for (int d = 0; d < dLength; ++d) {
			if (data[d] != 0) {
				whereNZ.add(d);
			}
		}
		int[] locs = new int[whereNZ.size()];
		for (int i = 0; i < locs.length; ++i) {
			locs[i] = whereNZ.get(i);
		}
		return locs;
	}
	
	/**
	 * Return sum of all elements in data
	 * @param data
	 * @return sum 
	 */
	public static int sum(int[] data) {
		int sum = 0;
		for(int i : data) {
			sum += i;
		}
		return sum;
	}
	
}
