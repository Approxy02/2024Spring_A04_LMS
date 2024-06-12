import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Process5 {
    static int MAX_LIMIT = 100;
    Scanner scanner;
    private ArrayList<Location> locationlist;
    private BookDAO bookDAO = new BookDAO();
    private String process_input;

    public Process5() {
        scanner = new Scanner(System.in);
        manageLocation();
    }

    public void manageLocation(){
        int menu;
        while (true) {
            locationlist = bookDAO.getLocationInfoList();
            System.out.println("> A04 LMS: manage location");
            System.out.println("1) Add Location (자료실 추가)");
            System.out.println("2) Delete Location (자료실 삭제)");
            System.out.println("3) Change Location Information (자료실 정보 변경)");
            System.out.println("4) Previous (뒤로 가기)");
            System.out.println("------------------------------------------------------------");
            System.out.print("> A04 LMS : manage location > ");

            try {
                process_input = scanner.nextLine();
                if (isValid_MenuInput(process_input, 4)) {
                    menu = Integer.parseInt(process_input);
                    if (menu == 1) {
                        createLocation();
                    }
                    else if (menu == 2) {
                        deleteLocation();
                    }
                    else if (menu == 3) {
                        updateLocation();
                    }
                    else if (menu == 4) {
                        System.out.println("------------------------------------------------------------");
                        return;
                    } else {
                        System.out.println("잘못 입력했습니다. 범위(1~4) 안에서 다시 선택해주세요");
                        System.out.println("------------------------------------------------------------");
                    }
                } else {
                    System.out.println("잘못 입력했습니다. 범위(1~4) 안에서 다시 선택해주세요");
                    System.out.println("------------------------------------------------------------");
                }
            } catch (Exception e) {
                System.out.println("잘못 입력했습니다. 범위(1~4) 안에서 다시 선택해주세요");
                System.out.println("------------------------------------------------------------");
            }
            bookDAO.writeLocationFiles(locationlist);
        }
    }

    private void createLocation(){
        String LocationName;
        int BookStorageLimit;

        while(true){
            LocationName = getLocationName("생성");
            if(!isLocationExist(LocationName)) break;
            else System.out.println("이미 존재하는 자료실 이름입니다.");
        }
        while(true){
            BookStorageLimit = getStorageLimit("생성");
            if(1 <= BookStorageLimit && BookStorageLimit <= MAX_LIMIT) break;
            else System.out.println("1 이상, 100이하인 값을 입력해주세요.");
        }
        Location createdLocation = new Location(LocationName, BookStorageLimit);
        locationlist.add(createdLocation);
        System.out.println("위치가 성공적으로 추가되었습니다.");
    }

    private void deleteLocation(){
        System.out.println("------------------------------------------------------------");
        System.out.print("> A04 LMS: manage location > ");
        printLocations(locationlist);
        Location location = chooseLocation("삭제");
        locationlist.remove(location);
        System.out.println("위치가 성공적으로 삭제되었습니다.");
    }

    private void updateLocation(){
        int menu;
        printLocations(locationlist);
        Location location = chooseLocation("정보를 변경");
        System.out.println("------------------------------------------------------------");
        System.out.println("> 위치 관리 서비스 >");
        System.out.println("1) Change Name (자료실 이름 변경)");
        System.out.println("2) Change Book Storage Limit (도서 수용 한도 변경)");
        String LocationName;
        int BookStorageLimit;
        while(true){
            System.out.print("> 위치 관리 서비스 > ");
            try {
                process_input = scanner.nextLine();
                if (isValid_MenuInput(process_input, 2)) {
                    menu = Integer.parseInt(process_input);
                    if (menu == 1) {
                        while(true){
                            LocationName = getLocationName("정보를 변경");
                            BookStorageLimit = location.getBookStorageLimit();
                            if(!isLocationExist(LocationName) || location.getLocationName().equals(LocationName)) break;
                            else System.out.println("이미 존재하는 자료실 이름입니다.");
                        }
                        System.out.println("자료실 이름이 변경되었습니다.");
                        break;
                    }
                    else if (menu == 2) {
                        while(true){
                            LocationName = location.getLocationName();
                            BookStorageLimit = getStorageLimit("정보를 변경");
                            if(location.getBookStorageLimit() <= BookStorageLimit && BookStorageLimit <= MAX_LIMIT) break;
                            else System.out.println("자료실의 현재 도서 수 이상, 100 이하인 값을 입력해주세요.");
                        }
                        System.out.println("도서 수용 한도가 변경되었습니다.");
                        break;
                    }
                    else {
                        System.out.println("잘못 입력했습니다. 범위(1~2) 안에서 다시 선택해주세요");
                        System.out.println("------------------------------------------------------------");
                    }
                } else {
                    System.out.println("잘못 입력했습니다. 범위(1~2) 안에서 다시 선택해주세요");
                    System.out.println("------------------------------------------------------------");
                }
            } catch (Exception e) {
                System.out.println("잘못 입력했습니다. 범위(1~2) 안에서 다시 선택해주세요");
                System.out.println("------------------------------------------------------------");
            }
        }
        location.setLocationName(LocationName);
        location.setBookStorageLimit(BookStorageLimit);
    }

    private String getLocationName(String usage) {
        String locationName;
        while(true) {
            System.out.println("------------------------------------------------------------");
            if(usage.equals("정보를 변경")){
                System.out.print("> 위치 관리 서비스 > Create New Name > ");
            }else{
                System.out.print(usage);
                System.out.print("할 자료실 이름을 입력하세요 > ");
            }
            locationName = scanner.nextLine();
            String tmpLocationName = locationName;
            locationName = locationName.trim();
            if(!tmpLocationName.equals(locationName)){
                continue;
            }
            if(locationName.contains("\t")){
                continue;
            }
            if(locationName.isEmpty()) {
                continue;
            }
            if(!isValid_location(locationName.trim())) {
                continue;
            }
            else {
                return locationName;
            }
        }
    }
    private int getStorageLimit(String usage) {
        String inputLimit;
        while(true) {
            System.out.println("------------------------------------------------------------");
            if(usage.equals("정보를 변경")){
                System.out.print("> 위치 관리 서비스 > Change Book Storage Limit > ");
            }else{
                System.out.print(usage);
                System.out.print("할 자료실의 최대 도서 수를 입력하세요 > ");
            }
            inputLimit = scanner.nextLine();
            inputLimit = inputLimit.trim();
            if(!isValidToInteger(inputLimit)){
                continue;
            }
            else {
                return Integer.parseInt(inputLimit);
            }
        }
    }

    private boolean isLocationExist(String LocationName){
        boolean alreadyExist = false;
        for (Location location : locationlist) {
            if(LocationName.equals(location.getLocationName())){
                alreadyExist = true;
                break;
            }
        }
        return alreadyExist;
    }

    private Location chooseLocation(String usage) {
        int menu;
        while (true) {
            System.out.println("------------------------------------------------------------");
            System.out.print(usage);
            System.out.print("할 자료실의 \"위치\"를 입력하세요 > ");
            try {
                process_input = scanner.nextLine();
                if (isValid_MenuInput(process_input, locationlist.size())) {
                    menu = Integer.parseInt(process_input);
                    return locationlist.get(menu - 1);
                } else {
                    System.out.println("목록에 해당하는 위치를 선택해 주세요.");
                    printLocations(locationlist);
                }
            } catch (Exception e) {
                System.out.println("입력 값이 올바르지 않습니다.");
                printLocations(locationlist);
            }
        }
    }

    private boolean isValid_MenuInput(String input, int n) {
        String e = input.trim();
        if(!isValidToInteger(e)) return false;
        int analysis = Integer.parseInt(e);
        return analysis >= 1 && analysis <= n;
    }

    private boolean isValid_location(String location) {
        // 알파벳, 한글, 공백, 숫자만 허용하는 정규표현식
        String regex = "^[a-zA-Z가-힣0-9 ]+$";
        return location.matches(regex);
    }

    private boolean isValidToInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void printLocations(ArrayList<Location> locationlist){
        int index = 1;
        System.out.println("------------------------------------------------------------");
        System.out.println("> A04 LMS: manage location");
        System.out.println("현재 자료실 정보");
        for (Location location : locationlist) {
            System.out.println((index++) + ") " + location.getLocationName());
        }
    }
}
