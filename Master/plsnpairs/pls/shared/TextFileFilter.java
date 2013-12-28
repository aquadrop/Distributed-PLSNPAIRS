
package pls.shared;
import java.io.File;
import javax.swing.filechooser.FileFilter;

public class TextFileFilter extends FileFilter{
  public boolean accept(File f) {
        return f.isDirectory() || f.getName().endsWith(".txt");
    }

    public String getDescription() {
        return "txt file";
    }
}
