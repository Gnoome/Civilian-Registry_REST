package gr.gnoome.Domain;

public class Person  {
    String Id=null;
    String Name=null;
    String Surname=null;
    String Birthdate=null;
    String Gender=null;
    String Address=null;
    String Tax=null;

    
    public String getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public String getSurname() {
        return Surname;
    }

    public String getBirthdate() {
        return Birthdate;
    }

    public String getGender() {
        return Gender;
    }

    public String getAddress() {
        return Address;
    }

    public String getTax() {
        return Tax;
    }

    public void setid(String id) {
        this.Id = id;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public void setSurname(String surname) {
        this.Surname = surname;
    }

    public void setBirthdate(String birthdate) {
        this.Birthdate = birthdate;
    }

    public void setGender(String gender) {
        this.Gender = gender;
    }

    public void setAddress(String address) {
        this.Address = address;
    }

    public void setTax(String tax) {
        this.Tax = tax;
    }

    public String toString() {
        return "ID: " + Id + ", Name: " + Name + ", Surname: " + Surname + ", Birthdate: " + Birthdate + ", Gender: " + Gender + (Address != null ? ", Address: " + Address : "") + (Tax != null ? ", Tax: " + Tax : "");
    }
}
