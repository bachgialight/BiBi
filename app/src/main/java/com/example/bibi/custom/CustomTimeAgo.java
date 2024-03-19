package com.example.bibi.custom;

import java.util.Date;

public class CustomTimeAgo {

    public static String getTimeAgo(String timestamp) {
        // Chuyển đổi timestamp thành đối tượng Date
        Date date = new Date(Long.parseLong(timestamp));

        // Tính thời gian kể từ ngày hiện tại
        long timeAgoMillis = System.currentTimeMillis() - date.getTime();

        // Tính toán và trả về thời gian dưới dạng chuỗi
        return formatTimeAgo(timeAgoMillis);
    }
    private static String formatTimeAgo(long timeAgoMillis) {
        long seconds = timeAgoMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long weeks = days / 7;
        long months = days / 30; // Giả sử một tháng có 30 ngày
        long years = days / 365;
        // thuật toán tính theo năm
        if (years > 0) {
            return years + (years == 1 ? " năm trước" : " năm trước");
        } else if (months > 0) {
            return months + (months == 1 ? " tháng trước" : " tháng trước");
        } else if (weeks > 0) {
            return weeks + (weeks == 1 ? " tuần trước" : " tuần trước");
        } else if (days > 0) {
            return days + (days == 1 ? " ngày trước" : " ngày trước");
        } else if (hours > 0) {
            return hours + (hours == 1 ? " giờ trước" : " giờ trước");
        } else if (minutes > 0) {
            return minutes + (minutes == 1 ? " phút trước" : " phút trước");
        } else {
            return seconds + (seconds == 1 ? " giây trước" : " giây trước");
        }
    }
}
