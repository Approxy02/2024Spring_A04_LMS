//각 process는 생성자를 통해 오늘 날짜(todayDate)를 받아온다.
//todayDate는 사용자가 입력한 날짜를 의미하며, 각 년도, 월, 일은 공백으로 구분 (EX. 2023 02 01)
//모든 Process는 BookDAO를 통해 파일에서 데이터를 읽어오고, 데이터를 파일에 쓴다.
//BookDAO의 getDataFromFiles() 메소드는 bookList.txt 파일에서 데이터를 읽어와서 ArrayList<BookVO> 형태로 반환한다.
//ArrayList<BookVO>를 통해서 각 도서에 접근하고, 도서 정보를 수정 할 수 있다.
//각 Process는 특정 작업을 마치면, BookDAO의 writeDataToFiles(ArrayList<BookVO> bookList) 메소드를 호출하여, 변경된 데이터를 txt파일에 기록한다.

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
            input = input.replace(" ", "");
            int nextPrompt = parseForMainPrompt(input);
            switch (nextPrompt){
                case 1:
                    borrowBookPrompt();
                    break;
                case 2:
                    returnBookPrompt();
                    break;
                case 3:
                    printRecordPrompt();
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
        
        if(!checkCanBorrow()) {
            System.out.println("현재 대여가능한 도서가 없습니다.");
            System.out.println("--------------------------------------------------------------------------");
        }else {
            while (true) {
                System.out.println("> A04 LMS");
                System.out.println("검색할 도서의 제목 일부분을 입력하세요.");
                System.out.println("--------------------------------------------------------------------------");
                System.out.print("> A04 LMS: Search by Title > ");
                String input = scanner.nextLine();
                if (searchBorrowBookPrompt(input)) {  //존재하지 않는 책이면 false return하여 while 한번더
                    break;
                }
            }
        }
    }

    private boolean checkCanBorrow() {
        //System.out.println("bookList.size() = "+ bookList.size() );
        if (bookList.isEmpty())
            return false;

        for(BookVO book : bookList){
            try {
//                System.out.println(book.getCurrentRecord().getStudentNum());
                if (book.getCurrentRecord() == null)
                    return true;
            }catch(Exception e){
                System.out.println(e);
            }
        }
        return false;
    }

    private boolean searchBorrowBookPrompt(String paramInput){
        String regex = ".*" + paramInput + ".*";

//===============================================ForTesting===========================
//        System.out.println("bookList");
//        for(BookVO book : bookList)
//            System.out.println(book.toBookFileStringWithoutSno());
//===============================================ForTesting===========================

        ArrayList<BookVO> matchBooks = new ArrayList<>();   //검색 결과를 담을 List
        Boolean isAllBooksBorrowed = true;                  //찾은 모든 책이 대여중인지 확인하기 위한 변수

        for(int i = 0; i < bookList.size(); i++){   //정규식으로 일치하는 모든 책을 탐색
            if(bookList.get(i).getTitle().matches(regex)){
                matchBooks.add(bookList.get(i));
                if(bookList.get(i).getCurrentRecord() == null) //하나라도 대여 가능하다면 isAllBooksBorrowed = false
                    isAllBooksBorrowed = false;
            }
        }

        if(matchBooks.isEmpty()) {  //탐색결과 없음
            System.out.println("검색 결과가 존재하지 않습니다.");
            System.out.println("--------------------------------");
            return false;
        }
        if(isAllBooksBorrowed){     //입력한 제목에 해당하는 모든 책이 대여 중
            System.out.println("해당하는 모든 책이 대여중입니다.");
            System.out.println("--------------------------------");
            return false;
        }

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
        bookDAO.writeDataToFiles(bookList, todayDate);
        return true;
    }

    private void inputUserInfoPrompt(BookVO selectedBook){
        String input = "";

        while(true){
            System.out.println("--------------------------------------------------------------------------");
            System.out.print("“학번”을 입력하세요 > ");
            input = scanner.nextLine();
            if (regexForUserInfo(input)) {    //학번 검증
                if(!checkUserForBorrrow(Integer.parseInt(input))){   //연체나 최대권수면 종료
                    return;
                }
                processForBorrow(Integer.parseInt(input), selectedBook);
                System.out.println("대여가 완료되었습니다.");
//                System.out.println(todayDate);
                break;
            }
            System.out.println("올바른 형식의 학번을 입력해주세요(숫자 9자리)");
        }
    }

    private boolean checkUserForBorrrow(int sno){
        String stringedSno = String.valueOf(sno);
        User user = bookDAO.getUserInfo(stringedSno);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd");
        try {
            LocalDate today = LocalDate.parse(todayDate, formatter);

            if(Objects.isNull(user)){
                user = new User(stringedSno, 0, null);
                bookDAO.writeUserToFile(user);
                return true;
            }

            if(user.getPenaltyDate() != null) {
                String slicedPenaltyDate[] = user.getPenaltyDate().split(" ");
                LocalDate convertedPenaltyDate = LocalDate.of(Integer.parseInt(slicedPenaltyDate[0]), Integer.parseInt(slicedPenaltyDate[1]), Integer.parseInt(slicedPenaltyDate[2]));
                if (today.isBefore(convertedPenaltyDate)) {
                    user.setIsPenalty(1);
                    System.out.println(user.getPenaltyDate() + " 까지 연체 상태입니다. (금일 : " + todayDate + " )");    //연체 도서가 없어도 연체 상태인지
                    return false;
                } else {
                    user.setIsPenalty(0);   //today가 연체기간을 지났으면 연체상태 해제해주기
                    user.setPenaltyDate(null);
                }
            }

            //연체되었는지 검사
            if(user.getIsPenalty() == 0){
                for(BookVO book : user.getCurrentBorrowedBooks()) {  //연체 도서가 존재하는지
                    String dueFormat = book.getCurrentRecord().getEndDate();  //2020-01-01
                    String[] dueFormatList = dueFormat.split(" ");
                    LocalDate due = LocalDate.of(Integer.parseInt(dueFormatList[0]), Integer.parseInt(dueFormatList[1]), Integer.parseInt(dueFormatList[2]));
//                    System.out.println(due);

                    if (due.isBefore(today)) {
                        System.out.println("연체 도서가 존재합니다.");
                        System.out.println("연체 중인 도서 : " + book.getTitle());
                        return false;
                    }
                }
            }

            if(user.getCurrentBorrowedBooks().size() == 3){
                System.out.println("이미 최대 대여 권수를 대여 중입니다. (최대 대여 권수 : 3권)");
                System.out.println("대여 중인 도서");
                for(BookVO book : user.getCurrentBorrowedBooks()){
                    System.out.println(book.getTitle());
                }
                System.out.println("도서 반납 후 대여해주세요.");
                return false;
            }

            bookDAO.writeUserToFile(user);

            return true;
        }catch (Exception e){
            System.out.println(e.toString());
        }
//        LocalDateTime today = LocalDateTime.parse(todayDate, formatter);
        return true;
    }

    private void returnBookPrompt(){

        String input = "";

        while(true){
            System.out.println("--------------------------------------------------------------------------");
            System.out.print("“학번”을 입력하세요 > ");
            input = scanner.nextLine();
            input = input.replace(" ", "");
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
                break;
            }
        }
    }

    private void processForReturn(BookVO selected) {
        BookRecord curRecord = selected.getCurrentRecord();
        String num = curRecord.getStudentNum();
        User user = bookDAO.getUserInfo(num);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd");
        LocalDate today = LocalDate.parse(todayDate, formatter);
        LocalDate end = LocalDate.parse(curRecord.getEndDate(), formatter);
        LocalDate penaltyDate;

        System.out.println("반납이 완료되었습니다.");
        System.out.println(todayDate);

        if(checkOverdue(curRecord.getEndDate())){     //책이 연체됐을 때
            long days = end.until(today, ChronoUnit.DAYS);    //연체 일수
            if(user.getIsPenalty() == 1){       // User가 이미 연체면 원래 penaltydate에서 + days
                LocalDate prevPenaltyDate = LocalDate.parse(user.getPenaltyDate(), formatter);
                penaltyDate = prevPenaltyDate.plusDays(days);
            }else {                             // 연체 상태가 아니면
                penaltyDate = today.plusDays(days);
            }
            String penalty = penaltyDate.format(formatter);
            user.setPenaltyDate(penalty);
            user.setIsPenalty(1);
            System.out.println("반납 도서의 연체로 인해 도서 대여가 제한됩니다.");
            System.out.println("* "+ penalty + " 까지 도서 대여가 제한됩니다.");
        }

        //현재 대여중인 책에서 제거
        ArrayList<BookVO> curBooks = user.getCurrentBorrowedBooks();
        for(int i = 0; i < curBooks.size(); i++){
            BookVO book = curBooks.get(i);
            if(book.getTitle().equals(selected.getTitle())
            && book.getAuthor().equals(selected.getAuthor())
            && book.getIndex() == book.getIndex()){
                curBooks.remove(book);
            }
        }
        user.setCurrentBorrowedBooks(curBooks);

        BookVO tmp_book = new BookVO(selected.getTitle(), selected.getAuthor(), selected.getAddedDate(), selected.getIndex(), selected.getLocation(), selected.getCurrentRecord());

        //대여했던 책에 추가
        user.addPreviousBorrowedBook(tmp_book);

        selected.setCurrentRecord(null);    //curRecord null로 하여 반납처리

        curRecord.setEndDate(todayDate);

        if(selected.getBookRecords() == null){
            selected.setBookRecords(new ArrayList<>());
        }
        selected.getBookRecords().add(curRecord);   //대출 기록 반납일자를 오늘로 하여 기록 추가

        bookDAO.writeDataToFiles(bookList, todayDate);
        bookDAO.writeUserToFile(user);
    }

    private boolean checkOverdue(String endDate){       //연체 확인
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd");
        LocalDate today = LocalDate.parse(todayDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);
        if(today.isAfter(end)){
            return true;
        }
        else{
            return false;
        }
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd");

        LocalDate endLocalDate = startDate.plusDays(7);
        String endDate = endLocalDate.format(formatter);
        BookRecord record = new BookRecord(todayDate, endDate, new Integer(sno).toString());
        //--------------------------------------------------------------------------------

        selectedBook.setCurrentRecord(record);

        User user = bookDAO.getUserInfo(String.valueOf(sno));
//        System.out.println(user.getStudentNum());
        user.addCurrentBorrowedBook(selectedBook);

        bookDAO.writeUserToFile(user);
    }

    private boolean regexForUserInfo(String input){
        String regexForSno = "^20(([01][0-9])|(2[0-4]))[0-9]{5}$";  //2000 ~ 2024 까지의 년도만 허용
        return input.matches(regexForSno);
    }

    private int parseForSearchBooks(String input){
        input = input.replace(" ", "");
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

    private void printRecordPrompt(){
        String input = "";

        System.out.println("--------------------------------------------------------------------------");
        while(true){
            System.out.print("“학번”을 입력하세요 > ");
            input = scanner.nextLine();
            if (regexForUserInfo(input)) {    //학번 검증
                printRecord(Integer.parseInt(input));
                return;
            }
            System.out.println("--------------------------------------------------------------------------");
            System.out.println("올바른 형식의 학번을 입력해주세요(숫자 9자리)");
        }
    }

    private void printRecord(int sno) {
        User user = bookDAO.getUserInfo(Integer.toString(sno));

        System.out.println("--------------------------------------------------------------------------");
        System.out.println("> Rental History");
        if(user == null) {
            System.out.println("대여 중인 책이 없습니다.\n대여한 기록이 없습니다.");
            System.out.println("--------------------------------------------------------------------------");
        } else {
            if(user.getIsPenalty() == 1) { // 연체 상태 확인
                String penalty = user.getPenaltyDate();
                System.out.println("* "+ penalty + " 까지 도서 대여가 제한됩니다.");
                System.out.println();
            }
            ArrayList<BookVO> currentBorrowedBooks = user.getCurrentBorrowedBooks();
            ArrayList<BookVO> previousBorrowedBooks = user.getPreviousBorrowedBooks();
            // 대여 중인 책과 대여 기록 둘 다 비어있을 경우
            if(currentBorrowedBooks.isEmpty() && previousBorrowedBooks.isEmpty()) {
                System.out.println("대여 중인 책이 없습니다.\n대여한 기록이 없습니다.");
                System.out.println("--------------------------------------------------------------------------");
                return;
            }

            if(currentBorrowedBooks.isEmpty()) {
                System.out.println("대여 중인 책이 없습니다.");
            } else {
                System.out.println("대여 중인 책의 목록");
                int bookNum = 1;
                for (BookVO book : currentBorrowedBooks) {
                    System.out.println((bookNum++) + ") " + book.toBookFileStringWithoutSno());
                }
            }
            System.out.println();

            if(previousBorrowedBooks.isEmpty()) {
                System.out.println("대여한 기록이 없습니다.");
            } else {
                System.out.println("대여 기록");
                int bookNum = 1;
                for (BookVO book : previousBorrowedBooks) {
                    System.out.println((bookNum++) + ") " + book.toBookFileStringWithoutSno());
                }
            }
            System.out.println("--------------------------------------------------------------------------");
        }
    }
}
