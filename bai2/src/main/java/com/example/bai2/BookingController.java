package com.example.bai2;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/booking")
@RequiredArgsConstructor
public class BookingController {
    private final ChatClient.Builder chatClientBuilder;
    private final BookingService bookingService;

    @GetMapping("/check")
    public String checkRoom(@RequestParam String message) {
        // Lấy ngày hiện tại
        String today = LocalDate.now().toString();

        // Tạo System Prompt động
        String systemPrompt = "Bạn là trợ lý đặt phòng khách sạn. " +
                "Ngày hiện tại là " + today + ". " +
                "Hãy quy đổi mọi thời gian tương đối sang định dạng yyyy-MM-dd trước khi gọi tool.";

        return this.chatClientBuilder
                .defaultSystem(systemPrompt)
                .defaultTools(bookingService)
                .build()
                .prompt()
                .user(message)
                .call()
                .content();
    }
}