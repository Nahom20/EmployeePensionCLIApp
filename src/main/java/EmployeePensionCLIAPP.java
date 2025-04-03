

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EmployeePensionCLIAPP {

    public static void main(String[] args) {
        printAllEmployees();
        System.out.println("_--------------------------------------------------------------");
        printQuarterlyUpcomingEnrollees();
    }

    public static List<Employee> loadEmployees() {
        List<Employee> employees = new ArrayList<>();

        employees.add(
                Employee.builder()
                        .employeeId(1L)
                        .firstName("Daniel")
                        .lastName("Agar")
                        .employmentDate(LocalDate.of(2018, 1, 17))
                        .yearlySalary(105945.50)
                        .pensionPlan(PensionPlan.builder()
                                .planReferenceNumber("EX1089")
                                .enrollmentDate(LocalDate.of(2023, 1, 17))
                                .monthlyContribution(100.00)
                                .build())
                        .build()
        );

        employees.add(
                Employee.builder()
                        .employeeId(2L)
                        .firstName("Benard")
                        .lastName("Shaw")
                        .employmentDate(LocalDate.of(2022, 9, 3))
                        .yearlySalary(197750.00)
                        .build()
        );

        employees.add(
                Employee.builder()
                        .employeeId(3L)
                        .firstName("Carly")
                        .lastName("Agar")
                        .employmentDate(LocalDate.of(2014, 5, 16))
                        .yearlySalary(842000.75)
                        .pensionPlan(PensionPlan.builder()
                                .planReferenceNumber("SM2307")
                                .enrollmentDate(LocalDate.of(2019, 11, 4))
                                .monthlyContribution(1555.50)
                                .build())
                        .build()
        );

        employees.add(
                Employee.builder()
                        .employeeId(4L)
                        .firstName("Wesley")
                        .lastName("Schneider")
                        .employmentDate(LocalDate.of(2022, 7, 21))
                        .yearlySalary(74500.00)
                        .build()
        );

        employees.add(
                Employee.builder()
                        .employeeId(5L)
                        .firstName("Anna")
                        .lastName("Wiltord")
                        .employmentDate(LocalDate.of(2022, 6, 15))
                        .yearlySalary(85750.00)
                        .build()
        );

        employees.add(
                Employee.builder()
                        .employeeId(6L)
                        .firstName("Yosef")
                        .lastName("Tesfalem")
                        .employmentDate(LocalDate.of(2022, 10, 31))
                        .yearlySalary(100000.00)
                        .build()
        );

        return employees;
    }



    public static void printAllEmployees() {
        List<Employee> employees = EmployeePensionCLIAPP.loadEmployees();

        // Sort: Descending yearly salary, then ascending last name
        employees.sort(
                Comparator.comparing(Employee::getYearlySalary).reversed()
                        .thenComparing(Employee::getLastName)
        );

        // Configure ObjectMapper for Java time and pretty JSON
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // 🧠 Handles LocalDate
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Format as yyyy-MM-dd
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print

        try {
            String jsonOutput = mapper.writeValueAsString(employees);
            System.out.println("========== All Employees ==========");
            System.out.println(jsonOutput);
        } catch (JsonProcessingException e) {
            System.err.println("Error serializing employee list to JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void printQuarterlyUpcomingEnrollees() {
        List<Employee> employees = EmployeePensionCLIAPP.loadEmployees();

        // 1. Calculate next quarter's date range
        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        int currentQuarter = (currentMonth - 1) / 3 + 1;

        int nextQuarterStartMonth = ((currentQuarter % 4) * 3) + 1;
        int year = (nextQuarterStartMonth == 1) ? today.getYear() + 1 : today.getYear();

        LocalDate nextQuarterStart = LocalDate.of(year, nextQuarterStartMonth, 1);
        LocalDate nextQuarterEnd = nextQuarterStart.plusMonths(3).minusDays(1);

        // 2. Filter eligible employees
        List<Employee> upcomingEnrollees = employees.stream()
                .filter(emp -> emp.getPensionPlan() == null)
                .filter(emp -> {
                    LocalDate employmentDate = emp.getEmploymentDate();
                    long yearsWorked = java.time.temporal.ChronoUnit.YEARS.between(employmentDate, nextQuarterEnd);
                    return yearsWorked >= 3;
                })
                .sorted(Comparator.comparing(Employee::getEmploymentDate).reversed())
                .toList();

        // 3. Print as JSON
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            String jsonOutput = mapper.writeValueAsString(upcomingEnrollees);
            System.out.println("========== Quarterly Upcoming Enrollees ==========");
            System.out.println(jsonOutput);
        } catch (JsonProcessingException e) {
            System.err.println("Error generating enrollees JSON: " + e.getMessage());
        }
    }

}
