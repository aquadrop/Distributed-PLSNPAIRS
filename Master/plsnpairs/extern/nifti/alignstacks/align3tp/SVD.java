package extern.nifti.alignstacks.align3tp;import java.lang.IllegalArgumentException;import java.lang.ArithmeticException;/** * This class computes the Singlar Value Decomposition (SVD) for a * matrix; the static method fit() applies SVD to fitting an equation * to a set of data points. * See Press WH, Teukolsky SA, Vetterling WT, Flannery BP: Numverical * recipes in C++. Cambridge University Press, 2002, Chap. 2.6 & 15.4 * In Press, the "design matrix" for fitting is called A and the * fit parameters are called a.  I find this confusing.  I have * renamed the "design matrix" to be H. * Instantiating SVD with an m by n input matrix, H, does a SVD yielding * matrices U, W, V where U is m by n, W is sq with non-zero elements only * along the trace, and U is n by n.  U and V have orthonormal columns. * H = U*W*VT, where VT is the transpose of V.  m >= n. * General linear least squares fitting is done by fit().  Inputs to fit(), * x, y, sig, funcs are used to build the design matrix, H.  Null value for * sig -> all sig values are set to 1; null value for funcs -> funcs will * be a parabola.  H is then used during instantiation of an SVD object. * This static method, invoked as SVD.fit(), returns a SVD object * which fits y(x) = sum { a[k]*X[k](x) }, where for a polynomial * fit X[k](x) = x**k.  Although X[k](x) could be any set of functions * of x, e.g. sin(x) and cos(x). * fit() finds an m-vector, a, that minimizes chisq = |H*a-b|**2, where * H[i][j] = X[j](x[i])/sig[i], b = y[i]/sig[i]. * * @param h an m by n input matrix with m>=n  * @return u m by n orthonormal matrix U * @return w n diagonal elements of sq matrix W (only non-zero elements) * @return v n by n orthonormal matrix V (V not its transpose) * @return m rows * @return n columns * @author J. Anthony Parker, MD PhD <J.A.Parker@IEEE.org> * @version 7August2004 */public class SVD {	// Tol is the default value for double precision and variables	// scaled to order unity.	private static double TOL = 1.0e-13;	private double[][] u = null, v = null;	private double[] w = null;	private int m, n;	// rows and columns	// a and chisq are outputs from the static procedure fit()	// they are stored in the SVD which fit() instantiates	// an SVD not instantiated by fit will not use these variables	private double[] a = null;	private double chisq = 0.0;	/** Construtor performs SVD on input matrix h */	public SVD(double[][] h)				throws IllegalArgumentException, ArithmeticException {		m = h.length;	// rows		if(m<=0)			throw new IllegalArgumentException("Input has zero rows.");		n = h[0].length;	// columns		if(n<=0)			throw new IllegalArgumentException("Input has zero columns.");		for(int i=1; i<m; i++)			if(h[i].length<n)				throw new IllegalArgumentException("Short column(s)");		u = new double[m][n]; w = new double[n]; v = new double[n][n];		for(int i=0; i<m; i++)			for(int j=0; j<n; j++)				u[i][j] = h[i][j];		doSVD();		return;	}	/** Matrix U, m by n */	public double[][] getU() {return u;}	/** Trace of W */	public double[] getW() {return w;}	/** Set trace of W.  Set can be used to zero small values. */	public void setW(double[] w) {this.w = w; return;}	/** Matrix V, n by n */	public double[][] getV() {return v;}	/** Rows; equal to ndata for fitting */	public int getM() {return m;}	/** Columns; equal to number of basis functions for fitting */	public int getN() {return n;}	/** Fit parameters, y(x) = sum{ a[k]*X[k](x) } */	public double[] getA() {return a;}	/** Used by fit to set parameters, a */	void setA(double[] a) {this.a = a; return;}	/** Chisq from fit */	public double getChisq() {return chisq;}	/** Used by fit to set chisq */	void setChisq(double chisq) {this.chisq = chisq; return;}	/** Invokes fit with sig all 1.0 and funcs == parabola. */	public static SVD fit(double[] x, double[] y) {		return fit(x, y, null, null);	}	/** Invokes fit with sig all 1.0. */	public static SVD fit(double[] x, double[] y, SVDfunc funcs) {		return fit(x, y, null, funcs);	}	/** Does general linear fit returning an SVD object.  see Press svdfit	 * Since this static method instantiates an SVD it is just a bit	 * awkward accessing the SVD instance variable i.e. using gets and sets.	 *	 * @param funcs a function which computes basis X[k](x)	 * @param x vector of data locations	 * @param y vector of data valuse	 * @param sig vecor of SDs for data points used for	 * @return svd SVD object with a, chisq, and matrices	 * @return a the fiting parameters	 * @return chisq the chisq for the fit	 */	public static SVD fit(double[] x, double[] y, double[] sig, SVDfunc funcs)				throws IllegalArgumentException, ArithmeticException {		double wmax, tmp, thresh, sum;		// ndata corresponds to m above		int ndata = x.length;		if(ndata<=0)			throw new IllegalArgumentException("x has no data");		if(funcs == null)			funcs = new SVD.Parabola();		// ma corresponds to n above		int ma = funcs.length();		if(sig == null) {			sig = new double[ndata];			for(int i=0; i<ndata; i++)				sig[i] = 1.0;		}		if(y.length!=ndata || sig.length!=ndata)			throw new IllegalArgumentException(										"Inconsistent input dimensions.");		double[][] h = new double[ndata][ma];		double[] b = new double[ndata], afunc = new double[ma];		// Accumulate coefficients of the fitting matrix		for (int i=0; i<ndata; i++) {			afunc = funcs.getX(x[i]);			tmp = 1.0/sig[i];			for (int j=0; j<ma; j++) h[i][j] = afunc[j]*tmp;			b[i] = y[i]*tmp;		}		SVD svd = new SVD(h);	// Singular value decomposition		double[] w = svd.getW();		// Edit the singular values, given TOL setting very small to zero		wmax = 0.0;		for (int j=0; j<ma; j++)			if (w[j] > wmax) wmax = w[j];		thresh = TOL*wmax;		for (int j=0; j<ma; j++)			if (w[j] < thresh) w[j] = 0.0;		double[] a = svd.backsubstitution(b);		svd.setA(a);		double chisq = 0.0;		// Evaluate chisq		for (int i=0; i<ndata; i++) {			afunc = funcs.getX(x[i]);			sum = 0.0;			for (int j=0; j<ma; j++) sum += a[j]*afunc[j];			tmp = (y[i]-sum)/sig[i];			chisq += tmp*tmp;		}		svd.setChisq(chisq);		return svd;	}	/** see svbksb in Press.  Returns solution x for H*x = b */	public double[] backsubstitution(double[] b)									throws IllegalArgumentException {		int jj, j, i;		double s;		double[] x = new double[n];			if(b.length!=m)			throw new IllegalArgumentException("b wrong dimension");		double[] tmp = new double[n];		for (j=0; j<n; j++) {	// Calculate UT*B			s = 0.0;			if (w[j] != 0.0) {	// Nonzero result only if wj is nonzero				for (i=0; i<m; i++) s += u[i][j]*b[i];				s /= w[j];			}			tmp[j] = s;		}		for (j=0; j<n; j++) {	// Matrix multiply by V to get answer			s = 0.0;			for (jj=0; jj<n; jj++) s += v[j][jj]*tmp[jj];			x[j] = s;		}		return x;	}	// see svdcmp in Press	// input named, a, in Press is called h by me.  The same matrix	// becomes the output, u.  So, a in svdcmp -> u in doSVD.	private void doSVD() {	// does single value decomposition		boolean flag;		int i, its, j, jj, k, l=0, nm=0;		double anorm, c, f, g, h, s, scale, x, y, z;			double[] rv1 = new double[n];		g = scale = anorm = 0.0;		// Householder reduction to bidiagonal form		for (i=0; i<n; i++) {			l = i+2;			rv1[i] = scale*g;			g = s = scale = 0.0;			if (i < m) {				for (k=i; k<m; k++) scale += Math.abs(u[k][i]);				if (scale != 0.0) {					for (k=i; k<m; k++) {						u[k][i] /= scale;						s += u[k][i]*u[k][i];					}					f = u[i][i];					g = -sign(Math.sqrt(s), f);					h = f*g-s;					u[i][i] = f-g;					for (j=l-1; j<n; j++) {						for (s=0.0,k=i; k<m; k++) s += u[k][i]*u[k][j];						f = s/h;						for (k=i; k<m; k++) u[k][j] += f*u[k][i];					}					for (k=i; k<m; k++) u[k][i] *= scale;				}			}			w[i] = scale *g;			g = s = scale = 0.0;			if (i+1 <= m && i != n) {				for (k=l-1; k<n; k++) scale += Math.abs(u[i][k]);				if (scale != 0.0) {					for (k=l-1; k<n; k++) {						u[i][k] /= scale;						s += u[i][k]*u[i][k];					}					f = u[i][l-1];					g = -sign(Math.sqrt(s), f);					h = f*g-s;					u[i][l-1] = f-g;					for (k=l-1; k<n; k++) rv1[k] = u[i][k]/h;					for (j=l-1; j<m; j++) {						for (s=0.0,k=l-1; k<n; k++) s += u[j][k]*u[i][k];						for (k=l-1; k<n; k++) u[j][k] += s*rv1[k];					}					for (k=l-1; k<n; k++) u[i][k] *= scale;				}			}			anorm = Math.max(anorm, (Math.abs(w[i])+Math.abs(rv1[i])));		}		// Accumulation of right-hand transformations		for (i=n-1; i>=0; i--) {			if (i < n-1) {				if (g != 0.0) {					// Double division to avoid possible underflow					for (j=l; j<n; j++)						v[j][i] = (u[i][j]/u[i][l])/g;					for (j=l; j<n; j++) {						for (s=0.0,k=l; k<n; k++) s += u[i][k]*v[k][j];						for (k=l; k<n; k++) v[k][j] += s*v[k][i];					}				}				for (j=l; j<n; j++) v[i][j] = v[j][i] = 0.0;			}			v[i][i] = 1.0;			g = rv1[i];			l = i;		}		// Accumulation of left-hand trensformations		for (i=Math.min(m,n)-1; i>=0; i--) {			l = i+1;			g = w[i];			for (j=l; j<n; j++) u[i][j] = 0.0;			if (g != 0.0) {				g = 1.0/g;				for (j=l; j<n; j++) {					for (s=0.0,k=l; k<m; k++) s += u[k][i]*u[k][j];					f = (s/u[i][i])*g;					for (k=i; k<m; k++) u[k][j] += f*u[k][i];				}				for (j=i; j<m; j++) u[j][i] *= g;			} else for (j=i; j<m; j++) u[j][i] = 0.0;			++u[i][i];		}		// Diagnonalization of the bidiagonal form: Loop over singular		// values, and over allowed iterations		for (k=n-1; k>=0; k--) {			for (its=0; its<30; its++) {				flag = true;				for (l=k; l>=0; l--) {	// Test for splitting					nm = l-1;		// Note that rv1[0] is always zero					if (Math.abs(rv1[l])+anorm == anorm) {						flag = false;						break;					}					if (Math.abs(w[nm])+anorm == anorm) break;				}				if (flag) {					// Cancellation of rv1[1], if l>0					c = 0.0;					s = 1.0;					for (i=l-1; i<k+1; i++) {						f = s*rv1[i];						rv1[i] = c*rv1[i];						if (Math.abs(f)+anorm == anorm) break;						g = w[i];						h = pythag(f, g);						w[i] = h;						h = 1.0/h;						c = g*h;						s = -f*h;						for (j=0; j<m; j++) {							y = u[j][nm];							z = u[j][i];							u[j][nm] = y*c+z*s;							u[j][i] = z*c-y*s;						}					}				}				z = w[k];				if (l == k) {		// Convergence					if (z < 0.0) {	// Singular value is made nonnegative						w[k] = -z;						for (j=0; j<n; j++) v[j][k] = -v[j][k];					}					break;				}				if (its == 29)					throw new ArithmeticException(							"No convergence in 30 SVD iterations");				// Shift from bottom 2-by-2 minor				x = w[l];				nm = k-1;				y = w[nm];				g = rv1[nm];				h = rv1[k];				f = ((y-z)*(y+z)+(g-h)*(g+h))/(2.0*h*y);				g = pythag(f, 1.0);				f = ((x-z)*(x+z)+h*((y/(f+sign(g, f)))-h))/x;				c = s = 1.0;				for (j=l; j<=nm; j++) {					i = j+1;					g = rv1[i];					y = w[i];					h = s*g;					g = c*g;					z = pythag(f, h);					rv1[j] = z;					c = f/z;					s = h/z;					f = x*c+g*s;					g = g*c-x*s;					h = y*s;					y *= c;					for (jj=0; jj<n; jj++) {						x = v[jj][j];						z = v[jj][i];						v[jj][j] = x*c+z*s;						v[jj][i] = z*c-x*s;					}					z = pythag(f, h);					// Rotation can be arbitrary if z=0					w[j] = z;					if (z!=0.0) {						z = 1.0/z;						c = f*z;						s = h*z;					}					f = c*g+s*y;					x = c*y-s*g;					for (jj=0; jj<m; jj++) {						y = u[jj][j];						z = u[jj][i];						u[jj][j] = y*c+z*s;						u[jj][i] = z*c-y*s;					}				}				rv1[l] = 0.0;				rv1[k] = f;				w[k] = x;			}		}	} // end doSVD	private double sign(double a, double b) {		return b>=0.0 ? Math.abs(a) : -Math.abs(a);	}	private double pythag(double a, double b) {		double absa,absb;			absa=Math.abs(a);		absb=Math.abs(b);		if (absa>absb) return absa*Math.sqrt(1.0+(absb/absa)*(absb/absa));		else return (absb==0.0 ? 0.0 :							absb*Math.sqrt(1.0+(absa/absb)*(absa/absb)));	}abstract static public class SVDfunc {	/** number of basis functions */	abstract public int length();	/** Value of basis functions at x */	abstract public double[] getX(double x);	/** Value at location, x, given coefficients, a[] */	abstract public double eval(double[] a, double x);	/** Vertex given coefficients a */	abstract public double vertex(double[] a);	/** Vertex is a maximum */	abstract public boolean maximum(double[] a);}static public class Parabola extends SVDfunc {	/** null constructor */	public Parabola() {return;}	public int length() {return 3;}	public double[] getX(double x) {return new double[] {1.0, x, x*x};}	public double eval(double[] a, double x) {return a[0]+a[1]*x+a[2]*x*x;}	public double vertex(double[] a) {return -a[1]/(2.0*a[2]);}	public boolean maximum(double[] a) {return a[2]<0;}}} // end SVD