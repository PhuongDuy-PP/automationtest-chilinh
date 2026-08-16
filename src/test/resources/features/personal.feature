@personal
Feature: Cập nhật ảnh đại diện nhân viên
  Là một người dùng, tôi muốn upload ảnh đại diện cho nhân viên để hồ sơ đầy đủ thông tin

  Background:
    Given người dùng đang ở trang đăng nhập OranageHRM
    When người dùng đăng nhập với tài khoản "Admin" và mật khẩu "admin123"
    And người dùng mở trang cá nhân của nhân viên

  Scenario: Upload ảnh đại diện thành công
    When người dùng upload ảnh đại diện "dog.jpeg"
    Then hệ thống hiển thị thông báo upload ảnh thành công