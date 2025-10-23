import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DocumentManager {
    private static final Map<Integer, Document> documents = new ConcurrentHashMap<>();
    private static final Map<String, List<Integer>> userDocuments = new ConcurrentHashMap<>();
    private static final Map<Integer, List<DownloadRequest>> downloadRequests = new ConcurrentHashMap<>();
    private static int nextDocumentId = 1;

    public static class Document {
        public int id;
        public String name;
        public String owner;
        public long size;
        public String type;
        public Date uploadDate;
        public boolean approved;
        public String sharedWith; // "all" or specific users

        public Document(int id, String name, String owner, long size, String type) {
            this.id = id;
            this.name = name;
            this.owner = owner;
            this.size = size;
            this.type = type;
            this.uploadDate = new Date();
            this.approved = "admin".equals(owner); // Admin files auto-approved
            this.sharedWith = "all";
        }
    }

    public static class DownloadRequest {
        public String username;
        public int documentId;
        public Date requestDate;
        public boolean approved;

        public DownloadRequest(String username, int documentId) {
            this.username = username;
            this.documentId = documentId;
            this.requestDate = new Date();
            this.approved = false;
        }
    }

    static {
        // Add sample documents for testing
        addDocument("Project Proposal.docx", "admin", 2457600L, "Word");
        addDocument("Financial Report Q4.pdf", "admin", 5120000L, "PDF");
        addDocument("Company Budget.xlsx", "admin", 1843200L, "Excel");
        addDocument("Team Photo.jpg", "admin", 3145728L, "Image");
        addDocument("Product Demo.mp4", "admin", 15728640L, "Video");
        addDocument("Meeting Notes.pdf", "client1", 1024000L, "PDF");
        addDocument("Research Data.xlsx", "client2", 4096000L, "Excel");
    }

    public static synchronized int addDocument(String name, String owner, long size, String type) {
        int id = nextDocumentId++;
        Document doc = new Document(id, name, owner, size, type);
        documents.put(id, doc);

        // Add to user's document list
        userDocuments.computeIfAbsent(owner, k -> new ArrayList<>()).add(id);

        return id;
    }

    public static List<Document> getUserDocuments(String username) {
        List<Document> result = new ArrayList<>();
        List<Integer> userDocIds = userDocuments.get(username);
        if (userDocIds != null) {
            for (int docId : userDocIds) {
                Document doc = documents.get(docId);
                if (doc != null) {
                    result.add(doc);
                }
            }
        }
        return result;
    }

    public static List<Document> getAllDocuments() {
        return new ArrayList<>(documents.values());
    }

    public static List<Document> getApprovedDocuments() {
        List<Document> result = new ArrayList<>();
        for (Document doc : documents.values()) {
            if (doc.approved) {
                result.add(doc);
            }
        }
        return result;
    }

    public static synchronized void requestDownload(String username, int documentId) {
        DownloadRequest request = new DownloadRequest(username, documentId);
        downloadRequests.computeIfAbsent(documentId, k -> new ArrayList<>()).add(request);
    }

    public static List<DownloadRequest> getPendingRequests() {
        List<DownloadRequest> result = new ArrayList<>();
        for (List<DownloadRequest> requests : downloadRequests.values()) {
            for (DownloadRequest request : requests) {
                if (!request.approved) {
                    result.add(request);
                }
            }
        }
        return result;
    }

    public static synchronized boolean approveDownload(String username, int documentId) {
        List<DownloadRequest> requests = downloadRequests.get(documentId);
        if (requests != null) {
            for (DownloadRequest request : requests) {
                if (request.username.equals(username) && !request.approved) {
                    request.approved = true;
                    return true;
                }
            }
        }
        return false;
    }

    public static synchronized boolean approveDocument(int documentId) {
        Document doc = documents.get(documentId);
        if (doc != null && !doc.approved) {
            doc.approved = true;
            return true;
        }
        return false;
    }

    public static Document getDocumentById(int documentId) {
        return documents.get(documentId);
    }

    public static int getTotalDocumentCount() {
        return documents.size();
    }

    public static long getTotalStorageUsed() {
        long total = 0;
        for (Document doc : documents.values()) {
            total += doc.size;
        }
        return total;
    }
}