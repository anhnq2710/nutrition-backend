package com.example.nutrition_backend.service;

import ai.onnxruntime.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.FloatBuffer;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PredictAIService {

    private OrtEnvironment env;
    private OrtSession session;
    private List<String> labels;

    @PostConstruct
    public synchronized void init() throws Exception {
        if (env != null && session != null) {
            return; // đã init
        }

        // Khởi tạo environment
        this.env = OrtEnvironment.getEnvironment();

        // Load model từ classpath (dùng InputStream vì getFile() thất bại khi đóng gói jar)
        ClassPathResource modelResource = new ClassPathResource("model/30mon_final_model.onnx");
        try (InputStream modelStream = modelResource.getInputStream()) {
            // OrtSession có thể cần file vật lý -> copy sang temp
            File tmp = File.createTempFile("model-", ".onnx");
            tmp.deleteOnExit();
            try (OutputStream out = new FileOutputStream(tmp)) {
                modelStream.transferTo(out);
            }
            this.session = env.createSession(tmp.getAbsolutePath());
        }

        // In tên input để debug
        System.out.println("Input names trong model:");
        session.getInputNames().forEach(System.out::println);

        // Load labels (từ classpath)
        ClassPathResource labelsResource = new ClassPathResource("model/labels.txt");
        try (InputStream labelsStream = labelsResource.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(labelsStream))) {
            labels = br.lines().map(String::trim).filter(l -> !l.isBlank()).collect(Collectors.toList());
        }

        if (labels == null || labels.isEmpty()) {
            throw new IllegalStateException("Không load được file labels.txt hoặc file rỗng");
        }

        System.out.println("Model ONNX + " + labels.size() + " nhãn đã load thành công!");
    }

    public Map<String, Object> predict(MultipartFile imageFile) throws Exception {
        // Đảm bảo env và session không null
        if (env == null || session == null) {
            throw new IllegalStateException("ONNX model chưa được khởi tạo!");
        }

        // Đọc và resize ảnh
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageFile.getBytes()));
        if (img == null) {
            throw new IllegalArgumentException("Không đọc được ảnh từ file upload");
        }
        final int height = 224;
        final int width = 224;
        final int channels = 3;
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        resized.getGraphics().drawImage(img, 0, 0, width, height, null);

        // Lấy input info từ model để biết ordering (NCHW vs NHWC) và shape
        Map<String, NodeInfo> inputInfo = session.getInputInfo();
        String inputName = inputInfo.keySet().iterator().next();
        TensorInfo tInfo = (TensorInfo) inputInfo.get(inputName).getInfo();
        long[] inputShape = tInfo.getShape(); // ví dụ: [1,3,224,224] hoặc [1,224,224,3]
        System.out.println("Model input name: " + inputName + ", shape: " + Arrays.toString(inputShape));

        // Quyết định NCHW hay NHWC:
        boolean isNCHW;
        if (inputShape.length == 4) {
            // nếu index 1 = 3 => NCHW; nếu index 3 = 3 => NHWC
            long dim1 = inputShape[1];
            long dim3 = inputShape[3];
            if (dim1 == 3 || dim1 == -1 && dim3 != 3) {
                isNCHW = true;
            } else if (dim3 == 3 || dim3 == -1 && dim1 != 3) {
                isNCHW = false;
            } else {
                // fallback: nếu không rõ thì thử NCHW (thường phổ biến)
                isNCHW = true;
            }
        } else {
            // fallback
            isNCHW = true;
        }
        System.out.println("Detected ordering: " + (isNCHW ? "NCHW (channel-first)" : "NHWC (channel-last)"));

        // Chuẩn bị FloatBuffer với shape phù hợp
        long[] tensorShape;
        int totalSize;
        if (isNCHW) {
            tensorShape = new long[]{1, channels, height, width};
            totalSize = 1 * channels * height * width;
        } else {
            tensorShape = new long[]{1, height, width, channels};
            totalSize = 1 * height * width * channels;
        }

        FloatBuffer buffer = FloatBuffer.allocate(totalSize);

        // Ghi pixels theo ordering đã detect. Chuẩn hóa đơn giản /255.0 (nếu mô hình cần mean/std bạn phải thay vào)
        if (isNCHW) {
            // channel-first: [C, H, W]
            for (int c = 0; c < channels; c++) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pixel = resized.getRGB(x, y);
                        float value;
                        if (c == 0) { // R
                            value = ((pixel >> 16) & 0xFF) / 255.0f;
                        } else if (c == 1) { // G
                            value = ((pixel >> 8) & 0xFF) / 255.0f;
                        } else { // B
                            value = (pixel & 0xFF) / 255.0f;
                        }
                        buffer.put(value);
                    }
                }
            }
        } else {
            // channel-last: [H, W, C]
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = resized.getRGB(x, y);
                    // order: R, G, B
                    buffer.put(((pixel >> 16) & 0xFF) / 255.0f);
                    buffer.put(((pixel >> 8) & 0xFF) / 255.0f);
                    buffer.put((pixel & 0xFF) / 255.0f);
                }
            }
        }
        buffer.rewind();

        // Tạo tensor và chạy mô hình
        try (OnnxTensor tensor = OnnxTensor.createTensor(env, buffer, tensorShape);
             OrtSession.Result result = session.run(Collections.singletonMap(inputName, tensor))) {

            // Lấy output (thường node 0)
            Object raw = result.get(0).getValue();
            float[][] output2d;

            if (raw instanceof float[][]) {
                output2d = (float[][]) raw;
            } else if (raw instanceof float[]) {
                float[] flat = (float[]) raw;
                output2d = new float[1][flat.length];
                System.arraycopy(flat, 0, output2d[0], 0, flat.length);
            } else {
                throw new IllegalStateException("Unexpected model output type: " + raw.getClass());
            }

            Map<String, Float> scores = new HashMap<>();
            for (int i = 0; i < labels.size() && i < output2d[0].length; i++) {
                scores.put(labels.get(i), output2d[0][i]);
            }

            // Top 3
            List<Map.Entry<String, Float>> top3 = scores.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .limit(3)
                    .toList();

            System.out.println("\nTOP 3 DỰ ĐOÁN:");
            top3.forEach(e -> System.out.printf(" → %s: %.2f%%\n", e.getKey(), e.getValue() * 100));

            Map<String, Object> response = new HashMap<>();
            if (!top3.isEmpty()) {
                response.put("best", top3.get(0).getKey());
                response.put("confidence", String.format("%.2f%%", top3.get(0).getValue() * 100));
                response.put("top3", top3.stream().map(e -> Map.of(
                        "food", e.getKey(),
                        "confidence", String.format("%.2f%%", e.getValue() * 100)
                )).toList());
            } else {
                response.put("best", "");
                response.put("confidence", "0.00%");
                response.put("top3", Collections.emptyList());
            }
            return response;
        } catch (OrtException e) {
            // log / rethrow để controller bắt và trả 500
            System.err.println("OrtException during prediction: " + e.getMessage());
            throw e;
        }
    }

    @PreDestroy
    public synchronized void close() {
        try {
            if (session != null) {
                session.close();
                session = null;
            }
        } catch (OrtException e) {
            System.err.println("Error while closing session: " + e.getMessage());
        }
        // OrtEnvironment không luôn cần đóng nhưng gọi shutdown an toàn
        if (env != null) {
            env.close();
            env = null;
        }
    }
}
