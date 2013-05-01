package extern.nifti.alignstacks.align3tp;import ij.gui.ImageWindow;/** * This abstract class provides a similarity function for ImagePlusPlus * objects.  Known subclasses are SliceFunction and StackFunction.  It was * tacked on to provide common parent for SliceFunction and StackFunction. * It is highly likely that some items could be move up to this parent. * Subclasses need to provide: *   construtor * @author J. Anthony Parker, MD PhD <J.A.Parker@IEEE.org> * @version 28November2004 */abstract public class ImageFunction extends Function {	protected final static double MIN_SCALE = .001;	protected final static boolean SCALE_WRT_CENTER = false;	protected int transform;	protected double[] xt;	protected double minOverlap = 0.25;	protected double distanceSq = 0.0;	protected double halfPenaltySq = Math.sqrt(Double.MAX_VALUE);	protected double changePenalty = 1.0;	protected Affine initReg;	protected ImagePlusPlus[] imppIn;	// boundry[low corner, high corner][x,y,z]	protected int[][] boundry = null;	protected ImageWindow win;	/** Length of xt, which number of arguments to optimize */	abstract protected int length() throws IllegalArgumentException;	/** Evaluation of the function for independent variables, xt */	abstract protected double eval(double[] xt);	/** Evaluate the function for the two images after the transform has	 * been set.  Defined in subclasses.*/	abstract protected double eval();	/** Independent variables, xt. */	public double[] getXt() {return xt;}	/** Sets the registration affine transform in imppIn[1], using	 * the independant variables, xt, after adjusting for	 * the view in imppIn[1].  xt are defined in the current view. */	abstract public void setXt(double[] xt) throws IllegalArgumentException;	/** @param minOverlap minimum overlap allowed */	public void setMinOverlap(double minOverlap) {		this.minOverlap = minOverlap;		return;	}	/** Set square of the half value point for change from unit	 * vector penalty.  If x==0.0, use a large number. */	public void setHalfPenalty(double x) {		if(x==0.0)			halfPenaltySq = Math.sqrt(Double.MAX_VALUE);		else			halfPenaltySq = x*x;		return;	}	/** Save initial registration. */	public void saveReg() {		initReg = new Affine(imppIn[1].getReg());		return;	}	/** Restores saved orientation. */	public void restoreReg() {		imppIn[1].setReg(initReg);		return;	}}	// end ImageFunction