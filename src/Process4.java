//각 process는 생성자를 통해 오늘 날짜(todayDate)를 받아온다.
//todayDate는 사용자가 입력한 날짜를 의미하며, 각 년도, 월, 일은 공백으로 구분 (EX. 2023 02 01)
//모든 Process는 BookDAO를 통해 파일에서 데이터를 읽어오고, 데이터를 파일에 쓴다.
//BookDAO의 getDataFromFiles() 메소드는 bookList.txt 파일에서 데이터를 읽어와서 ArrayList<BookVO> 형태로 반환한다.
//ArrayList<BookVO>를 통해서 각 도서에 접근하고, 도서 정보를 수정 할 수 있다.
//각 Process는 특정 작업을 마치면, BookDAO의 writeDataToFiles(ArrayList<BookVO> bookList) 메소드를 호출하여, 변경된 데이터를 txt파일에 기록한다.

import java.time.LocalDate;
import java.util.*;

public class Process4 {
    ArrayList<BookVO> bookList;
    Scanner scanner;
    BookDAO bookDAO;
    String todayDate;
    BookVO borrowed;

    public Process4(String todayDate) {
        boolean goPrevious = true;
        this.scanner = new Scanner(System.in);
        this.bookDAO = new BookDAO();
        this.todayDate = todayDate;
        this.bookList = bookDAO.getDataFromFiles();

        while(goPrevious){
            System.out.println("> 대여 서비스");
            System.out.println("1) Borrow Books (도서 대여)");
            System.out.println("2) Return Books (도서 반납)");
            System.out.println("3) Record (대출 기록 보기)");
            System.out.println("4) Previous (뒤로가기)");
            System.out.println("------------------------------------------------------------");
            System.out.print("> 대여 서비스 > ");

            String input = scanner.nextLine();
            int nextPrompt = parseForMainPrompt(input);
            switch (nextPrompt){
                case 1:
                    borrowBookPrompt();
                    break;
                case 2:
                    returnBookPrompt();
                    break;
                case 3:
                    printRecord();
                    break;
                case 4:
                    goPrevious = false; //while문 loop 조건 false로 변경
                    break;
                case -1:    //int가 아닌 다른 값
                    System.out.println("잘못 입력했습니다. 범위(1~4) 안에서 다시 선택해주세요");
                    break;
                default:    //1 ~ 4 가 아닌 다른 값
                    System.out.println("잘못 입력했습니다. 범위(1~4) 안에서 다시 선택해주세요");
                    break;
            }
        }
    }

    private void borrowBookPrompt(){

        while(true){
            System.out.println("> A04 LMS");
            System.out.println("검색할 도서의 제목 일부분을 입력하세요.");
            System.out.println("--------------------------------------------------------------------------");
            System.out.print("> A04 LMS: Search by Title > ");
            String input = scanner.nextLine();
            if(searchBorrowBookPrompt(input)){  //존재하지 않는 책이면 false return하여 while 한번더
                break;
            }
            System.out.println("검색 결과가 존재하지 않습니다.");
        }
    }

