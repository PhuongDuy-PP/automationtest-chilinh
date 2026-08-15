  @login
  Feature: Đăng nhập hệ thống OrangeHRM
    Là một người dùng, tôi muốn đăng nhập vào hệ thống để có thể truy cập vào các chức năng quản lý nhân sự

    Background:
#      chạy trước khi bắt đầu chạy test case => truy cập vào page login
      Given  người dùng đang ở trang đăng nhập OranageHRM

    @login_success
    Scenario: Đăng nhập thành công với tài khoản hợp lệ
      When người dùng đăng nhập với tài khoản "Admin" và mật khẩu "admin123"
      Then người dùng được chuyển đến trang Dashboard

    @login_fail
    Scenario: Đăng nhập thất bại với mật khẩu sai
      When người dùng đăng nhập với tài khoản "Admin" và mật khẩu "admin1234"
      Then hệ thống báo lỗi và vẫn ở trang đăng nhập

    @login_data
    Scenario Outline: Đăng nhập với nhiều bộ dữ liệu khác nhau
      When người dùng đăng nhập với tài khoản "<username>" và mật khẩu "<password>"
      Then kết quả đăng nhập phải là "<expected>"

      Examples:
        | username | password | expected |
        | Admin    | admin123 | success  |
        | Admin1   | admin1234| fail     |
        | Admin    | admin123 | fail     |
        | Admin1   | admin1234| fail     |