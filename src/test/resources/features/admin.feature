@admin
Feature: Quản lý người dùng hệ thống (Admin account)
  Là một Admin account, tôi muốn lọc danh sách người dùng theo username và role để quản lý tài khoản hệ thống

  Background:
    Given người dùng đã đăng nhập thành công vào hệ thống với tài khoản "Admin"
    And người dùng mở trang Admin - System Users

  Scenario: Lọc người dùng theo username và role
    When người dùng lọc theo username "Admin" và role "Admin"
    Then số lượng bản ghi hiển thị phải khớp với số dòng dữ liệu