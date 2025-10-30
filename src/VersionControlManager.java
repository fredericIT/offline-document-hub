import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

public class VersionControlManager {
    private static final String VERSIONS_DIR = "./data/versions/";
    private static final int MAX_VERSIONS_PER_FILE = 50;

    public static class FileVersion {
        public String versionId;
        public String fileName;
        public String filePath;
        public String createdBy;
        public Date createdDate;
        public long fileSize;
        public String changeDescription;
        public int versionNumber;

        public FileVersion(String fileName, String createdBy, String changeDescription, int versionNumber) {
            this.versionId = UUID.randomUUID().toString();
            this.fileName = fileName;
            this.createdBy = createdBy;
            this.createdDate = new Date();
            this.changeDescription = changeDescription;
            this.versionNumber = versionNumber;
        }
    }

    public static FileVersion createVersion(File file, String username, String changeDescription) {
        try {
            // Ensure versions directory exists
            new File(VERSIONS_DIR).mkdirs();

            String baseName = getFileBaseName(file.getName());
            int nextVersion = getNextVersionNumber(baseName);

            FileVersion version = new FileVersion(file.getName(), username, changeDescription, nextVersion);
            version.filePath = VERSIONS_DIR + baseName + "_v" + nextVersion + ".zip";

            // Create compressed version
            try (FileOutputStream fos = new FileOutputStream(version.filePath);
                 ZipOutputStream zos = new ZipOutputStream(fos);
                 FileInputStream fis = new FileInputStream(file)) {

                ZipEntry zipEntry = new ZipEntry(file.getName());
                zos.putNextEntry(zipEntry);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
            }

            version.fileSize = new File(version.filePath).length();

            // Save version metadata
            saveVersionMetadata(version);

            // Clean up old versions if exceeding limit
            cleanupOldVersions(baseName);

            return version;
        } catch (IOException e) {
            System.err.println("Failed to create file version: " + e.getMessage());
            return null;
        }
    }

    public static boolean restoreVersion(String versionId, String targetPath) {
        try {
            FileVersion version = getVersion(versionId);
            if (version == null) return false;

            try (FileInputStream fis = new FileInputStream(version.filePath);
                 ZipInputStream zis = new ZipInputStream(fis)) {

                ZipEntry zipEntry = zis.getNextEntry();
                if (zipEntry != null) {
                    try (FileOutputStream fos = new FileOutputStream(targetPath)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                    zis.closeEntry();
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to restore version: " + e.getMessage());
        }
        return false;
    }

    public static List<FileVersion> getFileVersions(String fileName) {
        List<FileVersion> versions = new ArrayList<>();
        String baseName = getFileBaseName(fileName);
        File versionsDir = new File(VERSIONS_DIR);

        File[] versionFiles = versionsDir.listFiles((dir, name) ->
                name.startsWith(baseName + "_v") && name.endsWith(".zip"));

        if (versionFiles != null) {
            for (File versionFile : versionFiles) {
                FileVersion version = loadVersionMetadata(versionFile.getName());
                if (version != null) {
                    versions.add(version);
                }
            }
        }

        versions.sort((v1, v2) -> Integer.compare(v2.versionNumber, v1.versionNumber));
        return versions;
    }

    public static boolean compareVersions(String versionId1, String versionId2) {
        // Implementation for comparing two versions
        // This could use diff tools or custom comparison logic
        System.out.println("Comparing versions: " + versionId1 + " vs " + versionId2);
        return true;
    }

    private static String getFileBaseName(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }

    private static int getNextVersionNumber(String baseName) {
        List<FileVersion> existingVersions = getFileVersions(baseName + ".tmp");
        return existingVersions.size() + 1;
    }

    private static void saveVersionMetadata(FileVersion version) {
        String metadataFile = VERSIONS_DIR + version.versionId + ".meta";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(metadataFile))) {
            oos.writeObject(version);
        } catch (IOException e) {
            System.err.println("Failed to save version metadata: " + e.getMessage());
        }
    }

    private static FileVersion loadVersionMetadata(String versionFileName) {
        String baseName = versionFileName.replace(".zip", "");
        String metadataFile = VERSIONS_DIR + baseName + ".meta";

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(metadataFile))) {
            return (FileVersion) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load version metadata: " + e.getMessage());
            return null;
        }
    }

    private static void cleanupOldVersions(String baseName) {
        List<FileVersion> versions = getFileVersions(baseName + ".tmp");
        if (versions.size() > MAX_VERSIONS_PER_FILE) {
            versions.sort((v1, v2) -> Integer.compare(v1.versionNumber, v2.versionNumber));
            for (int i = 0; i < versions.size() - MAX_VERSIONS_PER_FILE; i++) {
                FileVersion oldVersion = versions.get(i);
                new File(oldVersion.filePath).delete();
                new File(VERSIONS_DIR + oldVersion.versionId + ".meta").delete();
            }
        }
    }

    private static FileVersion getVersion(String versionId) {
        File metadataFile = new File(VERSIONS_DIR + versionId + ".meta");
        if (!metadataFile.exists()) return null;

        return loadVersionMetadata(versionId + ".zip");
    }
}