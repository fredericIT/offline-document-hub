import javax.swing.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CollaborationManager {
    private static final Map<String, List<CollaborationSession>> userSessions = new ConcurrentHashMap<>();
    private static final Map<String, DocumentCollaborators> documentCollaborators = new ConcurrentHashMap<>();

    public static class CollaborationSession {
        public String sessionId;
        public String documentId;
        public String owner;
        public List<String> participants;
        public Date startTime;
        public boolean isActive;

        public CollaborationSession(String documentId, String owner) {
            this.sessionId = UUID.randomUUID().toString();
            this.documentId = documentId;
            this.owner = owner;
            this.participants = new ArrayList<>();
            this.participants.add(owner);
            this.startTime = new Date();
            this.isActive = true;
        }
    }

    public static class DocumentCollaborators {
        public String documentId;
        public Set<String> collaborators;
        public Map<String, String> userCursors; // user -> cursor position
        public List<EditHistory> editHistory;

        public DocumentCollaborators(String documentId) {
            this.documentId = documentId;
            this.collaborators = new HashSet<>();
            this.userCursors = new HashMap<>();
            this.editHistory = new ArrayList<>();
        }
    }

    public static class EditHistory {
        public String userId;
        public String action;
        public Date timestamp;
        public String details;

        public EditHistory(String userId, String action, String details) {
            this.userId = userId;
            this.action = action;
            this.timestamp = new Date();
            this.details = details;
        }
    }

    public static CollaborationSession startCollaborationSession(String documentId, String owner) {
        CollaborationSession session = new CollaborationSession(documentId, owner);
        userSessions.computeIfAbsent(owner, k -> new ArrayList<>()).add(session);

        // Notify other users via LAN
        LANCommunicationManager.broadcastToLAN("COLLABORATION_START:" + documentId + ":" + owner);

        return session;
    }

    public static void inviteToCollaboration(String sessionId, String targetUser) {
        // Find session and add user
        for (List<CollaborationSession> sessions : userSessions.values()) {
            for (CollaborationSession session : sessions) {
                if (session.sessionId.equals(sessionId) && !session.participants.contains(targetUser)) {
                    session.participants.add(targetUser);

                    // Notify the invited user
                    LANCommunicationManager.broadcastToLAN("COLLABORATION_INVITE:" + sessionId + ":" + targetUser);
                    break;
                }
            }
        }
    }

    public static void addDocumentCollaborator(String documentId, String userId) {
        DocumentCollaborators collaborators = documentCollaborators.computeIfAbsent(
                documentId, k -> new DocumentCollaborators(documentId));
        collaborators.collaborators.add(userId);
    }

    public static void trackUserCursor(String documentId, String userId, String cursorPosition) {
        DocumentCollaborators collaborators = documentCollaborators.get(documentId);
        if (collaborators != null) {
            collaborators.userCursors.put(userId, cursorPosition);

            // Broadcast cursor position to other collaborators
            LANCommunicationManager.broadcastToLAN(
                    "CURSOR_UPDATE:" + documentId + ":" + userId + ":" + cursorPosition);
        }
    }

    public static void logEditHistory(String documentId, String userId, String action, String details) {
        DocumentCollaborators collaborators = documentCollaborators.get(documentId);
        if (collaborators != null) {
            EditHistory history = new EditHistory(userId, action, details);
            collaborators.editHistory.add(history);

            // Limit history size
            if (collaborators.editHistory.size() > 100) {
                collaborators.editHistory.remove(0);
            }
        }
    }

    public static List<EditHistory> getEditHistory(String documentId) {
        DocumentCollaborators collaborators = documentCollaborators.get(documentId);
        return collaborators != null ? collaborators.editHistory : new ArrayList<>();
    }

    public static Set<String> getDocumentCollaborators(String documentId) {
        DocumentCollaborators collaborators = documentCollaborators.get(documentId);
        return collaborators != null ? collaborators.collaborators : new HashSet<>();
    }

    public static void endCollaborationSession(String sessionId) {
        for (List<CollaborationSession> sessions : userSessions.values()) {
            sessions.removeIf(session -> session.sessionId.equals(sessionId));
        }

        LANCommunicationManager.broadcastToLAN("COLLABORATION_END:" + sessionId);
    }
}