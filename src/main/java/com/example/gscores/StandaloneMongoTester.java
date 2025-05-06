package com.example.gscores; // Hoặc package bạn đã tạo

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class StandaloneMongoTester {

    public static void main(String[] args) {
        String connectionString = "mongodb+srv://quocti:123456789ti@cluster0.y3phnc4.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
        // --------------------------------------------------------------------------

        System.out.println("--- Bắt đầu kiểm tra kết nối MongoDB độc lập ---");
        System.out.println("Sử dụng Connection String: " + connectionString);

        // --- Kiểm tra các lỗi phổ biến trong Connection String ---
        if (connectionString == null || connectionString.isBlank()) {
            System.err.println("LỖI: Connection String bị trống!");
            return;
        }
        if (connectionString.contains("<password>") || connectionString.contains("<db_password>") || connectionString.contains("<username>")) {
            System.err.println("LỖI: Connection String vẫn chứa placeholder (<password>, <username>...).");
            return;
        }
        // Kiểm tra xem có tên database sau dấu / và trước dấu ? không
        if (!connectionString.matches(".+/.+\\?.*")) {
            System.err.println("CẢNH BÁO: Connection String dường như thiếu tên database trước dấu '?'. Ví dụ: ...mongodb.net/tên_database?retryWrites=true");
        }



        // Cấu hình ServerApi
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        // Cấu hình MongoClientSettings
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                // Optional: Thêm timeout để tránh chờ quá lâu
                // .applyToClusterSettings(builder ->
                //         builder.serverSelectionTimeout(5000, TimeUnit.MILLISECONDS))
                .build();

        // Tạo client và thực hiện ping trong try-with-resources
        System.out.println("Đang tạo MongoClient và thử kết nối/ping...");
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            try {
                // Ping vào database 'admin'
                MongoDatabase database = mongoClient.getDatabase("admin");
                database.runCommand(new Document("ping", 1));
                System.out.println("**************************************************************");
                System.out.println("*** THÀNH CÔNG! Đã kết nối và ping thành công tới MongoDB! ***");
                System.out.println("**************************************************************");
            } catch (MongoException e) {
                System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.err.println("!!! THẤT BẠI! Không thể ping tới MongoDB deployment. Lỗi: !!!");
                System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.err.println("Thông báo lỗi: " + e.getMessage());
                System.err.println("---");
                System.err.println("Gợi ý kiểm tra:");
                System.err.println("1. Mật khẩu trong connection string có chính xác và đã được URL-encode nếu cần?");
                System.err.println("2. Network Access trong MongoDB Atlas đã cho phép địa chỉ IP của bạn chưa (0.0.0.0/0 hoặc IP cụ thể)?");
                System.err.println("3. Cluster MongoDB Atlas có đang chạy (trạng thái online) không?");
                System.err.println("4. Kết nối mạng của bạn có ổn định không?");
                System.err.println("--- Chi tiết lỗi Java: ---");
                e.printStackTrace(); // In chi tiết lỗi Java
            }
        } catch (Exception e) {
            // Bắt các lỗi khác khi tạo client (sai định dạng URI...)
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("!!! THẤT BẠI! Lỗi nghiêm trọng khi tạo MongoClient hoặc cấu hình. Lỗi: !!!");
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("Thông báo lỗi: " + e.getMessage());
            System.err.println("---");
            System.err.println("Gợi ý kiểm tra:");
            System.err.println("1. Cú pháp của Connection String có hoàn toàn chính xác không?");
            System.err.println("2. Các thư viện MongoDB driver cần thiết có trong classpath không (IDE/Maven thường tự xử lý)?");
            System.err.println("--- Chi tiết lỗi Java: ---");
            e.printStackTrace();
        } finally {
            System.out.println("--- Kết thúc kiểm tra kết nối MongoDB độc lập ---");
        }
    }
}