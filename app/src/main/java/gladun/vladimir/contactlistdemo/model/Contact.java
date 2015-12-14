package gladun.vladimir.contactlistdemo.model;

/**
 *
 * @author vvgladoun@gmail.com
 */
public class Contact {

    private int id;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String website;

    private Company company;
    private Address address;

    public Contact(int id, String name, String username, String email,
                   String phone, String website, Address address, Company company) {

        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.website = website;
        this.address = address;
        this.company = company;
    }

    public Address getAddress() {
        return address;
    }

    public Company getCompany() {
        return company;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public String getUsername() {
        return username;
    }

    public String getWebsite() {
        return website;
    }
}
