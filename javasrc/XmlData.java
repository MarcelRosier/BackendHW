package javasrc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class XmlData{

    private String filePath;
    private ArrayList<Person> people = new ArrayList<>();
    /*
     * Since the given data doesn't change (or at least only rarely) this hashmap is
     * used to answer numberPlate queries quicker
     */
    private HashMap<String, Car> numberPlate_to_carDetail;

    public XmlData(String filePath) {
        this.filePath = filePath;
        // parse data
        parseXmlFile();
        buildMap();
        
    }

    /*
     * Associate each car with its numberplate in a Hashmap
     */
    private void buildMap() {
        numberPlate_to_carDetail = new HashMap<>();
        for (Person person : people) {
            for (Car car : person.getCars()) {
                numberPlate_to_carDetail.put(car.getNumberPlate(), car);
            }
        }
    }

    // --------------------------------------------------------------------------------------
    /*
     * Methods for the Rest API
     */
    public String detailsByNumberPlate(String nP) {
        if (numberPlate_to_carDetail.containsKey(nP))
            return numberPlate_to_carDetail.get(nP).getDetails();
        else
            return "numberPlate not found!";

    }

    public String carDetailsByPersonId(int id) {
        StringBuilder sb = new StringBuilder();
        for (Person person : people) {
            if (person.getId() == id) {
                switch (person.getCars().size()) {
                case 0:
                    sb.append("This person doesn't own a car");
                    break;
                case 1:
                    sb.append(person.getCars().get(0).getDetails());
                    break;
                default:
                    sb.append("[ <br>");
                    for (Car car : person.getCars()) {
                        sb.append(car.getDetails() + ",<br>");
                    }
                    // delete the last ','
                    sb.deleteCharAt(sb.length() - 5); // 5 since <br> has length 4
                    sb.append("]");
                }
                return sb.toString();
            }
        }
        return sb.append("No Person matched the queried id").toString();

    }

    public String getPersonsByCarColor(String color) {
        StringBuilder sb = new StringBuilder("[<br>");
        boolean empty = true;
        for (Person person : people) {
            if (person.getCars().stream().anyMatch(car -> (car.getColor().equalsIgnoreCase(color)))) {
                sb.append("\"" + person.getName() + "\"" + ",<br>");
                empty = false;
            }
        }
        // delete the last ','
        if (!empty)
            sb.deleteCharAt(sb.length() - 5); // 5 since <br> has length 4
        return sb.append("]").toString();
    }

    public String getPersonsOlderThan(int age) {
        StringBuilder sb = new StringBuilder("[<br>");
        boolean empty = true;
        for (Person person : people) {
            if (person.getAge() > age) {
                sb.append("\"" + person.getName() + "\"" + ",<br>");
                empty = false;
            }
        }
        // delete the last ','
        if (!empty)
            sb.deleteCharAt(sb.length() - 5); // 5 since <br> has length 4
        return sb.append("]").toString();
    }

    public String getPersonsWithInsurance() {
        StringBuilder sb = new StringBuilder("[<br>");
        boolean empty = true;
        for (Person person : people) {
            if (person.getCars().stream().anyMatch(car -> (car.getInsurance() != null))) {
                sb.append("\"" + person.getName() + "\"" + ",<br>");
                empty = false;
            }
        }
        // delete the last ','
        if (!empty)
            sb.deleteCharAt(sb.length() - 5); // 5 since <br> has length 4
        return sb.append("]").toString();
    }

    // --------------------------------------------------------------------------------------

    /*
     * Read the specified Xml file and parse it into objects/ the people List
     */
    private void parseXmlFile() {
        try {
            File file = new File(filePath);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder;

            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);
            NodeList nl = document.getElementsByTagName("Person");
            for (int i = 0; i < nl.getLength(); i++) {
                Person person = new Person();
                Node node = nl.item(i);
                NodeList children = node.getChildNodes();
                for (int j = 0; j < children.getLength(); j++) {
                    Node child = children.item(j);
                    if (child.getNodeName().equals("id")) {
                        person.setId(Integer.valueOf(child.getTextContent()));
                    } else if (child.getNodeName().equals("Name")) {
                        person.setName(child.getTextContent());
                    } else if (child.getNodeName().equals("Age")) {
                        person.setAge(Integer.valueOf(child.getTextContent()));
                    } else if (child.getNodeName().equals("Country")) {
                        person.setCountry(child.getTextContent());
                    } else if (child.getNodeName().equals("Car")) {
                        person.getCars().add(parseCarNode(child.getChildNodes()));
                    }
                }

                people.add(person);
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Submethod of parseXml to parse a Car node
     */
    private Car parseCarNode(NodeList nl) {
        Car car = new Car();

        for (int i = 0; i < nl.getLength(); i++) {
            Node child = nl.item(i);
            if (child.getNodeName().equals("Color")) {
                car.setColor(child.getTextContent());
            } else if (child.getNodeName().equals("Type")) {
                car.setType(child.getTextContent());
            } else if (child.getNodeName().equals("NumberPlate")) {
                car.setNumberPlate(child.getTextContent());
            } else if (child.getNodeName().equals("Insurance")) {
                car.setInsurance(parseInsurance(child.getChildNodes()));
            }
        }

        return car;
    }

    /*
     * Submethod of parseXml to parse an Insurance node
     */
    private Insurance parseInsurance(NodeList nl) {

        Insurance ins = new Insurance();
        for (int i = 0; i < nl.getLength(); i++) {
            Node child = nl.item(i);
            if (child.getNodeName().equals("Company")) {
                ins.setCompanyName(child.getTextContent());
            } else if (child.getNodeName().equals("Type")) {
                ins.setType(child.getTextContent());
            }
        }

        return ins;
    }

    /*
     * Classes to hold the xml files' data I chose to use private classes since
     * they're quite small and 3 seperate files wouldn't bring any benefit
     */

    private class Person {
        private int id, age;
        private String name, country;
        private List<Car> cars;

        Person() {
            cars = new ArrayList<>();
        }

        @Override
        public String toString() {
            return "Person [age=" + age + ", cars=" + cars + ", country=" + country + ", id=" + id + ", name=" + name
                    + "]";
        }

        /*
         * Getter and Setter
         */

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public List<Car> getCars() {
            return cars;
        }

        public void setCars(List<Car> cars) {
            this.cars = cars;
        }

    }

    private class Car {
        private String color, type, numberPlate;
        private Insurance insurance;

        Car() {
        }

        public String getDetails() {
            return "{ <br> 'color': '" + color + "',<br>'type': '" + type + "',<br>}";
        }

        @Override
        public String toString() {
            return "Car [color=" + color + ", insurance=" + insurance + ", numberPlate=" + numberPlate + ", type="
                    + type + "]";
        }

        /*
         * Getter and Setter
         */

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getNumberPlate() {
            return numberPlate;
        }

        public void setNumberPlate(String numberPlate) {
            this.numberPlate = numberPlate;
        }

        public Insurance getInsurance() {
            return insurance;
        }

        public void setInsurance(Insurance insurance) {
            this.insurance = insurance;
        }

    }

    private class Insurance {
        private String companyName, type;

        Insurance() {
        }

        @Override
        public String toString() {
            return "Insurance [companyName=" + companyName + ", type=" + type + "]";
        }

        /*
         * Getter and Setter
         */

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        };

    }

}