package gladun.vladimir.contactlistdemo.model;

/**
 *
 * @author vvgladoun@gmail.com
 */
public class Address {
    private String street;
    private String suite;
    private String city;
    private String zipcode;
    private Geo geo;

    public Address(String city, Geo geo, String street, String suite, String zipcode) {
        this.city = city;
        this.geo = geo;
        this.street = street;
        this.suite = suite;
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public Geo getGeo() {
        return geo;
    }

    public String getStreet() {
        return street;
    }

    public String getSuite() {
        return suite;
    }

    public String getZipcode() {
        return zipcode;
    }
}
