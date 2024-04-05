import java.util.ArrayList;

public class ProcessForTest {
    public ProcessForTest() {
        //test1();
        System.out.println("------------------------------------------------------------");
        test2();

    }

//    public void test1() {
//        BookVO book = new BookVO("코스모스", "칼", "2024 01 01", 1, "베스트셀러실", "2024 04 02", "2024 04 08", "202411011", null);
//        book.printBookInfo();
//        // BookRecord 추가
//        book.addBookRecord(new BookRecord("2024 03 01", "2024 03 02", "202411233"));
//        book.printBookInfo();
//
//    }

    public void test2() {
        BookDAO bookDAO = new BookDAO();
        ArrayList<BookVO> bookList = bookDAO.getDataFromFiles();
        //for debug
        for(BookVO book : bookList){
            book.printBookInfo();
        }
        bookDAO.writeDataToFiles(bookList);
        bookDAO.getDataFromFiles();
        //for debug
        System.out.println("------------------------------------------------------------");
        for(BookVO book : bookList){
            book.printBookInfo();
        }
    }


}
