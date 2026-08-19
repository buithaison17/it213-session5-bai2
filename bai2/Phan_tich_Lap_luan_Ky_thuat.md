# Phân tích & Lập luận kỹ thuật: Giải pháp Tiêm biến thời gian (Time Injection) cho AI Agent

## 1. Vấn đề: "Bẫy" thời gian tương đối
Hiện tại, mô hình AI (LLM) hoạt động trong một môi trường cô lập, không có nhận thức về thời gian thực (real-time awareness). Khi khách hàng nhập các từ khóa tương đối như "ngày mai" hoặc "3 ngày tới", AI không có mốc tham chiếu (reference point) để thực hiện các phép tính toán học (arithmetic operations) nhằm chuyển đổi chúng thành các giá trị tuyệt đối (ví dụ: `yyyy-MM-dd`).

Khi AI trực tiếp truyền các chuỗi như "ngày mai" vào tham số của `Tool`, hệ thống backend (Java) sẽ cố gắng thực hiện `LocalDate.parse("ngày mai")`, dẫn đến lỗi `DateTimeParseException` và làm sập luồng xử lý (HTTP 500).

## 2. Giải pháp: Tiêm biến thời gian động (Dynamic Time Injection)
Giải pháp tối ưu là cung cấp "dòng thời gian" cho AI ngay trong ngữ cảnh hệ thống (System Prompt) trước khi nó xử lý câu hỏi của người dùng. Bằng cách sử dụng `LocalDate.now()` tại thời điểm request được gửi đến Controller, chúng ta cung cấp cho AI một mốc "tọa độ 0" chính xác.

### Tại sao giải pháp này loại bỏ được lỗi crash:
1. **Chuyển đổi trách nhiệm tính toán:** AI được hướng dẫn cụ thể để thực hiện việc tính toán (ví dụ: `Today + 1 day`) thay vì truyền chuỗi thô.
2. **Tuân thủ định dạng (Strict Formatting):** Với chỉ thị chặt chẽ trong Prompt, AI sẽ chỉ được phép xuất ra chuỗi định dạng `yyyy-MM-dd` mà backend đã quy định trước.
3. **Loại bỏ tính bất định (Determinism):** Khi có mốc thời gian rõ ràng, kết quả đầu ra của AI trở nên nhất quán và nằm trong tập dữ liệu hợp lệ (`valid set`) mà hàm parse của backend có thể xử lý thành công.
4. **Phòng thủ từ xa (Defensive Programming):** Bằng cách "khóa" định dạng đầu ra ngay từ bước Prompt, chúng ta giảm thiểu rủi ro phải xử lý ngoại lệ ở phía server, từ đó đảm bảo luồng API luôn ổn định.

## 3. Kết luận
Việc tiêm biến động không chỉ là một kỹ thuật Prompt Engineering, mà là một bước **chuẩn hóa dữ liệu đầu vào cho AI Agent**. Nó biến một mô hình ngôn ngữ "mù thời gian" thành một nhân viên có khả năng làm việc với dữ liệu cấu trúc, từ đó triệt tiêu hoàn toàn căn nguyên gây ra lỗi `DateTimeParseException` tại tầng xử lý backend.