    private boolean searchBorrowBookPrompt(String paramInput){
        String regex = ".*" + paramInput + ".*";

//===============================================ForTesting===========================
//        System.out.println("bookList");
//        for(BookVO book : bookList)
//            System.out.println(book.toBookFileStringWithoutSno());
//===============================================ForTesting===========================

        ArrayList<BookVO> matchBooks = new ArrayList<>();   //검색 결과를 담을 List

        for(int i = 0; i < bookList.size(); i++){   //정규식으로 일치하는 모든 책을 탐색
            if(bookList.get(i).getTitle().matches(regex)){
                matchBooks.add(bookList.get(i));
            }
        }

        if(matchBooks.isEmpty())    //탐색결과 없음
            return false;

//===============================================ForTesting===========================
//        System.out.println("matchBook");
//        for(BookVO book : matchBooks)
//            System.out.println(book.toBookFileStringWithoutSno());
//===============================================ForTesting===========================
        System.out.println("--------------------------------");

        System.out.println("  제목 / 저자 / 등록 날짜  / 인덱스 / 위치 / 대여기간");
        for(int i = 1; i <= matchBooks.size(); i++){
            System.out.print(i + ") ");
            System.out.println(matchBooks.get(i-1).toBookFileStringWithoutSno());
        }

        int inputNum = -1;

        while(true){
            System.out.println("--------------------------------------------------------------------------");
            System.out.print(">A04 LMS: Select book >");
            String input = scanner.nextLine();
            inputNum = parseForSearchBooks(input);
            if(inputNum > matchBooks.size()){   //목록 외의 숫자
                System.out.println("올바른 형식으로 입력해주세요(1 ~ " + matchBooks.size() + " )");
            }else if (inputNum == -1){      //정수가 아님
                System.out.println("올바른 형식으로 입력해주세요(1 ~ " + matchBooks.size() + " )");
            } else if (matchBooks.get(inputNum - 1).getCurrentRecord() != null) {       //이부분 수정 보고서 필요
                System.out.println("현재 대여 중입니다.");
            } else{  //맞는 입력
                break;
            }
        }

        borrowed = matchBooks.get(inputNum - 1);
        inputUserInfoPrompt(borrowed);
        bookDAO.writeDataToFiles(bookList);
        return true;
    }

    private void inputUserInfoPrompt(BookVO selectedBook){
        String input = "";

        while(true){
            System.out.println("--------------------------------------------------------------------------");
            System.out.print("“학번”을 입력하세요 > ");
            input = scanner.nextLine();
            if(regexForUserInfo(input)){    //학번 검증
                processForBorrow(Integer.parseInt(input), selectedBook);
                System.out.println("대여가 완료되었습니다.");
                System.out.println(todayDate);
                break;
            }
            System.out.println("올바른 형식의 학번을 입력해주세요(숫자 9자리)");
        }
    }

    private void returnBookPrompt(){

        String input = "";

        while(true){
            System.out.println("--------------------------------------------------------------------------");
            System.out.print("“학번”을 입력하세요 > ");
            input = scanner.nextLine();
            if(regexForUserInfo(input)){    //학번 검증
                selectReturnBook(Integer.parseInt(input));
                break;
            }
            System.out.println("올바른 형식의 학번을 입력해주세요(숫자 9자리)");
        }
    }

    private void selectReturnBook(int sno) {
//===============================================ForTesting===========================
//        System.out.println("in selected Book");
//        System.out.println(bookList.size());
//===============================================ForTesting===========================
        ArrayList<BookVO> matchBooks = new ArrayList<>();   //검색 결과를 담을 List

        for(BookVO book : bookList){
//===============================================ForTesting===========================
//            System.out.println("in selected Book");
//            System.out.println(book.getCurrentRecord().getStudentNum());
//===============================================ForTesting===========================
            try{
                if(Integer.parseInt(book.getCurrentRecord().getStudentNum()) == sno)
                    matchBooks.add(book);
            }catch(Exception ignored){

            }
        }

        if(matchBooks.isEmpty()) {    //탐색결과 없음 ====================================================보고서 수정 필
            System.out.println("대여한 책이 없습니다.");
            return;
        }
        System.out.println("in selected Book");

        while(true) {
            System.out.println("> Rental Information");
            System.out.println("  제목 / 저자 / 등록 날짜  / 인덱스 / 위치 / 대여기간");
            for (int i = 1; i <= matchBooks.size(); i++) {
                System.out.print(i + ") ");
                System.out.println(matchBooks.get(i - 1).toBookFileStringWithoutSno());
            }
            System.out.println("--------------------------------------------------------------------------");
            System.out.print("> Select Book > ");
            String input = scanner.nextLine();
            int inputNum = parseForSearchBooks(input);
            if(inputNum > matchBooks.size()){   //목록 외의 숫자
                System.out.println("올바른 형식으로 입력해주세요(1 ~ " + matchBooks.size() + " )");
            }else if (inputNum == -1){      //정수가 아님
                System.out.println("올바른 형식으로 입력해주세요(1 ~ " + matchBooks.size() + " )");
            } else{  //맞는 입력
                processForReturn(matchBooks.get(inputNum - 1));
                System.out.println("반납이 완료되었습니다.");
                System.out.println(todayDate);
                break;
            }
        }
    }

