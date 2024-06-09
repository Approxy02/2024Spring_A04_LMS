// txt파일로 부터 도서 정보를 읽어오고 기록하는 클래스

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

public class BookDAO {

    private File bookListFile; // bookList.txt
    private File booksFolder; // src/dataFiles/books

    public BookDAO() {
        checkDataValidation(); //프로그램 시작시 데이터 유효성 검사
    }

    public void checkDataValidation() {
        String projectPath = System.getProperty("user.dir");

        if (projectPath == null) {
            System.err.println("프로젝트 폴더의 경로를 찾을 수 없습니다.");
            System.exit(1);
        }

        projectPath += "/src";

        //for debug
//        System.out.println("------------------------------------------------------------");
//        System.out.println(projectPath);

        File dataFilesFolder = new File(projectPath, "dataFiles");

        if (!dataFilesFolder.exists()) {
            System.err.println("dataFiles 폴더 없음!");
            System.exit(1);
        }

        booksFolder = new File(dataFilesFolder, "books");
        if (!booksFolder.exists()) {
            System.err.println("books 폴더 없음!");
            System.exit(1);
        }

        bookListFile = new File(dataFilesFolder, "bookList.txt");
        if (!bookListFile.exists()) {
            System.err.println("bookList.txt 없음!");
            System.exit(1);
        }

        getDataFromFiles();
    }

