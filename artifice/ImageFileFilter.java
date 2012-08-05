package artifice;

import static artifice.ArtificeConstants.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

public class ImageFileFilter extends FileFilter {
     public final static String[] _compatibleTypes =
     { "jpg", "png", "jpeg", "gif" };

    // Only accept sound files supported by java's built-in AudioClips
    public boolean accept(File f) {
        if (f.isDirectory())
            return true;

        return this.isCompatibleFile(f);
    }

    public String getDescription() {
        return "Image files";
    }

    public boolean isCompatibleFile(File f) {
        // Get the File's extension
        String ext = this.getExtension(f);

//        return (ext != null && ext.equals(".jpg"));
        // System.out.println(s + " " + f + " " + ext + " " + i);
        
        // Check file's extension against list of compatible extension types
        for(int i = 0; i < _compatibleTypes.length; i++) {
            if (ext.equals(_compatibleTypes[i]))
                return true;
        }
        
        return false;
        
    }
    
    static public String getAbsoluteName(File f) {
        String ext = ImageFileFilter.getExtension(f);
        String name = f.getAbsolutePath();

        if (ext == null)
            return name;
        
        return name.substring(0, name.length() - ext.length() - 1);

    }
    
    static public String getName(File f) {
        String ext = ImageFileFilter.getExtension(f);
        String name = f.getName();

        if (ext == null)
            return name;

        return name.substring(0, name.length() - ext.length() - 1);
    }

    static public String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1)
            ext = s.substring(i + 1).toLowerCase();

        return ext;
    }
}
