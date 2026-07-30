package model;

/**
 * Student
 */
public class  Student {
    private int StudentID;
    private String FullName;
    private String Gender;
    private String Phone;
    private String Email;
    private String Address;

    // Constructor rỗng
    public Student() {
    }

    // Constructor đầy đủ
    public Student(int StudentID, String FullName, String Gender,
                   String Phone, String Email, String Address) {
        this.StudentID = StudentID;
        this.FullName = FullName;
        this.Gender = Gender;
        this.Phone = Phone;
        this.Email = Email;
        this.Address = Address;
    }

    public int getStudentID() {
        return StudentID;
    }

    public void setStudentID(int StudentID) {
        this.StudentID = StudentID;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String FullName) {
        this.FullName = FullName;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String Gender) {
        this.Gender = Gender;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    @Override
    public String toString() {
        return StudentID + " - " + FullName;
    }
}