    private void processForReturn(BookVO selected) {
        BookRecord curRecord = selected.getCurrentRecord();
        selected.setCurrentRecord(null);    //curRecord null로 하여 반납처리

        curRecord.setEndDate(todayDate);

        if(selected.getBookRecords() == null){
            selected.setBookRecords(new ArrayList<>());
        }
        selected.getBookRecords().add(curRecord);   //대출 기록 반납일자를 오늘로 하여 기록 추가
        bookDAO.writeDataToFiles(bookList);
    }

    //2024 02 01
    private void processForBorrow(int sno, BookVO selectedBook) {
        //7일후 계산하기-------------------------------------------------------------------
//===============================================ForTesting===========================
//        System.out.println("startDate ===================================");
//        System.out.println(todayDate);
//===============================================ForTesting===========================

        String[] splited = todayDate.split(" ");

//===============================================ForTesting===========================
//        for(String s : splited) {
//            System.out.println(s);
//            System.out.println(Integer.parseInt(s));
//        }
//===============================================ForTesting===========================

        LocalDate startDate = LocalDate.of(Integer.parseInt(splited[0]), Integer.parseInt(splited[1]), Integer.parseInt(splited[2]));

//===============================================ForTesting===========================
//        System.out.println("startDate ===================================");
//        System.out.println(startDate);
//===============================================ForTesting===========================

        LocalDate endLocalDate = startDate.plusDays(7);
        String endDate = endLocalDate.getYear() + " " +  endLocalDate.getMonthValue() + " " + endLocalDate.getDayOfMonth();
        BookRecord record = new BookRecord(todayDate, endDate, new Integer(sno).toString());
        //--------------------------------------------------------------------------------

        selectedBook.setCurrentRecord(record);
    }

    private boolean regexForUserInfo(String input){
        String regexForSno = "^20(([01][0-9])|(2[0-4]))[0-9]{5}$";  //2000 ~ 2024 까지의 년도만 허용
        return input.matches(regexForSno);
    }

    private int parseForSearchBooks(String input){
        try {
            return Integer.parseInt(input);
        } catch (Exception e){
            return -1;
        }
    }

    private int parseForMainPrompt(String input){
        int returnVal;
        try{
            returnVal = Integer.parseInt(input);
            return returnVal;
        }catch (Exception e){
            return -1;
        }
    }

    private void printRecord() {
//        BookDAO bookDAO = new BookDAO(); // 임시 bookDAO - 공동으로 사용하는 bookDAO가 있으면 그것을 가져와야 함 => class bookDAO로 변경
        ArrayList<BookVO> Books = bookDAO.getDataFromFiles();
        if (Books.isEmpty()) {
            System.out.println("검색 결과가 존재하지 않습니다.");
        } else {
            int bookNum = 1;
            System.out.println("  제목/ 저자 / 등록날짜 / 인덱스 / 위치 / 대여기간");
            for (BookVO book : Books) {
                if(book.getCurrentRecord() == null) {
                    System.out.println((bookNum++) + ")" + book.toBookFileString()  + "/대여가능");
                }else{
                    System.out.println((bookNum++) + ")" + book.toBookFileStringWithoutSno());
                }
                if(book.getBookRecords() == null) {
                    System.out.println("이전 대여 기록 없음");
                }
                else {
                    System.out.println("이전 대여 기록");
                    for (BookRecord records : book.getBookRecords()) {
                        System.out.println(records.getStartDate() + " ~ " + records.getEndDate());
                    }
                }
            }
        }
        System.out.println("‘q’를 입력하여 뒤로가기");
        System.out.println("------------------------------------------------------------");
        System.out.print("> A04 LMS: borrrow books > ");
        while(!scanner.nextLine().equals("q")){
            System.out.println("q 외에 다른 입력은 허용되지 않습니다.");
        }
    }
}
