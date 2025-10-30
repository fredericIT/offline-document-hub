import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class LANCommunicationManager {
    private static final int BROADCAST_PORT = 8888;
    private static final int FILE_TRANSFER_PORT = 8889;
    private static ServerSocket serverSocket;
    private static boolean isRunning = false;

    public static void startLANService() {
        if (isRunning) return;

        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(FILE_TRANSFER_PORT);
                isRunning = true;
                System.out.println("LAN Communication Service started on port " + FILE_TRANSFER_PORT);

                while (isRunning) {
                    Socket clientSocket = serverSocket.accept();
                    new ClientHandler(clientSocket).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        startBroadcastListener();
    }

    private static void startBroadcastListener() {
        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(BROADCAST_PORT)) {
                byte[] buffer = new byte[256];

                while (isRunning) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String message = new String(packet.getData(), 0, packet.getLength());
                    handleBroadcastMessage(message, packet.getAddress());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void handleBroadcastMessage(String message, InetAddress source) {
        System.out.println("Received LAN broadcast: " + message + " from " + source);

        // Handle different types of LAN messages
        if (message.startsWith("DOCUMENT_SHARE:")) {
            String[] parts = message.split(":");
            if (parts.length >= 3) {
                String docName = parts[1];
                String fromUser = parts[2];

                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null,
                            "User " + fromUser + " shared document: " + docName + "\n" +
                                    "via LAN transfer",
                            "Document Shared", JOptionPane.INFORMATION_MESSAGE);
                });
            }
        }
    }

    public static void broadcastToLAN(String message) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);

            byte[] buffer = message.getBytes();

            // Broadcast to local network
            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
                    broadcastAddress, BROADCAST_PORT);
            socket.send(packet);

            System.out.println("LAN Broadcast sent: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void shareDocumentViaLAN(String docName, String filePath, String[] targetUsers) {
        broadcastToLAN("DOCUMENT_SHARE:" + docName + ":" + UserStore.getCurrentUser());

        // In real implementation, establish direct file transfer to specific users
        for (String user : targetUsers) {
            // Simulate file transfer over LAN
            System.out.println("Sharing " + docName + " with " + user + " via LAN");
        }
    }

    public static void stopLANService() {
        isRunning = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                 ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

                // Handle file transfer requests
                Object request = in.readObject();
                if (request instanceof FileTransferRequest) {
                    handleFileTransfer((FileTransferRequest) request, out);
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void handleFileTransfer(FileTransferRequest request, ObjectOutputStream out) {
            // Implement file transfer logic
            System.out.println("Handling file transfer request for: " + request.getFileName());
        }
    }

    private static class FileTransferRequest implements Serializable {
        private String fileName;
        private String requestedBy;

        public FileTransferRequest(String fileName, String requestedBy) {
            this.fileName = fileName;
            this.requestedBy = requestedBy;
        }

        public String getFileName() { return fileName; }
        public String getRequestedBy() { return requestedBy; }
    }
}