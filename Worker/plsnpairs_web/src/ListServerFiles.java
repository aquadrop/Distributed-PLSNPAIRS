import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ListServerFiles
 */
public class ListServerFiles extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ListServerFiles() {
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
		String folderString = request.getParameter("folder");
		
		ArrayList<String> folders = new ArrayList<String>();
		ArrayList<String> files = new ArrayList<String>();
		
		File folder = new File(folderString);
		
		if (folder.exists() ) {
			if (folder.isDirectory() ) {
				File[] childfiles = folder.listFiles();
				for(File file : childfiles) {
					if (file.isDirectory() ) {
						folders.add(file.getName() );
					} else {
						files.add(file.getName() );
					}
					
				}
			}
		}
		
		Collections.sort(folders);
		String foldersString = "";
		for(String s : folders) {
			foldersString += s + ",";
		}
		
			if (foldersString.length() > 0) {
				response.getWriter().println(foldersString.substring(0, foldersString.length() - 1));
			} else {
				response.getWriter().println("");
			}
		
		
		Collections.sort(files);
		String filesString = "";
		for(String s : files) {
			filesString += s + ",";
		}
		
		if (filesString.length() > 0) {
			response.getWriter().print(filesString.substring(0, filesString.length() - 1));
		} else {
			response.getWriter().print("");
		}
	}

}