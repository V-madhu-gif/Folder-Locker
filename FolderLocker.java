import java.io.File;
import java.io.IOException;
public class FolderLocker {
    private static final String LOCKED_EXTENSION = ".locked";

    public static void main(String[] args) {
        String folderPath = ""; // Specify the folder path here
        String password = ""; // Specify your desired password here

        // Lock the folder
        lockFolder(folderPath, password);

        // Unlock the folder
        unlockFolder(folderPath, password);
    }

    private static void lockFolder(String folderPath, String password) {
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Invalid folder path");
            return;
        }

        // Create a locked folder
        String lockedFolderPath = folderPath + LOCKED_EXTENSION;
        File lockedFolder = new File(lockedFolderPath);
        if (lockedFolder.exists()) {
            System.out.println("Folder is already locked");
            return;
        }

        if (!lockedFolder.mkdir()) {
            System.out.println("Failed to create locked folder");
            return;
        }

        // Move all files and subdirectories to the locked folder
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                try {
                    moveFileOrFolder(file, new File(lockedFolderPath, file.getName()));
                } catch (IOException e) {
                    System.out.println("Failed to lock the folder");
                    return;
                }
            }
        }

        System.out.println("Folder locked successfully");
    }

    private static void unlockFolder(String folderPath, String password) {
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Invalid folder path");
            return;
        }

        if (!folder.getName().endsWith(LOCKED_EXTENSION)) {
            System.out.println("Folder is not locked");
            return;
        }

        // Create an unlocked folder
        String unlockedFolderPath = folderPath.substring(0, folderPath.length() - LOCKED_EXTENSION.length());
        File unlockedFolder = new File(unlockedFolderPath);
        if (unlockedFolder.exists()) {
            System.out.println("Unlocked folder already exists");
            return;
        }

        if (!unlockedFolder.mkdir()) {
            System.out.println("Failed to create unlocked folder");
            return;
        }

        // Move all files and subdirectories to the unlocked folder
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                try {
                    moveFileOrFolder(file, new File(unlockedFolderPath, file.getName()));
                } catch (IOException e) {
                    System.out.println("Failed to unlock the folder");
                    return;
                }
            }
        }

        // Delete the locked folder
        if (!folder.delete()) {
            System.out.println("Failed to delete locked folder");
            return;
        }

        System.out.println("Folder unlocked successfully");
    }

    private static void moveFileOrFolder(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            destination.mkdir();
            File[] files = source.listFiles();
            if (files != null) {
                for (File file : files) {
                    moveFileOrFolder(file, new File(destination, file.getName()));
                }
            }
        } else {
            java.nio.file.Files.move(source.toPath(), destination.toPath());
        }
    }
}