package de.techdev.trackr.domain.employee.addressbook;

import de.techdev.trackr.domain.employee.Employee;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.echocat.jomon.testing.BaseMatchers.isNotEmpty;
import static org.junit.Assert.assertThat;

public class AddressBookControllerTest {

    private AddressBookController addressBookController;

    @Before
    public void setUp() throws Exception {
        addressBookController = new AddressBookController();
    }

    @Test
    public void transformEmployees() throws Exception {
        Employee employee = new Employee();
        List<Employee> listOfEmployees = singletonList(employee);
        List<EmployeeForAddressBookDTO> employeeForAddressBookDTOs = addressBookController.transformToReducedEmployees(listOfEmployees);
        assertThat(employeeForAddressBookDTOs, isNotEmpty());
    }
}