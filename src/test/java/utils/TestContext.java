package utils;

// Class này dùng để lưu tạm infor error khi 1 step bị fail
// de Hooks.tearDown() đọc lại và ghi vào cột Ghi chú của file TestRults.xlsx
// Do đang setup cơ chế chạy song song các test case
// => dùng class ThreadLocal để lưu song song các kết quả
public class TestContext {
    private static final ThreadLocal<String> FAILURE_NOTE = new ThreadLocal<>();

    private TestContext() {}

//    function để lưu kết quả lỗi
    public static void setNote(String note) {
        FAILURE_NOTE.set(note);
    }

    public static String getNote() {
        String note = FAILURE_NOTE.get();
        return note == null ? "" : note;
    }

    public static  void clear() {
        FAILURE_NOTE.remove();
    }
}
