//각 process는 생성자를 통해 오늘 날짜(todayDate)를 받아온다.
//todayDate는 사용자가 입력한 날짜를 의미하며, 각 년도, 월, 일은 공백으로 구분 (EX. 2023 02 01)
//모든 Process는 BookDAO를 통해 파일에서 데이터를 읽어오고, 데이터를 파일에 쓴다.
//BookDAO의 getDataFromFiles() 메소드는 bookList.txt 파일에서 데이터를 읽어와서 ArrayList<BookVO> 형태로 반환한다.
//ArrayList<BookVO>를 통해서 각 도서에 접근하고, 도서 정보를 수정 할 수 있다.
//각 Process는 특정 작업을 마치면, BookDAO의 writeDataToFiles(ArrayList<BookVO> bookList) 메소드를 호출하여, 변경된 데이터를 txt파일에 기록한다.

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Process3 {     //도서 관리 기능
    Scanner scanner;
    private String todayDate;
    private ArrayList<BookVO> booklist;
    private List<BookVO> filteredBooks;
    private BookDAO bookDAO = new BookDAO();
    private ArrayList<BookVO> books;
    private String process_input;

    public Process3(String todayDate) {
        this.todayDate = todayDate;
        booklist = bookDAO.getDataFromFiles();
        manageBook();
        bookDAO.writeDataToFiles(booklist, todayDate);
    }

    private void manageBook() {
        if(booklist.isEmpty()){
            System.out.println("등록된 도서가 없습니다.");
            return;
        }
        BookVO book;    //관리할 도서
        searchByTitle();
        book = chooseBook();
        manageMenu(book);
    }

    private void manageMenu(BookVO book) {     //도서 선택 후 메뉴
        int menu;
        while (true) {
            System.out.println("> A04 LMS: manage book");
            System.out.println("1) Delete Book (도서 삭제)");
            System.out.println("2) Book Name Change (도서 제목 변경)");
            System.out.println("3) Book Author Change (도서 저자 변경)");
            System.out.println("4) Book Location Change (도서 위치 변경)");
            System.out.println("5) Previous (뒤로 가기)");
            System.out.println("------------------------------------------------------------");
            System.out.print("> A04 LMS : manage books > ");
            scanner = new Scanner(System.in);

            try {
                process_input = scanner.nextLine();
                if (Integer.parseInt(process_input) != 5)
                    System.out.println("------------------------------------------------------------");
                if (isValid_MenuInput(process_input, 5)) {
                    menu = Integer.parseInt(process_input);
                    if (menu == 1) {
                        deleteBook(book);
                        return;
                    }
                    else if (menu == 2) {
                        changeTitle(book);
                        return;
                    }
                    else if (menu == 3) {
                        changeAuthor(book);
                        return;
                    }
                    else if (menu == 4) {
                        changeLocation(book);
                        return;
                    }

                    if (menu == 5) {
                        return;
                    } else {
                        System.out.println("잘못 입력했습니다. 범위(1~5) 안에서 다시 선택해주세요1");
                        System.out.println("------------------------------------------------------------");
                    }
                } else {
                    System.out.println("잘못 입력했습니다. 범위(1~5) 안에서 다시 선택해주세요2");
                    System.out.println("------------------------------------------------------------");
                }
            } catch (Exception e) {
                System.out.println("잘못 입력했습니다. 범위(1~5) 안에서 다시 선택해주세요3");
                System.out.println("------------------------------------------------------------");

            }

        }
    }

    private void changeLocation(BookVO book) {
        System.out.println("현재 위치 : " + book.getLocation());
        System.out.println("1) 어린이 도서실");
        System.out.println("2) 제1문헌실");
        System.out.println("3) 제2문헌실");
        System.out.println("4) 베스트셀러실");
        System.out.println("변경할 위치를 입력하세요.");
        int menu;
        while (true) {
            System.out.println("------------------------------------------------------------");
            System.out.print("> A04 LMS: manage book >");
            scanner = new Scanner(System.in);

            try {
                process_input = scanner.nextLine();
                if (isValid_MenuInput(process_input, 4)) {
                    menu = Integer.parseInt(process_input);

                    if (menu == 1) {
                        book.setLocation("어린이 도서실");
                        System.out.println("위치가 성공적으로 변경되었습니다.");
                        return;
                    }
                    else if (menu == 2) {
                        book.setLocation("제1문헌실");
                        System.out.println("위치가 성공적으로 변경되었습니다.");
                        return;
                    }
                    else if (menu == 3) {
                        book.setLocation("제2문헌실");
                        System.out.println("위치가 성공적으로 변경되었습니다.");
                        return;
                    }
                    else if (menu == 4) {
                        book.setLocation("베스트셀러실");
                        System.out.println("위치가 성공적으로 변경되었습니다.");
                        return;
                    }
                    else {
                        continue;
                    }
                } else {
                    continue;
                }
            } catch (Exception e) {
                continue;
            }
        }
    }

    private void changeAuthor(BookVO book) {
        int menu;
        while (true) {
            System.out.println("------------------------------------------------------------");
            System.out.println("> A04 LMS");
            System.out.println("현재 저자 : " + book.getAuthor());
            System.out.println("변경할 저자를 입력하세요.");
            System.out.println("------------------------------------------------------------");
            System.out.print("> A04 LMS: manage book > ");
            scanner = new Scanner(System.in);

            process_input = scanner.nextLine();
            process_input = process_input.trim();
            if(isValid_ProcessInput(process_input)){
                int index = getIndex(book.getTitle(), process_input);
                book.setAuthor(process_input);
                book.setIndex(index);
                System.out.println("저자가 성공적으로 변경되었습니다.");
                return;
            }else {
                continue;
            }
        }
    }

    private void changeTitle(BookVO book) {
        int menu;
        while (true) {
            System.out.println("------------------------------------------------------------");
            System.out.println("> A04 LMS");
            System.out.println("현재 제목 : " + book.getTitle());
            System.out.println("변경할 제목을 입력하세요.");
            System.out.println("------------------------------------------------------------");
            System.out.print("> A04 LMS: manage book > ");
            scanner = new Scanner(System.in);

            process_input = scanner.nextLine();
            process_input = process_input.trim();
            if(isValid_ProcessInput(process_input)){
                int index = getIndex(process_input, book.getAuthor());
                book.setTitle(process_input);
                book.setIndex(index);
                System.out.println("제목이 성공적으로 변경되었습니다.");
                return;
            }else {
                continue;
            }
        }
    }

    private void deleteBook(BookVO book) {
        int menu;
        while (true) {
            System.out.println("------------------------------------------------------------");
            System.out.println("> A04 LMS");
            System.out.println("현재 도서를 삭제하시겠습니까?");
            System.out.println("1) 예");
            System.out.println("2) 아니오");
            System.out.println("------------------------------------------------------------");
            System.out.print("> A04 LMS: manage books > ");
            scanner = new Scanner(System.in);

            try {
                process_input = scanner.nextLine();
                if (isValid_MenuInput(process_input, 2)) {
                    menu = Integer.parseInt(process_input);
                    if(menu == 1){
                        booklist.remove(book);
                        System.out.println("도서가 성공적으로 삭제되었습니다.");
                        System.out.println("------------------------------------------------------------");
                        return;
                    } else {
                        System.out.println("취소되었습니다.");
                        System.out.println("------------------------------------------------------------");
                        return;
                    }
                } else {
                    System.out.println("잘못 입력했습니다. 범위(1~2) 안에서 다시 선택해주세요.");
                }
            } catch (Exception e) {
                System.out.println("잘못 입력했습니다. 범위(1~2) 안에서 다시 선택해주세요.");
            }
        }
    }

    private void searchByTitle() {   //관리할 도서 제목으로 검색
        while(true){
            System.out.println("> A04 LMS");
            System.out.println("관리할 도서의 제목 일부분을 입력 하세요.");
            System.out.println("------------------------------------------------------------");
            System.out.print("> A04 LMS: manage books > ");
            scanner = new Scanner(System.in);
            process_input = scanner.nextLine();

            if(!isValid_ProcessInput(process_input)) {
                System.out.println("검색 결과가 존재하지 않습니다.");
            } else {
                filteredBooks = booklist.stream()
                        .filter(book -> book.getTitle().contains(process_input))
                        .toList();

                if (filteredBooks.isEmpty()) {
                    System.out.println("검색 결과가 존재하지 않습니다.");
                } else {
                    printBooks(filteredBooks);
                    break;
                }
            }
        }
    }

    private BookVO chooseBook() {     //관리할 도서 제목으로 검색 후 선택
        int menu;
        while (true) {
            System.out.println("------------------------------------------------------------");
            System.out.print("> A04 LMS: manage books > ");
            scanner = new Scanner(System.in);

            try {
                process_input = scanner.nextLine();
                if (isValid_MenuInput(process_input, filteredBooks.size())) {
                    menu = Integer.parseInt(process_input);
                    return filteredBooks.get(menu-1);
                } else {
                    System.out.println("목록에 있는 도서를 선택해 주세요.");
                    printBooks(filteredBooks);
                }
            } catch (Exception e) {
                System.out.println("입력 값은 양의 정수입니다.");
                printBooks(filteredBooks);
            }
        }
    }
    private void printBooks(List<BookVO> filteredBooks) {
        int bookNum = 1;
        System.out.println("  제목/ 저자 / 등록날짜 / 인덱스 / 위치 / 대여기간");
        for (BookVO book : filteredBooks) {
            //System.out.println((bookNum++) + ")" + book.toBookFileString());
            if(book.getCurrentRecord() == null) {
                System.out.println((bookNum++) + ")" + book.toBookFileString() + "/대여가능");
            }else{
                System.out.println((bookNum++) + ")" + book.toBookFileStringWithoutSno());
            }
        }
    }
    private int getIndex(String title, String author){      //같은 책 중 가장 큰 인덱스에 +1해서 인덱스 반환
        int idx;
        int max = 0;
        for(int i=0; i < booklist.size(); i++){
            if(title.equals(booklist.get(i).getTitle())
                    && author.equals(booklist.get(i).getAuthor())){
                if(max < booklist.get(i).getIndex()){
                    max = booklist.get(i).getIndex();
                }
            }
        }
        idx = max + 1;
        return idx;
    }

    private boolean isValid_MenuInput(String e, int n) {
        if (e.length() != 1) {
            e = e.trim();
            int analysis = Integer.parseInt(e);
            if (e.length() != 1) {
                return false;
            }
            else {
                if (analysis < 1 || analysis > n) {
                    return false;
                }
                else
                    this.process_input = Integer.toString(analysis);
                return true;
            }
        }
        int analysis = Integer.parseInt(e);
        return analysis >= 1 && analysis <= n;
    }
    private boolean isValid_ProcessInput(String e) {
        return !e.trim().isEmpty();
    }
}
