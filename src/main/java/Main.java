import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json,"new_data1.json");

        String fileName2 = "data.xml";
        List<Employee> list2 = parseXML(fileName2);
        String json2 = listToJson(list2);
        writeString(json2,"new_data2.json");
    }

    private static List<Employee> parseXML(String fileName) {
        List<Employee> list = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));
            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            for (int i = 1; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element element = (Element) node;
                    String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = element.getElementsByTagName("country").item(0).getTextContent();
                    String ageStr = element.getElementsByTagName("age").item(0).getTextContent();
                    int age = Integer.parseInt(ageStr);
                    Employee employee = new Employee(i, firstName, lastName, country, age);
                    list.add(employee);
                }
            }
        }
        catch (SAXException | IOException | ParserConfigurationException ex) {
            System.out.println(ex.getMessage());
        }
        return list;
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (FileReader fileReader = new FileReader(fileName)) {
            try (CSVReader csvReader = new CSVReader(fileReader)) {
                ColumnPositionMappingStrategy<Employee> columnPos = new ColumnPositionMappingStrategy<>();
                columnPos.setType(Employee.class);
                columnPos.setColumnMapping(columnMapping);
                CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(csvReader)
                        .withMappingStrategy(columnPos)
                        .build();
                return csvToBean.parse();
            }
            catch (java.io.IOException ex) {
                System.out.println("Ошибка в создании csvReader: " + ex.getMessage());
                return null;
            }
        }
        catch (java.io.IOException ex) {
            System.out.println("Ошибка в создании fileReader: " + ex.getMessage());
            return null;
        }
    }

    private static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        return gson.toJson(list, listType);
    }

    private static void writeString(String json, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