    public String getLastDate() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(bookListFile));
            String line = reader.readLine();
            reader.close();
            if (line != null)
                return line;
            else return "1902 01 01";
        } catch (IOException e) {
            System.err.println("파일 읽기 오류: " + e.getMessage());
            System.exit(1);
        }
        return "null";
    }

    public ArrayList<BookVO> getDataFromFiles() {
        ArrayList<BookVO> bookList = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(bookListFile));
            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null) {
                if (lineCount == 0) {
                    lineCount++;
                    continue;
                }
                readBookListFile(line, bookList);
            }

            reader.close();
        } catch (IOException e) {
            System.err.println("파일 읽기 오류: " + e.getMessage());
            System.exit(1);
        } catch (NumberFormatException e) {
            System.err.println("숫자 형식 변환 오류: " + e.getMessage());
            System.exit(1);
        }

        //각 도서들의 이전 대여 기록 가져오기

        for (BookVO book : bookList) {
            String filename = book.getTitle() + "(" + book.getAuthor() + "_" + book.getIndex() + ").txt";

            //for debug
//            System.out.println(filename);

            File bookFile = new File(booksFolder, filename);
            if (!bookFile.exists()) {
                System.err.println(filename + " 없음!");
                System.exit(1);
            }

            try {
                BufferedReader reader = new BufferedReader(new FileReader(bookFile));
                String line;
                int count = 1;
                while ((line = reader.readLine()) != null) {
                    //for debug
//                    System.out.println(line);
                    if (count == 1) {
                        readAndCheckFirstLineOfBookFile(line, book, filename, count);
                    } else {
                        readRecordFromBookFile(line, book, filename, count);
                    }
                    count++;

                }

                reader.close();
            } catch (IOException e) {
                System.err.println("파일 읽기 오류: " + e.getMessage());
                System.exit(1);
            } catch (NumberFormatException e) {
                System.err.println("숫자 형식 변환 오류: " + e.getMessage());
                System.exit(1);
            }
        }

        return bookList;
    }

    private static void readRecordFromBookFile(String line, BookVO book, String fileName, int count) {
        try {
            String[] parts = line.split("/");
            String[] parts2 = parts[0].split("~");
            if (parts.length == 2) {
                BookRecord record = new BookRecord(parts2[0].trim(), parts2[1].trim(), parts[1]);
                book.addBookRecord(record);
            } else {
                System.err.println("잘못된 형식의 데이터입니다\n" + fileName + "\n" + line + "\nin Line " + count);
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("잘못된 형식의 데이터입니다\n" + fileName + "\n" + line + "\nin Line " + count);
            System.exit(1);
        }
    }

    private static void readAndCheckFirstLineOfBookFile(String line, BookVO book, String fileName, int count) {
        try {
            String[] parts = line.split("/");
            if (parts.length == 5) {
                if (!book.getTitle().equals(parts[0].trim())
                        || !book.getAuthor().equals(parts[1].trim())
                        || !book.getAddedDate().equals(parts[2].trim())
                        || book.getIndex() != Integer.parseInt(parts[3].trim())
                        || !book.getLocation().equals(parts[4].trim())) {
                    System.err.println("잘못된 형식의 데이터입니다\n" + fileName + "\n" + line + "\nin Line " + count);
                    System.exit(1);
                }

            } else if (parts.length == 7) {
                String[] parts2 = parts[5].split("~");
                if (!book.getTitle().equals(parts[0].trim())
                        || !book.getAuthor().equals(parts[1].trim())
                        || !book.getAddedDate().equals(parts[2].trim())
                        || book.getIndex() != Integer.parseInt(parts[3].trim())
                        || !book.getLocation().equals(parts[4].trim())
                        || !book.getCurrentRecord().getStartDate().equals(parts2[0].trim())
                        || !book.getCurrentRecord().getEndDate().equals(parts2[1].trim())
                        || !book.getCurrentRecord().getStudentNum().equals(parts[6].trim())
                ) {
                    System.err.println("잘못된 형식의 데이터입니다\n" + fileName + "\n" + line + "\nin Line " + count);
                    System.exit(1);
                }

            } else {
                System.err.println("잘못된 형식의 데이터입니다\n" + fileName + "\n" + line + "\nin Line " + count);
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("잘못된 형식의 데이터입니다\n" + fileName + "\n" + line + "\nin Line " + count);
            System.exit(1);
        }
    }

    private static void readBookListFile(String line, ArrayList<BookVO> bookList) {
        try {
            String[] parts = line.split("/");
            if (parts.length == 5) {
                String title = parts[0].trim();
                String author = parts[1].trim();
                String addedDate = parts[2].trim();
                int index = Integer.parseInt(parts[3].trim());
                String location = parts[4].trim();

                BookVO book = new BookVO(title, author, addedDate, index, location);
                bookList.add(book);
            } else if (parts.length == 7) {
                String title = parts[0].trim();
                String author = parts[1].trim();
                String addedDate = parts[2].trim();
                int index = Integer.parseInt(parts[3].trim());
                String location = parts[4].trim();
                String[] parts2 = parts[5].split("~");
                String startDate = parts2[0].trim();
                String endDate = parts2[1].trim();
                String studentNum = parts[6].trim();
                BookRecord record = new BookRecord(startDate, endDate, studentNum);

                BookVO book = new BookVO(title, author, addedDate, index, location, record);
                bookList.add(book);
            } else {
                System.err.println("잘못된 형식의 데이터입니다: bookList.txt " + line);
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("잘못된 형식의 데이터입니다: bookList.txt " + line);
            System.exit(1);
        }
    }

    public User getUserInfo(String studentNum) {
        String userFilePath = System.getProperty("user.dir");
        userFilePath += "/src/dataFiles/user/" + studentNum + ".txt";
        File userFile = new File(userFilePath);
        if (!userFile.exists()) {
//            System.err.println(studentNum + " 학번의 사용자 정보가 없습니다.");
            return null;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(userFile));
            String line;
            line = reader.readLine();
            String[] parts = line.split("/");

            User user;

            if (parts.length == 3) {
                user = new User(parts[0], Integer.parseInt(parts[1]), parts[2]);
            } else {
                user = new User(parts[0], Integer.parseInt(parts[1]), null);
            }

            while ((line = reader.readLine()) != null) {
                if(line.isEmpty())
                    break;
                String[] currentBooksParts = line.split("/");
                if (currentBooksParts.length == 6) {
                    String[] recordParts = currentBooksParts[5].split("~");
                    if(recordParts.length != 2)
                    {
                        System.err.println("잘못된 형식의 데이터입니다: " + studentNum + ".txt " + line);
                        System.exit(1);
                    }
                    BookRecord currentRecord = new BookRecord(recordParts[0].trim(), recordParts[1].trim(), studentNum);
                    BookVO book = new BookVO(currentBooksParts[0], currentBooksParts[1], currentBooksParts[2], Integer.parseInt(currentBooksParts[3]), currentBooksParts[4], currentRecord);
                    user.addCurrentBorrowedBook(book);
                } else {
                    System.err.println("잘못된 형식의 데이터입니다: " + studentNum + ".txt " + line);
                    System.exit(1);
                }
            }

            while ((line = reader.readLine()) != null){
                String[] pastBooksParts = line.split("/");
                if (pastBooksParts.length == 6) {
                    String[] recordParts = pastBooksParts[5].split("~");
                    if(recordParts.length != 2)
                    {
                        System.err.println("잘못된 형식의 데이터입니다: " + studentNum + ".txt " + line);
                        System.exit(1);
                    }
                    BookRecord pastRecord = new BookRecord(recordParts[0].trim(), recordParts[1].trim(), studentNum);
                    BookVO book = new BookVO(pastBooksParts[0], pastBooksParts[1], pastBooksParts[2], Integer.parseInt(pastBooksParts[3]), pastBooksParts[4], pastRecord);
                    user.addPreviousBorrowedBook(book);
                } else {
                    System.err.println("잘못된 형식의 데이터입니다: " + studentNum + ".txt " + line);
                    System.exit(1);
                }
            }

            reader.close();

            return user;

        } catch (Exception e) {
            System.err.println("파일 읽기 오류: " + e.getMessage());
            System.exit(1);
        }
        return null;
    }

    public void writeUserToFile(User user) {
        String userFilePath = System.getProperty("user.dir");
        userFilePath += "/src/dataFiles/user/" + user.getStudentNum() + ".txt";
        File userFile = new File(userFilePath);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(userFile));
            if(user.getIsPenalty() == 0)
                writer.write(user.getStudentNum() + "/" + user.getIsPenalty());
            else
                writer.write(user.getStudentNum() + "/" + user.getIsPenalty() + "/" + user.getPenaltyDate());
            writer.newLine();
            ArrayList<BookVO> currentBooks = user.getCurrentBorrowedBooks();
            if (currentBooks != null) {
                for (BookVO book : currentBooks) {
                    writer.write(book.toBookFileStringWithoutSno());
                    writer.newLine();
                }
            }
            writer.newLine();
            ArrayList<BookVO> pastBooks = user.getPreviousBorrowedBooks();
            if (pastBooks != null) {
                for (BookVO book : pastBooks) {
                    writer.write(book.toBookFileStringWithoutSno());
                    writer.newLine();
                }
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            System.err.println("파일 쓰기 오류: " + e.getMessage());
            System.exit(1);
        }
    }

    public void writeDataToFiles(ArrayList<BookVO> bookList, String todayDate) {

        if (bookList.isEmpty()) {
            //빈 책 리스트를 받은 경우, booklist.txt에는 날짜만 기입한다.
            //books하위파일들은 모두 삭제한다.
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(bookListFile));
                writer.write(todayDate);
                writer.newLine();
                writer.flush();
                writer.close();
            } catch (IOException e) {
                System.out.println("파일 쓰기 오류:" + e.getMessage());
                System.exit(1);
            }

            File[] existingFiles = booksFolder.listFiles();
            if (existingFiles != null) {
                for (File file : existingFiles) {
                    if (!file.isDirectory()) { // 파일이 디렉토리가 아닌 경우에만 삭제
                        file.delete();
                    }
                }
            }
        } else {
            //빈 리스트가 아닌경우
            File[] existingFiles = booksFolder.listFiles();
            if (existingFiles != null) {
                for (File file : existingFiles) {
                    if (!file.isDirectory()) { // 파일이 디렉토리가 아닌 경우에만 삭제
                        file.delete();
                    }
                }
            }

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(bookListFile));
                writer.write(todayDate);
                writer.newLine(); // 줄바꿈 추가

                for (int i = 0; i < bookList.size(); i++) {
                    BookVO book = bookList.get(i);
                    if (book.getCurrentRecord() == null) {
                        writer.write(book.getTitle() + '/' + book.getAuthor() + '/' + book.getAddedDate() + '/' + book.getIndex() + '/' + book.getLocation());
                    } else {
                        writer.write(book.getTitle() + '/' + book.getAuthor() + '/' + book.getAddedDate() + '/' + book.getIndex() + '/' + book.getLocation() + '/' + book.getCurrentRecord().getStartDate() + " ~ " + book.getCurrentRecord().getEndDate() + "/" + book.getCurrentRecord().getStudentNum());
                    }
                    if (i != bookList.size() - 1) {
                        writer.newLine(); // 마지막 요소가 아닌 경우에만 줄바꿈 추가
                    }
                }
                writer.flush();
                writer.close();
            } catch (IOException e) {
                System.err.println("파일 쓰기 오류: " + e.getMessage());
                System.exit(1);
            }


            for (BookVO book : bookList) {
                String filename = book.getTitle() + "(" + book.getAuthor() + "_" + book.getIndex() + ").txt";

                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(new File(booksFolder, filename)));
                    writer.write(book.toBookFileString()); // BookVO의 toBookFileString() 메서드를 이용하여 책 파일 정보를 문자열로 변환하여 쓴다.
                    if (book.getBookRecords() != null) {
                        for (BookRecord b : book.getBookRecords()) {
                            writer.newLine();
                            writer.write(b.getStartDate() + " ~ " + b.getEndDate() + "/" + b.getStudentNum());
                        }
                    }
                    writer.close();
                } catch (IOException e) {
                    System.err.println("파일 쓰기 오류: " + e.getMessage());
                    System.exit(1);
                }
            }
        }
    }

    public ArrayList<Location> getLocationInfoList() {

        ArrayList<Location> locationList = new ArrayList<>();

        File locationFolder = new File(System.getProperty("user.dir") + "/src/dataFiles/Locations");
        if (!locationFolder.exists()) {
            System.err.println("Locations 폴더 없음!");
            System.exit(1);
        }

        File[] locationFiles = locationFolder.listFiles();
        if (locationFiles == null || locationFiles.length == 0) {
            System.err.println("Locations 폴더에 파일 없음!");
            System.exit(1);
        }
        System.out.println("flag3");
        for (File locationFile : locationFiles) {
            try {
                String fileName = locationFile.getName();
                // Remove the .txt extension
                String locationname = fileName.substring(0, fileName.lastIndexOf(".txt"));

                BufferedReader reader = new BufferedReader(new FileReader(locationFile));
                String line;
                Location loc = null;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("/");
                    if (parts.length == 2) {
                        int bookstoragelimit = Integer.parseInt(parts[0].trim());
                        int currentbooknum = Integer.parseInt(parts[1].trim());
                        loc = new Location(locationname,bookstoragelimit, currentbooknum);
                        locationList.add(loc);
                    } else if (parts.length == 5) {
                        String title = parts[0].trim();
                        String author = parts[1].trim();
                        String addedDate = parts[2].trim();
                        int index = Integer.parseInt(parts[3].trim());
                        String location = parts[4].trim();
                        BookVO book = new BookVO(title, author, addedDate, index, location);
                        assert loc != null;
                        loc.addBookList(book);
                    }else {
                        System.err.println("잘못된 형식의 데이터입니다: " + locationFile.getName() + "\n" + line);
                        System.exit(1);
                    }
                }
                reader.close();
            } catch (IOException e) {
                System.err.println("파일 읽기 오류: " + e.getMessage());
                System.exit(1);
            }
        }

        return locationList;
    }

    public void writeLocationFiles(ArrayList<Location> locationList) {
        String locationDirPath = System.getProperty("user.dir") + "/src/dataFiles/Locations/";

        File locationDir = new File(locationDirPath);
        if (!locationDir.exists()) {
            locationDir.mkdirs();
        } else {
            // 디렉토리가 존재한다면 디렉토리 내 모든 파일 삭제
            File[] files = locationDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!file.delete()) {
                        System.err.println("파일 삭제 오류: " + file.getPath());
                        System.exit(1);
                    }
                }
            }
        }

        for (Location location : locationList) {
            String locationFilePath = locationDirPath + location.getLocationName() + ".txt";
            File locationFile = new File(locationFilePath);

            // 새로운 파일 작성
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(locationFile))) {
                writer.write(location.getBookStorageLimit() + "/" + location.getCurrentBookNum());
                writer.newLine();

                ArrayList<BookVO> bookList = location.getBookList();
                if (bookList != null) {
                    for (BookVO book : bookList) {
                        writer.write(book.getTitle()+"/"+book.getAuthor()+"/"+book.getAddedDate()+"/"+book.getIndex()+"/"+book.getLocation());
                        writer.newLine();
                    }
                }
            } catch (Exception e) {
                System.err.println("파일 쓰기 오류: " + e.getMessage());
                System.exit(1);
            }
        }
    }

}
