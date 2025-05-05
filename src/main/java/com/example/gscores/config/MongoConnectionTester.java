package com.example.gscores.config; // Hoặc package bạn muốn đặt (ví dụ: com.example.gscores.util)

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger; // Sử dụng Logger của SLF4J
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value; // Để inject giá trị từ properties
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order; // Optional: để kiểm soát thứ tự chạy nếu có nhiều Runner
import org.springframework.stereotype.Component;

@Component // Đánh dấu là một Spring Bean để được quét và quản lý
@Order(1) // Optional: Đặt thứ tự ưu tiên chạy (số nhỏ chạy trước)
public class MongoConnectionTester implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MongoConnectionTester.class);

    // Inject giá trị connection string từ application.properties
    @Value("${spring.data.mongodb.uri}")
    private String connectionString;

    @Override
    public void run(String... args) throws Exception {
        log.info("--- MongoDB Connection Test Runner: Bắt đầu kiểm tra kết nối ---");

        // Kiểm tra xem connectionString có giá trị không
        if (connectionString == null || connectionString.isBlank()) {
            log.error("LỖI KIỂM TRA KẾT NỐI: Giá trị 'spring.data.mongodb.uri' bị thiếu hoặc trống trong application.properties!");
            return; // Không thực hiện kiểm tra nếu không có URI
        }

        // Kiểm tra xem có placeholder password không (đây là lỗi phổ biến)
        if (connectionString.contains("<password>") || connectionString.contains("<db_password>")) {
            log.error("LỖI KIỂM TRA KẾT NỐI: Connection string '{}' dường như chứa placeholder '<password>' hoặc '<db_password>'. Vui lòng thay thế bằng mật khẩu thực tế!", connectionString);
            // Bạn có thể quyết định dừng ứng dụng ở đây nếu muốn bằng cách ném Exception
            // throw new IllegalStateException("Password placeholder found in MongoDB connection string.");
            return; // Hoặc chỉ cảnh báo và tiếp tục
        }


        // Cấu hình ServerApi (giống code gốc của bạn)
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        // Cấu hình MongoClientSettings (giống code gốc của bạn)
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                // Optional: Có thể thêm timeout để tránh treo quá lâu nếu kết nối thất bại
                // .applyToClusterSettings(builder ->
                //         builder.serverSelectionTimeout(5000, TimeUnit.MILLISECONDS)) // Timeout 5 giây
                .build();

        // Tạo client và thực hiện ping (dùng try-with-resources để đảm bảo client đóng lại)
        log.info("Đang cố gắng kết nối và ping tới MongoDB Atlas...");
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            try {
                // Ping vào database 'admin' để xác nhận kết nối
                MongoDatabase database = mongoClient.getDatabase("admin");
                database.runCommand(new Document("ping", 1));
                // Nếu lệnh ping thành công mà không ném Exception -> Kết nối OK
                log.info("--- MongoDB Connection Test Runner: THÀNH CÔNG! Kết nối tới MongoDB Atlas thành công. ---");
            } catch (MongoException e) {
                // Nếu có lỗi khi ping (sai mật khẩu, sai network access, cluster offline...)
                log.error("--- MongoDB Connection Test Runner: THẤT BẠI! Không thể ping tới deployment. Lỗi: {} ---", e.getMessage());
                log.error("Chi tiết lỗi kết nối:", e); // Log stack trace để debug
                // Cân nhắc: Ném Exception ở đây để dừng hoàn toàn ứng dụng nếu kết nối DB là bắt buộc
                // throw new RuntimeException("Failed to connect and ping MongoDB Atlas", e);
            }
        } catch (Exception e) {
            // Bắt các lỗi khác có thể xảy ra khi tạo MongoClient (ví dụ: sai định dạng URI)
            log.error("--- MongoDB Connection Test Runner: THẤT BẠI! Có lỗi khi khởi tạo MongoClient hoặc kết nối. Lỗi: {} ---", e.getMessage());
            log.error("Chi tiết lỗi khởi tạo:", e);
            // throw new RuntimeException("Failed to create MongoClient", e);
        }
        log.info("--- MongoDB Connection Test Runner: Kết thúc kiểm tra kết nối ---");
    }
}