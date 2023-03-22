package lr10.t6_myJSON;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.zip.DataFormatException;

public class MyJSONManager {
    static String getCollectionPath() { return "src/lr10/t6_myJSON/collection.json"; }

    public static void main(String[] args) {
        Object collection = getJSONObject(getCollectionPath());
        int answer = 0;

        do{
            switch(answer) {
                case 1: showCollectionMenu(collection); break;
                case 2: add2CollectionMenu(collection); break;
                case 3: searchInCollectionMenu(collection); break;
                case 4: deleteFromCollection(collection); break;
            }

            answer = mainMenu();
        } while(answer != 0);
    }

    static int mainMenu(){
        System.out.flush();
        System.out.println("-------------------------------------");
        System.out.println("Добро пожаловать в вашу коллекцию игр");
        System.out.println("-------------------------------------");
        System.out.println("Выберите действие:");
        System.out.println("1 - Просмотр коллекции");
        System.out.println("2 - Добавить игру в коллекцию");
        System.out.println("3 - Найти игру на платформу");
        System.out.println("4 - Удалить игру");
        System.out.println("\n0 - Выход");

        int answ = -1;
        boolean isFirst = true;

        Scanner in = new Scanner(System.in);
        do{
            if(isFirst) isFirst = false;
            else System.out.println("Такого пункта нет в меню");

            System.out.print("\n \nВаш ответ: ");

            try {
                String input = in.nextLine();

                if((input.length() - input.replace(",", "").length()) == 1 || (input.length() - input.replace(".", "").length()) == 1)
                    throw new ClassCastException("Необходимо целое число");

                answ = Integer.parseInt(input.trim());

            } catch (ClassCastException e) {
                System.out.println("Ошибка:" + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Не верный формат числа");
            }
        }while(answ < 0 || answ > 4);

        return answ;
    }

    static void showCollectionMenu(Object collection){
        System.out.println("\n-------------------------------------");
        System.out.println("Список игр в коллекции");
        System.out.println("-------------------------------------");
        JSONObject jsonObject = (JSONObject) collection;
        JSONArray jsonArray = (JSONArray) jsonObject.get("collection");

        for(Object o:jsonArray){
            JSONObject game = (JSONObject) o;
            printELement(game);
        }
        waitForEnter();
    }

    static void add2CollectionMenu(Object collection){
        System.out.println("\n-------------------------------------");
        System.out.println("Добавление игры в коллекцию");
        System.out.println("-------------------------------------");

        Scanner in = new Scanner(System.in);

        String inputTitle = "";
        String inputPlatform = "";
        int inputYear = 0;

        boolean valid;
        do {
            valid = true;
            System.out.print("\nВведите название игры: ");
            try {
                inputTitle = in.nextLine();
                if(inputTitle.isEmpty()) throw new DataFormatException();
            }catch(DataFormatException e){
                System.out.println("Ошибка! Название не должно быть пустым!");
                valid = false;
            }

            if(valid){
                List<Object> game = findInCollection(collection, "title", inputTitle);
                if(game.size() > 0){
                    System.out.println("Ошибка! Игра с таким названием уже есть в коллекции!");
                    valid = false;
                }
            }
        }while(!valid);

        do {
            valid = true;
            System.out.print("\nВведите платформу: ");
            try {
                inputPlatform = in.nextLine();
                if(inputPlatform.isEmpty()) throw new DataFormatException();
            }catch(DataFormatException e){
                System.out.println("Ошибка! Платформа не должна быть пустой!");
                valid = false;
            }
        }while(!valid);


        Date curdate = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(curdate);
        int year = calendar.get(Calendar.YEAR);

        do {
            valid = true;
            System.out.print("\nВведите год выхода игры: ");
            try {
                inputYear = in.nextInt();
            }catch (InputMismatchException e){
                System.out.println("Ошибка! Введите год цифрами!");
                valid = false;
            }
            finally {
                in.nextLine();
            }

            if(valid && (inputYear < 1950 || inputYear > year)){
                System.out.println("Ошибка! Введите год в промежутке между 1950 и " + year + " годами!");
                valid = false;
            }
        }while(!valid);

        if(addInCollection(collection, inputTitle, inputPlatform, inputYear)) {
            System.out.println("\nСледующая игра была успешно добавлена:");
            List<Object> game = findInCollection(collection, "title", inputTitle);
            if(game.size() == 1){
                JSONObject g = (JSONObject) game.get(0);
                printELement(g);
            }
        }else{
            System.out.println("\nОшибка. Игра не добавлена.");
        }
        waitForEnter();
    }

    static void searchInCollectionMenu(Object collection) {
        Scanner in = new Scanner(System.in);

        System.out.println("\n-------------------------------------");
        System.out.println("Поиск игр на платформу");
        System.out.println("-------------------------------------");
        System.out.print("\nВведите название платформы: ");
        String searchValue = in.nextLine();

        // Ищем
        List<Object> foundElements = findInCollection(collection, "platform", searchValue);

        if (foundElements.size() == 0)
        {
            System.out.println("\nВ коллекции ничего не найдено");
        }else{
            System.out.println("\nВ коллекции найдено игр - " + foundElements.size() + " :");
            for(int i=0;i<foundElements.size();i++){
                JSONObject game = (JSONObject) foundElements.get(i);
                printELement(game);
            }
        }
        waitForEnter();
    }

    static void deleteFromCollection(Object collection){
        System.out.println("\n-------------------------------------");
        System.out.println("Удаление игры из коллекции");
        System.out.println("-------------------------------------");

        Scanner in = new Scanner(System.in);

        String inputTitle = "";

        List<Object> game = new ArrayList<>();
        boolean valid;
        do {
            valid = true;
            System.out.print("\nВведите название удаляемой игры (пустое значение - выход): ");
            inputTitle = in.nextLine();

            if(inputTitle.isEmpty()) return;

            game = findInCollection(collection, "title", inputTitle);
            if(game.size() == 0){
                System.out.println("Ошибка! Игры с таким названием нет в коллекции!");
                valid = false;
            }
        }while(!valid);

        JSONObject gameItem = (JSONObject) game.get(0);
        System.out.println("\nНайдена следующая игра: ");
        printELement(gameItem);

        System.out.println("\nВы уверены, что хотите её удалить из коллекции?");
        System.out.println("1 - Да");
        System.out.println("0 - Отмена");

        System.out.print("\nВаш ответ: ");
        String answer = in.nextLine();

        if(answer.equals("1")){
            JSONObject jsonObject = (JSONObject) collection;
            JSONArray jsonArray = (JSONArray) jsonObject.get("collection");

            Iterator iterator = jsonArray.iterator();
            while (iterator.hasNext()) {
                JSONObject book = (JSONObject) iterator.next();
                if (inputTitle.equals(book.get("title"))) {
                    iterator.remove();
                }}

            saveFile(collection);

            System.out.println("\nИгра успешно удалена!");
            waitForEnter();
        }
    }

    static void printELement(JSONObject game){
        String gtitle = game.get("title").toString();
        String gplatform = game.get("platform").toString();
        int gyear = Integer.parseInt(game.get("year").toString());

        System.out.println("* " + gtitle + " (" + gplatform + ", " + gyear + ")");
    }

    static void waitForEnter() {
        System.out.println("\n\nНажмите [Enter] чтобы вернуться в меню...");
        Scanner in = new Scanner(System.in);
        in.nextLine();
    }

    static Object getJSONObject(String filePath){
        Object ret = null;

        try{
            JSONParser parser = new JSONParser();
            ret = parser.parse(new FileReader(filePath));
        }catch (Exception e){
            e.printStackTrace();
        }

        return ret;
    }

    static List<Object> findInCollection(Object collection, String searchParam, String searchValue){
        List<Object> retList = new ArrayList();

        JSONObject jsonObject = (JSONObject) collection;
        JSONArray jsonArray = (JSONArray) jsonObject.get("collection");

        for(Object o:jsonArray){
            JSONObject game = (JSONObject) o;

            String text = game.get(searchParam).toString();
            if(searchValue.equals(text)) retList.add(game);
        }

        return retList;
    }

    static boolean addInCollection(Object collection, String title, String platform, int year) {
        boolean ret = true;

        JSONObject jsonObject = (JSONObject) collection;
        JSONArray jsonArray = (JSONArray) jsonObject.get("collection");
        try {
            // Добавление игры
            JSONObject newBook = new JSONObject();
            newBook.put("title", title);
            newBook.put("platform", platform);
            newBook.put("year", year);
            jsonArray.add(newBook);

            saveFile(collection);
        } catch (Exception e){
            e.printStackTrace();
            ret = false;
        }

        return ret;
    }

    public static void saveFile(Object collection){
        try {
            JSONObject jsonObject = (JSONObject) collection;
            FileWriter file = new FileWriter(getCollectionPath());
            file.write(jsonObject.toJSONString());
            file.flush();
            file.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
